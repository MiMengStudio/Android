package com.mimeng.BaseClass;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mimeng.App;

public class BaseFragment extends Fragment {

    private int topMarginTimes = 5;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        resetLayoutTopMargin(view, topMarginTimes);
    }

    protected void resetLayoutTopMargin(@NonNull View root, int topMarginTimes) {
        App.resetLayoutTopMargin(getContext(), root, topMarginTimes);
    }

    protected int getTopMarginTimes() {
        return topMarginTimes;
    }

    /**
     * 设置上边距的距离, Default to 5
     * @param topMarginTimes 偏移率
     */
    protected void setTopMarginTimes(int topMarginTimes) {
        this.topMarginTimes = topMarginTimes;
        if (getView() != null) {
            resetLayoutTopMargin(getView(), topMarginTimes);
        }
    }

    /**
     * 跳转到其他页面
     * @param c 类
     * @param <T> 泛型
     */
    protected <T> void toActivity( Class<T> c ){
        Intent i = new Intent(requireActivity(),c);
        requireActivity().startActivity(i);
    }
}
