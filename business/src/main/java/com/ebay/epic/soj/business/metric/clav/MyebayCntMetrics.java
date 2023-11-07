package com.ebay.epic.soj.business.metric.clav;

import com.ebay.epic.soj.common.model.ClavSession;
import com.ebay.epic.soj.common.model.lookup.PageFamilyInfo;
import com.ebay.epic.soj.common.model.raw.UniEvent;
import com.ebay.epic.soj.common.model.trafficsource.TrafficSourceLookupManager;
import com.google.common.collect.ImmutableSet;

public class MyebayCntMetrics extends ClavSessionFieldMetrics {

    public static final ImmutableSet<String> myEbayIndicator = ImmutableSet.of("MYEBAY", "SM", "SMP");
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
                && (pageFamilyInfo != null && myEbayIndicator.contains(pageFamilyInfo.getPageFamily4()))) {
            clavSession.setMyebayCount(clavSession.getMyebayCount() + 1);
        }
    }

}
