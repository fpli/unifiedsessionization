package com.ebay.epic.common.enums;

public enum EventType {
    AUTOTRACK_NATIVE("autotrack"),
    AUTOTRACK("autotrack"),
    UBI("ubi"),
    UTP("utp"),
    SESSION("session"),
    DEFAULT("default");
    private String value;

    EventType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
