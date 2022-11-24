package com.ebay.epic.business.filter;

import com.ebay.epic.common.model.raw.RawEvent;

public class EventPreFilter extends RecordFilter<RawEvent> {
    @Override
    public void initCombinationFilters() {
        addCombinationFilters(new EmptyGuidFilter());
//        addCombinationFilters(new SamplingFilter());
    }
}
