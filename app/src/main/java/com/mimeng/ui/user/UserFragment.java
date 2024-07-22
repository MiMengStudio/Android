package com.mimeng.ui.user;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mimeng.BaseClass.BaseFragment;
import com.mimeng.R;
import com.mimeng.WebViewActivity;
import com.mimeng.user.Account;
import com.mimeng.user.AccountManager;
import com.squareup.picasso.Picasso;

public class UserFragment extends BaseFragment {
    public static final int REQUEST_LOGIN = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayout fragment_user_top = view.findViewById(R.id.fragment_user_top);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, getStatusBarHeight() * 5, 0, 0);
        fragment_user_top.setLayoutParams(params);

        // 获取 Account 信息
        if (AccountManager.hasLoggedIn()) {
            Account account = AccountManager.get();
            Log.d("Account", "Retrieved Account Info: " + account);
            // TODO 用户相关功能
            ImageView headImage = view.findViewById(R.id.head);
            AccountManager.loadUserIcon(headImage);
            TextView userNameText = view.findViewById(R.id.user_name);
            userNameText.setText(account.getName());
            if (account.isVip()) {
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
            intent.putExtra("showMenu", false);
            startActivityForResult(intent, REQUEST_LOGIN);
        });

        //价格表
        int[][] priceTable = {{19, 9}, {49, 24}, {178, 84}, {298, 168}};
        View vip1 = view.findViewById(R.id.vip_one_month);
        View vip2 = view.findViewById(R.id.vip_three_month);
        View vip3 = view.findViewById(R.id.vip_one_year);
        View vip4 = view.findViewById(R.id.vip_forever);
        final View[] vipSelected = {vip1};
        TextView ActualPrice = view.findViewById(R.id.price_actual);
        TextView DiscountedPrice = view.findViewById(R.id.price_discounted);
        int vipIndex = 0;
        for (View vip : new View[]{vip1, vip2, vip3, vip4}) {
            final int currentIndex = vipIndex;
            vipIndex++;
            vip.setOnClickListener(view1 -> {
                vipSelected[0].setBackgroundResource(R.color.background_white);
                vipSelected[0] = vip;
                vip.setBackgroundResource(R.drawable.bg_vip_buy);
                ActualPrice.setText(String.format("%d", priceTable[currentIndex][1]));
                DiscountedPrice.setText(String.format("已优惠￥%d", priceTable[currentIndex][0] - priceTable[currentIndex][1]));
            });
        }

        View buyVip = view.findViewById(R.id.vip_buy);

        buyVip.setOnClickListener(view1 -> {
            // TODO: 购买会员
            Toast.makeText(UserFragment.this.getActivity(), "支付系统维护中，请加Q群联系客服购买", Toast.LENGTH_LONG).show();
            Log.d("UserFragment", "Buy VIP button clicked");
        });

        View joinGroup = view.findViewById(R.id.join_qq_group);
        joinGroup.setOnClickListener(view1 -> {
            joinQQGroup("G4thHaZyCI");
        });

        View getHelp = view.findViewById(R.id.help_and_feedback);

        getHelp.setOnClickListener(view1 -> {
                    Intent intent = new Intent(UserFragment.this.getActivity(), WebViewActivity.class);
                    intent.putExtra("url", "https://support.qq.com/products/405243");
                    intent.putExtra("showMenu", true);
                    startActivity(intent);
                }
        );
    }

    public void joinQQGroup(String key) {
        Intent intent = new Intent();
        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D" + key));
        try {
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(UserFragment.this.getActivity(), "未安装手Q或安装的版本不支持", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
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