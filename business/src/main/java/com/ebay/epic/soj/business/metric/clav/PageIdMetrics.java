package com.ebay.epic.soj.business.metric.clav;

import com.ebay.epic.soj.common.model.ClavSession;
import com.ebay.epic.soj.common.model.raw.UniEvent;
import com.ebay.sojourner.common.util.SojEventTimeUtil;

public class PageIdMetrics extends ClavSessionFieldMetrics {

    @Override
    public void process(UniEvent event, ClavSession clavSession) throws Exception {
        if (event.isClavValidPage()) {
            boolean isEarlyValidEvent = SojEventTimeUtil.isEarlyEvent(event.getEventTs(), clavSession.getStartTimestamp());
            boolean isLateValidEvent = SojEventTimeUtil.isLateEvent(event.getEventTs(), clavSession.getExitTimestamp());

            if (clavSession.getStartPageId() == 0 || isEarlyValidEvent) {
                if (event.getPageId() != -1) {
                    clavSession.setStartPageId(event.getPageId());
                }
            }
            if (isLateValidEvent && event.getPageId() != -1) {
                clavSession.setExitPageId(event.getPageId());
            }
        }
    }

}
