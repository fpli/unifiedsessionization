package com.ebay.epic.soj.common.model.raw;

import com.ebay.epic.soj.common.constant.Constants;
import com.ebay.epic.soj.common.enums.Category;
import com.ebay.epic.soj.common.enums.EventType;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class RawEvent {
    private String guid;
    private String userId;
    private String siteId;
    private Long eventTs;
    private EventType eventType;
    private Integer pageId = 0;
    private Category category;
    private byte rdt;
    private Boolean iframe;
    private String globalSessionId = Constants.NO_SESSION_ID;
    private byte[] rheosByteArray;
    private Long ingestTimestamp;
    private Long kafkaReceivedTimestamp;
    private Map<String, Object> utpTs = new HashMap<>();
    private Map<String, Object> nonUtpTs;
//    private GenericRecord genericRecord;
    private String sessionId;
    private Long sessionSkey;
    private List<Integer> botFlags;

    // for traffic source
    private String entityType;
    private String referer;
    private Map<String,String> payload = new HashMap<>();
    private String pageUrl;
    private String experience;
    private String cobrand;
    private String appId;
    private String userAgent;
    private String clientData;
    private String sqr;


    public boolean isNewSession() {
        return Constants.NO_SESSION_ID.equals(globalSessionId);
    }

    public void updateGlobalSessionId() {
        this.globalSessionId = concatTimestamp(this.guid, this.eventTs);
    }

    private String concatTimestamp(String prefix, long timestamp) {
        long unixTimestamp = timestamp;
        int prefixLen = 0;
        if (!StringUtils.isBlank(prefix)) {
            prefixLen = prefix.length();
        } else {
            prefix = "";
        }
        StringBuilder builder = new StringBuilder(prefixLen + 16);
        builder.append(prefix);
        String x = Long.toHexString(unixTimestamp);
        for (int i = 16 - x.length(); i > 0; i--) {
            builder.append('0');
        }
        builder.append(x);
        return builder.toString();
    }
}
