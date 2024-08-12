package com.mimeng.activity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.mimeng.Adapter.FlexRecyclerAdapter;
import com.mimeng.Adapter.FragmentPageAdapter;
import com.mimeng.base.BaseActivity;
import com.mimeng.fragments.SearchArticleFragment;
import com.mimeng.databinding.ActivitySearchBinding;
import com.mimeng.ui.community.CommunityFragment;
import com.mimeng.ui.tools.ToolsFragment;
import com.mimeng.ui.user.UserFragment;
import com.mimeng.utils.DataBaseHelper;
import com.mimeng.utils.DataBaseUtils;

import java.util.ArrayList;
import java.util.List;


public class SearchActivity extends BaseActivity {

    private final SearchArticleFragment searchArticleFragment = new SearchArticleFragment();
    private ActivitySearchBinding binding;
    private DataBaseUtils dataBaseUtils;
    private FlexRecyclerAdapter adapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        blackParentBar();
        setFullScreen(false);

        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 创建数据库对象
        dataBaseUtils = new DataBaseUtils(this);
        // 打开数据库
        dataBaseUtils.open();

        binding.back.setOnClickListener(view -> finish());

        binding.searchEdit.requestFocus();
        binding.searchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    binding.tableParent.setVisibility(View.GONE);
                    binding.scrollView2.setVisibility(View.VISIBLE);
                    SearchHistory();
                }
            }
        });

        // 设置流式布局列表
        RecyclerView recyclerView = binding.searchHistory;
        FlexboxLayoutManager manager = new FlexboxLayoutManager(SearchActivity.this);
        manager.setFlexDirection(FlexDirection.ROW);
        adapter = new FlexRecyclerAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(manager);
        // 查询历史记录
        SearchHistory();
        // 根据点击搜索历史的关键词进行搜索
        adapter.setOnChangeListener(keyWord -> {
            searchBegin(keyWord);
            binding.searchEdit.setText(keyWord);
        });

        // 搜索按钮点击事件
        binding.search.setOnClickListener(v -> {
            String search = binding.searchEdit.getText().toString();
            if (!search.isEmpty()) {
                searchBegin(search);
                try {
                    ContentValues values = new ContentValues();
                    values.put("content", search);
                    dataBaseUtils.insertData(values, DataBaseHelper.TABLE_NAME);
                } catch (Exception e) {
                    Log.e("SearchActivity: ", "数据写入失败：" + e);
                }
            }
        });

        String[] titles = new String[]{"综合", "文章", "图鉴", "用户"};
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new ToolsFragment());
        fragments.add(searchArticleFragment);
        fragments.add(new CommunityFragment());
        fragments.add(new UserFragment());
        FragmentPageAdapter pageAdapter = new FragmentPageAdapter(getSupportFragmentManager(), fragments, titles);
        ViewPager viewPager = binding.viewPager;
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(pageAdapter);
        binding.tabLayout.setupWithViewPager(viewPager);

        // 清空历史记录
        binding.clearHistory.setOnClickListener(v -> {
            try {
                dataBaseUtils.clearTableData(DataBaseHelper.TABLE_NAME);
                SearchHistory();
                Toast.makeText(this, "已清空历史记录", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "清空历史记录失败", Toast.LENGTH_SHORT).show();
            }

        });
    }

    /**
     * 查询历史记录
     */
    @SuppressLint("NotifyDataSetChanged")
    public void SearchHistory(){
        ArrayList<String> contents = dataBaseUtils.select("SELECT * FROM search_history WHERE 1", "content");
        if (contents.isEmpty()){
            binding.asNotHistory.setVisibility(View.VISIBLE);
        }else {
            binding.asNotHistory.setVisibility(View.GONE);
        }
        adapter.setData(contents);
        SearchActivity.this.runOnUiThread(() -> adapter.notifyDataSetChanged());
    }

    /**
     * 执行搜索动作
     * @param search 搜索内容
     */
    public void searchBegin(String search){
        binding.tableParent.setVisibility(View.VISIBLE);
        binding.scrollView2.setVisibility(View.GONE);
        binding.tableParent.postDelayed(new Runnable() {
            @Override
            public void run() {
                searchArticleFragment.search(search);
            }
        }, 100);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    assert v != null;
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 隐藏软键盘(EditText)
     *
     * @param v     视图
     * @param event 触摸\点击事件
     * @return 返回
     */
    public boolean isShouldHideInput(View v, MotionEvent event) {
        if (v instanceof EditText) {
            Rect rect = new Rect();
            v.getGlobalVisibleRect(rect);
            v.clearFocus();
            // 如果点击事件在EditText的范围内，则不隐藏软键盘
            return !rect.contains((int) event.getRawX(), (int) event.getRawY());
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
        dataBaseUtils.closeDatabase();
    }
}
