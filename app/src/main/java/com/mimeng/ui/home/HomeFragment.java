package com.mimeng.ui.home;

import static androidx.core.content.ContextCompat.getSystemService;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;

import com.mimeng.ResourceManagementActivity;
import com.youth.banner.Banner;
import com.youth.banner.adapter.BannerImageAdapter;
import com.youth.banner.holder.BannerImageHolder;
import com.youth.banner.indicator.CircleIndicator;
import com.mimeng.R;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private Banner banner;
    private List<Integer> bannerData;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditText searchEdit = view.findViewById(R.id.search_edit);

        // 初始化轮播图和数据
        banner = view.findViewById(R.id.home_banner);
        initBannerData();

        banner.setAdapter(new BannerImageAdapter<Integer>(bannerData) {
            @Override
            public void onBindView(BannerImageHolder holder, Integer data, int position, int size) {
                holder.imageView.setImageResource(data);
            }
        });

        banner.setIndicator(new CircleIndicator(requireContext()));
        banner.start();

        View resourceManagement = view.findViewById(R.id.resourceManagement);
        resourceManagement.setOnClickListener(v -> {
            // 创建一个新的Intent来打开ResourceManagementActivity
            Intent intent = new Intent(view.getContext(), ResourceManagementActivity.class);

            // 使用动画资源ID创建ActivityOptions
            ActivityOptionsCompat options = ActivityOptionsCompat.makeCustomAnimation(
                    v.getContext(),
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
            );

            // 使用Intent和动画选项启动新的Activity
            ActivityCompat.startActivity(view.getContext(), intent, options.toBundle());
        });
    }

    // 初始化轮播图数据
    private void initBannerData() {
        bannerData = new ArrayList<>();
        bannerData.add(R.drawable.ad_sz_h);
        bannerData.add(R.drawable.ad_zm_h);
        bannerData.add(R.drawable.ad_saishi_h);
    }

}
