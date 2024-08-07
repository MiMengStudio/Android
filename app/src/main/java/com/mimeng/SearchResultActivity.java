package com.mimeng;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mimeng.base.BaseActivity;
import com.mimeng.databinding.ActivitySearchResultBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class SearchResultActivity extends BaseActivity {
    private final List<Integer> textViewXPositions = new ArrayList<>();
    private final List<TextView> typeTextViews = new ArrayList<>();
    private ActivitySearchResultBinding binding;

    @Override
    protected void onStart() {
        super.onStart();
        overridePendingTransition(0,0);
    }

    @SuppressLint("ResourceAsColor")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        blackParentBar();
        setFullScreen(false);

        binding = ActivitySearchResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.back.setOnClickListener(view -> {
            finish();
        });

        binding.searchEdit.requestFocus();

        LinearLayout parentLayout = binding.typeParentLayout; // 假设您的LinearLayout的id是parent_layout

        int[] location = new int[2];
        for (int i = 0; i < parentLayout.getChildCount(); i++) {
            View view = parentLayout.getChildAt(i);
            if (view instanceof TextView) {
                TextView tv = (TextView) view;
                // 获取每个TextView的中心点x坐标

                tv.getLocationInWindow(location);
                Log.d("p", tv.getText() + Arrays.toString(location));

                int centerX = location[0] + view.getWidth() / 2;
                textViewXPositions.add(centerX);
                typeTextViews.add(tv);
                tv.setOnClickListener(this::onTypeClick);
            }
        }

        // 初始化文本颜色和指示器位置
        for (TextView tv : typeTextViews) {
            tv.setTextColor(R.color.font_color_grey);
        }
        resetIndicator(0); // 选中第一个
    }

    private void onTypeClick(View view) {
        for (int i = 0; i < typeTextViews.size(); i++) {
            if (view == typeTextViews.get(i)) {
                resetIndicator(i);
                break;
            }
        }
    }

    private void resetIndicator(int index) {
        // 重置文本颜色
        for (TextView tv : typeTextViews) {
            tv.setTextColor(getResources().getColor(R.color.font_color_grey));
        }
        typeTextViews.get(index).setTextColor(getResources().getColor(R.color.colorPrimary)); // 选中的颜色

        // 移动指示器
        int position = textViewXPositions.get(index);
        Log.d("Position", "resetIndicator: "+ position);
        animateIndicatorToPosition(position);
    }

    private void animateIndicatorToPosition(int position) {
        binding.indicator.animate()
                .translationX(position - binding.indicator.getWidth() / 2)
                .setDuration(300) // 动画持续时间
                .start();
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
}
