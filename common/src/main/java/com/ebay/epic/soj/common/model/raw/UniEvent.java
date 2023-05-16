package com.ebay.epic.soj.common.model.raw;

import com.ebay.epic.soj.common.constant.Constants;
import com.ebay.epic.soj.common.enums.Category;
import com.ebay.epic.soj.common.enums.EventType;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
@Slf4j
public class UniEvent {
    private String guid;
    private String userId;
    private Long eventTs;
    private String sessionId;
    private Long sessionSkey;
    private Category category;
    private EventType eventType;
    private String globalSessionId = Constants.NO_SESSION_ID;
    private byte[] rheosByteArray;
    private List<Integer> botFlags;
    // for sessionstartdt
    private byte rdt;
    private Boolean iframe;
    private Map<String, Object> utpTs = new ConcurrentHashMap<>();
    private Map<String, Object> nonUtpTs = new ConcurrentHashMap<>();
    // collect some metrics for monitor and validation
    private Long ingestTimestamp;
    private Long kafkaReceivedTimestamp;

    // for traffic source
    private String entityType;
    private String referer;
    // both for traffic source and Clav session extension
    // See TrafficSourceConstants for available applicationPayload keys
    private Map<String,String> payload = new HashMap<>();
    private String pageUrl;
    private String experience;
    private boolean partialValidPage = true;
    private Integer pageId = null;
    private String siteId = null;
    private String cobrand = null;
    private String appId = null;
    private String userAgent = null;
    private boolean clavValidPage = false;


    public boolean isNewSession() {
        return Constants.NO_SESSION_ID.equals(globalSessionId);
    }

    public void updateGlobalSessionId() {
        try {
            this.globalSessionId = concatTimestamp(this.guid, this.eventTs);
        } catch (Exception e) {
            log.error(" update globalSessionId error:{}", e);
            log.error(" update globalSessionId error details:{}", this.toString());
        }
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
