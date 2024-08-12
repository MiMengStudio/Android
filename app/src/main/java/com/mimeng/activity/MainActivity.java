package com.mimeng.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.mimeng.ApplicationConfig;
import com.mimeng.R;
import com.mimeng.base.BaseActivity;
import com.mimeng.databinding.ActivityMainBinding;
import com.mimeng.user.Account;
import com.mimeng.user.AccountManager;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends BaseActivity {
    public static final String PREFS_NAME = "MyPrefsFile";
    public static final String KEY_FIRST_TIME = "isFirstTime";

    private long exitTime = 0;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        blackParentBar();
        setFullScreen(false);
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

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(binding.navView, navController);
        testLoginStatus();

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

    private void testLoginStatus() {
        String token = AccountManager.getAccountData(MainActivity.this).getToken();
        String id = AccountManager.getAccountData(MainActivity.this).getID();
        String url = ApplicationConfig.HOST_API +
                "/search?act=searchArticle&id=" +
                id + "&token=" +
                token + "&keyword=" +
                "测试" + "&page=1&sort=hot&reverse=false";
        Log.e("MainActivity", "onResponse: 完整的URL地址=> " + url);

        apiGetMethod(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("MainActivity", "onResponse: 搜索文章接口错误=> " + e);
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    assert response.body() != null;
                    String json = response.body().string();
                    Log.e("MainActivity", "onResponse: 返回的数据=> " + json);

                    if (json.equals("Unauthorized: Invalid token or account not found")) {
                        MainActivity.this.runOnUiThread(() -> {
                            Toast.makeText(MainActivity.this, "您的登录状态已失效，请重新登录", Toast.LENGTH_LONG).show();

                            AccountManager.clearUserLoginData(MainActivity.this);
                            Intent i = new Intent(MainActivity.this, WebViewActivity.class);
                            i.putExtra("url", AccountManager.LOGIN_IN_URL);
                            i.putExtra("showMenu", false);
                            startActivity(i);
                        });
                    }
                } catch (Exception e) {
                    Log.e("MainActivity", "onResponse: 搜索文章接口错误=> " + e);
                }
            }
        });
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
