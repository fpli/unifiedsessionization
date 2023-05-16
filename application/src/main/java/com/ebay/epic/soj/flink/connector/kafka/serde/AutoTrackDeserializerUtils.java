package com.ebay.epic.soj.flink.connector.kafka.serde;

import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AutoTrackDeserializerUtils {
    public static final List<Pattern> NATIVE_VERSION_PATTERN = Lists.newArrayList(
            Pattern.compile("ebayUserAgent/eBay(Android|IOS);([\\d.]+);.*"),
            Pattern.compile("eBay(iPhone|Android|iPad)/([\\d.]+).*")
    );
    public static final String M_WEB = "mweb";
    public static final String D_WEB = "dweb";
    public static final String WEB_VIEW = "webview";
    public static final String NATIVE = "native";

    public static String getExperience(String ua) {
        for (Pattern pattern : NATIVE_VERSION_PATTERN) {
            Matcher matcher = pattern.matcher(ua);
            if (matcher.matches()) {
                return NATIVE;
            }
        }
        if (StringUtils.containsIgnoreCase(ua, "WebView") || ua.contains("Linux; U; Android")
                || ((ua.contains("iPhone") || ua.contains("iPad") || ua.contains("iPod")) && !ua.contains("Safari"))
                || (ua.contains("Android") && (ua.contains("wv") || ua.contains(".0.0.0")))) {
            return WEB_VIEW;
        } else if (ua.contains("Android") || ua.contains("webOS") || ua.contains("iPhone") || ua.contains("iPad")
                || ua.contains("iPod") || ua.contains("BlackBerry") || ua.contains("Windows Phone")) {
            return M_WEB;
        } else {
            return D_WEB;
        }
    }
}
