package com.din.mzitu.ui.fragments.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.din.mzitu.R;
import com.din.mzitu.adapter.TabLayoutAdapter;
import com.din.mzitu.ui.fragments.mzitu.FragmentMain;
import com.din.mzitu.ui.fragments.mzitu.FragmentPostAll;
import com.din.mzitu.ui.fragments.mzitu.FragmentPostDate;
import com.din.mzitu.utill.ScreenHelper;

import java.util.ArrayList;
import java.util.List;

public class FragmentMzitu extends Fragment {

    public static final String MZITU = "MZITU";

    public static final String SEXY = "http://www.mzitu.com/xinggan/";
    public static final String JAPAN = "http://www.mzitu.com/japan/";
    public static final String TAIWAN = "http://www.mzitu.com/taiwan/";
    public static final String PURE = "http://www.mzitu.com/mm/";
    public static final String DAYUPDATE = "http://www.mzitu.com/all/";
    private View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_main, null);
        Toolbar toolbar = rootView.findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.drawer_nav_mzitu);
        ScreenHelper.addDrawerLayoutToggle(getActivity(), toolbar);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ViewPager viewPager = view.findViewById(R.id.viewpager);
        TabLayout tabLayout = view.findViewById(R.id.tablayout);

        List<Fragment> list = new ArrayList<>();

        list.add(FragmentMain.newInstance());
        list.add(FragmentPostAll.newInstance(SEXY));
        list.add(FragmentPostAll.newInstance(JAPAN));
        list.add(FragmentPostAll.newInstance(TAIWAN));
        list.add(FragmentPostAll.newInstance(PURE));
        list.add(FragmentPostSelf.newInstance());
        list.add(FragmentPostDate.newInstance(DAYUPDATE));

        List<String> titles = new ArrayList<>();
        titles.add("首页");
        titles.add("性感妹子");
        titles.add("日本妹子");
        titles.add("台湾妹子");
        titles.add("清纯妹子");
        titles.add("妹子自拍");
        titles.add("每日更新");

        TabLayoutAdapter adapter = new TabLayoutAdapter(getChildFragmentManager(), list, titles);
        viewPager.setAdapter(adapter);
        // 解决瀑布流空白卡顿
        viewPager.setOffscreenPageLimit(list.size());
        tabLayout.setupWithViewPager(viewPager);
    }
}