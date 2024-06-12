package com.mimeng;

import android.os.Bundle;
import android.widget.Toast;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import com.mimeng.BaseClass.BaseActivity;
import com.mimeng.databinding.ActivityMainBinding;

public class MainActivity extends BaseActivity {
    private long exitTime = 0;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        private static final String PREFS_NAME = "MyPrefsFile";
    private static final String KEY_FIRST_TIME = "isFirstTime";
        super.onCreate(savedInstanceState);
        blackParentBar();
        // Inflate and get instance of binding
        binding = ActivityMainBinding.inflate(getLayoutInflater());

        // set content view to binding's root
        setContentView(binding.getRoot());
        
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isFirstTime = prefs.getBoolean(KEY_FIRST_TIME, true);

        if (isFirstTime) {
            // 用户是第一次使用应用，执行相应操作，如显示教程
            showTutorial();

            // 设置标志，表明应用已被启动过
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(KEY_FIRST_TIME, false);
            editor.apply();
        } else {
            // 用户不是第一次使用，直接进入主界面或常规流程
            proceedToMain();
        }
    }
    
    private void showTutorial() {
        // 实现展示教程的逻辑
        // 短时间显示的Toast
        Toast.makeText(this, "首次使用", Toast.LENGTH_SHORT).show();
    }

    private void proceedToMain() {
        // 应用正常启动流程
        Toast.makeText(this, "正常启动", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        isDestroyAction();
    }

    private void isDestroyAction() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(this, "再操作一次退出软件", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
            return;
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.binding = null;
    }

}