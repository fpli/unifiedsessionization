package com.ebay.epic.flink.connector.kafka;

import com.ebay.epic.common.enums.DataCenter;
import com.ebay.epic.common.enums.EventType;
import com.ebay.epic.flink.connector.kafka.config.ConfigManager;
import com.ebay.epic.flink.connector.kafka.config.KafkaProducerConfig;
import com.ebay.epic.flink.connector.kafka.factory.FlinkKafkaProducerFactory;
import com.ebay.epic.utils.Property;
import com.google.common.base.Preconditions;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.connectors.kafka.KafkaDeserializationSchema;

import static com.ebay.epic.utils.FlinkEnvUtils.*;
import static com.ebay.epic.utils.FlinkEnvUtils.getStringArray;
import static com.ebay.epic.utils.Property.*;

public class FlinkKafkaSinkBuilder<T> {

    private final DataStream<T> dataStream;
    private DataCenter dc;
    private String operatorName;
    private String uid;
    private String sinkSlotGroup;
    private int parallelism = getInteger(DEFAULT_PARALLELISM);
    private EventType eventType;
    private String topic;
    private String topicSubject;
    private Class<T> className;
    private ConfigManager configManager;
    public FlinkKafkaSinkBuilder(DataStream<T> dataStream, DataCenter dc, EventType eventType) {
        this.dataStream = dataStream;
        this.dc=dc;
        this.eventType=eventType;
        this.configManager = new ConfigManager(dc,eventType,true);
    }

    public FlinkKafkaSinkBuilder<T> operatorName(String operatorName) {
        this.operatorName = configManager.getOPName(operatorName);
        return this;
    }

    public FlinkKafkaSinkBuilder<T> parallelism(String parallelism) {
        this.parallelism = configManager.getParallelism(parallelism);
        return this;
    }

    public FlinkKafkaSinkBuilder<T> uid(String uid) {
        this.uid = configManager.getOPUid(uid);
        return this;
    }

    public FlinkKafkaSinkBuilder<T> topic(String topic) {
        this.topic = configManager.getStrValueNODC(topic);
        return this;
    }

    public FlinkKafkaSinkBuilder<T> topicSubject(String topicSubject) {
        this.topicSubject = configManager.getStrValueNODC(topicSubject);
        return this;
    }

    public FlinkKafkaSinkBuilder<T> slotGroup(String slotGroup) {
        this.sinkSlotGroup = configManager.getSlotSharingGroup(slotGroup);
        return this;
    }

    public FlinkKafkaSinkBuilder<T> className(Class<T> tClass) {
        this.className = tClass;
        return this;
    }

    public void build() {
        KafkaProducerConfig config = KafkaProducerConfig.build(this.dc,this.eventType);
        FlinkKafkaProducerFactory producerFactory = new FlinkKafkaProducerFactory(config);
        dataStream.addSink(producerFactory.get(
                        className,
                        getString(Property.RHEOS_KAFKA_REGISTRY_URL),
                        topic,
                        topicSubject,
                        getString(PRODUCER_ID),
                        getBoolean(ALLOW_DROP),
                        getStringArray(FLINK_APP_SINK_MESSAGE_KEY, ",")))
                .setParallelism(parallelism)
                .slotSharingGroup(sinkSlotGroup)
                .name(operatorName)
                .uid(uid);
    }
}
