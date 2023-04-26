package com.ebay.epic.soj.business.filter;

import com.ebay.epic.common.enums.EventType;
import com.ebay.epic.common.model.UniSession;

public class SessionBotFlagFilter extends CombinationFilter<UniSession> {

    private EventType eventType;
    public SessionBotFlagFilter(EventType eventType){
        this.eventType=eventType;
    }
    @Override
    public boolean filter(UniSession uniSession) throws Exception {
        // TODO need to do the enhancement for bot filter
         return this.eventType.getBotType().equals("nonbot");
    }

}
