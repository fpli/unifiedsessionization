package com.ebay.epic.flink.function;

import com.ebay.epic.business.filter.EventPreFilter;
import com.ebay.epic.common.model.RawEvent;
import com.ebay.epic.common.model.SojEvent;
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
