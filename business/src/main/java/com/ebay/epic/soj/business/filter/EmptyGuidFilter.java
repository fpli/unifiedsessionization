package com.ebay.epic.soj.business.filter;

import com.ebay.epic.soj.common.model.raw.RawEvent;

public class EmptyGuidFilter extends CombinationFilter<RawEvent> {
    @Override
    public boolean filter(RawEvent rawEvent) throws Exception {
        return rawEvent.getGuid() != null;
    }
}
