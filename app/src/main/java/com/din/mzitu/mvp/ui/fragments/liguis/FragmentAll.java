package com.din.mzitu.mvp.ui.fragments.liguis;

import android.os.Bundle;
import android.support.v7.widget.StaggeredGridLayoutManager;

import com.din.mzitu.adapters.SeriesAdapter;
import com.din.mzitu.beans.SeriesBean;
import com.din.mzitu.mvp.model.BaseFragment;
import com.din.mzitu.mvp.presenter.ParseHTMLData;
import com.din.mzitu.mvp.ui.fragments.FragmentLiGui;

import java.util.List;

public class FragmentAll extends BaseFragment {

    public static final String SERIES = "series";
    public static final String POSITION = "position";
    private StaggeredGridLayoutManager layoutManager;
    private SeriesAdapter adapter;

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
    protected ParseHTMLData doInBackgroundTask() {
        String url = getArguments().getString(SERIES);
        return new ParseHTMLData().parseLiGuiMainData(url);
    }

    @Override
    protected void addFirstAndLastVisibleItem() {
        int[] start = getLayoutManager().findFirstVisibleItemPositions(null);
        int[] end = getLayoutManager().findLastVisibleItemPositions(null);
        adapter.addInsertData(start[0], end[1]);
    }

    @Override
    public SeriesAdapter getAdapter() {
        if (adapter == null) {
            adapter = new SeriesAdapter();
        }
        adapter.setWebSite(FragmentLiGui.LIGUI);
        return adapter;
    }

    @Override
    public StaggeredGridLayoutManager getLayoutManager() {
        if (layoutManager == null) {
            layoutManager = new StaggeredGridLayoutManager(2, 1);
        }
        return layoutManager;
    }

    @Override
    protected void postExecuteTask(Object o) {
        ParseHTMLData parseHTMLData = (ParseHTMLData) o;
        List<SeriesBean> list = parseHTMLData.getSeriesBeans();
        adapter.setListBean(list);
        adapter.addInsertData(0, 6);
        setHasFetched(true);
        setRefreshing(false);          // 获取数据之后，刷新停止
    }
}