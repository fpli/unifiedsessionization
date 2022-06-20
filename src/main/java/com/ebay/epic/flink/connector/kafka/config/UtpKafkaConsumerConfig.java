package com.ebay.epic.flink.connector.kafka.config;

import com.ebay.epic.common.enums.DataCenter;
import com.ebay.epic.utils.FlinkEnvUtils;
import com.ebay.epic.utils.Property;
import com.google.common.base.Preconditions;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.RoundRobinAssignor;

import java.util.List;
import java.util.Properties;

import static com.ebay.epic.utils.FlinkEnvUtils.*;
import static com.ebay.epic.utils.Property.*;

@Data
public class UtpKafkaConsumerConfig extends KafkaCommonConfig {

    private List<String> topics;

    private UtpKafkaConsumerConfig(DataCenter dc) {
        super(dc);
    }

    public static UtpKafkaConsumerConfig ofDC(DataCenter dataCenter) {
        UtpKafkaConsumerConfig config = new UtpKafkaConsumerConfig(dataCenter);
        final List<String> topics = FlinkEnvUtils.getList(KAFKA_CONSUMER_TOPIC_UTP);
        Preconditions.checkState(CollectionUtils.isNotEmpty(topics));
        config.setTopics(topics);
        return config;
    }

    public static UtpKafkaConsumerConfig ofDC(String dataCenter) {
        DataCenter dc = DataCenter.of(dataCenter);
        return ofDC(dc);
    }

    @Override
    public void buildProperties(Properties properties) {
        super.buildProperties(properties);
        properties.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG,
                getInteger(Property.MAX_POLL_RECORDS_UTP));
        properties.put(ConsumerConfig.RECEIVE_BUFFER_CONFIG,
                getInteger(Property.RECEIVE_BUFFER_UTP));
        properties.put(ConsumerConfig.FETCH_MAX_BYTES_CONFIG,
                getInteger(Property.FETCH_MAX_BYTES_UTP));
        properties.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG,
                getInteger(Property.FETCH_MAX_WAIT_MS_UTP));
        properties.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG,
                getInteger(Property.MAX_PARTITIONS_FETCH_BYTES_UTP));
        properties.put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG,
                RoundRobinAssignor.class.getName());
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
                getString(Property.AUTO_RESET_OFFSET_UTP));
        // for new added partitions
        properties.put("flink.partition-discovery.interval-millis",
                getInteger(Property.PARTITION_DISCOVERY_INTERVAL_MS_UTP));
    }

    @Override
    public String getBrokersForDC(DataCenter dc) {
        String propKey = KAFKA_CONSUMER_BOOTSTRAP_SERVERS_UTP + "." + dc.getValue().toLowerCase();
        return getListString(propKey);
    }
    @Override
    public String getGId() {
        return getString(KAFKA_CONSUMER_GROUP_ID_UBI);
    }

}
