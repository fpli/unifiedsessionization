package com.ebay.epic.business.normalizer.global.event.ubi;

import com.ebay.epic.business.constant.ubi.domain.Field;
import com.ebay.epic.business.constant.ubi.domain.Tag;
import com.ebay.epic.business.normalizer.FieldNormalizer;
import com.ebay.epic.common.constant.DataSrc;
import com.ebay.epic.common.constant.EventLevel;
import com.ebay.epic.common.constant.EventPrimaryAsset;
import com.ebay.epic.common.constant.EventTrigger;
import com.ebay.epic.sojourner.common.constant.*;
import com.ebay.epic.common.model.avro.GlobalEvent;
import com.ebay.epic.common.model.avro.SojEvent;
import com.ebay.epic.utils.ExtractTag;
import org.apache.commons.lang3.StringUtils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import static com.ebay.epic.utils.MapPutIf.putIfNotBlankStr;
import static com.ebay.epic.utils.MapPutIf.putIfNumericStr;

public class CommonNormalizer extends FieldNormalizer<SojEvent, GlobalEvent> {

    private DateTimeFormatter dtf;

    @Override
    public void init() throws Exception {
        super.init();
        dtf = DateTimeFormatter.ISO_LOCAL_DATE.withZone(ZoneId.ofOffset("GMT", ZoneOffset.ofHours(-7)));
    }

    @Override
    public void normalize(SojEvent src, GlobalEvent tar) throws Exception {
        //Partition key
        tar.setDataSrc(DataSrc.SOJOURNER);
        tar.setEventTrigger(EventTrigger.EBAY_SERVED);
        tar.setEventLevel(EventLevel.PAGE);
        tar.setEventPrimaryAsset(EventPrimaryAsset.OTHER);

        //General info
        tar.setEventTimestamp(src.getEventTimestamp());
        tar.setDt(dtf.format(Instant.ofEpochMilli(src.getEventTimestamp())));
        tar.setPayload(src.getApplicationPayload());
        tar.setPageId(src.getPageId());
        try {
            String siteId = src.getSiteId();
            if (StringUtils.isNumeric(siteId)) {
                tar.setSiteId(Integer.valueOf(siteId));
            }
        } catch (NumberFormatException ignored) {
        }

        tar.setUserLang(ExtractTag.extract(src.getApplicationPayload(), Tag.T_UL));

        putIfNumericStr(tar.getTrafficSrc(), Field.F_CHANNEL_ID, ExtractTag.extract(src.getApplicationPayload(), Tag.T_MKCID));

        {
            String extract = ExtractTag.extract(src.getApplicationPayload(), Tag.T_MKRID);
            if (StringUtils.isNoneEmpty(extract)) {
                putIfNumericStr(tar.getTrafficSrc(), Field.F_ROTATION_ID, extract.replace("-", ""));
            }
        }

        //Client info
//        tar.setAppId(src.getAppId());
//        tar.setAppVersion(src.getAppVersion());
        tar.setUserAgent(src.getAgentInfo());
        tar.setReferer(src.getReferrer());
        tar.setGuid(src.getGuid());
        tar.setSessionId(src.getSessionId());

        tar.setCurrentImpressionId(ExtractTag.extract(src.getApplicationPayload(), Tag.T_CIID));

        try {
            String userId = src.getUserId();
            if (StringUtils.isNumeric(userId)) {
                tar.setUserId(Long.parseLong(userId));
            }
        } catch (NumberFormatException ignored) {
        }

        try {
            String width = ExtractTag.extract(src.getApplicationPayload(), Tag.T_WINDOW_WIDTH);
            if (StringUtils.isNumeric(width)) {
                tar.setScreenWidth(Integer.valueOf(width));
            }
        } catch (NumberFormatException ignored) {
        }

        try {
            String height = ExtractTag.extract(src.getApplicationPayload(), Tag.T_WINDOW_HEIGHT);
            if (StringUtils.isNumeric(height)) {
                tar.setScreenHeight(Integer.valueOf(height));
            }
        } catch (NumberFormatException ignored) {
        }

        //Item info
        try {
            String itemId = src.getItemId();
            if (StringUtils.isNumeric(itemId)) {
                tar.setItemId(Long.parseLong(itemId));
            }
        } catch (NumberFormatException ignored) {
        }

        //Session info
//        try {
//            String seqNum = src.getSeqNum();
//            if (StringUtils.isNumeric(seqNum)) {
////                tar.setSeqNum(Integer.valueOf(seqNum));
//            }
//        } catch (NumberFormatException ignored) {
//        }
//        tar.setSessionSkey(src.getSessionSkey().toString());
//        tar.setSessionStartTimestamp(src.getSessionStartDt());

        tar.setElementId(ExtractTag.extract(src.getApplicationPayload(), Tag.T_MODULE_DTL));

        //Biz Dtl
        putIfNotBlankStr(tar.getBizDtl(), Field.F_EVENT_ACTION, src.getEventAction());
        putIfNotBlankStr(tar.getBizDtl(), Field.F_EVENT_FAMILY, src.getEventFamily());
        putIfNotBlankStr(tar.getBizDtl(), Field.F_SID, src.getSid());
        putIfNotBlankStr(tar.getBizDtl(), Field.F_PAGE_CI, ExtractTag.extract(src.getApplicationPayload(), Tag.T_PAGE_CI));
        putIfNotBlankStr(tar.getBizDtl(), Field.F_CALLING_PAGE_ID, ExtractTag.extract(src.getApplicationPayload(), Tag.T_CP)); // Search, A2C, Watch
        putIfNotBlankStr(tar.getBizDtl(), Field.F_CALLING_PAGE_NAME, ExtractTag.extract(src.getApplicationPayload(), Tag.T_CALLING_PAGE_NAME)); // Search, A2C, Watch
        putIfNotBlankStr(tar.getBizDtl(), Field.F_DESKTOP_AGENT_FLG, ExtractTag.extract(src.getApplicationPayload(), Tag.T_DSKTOP)); // Search, A2C, Watch
        putIfNotBlankStr(tar.getBizDtl(), Field.F_NATIVE_APP_FLG, ExtractTag.extract(src.getApplicationPayload(), Tag.T_NATIVE_APP));
        putIfNotBlankStr(tar.getBizDtl(), Field.F_DEVICE_ADVERTISING_OPT_OUT, ExtractTag.extract(src.getApplicationPayload(), Tag.T_DEVICE_ADVERTISING_OPT_OUT));
    }

}
