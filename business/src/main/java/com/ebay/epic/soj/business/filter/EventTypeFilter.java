package com.ebay.epic.soj.business.filter;

import com.ebay.epic.common.enums.EventType;
import com.ebay.epic.common.model.raw.RawEvent;
import com.ebay.epic.common.model.raw.UniEvent;

public class EventTypeFilter extends CombinationFilter<UniEvent> {

    private EventType eventType;
    public EventTypeFilter(EventType eventType){
        this.eventType=eventType;
    }
    @Override
    public boolean filter(UniEvent rawEvent) throws Exception {
         return rawEvent.getEventType().name().equals(this.eventType.name());
    }

}
