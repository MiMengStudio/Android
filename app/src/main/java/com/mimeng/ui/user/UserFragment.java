package com.mimeng.ui.user;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mimeng.BaseClass.BaseFragment;
import com.mimeng.R;
import com.mimeng.WebViewActivity;
import com.mimeng.user.Account;
import com.squareup.picasso.Picasso;

public class UserFragment extends BaseFragment {
    public static final int REQUEST_LOGIN = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayout fragment_user_top = view.findViewById(R.id.fragment_user_top);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0,getStatusBarHeight()*5,0,0);
        fragment_user_top.setLayoutParams(params);

        // 获取 Account 信息
        Account account = Account.get(UserFragment.this.requireActivity());
        if (account != null) {
            Log.d("Account", "Retrieved Account Info: " + account.toString());
            // TODO 用户相关功能
            ImageView headImage = view.findViewById(R.id.head);
            Picasso.get()
                    .load("https://q1.qlogo.cn/g?b=qq&nk=" + account.getQQ()+ "&s=100")
                    .placeholder(R.drawable.ic_default_head)
                    .error(R.drawable.ic_default_head)
                    .into(headImage);
            TextView userNameText = view.findViewById(R.id.user_name);
            userNameText.setText(account.getName());
            long currentTimeMillis = System.currentTimeMillis();
            long vipDateMillis = account.getVipDate();
            if (currentTimeMillis < vipDateMillis) {
                ImageView userVipImage = view.findViewById(R.id.user_vip);
                userVipImage.setImageResource(R.drawable.ic_vip_activat);
            }
        } else {
            Log.d("Account", "No Account Info found.");
        }

        View user_info = view.findViewById(R.id.user_info);
        user_info.setOnClickListener(view1 -> {
            Intent intent = new Intent(UserFragment.this.getActivity(), WebViewActivity.class);
            intent.putExtra("url", "https://account.mimeng.fun?origin=MiMengAndroidAPP");
            startActivityForResult(intent, REQUEST_LOGIN);
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_LOGIN && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                String accountInfo = data.getStringExtra("accountInfo");
                if (accountInfo != null) {
                    // 更新 UI 或处理登录结果

                    Log.d("UserFragment", "Received login result: " + accountInfo);
                }
            }
        }
    }

}