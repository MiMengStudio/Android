package com.mimeng.Adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

public class FragmentPageAdapter extends FragmentPagerAdapter {
    private final String[] titles;
    private final List<Fragment> fragments;

    public FragmentPageAdapter(@NonNull FragmentManager fragmentManager, List<Fragment> fragments, String[] titles) {
        super(fragmentManager);
        this.titles = titles;
        this.fragments = fragments;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments != null ? fragments.size() : 0;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}
