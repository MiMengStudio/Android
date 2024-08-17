package com.mimeng.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mimeng.R;
import com.mimeng.base.BaseActivity;

@SuppressLint("CustomSplashScreen")
public class LaunchActivity extends BaseActivity {
    private static CountDownTimer timer;

    @SuppressLint("ObsoleteSdkInt")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        setFullScreen(true);

        // 计时
        TextView time = findViewById(R.id.timer);
        timer = new CountDownTimer(5000, 1000) {
            @SuppressLint("SetTextI18n")
            @Override
            public void onTick(long l) {
                long t = l / 1000;
                time.setText(getString(R.string.msg_skip_launch_screen, t));
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

        // 设置渐显效果
        ImageView launchBanner = findViewById(R.id.banner);
        launchBanner.setAlpha(0f);
        launchBanner.postDelayed(() -> {
            ObjectAnimator alpha = ObjectAnimator.ofFloat(launchBanner, "alpha", 0f, 1f);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(alpha);
            animatorSet.setDuration(800);
            animatorSet.setInterpolator(new AccelerateInterpolator());
            animatorSet.start();
        }, 700);

    }

    private void isBeginner() {
        // 检查是否已经看过教程
        SharedPreferences prefs = getSharedPreferences("MyPrefsFile", MODE_PRIVATE);
        // 仅保留是否看过教程的键
        boolean hasSeenGuide = prefs.getBoolean("hasSeenGuide", false);
        if (!hasSeenGuide) {
            // 如果用户未看过教程
            toMainActivity(GuideActivity.class);
            Toast.makeText(this, "展示教程", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            toMainActivity(MainActivity.class); // 如果已经看过教程，直接进入主界面
        }
    }


    // 覆盖返回按钮，不允许退出程序
    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onResume() {
        super.onResume();
        timer.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        timer.cancel();
    }

    // 避免内存泄漏，确保在Activity销毁时释放binding
    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }
}
