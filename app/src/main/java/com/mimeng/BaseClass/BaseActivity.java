package com.mimeng.BaseClass;

import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {
    public <T> void toMainActivity(Context context, Class<T> tClass){
        Intent i = new Intent(context,tClass);
        startActivity(i);
        finish();
    }
}
