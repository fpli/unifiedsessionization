package com.ebay.epic.business.filter;

import com.ebay.epic.common.enums.EventType;
import com.ebay.epic.common.model.raw.RawEvent;

public class EventTypeFilter extends CombinationFilter<RawEvent> {

    private EventType eventType;
    public EventTypeFilter(EventType eventType){
        this.eventType=eventType;
    }
    @Override
    public boolean filter(RawEvent rawEvent) throws Exception {
         return rawEvent.getEventType().name().equals(this.eventType.name());
    }

}
