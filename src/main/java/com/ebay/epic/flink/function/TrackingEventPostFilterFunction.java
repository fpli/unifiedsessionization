package com.ebay.epic.flink.function;

import com.ebay.epic.common.enums.EventType;
import com.ebay.epic.common.model.raw.RawEvent;
import com.ebay.epic.business.filter.EventPostFilter;
import org.apache.flink.api.common.functions.RichFilterFunction;
import org.apache.flink.configuration.Configuration;

public class TrackingEventPostFilterFunction extends RichFilterFunction<RawEvent> {

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
    public boolean filter(RawEvent t) throws Exception {
        return eventFilter.filter(t);
    }

    @Override
    public void close() throws Exception {
        super.close();
        eventFilter.close();
    }
}
