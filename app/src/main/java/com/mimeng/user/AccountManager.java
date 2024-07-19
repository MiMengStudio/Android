package com.mimeng.user;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;
import androidx.annotation.Nullable;
import com.google.gson.Gson;
import com.mimeng.R;
import android.content.SharedPreferences;
import com.squareup.picasso.Picasso;

public class AccountManager {
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
    
    public static Account get() {
        return loggedIn;
    }
    
    public static boolean hasLoggedIn() {
        return loggedIn != null;
    }
}
