package com.ebay.epic.soj.common.model.trafficsource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class DwMpxRotation {
    private Long rotationId;
    private Integer mpxChnlId;
}
