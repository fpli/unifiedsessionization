package com.ebay.epic.soj.common.model.trafficsource;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class TrafficSourceDetails {
    private String trafficSourceLevel3;
    private Long rotid;
    private Integer mpxChnlId;
    private Integer ldngPageId;
    private String referer;
    private String mppid;

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
            if(ldngPageId > 0) {
                trafficSourceMap.put("ldng_page_id", String.valueOf(ldngPageId));
            }
            if (referer != null) {
                trafficSourceMap.put("referer", referer);
            }
            if (mppid != null) {
                trafficSourceMap.put("mppid", mppid);
            }
            return trafficSourceMap;
        }
    }
}
