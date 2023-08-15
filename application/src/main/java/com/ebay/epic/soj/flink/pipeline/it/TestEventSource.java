package com.ebay.epic.soj.flink.pipeline.it;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.flink.streaming.api.functions.source.SourceFunction;

import java.util.concurrent.TimeUnit;

public class TestEventSource implements SourceFunction<TestEvent> {
    @Override
    public void run(SourceContext<TestEvent> ctx) throws Exception {
        while (true) {
            String generatedString = RandomStringUtils.randomAlphabetic(10);
            ctx.collect(new TestEvent(generatedString));
            TimeUnit.SECONDS.sleep(1);
        }
    }

    @Override
    public void cancel() {
        // nothing to do
    }
}
