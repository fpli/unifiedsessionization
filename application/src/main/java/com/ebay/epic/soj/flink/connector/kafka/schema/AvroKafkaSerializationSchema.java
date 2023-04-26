package com.ebay.epic.soj.flink.connector.kafka.schema;

import com.ebay.epic.soj.flink.connector.kafka.serde.AvroKafkaSerializer;
import com.ebay.epic.soj.flink.connector.kafka.serde.KafkaSerializer;
import org.apache.avro.specific.SpecificRecord;
import org.apache.flink.streaming.connectors.kafka.KafkaSerializationSchema;
import org.apache.kafka.clients.producer.ProducerRecord;

import javax.annotation.Nullable;
import java.util.List;

public class AvroKafkaSerializationSchema<T extends SpecificRecord> implements
        KafkaSerializationSchema<T> {

    public final String defaultTopic;
    private final List<String> keyFields;
    private transient KafkaSerializer<T> kafkaSerializer;

    public AvroKafkaSerializationSchema(String defaultTopic, List<String> keyFields) {
        this.defaultTopic = defaultTopic;
        this.keyFields = keyFields;
    }

    @Override
    public ProducerRecord<byte[], byte[]> serialize(T element, @Nullable Long timestamp) {
        if (kafkaSerializer == null) {
            kafkaSerializer = new AvroKafkaSerializer<>(element.getSchema());

        }
        return new ProducerRecord<>(defaultTopic,
                kafkaSerializer.encodeKey(element, keyFields),
                kafkaSerializer.encodeValue(element));
    }
}
