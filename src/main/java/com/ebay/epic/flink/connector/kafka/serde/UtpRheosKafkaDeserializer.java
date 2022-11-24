package com.ebay.epic.flink.connector.kafka.serde;

import com.ebay.epic.common.enums.EventType;
import com.ebay.epic.common.model.raw.RawEvent;
import io.ebay.rheos.schema.event.RheosEvent;
import org.apache.avro.generic.GenericRecord;

public class UtpRheosKafkaDeserializer extends RheosKafkaDeserializer<RawEvent> {

    public UtpRheosKafkaDeserializer(String schemaRegistryUrl) {
        super(schemaRegistryUrl);
    }

    @Override
    public RawEvent convert(GenericRecord genericRecord, RheosEvent rheosEvent) {
        RawEvent rawEvent = new RawEvent();
        rawEvent.setGuid(genericRecord.get("guid") == null ? null : genericRecord.get("guid").toString());
        rawEvent.setEventTs(Long.valueOf(genericRecord.get("producerEventTs").toString()));
        rawEvent.setEventType(EventType.UTP);
        rawEvent.setRheosByteArray(rheosEvent.toBytes());
        return rawEvent;
    }
}
