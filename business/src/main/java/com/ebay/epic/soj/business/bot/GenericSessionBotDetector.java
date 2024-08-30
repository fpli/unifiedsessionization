package com.ebay.epic.soj.business.bot;

import com.ebay.epic.soj.common.model.raw.RawUniSession;

public class GenericSessionBotDetector implements BotDetector<RawUniSession> {
    @Override
    public Long detectBot(RawUniSession rawUniSession) {
        if (rawUniSession.getUbiBotList().size() > 0) {
            return 1L;
        }
        return 0L;
    }
}
