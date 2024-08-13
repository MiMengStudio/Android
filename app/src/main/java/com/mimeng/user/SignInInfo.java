package com.mimeng.user;

public enum SignInInfo {
    /** 未签到 */
    NEED_SIGN_IN,
    /** 已签到 */
    SIGNED_IN,
    /** 签到成功 */
    SIGNED_SUCCESSFUL,
    INVALID_TOKEN("token错误"),
    USER_NOT_FOUND("账号不存在"),
    NOT_LOGGED_IN("账号未登录"),

    UNKNOWN_ERROR("Unknown error");

    private final String errorMsg;

    SignInInfo() {
        this("");
    }

    SignInInfo(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getErrorMsg() {
        return errorMsg;
    }
}
