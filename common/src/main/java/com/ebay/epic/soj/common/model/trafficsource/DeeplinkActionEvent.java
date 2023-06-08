package com.ebay.epic.soj.common.model.trafficsource;

import lombok.Data;

@Data
public class DeeplinkActionEvent extends TrafficSourceCandidate {
    private String referer;
}
