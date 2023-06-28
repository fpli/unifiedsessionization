package com.ebay.epic.soj.flink.connector.kafka.serde;

import com.ebay.epic.soj.common.model.raw.RawEvent;
import io.ebay.rheos.schema.event.RheosEvent;
import org.apache.avro.generic.GenericRecord;

import java.util.ArrayList;
import java.util.Map;

public class AutoTrackRheosKafkaDeserializer extends RheosKafkaDeserializer<RawEvent>{
    private AutoTrackTrafficSourceDeserializer autoTrackTrafficSourceDeserializer =
            new AutoTrackTrafficSourceDeserializer();

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
        autoTrackTrafficSourceDeserializer.convert(genericRecord, rawEvent);
        return rawEvent;
    }
}
