package com.ebay.epic.soj.flink.connector.kafka.factory;

import com.ebay.epic.soj.common.enums.SchemaSubject;
import com.ebay.epic.soj.flink.connector.kafka.serde.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RawEventDeserFactory {
    private Map<SchemaSubject, RheosKafkaDeserializer> maps = new ConcurrentHashMap<>();
    private String schemaRegistryUrl;
    public RawEventDeserFactory(String schemaRegistryUrl){
        this.schemaRegistryUrl=schemaRegistryUrl;
        maps.put(SchemaSubject.AUTOTRACK, new AutoTrackRheosKafkaDeserializer(this.schemaRegistryUrl));
        maps.put(SchemaSubject.UBI, new UbiRheosKafkaDeserializer(this.schemaRegistryUrl));
        maps.put(SchemaSubject.UTP, new UtpRheosKafkaDeserializer(this.schemaRegistryUrl));
        maps.put(SchemaSubject.ROI, new RoiRheosKafkaDeserializer(this.schemaRegistryUrl));
    }

    public RheosKafkaDeserializer getDeserializer(SchemaSubject schemaSubject){
        return maps.get(schemaSubject);
    }
}
