package com.ebay.epic.soj.business.metric;

import com.ebay.epic.soj.business.metric.clav.*;
import com.ebay.epic.soj.common.enums.EventType;
import com.ebay.epic.soj.common.model.ClavSession;
import com.ebay.epic.soj.common.model.UniSessionAccumulator;
import com.ebay.epic.soj.common.model.raw.UbiKey;
import com.ebay.epic.soj.common.model.raw.UniEvent;
import com.sun.jersey.client.impl.CopyOnWriteHashMap;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Set;
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
            }catch(Exception e){
                log.warn(" Clav session metrics init issue :{}",e);
            }
        }
    }

    public ClavSessionMetrics() {
        initFieldMetrics();
        try {
            init();
        } catch (Exception e) {
            log.warn("Failed to init session metrics", e);
        }
    }
    public void initFieldMetrics() {
        // Clav bot rule, now only handle ubi bot event rule
        addClavSessionFieldMetrics(new BotSessMetrics());
        //Page family cnt
        addClavSessionFieldMetrics(new Gr1CntMetrics());
        addClavSessionFieldMetrics(new GrCntMetrics());
        addClavSessionFieldMetrics(new HomepageCntMetrics());
        addClavSessionFieldMetrics(new MyebayCntMetrics());
        addClavSessionFieldMetrics(new SigninCntMetrics());
        addClavSessionFieldMetrics(new ViCntMetrics());

        addClavSessionFieldMetrics(new ClavTimestampMetrics());
        // page id metrics is dependency on timestamp
        addClavSessionFieldMetrics(new PageIdMetrics());
        addClavSessionFieldMetrics(new ValidPageCountMetrics());
    }

    @Override
    public boolean accept(UniEvent uniEvent) {
        return EventType.UBI_BOT.equals(uniEvent.getEventType())
                || EventType.UBI_NONBOT.equals(uniEvent.getEventType());
    }

    @Override
    public void process(UniEvent uniEvent, UniSessionAccumulator uniSessionAccumulator) throws Exception {
       ClavSession clavSession = getOrDefault(uniSessionAccumulator,uniEvent);
        for (FieldMetrics<UniEvent, ClavSession> metrics : fieldMetrics) {
            try {
                metrics.process(uniEvent, clavSession);
            }catch(Exception e){
                log.warn(" Clav session metric feed issue :{}",e);
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
        UbiKey ubiKey = new UbiKey(uniEvent.getGuid(), uniEvent.getGlobalSessionId(),uniEvent.getSiteId());
        ClavSession clavSession = uniSessionAccumulator.getUniSession().getClavSessionMap().get(ubiKey);
        if (clavSession == null) {
            clavSession = new ClavSession();
            clavSession.setSiteId(Integer.valueOf(uniEvent.getSiteId()));
            clavSession.setSessionId(uniEvent.getSessionId());
            clavSession.setBotFlag(0L);
            clavSession.setOthers(new CopyOnWriteHashMap<>());
            uniSessionAccumulator.getUniSession().getClavSessionMap().put(ubiKey, clavSession);
        }
        return clavSession;
    }
}