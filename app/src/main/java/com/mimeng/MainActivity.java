package com.mimeng;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.mimeng.BaseClass.BaseActivity;
import com.mimeng.databinding.ActivityMainBinding;

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
            Toast.makeText(this, "再按一次退出应用", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish(); // 用户两次点击返回键，退出应用
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v,ev)) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    assert v != null;
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 隐藏软键盘(EditText)
     * @param v 视图
     * @return 返回
     */
    public  boolean isShouldHideInput(View v,MotionEvent event) {
            if ((v instanceof EditText)) {
                int[] leftTop = { 0, 0 };
                //获取输入框当前的location位置
                v.getLocationInWindow(leftTop);
                int left = leftTop[0];
                int top = leftTop[1];
                int bottom = top + v.getHeight();
                int right = left + v.getWidth();
                v.clearFocus();
                // 点击的是输入框区域，保留点击EditText的事件
                return !(event.getX() > left) || !(event.getX() < right)
                        || !(event.getY() > top) || !(event.getY() < bottom);
            }
            return false;

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null; // 避免内存泄漏
    }
}
