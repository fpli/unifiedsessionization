package com.ebay.epic.soj.business.normalizer;

import com.ebay.epic.soj.common.model.raw.RawEvent;
import com.ebay.epic.soj.common.model.raw.UniEvent;
import com.google.common.collect.Sets;
import org.apache.avro.generic.GenericRecord;

public class CommonNormalizer extends FieldNormalizer<RawEvent, UniEvent> {

    @Override
    public void normalize(RawEvent src, UniEvent tar) throws Exception {
        tar.setEventTs(src.getEventTs());
        tar.setEventType(src.getEventType());
        tar.setGuid(src.getGuid());
        tar.setUserId(src.getUserId());
        tar.setSiteId(src.getSiteId());
        tar.setIngestTimestamp(src.getIngestTimestamp());
        tar.setGlobalSessionId(src.getGlobalSessionId());
        tar.setRheosByteArray(src.getRheosByteArray());
        tar.setKafkaReceivedTimestamp(src.getKafkaReceivedTimestamp());
        tar.setRdt(src.getRdt());
        tar.setIframe(src.getIframe());
        tar.setCategory(src.getCategory());
        tar.setBotFlags(src.getBotFlags());
        tar.setCobrand(src.getCobrand());
        tar.setSiteId(src.getSiteId());
        tar.setUserAgent(src.getUserAgent());
        tar.setPageId(src.getPageId());
        tar.setAppId(src.getAppId());
    }
}
