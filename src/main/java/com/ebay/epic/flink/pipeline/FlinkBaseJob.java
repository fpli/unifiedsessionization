package com.ebay.epic.flink.pipeline;

import com.ebay.epic.common.enums.DataCenter;
import com.ebay.epic.common.enums.EventType;
import com.ebay.epic.common.enums.SchemaSubject;
import com.ebay.epic.common.model.raw.RawEvent;
import com.ebay.epic.common.model.raw.UniEvent;
import com.ebay.epic.flink.connector.kafka.FlinkFilterFunctionBuilder;
import com.ebay.epic.flink.connector.kafka.FlinkKafkaSinkBuilder;
import com.ebay.epic.flink.connector.kafka.FlinkMapFunctionBuilder;
import com.ebay.epic.flink.connector.kafka.SourceDataStreamBuilder;
import com.ebay.epic.flink.connector.kafka.schema.RawEventKafkaDeserializationSchemaWrapper;
import com.ebay.epic.flink.connector.kafka.schema.RawEventUniDeserializationSchema;
import com.ebay.epic.flink.function.TrackingEventMapFunction;
import com.ebay.epic.flink.function.TrackingEventPostFilterFunction;
import com.ebay.epic.flink.function.TrackingEventPreFilterFunction;
import com.ebay.epic.utils.FlinkEnvUtils;
import com.ebay.epic.utils.Property;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import static com.ebay.epic.utils.Property.*;

public abstract class FlinkBaseJob {

    public static StreamExecutionEnvironment streamExecutionEnvironmentBuilder(String[] args) {
        // 0.0 Prepare execution environment
        // 0.1 UBI configuration
        // 0.2 Flink configuration
        StreamExecutionEnvironment executionEnvironment = FlinkEnvUtils.prepare(args);
        return executionEnvironment;
    }

    public static StreamExecutionEnvironment streamExecutionEnvironmentBuilder4Local(String[] args) {
        // 0.0 Prepare execution environment
        // 0.1 UBI configuration
        // 0.2 Flink configuration
        StreamExecutionEnvironment executionEnvironment = FlinkEnvUtils.prepare4Local(args);
        return executionEnvironment;
    }

    public DataStream<RawEvent> consumerBuilder(StreamExecutionEnvironment see,
                                                EventType eventType,
                                                DataCenter dataCenter) {
        // 1. Rheos Consumer
        // 1.1 Consume RawEvent from Rheos PathFinder topic
        // 1.2 Assign timestamps and emit watermarks.
        SourceDataStreamBuilder<RawEvent> dataStreamBuilder =
                new SourceDataStreamBuilder(see, dataCenter, eventType);

        // 1. Rheos Consumer
        // 1.1 Consume RawEvent from Rheos PathFinder topic
        // 1.2 Assign timestamps and emit watermarks.
        DataStream<RawEvent> rawEventDataStream = dataStreamBuilder
                .operatorName(SOURCE_OPERATOR_NAME_BASE)
                .uid(SOURCE_UID_BASE)
                .slotGroup(SOURCE_SLOT_SHARE_GROUP_BASE)
                .outOfOrderlessInMin(FLINK_APP_SOURCE_OFO_BASE)
                .fromTimestamp(FLINK_APP_SOURCE_FROM_TS_BASE)
                .parallelism(SOURCE_PARALLELISM)
                .idleSourceTimeout(FLINK_APP_SOURCE_TIM_BASE)
                .build(new RawEventKafkaDeserializationSchemaWrapper(
                        new RawEventUniDeserializationSchema(
                                FlinkEnvUtils.getString(Property.RHEOS_KAFKA_REGISTRY_URL),
                                eventType.name())));
        return rawEventDataStream;
    }

    public <T> void kafkaSinkBuilder(DataStream<T> dataStream, EventType eventType, DataCenter dataCenter) {
        FlinkKafkaSinkBuilder<T> flinkKafkaSinkBuilder =
                new FlinkKafkaSinkBuilder<>(dataStream, dataCenter, eventType);
        flinkKafkaSinkBuilder.className(dataStream.getType().getTypeClass())
                .parallelism(SINK_KAFKA_PARALLELISM_BASE)
                .operatorName(SINK_OPERATOR_NAME_BASE)
                .topic(FLINK_APP_SINK_TOPIC_BASE)
                .topicSubject(FLINK_APP_SINK_TOPIC_SUBJECT_BASE)
                .uid(SINK_UID_BASE)
                .slotGroup(SINK_SLOT_SHARE_GROUP_BASE)
                .build();
    }

    public SingleOutputStreamOperator<UniEvent> postFilterFunctionBuilder(DataStream<UniEvent> dataStream,
                                                                          EventType eventType,
                                                                          DataCenter dataCenter) {
        FlinkFilterFunctionBuilder<UniEvent> flinkFilterFunctionBuilder =
                new FlinkFilterFunctionBuilder(dataStream, dataCenter, eventType,true);
        return flinkFilterFunctionBuilder.filter(new TrackingEventPostFilterFunction(eventType))
                .parallelism(POST_FILTER_PARALLELISM)
                .operatorName(POST_FILTER_OP_NAME_BASE)
                .uid(POST_FILTER_OP_UID_BASE)
                .build();
    }

    public SingleOutputStreamOperator<RawEvent> preFilterFunctionBuilder(DataStream<RawEvent> dataStream,
                                                                          EventType eventType,
                                                                          DataCenter dataCenter) {
        FlinkFilterFunctionBuilder<RawEvent> flinkFilterFunctionBuilder =
                new FlinkFilterFunctionBuilder(dataStream, dataCenter, eventType,false);

        return flinkFilterFunctionBuilder.filter(new TrackingEventPreFilterFunction())
                .parallelism(SOURCE_PARALLELISM+"."+eventType.getValue())
                .operatorName(PRE_FILTER_OP_NAME)
                .uid(PRE_FILTER_OP_UID)
                .slotGroup(SOURCE_SLOT_SHARE_GROUP_BASE+"."+eventType.getValue())
                .build();
    }

    public SingleOutputStreamOperator<UniEvent> normalizerFunctionBuilder(DataStream<RawEvent> dataStream,
                                                                          EventType eventType,
                                                                          DataCenter dataCenter) {
        FlinkMapFunctionBuilder<RawEvent,UniEvent> flinkMapFunctionBuilder =
                new FlinkMapFunctionBuilder(dataStream, dataCenter, eventType,false);
        return flinkMapFunctionBuilder.map(new TrackingEventMapFunction())
                .parallelism(SOURCE_PARALLELISM+"."+eventType.getValue())
                .operatorName(NORMALIZER_OP_NAME)
                .uid(NORMALIZER_OP_UID)
                .slotGroup(SOURCE_SLOT_SHARE_GROUP_BASE+"."+eventType.getValue())
                .build();
    }
}
