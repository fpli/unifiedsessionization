package com.ebay.epic.utils;

import lombok.Data;

import java.io.Serializable;

/**
 * @author yimikong
 * @date 4/11/22
 */
@Data
public class ClickMetadata implements Serializable {
    private static final long serialVersionUID = 917600611976211258L;

    private Integer clickId;
    private String clickName;
    private String clickDesc;

}
