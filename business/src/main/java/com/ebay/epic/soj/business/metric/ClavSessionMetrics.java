package com.ebay.epic.soj.business.metric;

import com.ebay.epic.soj.business.metric.clav.Gr1CntMetrics;
import com.ebay.epic.soj.business.metric.clav.GrCntMetrics;
import com.ebay.epic.soj.common.enums.EventType;
import com.ebay.epic.soj.common.model.ClavSession;
import com.ebay.epic.soj.common.model.UniSessionAccumulator;
import com.ebay.epic.soj.common.model.raw.UbiKey;
import com.ebay.epic.soj.common.model.raw.UniEvent;
import lombok.extern.slf4j.Slf4j;

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
        addClavSessionFieldMetrics(new Gr1CntMetrics());
        addClavSessionFieldMetrics(new GrCntMetrics());
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
        UbiKey ubiKey = new UbiKey(uniEvent.getGuid(), uniEvent.getGlobalSessionId());
        ClavSession clavSession = uniSessionAccumulator.getUniSession().getClavSessionMap().get(ubiKey);
        if (clavSession == null) {
            clavSession = new ClavSession();
            uniSessionAccumulator.getUniSession().getClavSessionMap().put(ubiKey, clavSession);
        }
        return clavSession;
    }
}