package com.ebay.epic.flink.window;

import com.ebay.epic.common.DateTimeUtils;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.triggers.Trigger;
import org.apache.flink.streaming.api.windowing.triggers.TriggerResult;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;

/**
 * This trigger is used to emit open sessions in the midnight. Our batch pipeline will correlate
 * events to these open sessions.
 */
public class MidnightOpenSessionTrigger extends Trigger<Object, TimeWindow> {

  private static final long serialVersionUID = 1L;

  private long offset;

  public static MidnightOpenSessionTrigger of(Time offset) {
    return new MidnightOpenSessionTrigger(offset);
  }

  protected MidnightOpenSessionTrigger(Time offset) {
    this(offset.toMilliseconds());
  }

  protected MidnightOpenSessionTrigger(long offset) {
    this.offset = offset;
  }

  @Override
  public TriggerResult onElement(Object element, long timestamp, TimeWindow window,
      TriggerContext ctx) throws Exception {
    // We should not register open session trigger at max timestamp of window.
    for (long midnight : DateTimeUtils
        .midnightsBetween(window.getStart(), window.maxTimestamp(), offset)) {
      if (midnight > ctx.getCurrentWatermark()) {
        ctx.registerEventTimeTimer(midnight);
      }
    }
    return TriggerResult.CONTINUE;
  }

  @Override
  public TriggerResult onProcessingTime(long time, TimeWindow window, TriggerContext ctx)
      throws Exception {
    return TriggerResult.CONTINUE;
  }

  @Override
  public TriggerResult onEventTime(long time, TimeWindow window, TriggerContext ctx)
      throws Exception {
    return DateTimeUtils.isMidnight(time, offset) ?
        TriggerResult.FIRE :
        TriggerResult.CONTINUE;
  }

  @Override
  public void clear(TimeWindow window, TriggerContext ctx) throws Exception {
    for (long midnight : DateTimeUtils
        .midnightsBetween(window.getStart(), window.maxTimestamp(), offset)) {
      ctx.deleteEventTimeTimer(midnight);
    }
  }

  @Override
  public boolean canMerge() {
    return true;
  }

  @Override
  public void onMerge(TimeWindow window, OnMergeContext ctx) {
    for (long midnight : DateTimeUtils
        .midnightsBetween(window.getStart(), window.maxTimestamp(), offset)) {
      if (midnight > ctx.getCurrentWatermark()) {
        ctx.registerEventTimeTimer(midnight);
      }
    }
  }

  @Override
  public String toString() {
    return "MidnightOpenSessionTrigger()";
  }
}
