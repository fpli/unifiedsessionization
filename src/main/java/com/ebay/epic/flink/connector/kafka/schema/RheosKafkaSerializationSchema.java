package com.ebay.epic.flink.connector.kafka.schema;

import com.ebay.epic.flink.connector.kafka.config.RheosKafkaProducerConfig;
import com.ebay.epic.flink.connector.kafka.serde.KafkaSerializer;
import com.ebay.epic.flink.connector.kafka.serde.RheosAvroKafkaSerializer;
import com.google.common.collect.Lists;
import org.apache.avro.specific.SpecificRecord;
import org.apache.flink.streaming.connectors.kafka.KafkaSerializationSchema;
import org.apache.kafka.clients.producer.ProducerRecord;

import javax.annotation.Nullable;
import java.util.List;

public class RheosKafkaSerializationSchema<T extends SpecificRecord> implements KafkaSerializationSchema<T> {

    private final RheosKafkaProducerConfig rheosKafkaConfig;
    protected final List<String> keys;
    private final Class<T> clazz;
    private transient KafkaSerializer<T> rheosKafkaSerializer;

    public RheosKafkaSerializationSchema(RheosKafkaProducerConfig rheosKafkaConfig, Class<T> clazz, String... keys) {
        this.rheosKafkaConfig = rheosKafkaConfig;
        this.clazz = clazz;
        this.keys = Lists.newArrayList(keys);
        this.rheosKafkaSerializer = new RheosAvroKafkaSerializer<>(rheosKafkaConfig, clazz);
    }

    @Override
    public ProducerRecord<byte[], byte[]> serialize(T element, @Nullable Long timestamp) {
        return new ProducerRecord<>(
                rheosKafkaConfig.getTopic(),
                rheosKafkaSerializer.encodeKey(element, keys),
                rheosKafkaSerializer.encodeValue(element)
        );
    }
}
