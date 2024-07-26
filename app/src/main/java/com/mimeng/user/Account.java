package com.mimeng.user;

import androidx.annotation.NonNull;

public class Account {
    private String id;
    private String token;
    private String name;
    private String qq;
    private long date;
    private boolean internal;
    private long vipDate;
    private String miniuid;
    private long signInDate;

    // 无参构造函数
    public Account() {}

    // 全参构造函数
    public Account(String _id, String id, String token, String name, String qq, long date, boolean internal, long vipDate, String miniuid) {
        this.id = id;
        this.token = token;
        this.name = name;
        this.qq = qq;
        this.date = date;
        this.internal = internal;
        this.vipDate = vipDate;
        this.miniuid = miniuid;
    }

    // Getter 和 Setter 方法
    public String getID() {
        return id;
    }

    public void setID(String id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQQ() {
        return qq;
    }

    public void setQQ(String qq) {
        this.qq = qq;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public boolean isInternal() {
        return internal;
    }

    public void setInternal(boolean internal) {
        this.internal = internal;
    }

    public long getVipDate() {
        return vipDate;
    }

    public void setVipDate(long vipDate) {
        this.vipDate = vipDate;
    }

    public String getMiniuid() {
        return miniuid;
    }

    public void setMiniuid(String miniuid) {
        this.miniuid = miniuid;
    }

    public long getSignInDate() {
        return signInDate;
    }

    protected void setSignInDate(long signInDate) {
        this.signInDate = signInDate;
        AccountManager.notifyListenersUpdateSignInDate();
    }

    
    /**
      * 根据当前时间判断当前用户是否是vip
      */
    public boolean isVip() {
        return System.currentTimeMillis() < getVipDate();
    }

    @NonNull
    @Override
    public String toString() {
        return "Account{" +
                ", id='" + id + '\'' +
                ", token='" + token + '\'' +
                ", name='" + name + '\'' +
                ", qq='" + qq + '\'' +
                ", date=" + date +
                ", internal=" + internal +
                ", vipDate=" + vipDate +
                ", miniuid='" + miniuid + '\'' +
                ", signInDate=" + signInDate +
                '}';
    }
}