package com.ebay.epic.soj.flink.connector.kafka.factory;

import com.ebay.epic.soj.flink.assigner.TrackingEventTimestampAssigner;
import com.ebay.epic.soj.flink.connector.kafka.config.FlinkKafkaSourceConfigWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.reader.deserializer.KafkaRecordDeserializationSchema;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.kafka.KafkaDeserializationSchema;

import java.time.Duration;

@Slf4j
public class FlinkKafkaConsumerFactory {

    private final FlinkKafkaSourceConfigWrapper configWrapper;

    public FlinkKafkaConsumerFactory(FlinkKafkaSourceConfigWrapper configWrapper) {
        this.configWrapper = configWrapper;
    }

    @Deprecated
    public <T> FlinkKafkaConsumer<T> get(KafkaDeserializationSchema<T> deserializer) {
        FlinkKafkaConsumer<T> flinkKafkaConsumer = new FlinkKafkaConsumer<>(
                configWrapper.getKafkaConsumerConfig().getTopics(),
                deserializer,
                configWrapper.getKafkaConsumerConfig().getProperties());
        if (configWrapper.getOutOfOrderlessInMin() > 0) {
            log.error("if init timestampand watermarks:{}", configWrapper.getOutOfOrderlessInMin());
            flinkKafkaConsumer.assignTimestampsAndWatermarks(
                    WatermarkStrategy
                            .forBoundedOutOfOrderness(Duration.ofMinutes(configWrapper.getOutOfOrderlessInMin()))
                            .withTimestampAssigner(new TrackingEventTimestampAssigner())
                            .withIdleness(Duration.ofMinutes(configWrapper.getIdleSourceTimeout())));
        } else {
            log.error("else init timestamp and watermarks:{}", configWrapper.getOutOfOrderlessInMin());
        }
        String fromTimestamp = configWrapper.getFromTimestamp();
        if (fromTimestamp.equalsIgnoreCase("earliest")) {
            flinkKafkaConsumer.setStartFromEarliest();
        } else if (Long.parseLong(fromTimestamp) == 0) {
            flinkKafkaConsumer.setStartFromLatest();
        } else if (Long.parseLong(fromTimestamp) > 0) {
            flinkKafkaConsumer.setStartFromTimestamp(Long.parseLong(fromTimestamp));
        } else {
            throw new IllegalArgumentException("Cannot parse fromTimestamp value");
        }

        return flinkKafkaConsumer;
    }

  /**
   * flink 1.17 api for kafka
   * @param deserializer
   */
  public <T> KafkaSource<T> getKafkaSource(KafkaDeserializationSchema<T> deserializer) {
    return KafkaSource.<T>builder()
      .setTopics(configWrapper.getKafkaConsumerConfig().getTopics())
      .setProperties(configWrapper.getKafkaConsumerConfig().getProperties())
      .setStartingOffsets(configWrapper.getSourceKafkaStartingOffsets())
      .setDeserializer(KafkaRecordDeserializationSchema.of(deserializer)).build();
  }
  public <T> WatermarkStrategy<T> getWatermarkStrategy() {
    return WatermarkStrategy
      .forBoundedOutOfOrderness(Duration.ofMinutes(configWrapper.getOutOfOrderlessInMin()))
      .withTimestampAssigner(new TrackingEventTimestampAssigner())
      .withIdleness(Duration.ofMinutes(configWrapper.getIdleSourceTimeout()));

  }
}
