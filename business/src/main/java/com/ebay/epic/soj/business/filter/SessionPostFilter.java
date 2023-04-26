package com.ebay.epic.soj.business.filter;

import com.ebay.epic.soj.common.enums.EventType;
import com.ebay.epic.soj.common.model.UniSession;

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
