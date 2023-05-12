package com.ebay.epic.soj.business.metric.trafficsource;

import com.ebay.epic.soj.common.model.raw.UniEvent;
import com.ebay.epic.soj.common.model.trafficsource.TrafficSourceCandidate;
import com.ebay.epic.soj.common.model.trafficsource.TrafficSourceCandidates;
import com.ebay.epic.soj.common.model.trafficsource.TrafficSourceDetails;
import com.ebay.epic.soj.common.model.trafficsource.TrafficSourceLookupManager;

public class TrafficSourceDetector {
    public TrafficSourceCandidate extractCandidate(UniEvent uniEvent) {
        TrafficSourceLookupManager lookupManager = TrafficSourceLookupManager.getInstance();
//        lookupManager.getDwMpxRotationMap().get(1).getMpxChnlId();
//        lookupManager.getPageMap().get(1).getIframe();
        return null;
    }

    public TrafficSourceDetails determineTrafficSource(
            TrafficSourceCandidates trafficSourceCandidates) {
        return null;
    }

}
