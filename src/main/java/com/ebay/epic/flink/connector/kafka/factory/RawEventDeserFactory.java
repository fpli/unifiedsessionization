package com.ebay.epic.flink.connector.kafka.factory;

import com.ebay.epic.common.enums.SchemaSubject;
import com.ebay.epic.flink.connector.kafka.serde.AutoTrackRheosKafkaDeserializer;
import com.ebay.epic.flink.connector.kafka.serde.RheosKafkaDeserializer;
import com.ebay.epic.flink.connector.kafka.serde.UbiRheosKafkaDeserializer;
import com.ebay.epic.flink.connector.kafka.serde.UtpRheosKafkaDeserializer;

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
    }

    public RheosKafkaDeserializer getDeserializer(SchemaSubject schemaSubject){
        return maps.get(schemaSubject);
    }
}
