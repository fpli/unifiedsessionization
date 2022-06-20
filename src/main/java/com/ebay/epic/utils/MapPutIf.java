package com.ebay.epic.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class MapPutIf {
    public static <T> void putIf(Map<String, T> map, String key, T value, BiPredicate<String, Map<String, T>> testMap, Predicate<T> testValue) {
        if (testMap.test(key, map)) {
            if (testValue.test(value)) {
                map.put(key, value);
            }
        }
    }

    public static <T> void putIfNotNull(Map<String, T> map, String key, T value) {
        MapPutIf.putIf(map, key, value, (x, y) -> true, Objects::nonNull);
    }

    public static void putIfNotBlankStr(Map<String, String> map, String key, String value) {
        MapPutIf.putIf(map, key, value, (x, y) -> true, StringUtils::isNotBlank);
    }

    public static void putIfNumericStr(Map<String, String> map, String key, String value) {
        MapPutIf.putIf(map, key, value, (x, y) -> true, StringUtils::isNumeric);
    }
}
