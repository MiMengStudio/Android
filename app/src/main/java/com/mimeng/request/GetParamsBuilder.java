package com.mimeng.request;

import android.util.ArrayMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.mimeng.user.Account;

import java.util.Map;
import java.util.stream.Collectors;

public final class GetParamsBuilder {
    @NonNull
    private final Map<String, Object> getParams;

    public GetParamsBuilder() {
        this.getParams = new ArrayMap<>();
    }

    @CanIgnoreReturnValue
    public GetParamsBuilder set(@NonNull String key, @NonNull Object value) {
        getParams.put(key, value);
        return this;
    }

    @NonNull
    @Override
    public String toString() {
        return getParams.entrySet().stream()
                .map((entry) -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("&", "?", ""));
    }

    @CanIgnoreReturnValue
    public GetParamsBuilder setIDIfValid(@Nullable Account account) {
        return set("id", account != null ? account.getID() : "null");
    }

    @CanIgnoreReturnValue
    public GetParamsBuilder setTokenIfValid(@Nullable Account account) {
        return set("token", account != null ? account.getToken() : "null");
    }

    public void setIDAndTokenIfValid(@Nullable Account account) {
        setIDIfValid(account).setTokenIfValid(account);
    }

}
