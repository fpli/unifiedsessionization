package com.ebay.epic.soj.flink.connector.kafka.config;

import com.ebay.epic.common.enums.DataCenter;
import com.ebay.epic.common.enums.EventType;
import lombok.Getter;
import lombok.Setter;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.RoundRobinAssignor;

import java.util.List;
import java.util.Properties;

import static com.ebay.epic.utils.Property.*;

@Getter
@Setter
public class KafkaConsumerConfig extends KafkaCommonConfig {
    private List<String> topics;


    private KafkaConsumerConfig(DataCenter dc, EventType eventType, boolean isDerived) {
        super(dc, eventType, isDerived);
        this.topics = this.getConfigManager().getTopics(KAFKA_CONSUMER_TOPIC_BASE);
    }

    public static KafkaConsumerConfig build(DataCenter dataCenter, EventType eventType) {
        return build(dataCenter, eventType, true);
    }

    public static KafkaConsumerConfig build(DataCenter dataCenter, EventType eventType, boolean isDerived) {
        KafkaConsumerConfig config = new KafkaConsumerConfig(dataCenter, eventType, isDerived);
        return config;
    }

    @Override
    public void buildProperties(Properties properties) {
        super.buildProperties(properties);
        enrichConfig(properties);
    }

    private void enrichConfig(Properties properties) {
        properties.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG,
                this.getConfigManager().getIntValueNODC(MAX_POLL_RECORDS_BASE));
        properties.put(ConsumerConfig.RECEIVE_BUFFER_CONFIG,
                this.getConfigManager().getIntValueNODC(RECEIVE_BUFFER_BASE));
        properties.put(ConsumerConfig.FETCH_MAX_BYTES_CONFIG,
                this.getConfigManager().getIntValueNODC(FETCH_MAX_BYTES_BASE));
        properties.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG,
                this.getConfigManager().getIntValueNODC(FETCH_MAX_WAIT_MS_BASE));
        properties.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG,
                this.getConfigManager().getIntValueNODC(MAX_PARTITIONS_FETCH_BYTES_BASE));
        properties.put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG,
                RoundRobinAssignor.class.getName());
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
                this.getConfigManager().getStrValueNODC(AUTO_RESET_OFFSET_BASE));
        // for auto-detection on the new added partitions
        properties.put("flink.partition-discovery.interval-millis",
                this.getConfigManager().getIntValueNODC(PARTITION_DISCOVERY_INTERVAL_MS_BASE));
    }

    @Override
    public String getBrokersForDC(DataCenter dc) {
        return this.getConfigManager().getBrokers(KAFKA_CONSUMER_BOOTSTRAP_SERVERS_BASE);
    }

    @Override
    public String getGId() {
        return this.getConfigManager().getStrDirect(KAFKA_CONSUMER_GROUP_ID);
    }
}
