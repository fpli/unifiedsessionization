package com.ebay.epic.utils;

import org.apache.commons.lang3.StringUtils;

public class SojUtils {

    public static String extractNVP(String payloadValue, String PayloadKey, String keyDelimiter, String valueDelimiter) {
        if (payloadValue == null || PayloadKey == null) {
            return null;
        }
        String payloadV = payloadValue;
        String tagValue = getTagValue(payloadV, PayloadKey, keyDelimiter, valueDelimiter);
        if (tagValue == null) {
            return null;
        } else return tagValue;
    }

    public static String getTagValue(String value, String key, String keyDelimiter, String valueDelimiter) {
        if (StringUtils.isNotBlank(value) && StringUtils.isNotBlank(key)) {
            int kLen = key.length();
            int kDelimiterLen = keyDelimiter.length();
            int vDelimiterLen = valueDelimiter.length();
            if (value.startsWith(key + valueDelimiter)) {
                String searchKey = key + valueDelimiter;
                int pos = value.indexOf(keyDelimiter, searchKey.length());
                if (pos >= 0) {
                    return value.substring(searchKey.length(), pos);
                } else {
                    return value.substring(searchKey.length());
                }
            } else {
                String searchKey = keyDelimiter + key + valueDelimiter;
                int l = kLen + kDelimiterLen + vDelimiterLen;
                int startPos = value.indexOf(searchKey);
                if (startPos >= 0) {
                    if (value.length() > l + startPos) {
                        int endPos = value.indexOf(keyDelimiter, l + startPos);
                        if (endPos >= 0) {
                            return value.substring(l + startPos, endPos);
                        } else {
                            return value.substring(l + startPos);
                        }
                    } else {
                        return null;
                    }
                } else {
                    return null;
                }
            }
        } else {
            return null;
        }
    }

    public static String base36dDecode(String input) {
        if (StringUtils.isEmpty(input)) {
            return null;
        }
        StringBuilder decoded = new StringBuilder();
        if (input.contains(",")) {
            String[] numArray = input.split(",");
            for (String num : numArray) {
                if (decoded.length() > 0)
                    decoded.append(",");
                try {
                    decoded.append(Long.parseLong(num, 36));
                } catch (Exception e) {
                    return null;
                }
            }
        } else {
            try {
                decoded.append(Long.parseLong(input, 36));
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return decoded.toString();
    }
}
