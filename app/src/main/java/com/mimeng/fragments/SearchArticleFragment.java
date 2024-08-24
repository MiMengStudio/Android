package com.mimeng.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import com.mimeng.user.Account;
import com.mimeng.values.ArticleEntity;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SearchArticleFragment extends BaseFragment {

    private final String TAG = "SearchArticleFragment";
    private final Account account = ApiRequestManager.DEFAULT.getAccountData();
    private int count = 1;
    private String keyWord;
    private boolean canRequest = true;
    private ArticleRecAdapter adapter;
    private SmartRefreshLayout smartRefreshLayout;
    private ArrayList<ArticleEntity> arData = new ArrayList<>();

    public void search(String keyWord) {
        this.keyWord = keyWord;
        startSearchArticle(keyWord, true);
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
            assert account != null;
            String id = account.getID();
            String token = account.getToken();
            String url = "http://127.0.0.1:5500/index.html?id=" + arId + "&account=" + id + "&token=" + token;
            Toast.makeText(requireContext(), "参数 " + url, Toast.LENGTH_SHORT).show();
            Intent i = new Intent(requireContext(), WebViewActivity.class);
            i.putExtra("url", url);
            i.putExtra("showMenu", false);
            i.putExtra("showCloseBut", false);
            startActivity(i);
        });

        smartRefreshLayout = binding.smartRefresh;
        smartRefreshLayout.setOnRefreshListener(refreshLayout12 -> {
            count = 1;
            canRequest = true;
            startSearchArticle(keyWord, true);
        });

        smartRefreshLayout.setOnLoadMoreListener(refreshLayout1 -> {
            count++;
            if (canRequest) {
                startSearchArticle(keyWord, false);
                return;
            }
            Toast.makeText(requireContext(), "到底啦！", Toast.LENGTH_SHORT).show();
            smartRefreshLayout.finishLoadMore();
        });

        return binding.getRoot();
    }

    public void startSearchArticle(String word, boolean isRefresh) {
        ApiRequestManager.DEFAULT.searchArticle(word, count, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                smartRefreshLayout.finishLoadMore();
                smartRefreshLayout.finishRefresh();
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(Call call, Response response) {
                try {
                    assert response.body() != null;
                    String json = response.body().string();
                    Log.d(TAG, "onResponse: 返回的数据=> " + json);

                    requireActivity().runOnUiThread(() -> {
                        smartRefreshLayout.finishLoadMore();
                        smartRefreshLayout.finishRefresh();
                        if (isRefresh) {
                            arData = App.GSON.fromJson(json, new TypeToken<>() {
                            });
                        } else {
                            ArrayList<ArticleEntity> data = App.GSON.fromJson(json, new TypeToken<>() {
                            });
                            if (data.isEmpty()) {
                                count = count > 1 ? --count : 1;
                                canRequest = false;
                                Toast.makeText(requireContext(), "到底啦！", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            arData.addAll(data);
                        }
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
