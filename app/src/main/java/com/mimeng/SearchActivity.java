package com.mimeng;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.mimeng.BaseClass.BaseActivity;
import com.mimeng.R;
import com.mimeng.databinding.ActivitySearchBinding;


public class SearchActivity extends BaseActivity {

    private ActivitySearchBinding binding;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        blackParentBar();
        setFullScreen(false);

        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.back.setOnClickListener(view -> {
            finish();
        });

        binding.searchEdit.requestFocus();

        binding.search.setOnClickListener(v -> {
            String search = binding.searchEdit.getText().toString();
            if (search.length() > 0) {
                Intent intent = new Intent(this, SearchResultActivity.class);
                intent.putExtra("search", search);
                startActivity(intent);
                overridePendingTransition(0,0);
            }
        });
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
     * @param event 触摸\点击事件
     * @return 返回
     */
    public boolean isShouldHideInput(View v, MotionEvent event) {
        if (v instanceof EditText) {
            Rect rect = new Rect();
            v.getGlobalVisibleRect(rect);
            // 如果点击事件在EditText的范围内，则不隐藏软键盘
            return !rect.contains((int) event.getRawX(), (int) event.getRawY());
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
