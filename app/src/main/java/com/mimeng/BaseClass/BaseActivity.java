package com.mimeng.BaseClass;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.transition.Transition;
import android.transition.TransitionInflater;

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
}
