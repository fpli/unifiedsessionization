package com.ebay.epic.soj.flink.constant;

import com.ebay.epic.soj.common.model.UniSession;
import com.ebay.epic.soj.common.model.raw.RawUniSession;
import com.ebay.epic.soj.common.model.raw.UniEvent;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.util.OutputTag;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.ebay.epic.soj.common.enums.EventType.*;

public class OutputTagConstants {

    public static OutputTag<RawUniSession> sessionOutputTag =
            new OutputTag<>("session-output-tag", TypeInformation.of(RawUniSession.class));

    public static OutputTag<UniEvent> lateEventOutputTag =
            new OutputTag<>("late-event-output-tag", TypeInformation.of(UniEvent.class));

    public static OutputTag<UniEvent> mappedEventOutputTag =
            new OutputTag<>("mapped-event-output-tag", TypeInformation.of(UniEvent.class));

    public static OutputTag<UniEvent> dataSkewOutputTag =
            new OutputTag<>("skew-raw-event-output-tag", TypeInformation.of(UniEvent.class));

    public static OutputTag<UniEvent> atWEBOutputTag =
            new OutputTag<>(AUTOTRACK_WEB.getFullName(), TypeInformation.of(UniEvent.class));

    public static OutputTag<UniEvent> atNATIVEOutputTag =
            new OutputTag<>(AUTOTRACK_NATIVE.getFullName(), TypeInformation.of(UniEvent.class));

    public static OutputTag<UniEvent> ubiBOTOutputTag =
            new OutputTag<>(UBI_BOT.getFullName(), TypeInformation.of(UniEvent.class));

    public static OutputTag<UniEvent> ubiNONBOTOutputTag =
            new OutputTag<>(UBI_NONBOT.getFullName(), TypeInformation.of(UniEvent.class));

    public static OutputTag<UniEvent> utpNONBOTOutputTag =
            new OutputTag<>(UTP_NONBOT.getFullName(), TypeInformation.of(UniEvent.class));

    public static OutputTag<UniEvent> utpBOTOutputTag =
            new OutputTag<>(UTP_BOT.getFullName(), TypeInformation.of(UniEvent.class));

    public static OutputTag<UniEvent> roiNONBOTOutputTag =
            new OutputTag<>(ROI_NONBOT.getFullName(), TypeInformation.of(UniEvent.class));

    public static OutputTag<UniEvent> roiBOTOutputTag =
            new OutputTag<>(ROI_BOT.getFullName(), TypeInformation.of(UniEvent.class));

    public static Map<String, OutputTag<UniEvent>> outputTagMapMain = new ConcurrentHashMap<>();

    static {
        outputTagMapMain.put(AUTOTRACK_WEB.getFullName(), atWEBOutputTag);
        outputTagMapMain.put(AUTOTRACK_NATIVE.getFullName(), atNATIVEOutputTag);
        outputTagMapMain.put(UBI_BOT.getFullName(), ubiBOTOutputTag);
        outputTagMapMain.put(UBI_NONBOT.getFullName(), ubiNONBOTOutputTag);
        outputTagMapMain.put(UTP_NONBOT.getFullName(), utpNONBOTOutputTag);
        outputTagMapMain.put(UTP_BOT.getFullName(), utpBOTOutputTag);
        outputTagMapMain.put(ROI_NONBOT.getFullName(), roiNONBOTOutputTag);
        outputTagMapMain.put(ROI_BOT.getFullName(), roiBOTOutputTag);

    }
    public static OutputTag<UniEvent> atWEBOutputTagLate =
            new OutputTag<>(LATE_WEB.getFullName(), TypeInformation.of(UniEvent.class));

    public static OutputTag<UniEvent> atNATIVEOutputTagLate =
            new OutputTag<>(LATE_NATIVE.getFullName(), TypeInformation.of(UniEvent.class));

    public static OutputTag<UniEvent> ubiBOTOutputTagLate =
            new OutputTag<>(LATE_UBI_BOT.getFullName(), TypeInformation.of(UniEvent.class));

    public static OutputTag<UniEvent> ubiNONBOTOutputTagLate =
            new OutputTag<>(LATE_UBI_NONBOT.getFullName(), TypeInformation.of(UniEvent.class));

    public static OutputTag<UniEvent> utpNONBOTOutputTagLate =
            new OutputTag<>(LATE_UTP_NONBOT.getFullName(), TypeInformation.of(UniEvent.class));

    public static OutputTag<UniEvent> utpBOTOutputTagLate =
            new OutputTag<>(LATE_UTP_BOT.getFullName(), TypeInformation.of(UniEvent.class));

    public static OutputTag<UniEvent> roiNONBOTOutputTagLate =
            new OutputTag<>(LATE_ROI_NONBOT.getFullName(), TypeInformation.of(UniEvent.class));

    public static OutputTag<UniEvent> roiBOTOutputTagLate =
            new OutputTag<>(LATE_ROI_BOT.getFullName(), TypeInformation.of(UniEvent.class));

    public static Map<String, OutputTag<UniEvent>> outputTagMapLate = new ConcurrentHashMap<>();
    static {
        outputTagMapLate.put(AUTOTRACK_WEB.getFullName(), atWEBOutputTagLate);
        outputTagMapLate.put(AUTOTRACK_NATIVE.getFullName(), atNATIVEOutputTagLate);
        outputTagMapLate.put(UBI_BOT.getFullName(), ubiBOTOutputTagLate);
        outputTagMapLate.put(UBI_NONBOT.getFullName(), ubiNONBOTOutputTagLate);
        outputTagMapLate.put(UTP_BOT.getFullName(), utpBOTOutputTagLate);
        outputTagMapLate.put(UTP_NONBOT.getFullName(), utpNONBOTOutputTagLate);
        outputTagMapLate.put(ROI_NONBOT.getFullName(), roiNONBOTOutputTagLate);
        outputTagMapLate.put(ROI_BOT.getFullName(), roiBOTOutputTagLate);
    }

    public static OutputTag<UniSession> uniSessBotOutputTag =
            new OutputTag<>(SESSION_BOT.getFullName(), TypeInformation.of(UniSession.class));

    public static OutputTag<UniSession> uniSessNonbotOutputTag =
            new OutputTag<>(SESSION_NONBOT.getFullName(), TypeInformation.of(UniSession.class));

    public static OutputTag<UniSession> uniSessLkpBotOutputTag =
            new OutputTag<>(SESSION_LOOKUP_BOT.getFullName(), TypeInformation.of(UniSession.class));

    public static OutputTag<UniSession> uniSessLkpNonbotOutputTag =
            new OutputTag<>(SESSION_LOOKUP_NONBOT.getFullName(), TypeInformation.of(UniSession.class));

    public static Map<String, OutputTag<UniSession>> outputTagMapSess = new ConcurrentHashMap<>();

    static {
        outputTagMapSess.put(SESSION_BOT.getFullName(), uniSessBotOutputTag);
        outputTagMapSess.put(SESSION_NONBOT.getFullName(), uniSessNonbotOutputTag);
        outputTagMapSess.put(SESSION_LOOKUP_BOT.getFullName(), uniSessLkpBotOutputTag);
        outputTagMapSess.put(SESSION_LOOKUP_NONBOT.getFullName(), uniSessLkpNonbotOutputTag);

    }

}
