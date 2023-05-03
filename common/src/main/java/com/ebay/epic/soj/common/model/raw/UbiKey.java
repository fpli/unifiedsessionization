package com.ebay.epic.soj.common.model.raw;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString()
@EqualsAndHashCode
public class UbiKey {
    private String guid;
    private String sessionId;

}
