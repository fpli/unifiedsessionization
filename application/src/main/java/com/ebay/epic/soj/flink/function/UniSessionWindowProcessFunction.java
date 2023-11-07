package com.ebay.epic.soj.flink.function;

import com.ebay.epic.soj.business.metric.UniSessionMetrics;
import com.ebay.epic.soj.common.model.BotFlag;
import com.ebay.epic.soj.common.model.RheosHeader;
import com.ebay.epic.soj.common.model.UniSession;
import com.ebay.epic.soj.common.model.UniSessionAccumulator;
import com.ebay.epic.soj.common.model.raw.RawUniSession;
import com.ebay.epic.soj.common.utils.SojTimestamp;
import com.google.common.collect.Lists;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.api.common.typeutils.base.LongSerializer;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import org.apache.flink.util.StringUtils;

import java.sql.Date;
import java.util.ArrayList;

public class UniSessionWindowProcessFunction
        extends ProcessWindowFunction<UniSessionAccumulator, UniSession, Tuple, TimeWindow> {

    private final long timeDuration;
    private static final ValueStateDescriptor<Long> lastTimestampStateDescriptor =
            new ValueStateDescriptor("lastTimestamp", LongSerializer.INSTANCE);

    private TrafficSourceOutputProcessor trafficSourceOutputProcessor =
            new TrafficSourceOutputProcessor();

    public UniSessionWindowProcessFunction(long timeDuration) {
        this.timeDuration = timeDuration;
    }

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
        uniSession.setTrafficSourceDetails(rawUniSession.getTrafficSourceDtl());
        uniSession.setOthers(rawUniSession.getOthers());
        uniSession.setIsOpen(isOpen);
        uniSession.setUserId(rawUniSession.getUserId());
        uniSession.setFirstAppId(rawUniSession.getFirstAppId());
        uniSession.setCobrand(rawUniSession.getCobrand());
        uniSession.setUserAgent(rawUniSession.getUserAgent());
        uniSession.setClavSessions(new ArrayList<>(rawUniSession.getClavSessionMap().values()));
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
        trafficSourceOutputProcessor.output(rawUniSession, uniSession);
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
        boolean isOpen = isOpenSess(context);
        if(isSessLookup(context)) {
            sessionAccumulator.getUniSession().getOthers().put("issesslkp", "1");
            sessionAccumulator.getUniSession().getOthers().put("start", String.valueOf(context.window().getStart()));
            sessionAccumulator.getUniSession().getOthers().put("max", String.valueOf(context.window().maxTimestamp()));
            sessionAccumulator.getUniSession().getOthers().put("watermark", String.valueOf(context.currentWatermark()));
        }
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

    private boolean isSessLookup( Context context) {

        if (context.window().getStart() + this.timeDuration <= context.currentWatermark()
                && context.currentWatermark() < context.window().maxTimestamp()) {
            return true;
        }
        return false;
    }

    private boolean isOpenSess(Context context) {

        String startDateStr = SojTimestamp.getDateStrWithUnixTimestamp(context.window().getStart());
        String waterMarkDateStr = SojTimestamp.getDateStrWithUnixTimestamp(context.currentWatermark());
        String maxTimestampStr = SojTimestamp.getDateStrWithUnixTimestamp(context.window().maxTimestamp());
        String startminus1DateStr = SojTimestamp.getDateStrWithUnixTimestamp(context.window().getStart() - 1);
        if (context.currentWatermark() < context.window().maxTimestamp()
                && ((!startDateStr.equals(waterMarkDateStr) && waterMarkDateStr.equals(maxTimestampStr))
                || (!startminus1DateStr.equals(waterMarkDateStr) && waterMarkDateStr.equals(maxTimestampStr)))) {
            return true;
        }
        return false;
    }
}
