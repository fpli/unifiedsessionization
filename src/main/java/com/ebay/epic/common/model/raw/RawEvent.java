package com.ebay.epic.common.model.raw;

import com.ebay.epic.common.constant.Constants;
import com.ebay.epic.common.enums.EventType;
import com.ebay.epic.utils.SojTimestamp;
import lombok.Data;
import org.apache.avro.generic.GenericRecord;
import org.apache.commons.lang.StringUtils;

import java.util.Map;

@Data
public class RawEvent {
    private String guid;
    private Long eventTs;
    private EventType eventType;
    private Integer pageId;
    private String globalSessionId = Constants.NO_SESSION_ID;
    private byte[] rheosByteArray;
    private Long ingestTimestamp;
    private Long kafkaReceivedTimestamp;
    private Map<String, Object> utpTs;
    private Map<String, Object> nonUtpTs;
//    private GenericRecord genericRecord;
    private String sessionId;
    private Long sessionSkey;

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
