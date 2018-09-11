package com.din.mzitu.mvp.model;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bumptech.glide.Glide;
import com.din.mzitu.R;

public abstract class BaseFragment extends BaseSuperFragment {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefresh;
    private boolean hasFetched = false;


    public void setHasFetched(boolean hasFetched) {
        this.hasFetched = hasFetched;
    }

    @Override
    protected int layoutID() {
        return R.layout.fragment;
    }

    @Override
    protected void initView(View view) {
        recyclerView = view.findViewById(R.id.RecyclerView);
        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        FloatingActionButton actionButton = view.findViewById(R.id.floatbtn);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView.smoothScrollToPosition(0);
            }
        });

        swipeRefresh.setRefreshing(true);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                startAsyncTask();
            }
        });
        if (recyclerView.getLayoutManager() == null) {
            recyclerView.setLayoutManager(getLayoutManager());
        }
        if (recyclerView.getAdapter() == null) {
            recyclerView.setAdapter(getAdapter());    // 设置RecyclerView的适配器
        }
        setScrollListener(recyclerView);
    }

    protected void setScrollListener(RecyclerView recyclerView) {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                /**
                 * newState的三种状态:
                 * RecyclerView.SCROLL_STATE_IDLE（滑动停止状态）
                 * RecyclerView.SCROLL_STATE_DRAGGING（自然滑动状态）
                 * RecyclerView.SCROLL_STATE_SETTLING（手指滑动状态）
                 **/
                if (hasFetched) {
                    Glide.with(getActivity()).resumeRequests();
                    addFirstAndLastVisibleItem();
                }
            }
        });
    }

    /**
     * 设置是否刷新
     *
     * @param refresh
     */
    protected void setRefreshing(boolean refresh) {
        if (refresh) {
            swipeRefresh.setRefreshing(true);
        } else {
            swipeRefresh.setRefreshing(false);
        }
    }

    /**
     * 设置RecycleView.Adapper
     *
     * @return
     */
    protected abstract RecyclerView.Adapter getAdapter();

    /**
     * 设置RecyclerView.LaboutManager
     *
     * @return
     */
    protected abstract RecyclerView.LayoutManager getLayoutManager();

    /**
     * 为Adapter添加可见的Item加载位置
     */
    protected abstract void addFirstAndLastVisibleItem();

    /**
     * 异步加载的后台任务
     *
     * @return
     */
    @Override
    protected abstract Object doInBackgroundTask();

    /**
     * 异步加载结束的数据处理
     *
     * @param o
     */
    @Override
    protected abstract void postExecuteTask(Object o);

}