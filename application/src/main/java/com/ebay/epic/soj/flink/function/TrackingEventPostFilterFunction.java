package com.ebay.epic.soj.flink.function;

import com.ebay.epic.soj.business.filter.EventPostFilter;
import com.ebay.epic.soj.common.enums.EventType;
import com.ebay.epic.soj.common.model.raw.UniEvent;
import org.apache.flink.api.common.functions.RichFilterFunction;
import org.apache.flink.configuration.Configuration;

public class TrackingEventPostFilterFunction extends RichFilterFunction<UniEvent> {

    private transient EventPostFilter eventFilter;

    private EventType eventType;

    public TrackingEventPostFilterFunction(EventType eventType) {
        this.eventType = eventType;
    }

    @Override
    public void open(Configuration configuration) throws Exception {
        super.open(configuration);
        eventFilter = new EventPostFilter(this.eventType);
        eventFilter.init();
    }

    @Override
    public boolean filter(UniEvent t) throws Exception {
        return eventFilter.filter(t);
    }

    @Override
    public void close() throws Exception {
        super.close();
        eventFilter.close();
    }
}
