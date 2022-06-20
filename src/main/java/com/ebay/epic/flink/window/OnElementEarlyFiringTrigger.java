package com.ebay.epic.flink.window;

import org.apache.flink.streaming.api.windowing.triggers.CountTrigger;
import org.apache.flink.streaming.api.windowing.triggers.EventTimeTrigger;
import org.apache.flink.streaming.api.windowing.triggers.Trigger;
import org.apache.flink.streaming.api.windowing.triggers.TriggerResult;
import org.apache.flink.streaming.api.windowing.windows.Window;

import java.util.ArrayList;
import java.util.List;

public class OnElementEarlyFiringTrigger<W extends Window> extends Trigger<Object, W> {

  private static final long serialVersionUID = 1L;

  private Trigger countTrigger = CountTrigger.of(1);
  private Trigger eventTimeTrigger = EventTimeTrigger.create();
  private List<Trigger> triggers = new ArrayList<>();

  private OnElementEarlyFiringTrigger() {
    triggers.add(countTrigger);
    triggers.add(eventTimeTrigger);
  }

  public static <W extends Window> OnElementEarlyFiringTrigger<W> create() {
    return new OnElementEarlyFiringTrigger<>();
  }

  @Override
  public TriggerResult onElement(Object element, long timestamp, W window, TriggerContext ctx)
      throws Exception {
    if (ctx.getCurrentWatermark() >= window.maxTimestamp()) {
      return TriggerResult.CONTINUE;
    }

    List<TriggerResult> results = new ArrayList<>();
    for (Trigger trigger : triggers) {
      results.add(trigger.onElement(element, timestamp, window, ctx));
    }
    if (results.contains(TriggerResult.FIRE)) {
      return TriggerResult.FIRE;
    } else {
      return TriggerResult.CONTINUE;
    }
  }

  @Override
  public TriggerResult onEventTime(long time, W window, TriggerContext ctx) throws Exception {
    List<TriggerResult> results = new ArrayList<>();
    for (Trigger trigger : triggers) {
      results.add(trigger.onEventTime(time, window, ctx));
    }
    if (results.contains(TriggerResult.FIRE)) {
      return TriggerResult.FIRE;
    } else {
      return TriggerResult.CONTINUE;
    }
  }

  @Override
  public TriggerResult onProcessingTime(long time, W window, TriggerContext ctx) throws Exception {
    List<TriggerResult> results = new ArrayList<>();
    for (Trigger trigger : triggers) {
      results.add(trigger.onProcessingTime(time, window, ctx));
    }
    if (results.contains(TriggerResult.FIRE)) {
      return TriggerResult.FIRE;
    } else {
      return TriggerResult.CONTINUE;
    }
  }

  @Override
  public void clear(W window, TriggerContext ctx) throws Exception {
    for (Trigger trigger : triggers) {
      trigger.clear(window, ctx);
    }
  }

  @Override
  public boolean canMerge() {
    return true;
  }

  @Override
  public void onMerge(W window, OnMergeContext ctx) throws Exception {
    for (Trigger trigger : triggers) {
      trigger.onMerge(window, ctx);
    }
  }

  @Override
  public String toString() {
    return "SojTrigger()";
  }
}
