/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ebay.epic.flink.pipeline;

import com.ebay.epic.common.constant.OutputTagConstants;
import com.ebay.epic.common.enums.EventType;
import com.ebay.epic.common.enums.SchemaSubject;
import com.ebay.epic.common.model.*;
import com.ebay.epic.flink.connector.kafka.SourceDataStreamBuilder;
import com.ebay.epic.flink.connector.kafka.schema.RawEventKafkaDeserializationSchemaWrapper;
import com.ebay.epic.flink.connector.kafka.schema.RawEventUniDeserializationSchema;
import com.ebay.epic.flink.function.*;
import com.ebay.epic.flink.window.CompositeTrigger;
import com.ebay.epic.flink.window.MidnightOpenSessionTrigger;
import com.ebay.epic.flink.window.RawEventTimeSessionWindows;
import com.ebay.epic.utils.FlinkEnvUtils;
import com.ebay.epic.utils.Property;
import lombok.val;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.transformations.OneInputTransformation;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.triggers.EventTimeTrigger;
import org.apache.flink.streaming.runtime.operators.windowing.WindowOperatorHelper;

import static com.ebay.epic.common.enums.DataCenter.*;
import static com.ebay.epic.utils.FlinkEnvUtils.*;
import static com.ebay.epic.utils.Property.*;

public class UniSessRTJob extends FlinkBaseJob {

    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment see = streamExecutionEnvironmentBuilder(args);
        UniSessRTJob uniSessRTJob = new UniSessRTJob();
        DataStream surface = uniSessRTJob.consumerBuilder(see, EventType.AUTOTRACK);
        DataStream ubi = uniSessRTJob.consumerBuilder(see, EventType.UBI);
        DataStream utp = uniSessRTJob.consumerBuilder(see, EventType.UTP);

        DataStream uniDs = surface.union(ubi).union(utp);
        // Filter logic before normalizer
        val rawEventPreFilterDS =
                uniDs
                        .filter(new TrackingEventPreFilterFunction())
                        .setParallelism(getInteger(PRE_FILTER_PARALLELISM))
                        .name(getString(PRE_FILTER_OP_NAME))
                        .uid(getString(PRE_FILTER_OP_UID));

        // session window
        SingleOutputStreamOperator<UniSession> ubiSessionDataStream =
                rawEventPreFilterDS
                        .keyBy("guid")
                        .window(RawEventTimeSessionWindows.withGapAndMaxDuration(Time.minutes(30),
                                Time.hours(24)))
                        .trigger(CompositeTrigger.Builder.create().trigger(EventTimeTrigger.create())
                                .trigger(MidnightOpenSessionTrigger
                                        .of(Time.hours(7))).build())
                        .sideOutputLateData(OutputTagConstants.lateEventOutputTag)
                        .aggregate(new UniSessionAgg(), new UniSessionWindowProcessFunction());

        WindowOperatorHelper.enrichWindowOperator(
                (OneInputTransformation) ubiSessionDataStream.getTransformation(),
                new RawEventMapWithStateFunction(),
                OutputTagConstants.mappedEventOutputTag);

        ubiSessionDataStream
                .setParallelism(getInteger(Property.SESSION_PARALLELISM))
                .slotSharingGroup(getString(SESSION_WINDOR_SLOT_SHARE_GROUP))
                .name("Session Operator")
                .uid("session-operator").setMaxParallelism(getInteger(PARALLELISM_MAX));

        DataStream<RawEvent> rawEventWithSessionId =
                ubiSessionDataStream.getSideOutput(OutputTagConstants.mappedEventOutputTag);

        DataStream<RawEvent> latedStream =
                ubiSessionDataStream.getSideOutput(OutputTagConstants.lateEventOutputTag);

        DataStream<RawEvent> surfaceDS =
                rawEventWithSessionId.filter(new TrackingEventPostFilterFunction(EventType.AUTOTRACK))
                        .setParallelism(getInteger(POST_FILTER_PARALLELISM))
                        .name(getString(POST_FILTER_OP_NAME_AUTOTRACK))
                        .uid(getString(POST_FILTER_OP_UID_AUTOTRACK));

        DataStream<RawEvent> ubiDS =
                rawEventWithSessionId.filter(new TrackingEventPostFilterFunction(EventType.UBI))
                        .setParallelism(getInteger(POST_FILTER_PARALLELISM))
                        .name(getString(POST_FILTER_OP_NAME_UBI))
                        .uid(getString(POST_FILTER_OP_UID_UBI));

        DataStream<RawEvent> utpDS =
                rawEventWithSessionId.filter(new TrackingEventPostFilterFunction(EventType.UTP))
                        .setParallelism(getInteger(POST_FILTER_PARALLELISM))
                        .name(getString(POST_FILTER_OP_NAME_UTP))
                        .uid(getString(POST_FILTER_OP_UID_UTP));

        uniSessRTJob.kafkaSinkBuilder(surfaceDS, EventType.AUTOTRACK);
        uniSessRTJob.kafkaSinkBuilder(ubiDS, EventType.UBI);
        uniSessRTJob.kafkaSinkBuilder(utpDS, EventType.UTP);

        // Submit this job
        FlinkEnvUtils.execute(see, getString(FLINK_APP_NAME));
    }

    @Override
    public DataStream consumerBuilder(StreamExecutionEnvironment see, EventType eventType) {
        DataStream dataStream = null;
        switch (eventType) {
            case AUTOTRACK: {
                dataStream = autoTrackConsumerBuilder(see);
                break;
            }
            case UBI: {
                dataStream = ubiConsumerBuilder(see);
                break;
            }
            case UTP: {
                dataStream = utpConsumerBuilder(see);
                break;
            }
            default:
                break;

        }
        return dataStream;
    }

    @Override
    public void kafkaSinkBuilder(DataStream dataStream, EventType eventType) {
        switch (eventType) {
            case AUTOTRACK: {
                autoTrackSinkBuilder(dataStream);
                break;
            }
            case UBI: {
                ubiSinkBuilder(dataStream);
                break;
            }
            case UTP: {
                utpSinkBuilder(dataStream);
                break;
            }
            default:
                break;

        }
    }

    private DataStream autoTrackConsumerBuilder(StreamExecutionEnvironment see) {
        // 1. Rheos Consumer
        // 1.1 Consume RawEvent from Rheos PathFinder topic
        // 1.2 Assign timestamps and emit watermarks.
        SourceDataStreamBuilder<RawEvent> dataStreamBuilder =
                new SourceDataStreamBuilder(see);

        // 1. Rheos Consumer
        // 1.1 Consume RawEvent from Rheos PathFinder topic
        // 1.2 Assign timestamps and emit watermarks.
        DataStream<RawEvent> rawEventDataStreamForRNO = dataStreamBuilder
                .dc(RNO)
                .operatorName(getString(SOURCE_OPERATOR_NAME_RNO_AUTOTRACK))
                .eventType(EventType.AUTOTRACK)
                .uid(getString(SOURCE_UID_RNO_AUTOTRACK))
                .slotGroup(getString(SOURCE_SLOT_SHARE_GROUP_RNO_AUTOTRACK))
                .outOfOrderlessInMin(getInteger(FLINK_APP_SOURCE_OUT_OF_ORDERLESS_IN_MIN_AUTOTRACK))
                .fromTimestamp(getString(FLINK_APP_SOURCE_FROM_TIMESTAMP_AUTOTRACK))
                .idleSourceTimeout(getInteger(FLINK_APP_IDLE_SOURCE_TIMEOUT_IN_MIN_AUTOTRACK))
                .build(new RawEventKafkaDeserializationSchemaWrapper(
                        new RawEventUniDeserializationSchema(
                                FlinkEnvUtils.getString(Property.RHEOS_KAFKA_REGISTRY_URL),
                                SchemaSubject.SURFACE)));
        // 1. Rheos Consumer
        // 1.1 Consume RawEvent from Rheos PathFinder topic
        // 1.2 Assign timestamps and emit watermarks.
        DataStream<RawEvent> rawEventDataStreamForSLC = dataStreamBuilder
                .dc(SLC)
                .operatorName(getString(SOURCE_OPERATOR_NAME_SLC_AUTOTRACK))
                .eventType(EventType.AUTOTRACK)
                .uid(getString(SOURCE_UID_SLC_AUTOTRACK))
                .slotGroup(getString(Property.SOURCE_SLOT_SHARE_GROUP_SLC_AUTOTRACK))
                .outOfOrderlessInMin(getInteger(FLINK_APP_SOURCE_OUT_OF_ORDERLESS_IN_MIN_AUTOTRACK))
                .fromTimestamp(getString(FLINK_APP_SOURCE_FROM_TIMESTAMP_AUTOTRACK))
                .idleSourceTimeout(getInteger(FLINK_APP_IDLE_SOURCE_TIMEOUT_IN_MIN_AUTOTRACK))
                .build(new RawEventKafkaDeserializationSchemaWrapper(
                        new RawEventUniDeserializationSchema(
                                FlinkEnvUtils.getString(Property.RHEOS_KAFKA_REGISTRY_URL),
                                SchemaSubject.SURFACE)));
        // 1. Rheos Consumer
        // 1.1 Consume RawEvent from Rheos PathFinder topic
        // 1.2 Assign timestamps and emit watermarks.
        DataStream<RawEvent> rawEventDataStreamForLVS = dataStreamBuilder
                .dc(LVS)
                .operatorName(getString(SOURCE_OPERATOR_NAME_LVS_AUTOTRACK))
                .eventType(EventType.AUTOTRACK)
                .uid(getString(SOURCE_UID_LVS_AUTOTRACK))
                .slotGroup(getString(Property.SOURCE_SLOT_SHARE_GROUP_LVS_AUTOTRACK))
                .outOfOrderlessInMin(getInteger(FLINK_APP_SOURCE_OUT_OF_ORDERLESS_IN_MIN_AUTOTRACK))
                .fromTimestamp(getString(FLINK_APP_SOURCE_FROM_TIMESTAMP_AUTOTRACK))
                .idleSourceTimeout(getInteger(FLINK_APP_IDLE_SOURCE_TIMEOUT_IN_MIN_AUTOTRACK))
                .build(new RawEventKafkaDeserializationSchemaWrapper(
                        new RawEventUniDeserializationSchema(
                                FlinkEnvUtils.getString(Property.RHEOS_KAFKA_REGISTRY_URL),
                                SchemaSubject.SURFACE)));

        // union ubiEvent from SLC/RNO/LVS
        DataStream<RawEvent> rawEventDataStream = rawEventDataStreamForRNO
                .union(rawEventDataStreamForSLC)
                .union(rawEventDataStreamForLVS);
        return rawEventDataStream;
    }

    private DataStream ubiConsumerBuilder(StreamExecutionEnvironment see) {
        // 1. Rheos Consumer
        // 1.1 Consume RawEvent from Rheos PathFinder topic
        // 1.2 Assign timestamps and emit watermarks.
        SourceDataStreamBuilder<RawEvent> dataStreamBuilder =
                new SourceDataStreamBuilder<>(see);

        // 1. Rheos Consumer
        // 1.1 Consume RawEvent from Rheos PathFinder topic
        // 1.2 Assign timestamps and emit watermarks.
        DataStream<RawEvent> rawEventDataStreamForRNO = dataStreamBuilder
                .dc(RNO)
                .operatorName(getString(SOURCE_OPERATOR_NAME_RNO_UBI))
                .eventType(EventType.UBI)
                .uid(getString(SOURCE_UID_RNO_UBI))
                .slotGroup(getString(SOURCE_SLOT_SHARE_GROUP_RNO_UBI))
                .outOfOrderlessInMin(getInteger(FLINK_APP_SOURCE_OUT_OF_ORDERLESS_IN_MIN_UBI))
                .fromTimestamp(getString(FLINK_APP_SOURCE_FROM_TIMESTAMP_UBI))
                .idleSourceTimeout(getInteger(FLINK_APP_IDLE_SOURCE_TIMEOUT_IN_MIN_UBI))
                .build(new RawEventKafkaDeserializationSchemaWrapper(
                        new RawEventUniDeserializationSchema(
                                FlinkEnvUtils.getString(Property.RHEOS_KAFKA_REGISTRY_URL),
                                SchemaSubject.UBI)));
        return rawEventDataStreamForRNO;
    }

    private DataStream utpConsumerBuilder(StreamExecutionEnvironment see) {
        // 1. Rheos Consumer
        // 1.1 Consume RawEvent from Rheos PathFinder topic
        // 1.2 Assign timestamps and emit watermarks.
        SourceDataStreamBuilder<RawEvent> dataStreamBuilder =
                new SourceDataStreamBuilder<>(see);
        // 1. Rheos Consumer
        // 1.1 Consume RawEvent from Rheos PathFinder topic
        // 1.2 Assign timestamps and emit watermarks.
        DataStream<RawEvent> rawEventDataStreamForSLC = dataStreamBuilder
                .dc(SLC)
                .operatorName(getString(SOURCE_OPERATOR_NAME_SLC_UTP))
                .eventType(EventType.UTP)
                .uid(getString(SOURCE_UID_SLC_UTP))
                .slotGroup(getString(SOURCE_SLOT_SHARE_GROUP_SLC_UTP))
                .outOfOrderlessInMin(getInteger(FLINK_APP_SOURCE_OUT_OF_ORDERLESS_IN_MIN_UTP))
                .fromTimestamp(getString(FLINK_APP_SOURCE_FROM_TIMESTAMP_UTP))
                .idleSourceTimeout(getInteger(FLINK_APP_IDLE_SOURCE_TIMEOUT_IN_MIN_UTP))
                .build(new RawEventKafkaDeserializationSchemaWrapper(
                        new RawEventUniDeserializationSchema(
                                FlinkEnvUtils.getString(Property.RHEOS_KAFKA_REGISTRY_URL),
                                SchemaSubject.UTP)));
        // 1. Rheos Consumer
        // 1.1 Consume RawEvent from Rheos PathFinder topic
        // 1.2 Assign timestamps and emit watermarks.
        DataStream<RawEvent> rawEventDataStreamForLVS = dataStreamBuilder
                .dc(LVS)
                .operatorName(getString(SOURCE_OPERATOR_NAME_LVS_UTP))
                .eventType(EventType.UTP)
                .uid(getString(SOURCE_UID_LVS_UTP))
                .slotGroup(getString(Property.SOURCE_SLOT_SHARE_GROUP_LVS_UTP))
                .outOfOrderlessInMin(getInteger(FLINK_APP_SOURCE_OUT_OF_ORDERLESS_IN_MIN_UTP))
                .fromTimestamp(getString(FLINK_APP_SOURCE_FROM_TIMESTAMP_UTP))
                .idleSourceTimeout(getInteger(Property.FLINK_APP_IDLE_SOURCE_TIMEOUT_IN_MIN_UTP))
                .build(new RawEventKafkaDeserializationSchemaWrapper(
                        new RawEventUniDeserializationSchema(
                                FlinkEnvUtils.getString(Property.RHEOS_KAFKA_REGISTRY_URL),
                                SchemaSubject.UTP)));

        // union ubiEvent from SLC/RNO/LVS
        DataStream<RawEvent> rawEventDataStream = rawEventDataStreamForSLC
                .union(rawEventDataStreamForLVS);
        return rawEventDataStream;
    }

    private void autoTrackSinkBuilder(DataStream dataStream) {
        baseSinkBuilder(dataStream, RawEvent.class, SINK_OPERATOR_NAME_RNO_AUTOTRACK, SINK_UID_RNO_AUTOTRACK,
                FLINK_APP_SINK_TOPIC_AUTOTRACK,FLINK_APP_SINK_TOPIC_SUBJECT_AUTOTRACK,SINK_SLOT_SHARE_GROUP_RNO_AUTOTRACK);
    }

    private void ubiSinkBuilder(DataStream dataStream) {
        baseSinkBuilder(dataStream, RawEvent.class, SINK_OPERATOR_NAME_RNO_UBI, SINK_UID_RNO_UBI,
                FLINK_APP_SINK_TOPIC_UBI,FLINK_APP_SINK_TOPIC_SUBJECT_UBI,SINK_SLOT_SHARE_GROUP_RNO_UBI);
    }

    private void utpSinkBuilder(DataStream dataStream) {
        baseSinkBuilder(dataStream, RawEvent.class, SINK_OPERATOR_NAME_RNO_UTP, SINK_UID_RNO_UTP,
                FLINK_APP_SINK_TOPIC_UTP,FLINK_APP_SINK_TOPIC_SUBJECT_UTP,SINK_SLOT_SHARE_GROUP_RNO_UTP );
    }
}
