package com.mimeng.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.just.agentweb.AgentWeb;
import com.mimeng.R;
import com.mimeng.WebViewActivity;
import com.mimeng.databinding.FragmentUserBinding;
import com.mimeng.user.Account;

public class UserFragment extends Fragment {

    private FragmentUserBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 获取 Account 信息
        Account account = Account.get(UserFragment.this.getActivity());
        if (account != null) {
            Log.d("Account", "Retrieved Account Info: " + account.toString());
            // TODO 用户相关功能
        } else {
            Log.d("Account", "No Account Info found.");
        }

        View user_info = view.findViewById(R.id.user_info);
        user_info.setOnClickListener(view1 -> {
            Intent intent = new Intent(UserFragment.this.getActivity(), WebViewActivity.class);
            //intent.putExtra("url", "https://account.mimeng.fun");
            intent.putExtra("url", "https://account.mimeng.fun?origin=MiMengAndroidAPP");
            startActivity(intent);
        });
    }
}