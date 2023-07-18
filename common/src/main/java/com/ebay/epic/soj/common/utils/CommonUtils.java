package com.ebay.epic.soj.common.utils;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;

public class CommonUtils {
    public static final ObjectMapper objectMapper = new ObjectMapper();

    public static boolean isMeaningFullString(String str) {
        if (StringUtils.isBlank(str)) {
            return false;
        }
        return !StringUtils.equalsIgnoreCase("null", str.trim());
    }

    public static String numberToString(Number number) {
        if (number == null) {
            return null;
        }
        return String.valueOf(number);
    }

    public static Integer safeParseInteger(String str) {
        if (StringUtils.isBlank(str)) {
            return null;
        }
        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
            return null;
        }
    }
}
