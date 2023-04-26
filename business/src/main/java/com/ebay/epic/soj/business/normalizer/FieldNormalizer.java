package com.ebay.epic.soj.business.normalizer;

import org.apache.commons.lang3.StringUtils;

public abstract class FieldNormalizer<Source, Target> implements INormalizer<Source, Target> {
    public boolean accept(Source src){
        return true;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj instanceof FieldNormalizer) {
            return equalsStr(this.getClass().getCanonicalName(), obj.getClass().getCanonicalName());
        }
        return false;
    }

    private boolean equalsStr(String str1, String str2) {
        if (StringUtils.isEmpty(str1) && StringUtils.isEmpty(str2)) {
            return true;
        }
        return !StringUtils.isEmpty(str1) && str1.equals(str2);
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + this.getClass().getCanonicalName().hashCode();
        return result;
    }
}
