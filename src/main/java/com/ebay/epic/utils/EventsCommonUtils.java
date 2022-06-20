package com.ebay.epic.utils;

import com.ebay.epic.common.model.avro.GlobalEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author yimikong
 * @date 4/7/22
 */
@Slf4j
public class EventsCommonUtils {
    private static final Pattern MODULE_ID_PATTERN = Pattern.compile(".*(m|mi:)(\\d+).*");
    private static final Pattern CLICK_ID_PATTERN = Pattern.compile(".*(l|li:)(\\d+).*");

    public static String getModuleId(GlobalEvent event) {
        if (event == null || StringUtils.isEmpty(event.getElementId())) {
            return null;
        }
        String entityId = event.getElementId();
        if (entityId.contains("%")) {
            entityId = UrlUtils.fullyDecode(entityId);
        }
        Matcher matcher = MODULE_ID_PATTERN.matcher(entityId);
        if (matcher.matches()) {
            return matcher.group(2);
        }
        return null;
    }

    public static String getClickId(GlobalEvent event) {
        if (event == null || StringUtils.isEmpty(event.getElementId())) {
            return null;
        }
        String entityId = event.getElementId();
        if (entityId.contains("%")) {
            entityId = UrlUtils.fullyDecode(entityId);
        }
        Matcher matcher = CLICK_ID_PATTERN.matcher(entityId);
        if (matcher.matches()) {
            return matcher.group(2);
        }
        return null;
    }
}
