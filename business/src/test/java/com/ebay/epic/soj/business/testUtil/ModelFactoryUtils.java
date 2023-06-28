package com.ebay.epic.soj.business.testUtil;

import com.ebay.epic.soj.common.enums.EventType;
import com.ebay.epic.soj.common.model.raw.UniEvent;
import com.ebay.epic.soj.common.model.trafficsource.*;

import java.util.Map;

public class ModelFactoryUtils {
    public static UniEvent getUniEvent(EventType type, int pageId, int rdt, String url, String referer) {
        return UniEvent.builder()
                .eventType(type)
                .eventTs(System.currentTimeMillis())
                .guid("testGuid")
                .globalSessionId("testSessionId")
                .pageId(pageId)
                .pageUrl(url)
                .rdt((byte) rdt)
                .referer(referer)
                .build();
    }

    public static UniEvent getUniEvent(EventType type, int pageId, int rdt, String referer, Map<String, String> payload) {
        return UniEvent.builder()
                .eventType(type)
                .eventTs(System.currentTimeMillis())
                .guid("testGuid")
                .globalSessionId("testSessionId")
                .pageId(pageId)
                .rdt((byte) rdt)
                .referer(referer)
                .payload(payload)
                .build();
    }

    public static Page getPage(int pageId, String pageName, int iframe) {
        return Page.builder()
                .pageId(pageId)
                .pageName(pageName)
                .iframe(iframe)
                .build();
    }

    public static DwMpxRotation getMpxRotation(long rotationId, int mpxChnlId) {
        return DwMpxRotation.builder()
                .rotationId(rotationId)
                .mpxChnlId(mpxChnlId)
                .build();
    }

    public static ValidUbiEvent getValidUbiEvent(long eventTimestamp, int pageId, String pageName, String url, String referer) {
        return ValidUbiEvent.builder()
                .eventTimestamp(eventTimestamp)
                .pageId(pageId)
                .pageName(pageName)
                .url(url)
                .referer(referer)
                .build();
    }

    public static ValidSurfaceEvent getValidSurfaceEvent(long eventTimestamp, int pageId, String pageName, String url, String referer) {
        return ValidSurfaceEvent.builder()
                .eventTimestamp(eventTimestamp)
                .pageId(pageId)
                .pageName(pageName)
                .url(url)
                .referer(referer)
                .build();
    }

    public static UtpEvent getUtpEvent(long eventTimestamp, int pageId, Integer chnl, Long rotId, Integer mpxChnlId, String url) {
        return UtpEvent.builder()
                .eventTimestamp(eventTimestamp)
                .pageId(pageId)
                .chnl(chnl)
                .rotId(rotId)
                .mpxChnlId(mpxChnlId)
                .url(url)
                .build();
    }

    public static ImbdEvent getImbdEvent(long eventTimestamp, String mppid) {
        return ImbdEvent.builder()
                .eventTimestamp(eventTimestamp)
                .mppid(mppid)
                .build();
    }

    public static DeeplinkActionEvent getDeeplinkActionEvent(long eventTimestamp, String referer) {
        return DeeplinkActionEvent.builder()
                .eventTimestamp(eventTimestamp)
                .referer(referer)
                .build();
    }
}
