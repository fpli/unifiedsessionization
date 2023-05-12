package com.ebay.epic.soj.common.model.raw;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UbiKey {
    private String guid;
    private String sessionId;
    private Integer siteId;

}
