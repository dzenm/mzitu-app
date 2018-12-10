package com.din.mzitu.ui.fragments.mzitu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;

import com.din.mzitu.adapter.SeriesAdapter;
import com.din.mzitu.adapter.ViewHolder;
import com.din.mzitu.api.Mzitu;
import com.din.mzitu.base.BaseAdapter;
import com.din.mzitu.base.BaseFragment;
import com.din.mzitu.bean.SeriesBean;
import com.din.mzitu.ui.activities.ContentActivity;
import com.din.mzitu.ui.fragments.main.FragmentMzitu;

import java.util.List;

import io.reactivex.ObservableEmitter;

public class FragmentSeries extends BaseFragment implements BaseAdapter.OnItemClickListener {

    public static final String SERIES = "series";
    public static final String POSITION = "position";

    /**
     * 创建一个实例
     *
     * @param url 爬取数据的url
     * @return
     */
    public static FragmentSeries newInstance(String url) {
        Bundle bundle = new Bundle();
        bundle.putString(SERIES, url);
        bundle.putString(FragmentContent.CONTENT_WEBSITE, FragmentMzitu.MZITU);
        FragmentSeries fragmentSeries = new FragmentSeries();
        fragmentSeries.setArguments(bundle);
        return fragmentSeries;
    }

    /**
     * @param url
     * @param position
     * @return
     */
    public static FragmentSeries newInstance(String url, int position) {
        Bundle bundle = new Bundle();
        bundle.putString(SERIES, url);
        bundle.putInt(POSITION, position);
        FragmentSeries fragmentSeries = new FragmentSeries();
        fragmentSeries.setArguments(bundle);
        return fragmentSeries;
    }

    @Override
    protected int getPageFragment() {
        return getArguments().getInt(POSITION);
    }

    @Override
    protected void observableTask(ObservableEmitter emitter) {
        // 获取页面加载的url
        String url = getArguments().getString(SERIES);
        emitter.onNext(Mzitu.getInstance().parseMzituMainData(page++, url));
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
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        // 解决屏闪
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        return layoutManager;
    }

    @Override
    protected void observerData(Object p0) {
        listBeans.addAll((List<SeriesBean>) p0);
        adapter.addBeanData(listBeans);
        swipeRefresh.setRefreshing(false);          // 获取数据之后，刷新停止
    }

    @Override
    public void onItemClick(ViewHolder holder, int position) {
        SeriesBean bean = (SeriesBean) listBeans.get(position - 1);         // 获取数据的item
        Intent intent = new Intent(getActivity(), ContentActivity.class);
        intent.putExtra(FragmentContent.CONTENT_URL, bean.getUrl());      // 将URL和TITLE传递到下一个页面
        intent.putExtra(FragmentContent.CONTENT_TITLE, bean.getTitle());
        intent.putExtra(FragmentContent.CONTENT_WEBSITE, FragmentMzitu.MZITU);
        startActivity(intent);
    }
}