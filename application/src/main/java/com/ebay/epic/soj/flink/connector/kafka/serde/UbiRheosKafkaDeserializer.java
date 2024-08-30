package com.ebay.epic.soj.flink.connector.kafka.serde;

import com.ebay.epic.soj.common.model.raw.RawEvent;
import com.ebay.epic.soj.common.model.trafficsource.TrafficSourceConstants;
import com.google.common.collect.ImmutableSet;
import io.ebay.rheos.schema.event.RheosEvent;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.util.Utf8;
import org.apache.parquet.filter2.predicate.Operators;

import java.util.*;

public class UbiRheosKafkaDeserializer extends RheosKafkaDeserializer<RawEvent> {

    private UbiTrafficSourceDeserializer ubiTrafficSourceDeserializer = new UbiTrafficSourceDeserializer();
    private static List<String> payloadKeyList = Arrays.asList("page", "pfn", "cflgs", "an", "av", "in", "mr", "state",
            "app", "sHit", "cguidsrc", "eactn", "rdthttps", "mav", "efam", "gf", "n", "bu",
            TrafficSourceConstants.PAYLOAD_KEY_CHNL, TrafficSourceConstants.PAYLOAD_KEY_ROTID, TrafficSourceConstants.PAYLOAD_KEY_URL_MPRE,
            TrafficSourceConstants.PAYLOAD_KEY_PNACT, TrafficSourceConstants.PAYLOAD_KEY_MPPID, TrafficSourceConstants.PAYLOAD_KEY_REF,
            TrafficSourceConstants.PAYLOAD_KEY_NTYPE);

    public static final Set<Integer> BOT_BLACK_LIST = ImmutableSet.of(0,220,221,222,223);

    public UbiRheosKafkaDeserializer(String schemaRegistryUrl) {
        super(schemaRegistryUrl);
    }

    @Override
    public RawEvent convert(GenericRecord genericRecord, RheosEvent rheosEvent) {
        RawEvent rawEvent = new RawEvent();
        rawEvent.setGuid(getStrOrDefault(genericRecord.get("guid"), null));
        rawEvent.setUserId(userIdCoalesce(getStrOrDefault(genericRecord.get("userId"), null), getStrOrDefault(genericRecord.get("pageId"), "0"), (Map<String, String>) genericRecord.get("applicationPayload")));
        rawEvent.setSiteId(getStrOrDefault(genericRecord.get("siteId"), null));
        rawEvent.setPageId(Integer.valueOf(getStrOrDefault(genericRecord.get("pageId"), "0")));
        rawEvent.setEventTs(Long.valueOf(getStrOrDefault(genericRecord.get("eventTimestamp"), "0")));
        rawEvent.setRheosByteArray(rheosEvent.toBytes());
        rawEvent.setSessionId(getStrOrDefault(genericRecord.get("sessionId"), null));
        rawEvent.setSessionSkey(Long.valueOf(getStrOrDefault(genericRecord.get("sessionSkey"), "-1")));
        rawEvent.setSeqNum(Integer.valueOf(getStrOrDefault(genericRecord.get("seqNum"), "-1")));
        rawEvent.setIframe(Boolean.valueOf(getStrOrDefault(genericRecord.get("iframe"), "true")));
        rawEvent.setRdt(Byte.parseByte(getStrOrDefault(genericRecord.get("rdt"), "0")));
        rawEvent.setBotFlags(removeAll((List<Integer>) genericRecord.get("botFlags"), BOT_BLACK_LIST));
        rawEvent.setCobrand(getStrOrDefault(genericRecord.get("cobrand"), null));
        rawEvent.setAppId(getStrOrDefault(genericRecord.get("appId"), null));
        //User Agent
        Map<Object, Object> genericClientData = (Map<Object, Object>) genericRecord.get("clientData");
        Object agent = genericClientData.get(new Utf8("Agent"));
        String agentStr = (agent == null ? null : agent.toString());
        rawEvent.setUserAgent(getStrOrDefault(agentStr, ""));
        rawEvent.setClientData(utfMapToString(genericClientData == null ? Collections.emptyMap() : genericClientData));
        // reduce some useless key from the original map
        rawEvent.setPayload(reducePayload((Map<Utf8, Utf8>) genericRecord.get("applicationPayload")));
        addSeqnumToPayload(rawEvent.getSeqNum(), rawEvent.getPayload());
        rawEvent.setSqr(getStrOrDefault(genericRecord.get("sqr"), null));
        rawEvent.setPageUrl(getStrOrDefault(genericRecord.get("urlQueryString").toString(), null));
        ubiTrafficSourceDeserializer.convert(genericRecord, rawEvent);
        return rawEvent;
    }

    private String getStrOrDefault(Object o, String defaultStr) {
        return o != null ? o.toString() : defaultStr;
    }

    private String userIdCoalesce(String userId, String pageId, Map<String, String> applicationPayload) {
        if (userId != null) {
            return userId;
        }
        List<String> checkoutPageIds = Arrays.asList("2508507", "2368479", "2239237", "2255925", "2056812", "2368482", "2500857", "2523519", "2546490", "2523513");
        if (checkoutPageIds.contains(pageId)) {
            try {
                String buyerId = applicationPayload.get("buyer_id");
                // Check if it is a number
                Long.parseLong(buyerId);
                return buyerId;
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    private String genericSiteId(String siteId, String webServerStr) {
        if (webServerStr == null) {
            return siteId;
        }
        if ("www.ebay.coau".equals(webServerStr) && "0".equals(siteId)) {
            return "15";
        }
        if ("in.ebay.com".equals(webServerStr)) {
            return "203";
        }
        return siteId;
    }

    private static Map<String, String> reducePayload(Map<Utf8, Utf8> applicationPayload) {
        Map<String, String> stringMap = new HashMap<>();
        for (Map.Entry entry : applicationPayload.entrySet()) {
            if (entry.getKey() != null && entry.getValue() != null && payloadKeyList.contains(entry.getKey().toString())) {
                stringMap.put(entry.getKey().toString(), entry.getValue().toString());
            }
        }
        return stringMap;
    }

    private static void addSeqnumToPayload(Integer seqnum, Map<String, String> payload) {
        payload.put("seqnum", seqnum == null ? null : seqnum.toString());
    }

    public static String utfMapToString(Map<Object, Object> sojMap) {
        StringBuilder sb = new StringBuilder();
        Iterator var2 = sojMap.entrySet().iterator();

        while (var2.hasNext()) {
            Map.Entry<Object, Object> pair = (Map.Entry) var2.next();
            sb.append(pair.getKey().toString().toLowerCase()).append("=").append(pair.getValue().toString()).append("&");
        }

        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    private Map<String, String> utf8MapToStringMap(Map<Utf8, Utf8> applicationPayload) {
        Map<String, String> stringMap = new HashMap<>();
        for (Map.Entry entry : applicationPayload.entrySet()) {
            if (entry.getKey() != null && entry.getValue() != null) {
                stringMap.put(entry.getKey().toString(), entry.getValue().toString());
            }
        }
        return stringMap;
    }

    // remove all the itermidiate bot flags
    private List<Integer> removeAll(List<Integer> botFlags, Set<Integer> botBlackList) {
        botFlags.removeAll(botBlackList);
        return botFlags;
    }
}
