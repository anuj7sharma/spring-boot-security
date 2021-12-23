package com.learning.accesscontrol.entity;


public enum TokenType {
    REGISTER("REGISTER"),
    FORGOT_PASSWORD("FORGOT_PASSWORD");

    private final String type;

    private TokenType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
