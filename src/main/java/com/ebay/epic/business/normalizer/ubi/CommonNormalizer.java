package com.ebay.epic.business.normalizer.ubi;

import com.ebay.epic.business.constant.ubi.domain.Field;
import com.ebay.epic.business.constant.ubi.domain.Tag;
import com.ebay.epic.business.normalizer.FieldNormalizer;
import com.ebay.epic.common.constant.DataSrc;
import com.ebay.epic.common.constant.EventLevel;
import com.ebay.epic.common.constant.EventType;
import com.ebay.epic.common.constant.SubEventType;
import com.ebay.epic.common.model.SojEvent;
import com.ebay.epic.common.model.UniTrackingEvent;
import com.ebay.epic.utils.ExtractTag;
import org.apache.commons.lang3.StringUtils;

public class CommonNormalizer extends FieldNormalizer<SojEvent, UniTrackingEvent> {

    @Override
    public void init() throws Exception {

    }

    @Override
    public void normalize(SojEvent src, UniTrackingEvent tar) throws Exception {
        //Partition key
        tar.setDataSrc(DataSrc.SOJOURNER);
        tar.setEventType(EventType.SERVE);
        tar.setEventLevel(EventLevel.PAGE);

        //General info
        tar.setEventTimestamp(src.getEventTimestamp());
        tar.setPayload(src.getApplicationPayload());
        tar.setPageId(src.getPageId());
        try {
            String siteId = src.getSiteId();
            if (StringUtils.isNoneEmpty(siteId)) {
                tar.setSiteId(Integer.valueOf(siteId));
            }
        } catch (NumberFormatException ignored) {
        }
        tar.setSubEventType(SubEventType.OTHER);
        tar.setUserLang(ExtractTag.extract(src.getApplicationPayload(), Tag.T_UL));
        try {
            String extract = ExtractTag.extract(src.getApplicationPayload(), Tag.T_MKRID);
            if (StringUtils.isNoneEmpty(extract)) {
                tar.setRotationId(Long.parseLong(extract.replace("-", "")));
            }
        } catch (NumberFormatException ignored) {
        }

        //Client info
        tar.setAppId(src.getAppId());
        tar.setAppVersion(src.getAppVersion());
        tar.setUserAgent(src.getAgentInfo());
        tar.setReferer(src.getReferrer());
        tar.setGuid(src.getGuid());

        try {
            String userId = src.getUserId();
            if (StringUtils.isNoneEmpty(userId)) {
                tar.setUserId(Long.parseLong(userId));
            }
        } catch (NumberFormatException ignored) {
        }

        tar.getScreenDtl().put(Field.F_SCREEN_WIDTH, ExtractTag.extract(src.getApplicationPayload(), Tag.T_WINDOW_WIDTH));
        tar.getScreenDtl().put(Field.F_SCREEN_HEIGHT, ExtractTag.extract(src.getApplicationPayload(), Tag.T_WINDOW_HEIGHT));
        tar.getScreenDtl().put(Field.F_SCREEN_RESOLUTION, ExtractTag.extract(src.getApplicationPayload(), Tag.T_WINDOW_HEIGHT));

        //Item info
        try {
            String itemId = src.getItemId();
            if (StringUtils.isNoneEmpty(itemId)) {
                tar.setItemId(Long.parseLong(itemId));
            }
        } catch (NumberFormatException ignored) {
        }

        //Session info
        try {
            String seqNum = src.getSeqNum();
            if (StringUtils.isNoneEmpty(seqNum)) {
                tar.setSeqNum(Integer.valueOf(seqNum));
            }
        } catch (NumberFormatException ignored) {
        }
        tar.setSessionSkey(src.getSessionSkey().toString());
        tar.setSessionStartTimestamp(src.getSessionStartDt());

        //Biz Dtl
        tar.getBizDtl().put(Field.F_EVENT_ACTION, ExtractTag.extract(src.getApplicationPayload(), Tag.T_EACTN));
        tar.getBizDtl().put(Field.F_EVENT_FAMILY, ExtractTag.extract(src.getApplicationPayload(), Tag.T_EFAM));
        tar.getBizDtl().put(Field.F_DEVICE_ADVERTISING_OPT_OUT, ExtractTag.extract(src.getApplicationPayload(), Tag.T_DEVICE_ADVERTISING_OPT_OUT));
        tar.getBizDtl().put(Field.F_SID, ExtractTag.extract(src.getApplicationPayload(), Tag.T_SID));
        tar.getBizDtl().put(Field.F_MODULE_DTL, ExtractTag.extract(src.getApplicationPayload(), Tag.T_MODULE_DTL));
        tar.getBizDtl().put(Field.F_PAGE_CI, ExtractTag.extract(src.getApplicationPayload(), Tag.T_PAGE_CI));
        tar.getBizDtl().put(Field.F_CURRENT_IMPRESSION_ID, ExtractTag.extract(src.getApplicationPayload(), Tag.T_CIID));
        tar.getBizDtl().put(Field.F_SOURCE_IMPRESSION_ID, ExtractTag.extract(src.getApplicationPayload(), Tag.T_SIID));
        tar.getBizDtl().put(Field.F_CALLING_PAGE_ID, ExtractTag.extract(src.getApplicationPayload(), Tag.T_CP)); // Search, A2C, Watch
        tar.getBizDtl().put(Field.F_CALLING_PAGE_NAME, ExtractTag.extract(src.getApplicationPayload(), Tag.T_CALLING_PAGE_NAME)); // Search, A2C, Watch
        tar.getBizDtl().put(Field.F_DESKTOP_AGENT_FLG, ExtractTag.extract(src.getApplicationPayload(), Tag.T_DSKTOP)); // Search, A2C, Watch
        tar.getBizDtl().put(Field.F_NATIVE_APP_FLG, ExtractTag.extract(src.getApplicationPayload(), Tag.T_NATIVE_APP));
    }

    @Override
    public void close() throws Exception {

    }
}
