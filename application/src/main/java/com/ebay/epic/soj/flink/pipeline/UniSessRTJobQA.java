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

import static com.ebay.epic.soj.common.enums.DataCenter.*;
import static com.ebay.epic.soj.common.enums.EventType.SESSION_BOT;
import static com.ebay.epic.soj.flink.constant.OutputTagConstants.*;
import static com.ebay.epic.soj.common.enums.DataCenter.*;
import static com.ebay.epic.soj.common.enums.EventType.*;
import static com.ebay.epic.soj.flink.utils.FlinkEnvUtils.*;
import static com.ebay.epic.soj.common.utils.Property.*;

public class UniSessRTJobQA extends FlinkBaseJob {


    public static void main(String[] args) throws Exception {

        final StreamExecutionEnvironment see = streamExecutionEnvironmentBuilder4Local(args);
        see.setParallelism(8);
        see.disableOperatorChaining();
        UniSessRTJobQA uniSessRTJob = new UniSessRTJobQA();

        // consumer
        DataStream<RawEvent> ubi = uniSessRTJob.consumerBuilder(see, EventType.UBI_NONBOT, SLC);

        // Filter logic before normalizer
        val rawEventPreFilterDS =
                uniSessRTJob.preFilterFunctionBuilder(ubi,EventType.UBI_NONBOT,SLC);

        //normalizer
        val rawEventNormalizerDs
                = uniSessRTJob.normalizerFunctionBuilder(rawEventPreFilterDS,EventType.UBI_NONBOT,SLC);
        // session window
        SingleOutputStreamOperator<UniSession> uniSessionDataStream =
                rawEventNormalizerDs
                        .keyBy("guid")
                        .window(RawEventTimeSessionWindows.withGapAndMaxDuration(Time.minutes(30),
                                Time.hours(24)))
                        .trigger(CompositeTrigger.Builder.create().trigger(EventTimeTrigger.create())
                                .trigger(MidnightOpenSessionTrigger
                                        .of(Time.hours(7))).build())
                        .sideOutputLateData(OutputTagConstants.lateEventOutputTag)
                        .aggregate(new UniSessionAgg(),
                                new UniSessionWindowProcessFunction(
                                        Time.minutes(getLong(FLINK_APP_SESSION_TIMEDURATION)).toMilliseconds()))
                        .setParallelism(getInteger(Property.SESSION_PARALLELISM))
                        .slotSharingGroup(getString(SESSION_WINDOR_SLOT_SHARE_GROUP))
                        .name(getString(SESSION_WINDOR_OPERATOR_NAME))
                        .uid(getString(SESSION_WINDOR_UID))
                        .setMaxParallelism(getInteger(PARALLELISM_MAX_SESSION));

        WindowOperatorHelper.enrichWindowOperator(
                (OneInputTransformation<UniEvent, UniSession>) uniSessionDataStream.getTransformation(),
                new RawEventMapWithStateFunction(),
                OutputTagConstants.mappedEventOutputTag);

        DataStream<UniEvent> rawEventWithSessionId =
                uniSessionDataStream.getSideOutput(OutputTagConstants.mappedEventOutputTag);
        DataStream<UniEvent> latedStream =
                uniSessionDataStream.getSideOutput(OutputTagConstants.lateEventOutputTag);

//        DataStream<UniEvent> surfaceDS = uniSessRTJob.postFilterFunctionBuilder(rawEventWithSessionId, EventType.AUTOTRACK, RNO);
        SingleOutputStreamOperator<UniEvent> outputStreamOperator  = uniSessRTJob.uniEevntSplitFunctionBuilder
                (rawEventWithSessionId, EventType.DEFAULT,true);
        DataStream<UniEvent> ubiBotDS = outputStreamOperator.getSideOutput(ubiBOTOutputTag);
        DataStream<UniEvent> ubiNonBotDS = outputStreamOperator.getSideOutput(ubiNONBOTOutputTag);
//        DataStream<UniEvent> utpDS = uniSessRTJob.postFilterFunctionBuilder(rawEventWithSessionId, EventType.UTP, RNO);
//        ubiBotDS.print().uid("testevent").slotSharingGroup("local").setParallelism(1);
//        ubiNonBotDS.print().uid("testeventbot").slotSharingGroup("local").setParallelism(1);

        // filter our each kind of event based on late events
        SingleOutputStreamOperator<UniSession> outputStreamOperatorSess =
                uniSessRTJob.uniSessionSplitFunctionBuilder(uniSessionDataStream, SESSION_BOT);
        DataStream<UniSession> uniSessionNonbotDS = outputStreamOperatorSess.getSideOutput(uniSessNonbotOutputTag);
        DataStream<UniSession> uniSessionBotDS = outputStreamOperatorSess.getSideOutput(uniSessBotOutputTag);
        uniSessionNonbotDS.print().uid("testsess").slotSharingGroup("local").setParallelism(1);
//        uniSessRTJob.kafkaSinkBuilder(surfaceDS, EventType.AUTOTRACK, RNO);
//        uniSessRTJob.kafkaSinkBuilder(ubiDS, EventType.UBI, RNO);
//        uniSessRTJob.kafkaSinkBuilder(utpDS, EventType.UTP, RNO);
//        uniSessRTJob.kafkaSinkBuilder(uniSessionDS, EventType.SESSION, RNO);

        // Submit this job
        FlinkEnvUtils.execute(see, getString(FLINK_APP_NAME));
    }

}
