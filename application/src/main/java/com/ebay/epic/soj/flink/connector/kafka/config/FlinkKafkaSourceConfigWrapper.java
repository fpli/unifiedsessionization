package com.ebay.epic.soj.flink.connector.kafka.config;

import com.ebay.epic.soj.flink.assigner.TrackingEventTimestampAssigner;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;

import java.time.Duration;

@Data
@AllArgsConstructor
public class FlinkKafkaSourceConfigWrapper {
  private KafkaConsumerConfig kafkaConsumerConfig;
  private int outOfOrderlessInMin;
  private int idleSourceTimeout;
  private String fromTimestamp;

  public OffsetsInitializer getSourceKafkaStartingOffsets() {
    // start-offset must be declared explicitly
    if (fromTimestamp.equalsIgnoreCase("committed-offset")) {
      return OffsetsInitializer.committedOffsets();
    } else if (fromTimestamp.equalsIgnoreCase("committed-offset-or-earliest")) {
      return OffsetsInitializer.committedOffsets(OffsetResetStrategy.EARLIEST);
    } else if (fromTimestamp.equalsIgnoreCase("committed-offset-or-latest")) {
      return OffsetsInitializer.committedOffsets(OffsetResetStrategy.LATEST);
    } else if (fromTimestamp.equalsIgnoreCase("0") || fromTimestamp.equalsIgnoreCase("latest")) {
      return OffsetsInitializer.latest();
    } else if (fromTimestamp.equalsIgnoreCase("earliest")) {
      return OffsetsInitializer.earliest();
    } else {
      // from timestamp
      return OffsetsInitializer.timestamp(Long.parseLong(fromTimestamp));
    }

  }

}
