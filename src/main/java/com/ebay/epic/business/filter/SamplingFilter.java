package com.ebay.epic.business.filter;

import com.ebay.epic.business.filter.CombinationFilter;
import com.ebay.epic.common.model.RawEvent;
import com.ebay.epic.common.model.SojEvent;
import com.ebay.epic.utils.FlinkEnvUtils;
import com.ebay.epic.utils.Property;
import com.ebay.epic.utils.SOJSampleHash;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;

import static com.ebay.epic.utils.FlinkEnvUtils.*;

public class SamplingFilter extends CombinationFilter<RawEvent> {
    public static final int MOD_VALUE = 1000;
    public static final float FETCH_BIT = 10;

    public final String[] samplingKeys;
    public final float samplingPct;

    public SamplingFilter() {
        samplingKeys = getStringArray(Property.FLINK_APP_SINK_SAMPLING_KEY, ",");
        samplingPct = getFloat(Property.FLINK_APP_SINK_SAMPLING_PCT);
    }

    @Override
    public boolean filter(RawEvent rawEvent) throws Exception {
        StringBuilder sb = new StringBuilder();
        for (String key : samplingKeys) {
            Field keyField = rawEvent.getClass().getDeclaredField(key);
            keyField.setAccessible(true);
            Object value = keyField.get(rawEvent);
            sb.append(value);
        }
        if (StringUtils.isEmpty(sb)) {
            return false;
        }
        String keys = sb.toString();
        float modValue = SOJSampleHash.sampleHash(keys, MOD_VALUE) / FETCH_BIT;
        return modValue < samplingPct;
    }

}
