package com.ebay.epic.soj.flink.connector.kafka.serde;

import com.ebay.epic.soj.common.model.raw.RawEvent;
import com.ebay.epic.soj.common.model.trafficsource.TrafficSourceConstants;
import org.apache.avro.generic.GenericRecord;

import java.util.Map;

public class UbiTrafficSourceDeserializer {

    public void convert(GenericRecord genericRecord, RawEvent rawEvent) {
        Integer pageId = (Integer) genericRecord.get("pageId");
        rawEvent.setPageId(pageId);
        if (genericRecord.get("urlQueryString") != null) {
            rawEvent.setPageUrl(genericRecord.get("urlQueryString").toString());
        }
        if (genericRecord.get("referrer") != null) {
            rawEvent.setReferer(genericRecord.get("referrer").toString());
        }
        Map<String, String> payload = rawEvent.getPayload();
        extractPayload(genericRecord, TrafficSourceConstants.PAYLOAD_KEY_REF, payload);
        // UTP events
        // 2054060: Notifications: Apps
        if (pageId == 2547208 || pageId == 2054060) {
            extractPayload(genericRecord, TrafficSourceConstants.PAYLOAD_KEY_ROTID, payload);
            extractPayload(genericRecord, TrafficSourceConstants.PAYLOAD_KEY_URL_MPRE, payload);
        }
        if (pageId == 2547208) {
            extractPayload(genericRecord, TrafficSourceConstants.PAYLOAD_KEY_CHNL, payload);
        }
        if (pageId == 2054060) {
            extractPayload(genericRecord, TrafficSourceConstants.PAYLOAD_KEY_PNACT, payload);
        }
        // IMBD events
        if (pageId == 2051248) {
            extractPayload(genericRecord, TrafficSourceConstants.PAYLOAD_KEY_MPPID, payload);
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
