package com.din.mzitu.ui.fragments.ligui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.StaggeredGridLayoutManager;

import com.din.mzitu.adapter.SeriesAdapter;
import com.din.mzitu.adapter.ViewHolder;
import com.din.mzitu.api.LiGui;
import com.din.mzitu.base.BaseAdapter;
import com.din.mzitu.base.BaseFragment;
import com.din.mzitu.bean.SeriesBean;
import com.din.mzitu.ui.activities.ContentActivity;
import com.din.mzitu.ui.fragments.main.FragmentMzitu;
import com.din.mzitu.ui.fragments.mzitu.FragmentContent;

import java.util.List;

import io.reactivex.ObservableEmitter;

public class FragmentAll extends BaseFragment implements BaseAdapter.OnItemClickListener {

    public static final String SERIES = "series";
    public static final String POSITION = "position";
    private StaggeredGridLayoutManager layoutManager;
    private SeriesAdapter adapter;
    private List<SeriesBean> seriesBeans;

    public static FragmentAll newInstance(String url) {
        Bundle bundle = new Bundle();
        bundle.putString(SERIES, url);
        FragmentAll fragmentAll = new FragmentAll();
        fragmentAll.setArguments(bundle);
        return fragmentAll;
    }

    public static FragmentAll newInstance(String url, int position) {
        Bundle bundle = new Bundle();
        bundle.putString(SERIES, url);
        bundle.putInt(POSITION, position);
        FragmentAll fragmentAll = new FragmentAll();
        fragmentAll.setArguments(bundle);
        return fragmentAll;
    }

    @Override
    protected int getPageFragment() {
        int position = getArguments().getInt(POSITION);
        return position;
    }

    @Override
    protected void observableTask(ObservableEmitter emitter) {
        String url = getArguments().getString(SERIES);
        emitter.onNext(LiGui.getInstance().parseLiGuiMainData(++page, url));
    }

    @Override
    protected void pagingData(int position) {
        adapter.setNotifyStart(position);
        startAsyncTask();
    }

    @Override
    public SeriesAdapter getAdapter() {
        return new SeriesAdapter(this);
    }

    @Override
    public StaggeredGridLayoutManager getLayoutManager() {
        return new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
    }

    @Override
    protected void observerData(Object p0) {
        if (page == 1) {
            seriesBeans = (List<SeriesBean>) p0;
        } else {
            seriesBeans.addAll((List<SeriesBean>) p0);
        }
        adapter.addBeanData(seriesBeans);
        swipeRefresh.setRefreshing(false);          // 获取数据之后，刷新停止
    }

    @Override
    public void onItemClick(ViewHolder holder, int position) {
        SeriesBean bean = seriesBeans.get(position - 1);         // 获取数据的item
        Intent intent = new Intent(getActivity(), ContentActivity.class);
        intent.putExtra(FragmentContent.CONTENT_URL, bean.getUrl());      // 将URL和TITLE传递到下一个页面
        intent.putExtra(FragmentContent.CONTENT_TITLE, bean.getTitle());
        intent.putExtra(FragmentContent.CONTENT_WEBSITE, FragmentMzitu.MZITU);
        startActivity(intent);
    }
}