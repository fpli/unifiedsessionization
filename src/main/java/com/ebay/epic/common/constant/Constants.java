package com.ebay.epic.common.constant;

import java.util.TimeZone;

public class Constants {

    // flink metrics
    // first level metric name
    public static final String UNIFIED_SESSION_METRICS_GROUP = "unified_sessionization";
    // second level metric name
    public static final String SURFACE_WEB_METRICS_GROUP = "surface_web";
    public static final String SURFACE_NATIVE_METRICS_GROUP = "surface_native";
    public static final String SOJ_BOT_METRICS_GROUP = "soj_bot";
    public static final String SOJ_NONBOT_METRICS_GROUP = "soj_nonbot";
    public static final String UTP_NONBOT_METRICS_GROUP = "utp_nonbot";

    // flied delimiter
    public static final String FIELD_DELIM = "\007";
    public static final String DOMAIN_DEL = "-";
    public static final String METRIC_DEL = "_";
    public static final String NO_SESSION_ID = "NO_SESSION_ID";
    //Time format/ Time Zone Constants
    public static final String DEFAULT_TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    public static final String DEFAULT_DATE_MINS_FORMAT = "yyyy-MM-dd HH:mm";

    public static final String EBAY_TIMEZONE = "GMT-7";
    public static final TimeZone UTC_TIMEZONE = TimeZone.getTimeZone("UTC");
    public static final TimeZone PST_TIMEZONE = TimeZone.getTimeZone(EBAY_TIMEZONE);
    public static final String SOJ_METRIC_TYPE = "metric_type";
    public static final String SOJ_METRIC_NAME = "metric_name";
    public static final String SOJ_METRIC_DROPPED_EVENT_CNT = "dropped_event_cnt";
    //CHARSET
    public static final String CHAR_SET = "UTF-8";

    public final static String PAGE_METADATA_PATH = "/apps/b_trk/lookups/pages";
    public final static String MODULE_METADATA_PATH = "/apps/b_trk/lookups/modules";
    public final static String CLICK_METADATA_PATH = "/apps/b_trk/lookups/clicks";

    // the offset align with UTC-
    public static final long OFFSET = 2208963600000000L; // 25567L *24 * 3600 * 1000 * 1000 - 7 *
    // 3600 * 1000 * 1000;
    public static final long MICROECOFDAY = 86400000000L; // 24 * 3600 * 1000 * 1000
    public static final long MILSECOFDAY = 86400000L; // 24 * 3600 * 1000 * 1000
    public static final long MILSECOFDAYMINUS1 = 86400000L - 1L; // 24 * 3600 * 1000 * 1000
    public static final int MILLI2MICRO = 1000;

}

