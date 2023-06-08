package com.ebay.epic.soj.common.model.trafficsource;

import lombok.Data;

@Data
public class ValidSurfaceEvent extends TrafficSourceCandidate {
    private int pageId;
    private String pageName;
    private String referer;
    private String url;
}
