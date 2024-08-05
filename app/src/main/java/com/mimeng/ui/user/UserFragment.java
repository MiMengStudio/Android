package com.mimeng.ui.user;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mimeng.base.BaseFragment;
import com.mimeng.R;
import com.mimeng.WebViewActivity;
import com.mimeng.databinding.FragmentUserBinding;
import com.mimeng.user.Account;
import com.mimeng.user.AccountManager;
import com.mimeng.user.SignInInfo;

import java.util.Locale;

public class UserFragment extends BaseFragment {
    public static final int REQUEST_LOGIN = 1;
    private FragmentUserBinding binding;
    private final AccountManager.AccountSignInTimeListener listener = this::reloadSignInLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentUserBinding.inflate(inflater, container, false);

        binding.userInfo.setOnClickListener(view1 -> {
            Intent intent = new Intent(UserFragment.this.getActivity(), WebViewActivity.class);
            intent.putExtra("url", AccountManager.LOGIN_IN_URL);
            intent.putExtra("showMenu", false);
            startActivityForResult(intent, REQUEST_LOGIN);
        });

        //价格表
        int[][] priceTable = {{19, 9}, {49, 24}, {178, 84}, {298, 168}};
        View[] vips = {
                binding.vipOneMonth,
                binding.vipThreeMonth,
                binding.vipOneYear,
                binding.vipForever
        };
        final View[] vipSelected = {vips[0]};
        for (int vipIndex = 0; vipIndex < vips.length; ++vipIndex) {
            View vip = vips[vipIndex];
            int newPrice = priceTable[vipIndex][1],
                 oldPrice = priceTable[vipIndex][0];
            vips[vipIndex].setOnClickListener(_v -> {
                vipSelected[0].setBackgroundResource(R.color.background_white);
                vipSelected[0] = vip;
                vip.setBackgroundResource(R.drawable.bg_vip_buy);
                binding.priceActual.setText(String.format(Locale.getDefault(), "%d", newPrice));
                binding.priceDiscounted.setText(String.format(Locale.getDefault(), "已优惠￥%d", oldPrice - newPrice));
            });
        }

        binding.vipBuy.setOnClickListener(view1 -> {
            // TODO: 购买会员
            Toast.makeText(UserFragment.this.getActivity(), "支付系统维护中，请加Q群联系客服购买", Toast.LENGTH_LONG).show();
            Log.d("UserFragment", "Buy VIP button clicked");
        });

        binding.joinQqGroup.setOnClickListener(view1 -> joinQQGroup("G4thHaZyCI"));

        binding.helpAndFeedback.setOnClickListener(view1 -> {
                    Intent intent = new Intent(UserFragment.this.getActivity(), WebViewActivity.class);
                    intent.putExtra("url", "https://support.qq.com/products/405243");
                    intent.putExtra("showMenu", true);
                    startActivity(intent);
                }
        );

        AccountManager.addSignInDateUpdateListener(listener);

        binding.fragmentUserSignInLayout.setOnClickListener(_v -> {
            if (AccountManager.hasLoggedIn()) {
                AccountManager.performSigningIn();
            } else {
                Toast.makeText(requireActivity(), "你还没登录呢", Toast.LENGTH_SHORT).show();
            }
        });

        loadUserLayout();
        return binding.getRoot();
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

    private void reloadSignInLayout(SignInInfo info) {
        switch (info) {
            case SIGNED_SUCCESSFUL:
                requireActivity().runOnUiThread(
                        () -> Toast.makeText(requireActivity(),
                                "签到成功", Toast.LENGTH_SHORT).show());
                // 签到成功的同时也更新UserFragment的UI
            case SIGNED_IN: // fall through
                requireActivity().runOnUiThread(
                        () -> {
                            // 设置文本颜色
                            binding.fragmentUserSignInTextView.setTextColor(
                                    requireContext().getResources().getColor(R.color.colorPrimary, null));
                            // 设置文本内容
                            binding.fragmentUserSignInTextView.setText("已签到");
                        }
                );
                break;
            case NEED_SIGN_IN:
                requireActivity().runOnUiThread(
                        () -> binding.fragmentUserSignInTextView.setText("未签到")
                );
                break;
            case INVALID_TOKEN:
            case USER_NOT_FOUND:
            case UNKNOWN_ERROR:
                break;
            default:
                requireActivity().runOnUiThread(
                        () -> Toast.makeText(requireActivity(),
                                info.getErrorMsg(), Toast.LENGTH_SHORT).show()
                );
        }
    }

    private void loadUserLayout() {
        // 获取 Account 信息
        if (AccountManager.hasLoggedIn()) {
            Account account = AccountManager.get();
            Log.d("Account", "Retrieved Account Info: " + account);
            // TODO 用户相关功能
            AccountManager.loadUserIcon(binding.head);

            binding.userName.setText(account.getName());
            if (account.isVip()) {
                binding.userVip.setImageResource(R.drawable.ic_vip_activat);
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
                    loadUserLayout();
                }
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        AccountManager.removeSignInDateUpdateListener(listener);
    }
}