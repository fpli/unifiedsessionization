package com.ebay.epic.soj.common.model.trafficsource;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ValidUbiEvent extends TrafficSourceCandidate {
    private Integer pageId;
    private String pageName;
    private String referer;
    private String url;
}
