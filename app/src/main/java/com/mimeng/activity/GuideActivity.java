package com.mimeng.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.mimeng.R;
import com.mimeng.base.BaseActivity;

public class GuideActivity extends BaseActivity {
    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String KEY_GUIDE_SEEN = "hasSeenGuide";
    private long exitTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide); // 确保这是您的教程界面布局文件名

        // 假设按钮的ID是btnFinishGuide
        Button btnFinishGuide = findViewById(R.id.btnSkip); // 使用实际的按钮ID

        // 设置按钮点击事件
        btnFinishGuide.setOnClickListener(v -> {
                markGuideAsSeen(); // 标记教程已看
                proceedToMain(); // 进入主界面
                finish();
            }
        );
    }

    private void markGuideAsSeen() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY_GUIDE_SEEN, true); // 更新教程观看状态
        editor.apply();
    }

    private void proceedToMain() {
        toMainActivity(MainActivity.class);
    }

    @Override
    public void onBackPressed() {
        isDestroyAction();
    }

    private void isDestroyAction() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(this, R.string.msg_press_back_again_then_exit, Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish(); // 用户两次点击返回键，退出应用
        }
    }
}
