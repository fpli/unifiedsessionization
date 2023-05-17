package com.ebay.epic.soj.common.model.trafficsource;

import lombok.Data;

@Data
public class UtpEvent extends TrafficSourceCandidate {
    private int chnl;
    private long rotid;
    private String url;
    private int mpxChnlId;
    private int pageId;
}
