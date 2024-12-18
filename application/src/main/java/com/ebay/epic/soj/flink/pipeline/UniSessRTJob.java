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

package com.ebay.epic.soj.flink.pipeline;

import com.ebay.epic.soj.flink.constant.OutputTagConstants;
import com.ebay.epic.soj.common.enums.EventType;
import com.ebay.epic.soj.common.model.UniSession;
import com.ebay.epic.soj.common.model.raw.RawEvent;
import com.ebay.epic.soj.common.model.raw.UniEvent;
import com.ebay.epic.soj.flink.function.*;
import com.ebay.epic.soj.flink.window.CompositeTrigger;
import com.ebay.epic.soj.flink.window.CustomTimeDurationSessionTrigger;
import com.ebay.epic.soj.flink.window.MidnightOpenSessionTrigger;
import com.ebay.epic.soj.flink.window.RawEventTimeSessionWindows;
import com.ebay.epic.soj.flink.utils.FlinkEnvUtils;
import com.ebay.epic.soj.common.utils.Property;
import lombok.val;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.transformations.OneInputTransformation;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.triggers.EventTimeTrigger;
import org.apache.flink.streaming.runtime.operators.windowing.WindowOperatorHelper;

import static com.ebay.epic.soj.flink.constant.OutputTagConstants.*;
import static com.ebay.epic.soj.common.enums.DataCenter.*;
import static com.ebay.epic.soj.common.enums.EventType.*;
import static com.ebay.epic.soj.flink.utils.FlinkEnvUtils.*;
import static com.ebay.epic.soj.common.utils.Property.*;

public class UniSessRTJob extends FlinkBaseJob {


    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment see = streamExecutionEnvironmentBuilder(args);
        UniSessRTJob uniSessRTJob = new UniSessRTJob();

        // consumer
        DataStream<RawEvent> surfaceWeb = uniSessRTJob.consumerBuilder(see, AUTOTRACK_WEB, RNO);
        DataStream<RawEvent> surfaceNative = uniSessRTJob.consumerBuilder(see, AUTOTRACK_NATIVE, RNO);
        DataStream<RawEvent> ubiBot = uniSessRTJob.consumerBuilder(see, UBI_BOT, RNO);
        DataStream<RawEvent> ubiNonBot = uniSessRTJob.consumerBuilder(see, UBI_NONBOT, RNO);
        DataStream<RawEvent> utpNonBot = uniSessRTJob.consumerBuilder(see, UTP_NONBOT, LVS);
        // integrate roi source
        DataStream<RawEvent> roiNonBot = uniSessRTJob.consumerBuilder(see, ROI_NONBOT, LVS);

        // prefilter for each source
        DataStream<RawEvent> surfaceWebPreFilterDS = uniSessRTJob.preFilterFunctionBuilder(surfaceWeb, AUTOTRACK_WEB, RNO);
        DataStream<RawEvent> surfaceNativePreFilterDS = uniSessRTJob.preFilterFunctionBuilder(surfaceNative, AUTOTRACK_NATIVE, RNO);
        DataStream<RawEvent> ubiBotPreFilterDS = uniSessRTJob.preFilterFunctionBuilder(ubiBot, UBI_BOT, RNO);
        DataStream<RawEvent> ubiNonBotPreFilterDS = uniSessRTJob.preFilterFunctionBuilder(ubiNonBot, UBI_NONBOT, RNO);
        DataStream<RawEvent> utpNonBotPreFilterDS = uniSessRTJob.preFilterFunctionBuilder(utpNonBot, UTP_NONBOT, LVS);
        // roi prefilter
        DataStream<RawEvent> roiNonBotPreFilterDS = uniSessRTJob.preFilterFunctionBuilder(roiNonBot, ROI_NONBOT, LVS);
        //Normalizer for each source
        // surface web
        val surfaceWebNormalizerDs = uniSessRTJob.normalizerFunctionBuilder(surfaceWebPreFilterDS, AUTOTRACK_WEB, RNO);
        val surfaceNativeNormalizerDs
                = uniSessRTJob.normalizerFunctionBuilder(surfaceNativePreFilterDS, AUTOTRACK_NATIVE, RNO);
        val ubiBotNormalizerDs
                = uniSessRTJob.normalizerFunctionBuilder(ubiBotPreFilterDS, UBI_BOT, RNO);
        val ubiNonBotNormalizerDs
                = uniSessRTJob.normalizerFunctionBuilder(ubiNonBotPreFilterDS, UBI_NONBOT, RNO);
        val utpNonBotNormalizerDs
                = uniSessRTJob.normalizerFunctionBuilder(utpNonBotPreFilterDS, UTP_NONBOT, LVS);
        // for roi normalizer
        val roiNonBotNormalizerDs
                = uniSessRTJob.normalizerFunctionBuilder(roiNonBotPreFilterDS, ROI_NONBOT, LVS);

        //Union all three sources into one DataStream
        DataStream<UniEvent> uniDs = surfaceWebNormalizerDs
                .union(surfaceNativeNormalizerDs)
                .union(ubiBotNormalizerDs)
                .union(ubiNonBotNormalizerDs)
                .union(utpNonBotNormalizerDs)
                .union(roiNonBotNormalizerDs);

        //        // prefilter for each source
        //        val rawEventPreFilterDS = uniSessRTJob.preFilterFunctionBuilder(uniDs);
        //
        //        //Normalizer for each source
        //        // surface web
        //        val rawEventNormalizerDs
        //                = uniSessRTJob.normalizerFunctionBuilder(rawEventPreFilterDS);

        // session window
        SingleOutputStreamOperator<UniSession> uniSessionDataStream =
                uniDs.keyBy("guid")
                        .window(RawEventTimeSessionWindows.withGapAndMaxDuration(Time.minutes(30),
                                Time.hours(24)))
                        .trigger(CompositeTrigger.Builder.create()
                                .trigger(EventTimeTrigger.create())
                                .trigger(MidnightOpenSessionTrigger.of(Time.hours(7)))
                                .trigger(CustomTimeDurationSessionTrigger.of(
                                        Time.minutes(getLong(FLINK_APP_SESSION_TIMEDURATION)).toMilliseconds()))
                                .build())
                        .sideOutputLateData(OutputTagConstants.lateEventOutputTag)
                        .aggregate(new UniSessionAgg(), new UniSessionWindowProcessFunction(Time.minutes(getLong(FLINK_APP_SESSION_TIMEDURATION)).toMilliseconds()))
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
                (uniEventDS, EventType.DEFAULT, true);
        DataStream<UniEvent> surfaceWebDS = outputStreamOperator.getSideOutput(atWEBOutputTag);
        DataStream<UniEvent> surfaceNativeDS = outputStreamOperator.getSideOutput(atNATIVEOutputTag);
        DataStream<UniEvent> ubiBotDS = outputStreamOperator.getSideOutput(ubiBOTOutputTag);
        DataStream<UniEvent> ubiNonBotDS = outputStreamOperator.getSideOutput(ubiNONBOTOutputTag);

        // for roi events
        DataStream<UniEvent> roiNonBotDS = outputStreamOperator.getSideOutput(roiNONBOTOutputTag);

        // filter our each kind of event based on late events
        SingleOutputStreamOperator<UniEvent> outputStreamOperatorLate = uniSessRTJob.uniEevntSplitFunctionBuilder
                (latedStream, DEFAULT_LATE, false);
        DataStream<UniEvent> surfaceLateWebDS = outputStreamOperatorLate.getSideOutput(atWEBOutputTagLate);
        DataStream<UniEvent> surfaceLateNativeDS = outputStreamOperatorLate.getSideOutput(atNATIVEOutputTagLate);
        DataStream<UniEvent> ubiBotLateDS = outputStreamOperatorLate.getSideOutput(ubiBOTOutputTagLate);
        DataStream<UniEvent> ubiNonLateBotDS = outputStreamOperatorLate.getSideOutput(ubiNONBOTOutputTagLate);

        // for late roi events
        DataStream<UniEvent> roiNonLateBotDS = outputStreamOperatorLate.getSideOutput(roiNONBOTOutputTagLate);

        // filter our each kind of event based on late events
        SingleOutputStreamOperator<UniSession> outputStreamOperatorSess =
                uniSessRTJob.uniSessionSplitFunctionBuilder(uniSessionDataStream, SESSION_BOT);
        DataStream<UniSession> uniSessionNonbotDS = outputStreamOperatorSess.getSideOutput(uniSessNonbotOutputTag);
        DataStream<UniSession> uniSessionBotDS = outputStreamOperatorSess.getSideOutput(uniSessBotOutputTag);
        // add sessionlkp data
        DataStream<UniSession> uniSessionLkpNonBotDS = outputStreamOperatorSess.getSideOutput(uniSessLkpNonbotOutputTag);
        DataStream<UniSession> uniSessionLkpBotDS = outputStreamOperatorSess.getSideOutput(uniSessLkpBotOutputTag);

        // normal event sink
        uniSessRTJob.kafkaSinkBuilder(surfaceWebDS, AUTOTRACK_WEB, RNO);
        uniSessRTJob.kafkaSinkBuilder(surfaceNativeDS, AUTOTRACK_NATIVE, RNO);
        uniSessRTJob.kafkaSinkBuilder(ubiBotDS, UBI_BOT, RNO);
        uniSessRTJob.kafkaSinkBuilder(ubiNonBotDS, UBI_NONBOT, RNO);

        // for roi event sink
        uniSessRTJob.kafkaSinkBuilder(roiNonBotDS, ROI_NONBOT, RNO);

        // unisession sink
        uniSessRTJob.kafkaSinkBuilder(uniSessionNonbotDS, SESSION_NONBOT, RNO);
        uniSessRTJob.kafkaSinkBuilder(uniSessionLkpNonBotDS, SESSION_LOOKUP_NONBOT, RNO);

        //TODO currently no bot detection impletemented in unified sessionization
        //        uniSessRTJob.kafkaSinkBuilder(uniSessionBotDS, SESSION_BOT, RNO);

        // late event sink
        uniSessRTJob.kafkaSinkBuilder(surfaceLateWebDS, LATE_WEB, RNO );
        uniSessRTJob.kafkaSinkBuilder(surfaceLateNativeDS, LATE_NATIVE, RNO );
        uniSessRTJob.kafkaSinkBuilder(ubiBotLateDS, LATE_UBI_BOT, RNO);
        uniSessRTJob.kafkaSinkBuilder(ubiNonLateBotDS, LATE_UBI_NONBOT, RNO);

        // for late roi event sink
        uniSessRTJob.kafkaSinkBuilder(roiNonLateBotDS, LATE_ROI_NONBOT, RNO);

        //        //Discardsink
        //        // normal event sink
        //        uniSessRTJob.discardSinkBuilder(surfaceWebDS, AUTOTRACK_WEB, RNO);
        //        uniSessRTJob.discardSinkBuilder(surfaceNativeDS, AUTOTRACK_NATIVE, RNO);
        //        uniSessRTJob.discardSinkBuilder(ubiBotDS, UBI_BOT, RNO);
        //        uniSessRTJob.discardSinkBuilder(ubiNonBotDS, UBI_NONBOT, RNO);
        //
        //        // unisession sink
        //        uniSessRTJob.discardSinkBuilder(uniSessionNonbotDS, SESSION_NONBOT, RNO);
        //        //        uniSessRTJob.kafkaSinkBuilder(uniSessionBotDS, SESSION_BOT, RNO);
        //
        //        // late event sink
        //        uniSessRTJob.discardSinkBuilder(surfaceLateWebDS, LATE_WEB, RNO);
        //        uniSessRTJob.discardSinkBuilder(surfaceLateNativeDS, LATE_NATIVE, RNO);
        //        uniSessRTJob.discardSinkBuilder(ubiBotLateDS, LATE_UBI_BOT, RNO);
        //        uniSessRTJob.discardSinkBuilder(ubiNonLateBotDS, LATE_UBI_NONBOT, RNO);

        // Submit this job
        FlinkEnvUtils.execute(see, getString(FLINK_APP_NAME));
    }

}
