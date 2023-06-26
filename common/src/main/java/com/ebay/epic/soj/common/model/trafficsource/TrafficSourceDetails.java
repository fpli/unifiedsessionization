package com.ebay.epic.soj.common.model.trafficsource;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Data
@Slf4j
public class TrafficSourceDetails {
    private String trafficSourceLevel3;
    private Long rotid = 0L;
    private Integer mpxChnlId = 0;
    private Integer ldngPageId = 0;
    private String referer;
    private String mppid;

    public Map<String, String> toMap() {
        Map<String, String> trafficSourceMap = new HashMap<>();
        try {
            if (trafficSourceLevel3 == null) {
                return null;
            } else {
                trafficSourceMap.put("traffic_source_level3", trafficSourceLevel3);
                if (rotid != null && rotid > 0) {
                    trafficSourceMap.put("rotid", String.valueOf(rotid));
                }
                if (mpxChnlId != null && mpxChnlId > 0) {
                    trafficSourceMap.put("mpx_chnl_id", String.valueOf(mpxChnlId));
                }
                if(ldngPageId != null && ldngPageId > 0) {
                    trafficSourceMap.put("ldng_page_id", String.valueOf(ldngPageId));
                }
                if (referer != null) {
                    trafficSourceMap.put("referer", referer);
                }
                if (mppid != null) {
                    trafficSourceMap.put("mppid", mppid);
                }
            }
        } catch (Exception e) {
            log.warn("failed to convert TrafficSourceDetails to Map", e);
        }
        return trafficSourceMap;
    }
}
