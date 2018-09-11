package com.din.mzitu.mvp.ui.fragments.mzitus;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.din.mzitu.R;
import com.din.mzitu.adapters.TabLayoutAdapter;
import com.din.mzitu.utills.Url;

import java.util.ArrayList;
import java.util.List;

public class FragmentMain extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_mzitu_main, null);

        ViewPager viewPager = view.findViewById(R.id.viewpager);
        TabLayout tabLayout = view.findViewById(R.id.tablayout);

        List<Fragment> list = new ArrayList<>();
        list.add(FragmentSeries.newInstance(Url.MZITU_BEST, FragmentContent.PAGE_ONE));
        list.add(FragmentSeries.newInstance(Url.MZITU_HOT));
        List<String> titles = new ArrayList<>();
        titles.add("推荐");
        titles.add("热门");

        TabLayoutAdapter adapter = new TabLayoutAdapter(getChildFragmentManager(), list, titles);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        return view;
    }
}