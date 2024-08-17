package com.mimeng.user;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

import com.google.gson.Gson;
import com.mimeng.ApiRequestManager;
import com.mimeng.App;
import com.mimeng.R;
import com.mimeng.utils.DateUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AccountManager {
    private static final String TAG = "AccountManager";
    private static final ArrayList<WeakReference<AccountSignInTimeListener>> listeners = new ArrayList<>();
    @Nullable
    private static Account loggedIn; // 缓存全局已登录账号
    // null 表示还没有完成服务器请求
    @Nullable
    private static SignInInfo lastSignInInfo = null;

    /**
     * 保存 Account 对象到 SharedPreferences
     *
     * @param context Context 对象
     * @param account Account 对象
     */
    public static void save(Context context, Account account) {
        String json = account == null ? "" : App.GSON.toJson(account);
        SharedPreferences sharedPreferences = context.getSharedPreferences("AccountPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("account", json);
        editor.apply();

        loggedIn = account;
        lastSignInInfo = DateUtils.isSameDay(System.currentTimeMillis(), account.getSignInDate()) ? SignInInfo.SIGNED_IN : SignInInfo.NEED_SIGN_IN;

        notifyListenersUpdateSignInDate();
    }

    /**
     * 清除用户登录数据
     */
    public static void clearUserLoginData() {
//        Account account = new Account();
//        account.setName("");
//        account.setVipDate(0);
//        account.setQQ("");
//        account.setDate(0);
//        account.setMiniuid("");
//        account.setID(AccountManager.getAccountData(context).getID());
//        account.setToken(AccountManager.getAccountData(context).getToken());
        loggedIn = null;
        lastSignInInfo = SignInInfo.INVALID_TOKEN;
        notifyListenersUpdateSignInDate();
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
            loggedIn = App.GSON.fromJson(json, Account.class);
            Log.d(TAG, "Found account");
        }
    }

    /**
     * 加载用户头像至ImageView
     */
    public static void loadUserIcon(ImageView target) {
        assert loggedIn != null;
        Picasso.get()
                .load("https://q1.qlogo.cn/g?b=qq&nk=" + loggedIn.getQQ() + "&s=100")
                .placeholder(R.drawable.ic_default_head)
                .error(R.drawable.ic_default_head)
                .into(target);
    }

    /**
     * 向服务器请求，当前账号是否需要签到
     */
    public static void updateAccountSignInTime() {
        ApiRequestManager.DEFAULT.updateAccountSignInTime(new Callback() {
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
                        lastSignInInfo = switch (obj.getInt("code")) {
                            case 0 -> SignInInfo.NEED_SIGN_IN;
                            case 1 -> SignInInfo.SIGNED_IN;
                            case 2 -> SignInInfo.INVALID_TOKEN;
                            case 3 -> SignInInfo.USER_NOT_FOUND;
                            default ->
                                    throw new IllegalStateException("Unexpected value " + obj.getInt("code"));
                        };
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

    public static void performSigningIn() {
        Log.i(TAG, "Performing sign in");
        ApiRequestManager.DEFAULT.performSigningIn(new Callback() {
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
                        assert loggedIn != null;
                        JSONObject obj = new JSONObject(response.body().string());
                        lastSignInInfo = switch (obj.getInt("code")) {
                            case 0 -> {
                                loggedIn.setSignInDate(obj.getLong("date"));
                                yield SignInInfo.SIGNED_SUCCESSFUL;
                            }
                            case 1 -> {
                                loggedIn.setSignInDate(obj.getLong("date"));
                                yield SignInInfo.SIGNED_IN;
                            }
                            case 2 -> {
                                notifyListenersUpdateSignInDate();
                                yield SignInInfo.INVALID_TOKEN;
                            }
                            case 3 -> {
                                notifyListenersUpdateSignInDate();
                                yield SignInInfo.USER_NOT_FOUND;
                            }
                            default ->
                                    throw new IllegalStateException("Unexpected value: " + obj.getInt("code"));
                        };
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

    public static void validateToken(@NonNull ValidateTokenResult result) {
        ApiRequestManager.DEFAULT.validateToken(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Failed to validate token ", e);
            }

            @Override
            public void onResponse(Call call, Response response) {
                // 根据Core的源码，我们只需要检查返回值
                try {
                    assert response.body() != null;
                    Log.i(TAG, "validateToken: " + response.code() + " " + response.body().string());
                    if (response.isSuccessful()) {
                        result.onSuccess();
                    } else {
                        clearUserLoginData();
                        result.onFail();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "AccountManager -- onResponse: Token校验函数出错 => " + e);
                }

            }
        });
    }


    /**
     * 获取用户敏感信息
     *
     * @return 返回Account实体类对象
     */
    public static Account get() {
        return loggedIn;
    }

    public static boolean hasLoggedIn() {
        return loggedIn != null;
    }

    //  ----------------------------------- 事件 ------------------------------------ //

    /**
     * 监听签到事件，如果已经获得签到状态则会立刻调用.
     * 获取签到状态从app启动需要经过两个请求.
     * 根据网络情况的不同会导致更新时间可能缩短，也可能延长.
     * 所以使用事件回调.
     */
    public static void addSignInDateUpdateListener(@NonNull AccountSignInTimeListener listener) {
        if (lastSignInInfo != null) {
            if (listener.onReceive(lastSignInInfo))
                return;
        }
        listeners.add(new WeakReference<>(listener));
    }

    /**
     * 取消监听事件
     */
    public static void removeSignInDateUpdateListener(@NonNull final AccountSignInTimeListener listener) {
        Iterator<WeakReference<AccountSignInTimeListener>> iterator = listeners.iterator();
        while (iterator.hasNext()) {
            AccountSignInTimeListener ref = iterator.next().get();
            if (ref == null) {
                iterator.remove();
            } else if (ref == listener) {
                iterator.remove();
                break;
            }
        }
    }

    static void notifyListenersUpdateSignInDate() {
        Iterator<WeakReference<AccountSignInTimeListener>> iterator = listeners.iterator();
        while (iterator.hasNext()) {
            AccountSignInTimeListener listener = iterator.next().get();
            if (listener == null || listener.onReceive(lastSignInInfo)) {
                iterator.remove();
            }
        }

        // 回滚SIGNED_IN，防止重复更新UI
        if (lastSignInInfo == SignInInfo.SIGNED_SUCCESSFUL) {
            lastSignInInfo = SignInInfo.SIGNED_IN;
        }
    }

    public interface AccountSignInTimeListener {
        /**
         * 在签到时间 向服务器请求更新完毕后,
         * 或签到时间被更新(登录新账号)时调用.
         * 注意，调用时可能不在主线程.
         *
         * @return 返回true取消之后监听，false则继续监听
         */
        boolean onReceive(@NonNull SignInInfo newInfo);
    }

    public interface ValidateTokenResult {
        @WorkerThread
        void onSuccess();

        @WorkerThread
        void onFail();
    }
}
