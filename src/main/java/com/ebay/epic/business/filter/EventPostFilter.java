package com.ebay.epic.business.filter;

import com.ebay.epic.common.enums.EventType;
import com.ebay.epic.common.model.RawEvent;

public class EventPostFilter extends RecordFilter<RawEvent> {

    private EventType eventType;

    public EventPostFilter(EventType eventType) {
        this.eventType = eventType;
    }

    @Override
    public void initCombinationFilters() {
        addCombinationFilters(new EventTypeFilter(this.eventType));
    }
}
