package com.ebay.epic.soj.flink.connector.kafka.serde;

import com.ebay.epic.soj.common.model.raw.RawEvent;
import com.ebay.sojourner.common.util.RegexReplace;
import com.ebay.sojourner.common.util.SOJURLDecodeEscape;
import io.ebay.rheos.schema.event.RheosEvent;
import org.apache.avro.generic.GenericRecord;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;

public class UbiRheosKafkaDeserializer extends RheosKafkaDeserializer<RawEvent> {

    public UbiRheosKafkaDeserializer(String schemaRegistryUrl) {
        super(schemaRegistryUrl);
    }

    @Override
    public RawEvent convert(GenericRecord genericRecord, RheosEvent rheosEvent) {
        RawEvent rawEvent = new RawEvent();
        rawEvent.setGuid(genericRecord.get("guid").toString());
        rawEvent.setUserId(genericRecord.get("userId").toString());
        rawEvent.setSiteId(genericRecord.get("siteId").toString());
        rawEvent.setPageId(Integer.valueOf(genericRecord.get("pageId").toString()));
        rawEvent.setEventTs(Long.valueOf(genericRecord.get("eventTimestamp").toString()));
        rawEvent.setRheosByteArray(rheosEvent.toBytes());
        rawEvent.setSessionId(genericRecord.get("sessionId").toString());
        rawEvent.setSessionSkey(Long.valueOf(genericRecord.get("sessionSkey").toString()));
        rawEvent.setIframe(Boolean.valueOf(genericRecord.get("iframe").toString()));
        rawEvent.setRdt(Byte.valueOf(genericRecord.get("rdt").toString()));
        rawEvent.setBotFlags((List) genericRecord.get("botFlags"));
        rawEvent.setCobrand(genericRecord.get("cobrand").toString());
        rawEvent.setAppId(genericRecord.get("appId").toString());
        //User Agent
        GenericRecord genericClientData = (GenericRecord) genericRecord.get("clientData");
        Object agent = genericClientData.get("agent");
        rawEvent.setUserAgent(agent != null ? agent.toString() : "");
        rawEvent.setClientData(genericClientData.toString());
        rawEvent.setPayload((Map<String, String>) genericRecord.get("applicationPayload"));
        //TODO has sqr been decoded?
        rawEvent.setSqr(decodeSQR(genericRecord.get("sqr").toString()));
        return rawEvent;
    }

    private String decodeSQR(String sqr) {
        if (sqr != null && StringUtils.isNoneBlank(sqr)) {
            try {
                //different with jetstream, we will cut off when length exceed 4096,while jetstream not
                String sqrUtf8 = URLDecoder.decode(sqr, "UTF-8");
                if (sqrUtf8.length() <= 4096) {
                    return URLDecoder.decode(sqr, "UTF-8");
                } else {
                    return URLDecoder.decode(sqr, "UTF-8").substring(0, 4096);
                }
            } catch (UnsupportedEncodingException e) {
                String replacedChar = RegexReplace
                        .replace(sqr.replace('+', ' '), ".%[^0-9a-fA-F].?.", "", 1, 0, 'i');

                String replacedCharUtf8 = SOJURLDecodeEscape.decodeEscapes(replacedChar, '%');
                if (replacedCharUtf8.length() <= 4096) {
                    return SOJURLDecodeEscape.decodeEscapes(replacedChar, '%');
                } else {
                    return SOJURLDecodeEscape.decodeEscapes(replacedChar, '%').substring(0, 4096);
                }
            }
        }
        return null;
    }

}
