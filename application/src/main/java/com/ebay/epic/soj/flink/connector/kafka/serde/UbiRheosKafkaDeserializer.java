package com.ebay.epic.soj.flink.connector.kafka.serde;

import com.ebay.epic.soj.common.model.raw.RawEvent;
import com.ebay.sojourner.common.model.ClientData;
import io.ebay.rheos.schema.event.RheosEvent;
import org.apache.avro.generic.GenericRecord;

import java.util.List;

public class UbiRheosKafkaDeserializer extends RheosKafkaDeserializer<RawEvent> {

    public UbiRheosKafkaDeserializer(String schemaRegistryUrl) {
        super(schemaRegistryUrl);
    }

    @Override
    public RawEvent convert(GenericRecord genericRecord, RheosEvent rheosEvent) {
        RawEvent rawEvent = new RawEvent();
        rawEvent.setGuid(genericRecord.get("guid").toString());
        rawEvent.setUserId(genericRecord.get("userId").toString());
        rawEvent.setSiteId(genericRecord.get("siteId").toString());
        rawEvent.setEventTs(Long.valueOf(genericRecord.get("eventTimestamp").toString()));
        rawEvent.setRheosByteArray(rheosEvent.toBytes());
        rawEvent.setSessionId(genericRecord.get("sessionId").toString());
        rawEvent.setSessionSkey(Long.valueOf(genericRecord.get("sessionSkey").toString()));
        rawEvent.setIframe(Boolean.valueOf(genericRecord.get("iframe").toString()));
        rawEvent.setRdt(Byte.valueOf(genericRecord.get("rdt").toString()));
        rawEvent.setBotFlags((List)genericRecord.get("botFlags"));
        rawEvent.setCobrand(genericRecord.get("cobrand").toString());
        rawEvent.setAppId(genericRecord.get("appId").toString());
        //User Agent
        GenericRecord genericClientData = (GenericRecord) genericRecord.get("clientData");
        Object agent = genericClientData.get("agent");
        rawEvent.setUserAgent(agent != null ? agent.toString() : "");
        return rawEvent;
    }
}
