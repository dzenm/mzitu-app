package com.din.mzitudemo.fragments;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.din.mzitudemo.R;
import com.din.mzitudemo.adapters.UpdateAdapter;
import com.din.mzitudemo.javabeans.UpdateBean;
import com.din.mzitudemo.javabeans.Url;
import com.din.mzitudemo.parsehtml.ParseHTML;

import java.util.List;

public class FragmentUpdate extends Fragment {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefresh;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment, null);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.RecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        FloatingActionButton floatingButton = view.findViewById(R.id.floatbtn);
        floatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.smoothScrollToPosition(0);
            }
        });
        swipeRefresh.setRefreshing(true);       // 初始化时开始刷新
        new MyAsync().execute();        // 初始化时异步加载数据
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new MyAsync().execute();        // 下拉刷新时异步加载数据
            }
        });
    }

    // 异步加载通过jsoup抓取数据
    class MyAsync extends AsyncTask<String, Void, List<UpdateBean>> {
        @Override
        protected List<UpdateBean> doInBackground(String... strings) {
            return ParseHTML.parseDayUpdateHTMLData(Url.DAYUPDATE);
        }

        @Override
        protected void onPostExecute(List<UpdateBean> list) {
            super.onPostExecute(list);
            setRecyclerViewData(list);
        }
    }

    private void setRecyclerViewData(List<UpdateBean> list) {
        recyclerView.setAdapter(new UpdateAdapter(getActivity(), list));    // 设置RecyclerView的适配器
        if (swipeRefresh.isRefreshing() && list != null) {   // 获取数据之后，刷新停止
            swipeRefresh.setRefreshing(false);
        } 
    }
}