package com.ebay.epic.soj.flink.connector.kafka.serde;

import com.ebay.epic.soj.common.model.raw.RawEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.generic.GenericRecord;

@Slf4j
public class AutoTrackTrafficSourceDeserializer {

    public void convert(GenericRecord genericRecord, RawEvent rawEvent) {
        try {
            GenericRecord activity =  (GenericRecord) genericRecord.get("activity");
            GenericRecord trackable = (GenericRecord) genericRecord.get("trackable");
            String entityType = trackable.get("entityType").toString();
            rawEvent.setEntityType(entityType);
            if ("Page".equals(entityType)) {
                // entityId -> page id
                String entityId = trackable.get("entityId").toString();
                rawEvent.setPageId(Integer.valueOf(entityId));
                // instanceId -> page url
                String instanceId = handleString(trackable.get("instanceId"));
                rawEvent.setPageUrl(instanceId);
                // referer -> referer
                String referer = handleString(activity.get("referer"));
                rawEvent.setReferer(referer);
                // experience
                GenericRecord context = (GenericRecord) genericRecord.get("context");
                String userAgent = handleString(context.get("userAgent"));
                if (userAgent != null) {
                    rawEvent.setExperience(AutoTrackDeserializerUtils.getExperience(userAgent));
                }
            }
        } catch (Exception e) {
            log.warn("failed to convert surface event", e);
        }
    }

    private String handleString(Object obj) {
        return obj == null ? null : obj.toString();
    }
}
