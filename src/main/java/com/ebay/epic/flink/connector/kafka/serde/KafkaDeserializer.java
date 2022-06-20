package com.ebay.epic.flink.connector.kafka.serde;

public interface KafkaDeserializer<T> {

  String decodeKey(byte[] data);

  T decodeValue(byte[] data);
}
