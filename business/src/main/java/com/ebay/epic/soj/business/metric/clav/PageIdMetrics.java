package com.ebay.epic.soj.business.metric.clav;

import com.ebay.epic.soj.business.parser.PageIndicator;
import com.ebay.epic.soj.common.model.ClavSession;
import com.ebay.epic.soj.common.model.raw.UniEvent;
import com.ebay.sojourner.common.util.Property;
import com.ebay.sojourner.common.util.SojEventTimeUtil;
import com.ebay.sojourner.common.util.UBIConfig;

public class PageIdMetrics extends ClavSessionFieldMetrics {

    private PageIndicator indicator = null;

    @Override
    public void init() throws Exception {
        indicator = new PageIndicator(UBIConfig.getString(Property.SEARCH_VIEW_PAGES));
    }

    @Override
    public void start(ClavSession clavSession) throws Exception {
        clavSession.setStartPageId(Integer.MIN_VALUE);
        clavSession.setExitPageId(Integer.MIN_VALUE);
    }


    @Override
    public void process(UniEvent event, ClavSession clavSession) throws Exception {
        System.out.printf("PageIdMetrics Process Start: event is {%s}, clav session is:{%s}%n", event.toString(), clavSession.toString());
        if (event.isClavValidPage() && event.getIframe()) {
            if (event.getRdt() == 0 || indicator.isCorrespondingPageEvent(event.getPageId())) {
                boolean isEarlyValidEvent = SojEventTimeUtil.isEarlyEvent(event.getEventTs(), clavSession.getStartTimestamp());
                boolean isLateValidEvent = SojEventTimeUtil.isLateEvent(event.getEventTs(), clavSession.getExitTimestamp());

                if (clavSession.getStartPageId() == Integer.MIN_VALUE || isEarlyValidEvent) {
                    if (event.getPageId() != -1) {
                        clavSession.setStartPageId(event.getPageId());
                    }
                }
                if (isLateValidEvent && event.getPageId() != -1) {
                    clavSession.setExitPageId(event.getPageId());
                }
            }
        }
        System.out.printf("PageIdMetrics Process Finish: event is {%s}, clav session is:{%s}%n", event.toString(), clavSession.toString());
    }

    @Override
    public void end(ClavSession clavSession) throws Exception {
        System.out.printf("PageIdMetrics End Start: clav session is {%s}%n", clavSession.toString());
        if (clavSession.getStartPageId() == Integer.MIN_VALUE) {
            clavSession.setStartPageId(0);
        }
        if (clavSession.getExitPageId() == Integer.MIN_VALUE) {
            clavSession.setExitPageId(0);
        }
        System.out.printf("PageIdMetrics End Finish: clav session is {%s}%n", clavSession.toString());
    }
}
