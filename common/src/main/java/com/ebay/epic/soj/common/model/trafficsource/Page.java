package com.ebay.epic.soj.common.model.trafficsource;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Page {
    private int pageId;
    private String pageName;
    private int iframe;
}
