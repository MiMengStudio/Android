package com.mimeng;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.mimeng.user.AccountManager;

public class App extends Application {
    public static final Gson GSON = new Gson();
    private static App instance;

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
     *
     * @param root           view
     * @param topMarginTimes 偏移率
     */
    public static void resetLayoutTopMargin(@Nullable Context context, @NonNull View root, int topMarginTimes) {
        ViewGroup.LayoutParams layoutParams = root.getLayoutParams();
        if (layoutParams instanceof ViewGroup.MarginLayoutParams params) {
            params.topMargin += App.getStatusBarHeight(context) * topMarginTimes;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    @NonNull
    public App get() {
        return instance;
    }
}
