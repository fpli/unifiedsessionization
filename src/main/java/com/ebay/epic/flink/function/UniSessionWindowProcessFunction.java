package com.ebay.epic.flink.function;

import com.ebay.epic.business.metric.UniSessionMetrics;
import com.ebay.epic.common.model.UniSession;
import com.ebay.epic.common.model.UniSessionAccumulator;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeutils.base.LongSerializer;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

public class UniSessionWindowProcessFunction
        extends ProcessWindowFunction<UniSessionAccumulator, UniSession, Tuple, TimeWindow> {

    private static final ValueStateDescriptor<Long> lastTimestampStateDescriptor =
            new ValueStateDescriptor("lastTimestamp", LongSerializer.INSTANCE);

    private void outputSession(UniSession ubiSessionTmp,
                               Collector<UniSession> out, boolean isOpen) {
        UniSession uniSession = new UniSession();
        uniSession.setGuid(ubiSessionTmp.getGuid());
        uniSession.setGlobalSessionId(ubiSessionTmp.getGlobalSessionId());
        uniSession.setAbsStartTimestamp(ubiSessionTmp.getAbsStartTimestamp());
        uniSession.setAbsEndTimestamp(ubiSessionTmp.getAbsEndTimestamp());
        ubiSessionTmp.setSessionStartDt(ubiSessionTmp.getSessionStartDt());
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
