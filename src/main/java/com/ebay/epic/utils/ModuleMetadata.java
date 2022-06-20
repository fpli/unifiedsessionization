package com.ebay.epic.utils;

import lombok.Data;

import java.io.Serializable;

/**
 * @author yimikong
 * @date 4/11/22
 */
@Data
public class ModuleMetadata implements Serializable {
    private static final long serialVersionUID = -4323506506682631111L;

    private Integer moduleId;
    private String elementName;
    private String moduleName;
    private String moduleDesc;
}
