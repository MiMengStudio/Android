package com.mimeng;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class GuideActivity extends AppCompatActivity {
    private final String PREFS_NAME = "MyPrefsFile";
    private final String KEY_GUIDE_SEEN = "hasSeenGuide";
    
    private Button btnFinishGuide; // 假设按钮的ID是btnFinishGuide

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide); // 确保这是您的教程界面布局文件名

        btnFinishGuide = findViewById(R.id.btnSkip); // 使用实际的按钮ID

        // 设置按钮点击事件
        btnFinishGuide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                markGuideAsSeen(); // 标记教程已看
                proceedToMain(); // 进入主界面
                finish();
            }
        });
    }

    private void markGuideAsSeen() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY_GUIDE_SEEN, true); // 更新教程观看状态
        editor.apply();
    }

    private void proceedToMain() {
        Intent intent = new Intent(this, MainActivity.class); // 假设教程结束后进入MainActivity
        startActivity(intent);
        finish(); // 结束当前的GuideActivity
    }
}