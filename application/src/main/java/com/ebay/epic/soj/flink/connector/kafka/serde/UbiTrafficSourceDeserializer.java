package com.ebay.epic.soj.flink.connector.kafka.serde;

import com.ebay.epic.soj.common.model.raw.RawEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.generic.GenericRecord;

import static com.ebay.epic.soj.common.model.trafficsource.TrafficSourceConstants.UBI_FIELD_REFERRER;
import static com.ebay.epic.soj.common.model.trafficsource.TrafficSourceConstants.UBI_FIELD_URLQUERYSTRING;

@Slf4j
public class UbiTrafficSourceDeserializer {

    public void convert(GenericRecord genericRecord, RawEvent rawEvent) {
        try {
            // extract ubi event fields
            Object urlQueryString = genericRecord.get(UBI_FIELD_URLQUERYSTRING);
            if (urlQueryString != null) {
                rawEvent.setPageUrl(urlQueryString.toString());
            }
            Object referrer = genericRecord.get(UBI_FIELD_REFERRER);
            if (referrer != null) {
                rawEvent.setReferer(referrer.toString());
            }
        } catch (Exception e) {
            log.warn("failed to convert ubi event", e);
        }
    }
}
