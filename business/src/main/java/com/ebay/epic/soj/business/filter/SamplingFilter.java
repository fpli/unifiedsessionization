package com.ebay.epic.soj.business.filter;

import com.ebay.epic.soj.common.model.raw.RawEvent;
import com.ebay.epic.soj.common.utils.SOJSampleHash;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;

public class SamplingFilter extends CombinationFilter<RawEvent> {
    public static final int MOD_VALUE = 1000;
    public static final float FETCH_BIT = 10;

    public final String[] samplingKeys;
    public final float samplingPct;

    public SamplingFilter(String[] samplingKeys, float samplingPct) {
        this.samplingKeys = samplingKeys;
        this.samplingPct = samplingPct;
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
