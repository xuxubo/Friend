package com.guidian.searchFriends.model.enums;

public enum TeamStatusEnum {

    PUBLIC(0,"公开"),
    PRIVATE(1,"私有"),
    SECRET(2,"加密");

    private int value;

    private String text;

    public static TeamStatusEnum getEnum(int value) {
        for (TeamStatusEnum userStatusEnum : TeamStatusEnum.values()) {
            if (userStatusEnum.getValue() == value) {
                return userStatusEnum;
            }
        }
        return null;
    }

    TeamStatusEnum(int value, String text) {
        this.value = value;
        this.text = text;
    }

    public int getValue() {
        return value;
    }

    public String getText() {
        return text;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void setText(String text) {
        this.text = text;
    }

}
