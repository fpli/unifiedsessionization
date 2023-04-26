package com.ebay.epic.soj.flink.connector.kafka.schema;

import com.ebay.epic.soj.common.constant.KafkaMessageHeaders;
import com.ebay.epic.soj.flink.connector.kafka.config.RheosKafkaProducerConfig;
import com.ebay.epic.soj.flink.connector.kafka.serde.KafkaSerializer;
import com.ebay.epic.soj.flink.connector.kafka.serde.RheosAvroKafkaSerializer;
import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.streaming.connectors.kafka.KafkaSerializationSchema;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.List;

@Slf4j
public class RheosKafkaSerializationSchema<T> implements KafkaSerializationSchema<T> {

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
        if (rheosKafkaSerializer == null) {
            rheosKafkaSerializer = new RheosAvroKafkaSerializer<>(rheosKafkaConfig, clazz);
        }
        Field field = null;
        String globalSessionId=null;
        try {
            field = element.getClass().getDeclaredField(KafkaMessageHeaders.GLOBAL_SESSION_ID);
            field.setAccessible(true);
            globalSessionId = String.valueOf(field.get(element));

        } catch (Exception e) {
            log.error("Get field[{}] value error", KafkaMessageHeaders.GLOBAL_SESSION_ID, e);
        }
        Header gSessionIdHeader = new RecordHeader(KafkaMessageHeaders.GLOBAL_SESSION_ID,
                globalSessionId.getBytes(Charsets.UTF_8));
        return new ProducerRecord<>(
                rheosKafkaConfig.getTopic(),
                null,
                rheosKafkaSerializer.encodeKey(element, keys),
                rheosKafkaSerializer.encodeValue(element),Lists.newArrayList(gSessionIdHeader)

        );
    }
}
