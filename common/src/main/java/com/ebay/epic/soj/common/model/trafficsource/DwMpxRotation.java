package com.ebay.epic.soj.common.model.trafficsource;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DwMpxRotation {
    private long rotationId;
    private int mpxChnlId;
}
