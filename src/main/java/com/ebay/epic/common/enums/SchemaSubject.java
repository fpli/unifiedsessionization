package com.ebay.epic.common.enums;

public enum SchemaSubject {
    SURFACE_NATIVE("rheosTrackEvent"),
    SURFACE("sessionizedevent"),
    UBI("behavior.sojourner.sojevent.schema"),
    UTP("marketing.tracking.events.schema");

    private String value;
    SchemaSubject(String value){
        this.value=value;
    }

}
