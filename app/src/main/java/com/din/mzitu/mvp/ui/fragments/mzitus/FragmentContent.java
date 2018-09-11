package com.din.mzitu.mvp.ui.fragments.mzitus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;

import com.din.mzitu.adapters.ContentAdapter;
import com.din.mzitu.beans.ContentBean;
import com.din.mzitu.mvp.model.BaseFragment;
import com.din.mzitu.mvp.presenter.ParseHTMLData;
import com.din.mzitu.mvp.ui.fragments.FragmentLiGui;
import com.din.mzitu.mvp.ui.fragments.FragmentMzitu;

import java.util.List;

public class FragmentContent extends BaseFragment {

    public static final String CONTENT_URL = "content_url";
    public static final String CONTENT_TITLE = "content_title";
    public static final String CONTENT_WEBSITE = "content_website";
    public static final String CONTENT = "self";
    public static final String POSITION = "position";
    public static final int PAGE_DEFAULT = 0;
    public static final int PAGE_ONE = 1;

    private ContentAdapter adapter;
    private LinearLayoutManager layoutManager;

    public static FragmentContent newInstance(String url, int position) {
        Bundle bundle = new Bundle();
        bundle.putString(CONTENT, url);
        bundle.putInt(POSITION, position);
        FragmentContent fragmentSelf = new FragmentContent();
        fragmentSelf.setArguments(bundle);
        return fragmentSelf;
    }

    @Override
    protected int getPageFragment() {
        int position = getArguments().getInt(POSITION);
        return position;
    }

    @Override
    protected ParseHTMLData doInBackgroundTask() {
        String url;
        Intent intent = getActivity().getIntent();
        String website = intent.getStringExtra(CONTENT_WEBSITE);
        if (website.equals(FragmentMzitu.MZITU)) {
            url = intent.getStringExtra(CONTENT_URL);
            return new ParseHTMLData().parseMzituContentData(url);
        } else if (website.equals(FragmentLiGui.LIGUI)) {
            url = intent.getStringExtra(CONTENT_URL);
            return new ParseHTMLData().parseLiGuiContentData(url);
        }
        return null;
    }

    @Override
    protected void addFirstAndLastVisibleItem() {
        int start = layoutManager.findFirstVisibleItemPosition();
        int end = layoutManager.findLastVisibleItemPosition();
        adapter.addInsertData(start, end);
    }

    @Override
    public ContentAdapter getAdapter() {
        String title = getArguments().getString(CONTENT_TITLE);
        if (adapter == null) {
            adapter = new ContentAdapter();
            adapter.setTitle(title);
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