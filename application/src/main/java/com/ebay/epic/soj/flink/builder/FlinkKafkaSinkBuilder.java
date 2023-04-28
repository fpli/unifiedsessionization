package com.ebay.epic.soj.flink.builder;

import com.ebay.epic.soj.common.enums.DataCenter;
import com.ebay.epic.soj.common.enums.EventType;
import com.ebay.epic.soj.flink.connector.kafka.config.ConfigManager;
import com.ebay.epic.soj.flink.connector.kafka.config.KafkaProducerConfig;
import com.ebay.epic.soj.flink.connector.kafka.factory.FlinkKafkaProducerFactory;
import com.ebay.epic.soj.common.utils.Property;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.functions.sink.DiscardingSink;

import static com.ebay.epic.soj.flink.utils.FlinkEnvUtils.*;
import static com.ebay.epic.soj.common.utils.Property.*;

public class FlinkKafkaSinkBuilder<T> {

    private final DataStream<T> dataStream;
    private DataCenter dc;
    private String operatorName;
    private String uid;
    private String sinkSlotGroup;
    private int parallelism = getInteger(DEFAULT_PARALLELISM);
    private int maxParallelism = getInteger(MAX_PARALLELISM_SINK);
    private EventType eventType;
    private String topic;
    private String topicSubject;
    private Class<T> className;
    private ConfigManager configManager;

    public FlinkKafkaSinkBuilder(DataStream<T> dataStream, DataCenter dc, EventType eventType) {
        this.dataStream = dataStream;
        this.dc = dc;
        this.eventType = eventType;
        this.configManager = new ConfigManager(dc, eventType, true);

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
        this.topic = configManager.getTopic(topic, configManager.constructPostFix(configManager.DEL_POINT));
        return this;
    }

    public FlinkKafkaSinkBuilder<T> topicSubject(String topicSubject) {
        switch (this.eventType) {
            case LATE_NATIVE: {
                this.topicSubject = configManager.getTopicSubjectOR(topicSubject, EventType.AUTOTRACK_NATIVE);
                break;
            }
            case LATE_WEB: {
                this.topicSubject = configManager.getTopicSubjectOR(topicSubject, EventType.AUTOTRACK_WEB);
                break;
            }
            case LATE_UBI_BOT: {
                this.topicSubject = configManager.getTopicSubjectOR(topicSubject, EventType.UBI_BOT);
                break;
            }
            case LATE_UBI_NONBOT: {
                this.topicSubject = configManager.getTopicSubjectOR(topicSubject, EventType.UBI_NONBOT);
                break;
            }
            default: {
                this.topicSubject = configManager.getTopicSubject(topicSubject);
                break;
            }
        }
        return this;
    }

    public FlinkKafkaSinkBuilder<T> slotGroup(String slotGroup) {
        this.sinkSlotGroup = configManager.getSlotSharingGroupNoPF(slotGroup);
        return this;
    }

    public FlinkKafkaSinkBuilder<T> className(Class<T> tClass) {
        this.className = tClass;
        return this;
    }

    public void build() {
        KafkaProducerConfig config = KafkaProducerConfig.build(this.dc, this.eventType);
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
                .uid(uid)
                .getTransformation()
                .setMaxParallelism(maxParallelism);
    }

    public void buildWithDiscardSink() {
        KafkaProducerConfig config = KafkaProducerConfig.build(this.dc, this.eventType);
        FlinkKafkaProducerFactory producerFactory = new FlinkKafkaProducerFactory(config);
        dataStream.addSink(new DiscardingSink<>())
                .setParallelism(parallelism)
                .slotSharingGroup(sinkSlotGroup)
                .name(operatorName)
                .uid(uid)
                .getTransformation()
                .setMaxParallelism(maxParallelism);
    }

}
