package com.ebay.epic.soj.common.model.trafficsource;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ImbdEvent extends TrafficSourceCandidate {
    private String mppid;
}
