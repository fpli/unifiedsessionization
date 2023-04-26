package com.ebay.epic.soj.flink.connector.kafka.serde;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.Schema;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.avro.specific.SpecificRecord;
import org.apache.commons.collections.CollectionUtils;
import org.apache.kafka.common.errors.SerializationException;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class AvroKafkaSerializer<T extends SpecificRecord> implements KafkaSerializer<T> {

  private static final String FIELD_DELIM = ",";
  private final Schema schema;
  private final DatumWriter<SpecificRecord> writer;

  public AvroKafkaSerializer(Schema schema) {
    this.schema = schema;
    this.writer = new SpecificDatumWriter<>(schema);
  }

  @Override
  public byte[] encodeKey(T data, List<String> keyFields) {
    if (data == null || CollectionUtils.isEmpty(keyFields)) {
      return null;
    } else {
      Field field = null;
      List<String> valueList = new ArrayList<>();
      for (String keyName : keyFields) {
        try {
          field = data.getClass().getDeclaredField(keyName);
          field.setAccessible(true);
          Object value = field.get(data);
          valueList.add(String.valueOf(value));
        } catch (Exception e) {
          log.error("Get field[{}] value error", keyName, e);
          // throw new RuntimeException(e);
        }
      }
      return String.join(FIELD_DELIM, valueList).getBytes(StandardCharsets.UTF_8);
    }
  }

  @Override
  public byte[] encodeValue(T data) {
    try {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      byte[] serializedValue = null;
      BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(out, null);
      writer.write(data, encoder);
      encoder.flush();
      serializedValue = out.toByteArray();
      out.close();
      return serializedValue;
    } catch (Exception e) {
      throw new SerializationException("Error serializing Avro schema for schema: " +
                                           schema.getName(), e);
    }
  }
}
