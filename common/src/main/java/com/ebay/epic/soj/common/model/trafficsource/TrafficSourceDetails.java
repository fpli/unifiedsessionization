package com.ebay.epic.soj.common.model.trafficsource;

import lombok.Data;

@Data
public class TrafficSourceDetails {
    private String trafficSourceLevel3;
    private long rotid;
    private int mpxChnlId;
    private String pageName;
    private String referer;
}
