package com.din.mzitudemo.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.din.mzitudemo.R;
import com.din.mzitudemo.adapters.TabLayoutAdapter;

import java.util.ArrayList;
import java.util.List;

public class FragmentMzitu extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_mzitu, null);

        ViewPager viewPager = view.findViewById(R.id.viewpager);
        TabLayout tabLayout = view.findViewById(R.id.tablayout);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("妹子图");

        List<Fragment> list = new ArrayList<>();
        list.add(new FragmentMain());
        list.add(new FragmentSexy());
        list.add(new FragmentJapan());
        list.add(new FragmentTaiWan());
        list.add(new FragmentPure());
        list.add(new FragmentSelf());
        list.add(new FragmentUpdate());

        List<String> titles = new ArrayList<>();
        titles.add("主页");
        titles.add("性感妹子");
        titles.add("日本妹子");
        titles.add("台湾妹子");
        titles.add("清纯妹子");
        titles.add("妹子自拍");
        titles.add("每日更新");

        TabLayoutAdapter adapter = new TabLayoutAdapter(getChildFragmentManager(), list, titles);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        return view;
    }
}