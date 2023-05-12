package com.ebay.epic.soj.common.model.trafficsource;

import lombok.Data;

@Data
public class UtpEvent extends TrafficSourceCandidate {
    private String chnl;
    private long rotid;
    private String url;
    private int mpxChnlId;
}
