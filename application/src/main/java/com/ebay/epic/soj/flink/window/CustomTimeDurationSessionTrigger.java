package com.ebay.epic.soj.flink.window;

import org.apache.flink.annotation.VisibleForTesting;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.common.state.ReducingState;
import org.apache.flink.api.common.state.ReducingStateDescriptor;
import org.apache.flink.api.common.typeutils.base.LongSerializer;
import org.apache.flink.streaming.api.windowing.triggers.Trigger;
import org.apache.flink.streaming.api.windowing.triggers.TriggerResult;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;

/***
 * xiaoding
 */
public class CustomTimeDurationSessionTrigger extends Trigger<Object, TimeWindow> {

    private static final long serialVersionUID = 1L;
    private final long timeDuration;
    private final ReducingStateDescriptor<Long> stateDesc =
            new ReducingStateDescriptor<>("fire-time", new Min(), LongSerializer.INSTANCE);

    private CustomTimeDurationSessionTrigger(long timeDuration) {
        this.timeDuration = timeDuration;
    }

    public static CustomTimeDurationSessionTrigger of(long timeDuration) {
        return new CustomTimeDurationSessionTrigger(timeDuration);
    }

    @Override
    public TriggerResult onElement(Object element, long timestamp, TimeWindow window, TriggerContext ctx) throws Exception {
        ReducingState<Long> fireTimestamp = ctx.getPartitionedState(stateDesc);
        if (fireTimestamp.get() == null) {
            //            long start = timestamp - (timestamp % timeDuration);
            long start = timestamp;
            long nextFireTimestamp = start + timeDuration;
            ctx.registerEventTimeTimer(nextFireTimestamp);
            fireTimestamp.add(nextFireTimestamp);
        }
        return TriggerResult.CONTINUE;
    }

    @Override
    public TriggerResult onProcessingTime(long time, TimeWindow window, TriggerContext ctx) throws Exception {
        return TriggerResult.CONTINUE;
    }

    @Override
    public TriggerResult onEventTime(long time, TimeWindow window, TriggerContext ctx) throws Exception {
        ReducingState<Long> fireTimestampState = ctx.getPartitionedState(stateDesc);
        Long fireTimestamp = fireTimestampState.get();
        if (fireTimestamp != null && fireTimestamp == time) {
            // to avoid multiple triggers in same session
            //            fireTimestampState.clear();
            ctx.deleteEventTimeTimer(fireTimestamp);
            return TriggerResult.FIRE;
        }
        return TriggerResult.CONTINUE;
    }

    @Override
    public void clear(TimeWindow window, TriggerContext ctx) throws Exception {
        ReducingState<Long> fireTimestamp = ctx.getPartitionedState(stateDesc);
        Long timestamp = fireTimestamp.get();
        if (timestamp != null) {
            ctx.deleteEventTimeTimer(timestamp);
            fireTimestamp.clear();
        }
    }

    @Override
    public boolean canMerge() {
        return true;
    }

    @Override
    public void onMerge(TimeWindow window, OnMergeContext ctx) throws Exception {
        ctx.mergePartitionedState(stateDesc);
        Long nextFireTimestamp = ctx.getPartitionedState(stateDesc).get();
        // discard the out-of-date timers
        if (nextFireTimestamp != null && nextFireTimestamp > ctx.getCurrentWatermark()) {
            ctx.registerEventTimeTimer(nextFireTimestamp);
        }
    }

    @Override
    public String toString() {
        return "CustomTimeDurationSessionTrigger(" + timeDuration + ")";
    }

    @VisibleForTesting
    public long getTimeDuration() {
        return timeDuration;
    }

    private static class Min implements ReduceFunction<Long> {
        private static final long serialVersionUID = 1L;

        @Override
        public Long reduce(Long value1, Long value2) throws Exception {
            return Math.min(value1, value2);
        }
    }
}

