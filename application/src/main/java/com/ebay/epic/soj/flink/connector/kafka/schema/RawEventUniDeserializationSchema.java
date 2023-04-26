package com.ebay.epic.soj.flink.connector.kafka.schema;

import com.ebay.epic.common.constant.Constants;
import com.ebay.epic.common.enums.SchemaSubject;
import com.ebay.epic.common.model.raw.RawEvent;
import com.ebay.epic.flink.connector.kafka.factory.RawEventDeserFactory;
import com.ebay.epic.flink.connector.kafka.factory.RheosEventSerdeFactory;
import com.google.common.collect.Sets;
import io.ebay.rheos.schema.event.RheosEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.generic.GenericRecord;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.metrics.Counter;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;
import java.util.Date;
import java.util.Set;

@Slf4j
public class RawEventUniDeserializationSchema implements DeserializationSchema<RawEvent> {
    private String schemaRegistryUrl = null;
    private static final String G_TAG = "guid";
    private transient Counter droppedEventCounter;
    private String subjectName;
    private static Set<Integer> appidWhiteList = Sets.newHashSet(35024, 35023);
    private transient RawEventDeserFactory rawEventDeserFactory;

    public RawEventUniDeserializationSchema(String schemaRegistryUrl, String subjectName) {
        this.schemaRegistryUrl = schemaRegistryUrl;
        this.subjectName = subjectName;
    }

    @Override
    public void open(InitializationContext context) throws Exception {
        this.droppedEventCounter = context.getMetricGroup()
                .addGroup(Constants.SOJ_METRIC_TYPE)
                .counter(Constants.SOJ_METRIC_DROPPED_EVENT_CNT);
        this.rawEventDeserFactory = new RawEventDeserFactory(this.schemaRegistryUrl);
    }

    @Override
    public RawEvent deserialize(byte[] message) throws IOException {
        long ingestTime = new Date().getTime();
        RawEvent rawEvent = null;
        try {
            rawEvent = (RawEvent) rawEventDeserFactory.getDeserializer(SchemaSubject.of(subjectName)).decodeValue(message);
        } catch (Exception e) {
            droppedEventCounter.inc();
            return null;
        }
        rawEvent.setIngestTimestamp(ingestTime);
        return rawEvent;
    }

    @Override
    public boolean isEndOfStream(RawEvent nextElement) {
        return false;
    }

    @Override
    public TypeInformation<RawEvent> getProducedType() {
        return TypeInformation.of(RawEvent.class);
    }

}

