package com.ebay.epic.business.filter;

import org.apache.commons.lang3.StringUtils;

public abstract class CombinationFilter<T> implements IFilter<T> {

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj instanceof CombinationFilter) {
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
