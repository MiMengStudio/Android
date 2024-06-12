package com.mimeng;

import android.os.Bundle;
import android.widget.Toast;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import com.mimeng.BaseClass.BaseActivity;
import com.mimeng.databinding.ActivityMainBinding;

public class MainActivity extends BaseActivity {
    private long exitTime = 0;
    private ActivityMainBinding binding;
    private final String PREFS_NAME = "MyPrefsFile";
    private final String KEY_GUIDE_SEEN = "hasSeenGuide"; // 仅保留是否看过教程的键

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        blackParentBar();
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean hasSeenGuide = prefs.getBoolean(KEY_GUIDE_SEEN, false); // 检查是否已经看过教程

        if (!hasSeenGuide) { // 如果用户未看过教程
            showTutorial();
        } else {
            proceedToMain(); // 如果已经看过教程，直接进入主界面
        }
    }
    
    private void showTutorial() {
        Intent intent = new Intent(this, GuideActivity.class);
        startActivity(intent);
        Toast.makeText(this, "展示教程", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void proceedToMain() {
        // 应用正常启动流程
        Toast.makeText(this, "进入主界面", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        isDestroyAction();
    }

    private void isDestroyAction() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(this, "再按一次退出应用", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish(); // 用户两次点击返回键，退出应用
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null; // 避免内存泄漏
    }
}