package com.ebay.epic.flink.connector.kafka.schema;

import com.ebay.epic.common.constant.Constants;
import com.ebay.epic.common.enums.EventType;
import com.ebay.epic.common.enums.SchemaSubject;
import com.ebay.epic.common.model.RawEvent;
import com.ebay.epic.flink.connector.kafka.factory.RheosEventSerdeFactory;
import com.ebay.epic.utils.SojTimestamp;
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
    private SchemaSubject subjectName;
    private static Set<Integer> appidWhiteList = Sets.newHashSet(35024, 35023);
    private static final DateTimeFormatter formaterUtc =
            DateTimeFormat.forPattern(Constants.DEFAULT_TIMESTAMP_FORMAT)
                    .withZone(
                            DateTimeZone.forTimeZone(Constants.UTC_TIMEZONE));
    private static final DateTimeFormatter formater = DateTimeFormat.forPattern(
                    Constants.DEFAULT_TIMESTAMP_FORMAT)
            .withZone(
                    DateTimeZone.forTimeZone(Constants.PST_TIMEZONE));
    private static final DateTimeFormatter dateMinsFormatter = DateTimeFormat.forPattern(
                    Constants.DEFAULT_DATE_MINS_FORMAT)
            .withZone(
                    DateTimeZone.forTimeZone(Constants.PST_TIMEZONE));

    public RawEventUniDeserializationSchema(String schemaRegistryUrl, SchemaSubject subjectName) {
        this.schemaRegistryUrl = schemaRegistryUrl;
        this.subjectName = subjectName;
    }

    @Override
    public void open(InitializationContext context) throws Exception {
        this.droppedEventCounter = context.getMetricGroup()
                .addGroup(Constants.SOJ_METRIC_TYPE)
                .counter(Constants.SOJ_METRIC_DROPPED_EVENT_CNT);
    }

    @Override
    public RawEvent deserialize(byte[] message) throws IOException {
        long ingestTime = new Date().getTime();
        RheosEvent rheosEvent =
                RheosEventSerdeFactory.getRheosEventHeaderDeserializer().deserialize(null, message);
        GenericRecord genericRecord = null;
        try {
            genericRecord = RheosEventSerdeFactory.getRheosEventDeserializer(schemaRegistryUrl)
                    .decode(rheosEvent);
        } catch (Exception e) {
            log.error("Error when deserializing RawEvent from source, schemaId: {}, timestamp: {}",
                    rheosEvent.getSchemaId(), rheosEvent.getEventCreateTimestamp(), e);
            droppedEventCounter.inc();
            return null;
        }
        RawEvent rawEvent = deserialize(genericRecord, rheosEvent);
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

    private RawEvent deserialize(GenericRecord genericRecord, RheosEvent rheosEvent) {
        RawEvent rawEvent = null;
        switch (subjectName) {
            case SURFACE_NATIVE:
            case SURFACE: {
                rawEvent = surfaceConstruct(genericRecord, rheosEvent);
                break;
            }
            case UBI: {
                rawEvent = ubiConstruct(genericRecord, rheosEvent);
                break;
            }
            case UTP: {
                rawEvent = utpConstruct(genericRecord, rheosEvent);
                break;
            }
            default:
                break;
        }
        return rawEvent;
    }

    private RawEvent surfaceConstruct(GenericRecord genericRecord, RheosEvent rheosEvent) {
        RawEvent rawEvent = new RawEvent();
        rawEvent.setGuid(genericRecord.get("guid").toString());
        rawEvent.setEventTs(Long.valueOf(((GenericRecord) genericRecord.get("Activity"))
                .get("timestamp").toString()));
        rawEvent.setEventType(EventType.AUTOTRACK);
        rawEvent.setRheosByteArray(rheosEvent.toBytes());
        return rawEvent;
    }

    private RawEvent ubiConstruct(GenericRecord genericRecord, RheosEvent rheosEvent) {
        RawEvent rawEvent = new RawEvent();
        rawEvent.setGuid(genericRecord.get("guid").toString());
        rawEvent.setEventTs(SojTimestamp
                .getSojTimestampToUnixTimestamp(Long.valueOf(
                        genericRecord.get("eventTimestamp").toString())));
        rawEvent.setEventType(EventType.UBI);
        rawEvent.setRheosByteArray(rheosEvent.toBytes());
        return rawEvent;

    }

    private RawEvent utpConstruct(GenericRecord genericRecord, RheosEvent rheosEvent) {
        RawEvent rawEvent = new RawEvent();
        rawEvent.setGuid(genericRecord.get("guid") == null ? null : genericRecord.get("guid").toString());
        rawEvent.setEventTs(Long.valueOf(genericRecord.get("producerEventTs").toString()));
        rawEvent.setEventType(EventType.UTP);
        rawEvent.setRheosByteArray(rheosEvent.toBytes());
        return rawEvent;
    }
}

