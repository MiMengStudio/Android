package com.mimeng.activity;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.mimeng.R;
import com.mimeng.base.BaseActivity;
import com.mimeng.databinding.ActivityMainBinding;

public class MainActivity extends BaseActivity {
    public static final String PREFS_NAME = "MyPrefsFile";
    public static final String KEY_FIRST_TIME = "isFirstTime";

    private long exitTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        blackParentBar();
        com.mimeng.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
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

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(binding.navView, navController);
        Window window = getWindow();
        navController.addOnDestinationChangedListener((navController1, navDestination, bundle) -> {
            if (navDestination.getId()  == R.id.navigation_user){
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.parseColor("#79BD9A")); // 使用你的颜色代码
            }else {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.parseColor("#F5FBF5")); // 使用你的颜色代码
            }
        });
    }

    private void showTutorial() {
        // 实现展示教程的逻辑
        // 短时间显示的Toast
        Toast.makeText(this, "首次使用", Toast.LENGTH_SHORT).show();
    }

    private void proceedToMain() {
        // 应用正常启动流程
        // Toast.makeText(this, "正常启动", Toast.LENGTH_SHORT).show();
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
