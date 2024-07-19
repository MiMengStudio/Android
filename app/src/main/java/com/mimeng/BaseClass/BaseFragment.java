package com.mimeng.BaseClass;

import android.annotation.SuppressLint;

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

}
