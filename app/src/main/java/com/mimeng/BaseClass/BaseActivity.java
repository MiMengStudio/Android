package com.mimeng.BaseClass;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {

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
    }

    /**
     * 不传任何参数跳转
     * @param context 上下文
     * @param tClass  跳转到目标类
     * @param <T>     泛型
     */
    public <T> void toActivityNotData(Context context,Class<T> tClass){
        Intent i = new Intent(context,tClass);
        startActivity(i);
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
        startActivity(i);
    }
}
