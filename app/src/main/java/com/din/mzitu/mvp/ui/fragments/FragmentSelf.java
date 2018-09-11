package com.din.mzitu.mvp.ui.fragments;

import android.support.v7.widget.LinearLayoutManager;

import com.din.mzitu.adapters.ContentAdapter;
import com.din.mzitu.beans.ContentBean;
import com.din.mzitu.mvp.model.BaseFragment;
import com.din.mzitu.mvp.presenter.ParseHTMLData;
import com.din.mzitu.mvp.ui.fragments.mzitus.FragmentContent;
import com.din.mzitu.utills.Url;

import java.util.List;

public class FragmentSelf extends BaseFragment {
    
    private ContentAdapter adapter;
    private LinearLayoutManager layoutManager;

    public static FragmentSelf newInstance() {
        FragmentSelf fragmentSelf = new FragmentSelf();
        return fragmentSelf;
    }

    @Override
    protected int getPageFragment() {
        return FragmentContent.PAGE_ONE;
    }

    @Override
    protected ParseHTMLData doInBackgroundTask() {
        return new ParseHTMLData().parseMzituSelfData(Url.MZITU_SELF);
    }

    @Override
    protected void addFirstAndLastVisibleItem() {
        int start = layoutManager.findFirstVisibleItemPosition();
        int end = layoutManager.findLastVisibleItemPosition();
        adapter.addInsertData(start, end);
    }

    @Override
    public ContentAdapter getAdapter() {
        if (adapter == null) {
            adapter = new ContentAdapter();
            adapter.setActivity(getActivity());
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
        List<ContentBean> list = parseHTMLData.getContentBeans();
        adapter.setListBean(list);
        adapter.addInsertData(0, 6);
        setHasFetched(true);
        setRefreshing(false);          // 获取数据之后，刷新停止
    }
}
