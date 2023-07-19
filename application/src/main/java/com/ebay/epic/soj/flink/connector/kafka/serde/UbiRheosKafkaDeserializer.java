package com.ebay.epic.soj.flink.connector.kafka.serde;

import com.ebay.epic.soj.common.model.raw.RawEvent;
import com.ebay.sojourner.common.util.PropertyUtils;
import com.ebay.sojourner.common.util.RegexReplace;
import com.ebay.sojourner.common.util.SOJURLDecodeEscape;
import io.ebay.rheos.schema.event.RheosEvent;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.util.Utf8;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class UbiRheosKafkaDeserializer extends RheosKafkaDeserializer<RawEvent> {

    private UbiTrafficSourceDeserializer ubiTrafficSourceDeserializer =
            new UbiTrafficSourceDeserializer();

    public UbiRheosKafkaDeserializer(String schemaRegistryUrl) {
        super(schemaRegistryUrl);
    }

    @Override
    public RawEvent convert(GenericRecord genericRecord, RheosEvent rheosEvent) {
        RawEvent rawEvent = new RawEvent();
        rawEvent.setGuid(getStrOrDefault(genericRecord.get("guid"), null));
        rawEvent.setUserId(getStrOrDefault(genericRecord.get("userId"), null));
        rawEvent.setSiteId(getStrOrDefault(genericRecord.get("siteId"), null));
        rawEvent.setPageId(Integer.valueOf(getStrOrDefault(genericRecord.get("pageId"), "0")));
        rawEvent.setEventTs(Long.valueOf(getStrOrDefault(genericRecord.get("eventTimestamp"), "0")));
        rawEvent.setRheosByteArray(rheosEvent.toBytes());
        rawEvent.setSessionId(getStrOrDefault(genericRecord.get("sessionId"), null));
        rawEvent.setSessionSkey(Long.valueOf(getStrOrDefault(genericRecord.get("sessionSkey"), "-1")));
        rawEvent.setIframe(Boolean.valueOf(getStrOrDefault(genericRecord.get("iframe"), "true")));
        rawEvent.setRdt(Byte.parseByte(getStrOrDefault(genericRecord.get("rdt"), "0")));
        rawEvent.setBotFlags((List) genericRecord.get("botFlags"));
        rawEvent.setCobrand(getStrOrDefault(genericRecord.get("cobrand"), null));
        rawEvent.setAppId(getStrOrDefault(genericRecord.get("appId"), null));
        //User Agent
        Map<Object, Object> genericClientData = (Map<Object, Object>)genericRecord.get("clientData");
        Object agent = genericClientData.get(new Utf8("Agent"));
        String agentStr = (agent == null ? null:agent.toString());
        rawEvent.setUserAgent(getStrOrDefault(agentStr, ""));
        rawEvent.setClientData(utfMapToString(genericClientData == null ? Collections.emptyMap() : genericClientData));

        rawEvent.setPayload(utf8MapToStringMap((Map<Utf8, Utf8>) genericRecord.get("applicationPayload")));
        rawEvent.setSqr(getStrOrDefault(genericRecord.get("sqr"), null));
        rawEvent.setPageUrl(getStrOrDefault(genericClientData.get("urlQueryString"), null));
        ubiTrafficSourceDeserializer.convert(genericRecord, rawEvent);
        return rawEvent;
    }
    private String getStrOrDefault(Object o, String defaultStr) {
        return o != null ? o.toString() : defaultStr;
    }


    public static String utfMapToString(Map<Object, Object> sojMap) {
        StringBuilder sb = new StringBuilder();
        Iterator var2 = sojMap.entrySet().iterator();

        while(var2.hasNext()) {
            Map.Entry<Object, Object> pair = (Map.Entry)var2.next();
            sb.append(pair.getKey().toString().toLowerCase()).append("=").append(pair.getValue().toString()).append("&");
        }

        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
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

    private Map<String, String> utf8MapToStringMap(Map<Utf8, Utf8> applicationPayload) {
        Map<String, String> stringMap = new HashMap<>();
        for(Map.Entry entry : applicationPayload.entrySet()) {
            if (entry.getKey() != null && entry.getValue() != null) {
                stringMap.put(entry.getKey().toString(), entry.getValue().toString());
            }
        }
        return stringMap;
    }
}
