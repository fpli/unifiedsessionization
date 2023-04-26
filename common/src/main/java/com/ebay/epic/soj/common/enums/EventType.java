package com.ebay.epic.soj.common.enums;

public enum EventType {
    AUTOTRACK_NATIVE("autotrack","native","nonbot"),
    AUTOTRACK_WEB("autotrack","web","nonbot"),
    UBI_NONBOT("ubi","ubi","nonbot"),
    UBI_BOT("ubi","ubi","bot"),
    UTP_NONBOT("utp","utp","nonbot"),
    UTP_BOT("utp","utp","bot"),
    SESSION_BOT("session","session","bot"),
    SESSION_NONBOT("session","session","nonbot"),
    DEFAULT("default","default","default"),
    LATE_WEB("late","web","nonbot"),
    LATE_NATIVE("late","native","nonbot"),
    LATE_UBI_NONBOT("late","ubi","nonbot"),
    LATE_UBI_BOT("late","ubi","bot"),
    LATE_UTP_NONBOT("late","utp","nonbot"),
    LATE_UTP_BOT("late","utp","bot"),
    DEFAULT_LATE("late","default","default"),;

    private String name;
    private String category;
    private String botType;

    EventType(String name,String category,String botType) {
        this.name = name;
        this.category=category;
        this.botType=botType;
    }

    public String getName() {
        return name;
    }
    public String getCategory(){
        return category;
    }
    public String getBotType(){
        return botType;
    }

    public String getFullName(){
        return String.join(".",name,category,botType);
    }

    public static void main(String[] args) {
        System.out.println(EventType.AUTOTRACK_NATIVE.name.toUpperCase());
    }
}
