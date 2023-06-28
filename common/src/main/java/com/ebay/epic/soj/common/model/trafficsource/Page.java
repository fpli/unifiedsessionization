package com.ebay.epic.soj.common.model.trafficsource;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Page {
    private Integer pageId;
    private String pageName;
    private Integer iframe;
}
