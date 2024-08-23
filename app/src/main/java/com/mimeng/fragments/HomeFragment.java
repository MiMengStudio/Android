package com.mimeng.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mimeng.R;
import com.mimeng.activity.ResourceManagementActivity;
import com.mimeng.activity.SearchActivity;
import com.mimeng.activity.WebViewActivity;
import com.mimeng.adapters.ImageUrlBanner;
import com.mimeng.base.BaseFragment;
import com.mimeng.databinding.FragmentHomeBinding;
import com.mimeng.user.Account;
import com.mimeng.user.AccountManager;
import com.mimeng.values.BannerEntity;
import com.youth.banner.Banner;
import com.youth.banner.indicator.CircleIndicator;

import java.util.ArrayList;

public class HomeFragment extends BaseFragment {
    private final AccountManager.AccountSignInTimeListener listener = info -> {
        switch (info) {
            case SIGNED_SUCCESSFUL:
            case SIGNED_IN:// fall through
                // TODO: 去掉红点
        }
        return false;
    };
    private Banner<BannerEntity, ImageUrlBanner> banner;
    private FragmentHomeBinding binding;
    private ArrayList<BannerEntity> imgData;

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
        banner = binding.homeBanner;
        initBannerData();

        banner.setAdapter(new ImageUrlBanner(imgData, requireContext()))
                .setIndicator(new CircleIndicator(inflater.getContext()));
        banner.start();

        banner.setOnBannerListener((data, position) -> {
            Toast.makeText(requireContext(), "数据：" + data.getLink(), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(requireContext(), WebViewActivity.class);
            intent.putExtra("url", data.getLink());
            intent.putExtra("showMenu", true);
            startActivity(intent);
        });

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
        imgData = new ArrayList<>();
        // 暂时使用这部分代码模拟后端数据
        String json = """
                [
                            {
                                image:"https://app.mimeng.fun/images/sz.jpg",
                                link:"https://www.song3060.top/"
                            },
                            {
                                image:"https://app.mimeng.fun/images/zm.png",
                                link:"https://qm.qq.com/cgi-bin/qm/qr?_wv=1027&k=ygP92i-6Ibak91FyC_9D4qtzId46mEEc&authKey=L8xGeuDo7tEIAWCXovup8OqW1v1%2FYqJk%2B8BdRjovloPm13cv7ZGhQBTo%2BBulaWrS&noverify=0&group_code=826879869"
                            },
                            {
                                image:"https://apicdn.likepoems.com/images/pixiv/6N55222VKDI4249xp5Qw.png",
                                link:"https://apicdn.likepoems.com/images/pixiv/6N55222VKDI4249xp5Qw.png"
                            }
                        ]""";
        imgData = new Gson().fromJson(json, new TypeToken<>() {
        });


    }

    @Override
    public void onResume() {
        super.onResume();
        banner.start();
    }

    @Override
    public void onStop() {
        super.onStop();
        banner.stop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        banner = null;
        AccountManager.removeSignInDateUpdateListener(listener);
    }
}