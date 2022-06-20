package com.ebay.epic.flink.pipeline;

import com.ebay.epic.common.enums.EventType;
import com.ebay.epic.flink.connector.kafka.config.KafkaProducerConfig;
import com.ebay.epic.flink.connector.kafka.factory.FlinkKafkaProducerFactory;
import com.ebay.epic.utils.FlinkEnvUtils;
import com.ebay.epic.utils.Property;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import static com.ebay.epic.utils.FlinkEnvUtils.*;
import static com.ebay.epic.utils.FlinkEnvUtils.getString;
import static com.ebay.epic.utils.Property.*;
import static com.ebay.epic.utils.Property.SINK_SLOT_SHARE_GROUP_RNO_UTP;

public abstract class FlinkBaseJob {

    public static StreamExecutionEnvironment streamExecutionEnvironmentBuilder(String[] args){
        // 0.0 Prepare execution environment
        // 0.1 UBI configuration
        // 0.2 Flink configuration
        StreamExecutionEnvironment executionEnvironment = FlinkEnvUtils.prepare(args);
        return executionEnvironment;
    }

    public abstract DataStream consumerBuilder(StreamExecutionEnvironment see, EventType eventType);

    public abstract void kafkaSinkBuilder(DataStream dataStream, EventType eventType);

    public void baseSinkBuilder(DataStream dataStream, Class className, String opName, String opUid) {
        // sink to kafka
        KafkaProducerConfig config = KafkaProducerConfig.ofDC(getString(FLINK_APP_SINK_DC));
        FlinkKafkaProducerFactory producerFactory = new FlinkKafkaProducerFactory(config);
        dataStream.addSink(producerFactory.get(
                        className,
                        getString(Property.RHEOS_KAFKA_REGISTRY_URL),
                        getString(FLINK_APP_SINK_TOPIC_UTP),
                        getString(FLINK_APP_SINK_TOPIC_SUBJECT_UTP),
                        getString(PRODUCER_ID),
                        getBoolean(ALLOW_DROP),
                        getStringArray(FLINK_APP_SINK_MESSAGE_KEY, ",")))
                .setParallelism(getInteger(SINK_KAFKA_PARALLELISM))
                .slotSharingGroup(getString(SINK_SLOT_SHARE_GROUP_RNO_UTP))
                .name(getString(opName))
                .uid(getString(opUid));
    }
}
