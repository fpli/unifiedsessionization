package com.ebay.epic.soj.business.metric.clav;

import com.ebay.epic.soj.common.model.ClavSession;
import com.ebay.epic.soj.common.model.lookup.PageFamilyInfo;
import com.ebay.epic.soj.common.model.raw.UniEvent;
import com.ebay.epic.soj.common.model.trafficsource.TrafficSourceLookupManager;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Gr1CntMetrics extends ClavSessionFieldMetrics {

    public static final String GR_1 = "GR-1";
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
                && (pageFamilyInfo != null && GR_1.equals(pageFamilyInfo.getPageFamily4()))) {
            clavSession.setGr1Count(clavSession.getGr1Count() + 1);
        }
    }

}
