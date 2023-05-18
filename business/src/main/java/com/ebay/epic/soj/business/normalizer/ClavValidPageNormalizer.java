package com.ebay.epic.soj.business.normalizer;

import com.ebay.epic.soj.common.enums.EventType;
import com.ebay.epic.soj.common.model.raw.RawEvent;
import com.ebay.epic.soj.common.model.raw.UniEvent;
import com.ebay.sojourner.common.util.SOJExtractFlag;
import com.ebay.sojourner.common.util.SOJNVL;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public class ClavValidPageNormalizer extends FieldNormalizer<RawEvent, UniEvent> {

    private static final int[] CLFGPageIds = {2588, 3030, 3907, 4939, 5108};
    private static final int[] WSPageIds = {1702440, 2043183, 2043216, 2047524, 2051322, 2051319, 2052193, 2051542, 2052317, 3693, 2047675, 2054574, 2057587, 2052197, 2049334, 2052122, 2051865, 4853};
    private static final int[] STATEPageIds = {2765, 2771, 2685, 3306, 2769, 4034, 4026};
    private static final int[] XOProcessorPageIds = {5713, 2053584, 6024, 2053898, 6053, 2054900};
    private static final String[] IpLists = {"10.2.137.50", "10.2.182.150", "10.2.137.51", "10.2.182.151"};
    private static final String[] pageLists = {"ryprender", "cyp", "success", "pots", "error", "rypexpress", "MainCheckoutPage"};
    private static final String[] checkoutCleanList = {"MainCheckoutPage", "CheckoutPaymentSuccess", "CheckoutPayPalWeb", "PaymentSent", "CheckoutPaymentMethod", "Autopay", "CheckoutPayPalError1", "CheckoutPaymentFailed"};
    private static final int[] InvalidPageIds = {4600, 0, 4561, 4394, 4105, 3936, 4370, 4369, 4447, 3848, 3847, 3846, 3849, 4648, 3872, 4626, 2219, 4490,
            4016, 4813, 4684, 4433, 4803, 4827, 4843, 4909, 3054, 4095, 5024, 3880, 4887, 4818, 4599, 2608, 5277, 5209,
            5494, 3856, 5457, 5476, 5609, 3676, 4346, 4855, 1992, 4931, 5074, 4993, 4223, 4592, 2720, 1892, 1893, 4008,
            3288, 2015, 4699, 4859};

    private static final int[] WhiteListPageIds = {2381081}; // this page id log the infinite recommendation stream in SRP native


    @Override
    public boolean accept(RawEvent src) {
        return src.getEventType() == EventType.UBI_NONBOT || src.getEventType() == EventType.UBI_BOT;
    }

    @Override
    public void normalize(RawEvent src, UniEvent tar) throws Exception {
        if (checkIsValidEvent(src.getPageId(), src.getClientData(), src.getSqr(), Integer.parseInt(src.getSiteId()), src.getPayload(), src.getRdt() != 0, src.getIframe())) {
            tar.setClavValidPage(true);
        } else {
            tar.setPartialValidPage(false);
        }
    }

    private boolean checkIsValidEvent(Integer pageId, String clientData, String sqr, Integer siteId, Map<String, String> payload, boolean isRdt, boolean isIframe) {
        if (isWhiteListPage(pageId)) {
            return true;
        }
        int csTracking = 0;
        String urlQueryString = payload == null ? null : payload.get("urlQueryString");
        if (StringUtils.isNotBlank(urlQueryString) &&
                (urlQueryString.startsWith("/roverimp") || urlQueryString.contains("SojPageView"))) {
            csTracking = 1;
        }
        if (csTracking != 0 || isIframe) {
            return false;
        }
        if (!isPartialValidPage(pageId, urlQueryString, clientData, sqr, siteId, payload, isRdt)) {
            return false;
        }
        if (pageId == -1 || isCorrespondingPageId(pageId, InvalidPageIds)) {
            return false;
        }
        return true;
    }

    private boolean isPartialValidPage(Integer pageId, String urlQueryString, String clientData, String sqr, Integer siteId, Map<String, String> payload, boolean isRdt) {
        if (isRdt) {
            return false;
        }
        if (pageId != null && (pageId == 3686) && StringUtils.isNotBlank(urlQueryString) && urlQueryString.contains("Portlet")) {
            return false;
        }
        if (pageId != null && (pageId == 451) && StringUtils.isNotBlank(urlQueryString) && urlQueryString.contains("LogBuyerRegistrationJSEvent")) {
            return false;
        }
        String webServer = clientData == null ? null : SOJNVL.getTagValue(clientData, "server");
        if (StringUtils.isNotBlank(webServer) && webServer.contains("sandbox.ebay.")) {
            return false;
        }
        String sojPage = payload == null ? null : payload.get("page");
        String urlQueryPage = StringUtils.isNotBlank(urlQueryString) ? null : SOJNVL.getTagValue(urlQueryString, "page");
        String remoteIP = clientData == null ? null : SOJNVL.getTagValue(clientData, "RemoteIP");
        String pfn = payload == null ? null : payload.get("pfn");
        String cflags = payload == null ? null : payload.get("cflgs");

        if (isCorrespondingPageId(pageId, CLFGPageIds) && StringUtils.isNotBlank(cflags) && SOJExtractFlag.extractFlag(cflags, 4) == 1) {
            return false;
        }
        if (StringUtils.isNotBlank(cflags) && SOJExtractFlag.extractFlag(cflags, 14) == 1) {
            return false;
        }

        if (pageId != null && (pageId == 1468660) && siteId != null && (siteId == 0) && StringUtils.isNotBlank(webServer) && webServer.equals("rover.ebay.com")) {
            return false;
        }
        if (pageId != null && isCorrespondingPageId(pageId, WSPageIds) && StringUtils.isNotBlank(webServer) && webServer.startsWith("rover.ebay.")) {
            return false;
        }
        if (payload != null && payload.get("an") != null && payload.get("av") != null) {
            return false;
        }
        if (payload != null && payload.get("in") != null) {
            return false;
        }
        if (pageId != null && (pageId == 5360) && StringUtils.isNotBlank(urlQueryString) && urlQueryString.contains("_xhr=2")) {
            return false;
        }
        if (StringUtils.isNotBlank(urlQueryString) && (urlQueryString.startsWith("/_vti_bin") || urlQueryString.startsWith("/MSOffice/cltreq.asp"))) {
            return false;
        }
        if ((payload != null && "1".equals(payload.get("mr"))) || StringUtils.isNotBlank(urlQueryString) && (urlQueryString.contains("?redirect=mobile") || urlQueryString.contains("&redirect=mobile"))) {
            return false;
        }
        if (pageId != null && (pageId == 2043141) && StringUtils.isNotBlank(urlQueryString) && (urlQueryString.contains("jsf.js") || urlQueryString.startsWith("/intercept.jsf"))) {
            return false;
        }
        if (pageId != null && isCorrespondingPageId(pageId, STATEPageIds) && payload.get("state") == null) {
            return false;
        }
        if (clientData != null && remoteIP != null && isCorrespondingString(remoteIP, IpLists)) {
            return false;
        }
        if (StringUtils.isNotBlank(urlQueryString) && ("/&nbsp;".equals(urlQueryString) || "/&nbsb;".equals(urlQueryString))) {
            return false;
        }
        if (pageId != null && (pageId == 1677950) && StringUtils.isNotBlank(sqr) && sqr.equals("postalCodeTestQuery")) {
            return false;
        }
        if (pageId != null && isCorrespondingPageId(pageId, XOProcessorPageIds) && (sojPage == null || !isCorrespondingString(sojPage, pageLists))) {
            return false;
        }
        if ((pageId == null || (pageId != 2050757)) && StringUtils.isNotBlank(clientData) && clientData.contains("eBayNioHttpClient")) {
            return false;
        }
        if (pageId != null && (pageId == 2050867) && StringUtils.isNotBlank(urlQueryString) && (urlQueryString.contains("json") || urlQueryString.startsWith("/local/availability"))) {
            return false;
        }
        if (pageId != null && ((pageId == 2052122) || (pageId == 2050519)) && StringUtils.isNotBlank(urlQueryString) && urlQueryString.contains("json")) {
            return false;
        }
        if (StringUtils.isNotBlank(urlQueryString) && ("null".equals(urlQueryString) || "undefined".equals(urlQueryString) || urlQueryString.endsWith(".gif") || urlQueryString.endsWith(".png") || urlQueryString.endsWith(".pdf") || urlQueryString.endsWith(".jpeg") || urlQueryString.endsWith(".swf") || urlQueryString.endsWith(".txt") || urlQueryString.endsWith(".wav") || urlQueryString.endsWith(".zip") || urlQueryString.endsWith(".flv") || urlQueryString.endsWith(".ico") || urlQueryString.endsWith(".jpg"))) {
            return false;
        }
        String pageName = clientData == null ? null : SOJNVL.getTagValue(clientData, "tName");
        if (pageId != null && (pageId == 2050601) && (!StringUtils.isNotBlank(pageName) || !pageName.startsWith("FeedHome"))) {
            return false;
        }
        if (pageId != null && (pageId == 2054095) && (!StringUtils.isNotBlank(urlQueryString) || !urlQueryString.startsWith("/survey"))) {
            return false;
        }
        if (pageId != null && (pageId == 2056116) && StringUtils.isNotBlank(urlQueryString) && (urlQueryString.startsWith("/itm/watchInline") || urlQueryString.startsWith("/itm/ajaxSmartAppBanner"))) {
            return false;
        }
        if (pageId != null && (pageId == 2059707) && StringUtils.isNotBlank(urlQueryString) && urlQueryString.startsWith("/itm/delivery")) {
            return false;
        }
        if (pageId != null && (pageId == 2052197) && StringUtils.isNotBlank(urlQueryString) && (urlQueryString.contains("ImportHubItemDescription") || urlQueryString.contains("ImportHubCreateListing"))) {
            return false;
        }
        if (pageId != null && ((pageId == 2047935) || (pageId == 2053898)) && StringUtils.isNotBlank(webServer) && webServer.startsWith("reco.ebay.")) {
            return false;
        }
        if (pageId != null && (pageId == 2067339) && StringUtils.isNotBlank(urlQueryString) && urlQueryString.startsWith("/roverimp/0/0/9?")) {
            return false;
        }
        if (pageId != null && (pageId == 2053898) && (!StringUtils.isNotBlank(urlQueryPage) || !isCorrespondingString(urlQueryPage, checkoutCleanList) || sojPage == null)) {
            return false;
        }
        if (pageId != null && (pageId == 2056812) && (sojPage == null || (!("ryprender".equals(sojPage)) && !("cyprender".equals(sojPage))))) {
            return false;
        }
        if (pageId != null && (pageId == 2056116) && (!StringUtils.isNotBlank(pfn) || !("VI".equals(pfn)))) {
            return false;
        }
        String app = payload.get("app");
        if (pageId != null && pageId == 2380424 && ("1462".equals(app) || "2878".equals(app))) {
            return false;
        }
        String sHit = payload.get("sHit");
        if (pageId != null && pageId == 2351460 && sHit == null && ("1462".equals(app) || "2878".equals(app))) {
            return false;
        }
        return true;
    }

    private boolean isCorrespondingPageId(Integer id, int[] pageIdList) {
        for (int pageId : pageIdList) {
            if (pageId == id) {
                return true;
            }
        }
        return false;
    }

    private boolean isCorrespondingString(String source, String[] matchStr) {
        for (String str : matchStr) {
            if (str.equals(source)) {
                return true;
            }
        }
        return false;
    }

    private boolean isWhiteListPage(Integer pageId) {
        return isCorrespondingPageId(pageId, WhiteListPageIds);
    }

}
