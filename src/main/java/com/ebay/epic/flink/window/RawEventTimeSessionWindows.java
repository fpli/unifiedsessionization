package com.ebay.epic.flink.window;

import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.typeutils.TypeSerializer;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.MergingWindowAssigner;
import org.apache.flink.streaming.api.windowing.assigners.WindowAssigner;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.triggers.EventTimeTrigger;
import org.apache.flink.streaming.api.windowing.triggers.Trigger;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;

import java.util.*;

public class RawEventTimeSessionWindows extends MergingWindowAssigner<Object, TimeWindow> {

  private static final long serialVersionUID = 1L;

  protected long sessionTimeout;
  protected long sessionMaxDuration = 0;

  protected RawEventTimeSessionWindows(long sessionTimeout) {
    if (sessionTimeout <= 0) {
      throw new IllegalArgumentException(
          "Session timeout must be larger than 0.");
    }

    this.sessionTimeout = sessionTimeout;
  }

  protected RawEventTimeSessionWindows(long sessionTimeout, long sessionMaxDuration) {
    this(sessionTimeout);

    if (sessionMaxDuration <= 0) {
      throw new IllegalArgumentException(
          "Session max duration must be larger than 0.");
    }
    this.sessionMaxDuration = sessionMaxDuration;
  }

  @Override
  public Collection<TimeWindow> assignWindows(Object element, long timestamp,
      WindowAssignerContext context) {
    return Collections.singletonList(new TimeWindow(timestamp, timestamp + sessionTimeout));
  }

  @Override
  public Trigger<Object, TimeWindow> getDefaultTrigger(StreamExecutionEnvironment env) {
    return EventTimeTrigger.create();
  }

  @Override
  public String toString() {
    return "SojEventTimeSessionWindows(sessionTimeout=" + sessionTimeout + ", sessionMaxDuration="
        + sessionMaxDuration + ")";
  }

  /**
   * Creates a new {@code SessionWindows} {@link WindowAssigner} that assigns elements to sessions
   * based on the element timestamp.
   *
   * @param size The session timeout, i.e. the time gap between sessions
   * @return The policy.
   */
  public static RawEventTimeSessionWindows withGap(
      Time size) {
    return new RawEventTimeSessionWindows(size.toMilliseconds());
  }

  public static RawEventTimeSessionWindows withGapAndMaxDuration(
      Time size, Time maxDuration) {
    return new RawEventTimeSessionWindows(size.toMilliseconds(), maxDuration.toMilliseconds());
  }

  @Override
  public TypeSerializer<TimeWindow> getWindowSerializer(ExecutionConfig executionConfig) {
    return new TimeWindow.Serializer();
  }

  @Override
  public boolean isEventTime() {
    return true;
  }

  /**
   * Merge overlapping {@link TimeWindow}s.
   */
  @Override
  public void mergeWindows(Collection<TimeWindow> windows,
      MergeCallback<TimeWindow> c) {
    mergeWindowsWithMaxDuration(windows, c);
  }

  private void mergeWindowsWithMaxDuration(
      Collection<TimeWindow> windows, MergeCallback<TimeWindow> c) {
    // sort the windows by the start time and then merge overlapping windows

    List<TimeWindow> sortedWindows = new ArrayList<>(windows);

    Collections.sort(sortedWindows, new Comparator<TimeWindow>() {
      @Override
      public int compare(TimeWindow o1, TimeWindow o2) {
        return Long.compare(o1.getStart(), o2.getStart());
      }
    });

    List<Tuple2<TimeWindow, Set<TimeWindow>>> merged = new ArrayList<>();
    Tuple2<TimeWindow, Set<TimeWindow>> currentMerge = null;

    for (TimeWindow candidate : sortedWindows) {
      if (currentMerge == null) {
        currentMerge = new Tuple2<>();
        currentMerge.f0 = candidate;
        currentMerge.f1 = new HashSet<>();
        currentMerge.f1.add(candidate);
      } else if (canMerge(currentMerge, candidate)) {
        currentMerge.f0 = currentMerge.f0.cover(candidate);
        currentMerge.f1.add(candidate);
      } else {
        merged.add(currentMerge);
        currentMerge = new Tuple2<>();
        currentMerge.f0 = candidate;
        currentMerge.f1 = new HashSet<>();
        currentMerge.f1.add(candidate);
      }
    }

    if (currentMerge != null) {
      merged.add(currentMerge);
    }

    for (Tuple2<TimeWindow, Set<TimeWindow>> m : merged) {
      if (m.f1.size() > 1) {
        c.merge(m.f1, m.f0);
      }
    }
  }

  private boolean canMerge(Tuple2<TimeWindow, Set<TimeWindow>> currentMerge,
      TimeWindow candidate) {
    if (currentMerge.f0.intersects(candidate)) {
      if (sessionMaxDuration > 0) {
        TimeWindow merged = currentMerge.f0.cover(candidate);
        if (merged.getEnd() - merged.getStart() < sessionMaxDuration + sessionTimeout) {
          return true;
        }
      } else {
        return true;
      }
    }
    return false;
  }
}
