package com.ebay.epic.soj.business.metric.clav;

import com.ebay.epic.soj.common.model.ClavSession;
import com.ebay.epic.soj.common.model.raw.UniEvent;

import java.util.Objects;
import java.util.Optional;

public class CobrandMetrics extends ClavSessionFieldMetrics{

    @Override
    public void process(UniEvent uniEvent, ClavSession clavSession) throws Exception {
        if(uniEvent.isClavValidPage()&&uniEvent.isValid() && Objects.isNull(clavSession.getCobrand())){
           Optional.ofNullable(uniEvent.getCobrand()).ifPresent(t ->clavSession.setCobrand(Integer.valueOf(t)));
        }
    }
}
