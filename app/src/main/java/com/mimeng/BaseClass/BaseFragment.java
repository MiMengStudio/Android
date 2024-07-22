package com.mimeng.BaseClass;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class BaseFragment extends Fragment {

    /**
     * 获取状态栏高度偏移5dp
     * @return 返回高度
     */
    public int getStatusBarHeight(){
        @SuppressLint("InternalInsetResource")
        int offsetId = requireActivity().getResources().getIdentifier("status_bar_height", "dimen", "android");
        return requireActivity().getResources().getDimensionPixelOffset(offsetId) / 5;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // 适配异形ui屏
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
            ((ViewGroup.MarginLayoutParams) layoutParams).topMargin = getStatusBarHeight() * 5;
        }
    }
}
