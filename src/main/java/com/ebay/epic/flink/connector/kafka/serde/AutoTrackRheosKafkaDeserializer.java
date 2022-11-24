package com.ebay.epic.flink.connector.kafka.serde;

import com.ebay.epic.common.enums.EventType;
import com.ebay.epic.common.model.raw.RawEvent;
import io.ebay.rheos.schema.event.RheosEvent;
import org.apache.avro.generic.GenericRecord;

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
        rawEvent.setEventType(EventType.AUTOTRACK);
        rawEvent.setRheosByteArray(rheosEvent.toBytes());
        rawEvent.setSessionId(((Map<String,String>)activity.get("details")).get("ori_sid"));
        rawEvent.setSessionSkey(Long.valueOf(genericRecord.get("sessionId").toString()));
        return rawEvent;
    }
}
