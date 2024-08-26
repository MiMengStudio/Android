package com.mimeng.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mimeng.ApiRequestManager;
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

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class HomeFragment extends BaseFragment {
    private final AccountManager.AccountSignInTimeListener listener = info -> {
        switch (info) {
            case SIGNED_SUCCESSFUL:
            case SIGNED_IN:// fall through
                // TODO: 去掉红点
        }
        return false;
    };
    private final String TAG = "HomeFragment";
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
        initBannerData(inflater);

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
    private void initBannerData(LayoutInflater inflater) {
        banner = binding.homeBanner;
        imgData = new ArrayList<>();
        ApiRequestManager.getContent().getBannerApi(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    assert response.body() != null;
                    String json = response.body().string();
                    Log.d(TAG, "onResponse: 获取轮播图接口数据 => "+json );
                    imgData = new Gson().fromJson(json, new TypeToken<>() {
                    });
                    requireActivity().runOnUiThread(() -> {
                        banner.setAdapter(new ImageUrlBanner(imgData, requireContext()))
                                .setIndicator(new CircleIndicator(inflater.getContext()));
                        banner.start();
                        banner.setOnBannerListener((data, position) -> {
                            Intent intent = new Intent(requireContext(), WebViewActivity.class);
                            intent.putExtra("url", data.getUrl());
                            intent.putExtra("showMenu", true);
                            startActivity(intent);
                        });
                    });
                }catch (Exception e){
                    Log.e(TAG, "onResponse: 获取轮播图接口错误 => "+e );
                }

            }
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