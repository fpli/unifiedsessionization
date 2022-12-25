package com.ebay.epic.flink.pipeline;

import com.ebay.epic.common.enums.Category;
import com.ebay.epic.common.enums.DataCenter;
import com.ebay.epic.common.enums.EventType;
import com.ebay.epic.common.enums.SchemaSubject;
import com.ebay.epic.common.model.UniSession;
import com.ebay.epic.common.model.raw.RawEvent;
import com.ebay.epic.common.model.raw.UniEvent;
import com.ebay.epic.flink.connector.kafka.*;
import com.ebay.epic.flink.connector.kafka.schema.RawEventKafkaDeserializationSchemaWrapper;
import com.ebay.epic.flink.connector.kafka.schema.RawEventUniDeserializationSchema;
import com.ebay.epic.flink.function.*;
import com.ebay.epic.utils.FlinkEnvUtils;
import com.ebay.epic.utils.Property;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.List;

import static com.ebay.epic.utils.FlinkEnvUtils.getInteger;
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
                                eventType.getName().toUpperCase())));
        return rawEventDataStream;
    }

    /**
     *
     * @param dataStream
     * @param eventType
     * @param dataCenter
     * @param <T>
     */
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
                .slotGroup(POST_FILTER_SLOT_SHARE_GROUP)
                .build();
    }

    public SingleOutputStreamOperator<UniEvent> uniEevntSplitFunctionBuilder(DataStream<UniEvent> dataStream,
                                                                             EventType eventType,
                                                                             boolean enbaleMetrics) {
        FlinkSplitFunctionBuilder<UniEvent> flinkFilterFunctionBuilder =
                new FlinkSplitFunctionBuilder(dataStream,eventType,true);
        return flinkFilterFunctionBuilder
                .process(new UniEventSplitProcessFunction(eventType,
                        getInteger(Property.METRIC_WINDOW_SIZE),enbaleMetrics))
                .parallelism(SPLIT_PARALLELISM)
                .operatorName(SPLIT_OP_NAME_BASE)
                .uid(SPLIT_OP_UID_BASE)
                .slotGroup(SPLIT_SLOT_SHARE_GROUP)
                .build();
    }

    public SingleOutputStreamOperator<UniSession> uniSessionSplitFunctionBuilder(DataStream<UniSession> dataStream,
                                                                            EventType eventType) {
        FlinkSplitFunctionBuilder<UniSession> flinkFilterFunctionBuilder =
                new FlinkSplitFunctionBuilder(dataStream,eventType,true);
        return flinkFilterFunctionBuilder.process(new UniSessionSplitProcessFunction())
                .parallelism(SPLIT_PARALLELISM)
                .operatorName(SPLIT_OP_NAME_BASE)
                .uid(SPLIT_OP_UID_BASE)
                .slotGroup(SPLIT_SLOT_SHARE_GROUP)
                .build();
    }


    public SingleOutputStreamOperator<UniSession> postFilterSessionFunctionBuilder(DataStream<UniSession> dataStream,
                                                                            EventType eventType,
                                                                            DataCenter dataCenter) {
        FlinkFilterFunctionBuilder<UniSession> flinkFilterFunctionBuilder =
                new FlinkFilterFunctionBuilder(dataStream, dataCenter, eventType,true);
        return flinkFilterFunctionBuilder.filter(new UniSessionPostFilterFunction(eventType))
                .parallelism(POST_FILTER_PARALLELISM)
                .operatorName(POST_FILTER_OP_NAME_BASE)
                .uid(POST_FILTER_OP_UID_BASE)
                .slotGroup(POST_FILTER_SLOT_SHARE_GROUP)
                .build();
    }

    public SingleOutputStreamOperator<RawEvent> preFilterFunctionBuilder(DataStream<RawEvent> dataStream,
                                                                          DataCenter dataCenter) {
        FlinkFilterFunctionBuilder<RawEvent> flinkFilterFunctionBuilder =
                new FlinkFilterFunctionBuilder(dataStream, dataCenter,false);

        return flinkFilterFunctionBuilder.filter(new TrackingEventPreFilterFunction())
                .parallelism(PRE_FILTER_PARALLELISM)
                .operatorName(PRE_FILTER_OP_NAME)
                .uid(PRE_FILTER_OP_UID)
                .slotGroup(PRE_FILTER_SLOT_SHARE_GROUP)
                .build();
    }

    public SingleOutputStreamOperator<RawEvent> preFilterFunctionBuilder(DataStream<RawEvent> dataStream) {
      return this.preFilterFunctionBuilder(dataStream,null);
    }

    public SingleOutputStreamOperator<UniEvent> normalizerFunctionBuilder(DataStream<RawEvent> dataStream,
                                                                          DataCenter dataCenter) {
        FlinkMapFunctionBuilder<RawEvent,UniEvent> flinkMapFunctionBuilder =
                new FlinkMapFunctionBuilder(dataStream, dataCenter,false);
        return flinkMapFunctionBuilder.map(new TrackingEventMapFunction())
                .parallelism(NORMALIZER_PARALLELISM)
                .operatorName(NORMALIZER_OP_NAME)
                .uid(NORMALIZER_OP_UID)
                .slotGroup(NORMALIZER_SLOT_SHARE_GROUP)
                .build();
    }

    public SingleOutputStreamOperator<UniEvent> normalizerFunctionBuilder(DataStream<RawEvent> dataStream) {
      return this.normalizerFunctionBuilder(dataStream,null);
    }
}
