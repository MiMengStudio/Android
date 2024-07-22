package com.mimeng.BaseClass;

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
}
