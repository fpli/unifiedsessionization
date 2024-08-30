package com.ebay.epic.soj.business.testUtil;

import com.ebay.epic.soj.business.metric.trafficsource.TrafficSourceDetector;
import com.ebay.epic.soj.business.normalizer.ClavValidPageNormalizer;
import com.ebay.epic.soj.common.model.raw.RawEvent;
import com.ebay.epic.soj.common.model.raw.UniEvent;
import com.ebay.epic.soj.common.model.trafficsource.*;
import com.ebay.epic.soj.common.utils.UrlUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ValidPageTest {
    private static List<String> payloadKeyList = Arrays.asList("page", "pfn", "cflgs", "an", "av", "in", "mr", "state",
            "app", "sHit", "cguidsrc", "eactn", "rdthttps", "mav", "efam", "gf","spvpf",
            TrafficSourceConstants.PAYLOAD_KEY_CHNL, TrafficSourceConstants.PAYLOAD_KEY_ROTID, TrafficSourceConstants.PAYLOAD_KEY_URL_MPRE,
            TrafficSourceConstants.PAYLOAD_KEY_PNACT, TrafficSourceConstants.PAYLOAD_KEY_MPPID, TrafficSourceConstants.PAYLOAD_KEY_REF);

    @BeforeClass
    public static void init() {
    }

    @Test
    public void extractCandidate_ubi_valid() throws Exception {
        RawEvent rawEvent =new RawEvent();
        UniEvent uniEvent = new UniEvent();
        ObjectMapper mapper = new ObjectMapper();
        rawEvent.setPageId(2051248);
        String appStr = "{\"Agent\":\"ebayUserAgent/eBayIOS;6.122.0;iOS;16.6;Apple;iPhone12_1;--;414x896;2.0\",\"ContentLength\":\"869\",\"ForwardedFor\":\"67.253.137.194,23.197.194.46,10.191.206.49\",\"RemoteIP\":\"67.253.137.194\",\"Script\":\"/tracking/v1/batchtrack\",\"Server\":\"apisd.ebay.com\",\"TDuration\":\"12\",\"TMachine\":\"10.185.103.38\",\"TName\":\"Ginger.tracking.v1.batchtrack.POST\",\"TPayload\":\"corr_id_%3D1c593a297606e75b%26node_id%3Daf54cea3d715d577%26REQUEST_GUID%3D18a0e3cf-3bb0-ab96-7261-83f0fc5a0ae2%26logid%3Dt6faabwwmtuf%253C%253Dpiebgbcsqnuq%2560%25283osbh%2Aw%2560ut3440-18a0e3cf3b0-0x2346\",\"TPool\":\"r1edgetrksvc\",\"TStamp\":\"07:39:50.19\",\"TType\":\"URL\",\"corrId\":\"1c593a297606e75b\",\"nodeId\":\"af54cea3d715d577\"}";
        rawEvent.setClientData(utfMapToString(mapper.readValue(appStr, Map.class)));
        rawEvent.setSqr(null);
        rawEvent.setSiteId("0");
        String payloadStr ="{\"app\":\"1462\",\"botFlags\":\"\",\"callingpagename\":\"Foreground\",\"cp\":\"2051248\",\"deviceAdvertisingOptOut\":\"true\",\"devicetimestamp\":\"2023-08-19T14%3A39%3A50.614Z\",\"dm\":\"Apple\",\"dn\":\"iPhone12_1\",\"eactn\":\"FOREGROUND\",\"ec\":\"5\",\"efam\":\"NATIVELIFE\",\"epcalenv\":\"\",\"eprlogid\":\"t6fuuq%60%3F%3Ckuvcwpse*7vpqk%28rbpv670%3D-18a0e3caef9-0x104\",\"es\":\"0\",\"formFactor\":\"phone\",\"g\":\"006c3af71890a2db8fed16100106d6c8\",\"gitCommitId\":\"e8a57860258a1720a064724b7368f8616c66bba6\",\"h\":\"f7\",\"idfa\":\"00000000-0000-0000-0000-000000000000\",\"inputCorrelator\":\"%28Function%29\",\"isFontScalingActive\":\"false\",\"mav\":\"6.122.0\",\"mdbreftime\":\"1692455158791\",\"mnt\":\"wifi\",\"mos\":\"iOS\",\"mrollp\":\"49\",\"mtsts\":\"2023-08-19T14%3A39%3A32.265Z\",\"nativeApp\":\"true\",\"nqc\":\"CAgAAAAAACAAiAAQAACAAEIAAAAAgAgAAAAAAAAACCAQAAAIAAAAACAABAAAAEAACAAAAAEAAAAAAAAAAAAAAAAAiASAAAAAAAAgAAAAAAAAAAAAIAAAAAAAAAAAABAABgAAAAAAAIAAAAAAAAAAAAAAAAAAAAAAAEABAAAAAAAAAAAAIAAAAAAABACAAAAAAAAEAABQAAAAAAEAACAAAAAAAAAACAAAAAAEAAAAAAAAAAAAAAIAAAFAAIABAAAAAAAAAgAAAAAgAAAACgAAAAAAEAAAAAAAAAAAACAAAAAAAAQAAAAAAAAAAQIAAAgAAAAAVMoJAAAAAAAAAAAAAAAAAAgAAAAAAAAAAAAAgAAgAABAgAAAAAgAEAAAAAAAAAAEAAAAAAAAAAAAQAAAAAAAAAABAAAABQAAAlAAAAAAUAgABBAAAAAAIABAAAACAAAAAoAAAAAAABAAggAQABBAAEEAAIAAAAAAAAAAAAQUAAAIAAAAAAACAAIACAAAAwABAAABAQ**\",\"nqt\":\"CAgAAAAAACAAiAAQAACAAEIAAAAAgAgAAAAAAAAACCAQAAAIAAAAACAABAAAAEAACAAAAAEAAAAAABAAAAAAAAAAiASAAAAAAAAgAAAAAAAAAAAAIAAAAAAAAAAAABAABgAAAAAAAIAAAAAAAAAAAAAAAAAAAAAAAEABAAAAAAAAAAAAIAAAAAAABACAAAAAAAAEAABQAAAAAAEAECAAAAAAAAAACAAAAAAEAAAAAAAAAAAAAAIAAAFAAIABAAAAAAAAAgAAAAAgAAAACgAAAAAAEAAAAAAAAAAAACAAAAAAAAQAAAAAAAAAAQIAAAgAAAAAVMoJAAAAAAAAAAAAAAAAAAgAAAAAAAAAAAAAgAAgAABAgAAAAAgAEAAAAAAAAAAEAAAAAAAAAAAAQAAAAAAAAAABAAAABQAAAlAAAAAAUAgABBAAAAAAIABAAAACAAAAAoAAAAAAABAAggAQABBAAEEAAIAAAAAAAAAAAAQUAAAIAAAAAAACAAIACAAAAwABAAABAQ**\",\"osv\":\"16.6\",\"p\":\"2051248\",\"pagename\":\"Foreground\",\"res\":\"414X896\",\"rq\":\"1c593a297606e75b\",\"screenScale\":\"2.0\",\"t\":\"0\",\"theme\":\"light\",\"ts\":\"2023-08-19T14%3A39%3A32.265Z\",\"tz\":\"-04%3A00\",\"tzname\":\"America%2FNew_York\",\"u\":\"1046911659\",\"uc\":\"1\",\"uit\":\"1688679228\",\"ul\":\"en-US\",\"windowHeight\":\"896\",\"windowWidth\":\"414\"}";
        Map<String, String> payload = mapper.readValue(payloadStr, Map.class);;

        rawEvent.setPayload(reducePayload(payload));
        byte rdt=0;
        rawEvent.setRdt(rdt);
        rawEvent.setIframe(false);
        rawEvent.setPageUrl("/base/tracking/v1/batchtrack");
        rawEvent.setSeqNum(8);

        ClavValidPageNormalizer normalizer = new ClavValidPageNormalizer();
        normalizer.normalize(rawEvent,uniEvent);
//        assertThat(uniEvent.isClavValidPage()).isEqualTo(false);

    }

    private  Map<String, String> reducePayload(Map<String, String> applicationPayload) {
        return applicationPayload.entrySet().stream()
                .filter(e -> payloadKeyList.contains(e.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
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
}
