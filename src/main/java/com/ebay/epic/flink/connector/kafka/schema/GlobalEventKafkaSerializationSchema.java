package com.ebay.epic.flink.connector.kafka.schema;

import com.ebay.epic.common.constant.KafkaMessageHeaders;
import com.ebay.epic.common.model.avro.GlobalEvent;
import com.ebay.epic.flink.connector.kafka.config.RheosKafkaProducerConfig;
import com.ebay.epic.flink.connector.kafka.serde.KafkaSerializer;
import com.ebay.epic.flink.connector.kafka.serde.RheosAvroKafkaSerializer;
import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;

import javax.annotation.Nullable;

public class GlobalEventKafkaSerializationSchema extends RheosKafkaSerializationSchema<GlobalEvent> {

    private final RheosKafkaProducerConfig rheosKafkaConfig;
    private transient KafkaSerializer<GlobalEvent> rheosKafkaSerializer;

    public GlobalEventKafkaSerializationSchema(RheosKafkaProducerConfig rheosKafkaConfig, String... keys) {
        super(rheosKafkaConfig, GlobalEvent.class, keys);
        this.rheosKafkaConfig = rheosKafkaConfig;
        this.rheosKafkaSerializer = new RheosAvroKafkaSerializer<>(rheosKafkaConfig, GlobalEvent.class);
    }

    public GlobalEventKafkaSerializationSchema(RheosKafkaProducerConfig rheosKafkaConfig) {
        this(rheosKafkaConfig, "guid");
    }

    @Override
    public ProducerRecord<byte[], byte[]> serialize(GlobalEvent element, @Nullable Long timestamp) {
        if (rheosKafkaSerializer == null) {
            rheosKafkaSerializer = new RheosAvroKafkaSerializer<>(rheosKafkaConfig, GlobalEvent.class);
        }

        int pageId = element.getPageId() == null ? -1 : element.getPageId();
        Header pageIdHeader = new RecordHeader(KafkaMessageHeaders.PAGE_ID, Ints.toByteArray(pageId));

        return new ProducerRecord<>(rheosKafkaConfig.getTopic(),
                null,
                (byte[])null,
                rheosKafkaSerializer.encodeValue(element),
                Lists.newArrayList(pageIdHeader));
    }
}
