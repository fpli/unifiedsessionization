package com.ebay.epic.soj.business.metric.clav;

import com.ebay.epic.soj.common.model.ClavSession;
import com.ebay.epic.soj.common.model.lookup.PageFamilyInfo;
import com.ebay.epic.soj.common.model.raw.UniEvent;
import com.ebay.epic.soj.common.model.trafficsource.TrafficSourceLookupManager;

public class HomepageCntMetrics extends ClavSessionFieldMetrics {

    public static final String HOME = "HOME";
    private TrafficSourceLookupManager lookupManager;

    @Override
    public void init() throws Exception {
        lookupManager = TrafficSourceLookupManager.getInstance();
    }

    @Override
    public void process(UniEvent event, ClavSession clavSession) throws Exception {
        Integer pageId = event.getPageId();
        PageFamilyInfo pageFamilyInfo = lookupManager.getPageFamilyAllMap().get(pageId);
        if (event.getRdt() == 0
                && event.isPartialValidPage()
                && pageFamilyInfo != null
                && HOME.equals(pageFamilyInfo.getPageFamily4())) {
            clavSession.setHomepageCount(clavSession.getHomepageCount() + 1);
        }
    }
}
