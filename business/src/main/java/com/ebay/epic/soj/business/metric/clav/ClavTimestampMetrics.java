package com.ebay.epic.soj.business.metric.clav;

import com.ebay.epic.soj.business.parser.PageIndicator;
import com.ebay.epic.soj.common.model.ClavSession;
import com.ebay.epic.soj.common.model.raw.UniEvent;
import com.ebay.sojourner.common.util.*;

public class ClavTimestampMetrics extends ClavSessionFieldMetrics {

    private PageIndicator indicator = null;

    @Override
    public void init() throws Exception {
        indicator = new PageIndicator(UBIConfig.getString(Property.SEARCH_VIEW_PAGES));
    }

    @Override
    public void start(ClavSession clavSession) throws Exception {
        clavSession.setStartTimestamp(null);
        clavSession.setExitTimestamp(null);
        clavSession.setDuration(null);
    }

    @Override
    public void process(UniEvent uniEvent, ClavSession clavSession) throws Exception {
        if (uniEvent.isClavValidPage() && !uniEvent.getIframe()) {
            if (uniEvent.getRdt() == 0 || indicator.isCorrespondingPageEvent(uniEvent.getPageId())) {
                if (clavSession.getStartTimestamp() == null) {
                    clavSession.setStartTimestamp(uniEvent.getEventTs());
                } else if (uniEvent.getEventTs() != null && clavSession.getStartTimestamp() > uniEvent.getEventTs()) {
                    clavSession.setStartTimestamp(uniEvent.getEventTs());
                }
                if (clavSession.getExitTimestamp() == null) {
                    clavSession.setExitTimestamp(uniEvent.getEventTs());
                } else if (uniEvent.getEventTs() != null
                        && clavSession.getExitTimestamp() < uniEvent.getEventTs()) {
                    clavSession.setExitTimestamp(uniEvent.getEventTs());
                }
            }

        }
    }

    @Override
    public void end(ClavSession clavSession) throws Exception {
        long durationSec =
                (clavSession.getStartTimestamp() == null
                        || clavSession.getExitTimestamp() == null)
                        ? 0
                        :
                        ((clavSession.getExitTimestamp()
                                - clavSession.getStartTimestamp())
                                / 1000000);
        clavSession.setDuration(durationSec);
    }


}
