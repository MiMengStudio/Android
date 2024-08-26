package com.mimeng;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.mimeng.activity.WebViewActivity;
import com.mimeng.user.AccountManager;

public class App extends Application {
    public static final Gson GSON = new Gson();
    private static App instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        AccountManager.tryLoadFromStorage(this);
        if (AccountManager.hasLoggedIn()) {
            // 先检查token, 再更新签到状态
            AccountManager.validateToken(new AccountManager.ValidateTokenResult() {
                @Override
                public void onSuccess() {
                    AccountManager.updateAccountSignInTime();
                }

                @Override
                public void onFail() {
                    Intent intent = WebViewActivity.createLoginInIntent(App.this);
                    intent.putExtra("toast", "登录失效，请重新登录");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            });
        }
    }

    /**
     * 获取状态栏高度偏移5dp
     *
     * @return 返回高度
     */
    public static int getStatusBarHeight(@Nullable Context context) {
        if (context == null) {
            context = instance;
        }
        @SuppressLint("InternalInsetResource")
        int offsetId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return context.getResources().getDimensionPixelOffset(offsetId) / 5;
    }

    /**
     * 设置上边距的距离
     * @param root view
     * @param topMarginTimes 偏移率
     */
    public static void resetLayoutTopMargin(@Nullable Context context , @NonNull View root, int topMarginTimes) {
        ViewGroup.LayoutParams layoutParams = root.getLayoutParams();
        if (layoutParams instanceof ViewGroup.MarginLayoutParams params) {
            params.topMargin += App.getStatusBarHeight(context) * topMarginTimes;
        }
    }

    @NonNull
    public App get() {
        return instance;
    }
}
