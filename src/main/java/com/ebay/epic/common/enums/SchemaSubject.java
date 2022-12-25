package com.ebay.epic.common.enums;

import avro.shaded.com.google.common.base.Preconditions;

import java.io.Serializable;

public enum SchemaSubject {
    AUTOTRACK("sessionizedevent"),
    UBI("behavior.sojourner.sojevent.schema"),
    UTP("marketing.tracking.events.schema"),
    SESSION("unisessionschema");
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
