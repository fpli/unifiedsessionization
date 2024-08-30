package com.ebay.epic.soj.business.metric.clav;

import com.ebay.epic.soj.common.model.ClavSession;
import com.ebay.epic.soj.common.model.raw.UniEvent;
import com.ebay.sojourner.common.util.SojEventTimeUtil;

import java.util.Objects;

public class ClavTimestampMetrics extends ClavSessionFieldMetrics {

    @Override
    public void start(ClavSession clavSession) throws Exception {
        clavSession.setStartTimestamp(null);
        clavSession.setExitTimestamp(null);
        clavSession.setAbsStartTimestamp(null);
        clavSession.setAbsEndTimestamp(null);
    }

    @Override
    public void process(UniEvent uniEvent, ClavSession clavSession) throws Exception {
        if (uniEvent.isClavValidPage()) {
            boolean isEarlyValidEvent = SojEventTimeUtil.isEarlyEvent(uniEvent.getEventTs(), clavSession.getStartTimestamp());
            boolean isLateValidEvent = SojEventTimeUtil.isLateEvent(uniEvent.getEventTs(), clavSession.getExitTimestamp());
            if (clavSession.getStartTimestamp() == 0 || isEarlyValidEvent) {
                clavSession.setStartTimestamp(uniEvent.getEventTs());
            }
            if (isLateValidEvent) {
                clavSession.setExitTimestamp(uniEvent.getEventTs());
            }
        }
        boolean isEarlyEvent = SojEventTimeUtil.isEarlyEvent(uniEvent.getEventTs(), clavSession.getAbsStartTimestamp());
        boolean isLateEvent = SojEventTimeUtil.isLateEvent(uniEvent.getEventTs(), clavSession.getAbsEndTimestamp());
        if(isEarlyEvent && Objects.nonNull(uniEvent.getEventTs())){
            clavSession.setAbsStartTimestamp(uniEvent.getEventTs());
        }
        if(isLateEvent && Objects.nonNull(uniEvent.getEventTs())){
            clavSession.setAbsEndTimestamp(uniEvent.getEventTs());
        }
    }

    @Override
    public void end(ClavSession clavSession) throws Exception {
        long duration =
                (clavSession.getStartTimestamp() == 0 || clavSession.getExitTimestamp() == 0)
                        ? 0
                        : clavSession.getExitTimestamp() - clavSession.getStartTimestamp();
        clavSession.setDuration(duration);
        if(Objects.nonNull(clavSession.getStartTimestamp())){
            clavSession.setSessionStartDt(clavSession.getStartTimestamp());
        }else{
            clavSession.setSessionStartDt(clavSession.getAbsStartTimestamp());
        }
    }


}
