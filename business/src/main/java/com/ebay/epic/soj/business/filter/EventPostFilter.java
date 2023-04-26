package com.ebay.epic.soj.business.filter;

import com.ebay.epic.soj.common.enums.Category;
import com.ebay.epic.soj.common.enums.EventType;
import com.ebay.epic.soj.common.model.raw.RawEvent;
import com.ebay.epic.soj.common.model.raw.UniEvent;

public class EventPostFilter extends RecordFilter<UniEvent> {

    private EventType eventType;

    public EventPostFilter(EventType eventType) {
        this.eventType = eventType;
    }

    @Override
    public void initCombinationFilters() {
        addCombinationFilters(new EventTypeFilter(this.eventType));
        addCombinationFilters(new EventCategoryFilter(this.eventType));
    }
}
