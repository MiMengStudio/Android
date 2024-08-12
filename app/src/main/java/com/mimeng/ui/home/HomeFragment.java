package com.mimeng.ui.home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mimeng.base.BaseFragment;
import com.mimeng.R;
import com.mimeng.activity.ResourceManagementActivity;
import com.mimeng.activity.SearchActivity;
import com.mimeng.databinding.FragmentHomeBinding;
import com.mimeng.user.Account;
import com.mimeng.user.AccountManager;
import com.youth.banner.Banner;
import com.youth.banner.adapter.BannerImageAdapter;
import com.youth.banner.holder.BannerImageHolder;
import com.youth.banner.indicator.CircleIndicator;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends BaseFragment {
    private final AccountManager.AccountSignInTimeListener listener = info -> {
        switch (info) {
            case SIGNED_SUCCESSFUL:
            case SIGNED_IN:// fall through
                // TODO: 去掉红点
        }
    };
    private FragmentHomeBinding binding;
    private List<Integer> bannerData;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        if (AccountManager.hasLoggedIn()) {
            Account account = AccountManager.get();
            Log.d("Account", "Retrieved Account Info: " + account);
            // TODO 用户相关功能
            AccountManager.loadUserIcon(binding.user);
            AccountManager.addSignInDateUpdateListener(listener);
        } else {
            Log.d("Account", "No Account Info found.");
        }

        // 初始化轮播图和数据
        Banner<Integer, BannerImageAdapter<Integer>> banner = binding.homeBanner;
        initBannerData();

        banner.setAdapter(new BannerImageAdapter<>(bannerData) {
            @Override
            public void onBindView(BannerImageHolder holder, Integer data, int position, int size) {
                holder.imageView.setImageResource(data);
            }
        });

        banner.setIndicator(new CircleIndicator(inflater.getContext()));
        banner.start();

        binding.search.setOnClickListener(v -> {
            Intent intent = new Intent(inflater.getContext(), SearchActivity.class);
            startActivity(intent);
            ((Activity) inflater.getContext()).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

        binding.resourceManagement.setOnClickListener(v -> {
            // 创建一个新的Intent来打开ResourceManagementActivity
            toActivity(ResourceManagementActivity.class);
        });

        return binding.getRoot();
    }

    // 初始化轮播图数据
    private void initBannerData() {
        bannerData = new ArrayList<>();
        bannerData.add(R.drawable.ad_sz_h);
        bannerData.add(R.drawable.ad_zm_h);
        bannerData.add(R.drawable.ad_saishi_h);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        AccountManager.removeSignInDateUpdateListener(listener);
    }
}