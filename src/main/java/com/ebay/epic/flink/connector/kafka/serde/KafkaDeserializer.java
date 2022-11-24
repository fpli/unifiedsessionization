package com.ebay.epic.flink.connector.kafka.serde;

public interface KafkaDeserializer<T> {

    default String decodeKey(byte[] data) {
        return null;
    }

    T decodeValue(byte[] data) throws Exception;
}
