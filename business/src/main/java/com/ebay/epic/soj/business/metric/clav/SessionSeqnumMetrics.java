package com.ebay.epic.soj.business.metric.clav;

import com.ebay.epic.soj.common.model.ClavSession;
import com.ebay.epic.soj.common.model.raw.UniEvent;

import java.util.HashMap;
import java.util.Map;

public class SessionSeqnumMetrics extends ClavSessionFieldMetrics {

    private static final String MIN_SC_SEQNUM = "min_sc_seqnum";
    private static final String MAX_SC_SEQNUM = "max_sc_seqnum";

    @Override
    public void process(UniEvent uniEvent, ClavSession clavSession) throws Exception {
        String seqnum = uniEvent.getPayload().get("seqnum");
        if (seqnum == null) {
            return;
        }
        Map<String, String> map = clavSession.getOthers();
        if (map == null) {
            map = new HashMap<>();
            clavSession.setOthers(map);
        }
        if (map.containsKey(MIN_SC_SEQNUM)) {
            int cur = Integer.parseInt(map.get(MIN_SC_SEQNUM));
            if (Integer.parseInt(seqnum) < cur) {
                map.put(MIN_SC_SEQNUM, seqnum);
            }
        } else {
            map.put(MIN_SC_SEQNUM, seqnum);
        }
        if (map.containsKey(MAX_SC_SEQNUM)) {
            int cur = Integer.parseInt(map.get(MIN_SC_SEQNUM));
            if (Integer.parseInt(seqnum) > cur) {
                map.put(MAX_SC_SEQNUM, seqnum);
            }
        } else {
            map.put(MAX_SC_SEQNUM, seqnum);
        }
    }

}
