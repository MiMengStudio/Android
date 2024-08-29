package com.mimeng.request;

public enum RequestMethods {
    GET("GET", false),
    POST("POST", true),
    PUT("PUT", true),
    DELETE("DELETE", true);
    private final String value;
    private final boolean requireBody;

    RequestMethods(String value, boolean requireBody) {
        this.value = value;
        this.requireBody = requireBody;
    }

    public String getValue() {
        return value;
    }

    public boolean isRequireBody() {
        return requireBody;
    }
}
