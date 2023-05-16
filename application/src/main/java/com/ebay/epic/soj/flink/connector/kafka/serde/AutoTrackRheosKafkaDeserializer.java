package com.ebay.epic.soj.flink.connector.kafka.serde;

import com.ebay.epic.soj.common.model.raw.RawEvent;
import io.ebay.rheos.schema.event.RheosEvent;
import org.apache.avro.generic.GenericRecord;

import java.util.ArrayList;
import java.util.Map;

public class AutoTrackRheosKafkaDeserializer extends RheosKafkaDeserializer<RawEvent>{

    public AutoTrackRheosKafkaDeserializer(String schemaRegistryUrl) {
        super(schemaRegistryUrl);
    }
    @Override
    public RawEvent convert(GenericRecord genericRecord,RheosEvent rheosEvent) {
        GenericRecord activity =  (GenericRecord)genericRecord.get("activity");
        RawEvent rawEvent = new RawEvent();
        rawEvent.setGuid(genericRecord.get("guid").toString());
        rawEvent.setEventTs(Long.valueOf(activity.get("timestamp").toString()));
        rawEvent.setRheosByteArray(rheosEvent.toBytes());
        rawEvent.setSessionId(((Map<String,String>)activity.get("details")).get("ori_sid"));
        rawEvent.setSessionSkey(Long.valueOf(genericRecord.get("sessionId").toString()));
        rawEvent.setIframe(false);
        rawEvent.setRdt((byte)0);
        rawEvent.setBotFlags(new ArrayList<>());

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
        return rawEvent;
    }
}
