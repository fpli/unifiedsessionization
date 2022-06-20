package com.ebay.epic.flink.function;

import com.ebay.epic.common.model.SojEvent;
import com.ebay.epic.common.model.UniTrackingEvent;
import com.ebay.epic.business.normalizer.ubi.ServeEventNormalizer;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.configuration.Configuration;

import java.util.HashMap;

public class TrackingEventNormalizerMapFunction extends RichMapFunction<SojEvent, UniTrackingEvent> {

    private transient ServeEventNormalizer eventNormalizer;

    @Override
    public void open(Configuration configuration) throws Exception {
        super.open(configuration);
        eventNormalizer = new ServeEventNormalizer();
        eventNormalizer.init();
    }

    @Override
    public UniTrackingEvent map(SojEvent event) throws Exception {
        UniTrackingEvent uniTrackingEvent = new UniTrackingEvent();
        uniTrackingEvent.setPayload(new HashMap<>());
        uniTrackingEvent.setScreenDtl(new HashMap<>());
        uniTrackingEvent.setTrafficSrc(new HashMap<>());
        uniTrackingEvent.setBizDtl(new HashMap<>());

        eventNormalizer.normalize(event, uniTrackingEvent);
        return uniTrackingEvent;
    }

    @Override
    public void close() throws Exception {
        super.close();
        eventNormalizer.close();
    }
}
