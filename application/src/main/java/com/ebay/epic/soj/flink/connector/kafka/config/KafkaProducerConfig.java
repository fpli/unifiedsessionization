package com.ebay.epic.soj.flink.connector.kafka.config;

import com.ebay.epic.soj.common.enums.DataCenter;
import com.ebay.epic.soj.common.enums.EventType;
import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.Setter;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;

import java.util.Properties;

import static com.ebay.epic.soj.common.utils.Property.*;

@Getter
@Setter
public class KafkaProducerConfig extends KafkaCommonConfig {

    private KafkaProducerConfig(DataCenter dc, EventType eventType, boolean isDerived) {
        super(dc, eventType);

    }

    public static KafkaProducerConfig build(DataCenter dataCenter, EventType eventType) {
        Preconditions.checkNotNull(dataCenter);
        Preconditions.checkNotNull(eventType);
        return build(dataCenter,eventType,true);
    }

    public static KafkaProducerConfig build(DataCenter dataCenter, EventType eventType,boolean isDerived) {
        Preconditions.checkNotNull(dataCenter);
        Preconditions.checkNotNull(eventType);
        KafkaProducerConfig config = new KafkaProducerConfig(dataCenter, eventType,isDerived);
        return config;
    }

    @Override
    public void buildProperties(Properties producerConfig) {
        super.buildProperties(producerConfig);
        producerConfig.put(ConsumerConfig.GROUP_ID_CONFIG, getGroupId());
        producerConfig.put(ProducerConfig.BATCH_SIZE_CONFIG,
                this.getConfigManager().getIntValueNODC(BATCH_SIZE));
        producerConfig.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG,
                this.getConfigManager().getIntValueNODC(REQUEST_TIMEOUT_MS));
        producerConfig.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG,
                this.getConfigManager().getIntValueNODC(DELIVERY_TIMEOUT_MS));
        producerConfig.put(ProducerConfig.RETRIES_CONFIG,
                this.getConfigManager().getIntValueNODC(REQUEST_RETRIES));
        producerConfig.put(ProducerConfig.LINGER_MS_CONFIG,
                this.getConfigManager().getIntValueNODC(LINGER_MS));
        producerConfig.put(ProducerConfig.BUFFER_MEMORY_CONFIG,
                this.getConfigManager().getIntValueNODC(BUFFER_MEMORY));
        producerConfig.put(ProducerConfig.ACKS_CONFIG,
                this.getConfigManager().getStrValueNODC(ACKS));
        producerConfig.put(ProducerConfig.COMPRESSION_TYPE_CONFIG,
                this.getConfigManager().getStrValueNODC(COMPRESSION_TYPE));
        producerConfig.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG,
                this.getConfigManager().getIntValueNODC(MAX_REQUEST_SIZE));
    }

    @Override
    public String getBrokersForDC(DataCenter dc) {
        return this.getConfigManager().getBrokersWithFN(KAFKA_PRODUCER_BOOTSTRAP_SERVERS_BASE);
    }

    @Override
    public String getGId() {
        return this.getConfigManager().getStrDirect(PRODUCER_ID);
    }
}
