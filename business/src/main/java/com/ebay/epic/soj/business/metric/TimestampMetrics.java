package com.ebay.epic.soj.business.metric;

import com.ebay.epic.soj.common.model.UniSessionAccumulator;
import com.ebay.epic.soj.common.model.raw.RawUniSession;
import com.ebay.epic.soj.common.model.raw.UniEvent;
import com.ebay.epic.soj.common.utils.SojEventTimeUtil;

public class TimestampMetrics implements FieldMetrics<UniEvent, UniSessionAccumulator> {

    @Override
    public void start(UniSessionAccumulator uniSessionAccumulator) {
        uniSessionAccumulator.getUniSession().setAbsStartTimestamp(null);
        uniSessionAccumulator.getUniSession().setAbsEndTimestamp(null);
        uniSessionAccumulator.getUniSession().setStartTimestamp(null);
        uniSessionAccumulator.getUniSession().setStartTimestampNOIFRAMERDT(null);
    }

    @Override
    public void process(UniEvent event, UniSessionAccumulator uniSessionAccumulator) throws Exception {
        RawUniSession uniSession = uniSessionAccumulator.getUniSession();
        boolean isEarlyValidEvent = SojEventTimeUtil
                .isEarlyEvent(event.getEventTs(),
                        uniSessionAccumulator.getUniSession().getStartTimestamp());
        if (uniSession.getAbsStartTimestamp() == null) {
            uniSession.setAbsStartTimestamp(event.getEventTs());
        } else if (event.getEventTs() != null
                && uniSession.getAbsStartTimestamp() > event.getEventTs()) {
            uniSession.setAbsStartTimestamp(event.getEventTs());
        }
        if (uniSession.getAbsEndTimestamp() == null) {
            uniSession.setAbsEndTimestamp(event.getEventTs());
        } else if (event.getEventTs() != null
                && uniSession.getAbsEndTimestamp() < event.getEventTs()) {
            uniSession.setAbsEndTimestamp(event.getEventTs());
        }
        if (isEarlyValidEvent && !event.getIframe() && event.getRdt() == 0) {
            uniSession.setStartTimestamp(event.getEventTs());
        }
        if (event.getRdt() == 0 && !event.getIframe()) {
            if (uniSession.getStartTimestampNOIFRAMERDT() == null) {
                uniSession.setStartTimestampNOIFRAMERDT(event.getEventTs());
            } else if (event.getEventTs() != null && uniSession.getStartTimestampNOIFRAMERDT() > event.getEventTs()) {
                uniSession.setStartTimestampNOIFRAMERDT(event.getEventTs());
            }
        }
    }

    @Override
    public void end(UniSessionAccumulator uniSessionAccumulator) {
        uniSessionAccumulator.getUniSession()
                .setSessionStartDt(uniSessionAccumulator.getUniSession()
                        .getAbsStartTimestamp());
    }

}
