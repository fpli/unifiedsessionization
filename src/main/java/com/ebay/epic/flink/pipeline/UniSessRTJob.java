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
import com.ebay.epic.common.enums.Category;
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
import org.apache.flink.util.OutputTag;

import java.util.Arrays;

import static com.ebay.epic.common.constant.OutputTagConstants.*;
import static com.ebay.epic.common.enums.DataCenter.*;
import static com.ebay.epic.common.enums.EventType.*;
import static com.ebay.epic.utils.FlinkEnvUtils.*;
import static com.ebay.epic.utils.Property.*;

public class UniSessRTJob extends FlinkBaseJob {


    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment see = streamExecutionEnvironmentBuilder(args);
        UniSessRTJob uniSessRTJob = new UniSessRTJob();

        // consumer
        DataStream<RawEvent> surfaceWeb = uniSessRTJob.consumerBuilder(see, AUTOTRACK_WEB, RNO);
        DataStream<RawEvent> surfaceNative = uniSessRTJob.consumerBuilder(see, AUTOTRACK_NATIVE, RNO);
        DataStream<RawEvent> ubiBot = uniSessRTJob.consumerBuilder(see, UBI_BOT, RNO);
        DataStream<RawEvent> ubiNonBot = uniSessRTJob.consumerBuilder(see, UBI_NONBOT, RNO);
        DataStream<RawEvent> utpNonbot = uniSessRTJob.consumerBuilder(see, UTP_NONBOT, LVS);

        //Union all three sources into one DataStream
        DataStream<RawEvent> uniDs = surfaceWeb
                .union(surfaceNative)
                .union(ubiBot)
                .union(ubiNonBot)
                .union(utpNonbot);

        // prefilter for each source
        val rawEventPreFilterDS = uniSessRTJob.preFilterFunctionBuilder(uniDs);

        //Normalizer for each source
        // surface web
        val rawEventNormalizerDs
                = uniSessRTJob.normalizerFunctionBuilder(rawEventPreFilterDS);

        // session window
        SingleOutputStreamOperator<UniSession> uniSessionDataStream =
                rawEventNormalizerDs.keyBy("guid")
                        .window(RawEventTimeSessionWindows.withGapAndMaxDuration(Time.minutes(30),
                                Time.hours(24)))
                        .trigger(CompositeTrigger.Builder.create().trigger(EventTimeTrigger.create())
                                .trigger(MidnightOpenSessionTrigger
                                        .of(Time.hours(7))).build())
                        .sideOutputLateData(OutputTagConstants.lateEventOutputTag)
                        .aggregate(new UniSessionAgg(), new UniSessionWindowProcessFunction())
                        .setParallelism(getInteger(Property.SESSION_PARALLELISM))
                        .slotSharingGroup(getString(SESSION_WINDOR_SLOT_SHARE_GROUP))
                        .name(getString(SESSION_WINDOR_OPERATOR_NAME))
                        .uid(getString(SESSION_WINDOR_UID))
                        .setMaxParallelism(getInteger(PARALLELISM_MAX_SESSION));

        WindowOperatorHelper.enrichWindowOperator(
                (OneInputTransformation<UniEvent, UniSession>) uniSessionDataStream.getTransformation(),
                new RawEventMapWithStateFunction(),
                OutputTagConstants.mappedEventOutputTag);

        DataStream<UniEvent> uniEventDS =
                uniSessionDataStream.getSideOutput(OutputTagConstants.mappedEventOutputTag);
        DataStream<UniEvent> latedStream =
                uniSessionDataStream.getSideOutput(OutputTagConstants.lateEventOutputTag);

        // filter out each kind of event , surface native,web; ubi bot nonbot...etc

        SingleOutputStreamOperator<UniEvent> outputStreamOperator = uniSessRTJob.uniEevntSplitFunctionBuilder
                (uniEventDS, EventType.DEFAULT,true);
        DataStream<UniEvent> surfaceWebDS = outputStreamOperator.getSideOutput(atWEBOutputTag);
        DataStream<UniEvent> surfaceNativeDS = outputStreamOperator.getSideOutput(atNATIVEOutputTag);
        DataStream<UniEvent> ubiBotDS = outputStreamOperator.getSideOutput(ubiBOTOutputTag);
        DataStream<UniEvent> ubiNonBotDS = outputStreamOperator.getSideOutput(ubiNONBOTOutputTag);

        // filter our each kind of event based on late events
        SingleOutputStreamOperator<UniEvent> outputStreamOperatorLate = uniSessRTJob.uniEevntSplitFunctionBuilder
                (latedStream, DEFAULT_LATE,false);
        DataStream<UniEvent> surfaceLateWebDS = outputStreamOperatorLate.getSideOutput(atWEBOutputTagLate);
        DataStream<UniEvent> surfaceLateNativeDS = outputStreamOperatorLate.getSideOutput(atNATIVEOutputTagLate);
        DataStream<UniEvent> ubiBotLateDS = outputStreamOperatorLate.getSideOutput(ubiBOTOutputTagLate);
        DataStream<UniEvent> ubiNonLateBotDS = outputStreamOperatorLate.getSideOutput(ubiNONBOTOutputTagLate);

        // filter our each kinf of event based on late events
        SingleOutputStreamOperator<UniSession> outputStreamOperatorSess =
                uniSessRTJob.uniSessionSplitFunctionBuilder(uniSessionDataStream, SESSION_BOT);
        DataStream<UniSession> uniSessionNonbotDS = outputStreamOperatorSess.getSideOutput(uniSessNonbotOutputTag);
        DataStream<UniSession> uniSessionBotDS = outputStreamOperatorSess.getSideOutput(uniSessBotOutputTag);

        // noarmal event sink
        uniSessRTJob.kafkaSinkBuilder(surfaceWebDS, AUTOTRACK_WEB, RNO);
        uniSessRTJob.kafkaSinkBuilder(surfaceNativeDS, AUTOTRACK_NATIVE, RNO);
        uniSessRTJob.kafkaSinkBuilder(ubiBotDS, UBI_BOT, RNO);
        uniSessRTJob.kafkaSinkBuilder(ubiNonBotDS, UBI_NONBOT, RNO);

        // unisession sink
        uniSessRTJob.kafkaSinkBuilder(uniSessionNonbotDS, SESSION_NONBOT, RNO);
        //        uniSessRTJob.kafkaSinkBuilder(uniSessionBotDS, SESSION_BOT, RNO);

        // late event sink
        uniSessRTJob.kafkaSinkBuilder(surfaceLateWebDS, LATE_WEB, RNO);
        uniSessRTJob.kafkaSinkBuilder(surfaceLateNativeDS, LATE_NATIVE, RNO);
        uniSessRTJob.kafkaSinkBuilder(ubiBotLateDS, LATE_UBI_BOT, RNO);
        uniSessRTJob.kafkaSinkBuilder(ubiNonLateBotDS, LATE_UBI_NONBOT, RNO);

        // Submit this job
        FlinkEnvUtils.execute(see, getString(FLINK_APP_NAME));
    }

}
