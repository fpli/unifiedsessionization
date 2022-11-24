package com.ebay.epic.flink.function;

import com.ebay.epic.business.metric.UniSessionMetrics;
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

public class UniSessionWindowProcessFunction
        extends ProcessWindowFunction<UniSessionAccumulator, RawUniSession, Tuple, TimeWindow> {

    private static final ValueStateDescriptor<Long> lastTimestampStateDescriptor =
            new ValueStateDescriptor("lastTimestamp", LongSerializer.INSTANCE);

    private void outputSession(RawUniSession ubiSessionTmp,
                               Collector<RawUniSession> out, boolean isOpen) {
        RawUniSession uniSession = new RawUniSession();
        uniSession.setGuid(ubiSessionTmp.getGuid());
        uniSession.setGlobalSessionId(ubiSessionTmp.getGlobalSessionId());
        uniSession.setAbsStartTimestamp(ubiSessionTmp.getAbsStartTimestamp());
        uniSession.setAbsEndTimestamp(ubiSessionTmp.getAbsEndTimestamp());
        uniSession.setSessionStartDt(ubiSessionTmp.getSessionStartDt());
        uniSession.setUbiSessIds(ubiSessionTmp.getUbiSessIds());
        uniSession.setUbiSessSkeys(ubiSessionTmp.getUbiSessSkeys());
        uniSession.setAutotrackSessSkeys(ubiSessionTmp.getAutotrackSessSkeys());
        uniSession.setAutotrackSessIds(ubiSessionTmp.getAutotrackSessIds());
        uniSession.setTrafficSource(ubiSessionTmp.getTrafficSource());
        uniSession.setOthers(ubiSessionTmp.getOthers());
        out.collect(uniSession);
    }

    @Override
    public void process(
            Tuple tuple,
            Context context,
            Iterable<UniSessionAccumulator> elements,
            Collector<RawUniSession> out)
            throws Exception {
        UniSessionAccumulator sessionAccumulator = elements.iterator().next();
        endSessionEvent(sessionAccumulator);
        if (context.currentWatermark() >= context.window().maxTimestamp()) {
            outputSession(sessionAccumulator.getUniSession(), out, false);
        } else {
            outputSession(sessionAccumulator.getUniSession(), out, true);
        }
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
