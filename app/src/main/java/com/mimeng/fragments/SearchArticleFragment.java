package com.mimeng.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.reflect.TypeToken;
import com.mimeng.ApiRequestManager;
import com.mimeng.App;
import com.mimeng.R;
import com.mimeng.activity.WebViewActivity;
import com.mimeng.adapters.ArticleRecAdapter;
import com.mimeng.base.BaseFragment;
import com.mimeng.databinding.FragmentSearchArticleBinding;
import com.mimeng.values.ArticleEntity;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SearchArticleFragment extends BaseFragment {

    private final String TAG = "SearchArticleFragment";
    private ArrayList<ArticleEntity> arData = new ArrayList<>();
    private ArticleRecAdapter adapter;

    public void search(String keyWord) {
        startSearchArticle(keyWord);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentSearchArticleBinding binding = FragmentSearchArticleBinding.inflate(inflater, container, false);

        RecyclerView recyclerView = binding.searchRecArticle;
        adapter = new ArticleRecAdapter(requireActivity());
        LinearLayoutManager manager = new LinearLayoutManager(requireActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        recyclerView.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.md_theme_background));

        // 文章点击监听
        adapter.setItemChangeListener(arId -> {
            Intent i = new Intent(requireContext(), WebViewActivity.class);
            i.putExtra("url", "");
            i.putExtra("showMenu", false);
            i.putExtra("showCloseBut", false);
            startActivity(i);
        });

        return binding.getRoot();
    }

    public void startSearchArticle(String word) {
        ApiRequestManager.DEFAULT.searchArticle(word, requireContext(),new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(Call call, Response response) {
                try {
                    assert response.body() != null;
                    String json = response.body().string();
                    Log.d(TAG, "onResponse: 返回的数据=> " + json);
                    arData = App.GSON.fromJson(json, new TypeToken<>() {
                    });
                    requireActivity().runOnUiThread(() -> {
                        adapter.setData(arData);
                        adapter.notifyDataSetChanged();
                    });
                } catch (Exception e) {
                    Log.e(TAG, "onResponse: 搜索文章接口错误=> " + e);
                }
            }
        });
    }
}
