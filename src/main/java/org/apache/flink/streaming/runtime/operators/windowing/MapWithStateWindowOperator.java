package org.apache.flink.streaming.runtime.operators.windowing;

import org.apache.flink.api.common.state.AppendingState;
import org.apache.flink.api.common.state.StateDescriptor;
import org.apache.flink.api.common.typeutils.TypeSerializer;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.runtime.state.internal.InternalAppendingState;
import org.apache.flink.runtime.state.internal.InternalMergingState;
import org.apache.flink.streaming.api.operators.InternalTimerService;
import org.apache.flink.streaming.api.windowing.assigners.MergingWindowAssigner;
import org.apache.flink.streaming.api.windowing.assigners.WindowAssigner;
import org.apache.flink.streaming.api.windowing.triggers.Trigger;
import org.apache.flink.streaming.api.windowing.triggers.TriggerResult;
import org.apache.flink.streaming.api.windowing.windows.Window;
import org.apache.flink.streaming.runtime.operators.windowing.functions.InternalWindowFunction;
import org.apache.flink.streaming.runtime.streamrecord.StreamRecord;
import org.apache.flink.util.OutputTag;

import java.util.Collection;

public class MapWithStateWindowOperator<K, IN, ACC, OUT, W extends Window, MAPPED>
    extends WindowOperator<K, IN, ACC, OUT, W> {

  protected final MapWithStateFunction<IN, ACC, MAPPED> mapWithStateFunction;

  protected OutputTag<MAPPED> mappedRecordsOutputTag;

  /**
   * The state in which the window contents is stored. Each window is a namespace
   */
  private transient InternalAppendingState<K, W, IN, ACC, ACC> windowState;

  /**
   * The {@link #windowState}, typed to merging state for merging windows. Null if the window state
   * is not mergeable.
   */
  private transient InternalMergingState<K, W, IN, ACC, ACC> windowMergingState;

  /**
   * Creates a new {@code MapWithStateWindowOperator} based on the given policies and user
   * functions.
   */
  public MapWithStateWindowOperator(
      WindowAssigner<? super IN, W> windowAssigner,
      TypeSerializer<W> windowSerializer,
      KeySelector<IN, K> keySelector,
      TypeSerializer<K> keySerializer,
      StateDescriptor<? extends AppendingState<IN, ACC>, ?> windowStateDescriptor,
      InternalWindowFunction<ACC, OUT, K, W> windowFunction,
      Trigger<? super IN, ? super W> trigger,
      long allowedLateness,
      OutputTag<IN> lateDataOutputTag,
      MapWithStateFunction<IN, ACC, MAPPED> mapWithStateFunction,
      OutputTag<MAPPED> mappedRecordsOutputTag) {

    super(windowAssigner,
        windowSerializer,
        keySelector,
        keySerializer,
        windowStateDescriptor,
        windowFunction,
        trigger,
        allowedLateness,
        lateDataOutputTag);

    this.mapWithStateFunction = mapWithStateFunction;
    this.mappedRecordsOutputTag = mappedRecordsOutputTag;
  }

  public static <IN, ACC, MAPPED> MapWithStateWindowOperator from(
      WindowOperator windowOperator,
      MapWithStateFunction<IN, ACC, MAPPED> mapWithStateFunction,
      OutputTag<IN> mappedRecordsOutputTag
  ) {
    return new MapWithStateWindowOperator(
        windowOperator.getWindowAssigner(),
        (TypeSerializer) WindowOperatorHelper.getField(windowOperator, "windowSerializer"),
        windowOperator.getKeySelector(),
        (TypeSerializer) WindowOperatorHelper.getField(windowOperator, "keySerializer"),
        windowOperator.getStateDescriptor(),
        (InternalWindowFunction) windowOperator.getUserFunction(),
        windowOperator.getTrigger(),
        (long) WindowOperatorHelper.getField(windowOperator, "allowedLateness"),
        (OutputTag) WindowOperatorHelper.getField(windowOperator, "lateDataOutputTag"),
        mapWithStateFunction,
        mappedRecordsOutputTag
    );
  }

  public static <IN> MapWithStateWindowOperator from(WindowOperator windowOperator,
      OutputTag<IN> mappedRecordsOutputTag) {
    return from(windowOperator, new IdentityMapper<>(), mappedRecordsOutputTag);
  }

  @Override
  public void open() throws Exception {
    super.open();
    windowState = (InternalAppendingState<K, W, IN, ACC, ACC>) WindowOperatorHelper.getField(
        this,
        WindowOperator.class,
        "windowState");
    windowMergingState = (InternalMergingState<K, W, IN, ACC, ACC>) WindowOperatorHelper.getField(
        this,
        WindowOperator.class,
        "windowMergingState"
    );
  }

  @Override
  public void processElement(StreamRecord<IN> element) throws Exception {
    final Collection<W> elementWindows = windowAssigner.assignWindows(
        element.getValue(), element.getTimestamp(), windowAssignerContext);

    //if element is handled by none of assigned elementWindows
    boolean isSkippedElement = true;

    final K key = this.<K>getKeyedStateBackend().getCurrentKey();

    if (windowAssigner instanceof MergingWindowAssigner) {
      MergingWindowSet<W> mergingWindows = getMergingWindowSet();

      for (W window : elementWindows) {

        // adding the new window might result in a merge, in that case the actualWindow
        // is the merged window and we work with that. If we don't merge then
        // actualWindow == window
        W actualWindow = mergingWindows.addWindow(window, new MergingWindowSet.MergeFunction<W>() {
          @Override
          public void merge(W mergeResult,
              Collection<W> mergedWindows, W stateWindowResult,
              Collection<W> mergedStateWindows) throws Exception {

            if ((_windowAssigner().isEventTime()
                && mergeResult.maxTimestamp() + _allowedLateness() <= _internalTimerService()
                .currentWatermark())) {
              throw new UnsupportedOperationException("The end timestamp of an " +
                  "event-time window cannot become earlier than the current watermark " +
                  "by merging. Current watermark: " + _internalTimerService().currentWatermark() +
                  " window: " + mergeResult);
            } else if (!_windowAssigner().isEventTime()
                && mergeResult.maxTimestamp() <= _internalTimerService().currentProcessingTime()) {
              throw new UnsupportedOperationException("The end timestamp of a " +
                  "processing-time window cannot become earlier than the current processing time " +
                  "by merging. Current processing time: " + _internalTimerService()
                  .currentProcessingTime() +
                  " window: " + mergeResult);
            }

            WindowOperatorHelper.setField(_triggerContext(), "key", key);
            WindowOperatorHelper.setField(_triggerContext(), "window", mergeResult);

            _triggerContext().onMerge(mergedWindows);

            for (W m : mergedWindows) {
              WindowOperatorHelper.setField(_triggerContext(), "window", m);
              _triggerContext().clear();
              _deleteCleanupTimer(m);
            }

            // merge the merged state windows into the newly resulting state window
            windowMergingState.mergeNamespaces(stateWindowResult, mergedStateWindows);
          }
        });

        // drop if the window is already late
        if (isWindowLate(actualWindow)) {
          mergingWindows.retireWindow(actualWindow);
          continue;
        }
        isSkippedElement = false;

        W stateWindow = mergingWindows.getStateWindow(actualWindow);
        if (stateWindow == null) {
          throw new IllegalStateException("Window " + window + " is not in in-flight window set.");
        }

        windowState.setCurrentNamespace(stateWindow);
        windowState.add(element.getValue());

        WindowOperatorHelper.setField(triggerContext, "key", key);
        WindowOperatorHelper.setField(triggerContext, "window", actualWindow);

        TriggerResult triggerResult = triggerContext.onElement(element);

        if (triggerResult.isFire()) {
          ACC contents = windowState.get();
          if (contents == null) {
            continue;
          }
          emitWindowContents(actualWindow, contents);
        }

        if (triggerResult.isPurge()) {
          windowState.clear();
        }
        registerCleanupTimer(actualWindow);
      }

      // need to make sure to update the merging state in state
      mergingWindows.persist();
    } else {
      for (W window : elementWindows) {

        // drop if the window is already late
        if (isWindowLate(window)) {
          continue;
        }
        isSkippedElement = false;

        windowState.setCurrentNamespace(window);
        windowState.add(element.getValue());

        WindowOperatorHelper.setField(triggerContext, "key", key);
        WindowOperatorHelper.setField(triggerContext, "window", window);

        TriggerResult triggerResult = triggerContext.onElement(element);

        if (triggerResult.isFire()) {
          ACC contents = windowState.get();
          if (contents == null) {
            continue;
          }
          emitWindowContents(window, contents);
        }

        if (triggerResult.isPurge()) {
          windowState.clear();
        }
        registerCleanupTimer(window);
      }
    }

    // side output input event if
    // element not handled by any window
    // late arriving tag has been set
    // windowAssigner is event time and current timestamp + allowed lateness no less than element timestamp
    if (isSkippedElement && isElementLate(element)) {
      if (lateDataOutputTag != null) {
        sideOutput(element);
      } else {
        this.numLateRecordsDropped.inc();
      }
    } else {
      // side output StreamRecord(s) mapped with window state
      output.collect(mappedRecordsOutputTag,
          element.replace(mapWithStateFunction.map(element.getValue(), windowState.get())));
    }
  }

  /**
   * Emits the contents of the given window using the {@link InternalWindowFunction}.
   */
  @SuppressWarnings("unchecked")
  private void emitWindowContents(W window, ACC contents) throws Exception {
    timestampedCollector.setAbsoluteTimestamp(window.maxTimestamp());
    WindowOperatorHelper.setField(processContext, "window", window);
    userFunction
        .process((K) WindowOperatorHelper.getField(triggerContext, "key"), window, processContext,
            contents, timestampedCollector);
  }

  WindowAssigner _windowAssigner() {
    return this.windowAssigner;
  }

  long _allowedLateness() {
    return this.allowedLateness;
  }

  InternalTimerService _internalTimerService() {
    return this.internalTimerService;
  }

  Context _triggerContext() {
    return this.triggerContext;
  }

  void _deleteCleanupTimer(W m) {
    this.deleteCleanupTimer(m);
  }
}
