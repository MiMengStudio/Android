package com.mimeng.base;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.errorprone.annotations.concurrent.LazyInit;
import com.mimeng.ApiRequestManager;
import com.mimeng.App;
import com.mimeng.R;

import java.util.Map;

public class BaseFragment extends Fragment {

    private int topMarginTimes = 5;
    @LazyInit
    private ApiRequestManager requestManager;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        resetLayoutTopMargin(view, topMarginTimes);

        TransitionInflater inflater = TransitionInflater.from(requireActivity());
        Transition transition = inflater.inflateTransition(R.transition.transition);
        requireActivity().getWindow().setEnterTransition(transition);
        requireActivity().getWindow().setExitTransition(transition);
    }

    protected void resetLayoutTopMargin(@NonNull View root, int topMarginTimes) {
        App.resetLayoutTopMargin(getContext(), root, topMarginTimes);
    }

    protected int getTopMarginTimes() {
        return topMarginTimes;
    }

    /**
     * 设置上边距的距离, Default to 5
     *
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
     *
     * @param c   目标界面
     * @param <T> 泛型
     */
    protected <T> void toActivity(Class<T> c) {
        Intent i = new Intent(requireActivity(), c);
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(requireActivity());
        requireActivity().startActivity(i, options.toBundle());
    }

    protected void runOnUiThread(@NonNull Runnable action) {
        requireActivity().runOnUiThread(action);
    }

    /**
     * 跳转到其他页面
     *
     * @param c   类
     * @param <T> 泛型
     */
    protected <T> void toActivityHaveData(Class<T> c, Map<String, Object> values) {
        Intent i = new Intent(requireActivity(), c);
        values.forEach((key, value) -> {
            if (value instanceof Long) i.putExtra(key, (Long) value);
            if (value instanceof Float) i.putExtra(key, (Float) value);
            if (value instanceof String) i.putExtra(key, (String) value);
            if (value instanceof Double) i.putExtra(key, (Double) value);
            if (value instanceof Boolean) i.putExtra(key, (Boolean) value);
            if (value instanceof Integer) i.putExtra(key, (Integer) value);
        });
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(requireActivity());
        requireActivity().startActivity(i, options.toBundle());
    }
}
