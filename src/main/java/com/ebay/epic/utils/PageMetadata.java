package com.ebay.epic.utils;

import lombok.Data;

import java.io.Serializable;

/**
 * @author yimikong
 * @date 4/11/22
 */
@Data
public class PageMetadata implements Serializable {
    private static final long serialVersionUID = -7797724702618578387L;

    private Integer pageId;
    private String pageName;
    private Integer pageGroupId;
    private String pageGroupName;
    private String pageGroupDesc;

}
