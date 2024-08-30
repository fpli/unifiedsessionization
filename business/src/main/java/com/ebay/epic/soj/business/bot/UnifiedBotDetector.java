package com.ebay.epic.soj.business.bot;

import com.ebay.epic.soj.common.model.raw.RawUniSession;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class UnifiedBotDetector implements BotDetector<RawUniSession> {

    @Override
    public void init() {
        for (BotDetector<RawUniSession> botDetector : botDetectorSet) {
            try {
                botDetector.init();
            } catch (Exception e) {
                log.warn(" BotDetector init issue :{%s}", e);
            }
        }
    }

    public UnifiedBotDetector() {
        addBotDetectors();
        try {
            init();
        } catch (Exception e) {
            log.warn("Failed to init BotDetector {%s}", e);
        }

    }

    private final Set<BotDetector<RawUniSession>> botDetectorSet = ConcurrentHashMap.newKeySet();

    @Override
    public Long detectBot(RawUniSession rawUniSession) {
        Long botSignature = 0L;
        for (BotDetector<RawUniSession> botDetector : botDetectorSet) {
            try {
                botSignature |= botDetector.detectBot(rawUniSession);
            } catch (Exception e) {
                log.warn(" BotDetector detect issue :{%s}", e);
            }
        }
        return botSignature;
    }

    private void addBotDetector(BotDetector<RawUniSession> botDetector) {
        botDetectorSet.add(botDetector);
    }

    private void addBotDetectors() {
        addBotDetector(new GenericSessionBotDetector());
    }

}
