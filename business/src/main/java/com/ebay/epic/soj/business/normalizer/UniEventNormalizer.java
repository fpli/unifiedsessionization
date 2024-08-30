package com.ebay.epic.soj.business.normalizer;

import com.ebay.epic.soj.common.model.raw.RawEvent;
import com.ebay.epic.soj.common.model.raw.UniEvent;

public class UniEventNormalizer extends RecordNormalizer<RawEvent, UniEvent> {

    @Override
    public void initFieldNormalizers() {
        addFieldNormalizer(new CommonNormalizer());
        addFieldNormalizer(new UtpTrafficSourceNormalizer());
        addFieldNormalizer(new SessionIdsNormalizer());
//        addFieldNormalizer(new ClavValidPageNormalizer());
        addFieldNormalizer(new PartialValidPageNormalizer());
        addFieldNormalizer(new TrafficSourceNormalizer());
    }

}
