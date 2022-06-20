package com.ebay.epic.utils;

import com.ebay.epic.common.model.SurfaceTrackingEvent;
import com.ebay.epic.common.model.SojEvent;
import com.ebay.epic.common.model.UTPEvent;
import com.ebay.epic.common.model.UniTrackingEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TimestampFieldExtractor {

    public static <T> long getField(T t) {

        if (t instanceof UniTrackingEvent) {
            UniTrackingEvent uniTrackingEvent = (UniTrackingEvent) t;
            return uniTrackingEvent.getEventTimestamp();
        } else if (t instanceof SojEvent) {
            SojEvent sojEvent = (SojEvent) t;
            return sojEvent.getEventTimestamp();
        } else if (t instanceof UTPEvent) {
            UTPEvent utpEvent = (UTPEvent) t;
            return utpEvent.getEventTs();
        } else if (t instanceof SurfaceTrackingEvent) {
            SurfaceTrackingEvent surfaceTrackingEvent = (SurfaceTrackingEvent) t;
            return surfaceTrackingEvent.getRheosHeader().getEventSentTimestamp();
        } else {
            throw new IllegalStateException("Cannot extract timestamp filed for generate watermark");
        }
    }
}
