package com.ebay.epic.flink.function;

import com.ebay.epic.business.normalizer.RecordNormalizer;
import com.ebay.epic.business.normalizer.UniEventNormalizer;
import com.ebay.epic.common.model.raw.RawEvent;
import com.ebay.epic.common.model.raw.UniEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.configuration.Configuration;

@Slf4j
public class TrackingEventMapFunction extends RichMapFunction<RawEvent, UniEvent> {
    private RecordNormalizer recordNormalizer;
    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        recordNormalizer =new UniEventNormalizer();
        recordNormalizer.init();
    }

    @Override
    public UniEvent map(RawEvent rawEvent) throws Exception {
        UniEvent uniEvent = new UniEvent();
        recordNormalizer.normalize(rawEvent,uniEvent);
        return uniEvent;
    }

    @Override
    public void close() throws Exception {
        super.close();
        recordNormalizer.close();
        recordNormalizer=null;
    }
}
