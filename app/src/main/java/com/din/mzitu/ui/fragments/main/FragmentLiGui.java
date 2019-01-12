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
import com.din.mzitu.api.LiGui;
import com.din.mzitu.ui.fragments.ligui.FragmentPost;
import com.din.mzitu.utill.ScreenHelper;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class FragmentLiGui extends Fragment {

    public static final String LIGUI = "LIGUI";
    public static final String INDEX = "http://www.ligui.org/index.html";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_main, null);
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.drawer_nav_ligui);
        ScreenHelper.addDrawerLayoutToggle(getActivity(), toolbar);
        initView(view);
        return view;
    }

    private void initView(View view) {
        final TabLayout tabLayout = view.findViewById(R.id.tablayout);
        final ViewPager viewPager = view.findViewById(R.id.viewpager);

        Observable.create(new ObservableOnSubscribe<List<List>>() {
            @Override
            public void subscribe(ObservableEmitter<List<List>> emitter) {
//                LiGui.getInstance().parseLiGuiBanner(INDEX);
                emitter.onNext(LiGui.getInstance().getTopNavs());
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<List>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<List> lists) {
                        // 获取每一个导航栏的url
                        if (lists.size() != 2) {
                            return;
                        }
                        List<Fragment> fragments = new ArrayList<>();
                        List<String> urls = lists.get(1);
                        for (int i = 0; i < urls.size(); i++) {
                            if (i == 0) {
                                fragments.add(FragmentPost.newInstance(urls.get(i), FragmentPost.PAGE_ONE));
                            } else {
                                fragments.add(FragmentPost.newInstance(urls.get(i)));
                            }
                        }
                        // 获取每一个导航栏的标题
                        List<String> titles = lists.get(0);

                        TabLayoutAdapter adapter = new TabLayoutAdapter(getChildFragmentManager(), fragments, titles);
                        viewPager.setAdapter(adapter);
                        // 解决Fragment切换时被销毁
                        viewPager.setOffscreenPageLimit(fragments.size());
                        tabLayout.setupWithViewPager(viewPager);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}