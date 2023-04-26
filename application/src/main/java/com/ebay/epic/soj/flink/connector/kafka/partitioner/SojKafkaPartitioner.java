package com.ebay.epic.soj.flink.connector.kafka.partitioner;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.Charsets;
import org.apache.flink.streaming.connectors.kafka.partitioner.FlinkKafkaPartitioner;
import org.apache.flink.util.Preconditions;

@Slf4j
public class SojKafkaPartitioner<T> extends FlinkKafkaPartitioner<T> {

    @Override
    public int partition(T record, byte[] key, byte[] value, String targetTopic,
                         int[] partitions) {
        Preconditions.checkNotNull(
                partitions, "partition of the target topic is null.");
        Preconditions.checkArgument(partitions.length > 0,
                "partition of the target topic is empty.");
        String keyStr = new String(key, Charsets.UTF_8);
        return Math.abs(keyStr.hashCode() % partitions.length);
    }

    @Override
    public boolean equals(Object o) {
        return this == o || o instanceof SojKafkaPartitioner;
    }

    @Override
    public int hashCode() {
        return SojKafkaPartitioner.class.hashCode();
    }
}
