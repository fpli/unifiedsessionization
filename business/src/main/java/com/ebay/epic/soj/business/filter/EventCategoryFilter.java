package com.ebay.epic.soj.business.filter;

import com.ebay.epic.common.enums.EventType;
import com.ebay.epic.common.model.raw.UniEvent;

public class EventCategoryFilter extends CombinationFilter<UniEvent> {

    private EventType eventType;

    public EventCategoryFilter(EventType eventType) {
        this.eventType = eventType;
    }

    @Override
    public boolean filter(UniEvent rawEvent) throws Exception {
        return rawEvent.getEventType()==this.eventType;
    }

}
