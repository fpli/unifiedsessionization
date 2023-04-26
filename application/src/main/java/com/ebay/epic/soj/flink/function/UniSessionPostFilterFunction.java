package com.ebay.epic.soj.flink.function;

import com.ebay.epic.soj.business.filter.SessionPostFilter;
import com.ebay.epic.soj.common.enums.Category;
import com.ebay.epic.soj.common.enums.EventType;
import com.ebay.epic.soj.common.model.UniSession;
import org.apache.flink.api.common.functions.RichFilterFunction;
import org.apache.flink.configuration.Configuration;

public class UniSessionPostFilterFunction extends RichFilterFunction<UniSession> {

    private transient SessionPostFilter sessionPostFilter;

    private EventType eventType;

    public UniSessionPostFilterFunction(EventType eventType) {
        this.eventType = eventType;
    }

    @Override
    public void open(Configuration configuration) throws Exception {
        super.open(configuration);
        sessionPostFilter = new SessionPostFilter(this.eventType);
        sessionPostFilter.init();
    }

    @Override
    public boolean filter(UniSession t) throws Exception {
        return sessionPostFilter.filter(t);
    }

    @Override
    public void close() throws Exception {
        super.close();
        sessionPostFilter.close();
    }
}
