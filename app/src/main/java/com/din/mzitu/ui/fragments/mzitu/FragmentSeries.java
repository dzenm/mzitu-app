package com.din.mzitu.ui.fragments.mzitu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.StaggeredGridLayoutManager;

import com.din.mzitu.adapters.SeriesAdapter;
import com.din.mzitu.adapters.ViewHolder;
import com.din.mzitu.api.Mzitu;
import com.din.mzitu.base.BaseAdapter;
import com.din.mzitu.base.BaseFragment;
import com.din.mzitu.bean.SeriesBean;
import com.din.mzitu.ui.activities.ContentActivity;
import com.din.mzitu.ui.fragments.main.FragmentMzitu;

import java.util.List;

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
    protected List<SeriesBean> doInBackgroundTask(int page) {
        // 获取页面加载的url
        String url = getArguments().getString(SERIES);
        return Mzitu.getInstance().parseMzituMainData(page, url);
    }

    @Override
    protected void nextPageData(int position) {
        adapter.setNotifyStart(position);
        adapter.setLoadingStatus(SeriesAdapter.LOADING_STATE_RUNNING);
        startAsyncTask(++page);
    }

    @Override
    public SeriesAdapter getAdapter() {
        return new SeriesAdapter(getActivity());
    }

    @Override
    public StaggeredGridLayoutManager getLayoutManager() {
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        return layoutManager;
    }

    @Override
    protected void postExecuteTask(Object p0) {
        if (p0 != null) {
            if (!isFetchPrepared) {
                isFetchPrepared = true;
            }
            listBeans.addAll((List<SeriesBean>) p0);
            adapter.addBeanData(listBeans);
        } else {
            // 返回为空解析失败或没有更多数据时不再加载数据
            isFetchDataAll = true;
        }
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