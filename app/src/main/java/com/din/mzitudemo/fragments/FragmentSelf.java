package com.din.mzitudemo.fragments;

import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

import com.din.mzitudemo.adapters.ContentAdapter;
import com.din.mzitudemo.javabeans.ContentBean;
import com.din.mzitudemo.javabeans.Url;
import com.din.mzitudemo.parsehtml.ParseHTML;

import java.util.List;

public class FragmentSelf extends BaseFragment {
    /**
     * 设置布局管理
     *
     * @return
     */
    @Override
    protected RecyclerView.LayoutManager layoutManager() {
        return new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
    }

    @Override
    protected void getRefreshData() {
        new GetHTMLDataAsyncTask().execute();
    }

    // 异步加载通过jsoup抓取数据
    class GetHTMLDataAsyncTask extends AsyncTask<String, Void, List<ContentBean>> {

        @Override
        protected List<ContentBean> doInBackground(String... strings) {
            return ParseHTML.parseSelfHTMLData(Url.SELF);
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