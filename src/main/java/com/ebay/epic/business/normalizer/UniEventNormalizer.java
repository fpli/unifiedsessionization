package com.ebay.epic.business.normalizer;

import com.ebay.epic.common.model.raw.RawEvent;
import com.ebay.epic.common.model.raw.UniEvent;

public class UniEventNormalizer extends RecordNormalizer<RawEvent, UniEvent> {

    @Override
    public void initFieldNormalizers() {
        addFieldNormalizer(new CommonNormalizer());
        addFieldNormalizer(new UtpTrafficSourceNormalizer());
        addFieldNormalizer(new SessionIdsNormalizer());
    }
}
