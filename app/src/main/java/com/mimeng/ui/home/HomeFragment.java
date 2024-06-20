package com.mimeng.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mimeng.R;

public class HomeFragment extends Fragment {

    /**
     * 建议采用直接引入布局文件即可，不用使用View Model，那样太麻烦了
     * 不引用ViewModel需要重载几个抽象方法：onCreateView、onViewCreate
     */

    // 这个方法用来绑定布局
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 这个是原始的，注释掉它，使用LayoutInflater方法
        // return super.onCreateView(inflater, container, savedInstanceState);

        return LayoutInflater.from(requireActivity()).inflate(R.layout.fragment_home,container,false);
    }

    // 这个方法用来绑定布局中的控件(Id)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // 下面我将做个如何使用TextView控件的示例，要找到控件id，必须使用onViewCreated的形参(view)
        TextView textView = view.findViewById(R.id.text_home);
        textView.setText("这是首页");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
