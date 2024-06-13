package com.mimeng.BaseClass;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Build;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mimeng.R;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TransitionInflater inflater = TransitionInflater.from(this);
        Transition transition = inflater.inflateTransition(R.transition.transition);
        getWindow().setEnterTransition(transition);
        getWindow().setExitTransition(transition);
    }

    /**
     * 点击返回键时，不返回到当前Activity时使用
     * @param context 上下文
     * @param tClass  跳转到目标类
     * @param <T>     泛型
     */
    public <T> void toMainActivity(Context context, Class<T> tClass){
        Intent i = new Intent(context,tClass);
        startActivity(i);
        finish();
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
    }

    /**
     * 不传任何参数跳转
     * @param context 上下文
     * @param tClass  跳转到目标类
     * @param <T>     泛型
     */
    public <T> void toActivityNotData(Context context,Class<T> tClass){
        Intent i = new Intent(context,tClass);
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this);
        startActivity(i,options.toBundle());
    }

    /**
     * 带参数跳转
     * @param context 上下文
     * @param bundle  bundle对象
     * @param tClass  跳转到目标类
     * @param <T>     泛型
     */
    public <T> void toActivityHasBundle(Context context, Bundle bundle, Class<T> tClass){
        Intent i = new Intent(context,tClass);
        i.putExtras(bundle);
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this);
        startActivity(i,options.toBundle());
    }

    /**
     * get请求
     * @param url       网址
     * @param callback  回调
     */
    public void ApiGetMethod(String url, Callback callback){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        client.newCall(request).enqueue(callback);
    }

    /**
     * 黑色字体状态栏
     */
    public void blackParentBar() {
        //使状态栏完全透明，条件，需要在Theme文件样式代码中，加上<item name="android:windowTranslucentStatus">true</item>"
        Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//设置状态栏字体颜色
    }
    
    /**
     * 全屏且适配全面屏刘海
     */
    public void setFullScreen(boolean hideSystemUI) {
        // 实现全屏显示
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_FULLSCREEN;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 状态栏颜色设置为透明，适用于Android 6.0及以上版本
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        // 适配刘海屏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            getWindow().setAttributes(lp);
        }
        
        if (hideSystemUI) {
            decorView.setSystemUiVisibility(uiOptions);
            // 监听系统UI可见性变化，确保状态栏和导航栏隐藏
            decorView.setOnSystemUiVisibilityChangeListener(
                    visibility -> {
                        if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                            decorView.setSystemUiVisibility(uiOptions);
                        }
                    });
            }
    }
}
