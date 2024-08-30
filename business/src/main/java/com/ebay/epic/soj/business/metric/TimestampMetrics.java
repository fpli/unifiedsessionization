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
        uniSessionAccumulator.getUniSession().setEndTimestamp(null);
    }

    @Override
    public void process(UniEvent event, UniSessionAccumulator uniSessionAccumulator) throws Exception {
        RawUniSession uniSession = uniSessionAccumulator.getUniSession();
        boolean isEarlyValidEvent = SojEventTimeUtil
                .isEarlyEvent(event.getEventTs(),
                        uniSession.getStartTimestamp());
        boolean isLateValidEvent = SojEventTimeUtil
                .isEarlyEvent(event.getEventTs(),
                        uniSession.getEndTimestamp());
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
        if (isEarlyValidEvent && event.isValid()) {
            uniSession.setStartTimestamp(event.getEventTs());
        }
        if (event.isValid()) {
            if (uniSession.getStartTimestampNOIFRAMERDT() == null) {
                uniSession.setStartTimestampNOIFRAMERDT(event.getEventTs());
            } else if (event.getEventTs() != null && uniSession.getStartTimestampNOIFRAMERDT() > event.getEventTs()) {
                uniSession.setStartTimestampNOIFRAMERDT(event.getEventTs());
            }
        }
        // end timestamp logic
        if (isLateValidEvent && event.isValid()) {
            uniSession.setEndTimestamp(event.getEventTs());
        }
    }

    @Override
    public void end(UniSessionAccumulator uniSessionAccumulator) {
        uniSessionAccumulator.getUniSession()
                .setSessionStartDt(uniSessionAccumulator.getUniSession()
                        .getAbsStartTimestamp());
    }

}
