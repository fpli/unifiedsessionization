package com.ebay.epic.soj.flink.connector.kafka.serde;

import com.ebay.epic.soj.common.model.raw.RawEvent;
import com.ebay.epic.soj.common.model.trafficsource.TrafficSourceConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.generic.GenericRecord;

import java.util.Map;

import static com.ebay.epic.soj.common.model.trafficsource.TrafficSourceConstants.CHOCOLATE_PAGE;
import static com.ebay.epic.soj.common.model.trafficsource.TrafficSourceConstants.IMBD_PAGE;
import static com.ebay.epic.soj.common.model.trafficsource.TrafficSourceConstants.NOTIFICATION_PAGE;
import static com.ebay.epic.soj.common.model.trafficsource.TrafficSourceConstants.UBI_FIELD_REFERRER;
import static com.ebay.epic.soj.common.model.trafficsource.TrafficSourceConstants.UBI_FIELD_URLQUERYSTRING;

@Slf4j
public class UbiTrafficSourceDeserializer {

    public void convert(GenericRecord genericRecord, RawEvent rawEvent) {
        try {
            // extract ubi event fields
            Object urlQueryString = genericRecord.get(UBI_FIELD_URLQUERYSTRING);
            if (urlQueryString != null) {
                rawEvent.setPageUrl(urlQueryString.toString());
            }
            Object referrer = genericRecord.get(UBI_FIELD_REFERRER);
            if (referrer != null) {
                rawEvent.setReferer(referrer.toString());
            }
            // extract application payload tags
            Map<String, String> payload = rawEvent.getPayload();
            extractPayload(genericRecord, TrafficSourceConstants.PAYLOAD_KEY_REF, payload);
            Integer pageId = rawEvent.getPageId();
            // UTP events
            if (pageId == CHOCOLATE_PAGE || pageId == NOTIFICATION_PAGE) {
                extractPayload(genericRecord, TrafficSourceConstants.PAYLOAD_KEY_ROTID, payload);
                extractPayload(genericRecord, TrafficSourceConstants.PAYLOAD_KEY_URL_MPRE, payload);
            }
            if (pageId == CHOCOLATE_PAGE) {
                extractPayload(genericRecord, TrafficSourceConstants.PAYLOAD_KEY_CHNL, payload);
            }
            if (pageId == NOTIFICATION_PAGE) {
                extractPayload(genericRecord, TrafficSourceConstants.PAYLOAD_KEY_PNACT, payload);
            }
            // IMBD events
            if (pageId == IMBD_PAGE) {
                extractPayload(genericRecord, TrafficSourceConstants.PAYLOAD_KEY_MPPID, payload);
            }
        } catch (Exception e) {
            log.warn("failed to convert ubi event", e);
        }
    }

    private void extractPayload(
            GenericRecord genericRecord,
            String key,
            Map<String, String> payload) {
        if (genericRecord.get(key) != null) {
            payload.put(key, genericRecord.get(key).toString());
        }
    }
}
