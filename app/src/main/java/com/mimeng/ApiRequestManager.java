package com.mimeng;

import android.util.ArrayMap;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.mimeng.user.Account;
import com.mimeng.user.AccountManager;
import com.mimeng.user.SignInInfo;

import java.util.Map;
import java.util.stream.Collectors;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public final class ApiRequestManager implements AccountManager.AccountSignInTimeListener {
    @NonNull
    public static final ApiRequestManager DEFAULT = new ApiRequestManager();
    private static final String TAG = "ApiRequestManager";
    private final OkHttpClient client;
    @Nullable
    private Account account;

    ApiRequestManager() {
        this.client = new OkHttpClient();
        AccountManager.addSignInDateUpdateListener(this);
    }

    private Request buildRequest(String url) {
        Request.Builder builder = new Request.Builder()
                .url(url);
        
        Log.i(TAG, "Request url " + url);
        if (account != null) {
            builder.addHeader("Authorization", "Bearer " + account.getToken());
            Log.d(TAG, "Using token " + account.getToken() );
        }
        return builder.get().build();
    }

    void setAccount(@NonNull Account account) {
        this.account = account;
    }

    /**
     * 搜索文章
     * @param word 关键词
     * @param pager 页数
     * @param callback 请求回调
     */
    public void searchArticle(@NonNull String word, int pager, @NonNull Callback callback) {
        RequestURLBuilder builder = new RequestURLBuilder(ApplicationConfig.HOST_API + "/search", account)
                .setAction(RequestURLBuilder.Action.SEARCH_ARTICLE)
                .set("keyword", word)
                .set("page", pager)
                .set("sort", "hot")
                .set("reverse", "false");
        String url = builder.toString();
        Log.d(TAG, "startSearchArticle: 完整API => " + url);
        client.newCall(buildRequest(url)).enqueue(callback);
    }

    /**
     * 获取轮播图
     * @param callback 请求回调
     */
    public void getBannerApi(@NonNull Callback callback){
        RequestURLBuilder builder = new RequestURLBuilder(ApplicationConfig.HOST_API + "/content", account)
                .setAction(RequestURLBuilder.Action.AD);
        String url = builder.toString();
        Log.d(TAG, "getBannerApi: 完整API => " + url);
        client.newCall(buildRequest(url)).enqueue(callback);
    }

    /**
     * 获取账号数据
     * @return account实例
     */
    public Account getAccountData(){
        return account;
    }

    public void performSigningIn(@NonNull Callback callback) {
        Log.i(TAG, "Performing sign in");
        client.newCall(buildRequest(
                new RequestURLBuilder(ApplicationConfig.ACCOUNT_SERVICE_URL, account)
                        .setAction(RequestURLBuilder.Action.SIGN_IN)
                        .toString())).enqueue(callback);
    }

    public void updateAccountSignInTime(@NonNull Callback callback) {
        Log.i(TAG, "Request to server for sign in info");
        client.newCall(buildRequest(
                new RequestURLBuilder(ApplicationConfig.ACCOUNT_SERVICE_URL, account)
                        .setAction(RequestURLBuilder.Action.IS_SIGNED_IN)
                        .toString()
        )).enqueue(callback);
    }

    public void validateToken(@NonNull Callback callback) {
        Log.i(TAG, "Request to server for validateToken");
        client.newCall(buildRequest(
                new RequestURLBuilder(ApplicationConfig.ACCOUNT_SERVICE_URL, account)
                        .setAction(RequestURLBuilder.Action.VALIDATE_TOKEN)
                        .toString()
        )).enqueue(callback);
    }

    @Override
    public boolean onReceive(@NonNull SignInInfo newInfo) {
        setAccount(AccountManager.get());
        return false;
    }

    private final static class RequestURLBuilder {
        @NonNull
        private final Map<String, Object> getParams;
        @NonNull
        private final String base;

        private RequestURLBuilder(@NonNull String base) {
            this.getParams = new ArrayMap<>();
            this.base = base;
        }

        private RequestURLBuilder(@NonNull String base, @Nullable Account id) {
            this(base);
            setIDIfValid(id);
            setTokenIfValid(id);
        }

        public RequestURLBuilder setAction(@NonNull String act) {
            getParams.put("act", act);
            return this;
        }

        public RequestURLBuilder setAction(@NonNull Action act) {
            return setAction(act.toString());
        }

        public RequestURLBuilder set(@NonNull String key, @NonNull Object value) {
            getParams.put(key, value);
            return this;
        }

        @CanIgnoreReturnValue
        public RequestURLBuilder setIDIfValid(@Nullable Account account) {
            return set("id", account != null ? account.getID() : "null");
        }
        
        @CanIgnoreReturnValue
        public RequestURLBuilder setTokenIfValid(@Nullable Account account) {
            return set("token", account != null ? account.getToken() : "null");
        }

        @NonNull
        @Override
        public String toString() {
            return base + getParams.entrySet().stream()
                    .map((entry) -> entry.getKey() + "=" + entry.getValue())
                    .collect(Collectors.joining("&", "?", ""));
        }

        enum Action {
            SEARCH_ARTICLE("searchArticle"),
            IS_SIGNED_IN("isSignedIn"),
            SIGN_IN("signIn"),
            VALIDATE_TOKEN("validateToken"),
            AD("getAD");
            @NonNull
            private final String act;

            Action(@NonNull String act) {
                this.act = act;
            }

            @NonNull
            @Override
            public String toString() {
                return act;
            }
        }
    }
}
