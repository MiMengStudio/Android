package com.mimeng;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
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

        setFullScreen(true);

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
                .placeholder(R.mipmap.banner_loading)
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
        toMainActivity(GuideActivity.class);
        Toast.makeText(this, "展示教程", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void proceedToMain() {
        // 应用正常启动流程
        toMainActivity(MainActivity.class);
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