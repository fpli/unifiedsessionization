package com.ebay.epic.flink.connector.kafka.schema;

import com.ebay.epic.common.model.raw.RawEvent;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.serialization.DeserializationSchema.InitializationContext;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.connectors.kafka.KafkaDeserializationSchema;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.HashSet;
import java.util.Set;

public class RawEventKafkaDeserializationSchemaWrapper implements
    KafkaDeserializationSchema<RawEvent> {

  private Set<String> skewGuids = new HashSet<>();
  private final DeserializationSchema<RawEvent> rawEventDeserializationSchema;

  public RawEventKafkaDeserializationSchemaWrapper(Set<String> skewGuids,
                                                   DeserializationSchema rawEventDeserializationSchema) {
    this.skewGuids = skewGuids;
    this.rawEventDeserializationSchema = rawEventDeserializationSchema;
  }
  public RawEventKafkaDeserializationSchemaWrapper(
          DeserializationSchema rawEventDeserializationSchema) {
    this.rawEventDeserializationSchema = rawEventDeserializationSchema;
  }

  @Override
  public void open(InitializationContext context) throws Exception {
    rawEventDeserializationSchema.open(context);
  }

  @Override
  public boolean isEndOfStream(RawEvent nextElement) {
    return false;
  }

  @Override
  public RawEvent deserialize(ConsumerRecord<byte[], byte[]> record) throws Exception {
    if (record.key() != null && skewGuids.contains(new String(record.key()))) {
      return null;
    } else {
      Long produceTimestamp = record.timestamp();
      RawEvent rawEvent= rawEventDeserializationSchema.deserialize(record.value());
      rawEvent.setKafkaReceivedTimestamp(produceTimestamp);
      return rawEvent;
    }
  }

  @Override
  public TypeInformation<RawEvent> getProducedType() {
    return TypeInformation.of(RawEvent.class);
  }
}
