package com.mimeng;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import android.content.Intent;
import android.content.SharedPreferences;
import com.mimeng.BaseClass.BaseActivity;
import com.mimeng.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends BaseActivity {
    private long exitTime = 0;
    private ActivityMainBinding binding;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final String PREFS_NAME = "MyPrefsFile";
        final String KEY_FIRST_TIME = "isFirstTime";
        super.onCreate(savedInstanceState);
        blackParentBar();
        binding = ActivityMainBinding.inflate(getLayoutInflater());
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

        setFullScreen(false);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
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