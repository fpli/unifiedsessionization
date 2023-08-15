package com.ebay.epic.soj.flink.pipeline.it;

import com.ebay.epic.soj.common.model.trafficsource.TrafficSourceConstants;
import com.ebay.epic.soj.common.model.trafficsource.TrafficSourceLookupManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;

@Slf4j
public class TestProcessFunction extends ProcessFunction<TestEvent, TestEvent> {

    @Override
    public void open(Configuration parameters) throws Exception {
        TrafficSourceLookupManager lookupManager = TrafficSourceLookupManager.getInstance();
    }

    @Override
    public void processElement(
            TestEvent event,
            ProcessFunction<TestEvent, TestEvent>.Context ctx,
            Collector<TestEvent> out) throws Exception {
        if (TrafficSourceLookupManager.getInstance().getPageMap()
                .get(TrafficSourceConstants.CHOCOLATE_PAGE).getIframe() != 1) {
            throw new RuntimeException("page not correct");
        }
        if (TrafficSourceLookupManager.getInstance().getDwMpxRotationMap()
                .get(215791617356762562L).getMpxChnlId() != 2) {
            throw new RuntimeException("rotation not correct");
        }
        out.collect(event);
    }
}
