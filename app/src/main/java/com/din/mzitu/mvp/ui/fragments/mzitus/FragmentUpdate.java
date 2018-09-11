package com.din.mzitu.mvp.ui.fragments.mzitus;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;

import com.din.mzitu.adapters.UpdateAdapter;
import com.din.mzitu.beans.UpdateBean;
import com.din.mzitu.mvp.model.BaseFragment;
import com.din.mzitu.mvp.presenter.ParseHTMLData;

import java.util.List;

public class FragmentUpdate extends BaseFragment {

    public static final String DAY_UPDATE = "day_update";

    private UpdateAdapter adapter;
    private LinearLayoutManager layoutManager;

    public static FragmentUpdate newInstance(String url) {
        Bundle bundle = new Bundle();
        bundle.putString(DAY_UPDATE, url);
        FragmentUpdate fragmentUpdate = new FragmentUpdate();
        fragmentUpdate.setArguments(bundle);
        return fragmentUpdate;
    }

    @Override
    protected ParseHTMLData doInBackgroundTask() {
        String url = getArguments().getString(DAY_UPDATE);
        return new ParseHTMLData().parseMzituUpdateData(url);
    }

    @Override
    protected void addFirstAndLastVisibleItem() {
        int start = getLayoutManager().findFirstVisibleItemPosition();
        int end = getLayoutManager().findLastVisibleItemPosition();
        adapter.addInsertData(start, end);
    }

    @Override
    public UpdateAdapter getAdapter() {
        if (adapter == null) {
            adapter = new UpdateAdapter();
        }
        return adapter;
    }

    @Override
    public LinearLayoutManager getLayoutManager() {
        if (layoutManager == null) {
            layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        }
        return layoutManager;
    }

    @Override
    protected void postExecuteTask(Object o) {
        ParseHTMLData parseHTMLData = (ParseHTMLData) o;
        List<UpdateBean> list = parseHTMLData.getUpdateBeans();
        adapter.setListBean(list);
        adapter.addInsertData(0, 6);
        setHasFetched(true);
        setRefreshing(false);          // 获取数据之后，刷新停止
    }
}