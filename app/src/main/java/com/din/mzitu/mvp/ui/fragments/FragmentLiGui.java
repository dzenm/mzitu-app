package com.din.mzitu.mvp.ui.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.din.mzitu.R;
import com.din.mzitu.adapters.TabLayoutAdapter;
import com.din.mzitu.mvp.ui.fragments.liguis.FragmentAll;
import com.din.mzitu.mvp.ui.fragments.mzitus.FragmentContent;
import com.din.mzitu.utills.Url;

import java.util.ArrayList;
import java.util.List;

public class FragmentLiGui extends Fragment {

    public static final String LIGUI = "LIGUI";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_series, null);
        Toolbar toolbar = view.findViewById(R.id.toolbar);

        toolbar.setTitle("丽柜图");
        return view;
    }

    private void initView(View view) {
        TabLayout tabLayout = view.findViewById(R.id.tablayout);
        ViewPager viewPager = view.findViewById(R.id.viewpager);

        List<Fragment> fragments = new ArrayList<>();
        fragments.add(FragmentAll.newInstance(Url.LIGUI_NEW, FragmentContent.PAGE_ONE));
        fragments.add(FragmentAll.newInstance(Url.LIGUI_IMISS));
        fragments.add(FragmentAll.newInstance(Url.LIGUI_UXING));
        fragments.add(FragmentAll.newInstance(Url.LIGUI_UGIRLS));
        fragments.add(FragmentAll.newInstance(Url.LIGUI_YOUWU));
        fragments.add(FragmentAll.newInstance(Url.LIGUI_FEILIN));
        fragments.add(FragmentAll.newInstance(Url.LIGUI_XINGLEYUAN));
        fragments.add(FragmentAll.newInstance(Url.LIGUI_LOLI));
        fragments.add(FragmentAll.newInstance(Url.LIGUI_MISTAR));
        fragments.add(FragmentAll.newInstance(Url.LIGUI_TUIGIRLS));
        fragments.add(FragmentAll.newInstance(Url.LIGUI_MFSTAR));
        fragments.add(FragmentAll.newInstance(Url.LIGUI_WINGS));
        fragments.add(FragmentAll.newInstance(Url.LIGUI_QINGDOUKE));
        fragments.add(FragmentAll.newInstance(Url.LIGUI_LEGBABY));
        fragments.add(FragmentAll.newInstance(Url.LIGUI_ROSI));

        List<String> titles = new ArrayList<>();
        titles.add("最新美图");
        titles.add("爱蜜社");
        titles.add("优星馆");
        titles.add("尤果网");
        titles.add("尤物");
        titles.add("私房照");
        titles.add("星乐园");
        titles.add("萝莉社");
        titles.add("魅妍社");
        titles.add("推女郎");
        titles.add("模仿学院");
        titles.add("影私荟");
        titles.add("青豆客");
        titles.add("长腿宝贝");
        titles.add("ROSI");

        TabLayoutAdapter adapter = new TabLayoutAdapter(getChildFragmentManager(), fragments, titles);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }
}