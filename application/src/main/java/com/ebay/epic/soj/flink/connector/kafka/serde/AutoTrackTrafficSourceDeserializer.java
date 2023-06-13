package com.ebay.epic.soj.flink.connector.kafka.serde;

import com.ebay.epic.soj.common.model.raw.RawEvent;
import org.apache.avro.generic.GenericRecord;

public class AutoTrackTrafficSourceDeserializer {

    public void convert(GenericRecord genericRecord, RawEvent rawEvent) {
        GenericRecord activity =  (GenericRecord)genericRecord.get("activity");
        GenericRecord trackable = (GenericRecord) genericRecord.get("trackable");
        String entityType = trackable.get("entityType").toString();
        rawEvent.setEntityType(entityType);
        if ("Page".equals(entityType)) {
            // entityId -> page id
            String entityId = trackable.get("entityId").toString();
            rawEvent.setPageId(Integer.valueOf(entityId));
            // instanceId -> page url
            String instanceId = trackable.get("instanceId") == null ?
                    null : trackable.get("instanceId").toString();
            rawEvent.setPageUrl(instanceId);
            // referer -> referer
            String referer = activity.get("referer") == null ?
                    null : activity.get("referer").toString();
            rawEvent.setReferer(referer);
            // experience
            GenericRecord context = (GenericRecord) genericRecord.get("context");
            String userAgent = context.get("userAgent") == null ?
                    null : context.get("userAgent").toString();
            if (userAgent != null) {
                rawEvent.setExperience(AutoTrackDeserializerUtils.getExperience(userAgent));
            }
        }
    }
}
