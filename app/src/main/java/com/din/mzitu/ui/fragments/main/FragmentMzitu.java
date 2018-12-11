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
import com.din.mzitu.utill.Url;

import java.util.ArrayList;
import java.util.List;

public class FragmentMzitu extends Fragment {

    public static final String MZITU = "MZITU";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_main, null);
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("妹子图");
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ViewPager viewPager = view.findViewById(R.id.viewpager);
        TabLayout tabLayout = view.findViewById(R.id.tablayout);

        List<Fragment> list = new ArrayList<>();
        list.add(new FragmentMain());
        list.add(FragmentPostAll.newInstance(Url.MZITU_SEXY));
        list.add(FragmentPostAll.newInstance(Url.MZITU_JAPAN));
        list.add(FragmentPostAll.newInstance(Url.MZITU_TAIWAN));
        list.add(FragmentPostAll.newInstance(Url.MZITU_PURE));
        list.add(FragmentPostDate.newInstance(Url.MZITU_DAYUPDATE));

        List<String> titles = new ArrayList<>();
        titles.add("主页");
        titles.add("性感妹子");
        titles.add("日本妹子");
        titles.add("台湾妹子");
        titles.add("清纯妹子");
        titles.add("每日更新");

        TabLayoutAdapter adapter = new TabLayoutAdapter(getChildFragmentManager(), list, titles);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(list.size());
        tabLayout.setupWithViewPager(viewPager);
    }
}