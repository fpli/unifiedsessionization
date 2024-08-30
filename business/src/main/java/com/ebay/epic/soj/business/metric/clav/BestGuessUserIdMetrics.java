package com.ebay.epic.soj.business.metric.clav;

import com.ebay.epic.soj.common.model.ClavSession;
import com.ebay.epic.soj.common.model.raw.UniEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BestGuessUserIdMetrics extends ClavSessionFieldMetrics {

    private static final String BU = "bu";

    @Override
    public void process(UniEvent uniEvent, ClavSession clavSession) throws Exception {
        Map<String, String> map = clavSession.getOthers();
        if (map == null) {
            map = new ConcurrentHashMap<>();
            clavSession.setOthers(map);
        }
        if (uniEvent.getPayload().containsKey(BU) && !map.containsKey(BU)) {
            // one guid, skey, site_id can have multiple bu, only put the first one
            map.put(BU, uniEvent.getPayload().get(BU));
        }
    }

}
