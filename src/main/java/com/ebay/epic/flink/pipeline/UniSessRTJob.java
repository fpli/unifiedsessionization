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
import com.ebay.epic.common.enums.DataCenter;
import com.ebay.epic.common.enums.EventType;
import com.ebay.epic.common.enums.SchemaSubject;
import com.ebay.epic.common.model.UniSession;
import com.ebay.epic.common.model.raw.RawEvent;
import com.ebay.epic.common.model.raw.RawUniSession;
import com.ebay.epic.common.model.raw.UniEvent;
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
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.lang3.StringUtils;
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
        see.disableOperatorChaining();
        UniSessRTJob uniSessRTJob = new UniSessRTJob();

        // consumer
        DataStream<RawEvent> surface = uniSessRTJob.consumerBuilder(see, EventType.AUTOTRACK, RNO);
        DataStream<RawEvent> ubi = uniSessRTJob.consumerBuilder(see, EventType.UBI, RNO);
        DataStream<RawEvent> utp = uniSessRTJob.consumerBuilder(see, EventType.UTP, LVS);

        // prefilter for each source
        val rawEventPreFilterDSAT =
                uniSessRTJob.preFilterFunctionBuilder(surface, EventType.AUTOTRACK, null);

        val rawEventPreFilterDSUBI =
                uniSessRTJob.preFilterFunctionBuilder(ubi, EventType.UBI, null);

        val rawEventPreFilterDSUTP =
                uniSessRTJob.preFilterFunctionBuilder(utp, EventType.UTP, null);

        //Normalizer for each source
        val rawEventNormalizerDsAT
                = uniSessRTJob.normalizerFunctionBuilder(rawEventPreFilterDSAT,EventType.AUTOTRACK, null);
        val rawEventNormalizerDsUBI
                = uniSessRTJob.normalizerFunctionBuilder(rawEventPreFilterDSUBI,EventType.UBI, null);
        val rawEventNormalizerDsUTP
                = uniSessRTJob.normalizerFunctionBuilder(rawEventPreFilterDSUTP,EventType.UTP, null);

        //Union all three sources into one DataStream
        DataStream<UniEvent> uniDs = rawEventNormalizerDsAT.union(rawEventNormalizerDsUBI)
                .union(rawEventNormalizerDsUTP);

//        // Filter logic before normalizer
//        val rawEventPreFilterDS =
//                uniSessRTJob.preFilterFunctionBuilder(uniDs, EventType.AUTOTRACK, null);
//        val rawEventNormalizerDs
//                = uniSessRTJob.normalizerFunctionBuilder(rawEventPreFilterDS);
        // session window
        SingleOutputStreamOperator<RawUniSession> uniSessionDataStream =
                uniDs.keyBy("guid")
                        .window(RawEventTimeSessionWindows.withGapAndMaxDuration(Time.minutes(30),
                                Time.hours(24)))
                        .trigger(CompositeTrigger.Builder.create().trigger(EventTimeTrigger.create())
                                .trigger(MidnightOpenSessionTrigger
                                        .of(Time.hours(7))).build())
                        .sideOutputLateData(OutputTagConstants.lateEventOutputTag)
                        .aggregate(new UniSessionAgg(), new UniSessionWindowProcessFunction())
                        .setParallelism(getInteger(Property.SESSION_PARALLELISM))
                        .slotSharingGroup(getString(SESSION_WINDOR_SLOT_SHARE_GROUP))
                        .name("Session Operator")
                        .uid("session-operator")
                        .setMaxParallelism(getInteger(PARALLELISM_MAX));

        WindowOperatorHelper.enrichWindowOperator(
                (OneInputTransformation<UniEvent, RawUniSession>) uniSessionDataStream.getTransformation(),
                new RawEventMapWithStateFunction(),
                OutputTagConstants.mappedEventOutputTag);

        DataStream<UniEvent> rawEventWithSessionId =
                uniSessionDataStream.getSideOutput(OutputTagConstants.mappedEventOutputTag);
        DataStream<UniEvent> latedStream =
                uniSessionDataStream.getSideOutput(OutputTagConstants.lateEventOutputTag);

        DataStream<UniEvent> surfaceDS = uniSessRTJob.postFilterFunctionBuilder(rawEventWithSessionId, EventType.AUTOTRACK, RNO);
        DataStream<UniEvent> ubiDS = uniSessRTJob.postFilterFunctionBuilder(rawEventWithSessionId, EventType.UBI, RNO);
        DataStream<UniEvent> utpDS = uniSessRTJob.postFilterFunctionBuilder(rawEventWithSessionId, EventType.UTP, RNO);
        SingleOutputStreamOperator<UniSession> uniSessionDS =
                uniSessionDataStream
                        .process(new RawUniSessionToUniSessionProcessFunction())
                        .setParallelism(getInteger(Property.SESSION_PARALLELISM))
                        .slotSharingGroup(getString(SESSION_WINDOR_SLOT_SHARE_GROUP))
                        .name("RawUniSession to UniSession")
                        .uid("RawUniSession-to-UniSession");
        uniSessRTJob.kafkaSinkBuilder(surfaceDS, EventType.AUTOTRACK, RNO);
        uniSessRTJob.kafkaSinkBuilder(ubiDS, EventType.UBI, RNO);
        uniSessRTJob.kafkaSinkBuilder(utpDS, EventType.UTP, RNO);
        uniSessRTJob.kafkaSinkBuilder(uniSessionDS, EventType.SESSION, RNO);

        // Submit this job
        FlinkEnvUtils.execute(see, getString(FLINK_APP_NAME));
    }

}
