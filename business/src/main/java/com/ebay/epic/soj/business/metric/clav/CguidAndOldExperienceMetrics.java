package com.ebay.epic.soj.business.metric.clav;

import com.ebay.epic.soj.common.model.ClavSession;
import com.ebay.epic.soj.common.model.raw.UniEvent;

public class CguidAndOldExperienceMetrics extends ClavSessionFieldMetrics {

    private final String CGUID = "cguid";

    @Override
    public void process(UniEvent uniEvent, ClavSession clavSession) throws Exception {
        if (clavSession.getOthers() != null && !clavSession.getOthers().containsKey(CGUID)) {
            String cguid = uniEvent.getPayload().get("n");
            if (cguid != null && cguid.length() == 32) {
                clavSession.getOthers().put(this.CGUID, cguid);
            }
        }
    }


}
