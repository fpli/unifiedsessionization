package com.ebay.epic.soj.flink.connector.kafka.serde;

import com.ebay.epic.soj.common.enums.EventType;
import com.ebay.epic.soj.common.model.raw.RawEvent;
import io.ebay.rheos.schema.event.RheosEvent;
import org.apache.avro.generic.GenericRecord;

import java.util.ArrayList;

public class UtpRheosKafkaDeserializer extends RheosKafkaDeserializer<RawEvent> {

    public UtpRheosKafkaDeserializer(String schemaRegistryUrl) {
        super(schemaRegistryUrl);
    }

    @Override
    public RawEvent convert(GenericRecord genericRecord, RheosEvent rheosEvent) {
        RawEvent rawEvent = new RawEvent();
        rawEvent.setGuid(genericRecord.get("guid") == null ? null : genericRecord.get("guid").toString());
        rawEvent.setEventTs(Long.valueOf(genericRecord.get("producerEventTs").toString()));
        rawEvent.setRheosByteArray(rheosEvent.toBytes());
        rawEvent.setIframe(false);
        rawEvent.setRdt((byte)0);
        rawEvent.setBotFlags(new ArrayList<>());
        return rawEvent;
    }
}
