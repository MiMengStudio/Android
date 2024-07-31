package com.mimeng;

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

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.mimeng.Adapter.FragmentPageAdapter;
import com.mimeng.BaseClass.BaseActivity;
import com.mimeng.Fragment.SearchArticleFragment;
import com.mimeng.databinding.ActivitySearchBinding;
import com.mimeng.ui.community.CommunityFragment;
import com.mimeng.ui.tools.ToolsFragment;
import com.mimeng.ui.user.UserFragment;
import com.mimeng.utils.DataBaseHelper;
import com.mimeng.utils.DataBaseUtils;

import java.util.ArrayList;
import java.util.List;


public class SearchActivity extends BaseActivity {

    private ActivitySearchBinding binding;
    private DataBaseUtils dataBaseUtils;
    private final SearchArticleFragment searchArticleFragment = new SearchArticleFragment();

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
                }
            }
        });

        binding.search.setOnClickListener(v -> {
            String search = binding.searchEdit.getText().toString();
            if (!search.isEmpty()) {
                binding.tableParent.setVisibility(View.VISIBLE);
                binding.scrollView2.setVisibility(View.GONE);
                binding.tableParent.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        searchArticleFragment.search(search);
                    }
                },500);

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
        viewPager.setAdapter(pageAdapter);
        binding.tabLayout.setupWithViewPager(viewPager);

        // 清空历史记录
        binding.clearHistory.setOnClickListener(v -> dataBaseUtils.clearTableData(DataBaseHelper.TABLE_NAME));
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
