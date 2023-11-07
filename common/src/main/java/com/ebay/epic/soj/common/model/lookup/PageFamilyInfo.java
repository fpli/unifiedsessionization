package com.ebay.epic.soj.common.model.lookup;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageFamilyInfo {
    private Integer pageId;
    private String pageFamily1;
    private String pageFamily2;
    private String pageFamily3;
    private String pageFamily4;
    private Integer iframe;
}
