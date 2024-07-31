package com.mimeng.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mimeng.Adapter.ArticleRecAdapter;
import com.mimeng.ApplicationConfig;
import com.mimeng.BaseClass.BaseFragment;
import com.mimeng.EntityClass.ArticleEntity;
import com.mimeng.databinding.FragmentSearchArticleBinding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SearchArticleFragment extends BaseFragment {

    private final ArrayList<ArticleEntity> arData = new ArrayList<>();
    private final String TAG = "SearchArticleFragment";
    private FragmentSearchArticleBinding binding;
    private ArticleRecAdapter adapter;
    private final Handler handler = new Handler() {
        @Override
        public void publish(LogRecord record) {

        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void flush() {
            adapter.setData(arData);
            adapter.notifyDataSetChanged();
        }

        @Override
        public void close() throws SecurityException {

        }
    };

    public void search(String keyWord) {
        startSearchArticle(keyWord);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSearchArticleBinding.inflate(inflater, container, false);

        RecyclerView recyclerView = binding.searchRecArticle;
        adapter = new ArticleRecAdapter(requireActivity());
        LinearLayoutManager manager = new LinearLayoutManager(requireActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);

//        ArticleEntity entity = new ArticleEntity();
//        for (int i = 0; i < 10; i++) {
//            entity.setAuthor("远赤");
//            entity.setLikes(180);
//            entity.setCommentCounter(40);
//            entity.setShares(102);
//            entity.setTitle("模拟数据");
//            entity.setOutline("这是一个本地for循环模拟的数据 这是一个本地for循环模拟的数据 这是一个本地for循环模拟的数据 这是一个本地for循环模拟的数据");
//            entity.setPublishDate(264620650);
//            entity.setHead("http://thirdqq.qlogo.cn/ek_qqapp/AQUZ5zj1CrS1ux8BlmnlAibI81zZLtwqUVrFQgXvNTbYs5ichEHdDEaLHreyrrxHibZoNiaK5icUYQDVjKpLfjXsdBgft2HRlnXJ9Dx5XIcSvKpib88Bueiaww/100");
//            arData.add(entity);
//        }
//        handler.flush();

        return binding.getRoot();
    }

    public void startSearchArticle(String word) {
        Toast.makeText(requireActivity(), "(SearchArticleFragment)输入的文字：" + word, Toast.LENGTH_SHORT).show();

        String url = ApplicationConfig.HOST_API +
                "/search?act=searchArticle&id=Test123&token=d7fvJ2CK7Hcbesq10iyOTykJbZJLnDar4wsh4JCezODN5JDNmFLe36QVX3Cg96ar&keyword=" +
                word + "&page=1&sort=hot&reverse=false";

        apiGetMethod(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    assert response.body() != null;
                    String json = response.body().string();
                    Log.d(TAG, "onResponse: 返回的数据=> " + json);
                    ArrayList<ArticleEntity> entityGson = new Gson().fromJson(json, new TypeToken<>() {
                    });
                    arData.addAll(entityGson);
                    requireActivity().runOnUiThread(handler::flush);
                } catch (Exception e) {
                    Log.e(TAG, "onResponse: 搜索文章接口错误=> " + e);
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
