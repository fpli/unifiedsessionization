package com.ebay.epic.flink.connector.kafka.serde;

import java.util.List;

public interface KafkaSerializer<T> {

  byte[] encodeKey(T data, List<String> keyFields);

  byte[] encodeValue(T data);

}
