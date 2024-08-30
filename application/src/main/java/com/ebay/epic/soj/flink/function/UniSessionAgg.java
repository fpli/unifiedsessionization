package com.ebay.epic.soj.flink.function;

import com.ebay.epic.soj.business.bot.UnifiedBotDetector;
import com.ebay.epic.soj.business.metric.UniSessionMetrics;
import com.ebay.epic.soj.common.model.UniSessionAccumulator;
import com.ebay.epic.soj.common.model.raw.UniEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.functions.AggregateFunction;

@Slf4j
public class UniSessionAgg implements AggregateFunction<UniEvent, UniSessionAccumulator, UniSessionAccumulator> {

    private static final UnifiedBotDetector unifiedBotDetector = new UnifiedBotDetector();
    @Override
    public UniSessionAccumulator createAccumulator() {
        UniSessionAccumulator sessionAccumulator = new UniSessionAccumulator();
        try {
            UniSessionMetrics.getInstance().start(sessionAccumulator);
        } catch (Exception e) {
            log.error("init session metrics failed", e);
        }
        return sessionAccumulator;
    }

    @Override
    public UniSessionAccumulator add(UniEvent value, UniSessionAccumulator accumulator) {
        try {
            UniSessionMetrics.getInstance().feed(value, accumulator);
        } catch (Exception e) {
            log.error("start session metrics collection failed", e);
        }
        try {
            Long botSignature = unifiedBotDetector.detectBot(accumulator.getUniSession());
            accumulator.getUniSession().setBotSignature(botSignature);
        } catch (Exception e) {
            log.error("detect bot failed", e);
        }
        return accumulator;
    }

    @Override
    public UniSessionAccumulator getResult(UniSessionAccumulator sessionAccumulator) {
        return sessionAccumulator;
    }

    @Override
    public UniSessionAccumulator merge(UniSessionAccumulator a, UniSessionAccumulator b) {
        log.info("session accumulator merge");
        a.setUniSession(a.getUniSession().merge(b.getUniSession()));
        return a;
    }
}
