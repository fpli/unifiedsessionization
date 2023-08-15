package com.ebay.epic.soj.flink.connector.kafka.serde;

import com.ebay.epic.soj.common.model.raw.RawEvent;
import io.ebay.rheos.schema.event.RheosEvent;
import org.apache.avro.generic.GenericRecord;

import java.util.ArrayList;

public class RoiRheosKafkaDeserializer extends RheosKafkaDeserializer<RawEvent> {

    public RoiRheosKafkaDeserializer(String schemaRegistryUrl) {
        super(schemaRegistryUrl);
    }

    @Override
    public RawEvent convert(GenericRecord genericRecord, RheosEvent rheosEvent) {
        RawEvent rawEvent = new RawEvent();
        rawEvent.setGuid(genericRecord.get("guid") == null ? null : genericRecord.get("guid").toString());
        rawEvent.setEventTs(Long.valueOf(genericRecord.get("eventTime").toString()));
        rawEvent.setUserId(getStrOrDefault(genericRecord.get("userId"), null));
        rawEvent.setSiteId(getStrOrDefault(genericRecord.get("siteId"), null));
        rawEvent.setPageId(Integer.valueOf(getStrOrDefault(genericRecord.get("pageId"), "0")));
        rawEvent.setRheosByteArray(rheosEvent.toBytes());
        rawEvent.setIframe(false);
        rawEvent.setRdt((byte)0);
        rawEvent.setBotFlags(new ArrayList<>());
        return rawEvent;
    }

    private String getStrOrDefault(Object o, String defaultStr) {
        return o != null ? o.toString() : defaultStr;
    }
}
