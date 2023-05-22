package com.ebay.epic.soj.business.metric.clav;

import com.ebay.epic.soj.common.model.ClavSession;
import com.ebay.epic.soj.common.model.raw.UniEvent;

public class ValidPageCountMetrics extends ClavSessionFieldMetrics {

    @Override
    public void process(UniEvent uniEvent, ClavSession clavSession) throws Exception {
        if (uniEvent.isClavValidPage()) {
            clavSession.setValidPageCount(clavSession.getValidPageCount() + 1);
        }
    }
}
