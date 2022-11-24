package com.ebay.epic.flink.connector.kafka.config;

import com.ebay.epic.common.enums.DataCenter;
import com.ebay.epic.common.enums.EventType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.ebay.epic.utils.FlinkEnvUtils.*;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class ConfigManager {
    private DataCenter dataCenter = DataCenter.RNO;
    private @NonNull EventType eventType;
    private boolean isDrived = true;
    public static final String DEL_POINT = ".";
    public static final String DEL_SPACE = " ";
    public static final String DEL_LINE = "-";

    public String getOPName(String baseName) {
        return getValue(DEL_SPACE, baseName);
    }

    public String getOPUid(String baseName) {
        return getValue(DEL_LINE, baseName);
    }

    public String getSlotSharingGroup(String baseName) {
        return getValue(DEL_LINE, baseName);
    }

    public String getBrokers(String baseName) {
        return getList2StrValue(baseName);
    }

    public List<String> getTopics(String baseName) {
        return getListValueNODC(baseName);
    }

    private String getValue(String del, String baseName) {
        if (!isDrived) {
            return String.join(del, getStrValue(baseName));
        } else {
            return String.join(del, getStrValueNODC(baseName),
                    dataCenter.name().toLowerCase());
        }
    }

    public int getParallelism(String baseName) {
        return getIntValueNODC(baseName);
    }

    public String getStrValue(String baseName) {
        if (!isDrived) {
            return getString(baseName);
        } else {
            return getStringOrDefault(getKeyName(baseName), getString(getDefaultName(baseName)));
        }

    }

    public String getList2StrValue(String baseName) {
        if (!isDrived) {
            return getListString(baseName);
        } else {
            return getListString(getKeyName(baseName));
        }
    }

    public List<String> getListValueNODC(String baseName) {
        return getList(getKeyNameNODC(baseName));
    }

    public String getStrValueNODC(String baseName) {
        if (!isDrived) {
            return getString(baseName);
        } else {
//            return getString(getKeyNameNODC(baseName));
            return getStringOrDefault(getKeyNameNODC(baseName),
                    getStringOrDefault(getDefaultNameNODC(baseName), null));
        }
    }

    public String getStrDirect(String key) {
        return getString(key);
    }

    public Integer getIntValue(String baseName) {
        if (!isDrived) {
            return getInteger(baseName);
        } else {
            return getIntegerOrDefault(getKeyName(baseName),
                    getIntegerOrDefault(getDefaultName(baseName), null));
        }
    }

    public Integer getIntValueNODC(String baseName) {
        if (!isDrived) {
            return getInteger(baseName);
        } else {
            return getIntegerOrDefault(getKeyNameNODC(baseName),
                    getIntegerOrDefault(getDefaultNameNODC(baseName), null));
        }
    }

    private String getKeyName(String baseName) {
        return String.join(DEL_POINT, baseName,
                this.dataCenter.name().toLowerCase(),
                this.eventType.getValue());
    }

    private String getKeyNameNODC(String baseName) {
        return String.join(DEL_POINT, baseName,
                this.eventType.getValue());
    }

    private String getDefaultName(String baseName) {
        return String.join(DEL_POINT, baseName,
                this.dataCenter.name().toLowerCase(),
                EventType.DEFAULT.getValue());
    }

    private String getDefaultNameNODC(String baseName) {
        return String.join(DEL_POINT, baseName,
                EventType.DEFAULT.getValue());
    }
}
