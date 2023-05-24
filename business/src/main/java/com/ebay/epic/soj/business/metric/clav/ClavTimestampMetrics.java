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
        clavSession.setStartTimestamp(Long.MAX_VALUE);
        clavSession.setExitTimestamp(Long.MIN_VALUE);
        clavSession.setDuration(Long.MIN_VALUE);
    }

    @Override
    public void process(UniEvent uniEvent, ClavSession clavSession) throws Exception {
        System.out.printf("ClavTimestampMetrics Process Start: event is {%s}, clav session is:{%s}%n", uniEvent.toString(), clavSession.toString());
        if (uniEvent.isClavValidPage() && !uniEvent.getIframe()) {
            if (uniEvent.getRdt() == 0 || indicator.isCorrespondingPageEvent(uniEvent.getPageId())) {
                if (uniEvent.getEventTs() != null && clavSession.getStartTimestamp() > uniEvent.getEventTs()) {
                    clavSession.setStartTimestamp(uniEvent.getEventTs());
                }
                if (uniEvent.getEventTs() != null && clavSession.getExitTimestamp() < uniEvent.getEventTs()) {
                    clavSession.setExitTimestamp(uniEvent.getEventTs());
                }
            }
        }
        System.out.printf("ClavTimestampMetrics Process Finish: event is {%s}, clav session is:{%s}%n", uniEvent.toString(), clavSession.toString());
    }

    @Override
    public void end(ClavSession clavSession) throws Exception {
        System.out.printf("ClavTimestampMetrics End Start: clav session is:{%s}%n", clavSession.toString());
        long durationSec =
                (clavSession.getStartTimestamp() == Long.MAX_VALUE || clavSession.getExitTimestamp() == Long.MIN_VALUE)
                        ? 0
                        : ((clavSession.getExitTimestamp() - clavSession.getStartTimestamp()) / 1000000);
        clavSession.setDuration(durationSec);
        System.out.printf("ClavTimestampMetrics End Finish: clav session is:{%s}%n", clavSession.toString());
    }


}
