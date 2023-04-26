package com.ebay.epic.soj.flink.function;

import com.ebay.epic.common.model.RheosHeader;
import com.ebay.epic.common.model.UniSession;
import com.ebay.epic.common.model.raw.RawUniSession;
import com.google.common.collect.Lists;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;

public class RawUniSessionToUniSessionProcessFunction extends ProcessFunction<RawUniSession, UniSession> {
    @Override
    public void processElement(RawUniSession rawUniSession, Context context, Collector<UniSession> out) {
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
        RheosHeader rheosHeader = new RheosHeader();
        rheosHeader.setEventId("DummyID");
        rheosHeader.setEventCreateTimestamp(System.currentTimeMillis());
        rheosHeader.setEventSentTimestamp(System.currentTimeMillis());
        rheosHeader.setProducerId("DummyID");
        rheosHeader.setSchemaId(-999);
        uniSession.setRheosHeader(rheosHeader);
        out.collect(uniSession);
    }
}
