package com.din.mzitu.ui.fragments.mzitu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;

import com.din.mzitu.adapter.UpdateAdapter;
import com.din.mzitu.adapter.ViewHolder;
import com.din.mzitu.api.Mzitu;
import com.din.mzitu.base.BaseAdapter;
import com.din.mzitu.base.BaseFragment;
import com.din.mzitu.bean.UpdateBean;
import com.din.mzitu.ui.activities.ContentActivity;
import com.din.mzitu.ui.fragments.main.FragmentMzitu;

import java.util.List;

import io.reactivex.ObservableEmitter;

public class FragmentUpdate extends BaseFragment implements BaseAdapter.OnItemClickListener {

    public static final String DAY_UPDATE = "day_update";

    public static FragmentUpdate newInstance(String url) {
        Bundle bundle = new Bundle();
        bundle.putString(DAY_UPDATE, url);
        FragmentUpdate fragmentUpdate = new FragmentUpdate();
        fragmentUpdate.setArguments(bundle);
        return fragmentUpdate;
    }

    @Override
    protected void observableTask(ObservableEmitter emitter) {
        String url = getArguments().getString(DAY_UPDATE);
        emitter.onNext(Mzitu.getInstance().parseMzituUpdateData(++page, url));
    }

    @Override
    protected void pagingData(int position) {
        adapter.setNotifyStart(position);
        startAsyncTask();
    }

    @Override
    public UpdateAdapter getAdapter() {
        return new UpdateAdapter(this);
    }

    @Override
    public LinearLayoutManager getLayoutManager() {
        return new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
    }

    @Override
    protected void observerData(Object p0) {
        listBeans.addAll((List<UpdateBean>) p0);
        adapter.addBeanData(listBeans);
        adapter.setLoadingStatus(UpdateAdapter.LOADING_STATE_MORE);
        swipeRefresh.setRefreshing(false);          // 获取数据之后，刷新停止
    }

    @Override
    public void onItemClick(ViewHolder holder, int position) {
        UpdateBean bean = (UpdateBean) listBeans.get(position - 1);
        Intent intent = new Intent(getActivity(), ContentActivity.class);
        intent.putExtra(FragmentContent.CONTENT_URL, bean.getUrl());      // 将URL和TITLE传递到下一个页面
        intent.putExtra(FragmentContent.CONTENT_TITLE, bean.getTitle());
        intent.putExtra(FragmentContent.CONTENT_WEBSITE, FragmentMzitu.MZITU);
        startActivity(intent);
    }
}