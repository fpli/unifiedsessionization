package com.ebay.epic.soj.business.bot;

import com.ebay.epic.soj.business.utils.BotruleUtils;
import com.ebay.epic.soj.common.model.raw.RawUniSession;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class GenericSessionBotDetector implements BotDetector<RawUniSession> {

    private static  Map<Integer, Long> signatureMap;

    @Override
    public void init() throws Exception {
        signatureMap = BotRulesConfigLoader.getRuleList("bot.signatures");
    }

    @Override
    public Long detectBot(RawUniSession rawUniSession) {
        if (rawUniSession.getUbiBotList().size() > 0) {
            Set<Long> botSignatures = rawUniSession.getUbiBotList().stream().map(signatureMap::get).collect(Collectors.toSet());
            return botSignatures.stream().reduce(0L, BotruleUtils::setBotFlag);
        }
        return 0L;
    }
}
