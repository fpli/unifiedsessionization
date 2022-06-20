package com.ebay.epic.utils;

import lombok.var;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public class ExtractTag {
    public static String extract(Map<String, String> map, String tag) {
        var value = map.get(tag);
        if (StringUtils.isNoneEmpty(value)) return value;

        value = map.get("!" + tag);
        if (StringUtils.isNoneEmpty(value)) return value;

        return map.get("!_" + tag);
    }
}
