package com.ebay.epic.soj.flink.connector.kafka.config;

import com.ebay.epic.soj.common.enums.DataCenter;
import com.ebay.epic.soj.common.enums.EventType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.ebay.epic.soj.flink.utils.FlinkEnvUtils.*;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class ConfigManager {
    private DataCenter dataCenter = DataCenter.RNO;
    private EventType eventType;
    private boolean isDrived = true;
    public static final String DEL_POINT = ".";
    public static final String DEL_SPACE = " ";
    public static final String DEL_LINE = "-";
    public String getOPName(String baseName) {
        return getValue(DEL_LINE, baseName);
    }
    public String getOPNameNODC(String baseName) {
        return getValueNODC(DEL_LINE, baseName);
    }
    public String getOPUid(String baseName) {
        return getValueNODC(DEL_LINE, baseName);
    }
    public String getSlotSharingGroup(String baseName) {
        return getValueNODC(DEL_LINE, baseName);
    }

    public String getSlotSharingGroupNoPF(String baseName) {
        return getValue(baseName);
    }

    public String getBrokers(String baseName) {
        return getList2StrValue(baseName);
    }

    public String getBrokersWithFN(String baseName) {
        if(getList2StrValueWithFN(baseName)!=null) {
            return getList2StrValueWithFN(baseName);
        }else{
            return getBrokers(baseName);
        }
    }

    public List<String> getTopics(String baseName) {
        return getListValueNODC(baseName);
    }

    public String getTopic(String baseName, String category) {
        return getStrValueNODC(baseName,category);
    }

    public String getTopicSubject(String baseName) {
        return getStrValueNODC(baseName);
    }

    public String getTopicSubjectOR(String baseName,EventType eventType) {
        EventType eventTypetmp = this.eventType;
        this.eventType=eventType;
        String subject= getStrValueNODC(baseName);
        this.eventType=eventTypetmp;
        return subject;
    }

    public int getParallelism(String baseName) {
        return getIntValueNODC(baseName);
    }

    public int getParallelism(String baseName,String postfix) {
        return getIntValueNODC(baseName,postfix);
    }

    private String getValue(String del, String baseName) {
        if (!isDrived) {
            return String.join(del, getStrValue(baseName));
        } else {
            return String.join(del, getStrValueNODC(baseName),
                    dataCenter.name().toLowerCase(),constructPostFix(DEL_LINE));
        }
    }

    private String getValueNODC(String del, String baseName) {
        if (!isDrived) {
            return String.join(del, getStrValue(baseName));
        } else {
            return String.join(del, getStrValueNODC(baseName),
                    constructPostFix(DEL_LINE));
        }
    }
    private String getValue(String baseName) {
        if (!isDrived) {
            return getStrValue(baseName);
        } else {
            return getStrValueNODC(baseName);
        }
    }

    public String getStrValue(String baseName) {
        if (!isDrived) {
            return getString(baseName);
        } else {
            return getStringOrDefault(getKeyName(baseName), getString(getDefaultName(baseName)));
        }
    }

    public String getStrValueNODC(String baseName) {
        if (!isDrived) {
            return getString(baseName);
        } else {
            return getStringOrDefault(getKeyNameNODC(baseName),
                    getStringOrDefault(getDefaultNameNODC(baseName), null));
        }
    }
    public String getStrValueNODC(String baseName,String category) {
        if (!isDrived) {
            return getString(baseName);
        } else {
            return getStringOrDefault(getKeyNameNODC(baseName,category),
                    getStringOrDefault(getDefaultNameNODC(baseName), null));
        }
    }

    public String getList2StrValue(String baseName) {
        if (!isDrived) {
            return getListString(baseName);
        } else {
            return getListString(getKeyName(baseName));
        }
    }

    public String getList2StrValueWithFN(String baseName) {
        if (!isDrived) {
            return getListString(baseName);
        } else {
            return getListString(getKeyName(baseName, constructPostFix(DEL_POINT)));
        }
    }

    public List<String> getListValueNODC(String baseName) {
        return getList(getKeyNameNODC(baseName,constructPostFix(DEL_POINT)));
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

    public Integer getIntValueNODC(String baseName,String postfix) {
        if (!isDrived) {
            return getInteger(baseName);
        } else {
            return getIntegerOrDefault(getKeyNameNODC(baseName,postfix),
                    getIntegerOrDefault(getDefaultNameNODC(baseName), null));
        }
    }
    private String getKeyName(String baseName) {
        return String.join(DEL_POINT, baseName,
                this.dataCenter.name().toLowerCase(),
                this.eventType.getName());
    }

    private String getKeyName(String baseName, String postFix) {
        return String.join(DEL_POINT, baseName,
                this.dataCenter.name().toLowerCase(),
                this.eventType.getName(), postFix);
    }

    private String getKeyNameNODC(String baseName) {
        return String.join(DEL_POINT, baseName,
                this.eventType.getName());
    }

    private String getKeyNameNODC(String baseName, String postFix) {
        return String.join(DEL_POINT, baseName,
                this.eventType.getName(), postFix);
    }

    private String getDefaultName(String baseName) {
        return String.join(DEL_POINT, baseName,
                this.dataCenter.name().toLowerCase(),
                EventType.DEFAULT.getName());
    }

    private String getDefaultNameNODC(String baseName) {
        return String.join(DEL_POINT, baseName,
                EventType.DEFAULT.getName());
    }
    public String constructPostFix(String del) {
        return String.join(del, eventType.getCategory().toLowerCase()
                , eventType.getBotType().toLowerCase());
    }
}

