package com.ebay.epic.soj.flink.function;

import com.ebay.epic.soj.business.filter.EventPreFilter;
import com.ebay.epic.soj.common.model.raw.RawEvent;
import org.apache.flink.api.common.functions.RichFilterFunction;
import org.apache.flink.configuration.Configuration;

public class TrackingEventPreFilterFunction extends RichFilterFunction<RawEvent> {

    private transient EventPreFilter eventFilter;

    @Override
    public void open(Configuration configuration) throws Exception {
        super.open(configuration);
        eventFilter = new EventPreFilter();
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
