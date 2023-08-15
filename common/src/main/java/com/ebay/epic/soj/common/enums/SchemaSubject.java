package com.ebay.epic.soj.common.enums;

import avro.shaded.com.google.common.base.Preconditions;

public enum SchemaSubject {
    AUTOTRACK("sessionizedevent"),
    UBI("behavior.sojourner.sojevent.schema"),
    UTP("marketing.tracking.events.schema"),
    SESSION("unisessionschema"),
    ROI("globalevents.roi.schema");
    private String value;

    SchemaSubject(String value) {
        this.value = value;
    }

    public static SchemaSubject of(String name) {
        Preconditions.checkNotNull(SchemaSubject.valueOf(name));
        return SchemaSubject.valueOf(name);
    }

    public static void main(String[] args) {
        System.out.println(SchemaSubject.valueOf(EventType.AUTOTRACK_NATIVE.getName().toUpperCase()));
    }
}
