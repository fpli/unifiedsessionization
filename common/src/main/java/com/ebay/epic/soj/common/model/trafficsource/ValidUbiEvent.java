package com.ebay.epic.soj.common.model.trafficsource;

import lombok.Data;

@Data
public class ValidUbiEvent extends TrafficSourceCandidate {
    private Integer pageId;
    private String pageName;
    private String referer;
    private String url;
}
