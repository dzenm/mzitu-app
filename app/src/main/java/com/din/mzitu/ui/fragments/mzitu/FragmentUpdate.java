package com.din.mzitu.ui.fragments.mzitu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;

import com.din.mzitu.adapters.UpdateAdapter;
import com.din.mzitu.adapters.ViewHolder;
import com.din.mzitu.api.Mzitu;
import com.din.mzitu.base.BaseAdapter;
import com.din.mzitu.base.BaseFragment;
import com.din.mzitu.bean.UpdateBean;
import com.din.mzitu.ui.activities.ContentActivity;
import com.din.mzitu.ui.fragments.main.FragmentMzitu;

import java.util.List;

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
    protected List<UpdateBean> doInBackgroundTask(int page) {
        String url = getArguments().getString(DAY_UPDATE);
        return Mzitu.getInstance().parseMzituUpdateData(page, url);
    }

    @Override
    protected void nextPageData(int position) {
        adapter.setLoadingStatus(UpdateAdapter.LOADING_STATE_RUNNING);
        adapter.setNotifyStart(position);
        startAsyncTask(++page);
    }

    @Override
    public UpdateAdapter getAdapter() {
        return new UpdateAdapter(getActivity());
    }

    @Override
    public LinearLayoutManager getLayoutManager() {
        return new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
    }

    @Override
    protected void postExecuteTask(Object p0) {
        // 返回为空时解析失败或没有更多数据
        if (p0 != null) {
            if (!isFetchPrepared) {
                isFetchPrepared = true;
            }
            listBeans.addAll((List<UpdateBean>) p0);
            adapter.addBeanData(listBeans);
            adapter.setLoadingStatus(UpdateAdapter.LOADING_STATE_MORE);
        } else {
            isFetchDataAll = true;
        }
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