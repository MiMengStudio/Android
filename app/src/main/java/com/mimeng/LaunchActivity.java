package com.mimeng;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.mimeng.BaseClass.BaseActivity;

@SuppressLint("CustomSplashScreen")
public class LaunchActivity extends BaseActivity {

    @SuppressLint("ObsoleteSdkInt")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_launch);

        // 实现全屏显示
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;

        decorView.setSystemUiVisibility(uiOptions);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 状态栏颜色设置为透明，适用于Android 6.0及以上版本
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        // 适配刘海屏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            getWindow().setAttributes(lp);
        }

        // 监听系统UI可见性变化，确保状态栏和导航栏隐藏
        decorView.setOnSystemUiVisibilityChangeListener(visibility -> {
            if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                decorView.setSystemUiVisibility(uiOptions);
            }
        });

        // 渐入渐出动画&利用控件的postDelayed方法控制进入主页面时间
        ImageView imageView = findViewById(R.id.launcher_icon);
        imageView.postDelayed(() -> toMainActivity(LaunchActivity.this, MainActivity.class), 3000);

    }

    // 覆盖返回按钮，不允许退出程序
    @Override
    public void onBackPressed() {
    }

    // 避免内存泄漏，确保在Activity销毁时释放binding
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}