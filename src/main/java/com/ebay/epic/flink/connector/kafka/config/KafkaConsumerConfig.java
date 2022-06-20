package com.ebay.epic.flink.connector.kafka.config;

import com.ebay.epic.common.enums.DataCenter;
import com.ebay.epic.common.enums.EventType;
import com.ebay.epic.utils.FlinkEnvUtils;
import com.ebay.epic.utils.Property;
import com.google.common.base.Preconditions;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.RoundRobinAssignor;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import static com.ebay.epic.utils.FlinkEnvUtils.*;
import static com.ebay.epic.utils.Property.*;
import static com.ebay.epic.utils.Property.PARTITION_DISCOVERY_INTERVAL_MS_AUTOTRACK;

@Data
public class KafkaConsumerConfig extends KafkaCommonConfig {
    private List<String> topics;
    private EventType eventType;
    private static ConcurrentHashMap brokerMap = new ConcurrentHashMap();
    private static ConcurrentHashMap groupidMap = new ConcurrentHashMap();
    private static ConcurrentHashMap topicMap = new ConcurrentHashMap();

    static {
        brokerMap.put(EventType.AUTOTRACK, KAFKA_CONSUMER_BOOTSTRAP_SERVERS_AUTOTRACK);
        brokerMap.put(EventType.UBI, KAFKA_CONSUMER_BOOTSTRAP_SERVERS_UBI);
        brokerMap.put(EventType.UTP, KAFKA_CONSUMER_BOOTSTRAP_SERVERS_UTP);
        groupidMap.put(EventType.AUTOTRACK, KAFKA_CONSUMER_GROUP_ID_AUTOTRACK);
        groupidMap.put(EventType.UBI, KAFKA_CONSUMER_GROUP_ID_UBI);
        groupidMap.put(EventType.UTP, KAFKA_CONSUMER_GROUP_ID_UTP);
        topicMap.put(EventType.AUTOTRACK, KAFKA_CONSUMER_TOPIC_AUTOTRACK);
        topicMap.put(EventType.UBI, KAFKA_CONSUMER_TOPIC_UBI);
        topicMap.put(EventType.UTP, KAFKA_CONSUMER_TOPIC_UTP);

    }

    private KafkaConsumerConfig(DataCenter dc, EventType eventType) {
        super(dc);
        this.eventType = eventType;
    }

    public static KafkaConsumerConfig build(DataCenter dataCenter, EventType eventType) {
        KafkaConsumerConfig config = new KafkaConsumerConfig(dataCenter, eventType);
        final List<String> topics = FlinkEnvUtils.getList(topicMap.get(eventType).toString());
        Preconditions.checkState(CollectionUtils.isNotEmpty(topics));
        config.setTopics(topics);
        return config;
    }

    @Override
    public void buildProperties(Properties properties) {
        super.buildProperties(properties);
        enrichConfig(properties);
    }

    private void enrichConfig(Properties properties) {
        switch (eventType) {
            case AUTOTRACK: {
                properties.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG,
                        getInteger(MAX_POLL_RECORDS_AUTOTRACK));
                properties.put(ConsumerConfig.RECEIVE_BUFFER_CONFIG,
                        getInteger(RECEIVE_BUFFER_AUTOTRACK));
                properties.put(ConsumerConfig.FETCH_MAX_BYTES_CONFIG,
                        getInteger(FETCH_MAX_BYTES_AUTOTRACK));
                properties.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG,
                        getInteger(FETCH_MAX_WAIT_MS_AUTOTRACK));
                properties.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG,
                        getInteger(MAX_PARTITIONS_FETCH_BYTES_AUTOTRACK));
                properties.put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG,
                        RoundRobinAssignor.class.getName());
                properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
                        getString(AUTO_RESET_OFFSET_AUTOTRACK));
                // for new added partitions
                properties.put("flink.partition-discovery.interval-millis",
                        getInteger(PARTITION_DISCOVERY_INTERVAL_MS_AUTOTRACK));
                break;
            }
            case UBI: {
                properties.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG,
                        getInteger(MAX_POLL_RECORDS_UBI));
                properties.put(ConsumerConfig.RECEIVE_BUFFER_CONFIG,
                        getInteger(Property.RECEIVE_BUFFER_UBI));
                properties.put(ConsumerConfig.FETCH_MAX_BYTES_CONFIG,
                        getInteger(Property.FETCH_MAX_BYTES_UBI));
                properties.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG,
                        getInteger(Property.FETCH_MAX_WAIT_MS_UBI));
                properties.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG,
                        getInteger(Property.MAX_PARTITIONS_FETCH_BYTES_UBI));
                properties.put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG,
                        RoundRobinAssignor.class.getName());
                properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
                        getString(Property.AUTO_RESET_OFFSET_UBI));
                // for new added partitions
                properties.put("flink.partition-discovery.interval-millis",
                        getInteger(Property.PARTITION_DISCOVERY_INTERVAL_MS_UBI));
                break;

            }
            case UTP: {
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
            default:
                break;
        }
    }

    @Override
    public String getBrokersForDC(DataCenter dc) {

        String propKey = brokerMap.get(this.eventType) + "." + dc.getValue().toLowerCase();
        return getListString(propKey);
    }

    @Override
    public String getGId() {

        return getString(groupidMap.get(this.eventType).toString());
    }
}
