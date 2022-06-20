package com.ebay.epic.flink.connector.kafka.serde;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificRecord;

import java.nio.charset.StandardCharsets;

@Slf4j
public class AvroKafkaDeserializer<T extends SpecificRecord> implements KafkaDeserializer<T> {

  private final DatumReader<T> reader;

  public AvroKafkaDeserializer(Class<T> clazz) {
    this.reader = new SpecificDatumReader<>(clazz);
  }

  @Override
  public String decodeKey(byte[] data) {
    return new String(data, StandardCharsets.UTF_8);
  }

  @Override
  public T decodeValue(byte[] data) {
    try {
      BinaryDecoder decoder = DecoderFactory.get().binaryDecoder(data, null);
      return reader.read(null, decoder);
    } catch (Exception ex) {
      log.error("Cannot decode kafka message");
      throw new RuntimeException(ex);
    }
  }
}
