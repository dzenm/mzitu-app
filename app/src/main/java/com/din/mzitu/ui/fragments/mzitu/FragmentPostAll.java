package com.din.mzitu.ui.fragments.mzitu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import com.din.mzitu.adapter.PostAllAdapter;
import com.din.mzitu.adapter.ViewHolder;
import com.din.mzitu.api.Mzitu;
import com.din.mzitu.base.BaseAdapter;
import com.din.mzitu.base.BaseFragment;
import com.din.mzitu.bean.PostAllBean;
import com.din.mzitu.ui.activities.PostSingleActivity;
import com.din.mzitu.ui.fragments.main.FragmentMzitu;

import java.util.List;

import io.reactivex.ObservableEmitter;

public class FragmentPostAll extends BaseFragment implements BaseAdapter.OnItemClickListener, View.OnClickListener {

    public static final String POST_ALL = "post_all";
    public static final String POSITION = "position";

    // 创建一个实例   url:爬取数据的url
    public static FragmentPostAll newInstance(String url) {
        Bundle bundle = new Bundle();
        bundle.putString(POST_ALL, url);
        bundle.putString(FragmentPostSingle.POST_WEBSITE, FragmentMzitu.MZITU);
        FragmentPostAll fragmentPostAll = new FragmentPostAll();
        fragmentPostAll.setArguments(bundle);
        return fragmentPostAll;
    }

    public static FragmentPostAll newInstance(String url, int position) {
        Bundle bundle = new Bundle();
        bundle.putString(POST_ALL, url);
        bundle.putInt(POSITION, position);
        FragmentPostAll fragmentPostAll = new FragmentPostAll();
        fragmentPostAll.setArguments(bundle);
        return fragmentPostAll;
    }

    @Override
    protected int getPageFragment() {
        return getArguments().getInt(POSITION);
    }

    @Override
    protected void observableTask(ObservableEmitter emitter) {
        // 获取页面加载的url
        String url = getArguments().getString(POST_ALL);
        emitter.onNext(Mzitu.getInstance().parseMzituMainData(page++, url));
        emitter.onComplete();
    }

    @Override
    public PostAllAdapter getAdapter() {
        return new PostAllAdapter(this);
    }

    @Override
    public StaggeredGridLayoutManager getLayoutManager() {
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        // 解决屏闪，乱跳
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        return layoutManager;
    }

    @Override
    protected void observerData(Object p0) {
        listBeans.addAll((List<PostAllBean>) p0);
        adapter.addBeanData(listBeans);
        swipeRefresh.setRefreshing(false);          // 获取数据之后，刷新停止l
    }

    @Override
    public void onItemClick(ViewHolder holder, int position) {
        PostAllBean bean = (PostAllBean) listBeans.get(position - 1);         // 获取数据的item
//        Intent intent = new Intent(getActivity(), PostSingleActivity.class);
        Intent intent = null;
        intent.putExtra(FragmentPostSingle.POST_URL, bean.getUrl());      // 将URL和TITLE传递到下一个页面
        intent.putExtra(FragmentPostSingle.POST_TITLE, bean.getTitle());
        intent.putExtra(FragmentPostSingle.POST_WEBSITE, FragmentMzitu.MZITU);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        recyclerView.smoothScrollToPosition(0);
    }
}