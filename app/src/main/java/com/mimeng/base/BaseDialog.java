package com.mimeng.base;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;

public class BaseDialog extends Dialog {
    private final int resLayout;

    public BaseDialog(@NonNull Activity context, int themeResId, int resLayout) {
        super(context, themeResId);
        this.resLayout = resLayout;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(resLayout);
    }
}
