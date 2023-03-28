package com.ebay.epic.business.filter;

import com.ebay.epic.common.enums.EventType;
import com.ebay.epic.common.model.UniSession;

public class SessionPostFilter extends RecordFilter<UniSession> {

    private EventType eventType;

    public SessionPostFilter(EventType eventType) {
        this.eventType = eventType;
    }

    @Override
    public void initCombinationFilters() {
        addCombinationFilters(new SessionBotFlagFilter(this.eventType));
    }
}
