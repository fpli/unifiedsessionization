package com.ebay.epic.flink.connector.kafka.serde;

import com.ebay.epic.flink.connector.kafka.factory.RheosEventSerdeFactory;
import io.ebay.rheos.schema.event.RheosEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.generic.GenericRecord;

@Slf4j
public abstract class RheosKafkaDeserializer<T> implements KafkaDeserializer<T> {

    private String schemaRegistryUrl;

    public RheosKafkaDeserializer(String schemaRegistryUrl) {
        this.schemaRegistryUrl = schemaRegistryUrl;
    }

    @Override
    public T decodeValue(byte[] message) throws Exception {
        RheosEvent rheosEvent =
                RheosEventSerdeFactory.getRheosEventHeaderDeserializer().deserialize(null, message);
        GenericRecord genericRecord=null;
        try {
            genericRecord = RheosEventSerdeFactory.getRheosEventDeserializer(schemaRegistryUrl)
                    .decode(rheosEvent);
        }catch (Exception e){
            log.error("Error when deserializing RawEvent from source, schemaId: {}, timestamp: {}",
                    rheosEvent.getSchemaId(), rheosEvent.getEventCreateTimestamp(), e);
            throw e;
        }finally {
            if(genericRecord!=null) {
                return convert(genericRecord, rheosEvent);
            }else{
                return null;
            }
        }

    }

    public abstract T convert(GenericRecord genericRecord, RheosEvent rheosEvent);
}