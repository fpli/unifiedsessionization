package com.ebay.epic.soj.business.metric;

import com.ebay.epic.soj.common.model.raw.RawEvent;
import com.ebay.epic.soj.common.model.raw.RawUniSession;
import com.ebay.epic.soj.common.model.UniSessionAccumulator;
import com.ebay.epic.soj.common.model.raw.UniEvent;

public class GlobalSessionIdMetrics implements FieldMetrics<UniEvent, UniSessionAccumulator> {

    @Override
    public void start(UniSessionAccumulator uniSessionAccumulator) {
        uniSessionAccumulator.getUniSession().setGlobalSessionId(null);
    }

    @Override
    public void process(UniEvent event, UniSessionAccumulator uniSessionAccumulator) throws Exception {
        RawUniSession uniSession = uniSessionAccumulator.getUniSession();
        if (!event.isNewSession() && uniSession.getGlobalSessionId() == null) {
            uniSession.setGlobalSessionId(event.getGlobalSessionId());
        } else if (event.isNewSession() && uniSession.getGlobalSessionId() != null) {
            event.setGlobalSessionId(uniSession.getGlobalSessionId());
        } else if (event.isNewSession() && uniSession.getGlobalSessionId() == null) {
            event.updateGlobalSessionId();
            uniSession.setGlobalSessionId(event.getGlobalSessionId());
        }
    }
}
