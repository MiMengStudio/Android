package com.mimeng.user;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.mimeng.R;
import com.mimeng.utils.DateUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.function.Consumer;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AccountManager {
    public static final String LOGIN_IN_URL = "https://account.mimeng.fun?origin=MiMengAndroidAPP";
    public static final String ACCOUNT_SERVICE_URL = "https://cloud.mimeng.fun/account";
    private static final String TAG = "AccountManager";
    @Nullable
    private static Account loggedIn; // 缓存全局已登录账号

    /**
     * 保存 Account 对象到 SharedPreferences
     * @param context Context 对象
     * @param account Account 对象
     */
    public static void save(Context context, Account account) {
        Gson gson = new Gson();
        String json = gson.toJson(account);
        SharedPreferences sharedPreferences = context.getSharedPreferences("AccountPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("account", json);
        editor.apply();

        loggedIn = account;
    }

    /**
     * 从 SharedPreferences 获取 Account 对象
     * @param context Context 对象
     */
    public static void tryLoadFromStorage(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("AccountPrefs", Context.MODE_PRIVATE);
        String json = sharedPreferences.getString("account", null);
        if (json != null) {
            // Gson解析也是有开销的，能缓存就缓存
            Gson gson = new Gson();
            loggedIn = gson.fromJson(json, Account.class);
            Log.d(TAG, "Found account");
        }
    }

    private static void ensureUserLoggedIn() {
        if (!hasLoggedIn()) {
            throw new RuntimeException("No user logged in.");
        }
    }

    /**
     * 加载用户头像至ImageView
     */
    public static void loadUserIcon(ImageView target) {
        ensureUserLoggedIn();
        Picasso.get()
            .load("https://q1.qlogo.cn/g?b=qq&nk=" + loggedIn.getQQ()+ "&s=100")
            .placeholder(R.drawable.ic_default_head)
            .error(R.drawable.ic_default_head)
            .into(target);
    }

    /**
     * 向服务器请求，当前账号是否需要签到
     */
    protected static void updateAccountSignInTime(@NonNull Consumer<Integer> callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(ACCOUNT_SERVICE_URL + "?act=isSignedIn&id=" +
                        loggedIn.getAccount() + "&token=" + loggedIn.getToken())
                .get().build();
        Log.i(TAG, "Request to server for sign in info");
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Failed to GET Account Sign In Info", e);
                callback.accept(-1);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        JSONObject obj = new JSONObject(response.body().string());
                        if (obj.has("lastSignDate")) {
                            long time = obj.getLong("lastSignDate");
                            Log.d(TAG, "Update sign in date" + time);
                            loggedIn.setSignInDate(time);
                        }
                        callback.accept(obj.getInt("code"));
                    } catch (JSONException e) {
                        Log.e(TAG, "Json Syntax Error " + response, e);
                        callback.accept(-1);
                    }
                } else {
                    Log.e(TAG, "Failed to GET Account Sign In Info, Code " + response.code() + response.body().string());
                    callback.accept(-1);
                }
            }
        });
    }

    public static void requireSignIn(@NonNull Consumer<SignInInfo> callback) {
        ensureUserLoggedIn();
        // 第一次登录
        long currentTime = System.currentTimeMillis();
        long signInDate = loggedIn.getSignInDate();

        if (DateUtils.isSameDay(currentTime, signInDate)) {
            callback.accept(SignInInfo.SIGNED_IN);
            return;
        }

         AccountManager.updateAccountSignInTime(code -> {
                switch (code) {
                    case 0:
                        callback.accept(SignInInfo.NEED_SIGN_IN);
                        break;
                    case 1:
                        callback.accept(SignInInfo.SIGNED_IN);
                        break;
                    case 2:
                        callback.accept(SignInInfo.INVALID_TOKEN);
                        break;
                    case 3:
                        callback.accept(SignInInfo.USER_NOT_FOUND);
                        break;
                    case -1:
                        callback.accept(SignInInfo.UNKNOWN_ERROR);
                }
            });
    }

    public static void performSigningIn(@NonNull Consumer<SignInInfo> callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(ACCOUNT_SERVICE_URL + "?act=signIn&id=" +
                        loggedIn.getAccount() + "&token=" + loggedIn.getToken())
                .get().build();
        Log.i(TAG, "Performing sign in");
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Failed to GET Account Sign In Info", e);
                callback.accept(SignInInfo.UNKNOWN_ERROR);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        JSONObject obj = new JSONObject(response.body().string());
                        switch (obj.getInt("code")) {
                            case 0:
                                callback.accept(SignInInfo.SIGNED_SUCCESSFUL);
                                break;
                            case 1:
                                callback.accept(SignInInfo.SIGNED_IN);
                                break;
                            case 2:
                                callback.accept(SignInInfo.INVALID_TOKEN);
                                break;
                            case 3:
                                callback.accept(SignInInfo.USER_NOT_FOUND);
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Json Syntax Error " + response, e);
                        callback.accept(SignInInfo.UNKNOWN_ERROR);
                    }
                } else {
                    Log.e(TAG, "Failed to Sign In, Code " + response.code() + response.body().string());
                    callback.accept(SignInInfo.UNKNOWN_ERROR);
                }
            }
        });
    }

    public static Account get() {
        return loggedIn;
    }

    public static boolean hasLoggedIn() {
        return loggedIn != null;
    }
}
