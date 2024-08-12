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
import java.util.concurrent.CopyOnWriteArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AccountManager {
    public static final String LOGIN_IN_URL = "https://account.mimeng.fun?origin=MiMengAndroidAPP";
    public static final String ACCOUNT_SERVICE_URL = "https://cloud.mimeng.fun/account";
    //public static final String ACCOUNT_SERVICE_URL = "http://127.0.0.1:3000/account";
    private static final String TAG = "AccountManager";
    private static final CopyOnWriteArrayList<AccountSignInTimeListener> listeners = new CopyOnWriteArrayList<>();
    @Nullable
    private static Account loggedIn; // 缓存全局已登录账号
    @Nullable
    private static SignInInfo lastSignInInfo = null;

    /**
     * 保存 Account 对象到 SharedPreferences
     *
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
        lastSignInInfo = DateUtils.isSameDay(System.currentTimeMillis(), account.getSignInDate()) ? SignInInfo.SIGNED_IN : SignInInfo.NEED_SIGN_IN;
        notifyListenersUpdateSignInDate();
    }

    /**
     * 清除用户数据，只保留token和id
     *
     * @param context 上下文
     */
    public static void clearUserLoginData(Context context) {
        Account account = new Account();
        account.setName("");
        account.setVipDate(0);
        account.setQQ("");
        account.setDate(0);
        account.setMiniuid("");
        account.setID(AccountManager.getAccountData(context).getID());
        account.setToken(AccountManager.getAccountData(context).getToken());
        save(context, account);
    }

    /**
     * 获取用户敏感信息
     *
     * @param context 上下文对象
     * @return 返回Account实体类对象
     */
    public static Account getAccountData(Context context) {
        Account account = new Account();
        try {
            SharedPreferences sharedPreferences = context.getSharedPreferences("AccountPrefs", Context.MODE_PRIVATE);
            String json = sharedPreferences.getString("account", "");
            account = new Gson().fromJson(json, Account.class);
        } catch (Exception e) {
            Log.e(TAG, "getAccountData: 获取用户信息方法发送错误 => " + e);
        }
        return account;
    }

    /**
     * 从 SharedPreferences 获取 Account 对象
     *
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
        assert loggedIn != null;
        Picasso.get()
                .load("https://q1.qlogo.cn/g?b=qq&nk=" + loggedIn.getQQ() + "&s=100")
                .placeholder(R.drawable.ic_default_head)
                .error(R.drawable.ic_default_head)
                .into(target);
    }

    static void notifyListenersUpdateSignInDate() {
        assert lastSignInInfo != null;
        for (AccountSignInTimeListener listener : listeners) {
            listener.onReceive(lastSignInInfo);
        }

        if (lastSignInInfo == SignInInfo.SIGNED_SUCCESSFUL) {
            lastSignInInfo = SignInInfo.SIGNED_IN;
        }
    }

    /**
     * 向服务器请求，当前账号是否需要签到
     */
    public static void updateAccountSignInTime() {
        OkHttpClient client = new OkHttpClient();
        assert loggedIn != null;
        Request request = new Request.Builder()
                .url(ACCOUNT_SERVICE_URL + "?act=isSignedIn&id=" +
                        loggedIn.getID() + "&token=" + loggedIn.getToken())
                .get().build();
        Log.i(TAG, "Request to server for sign in info");
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Failed to GET Account Sign In Info", e);
                lastSignInInfo = SignInInfo.UNKNOWN_ERROR;
                notifyListenersUpdateSignInDate();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        JSONObject obj = new JSONObject(response.body().string());
                        switch (obj.getInt("code")) {
                            case 0:
                                lastSignInInfo = SignInInfo.NEED_SIGN_IN;
                                break;
                            case 1:
                                lastSignInInfo = SignInInfo.SIGNED_IN;
                                break;
                            case 2:
                                lastSignInInfo = SignInInfo.INVALID_TOKEN;
                                break;
                            case 3:
                                lastSignInInfo = SignInInfo.USER_NOT_FOUND;
                                break;
                        }
                        if (obj.has("lastSignDate")) {
                            long time = obj.getLong("lastSignDate");
                            Log.d(TAG, "Update sign in date" + time);
                            loggedIn.setSignInDate(time);
                        } else {
                            notifyListenersUpdateSignInDate();
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Json Syntax Error " + response, e);
                        lastSignInInfo = SignInInfo.UNKNOWN_ERROR;
                        notifyListenersUpdateSignInDate();
                    }
                } else {
                    Log.e(TAG, "Failed to GET Account Sign In Info, Code " + response.code() + response.body().string());
                    lastSignInInfo = SignInInfo.UNKNOWN_ERROR;
                    notifyListenersUpdateSignInDate();
                }
            }
        });
    }

    public static void addSignInDateUpdateListener(@NonNull AccountSignInTimeListener listener) {
        if (lastSignInInfo != null) {
            listener.onReceive(lastSignInInfo);
        }
        listeners.add(listener);
    }

    public static void removeSignInDateUpdateListener(@NonNull AccountSignInTimeListener listener) {
        listeners.remove(listener);
    }

    public static void performSigningIn() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(ACCOUNT_SERVICE_URL + "?act=signIn&id=" +
                        loggedIn.getID() + "&token=" + loggedIn.getToken())
                .get().build();
        Log.i(TAG, "Performing sign in");
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Failed to GET Account Sign In Info", e);
                lastSignInInfo = SignInInfo.UNKNOWN_ERROR;
                notifyListenersUpdateSignInDate();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        JSONObject obj = new JSONObject(response.body().string());
                        switch (obj.getInt("code")) {
                            case 0:
                                lastSignInInfo = SignInInfo.SIGNED_SUCCESSFUL;
                                loggedIn.setSignInDate(obj.getLong("date"));
                                break;
                            case 1:
                                lastSignInInfo = SignInInfo.SIGNED_IN;
                                loggedIn.setSignInDate(obj.getLong("date"));
                                break;
                            case 2:
                                lastSignInInfo = SignInInfo.INVALID_TOKEN;
                                break;
                            case 3:
                                lastSignInInfo = SignInInfo.USER_NOT_FOUND;
                        }
                        notifyListenersUpdateSignInDate();
                    } catch (JSONException e) {
                        Log.e(TAG, "Json Syntax Error " + response, e);
                        lastSignInInfo = SignInInfo.UNKNOWN_ERROR;
                        notifyListenersUpdateSignInDate();
                    }
                } else {
                    Log.e(TAG, "Failed to Sign In, Code " + response.code() + response.body().string());
                    lastSignInInfo = SignInInfo.UNKNOWN_ERROR;
                    notifyListenersUpdateSignInDate();
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

    public interface AccountSignInTimeListener {
        /**
         * 在签到时间 向服务器请求更新完毕后,
         * 或签到时间被更新(登录新账号)时调用.
         * 注意，调用时可能不在主线程.
         */
        void onReceive(@NonNull SignInInfo newInfo);
    }
}
