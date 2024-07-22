package com.mimeng.BaseClass;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.LinearLayout;

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

    /**
     * 设置上边距的距离
     * @param v view元素
     * @param index 偏移率
     */
    public void setLayoutMarginTop(View v, int index) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, getStatusBarHeight() * index, 0, 0);
        v.setLayoutParams(params);
    }

}
