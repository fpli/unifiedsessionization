package com.ebay.epic.soj.business.metric;

import com.ebay.epic.soj.business.metric.clav.*;
import com.ebay.epic.soj.common.enums.EventType;
import com.ebay.epic.soj.common.model.ClavSession;
import com.ebay.epic.soj.common.model.UniSessionAccumulator;
import com.ebay.epic.soj.common.model.raw.UbiKey;
import com.ebay.epic.soj.common.model.raw.UniEvent;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Slf4j
public class ClavSessionMetrics implements FieldMetrics<UniEvent, UniSessionAccumulator> {
    protected Set<FieldMetrics<UniEvent, ClavSession>> fieldMetrics
            = new CopyOnWriteArraySet<>();

    @Override
    public void init() throws Exception {
        for (FieldMetrics<UniEvent, ClavSession> metrics : fieldMetrics) {
            try {
                metrics.init();
            } catch (Exception e) {
                log.warn(" Clav session metrics init issue :{%s}", e);
            }
        }
    }

    public ClavSessionMetrics() {
        initFieldMetrics();
        try {
            init();
        } catch (Exception e) {
            log.warn("Failed to init session metrics {%s}", e);
        }
    }

    public void initFieldMetrics() {
        // Clav bot rule, now only handle ubi bot event rule
        addClavSessionFieldMetrics(new LegacySessMetrics());
        //Page family cnt
        addClavSessionFieldMetrics(new Gr1CntMetrics());
        addClavSessionFieldMetrics(new GrCntMetrics());
        addClavSessionFieldMetrics(new HomepageCntMetrics());
        addClavSessionFieldMetrics(new MyebayCntMetrics());
        addClavSessionFieldMetrics(new SigninCntMetrics());
        addClavSessionFieldMetrics(new ViCntMetrics());
        // page id metrics is dependency on timestamp, move before timestamp metrics
        addClavSessionFieldMetrics(new PageIdMetrics());
        addClavSessionFieldMetrics(new ClavTimestampMetrics());
        addClavSessionFieldMetrics(new ValidPageCntMetrics());
        addClavSessionFieldMetrics(new CguidAndOldExperienceMetrics());
        addClavSessionFieldMetrics(new SessionSeqnumMetrics());
        addClavSessionFieldMetrics(new BestGuessUserIdMetrics());
        addClavSessionFieldMetrics(new CobrandMetrics());

        // experience and device
        addClavSessionFieldMetrics(new ClavAgentStringMetrics());
        addClavSessionFieldMetrics(new ExperienceAndDeviceMetrics());
    }

    @Override
    public boolean accept(UniEvent uniEvent) {
        return EventType.UBI_BOT.equals(uniEvent.getEventType())
                || EventType.UBI_NONBOT.equals(uniEvent.getEventType());
    }

    @Override
    public void process(UniEvent uniEvent, UniSessionAccumulator uniSessionAccumulator) throws Exception {
        try {
            ClavSession clavSession = getOrDefault(uniSessionAccumulator, uniEvent);

            for (FieldMetrics<UniEvent, ClavSession> metrics : fieldMetrics) {
                try {
                    metrics.process(uniEvent, clavSession);
                } catch (Exception e) {
                    log.warn(" Clav session metric feed issue :{%s}", e);
                }
            }
        } catch(Exception e){
            log.error("Clav session metrics process failed", e);
            e.printStackTrace();
        }
    }

    @Override
    public void end(UniSessionAccumulator uniSessionAccumulator) throws Exception {
        Collection<ClavSession> clavSessions = uniSessionAccumulator.getUniSession().getClavSessionMap().values();
        for (ClavSession clavSession : clavSessions) {
            for (FieldMetrics<UniEvent, ClavSession> metrics : fieldMetrics) {
                try {
                    metrics.end(clavSession);
                } catch (Exception e) {
                    log.warn(" Clav session metric end issue :{%s}", e);
                }
            }
        }
    }

    public void addClavSessionFieldMetrics(FieldMetrics<UniEvent, ClavSession> metrics) {
        // TODO: this line is always true sine FieldMetrics does not override equals()
        if (!fieldMetrics.contains(metrics)) {
            fieldMetrics.add(metrics);
        } else {
            throw new RuntimeException("Duplicate Metrics!");
        }
    }

    public ClavSession getOrDefault(UniSessionAccumulator uniSessionAccumulator, UniEvent uniEvent) {
        UbiKey ubiKey = new UbiKey(uniEvent.getGuid(), uniSessionAccumulator.getUniSession().getGlobalSessionId(), uniEvent.getSiteId());
        ClavSession clavSession = uniSessionAccumulator.getUniSession().getClavSessionMap().get(ubiKey);
        if (clavSession == null) {
            clavSession = new ClavSession();
            clavSession.setSiteId(Integer.valueOf(uniEvent.getSiteId()));
            clavSession.setSessionId(uniSessionAccumulator.getUniSession().getGlobalSessionId());
            clavSession.setBotFlag(Optional.ofNullable(uniSessionAccumulator.getUniSession().getBotSignature()).orElse(0L));
            clavSession.setOthers(new ConcurrentHashMap<>());
            clavSession.setSessionSkey(uniSessionAccumulator.getUniSession().getAbsStartTimestamp());
            uniSessionAccumulator.getUniSession().getClavSessionMap().put(ubiKey, clavSession);
        } else {
            Optional.ofNullable(uniSessionAccumulator.getUniSession().getAbsStartTimestamp())
                    .ifPresent(clavSession::setSessionSkey);
            Optional.ofNullable(uniSessionAccumulator.getUniSession().getBotSignature())
                    .ifPresent(clavSession::setBotFlag);
        }
        return clavSession;
    }
}