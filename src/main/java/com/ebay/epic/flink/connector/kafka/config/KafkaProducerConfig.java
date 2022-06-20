package com.ebay.epic.flink.connector.kafka.config;

import com.ebay.epic.common.enums.DataCenter;
import com.ebay.epic.utils.Property;
import com.google.common.base.Preconditions;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;

import java.util.Properties;

import static com.ebay.epic.utils.FlinkEnvUtils.*;
import static com.ebay.epic.utils.Property.KAFKA_PRODUCER_BOOTSTRAP_SERVERS;
import static com.ebay.epic.utils.Property.PRODUCER_ID;

@Data
@NoArgsConstructor
public class KafkaProducerConfig extends KafkaCommonConfig {

    private KafkaProducerConfig(DataCenter dc) {
        super(dc);
    }

    public static KafkaProducerConfig ofDC(DataCenter dataCenter) {
        Preconditions.checkNotNull(dataCenter);
        KafkaProducerConfig config = new KafkaProducerConfig(dataCenter);
        return config;
    }

    public static KafkaProducerConfig ofDC(String dataCenter) {
        DataCenter dc = DataCenter.of(dataCenter);
        return ofDC(dc);
    }

    @Override
    public void buildProperties(Properties producerConfig) {
        super.buildProperties(producerConfig);
        producerConfig.put(ConsumerConfig.GROUP_ID_CONFIG, getGroupId());
        producerConfig.put(ProducerConfig.BATCH_SIZE_CONFIG,
                getInteger(Property.BATCH_SIZE));
        producerConfig.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG,
                getInteger(Property.REQUEST_TIMEOUT_MS));
        producerConfig.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG,
                getInteger(Property.DELIVERY_TIMEOUT_MS));
        producerConfig.put(ProducerConfig.RETRIES_CONFIG,
                getInteger(Property.REQUEST_RETRIES));
        producerConfig.put(ProducerConfig.LINGER_MS_CONFIG,
                getInteger(Property.LINGER_MS));
        producerConfig.put(ProducerConfig.BUFFER_MEMORY_CONFIG,
                getInteger(Property.BUFFER_MEMORY));
        producerConfig.put(ProducerConfig.ACKS_CONFIG,
                getString(Property.ACKS));
        producerConfig.put(ProducerConfig.COMPRESSION_TYPE_CONFIG,
                getString(Property.COMPRESSION_TYPE));
        producerConfig.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG,
                getInteger(Property.MAX_REQUEST_SIZE));
    }

    @Override
    public String getBrokersForDC(DataCenter dc) {
        String propKey = KAFKA_PRODUCER_BOOTSTRAP_SERVERS + "." + dc.getValue().toLowerCase();
        return getListString(propKey);
    }

    @Override
    public String getGId() {
        return getString(PRODUCER_ID);
    }
}
