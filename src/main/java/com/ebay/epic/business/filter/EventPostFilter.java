package com.ebay.epic.business.filter;

import com.ebay.epic.common.enums.EventType;
import com.ebay.epic.common.model.raw.RawEvent;
import com.ebay.epic.common.model.raw.UniEvent;

public class EventPostFilter extends RecordFilter<UniEvent> {

    private EventType eventType;

    public EventPostFilter(EventType eventType) {
        this.eventType = eventType;
    }

    @Override
    public void initCombinationFilters() {
        addCombinationFilters(new EventTypeFilter(this.eventType));
    }
}
