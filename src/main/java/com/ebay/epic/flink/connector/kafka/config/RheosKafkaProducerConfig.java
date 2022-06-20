package com.ebay.epic.flink.connector.kafka.config;

import com.ebay.epic.utils.Property;
import io.ebay.rheos.kafka.client.StreamConnectorConfig;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@AllArgsConstructor
@Data
public class RheosKafkaProducerConfig implements Serializable {
  private String rheosServiceUrls;
  private String topic;
  private String schemaSubject;
  private String producerId;
  private Properties kafkaProps;

  public Map<String, Object> toConfigMap() {
    Map<String, Object> map = new HashMap<>((Map) kafkaProps);
    map.put(Property.PRODUCER_ID, producerId);
    map.put(StreamConnectorConfig.RHEOS_SERVICES_URLS, rheosServiceUrls);
    return map;
  }
}
