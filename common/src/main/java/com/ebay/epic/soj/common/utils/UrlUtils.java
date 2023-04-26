package com.ebay.epic.soj.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * xiaoding
 */
@Slf4j
public class UrlUtils {
    private static final Integer MAX_DECODE_TRIES = 20;
    private static final Pattern END_WITH_HTML_REGEX = Pattern.compile("^(.*)\\w+\\.html$");
    private static final Pattern ITEM_PATH_REGEX = Pattern.compile("^.*/(\\d+)/?$");

    public static String[] splitPaths(String urlStr) {

        if (urlStr == null) {
            return null;
        }
        try {
            URL url = new URL(urlStr);
            String paths = url.getPath();

            if (paths == null) {
                return null;
            }
            return Arrays.stream(paths.split("/")).filter(StringUtils::isNotBlank).toArray(String[]::new);
        } catch (Exception e) {
            log.debug("failed to parse url");
        }
        return null;
    }

    public static String getFirstPath(String urlStr) {
        String[] paths = splitPaths(urlStr);
        if (paths == null || paths.length == 0) {
            return null;
        }
        return paths[0];
    }

    public static String removeParams(String urlStr) {
        if (urlStr == null || urlStr.length() == 0) {
            return "";
        }
        try {
            URL url = new URL(urlStr);

            String path = url.getPath();
            if (path == null) {
                return url.getHost();
            }
            return url.getHost() + path;
        } catch (Exception e) {
            log.debug("failed to get first path");
        }
        return "";
    }

    public static String getFirstDomain(String url) {
        String domain = getDomain(url);
        String[] domains = domain.split("\\.");
        int length = domains.length;
        if (length == 1) {
            return domains[0];
        }
        return String.join(".", domains[length - 2], domains[length - 1]);
    }

    //FROM SOJ Libs
    public static String getDomain(String url) {
        if (url == null) {
            return "";
        }

        //check :
        int posStart = url.indexOf(":");
        if (posStart < 0) {
            return "";
        }
        //verify // after :
        if (url.length() < posStart + 3
                || !"//".equals(url.substring(posStart + 1, posStart + 3))) {
            return "";
        } else {
            posStart += 2;
        }

        //check uid
        int posUID = url.indexOf("@", posStart + 1);
        //get position of /
        int posPath = url.indexOf("/", posStart + 1);


        //@ is not separator of uid/pwd
        if (posUID >= 0 && posPath >= 0 && posPath < posUID) {
            posUID = -1;
        }

        if (posUID >= 0) {
            posStart = posUID;
        }
        if (posPath < 0) {
            posPath = url.length();
        }

        // check port
        int posEnd = url.indexOf(":", posStart + 1);
        if (posEnd >= 0 && posEnd < posPath) {
            posPath = posEnd;
        }
        String temp = url.substring(posStart + 1, posPath);
        return temp.substring(0, lastAlphaNumber(temp) + 1);
    }

    public static int lastAlphaNumber(String str) {
        return lastAlphaNumber(str, str.length() - 1, 0);
    }

    public static int lastAlphaNumber(String str, int startPos, int endPos) {
        for (int i = startPos; i >= endPos; i--) {
            if (Character.isAlphabetic(str.charAt(i))) {
                return i;
            }
        }
        return -1;
    }

    public static Map<String, String> splitQuery(URL url) {
        return splitQuery(url.getQuery());
    }

    public static String getParamValue(String query, String paramKey) {
        if (StringUtils.isBlank(query) || StringUtils.isBlank(paramKey)) {
            return null;
        }
        return splitQuery(query).get(paramKey.toLowerCase(Locale.ROOT));
    }

    public static Map<String, String> splitQuery(String query) {
        Map<String, String> queryPairs = new HashMap<>();
        if (StringUtils.isBlank(query)) {
            return queryPairs;
        }
        int idx = query.indexOf("?");
        query = idx > 0 && query.length() > idx + 1 ? query.substring(idx + 1) : query;
        final String[] pairs = query.split("&");
        for (String pair : pairs) {
            idx = pair.indexOf("=");
            String key = idx > 0 ? fullyDecode(pair.substring(0, idx)) : pair;
            String value = idx > 0 && pair.length() > idx + 1 ? fullyDecode(pair.substring(idx + 1)) : null;
            queryPairs.put(key.toLowerCase(Locale.ROOT), value);
        }
        return queryPairs;
    }

    public static String decode(String encoded) {
        try {
            return URLDecoder.decode(encoded, "UTF-8");
        } catch (Exception e) {
            return encoded;
        }
    }

    public static String fullyDecode(String encoded) {
        try {
            String decode = URLDecoder.decode(encoded, "UTF-8");
            for (int i = 0; i < MAX_DECODE_TRIES && !Objects.equals(encoded, decode); i++) {
                encoded = decode;
                decode = URLDecoder.decode(encoded, "UTF-8");
            }
            return decode;
        } catch (Exception e) {
            return encoded;
        }
    }

    public static Long getItemId(String urlStr) {
        if (urlStr == null) {
            return null;
        }
        try {
            URL url = new URL(urlStr);
            String path = url.getPath();
            if (path == null) {
                return null;
            }
            Matcher matcher = ITEM_PATH_REGEX.matcher(path);
            String itemId = null;
            if (matcher.matches()) {
                return Long.parseLong(matcher.group(1));
            } else if ((itemId = UrlUtils.getParamValue(urlStr, "itemId")) != null) {
                return Long.parseLong(itemId);
            }
        } catch (Exception e) {
            log.debug("failed to get first path");
        }
        return null;
    }

    public static String cleanParamAndHtml(String url) {
        String cleaned = removeParams(url);
        Matcher matcher = END_WITH_HTML_REGEX.matcher(cleaned);
        if (matcher.matches()) {
            return matcher.group(1);
        }
        return cleaned;
    }

    public static String extraFromModuleIds(String moduleIds, String key) {
        if (moduleIds == null || key == null) {
            return null;
        }
        moduleIds = fullyDecode(moduleIds);
        String[] kvs = moduleIds.split("\\|");
        for (String kv : kvs) {
            String[] kvArray = kv.split(":");
            if (kvArray.length != 2) {
                continue;
            }
            String k = kvArray[0];
            if (k.contains("=")) {
                k = k.split("=")[1];
            }
            String v = kvArray[1];
            if (v.contains("&")) {
                v = v.split("&")[0];
            }
            if (StringUtils.equalsIgnoreCase(key, k)) {
                return v;
            }
        }
        return null;
    }

    //Special logic for Marketing
    public static boolean determineSameLink(String utpLink, String deeplink) {
        if (StringUtils.isBlank(utpLink) && StringUtils.isBlank(deeplink)) {
            return true;
        } else if (StringUtils.isBlank(utpLink) || StringUtils.isBlank(deeplink)) {
            return false;
        }

        String decodedUtp = fullyDecode(utpLink);
        String decodedDeep = fullyDecode(deeplink);

        return decodedUtp.startsWith(decodedDeep);
    }
}
