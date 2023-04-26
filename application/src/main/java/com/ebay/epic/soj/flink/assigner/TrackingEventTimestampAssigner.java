package com.ebay.epic.soj.flink.assigner;

import com.ebay.epic.soj.common.utils.TimestampFieldExtractor;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;

@Slf4j
public class TrackingEventTimestampAssigner<T> implements SerializableTimestampAssigner<T> {

    @Override
    public long extractTimestamp(T element, long recordTimestamp) {

        var field = 0L;
        try {
            field = TimestampFieldExtractor.getField(element);
        } catch (Exception e) {
            field = System.currentTimeMillis();
            log.warn("extract timestamp failed" + field);
        }

        return field;
    }
}
