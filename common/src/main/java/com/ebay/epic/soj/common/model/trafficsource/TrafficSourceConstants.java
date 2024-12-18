package com.ebay.epic.soj.common.model.trafficsource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TrafficSourceConstants {
    // managed traffic source level 1
    public static final String LEVEL1_MANAGED_FREE = "Managed: Free";
    public static final String LEVEL1_MANAGED_NAV_SEARCH = "Managed: Nav Search";
    public static final String LEVEL1_MANAGED_PAID = "Managed: Paid";
    public static final String LEVEL1_SEO = "SEO";
    public static final String LEVEL1_UNMANAGED = "Unmanaged";

    // customer traffic source level 1
    public static final String LEVEL1_FREE = "Free";
    public static final String LEVEL1_ORGANIC = "Organic";
    public static final String LEVEL1_PAID = "Paid";

    //customer traffic source level 2
    public static final String LEVEL2_FREE_FREE_SOCIAL = "Free: Free Social";
    public static final String LEVEL2_FREE_MKTG_COMMS = "Free: Mktg Comms";
    public static final String LEVEL2_FREE_OTHER = "Free: Other";
    public static final String LEVEL2_FREE_SEO = "Free: SEO";
    public static final String LEVEL2_ORGANIC_DIRECT = "Organic: Direct";
    public static final String LEVEL2_ORGANIC_IMBD = "Organic: IMBD";
    public static final String LEVEL2_ORGANIC_NAV_SEARCH_FREE = "Organic: Nav Search: Free";
    public static final String LEVEL2_ORGANIC_NAV_SEARCH_PAID = "Organic: Nav Search: Paid";
    public static final String LEVEL2_ORGANIC_TXN_COMMS = "Organic: Txn Comms";
    public static final String LEVEL2_PAID_DISPLAY = "Paid: Display";
    public static final String LEVEL2_PAID_EPN = "Paid: ePN";
    public static final String LEVEL2_PAID_PAID_SEARCH = "Paid: Paid Search";
    public static final String LEVEL2_PAID_PAID_SOCIAL = "Paid: Paid Social";

    // traffic source level 3
    public static final String ORGANIC_NAV_SEARCH_PAID = "Organic: Nav Search: Paid";
    public static final String PAID_PAID_SEARCH = "Paid: Paid Search";
    public static final String PAID_EPN = "Paid: ePN";
    public static final String PAID_DISPLAY = "Paid: Display";
    public static final String PAID_PAID_SOCIAL = "Paid: Paid Social";
    public static final String FREE_SEO_FREE_FEEDS = "Free: SEO: Free Feeds";
    public static final String ORGANIC_TXN_COMMS_SITE_EMAIL = "Organic: Txn Comms: Site Email";
    public static final String FREE_MKTG_COMMS_MKTG_EMAIL = "Free: Mktg Comms: Mktg Email";
    public static final String FREE_FREE_SOCIAL = "Free: Free Social";
    public static final String FREE_MKTG_COMMS_SMS = "Free: Mktg Comms: SMS";
    public static final String ORGANIC_TXN_COMMS_CS_EMAIL = "Organic: Txn Comms: Customer Service Email";
    public static final String FREE_MRKT_COMMS_NOTIF = "Free: Mktg Comms: Notifications: Apps";
    public static final String ORGANIC_TXN_COMMS_NOTIF = "Organic: Txn Comms: Notifications: Apps";
    public static final String ORGANIC_IMBD = "Organic: IMBD";
    public static final String FREE_SEO_NATURAL_SEARCH = "Free: SEO: Natural Search";
    public static final String ORGANIC_NAV_SEARCH_FREE = "Organic: Nav Search: Free";
    public static final String ORGANIC_DIRECT_ON_EBAY = "Organic: Direct: On eBay";
    public static final String ORGANIC_TXN_COMMS_WEBMAIL = "Organic: Txn Comms: Webmail w/o tracking";
    public static final String ORGANIC_DIRECT_NO_REF = "Organic: Direct: No Referrer";
    public static final String FREE_OTHER = "Free: Other";

    // ubi event fields
    public static final String UBI_FIELD_URLQUERYSTRING = "urlQueryString";
    public static final String UBI_FIELD_REFERRER = "referrer";

    // payload tags
    public static final String PAYLOAD_KEY_REF = "ref";
    // use ubi event field instead, this is slightly different from batch
    public static final String PAYLOAD_KEY_RDT = "rdt";
    public static final String PAYLOAD_KEY_CHNL = "chnl";
    public static final String PAYLOAD_KEY_ROTID = "rotid";
    public static final String PAYLOAD_KEY_URL_MPRE = "url_mpre";
    public static final String PAYLOAD_KEY_PNACT = "pnact";
    public static final String PAYLOAD_KEY_MPPID = "mppid";
    public static final String PAYLOAD_KEY_NTYPE = "ntype";

    // page ids
    public static final int CHOCOLATE_PAGE = 2547208;
    public static final int NOTIFICATION_PAGE = 2054060;
    public static final int IMBD_PAGE = 2051248;
    public static final int DEEPLINK_PAGE = 2367320;

    // timestamp threshold
    public static final long UTP_THRESHOLD = 5000;
    public static final long IMBD_THRESHOLD = 600000;

    // others
    public static final String KEYWORD_PARAM = "keyword";
    public static final String UNKNOWN_REF = "unknown";
    public static final List<String> EBAY_KEYWORDS = new ArrayList<>(Arrays.asList("ebay", "eaby", "eby", "eba", "eabay",
            "e+bay", "e-bay", "e.bay", "kijiji")); // Common ebay misspellings and ebay subsidiary
    public static final List<String> CHOCOLATE_CHNL = new ArrayList<>(Arrays.asList("1", "2", "4", "7", "8", "16",
            "24", "28", "29"));
    public static final List<String> SEARCH_ENGINE_KEYWORDS = new ArrayList<>(Arrays.asList("google", "bing", "yahoo",
            "duckduckgo", "yandex"));
    public static final List<String> SOCIAL_KEYWORDS = new ArrayList<>(Arrays.asList("youtube", "facebook", "twitter",
            "pinterest", "instagram", "linkedin", "t.co"));
    public static final String MAIL_KEYWORD = "mail";
    public static final String EBAY_KEYWORD = "ebay";
    public static final String NULL_KEYWORD = "null";
    public static final String HOME_PAGE_NAME = "Home Page";
    public static final String MRKT_NOTIF_NTYPE = "MarketingCampaign";
    public static final String TRAFFIC_SOUCE_OTHER = "Other";
}
