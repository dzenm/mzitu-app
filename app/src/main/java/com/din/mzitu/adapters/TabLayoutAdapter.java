package com.din.mzitu.adapters;


import android.app.Fragment;
import android.app.FragmentManager;
import android.support.annotation.Nullable;

import java.util.List;

public class TabLayoutAdapter extends FragmentPagerAdapter {

    private List<Fragment> list;
    private List<String> titles;

    public TabLayoutAdapter(FragmentManager fm, List<Fragment> list, List<String> titles) {
        super(fm);
        this.list = list;
        this.titles = titles;
    }

    @Override
    public Fragment getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }
}