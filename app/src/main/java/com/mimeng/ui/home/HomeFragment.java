package com.mimeng.ui.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mimeng.BaseClass.BaseFragment;
import com.mimeng.R;
import com.mimeng.ResourceManagementActivity;
import com.mimeng.ui.search.SearchPage;
import com.mimeng.user.Account;
import com.mimeng.user.AccountManager;
import com.youth.banner.Banner;
import com.youth.banner.adapter.BannerImageAdapter;
import com.youth.banner.holder.BannerImageHolder;
import com.youth.banner.indicator.CircleIndicator;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends BaseFragment {

    private Banner banner;
    private List<Integer> bannerData;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @SuppressLint({"InternalInsetResource", "DiscouragedApi"})
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditText searchEdit = view.findViewById(R.id.search_edit);
        LinearLayout goSearch = view.findViewById(R.id.search);

        goSearch.setOnClickListener(view1 -> toActivity(SearchPage.class));

        if (AccountManager.hasLoggedIn()) {
            Account account = AccountManager.get();
            Log.d("Account", "Retrieved Account Info: " + account);
            // TODO 用户相关功能
            ImageView userImage = view.findViewById(R.id.user);
            AccountManager.loadUserIcon(userImage);
        } else {
            Log.d("Account", "No Account Info found.");
        }

        int offsetId = requireActivity().getResources().getIdentifier("status_bar_height", "dimen", "android");
        int statusBarHeight = requireActivity().getResources().getDimensionPixelOffset(offsetId) / 5;
        RelativeLayout home_fragment_tool = view.findViewById(R.id.home_fragment_tool);
        home_fragment_tool.setPadding(0, statusBarHeight, 0, 0);

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
            startActivity(intent);
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