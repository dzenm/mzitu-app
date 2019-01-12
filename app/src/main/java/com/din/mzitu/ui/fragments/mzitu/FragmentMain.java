package com.din.mzitu.ui.fragments.mzitu;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.din.mzitu.R;
import com.din.mzitu.adapter.TabLayoutAdapter;
import com.din.mzitu.api.LiGui;

import java.util.ArrayList;
import java.util.List;

public class FragmentMain extends Fragment {

    public static final String NEW = "https://www.mzitu.com/";
    public static final String BEST = "http://www.mzitu.com/best/";
    public static final String HOT = "http://www.mzitu.com/hot/";

    /**
     * 创建单例
     *
     * @return
     */
    public static FragmentMain newInstance() {
        FragmentMain fragmentMain = new FragmentMain();
        return fragmentMain;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_mzitu_main, null);

        ViewPager viewPager = view.findViewById(R.id.viewpager);
        TabLayout tabLayout = view.findViewById(R.id.tablayout);

        List<Fragment> list = new ArrayList<>();
        list.add(FragmentPostAll.newInstance(NEW, FragmentPostAll.PAGE_ONE));
        list.add(FragmentPostAll.newInstance(BEST));
        list.add(FragmentPostAll.newInstance(HOT));
        List<String> titles = new ArrayList<>();
        titles.add("最新");
        titles.add("推荐");
        titles.add("热门");

        TabLayoutAdapter adapter = new TabLayoutAdapter(getChildFragmentManager(), list, titles);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(list.size());
        tabLayout.setupWithViewPager(viewPager);
        return view;
    }
}