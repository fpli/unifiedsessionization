package com.ebay.epic.soj.common.model.trafficsource;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class TrafficSourceDetails {
    private String trafficSourceLevel3;
    private long rotid;
    private int mpxChnlId;
    private String pageName;
    private String referer;

    public Map<String, String> toMap() {
        if (trafficSourceLevel3 == null) {
            return null;
        } else {
            Map<String, String> trafficSourceMap = new HashMap<>();
            trafficSourceMap.put("traffic_source_level3", trafficSourceLevel3);
            if (rotid > 0) {
                trafficSourceMap.put("rotid", String.valueOf(rotid));
            }
            if (mpxChnlId > 0) {
                trafficSourceMap.put("mpx_chnl_id", String.valueOf(mpxChnlId));
            }
            if(pageName != null) {
                trafficSourceMap.put("page_name", pageName);
            }
            if (referer != null) {
                trafficSourceMap.put("referer", referer);
            }
            return trafficSourceMap;
        }
    }
}
