package com.mimeng;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.SharedPreferences;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mimeng.BaseClass.BaseActivity;

@SuppressLint("CustomSplashScreen")
public class LaunchActivity extends BaseActivity {
    private final String PREFS_NAME = "MyPrefsFile";
    private final String KEY_GUIDE_SEEN = "hasSeenGuide"; // 仅保留是否看过教程的键

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
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
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

        // 计时
        TextView time = findViewById(R.id.timer);
        CountDownTimer timer =
                new CountDownTimer(5000, 1000) {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onTick(long l) {
                        long t = l / 1000;
                        time.setText("跳过(" + t + ")");
                    }

                    @Override
                    public void onFinish() {
                        isBeginner();
                    }
                };
        timer.start();

        // 跳过
        time.setOnClickListener(view -> {
            timer.cancel();
            isBeginner();
        });

        // 获取随机图片
        ImageView launchBanner = findViewById(R.id.banner);
        Glide.with(LaunchActivity.this).load("https://t.mwm.moe/fj".trim())
                .skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(launchBanner);
    }

    private void isBeginner() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean hasSeenGuide = prefs.getBoolean(KEY_GUIDE_SEEN, false); // 检查是否已经看过教程
        if (!hasSeenGuide) { // 如果用户未看过教程
            showTutorial();
        } else {
            proceedToMain(); // 如果已经看过教程，直接进入主界面
        }
    }

    private void showTutorial() {
        toMainActivity(LaunchActivity.this, GuideActivity.class);
        Toast.makeText(this, "展示教程", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void proceedToMain() {
        // 应用正常启动流程
        toMainActivity(LaunchActivity.this, MainActivity.class);
        Toast.makeText(this, "进入主界面", Toast.LENGTH_SHORT).show();
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