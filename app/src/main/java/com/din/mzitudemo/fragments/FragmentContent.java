package com.din.mzitudemo.fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.din.mzitudemo.adapters.ContentAdapter;
import com.din.mzitudemo.javabeans.ContentBean;
import com.din.mzitudemo.parsehtml.ParseHTML;

import java.util.List;

public class FragmentContent extends BaseFragment {

    private String url;

    /**
     * 设置布局管理
     *
     * @return
     */
    @Override
    protected RecyclerView.LayoutManager layoutManager() {
        return new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
    }

    /**
     * 开启异步加载
     */
    @Override
    protected void getRefreshData() {
        Intent intent = getActivity().getIntent();
        url = intent.getStringExtra("URL");     // 获取上个页面传过来的数据
        new GetHTMLDataAsyncTask().execute();
    }

    // 异步加载通过jsoup抓取数据
    private class GetHTMLDataAsyncTask extends AsyncTask<String, Void, List<ContentBean>> {

        @Override
        protected List<ContentBean> doInBackground(String... strings) {
            return ParseHTML.parseContentHTMLData(url);
        }

        @Override
        protected void onPostExecute(List<ContentBean> list) {
            super.onPostExecute(list);
            getRecyclerView().setAdapter(new ContentAdapter(getActivity(), list));
            getRecyclerView().setLayoutManager(layoutManager());
            finishRefresh(list);
        }
    }
}