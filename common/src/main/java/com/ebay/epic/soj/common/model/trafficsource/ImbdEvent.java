package com.ebay.epic.soj.common.model.trafficsource;

import lombok.Data;

@Data
public class ImbdEvent extends TrafficSourceCandidate {
    private String chnl;
    private String mppid;
}
