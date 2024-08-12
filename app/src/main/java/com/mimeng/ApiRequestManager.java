package com.mimeng;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mimeng.user.Account;
import com.mimeng.user.AccountManager;
import com.mimeng.user.SignInInfo;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class ApiRequestManager implements AccountManager.AccountSignInTimeListener {
    @NonNull
    public static final ApiRequestManager DEFAULT = new ApiRequestManager();
    private static final String TAG = "ApiRequestManager";
    private final OkHttpClient client;
    @Nullable
    private Account account;

    ApiRequestManager() {
        this.client = new OkHttpClient();
    }

    private Request buildRequest(String url) {
        Request.Builder builder = new Request.Builder()
                .url(url);
        if (account != null) {
            builder.addHeader("Authorization", "Bearer " + account.getToken());
        }
        return builder.get().build();
    }

    private String getIdIfNotNull() {
        return account != null ? account.getID() : "null";
    }

    private String getTokenIfNotNull() {
        return account != null ? account.getToken() : "null";
    }

    public void setAccount(@NonNull Account account) {
        this.account = account;
    }

    public void searchArticle(@NonNull String word, Context context, @NonNull Callback callback) {
        String url = ApplicationConfig.HOST_API +
                "/search?act=searchArticle&id=" +
                AccountManager.getAccountData(context).getID() + "&token=" +
                AccountManager.getAccountData(context).getToken() + "&keyword=" +
                word + "&page=1&sort=hot&reverse=false";
        Log.d(TAG, "startSearchArticle: 完整API => " + url);
        client.newCall(buildRequest(url)).enqueue(callback);
    }

    public void performSigningIn(@NonNull Callback callback) {
        String url = ApplicationConfig.ACCOUNT_SERVICE_URL + "?act=signIn&id=" +
                getIdIfNotNull() + "&token=" + getTokenIfNotNull();
        Log.i(TAG, "Performing sign in");
        client.newCall(buildRequest(url)).enqueue(callback);
    }

    public void updateAccountSignInTime(@NonNull Callback callback) {
        String url = ApplicationConfig.ACCOUNT_SERVICE_URL + "?act=isSignedIn&id=" +
                getIdIfNotNull() + "&token=" + getTokenIfNotNull();
        Log.i(TAG, "Request to server for sign in info");
        client.newCall(buildRequest(url)).enqueue(callback);
    }

    @Override
    public boolean onReceive(@NonNull SignInInfo newInfo) {
        switch (newInfo) {
            case NEED_SIGN_IN:
            case SIGNED_IN:
            case SIGNED_SUCCESSFUL:
                setAccount(AccountManager.get());
            default:
                return false;
        }
    }
}
