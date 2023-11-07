package com.ebay.epic.soj.common.model.trafficsource;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Data
@Slf4j
public class TrafficSourceDetails {
    private String mngdTrafficSourceLevel1;
    private String custTrafficSourceLevel1;
    private String custTrafficSourceLevel2;
    private String trafficSourceLevel3;
    private Long rotid = 0L;
    private Integer mpxChnlId = 0;
    private Integer ldngPageId = 0;
    private String referer;
    private String mppid;
    private String ntype;

    public Map<String, String> toMap() {
        if (trafficSourceLevel3 == null) {
            return null;
        }

        Map<String, String> trafficSourceMap = new HashMap<>();
        try {
            trafficSourceMap.put("mngd_traffic_source_level1", mngdTrafficSourceLevel1);
            trafficSourceMap.put("cust_traffic_source_level1", custTrafficSourceLevel1);
            trafficSourceMap.put("cust_traffic_source_level2", custTrafficSourceLevel2);
            trafficSourceMap.put("traffic_source_level3", trafficSourceLevel3);
            if (rotid != null && rotid > 0) {
                trafficSourceMap.put("rotid", String.valueOf(rotid));
            }
            if (mpxChnlId != null && mpxChnlId > 0) {
                trafficSourceMap.put("mpx_chnl_id", String.valueOf(mpxChnlId));
            }
            if (ldngPageId != null && ldngPageId > 0) {
                trafficSourceMap.put("ldng_page_id", String.valueOf(ldngPageId));
            }
            if (referer != null) {
                trafficSourceMap.put("referer", referer);
            }
            if (mppid != null) {
                trafficSourceMap.put("mppid", mppid);
            }
            if (ntype != null) {
                trafficSourceMap.put("ntype", ntype);
            }
        } catch (Exception e) {
            log.warn("failed to convert TrafficSourceDetails to Map", e);
        }
        return trafficSourceMap;
    }
}
