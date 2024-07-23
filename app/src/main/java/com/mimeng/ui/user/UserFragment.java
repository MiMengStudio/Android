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

import java.util.Locale;

public class UserFragment extends BaseFragment {
    public static final int REQUEST_LOGIN = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        loadUserLayout(view);

        LinearLayout user_info = view.findViewById(R.id.user_info);
        user_info.setOnClickListener(view1 -> {
            Intent intent = new Intent(UserFragment.this.getActivity(), WebViewActivity.class);
            intent.putExtra("url", "https://account.mimeng.fun?origin=MiMengAndroidAPP");
            intent.putExtra("showMenu", false);
            startActivityForResult(intent, REQUEST_LOGIN);
        });

        //价格表
        int[][] priceTable = {{19, 9}, {49, 24}, {178, 84}, {298, 168}};
        View[] vips = {
                view.findViewById(R.id.vip_one_month),
                view.findViewById(R.id.vip_three_month),
                view.findViewById(R.id.vip_one_year),
                view.findViewById(R.id.vip_forever)
        };
        final View[] vipSelected = {vips[0]};
        TextView actualPriceText = view.findViewById(R.id.price_actual);
        TextView discountedPriceText = view.findViewById(R.id.price_discounted);
        for (int vipIndex = 0; vipIndex < vips.length; ++vipIndex) {
            View vip = vips[vipIndex];
            int newPrice = priceTable[vipIndex][1],
                 oldPrice = priceTable[vipIndex][0];
            vips[vipIndex].setOnClickListener(_v -> {
                vipSelected[0].setBackgroundResource(R.color.background_white);
                vipSelected[0] = vip;
                vip.setBackgroundResource(R.drawable.bg_vip_buy);
                actualPriceText.setText(String.format(Locale.getDefault(), "%d", newPrice));
                discountedPriceText.setText(String.format(Locale.getDefault(), "已优惠￥%d", oldPrice - newPrice));
            });
        }

        View buyVip = view.findViewById(R.id.vip_buy);
        buyVip.setOnClickListener(view1 -> {
            // TODO: 购买会员
            Toast.makeText(UserFragment.this.getActivity(), "支付系统维护中，请加Q群联系客服购买", Toast.LENGTH_LONG).show();
            Log.d("UserFragment", "Buy VIP button clicked");
        });

        View joinGroup = view.findViewById(R.id.join_qq_group);
        joinGroup.setOnClickListener(view1 -> joinQQGroup("G4thHaZyCI"));

        View getHelp = view.findViewById(R.id.help_and_feedback);
        getHelp.setOnClickListener(view1 -> {
                    Intent intent = new Intent(UserFragment.this.getActivity(), WebViewActivity.class);
                    intent.putExtra("url", "https://support.qq.com/products/405243");
                    intent.putExtra("showMenu", true);
                    startActivity(intent);
                }
        );
        return view;
    }

    private void joinQQGroup(String key) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D" + key));
        try {
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(UserFragment.this.getActivity(), "未安装手Q或安装的版本不支持", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadUserLayout(@NonNull View root) {
        // 获取 Account 信息
        if (AccountManager.hasLoggedIn()) {
            Account account = AccountManager.get();
            Log.d("Account", "Retrieved Account Info: " + account);
            // TODO 用户相关功能
            ImageView headImage = root.findViewById(R.id.head);
            AccountManager.loadUserIcon(headImage);

            TextView userNameText = root.findViewById(R.id.user_name);
            userNameText.setText(account.getName());
            if (account.isVip()) {
                ImageView userVipImage = root.findViewById(R.id.user_vip);
                userVipImage.setImageResource(R.drawable.ic_vip_activat);
            }
        } else {
            Log.d("Account", "No Account Info found.");
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
                    loadUserLayout(requireView());
                }
            }
        }
    }

}