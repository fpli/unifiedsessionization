package com.ebay.epic.soj.common.model.trafficsource;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UtpEvent extends TrafficSourceCandidate {
    private Integer chnl;
    private Long rotId;
    private String url;
    private Integer mpxChnlId;
    private Integer pageId;
    private String ntype;
}
