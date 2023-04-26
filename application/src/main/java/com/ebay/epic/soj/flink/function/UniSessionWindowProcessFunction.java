package com.ebay.epic.soj.flink.function;

import com.ebay.epic.business.metric.UniSessionMetrics;
import com.ebay.epic.common.model.BotFlag;
import com.ebay.epic.common.model.RheosHeader;
import com.ebay.epic.common.model.UniSession;
import com.ebay.epic.common.model.raw.RawUniSession;
import com.ebay.epic.common.model.UniSessionAccumulator;
import com.google.common.collect.Lists;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeutils.base.LongSerializer;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.util.Arrays;

public class UniSessionWindowProcessFunction
        extends ProcessWindowFunction<UniSessionAccumulator, UniSession, Tuple, TimeWindow> {

    private static final ValueStateDescriptor<Long> lastTimestampStateDescriptor =
            new ValueStateDescriptor("lastTimestamp", LongSerializer.INSTANCE);

    private void outputSession(RawUniSession rawUniSession,
                               Collector<UniSession> out, boolean isOpen) {
        UniSession uniSession = new UniSession();
        uniSession.setGuid(rawUniSession.getGuid());
        uniSession.setGlobalSessionId(rawUniSession.getGlobalSessionId());
        uniSession.setAbsStartTimestamp(rawUniSession.getAbsStartTimestamp());
        uniSession.setAbsEndTimestamp(rawUniSession.getAbsEndTimestamp());
        uniSession.setSessionStartDt(rawUniSession.getSessionStartDt());
        uniSession.setUbiSessIds(Lists.newArrayList(rawUniSession.getUbiSessIds()));
        uniSession.setUbiSessSkeys(Lists.newArrayList(rawUniSession.getUbiSessSkeys()));
        uniSession.setAutotrackSessSkeys(Lists.newArrayList(rawUniSession.getAutotrackSessSkeys()));
        uniSession.setAutotrackSessIds(Lists.newArrayList(rawUniSession.getAutotrackSessIds()));
        uniSession.setTrafficSource(rawUniSession.getTrafficSource().name());
        uniSession.setOthers(rawUniSession.getOthers());
        uniSession.setIsOpen(isOpen);
        RheosHeader rheosHeader = new RheosHeader();
        rheosHeader.setEventId("DummyID");
        rheosHeader.setEventCreateTimestamp(System.currentTimeMillis());
        rheosHeader.setEventSentTimestamp(System.currentTimeMillis());
        rheosHeader.setProducerId("DummyID");
        rheosHeader.setSchemaId(-999);
        uniSession.setRheosHeader(rheosHeader);
        BotFlag botFlag = new BotFlag();
        botFlag.setSurface(Lists.newArrayList(rawUniSession.getSurfaceBotList()));
        botFlag.setUbi(Lists.newArrayList(rawUniSession.getUbiBotList()));
        botFlag.setUtp(Lists.newArrayList(rawUniSession.getSutpBotList()));
        uniSession.setBotFlag(botFlag);
        out.collect(uniSession);
    }

    @Override
    public void process(
            Tuple tuple,
            Context context,
            Iterable<UniSessionAccumulator> elements,
            Collector<UniSession> out)
            throws Exception {
        UniSessionAccumulator sessionAccumulator = elements.iterator().next();
        endSessionEvent(sessionAccumulator);
        boolean isOpen = context.currentWatermark() < context.window().maxTimestamp();
        outputSession(sessionAccumulator.getUniSession(), out, isOpen);
    }

    private void endSessionEvent(UniSessionAccumulator sessionAccumulator) throws Exception {
        UniSessionMetrics.getInstance().end(sessionAccumulator);
    }

    @Override
    public void open(Configuration conf) throws Exception {
        super.open(conf);
    }

    @Override
    public void clear(Context context) throws Exception {
        context.globalState().getState(lastTimestampStateDescriptor).clear();
    }
}
