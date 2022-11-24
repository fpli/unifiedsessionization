package com.ebay.epic.business.normalizer;

import com.ebay.epic.common.model.raw.RawEvent;
import com.ebay.epic.common.model.raw.UniEvent;
import com.google.common.collect.Sets;
import org.apache.avro.generic.GenericRecord;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CommonNormalizer extends FieldNormalizer<RawEvent, UniEvent> {

    @Override
    public void normalize(RawEvent src, UniEvent tar) throws Exception {
        tar.setEventTs(src.getEventTs());
        tar.setEventType(src.getEventType());
        tar.setGuid(src.getGuid());
        tar.setIngestTimestamp(src.getIngestTimestamp());
        tar.setGlobalSessionId(src.getGlobalSessionId());
        tar.setRheosByteArray(src.getRheosByteArray());
        tar.setKafkaReceivedTimestamp(src.getKafkaReceivedTimestamp());
    }
}
