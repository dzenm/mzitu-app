package com.din.mzitu.base;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.din.mzitu.R;
import com.din.mzitu.adapter.ViewHolder;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public abstract class BaseFragment<T> extends Fragment implements SwipeRefreshLayout.OnRefreshListener, BaseAdapter.OnItemClickListener {

    private boolean isViewCreated = false;          // 判断页面是否创建完成
    protected boolean isFetchPrepared = false;      // 判断页面加载完成

    protected RecyclerView recyclerView;
    protected SwipeRefreshLayout swipeRefresh;

    protected BaseAdapter adapter;
    protected List listBeans;

    protected int page = 1;                          // 加载的页数
    protected boolean isFetchDataAll = false;        // 页数是否全部完成


    public static final int PAGE_DEFAULT = 0;       // 默认页
    public static final int PAGE_ONE = 1;

    /**
     * 是否是Fragment的第一个页面
     *
     * @return
     */
    protected int getPageFragment() {
        return PAGE_DEFAULT;
    }

    /**
     * 设置 Adapper
     *
     * @return
     */
    protected abstract BaseAdapter getAdapter();

    /**
     * 设置RecyclerView.LaboutManager
     *
     * @return
     */
    protected abstract RecyclerView.LayoutManager getLayoutManager();

    /**
     * 异步加载的后台任务
     *
     * @return
     */
    protected abstract void observableTask(ObservableEmitter<T> emitter);

    /**
     * 异步加载结束的数据处理
     *
     * @param t 返回的数据
     */
    protected abstract void observerData(T t);

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return LayoutInflater.from(getActivity()).inflate(layoutID(), null);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint() && isViewCreated && !isFetchPrepared) {
            startAsyncTask();       // 页面可见并且View创建后开始加载异步任务
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        if (getPageFragment() == PAGE_ONE) {
            startAsyncTask();             // 第一个Fragment加载异步任务
        }
        isViewCreated = true;             // 页面创建完成的标志
    }

    /**
     * 布局文件
     *
     * @return
     */
    protected int layoutID() {
        return R.layout.fragment_post_rv;
    }

    /**
     * 初始化View
     *
     * @param view
     */
    protected void initView(View view) {
        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        recyclerView = view.findViewById(R.id.recyclerView);

        listBeans = new ArrayList<>();
        swipeRefresh.setRefreshing(true);
        swipeRefresh.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE, Color.CYAN, Color.BLACK);
        swipeRefresh.setOnRefreshListener(this);

        adapter = getAdapter();
        recyclerView.setLayoutManager(getLayoutManager());
        recyclerView.setAdapter(adapter);    // 设置RecyclerView的适配器
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter.setOnItemClickListener(this);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                    if (layoutManager instanceof StaggeredGridLayoutManager) {
                        ((StaggeredGridLayoutManager) layoutManager).invalidateSpanAssignments();   // 解决屏闪
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                // 得到当前显示的最后一个item的view
                View lastChildView = layoutManager.getChildAt(layoutManager.getChildCount() - 1);
                int lastChildBottom = lastChildView.getBottom();        // 得到lastChildView的bottom坐标值
                // 得到Recyclerview的底部坐标减去底部padding值，也就是显示内容最底部的坐标
                int recyclerBottom = recyclerView.getBottom() - recyclerView.getPaddingBottom();
                // 通过这个lastChildView得到这个view当前的position值
                int lastPosition = layoutManager.getPosition(lastChildView);
                //判断lastChildView的bottom值跟recyclerBottom，判断lastPosition是不是最后一个position，如果两个条件都满足则说明是真正的滑动到了底部
                if (lastChildBottom == recyclerBottom && lastPosition == recyclerView.getLayoutManager().getItemCount() - 1) {
                    recyclerView.stopScroll();              // 解决下拉到底部时自动滚动
                    onLastPosition(lastPosition);
                }
            }
        });
    }

    /**
     * 滑动到最后一个position
     *
     * @param lastPosition
     */
    private void onLastPosition(int lastPosition) {
        if (!isFetchDataAll) {
            adapter.setLoadingStatus(BaseAdapter.LOAD_LOADING);
            adapter.setNotifyStart(lastPosition);
            startAsyncTask();
        } else {
            adapter.setLoadingStatus(BaseAdapter.LOAD_FINISH);
        }
    }

    /**
     * 刷新监听事件
     */
    @Override
    public void onRefresh() {
        swipeRefresh.setRefreshing(true);
        startAsyncTask();
    }

    /**
     * Item点击事件
     *
     * @param viewHolder
     * @param position
     */
    @Override
    public void onItemClick(ViewHolder viewHolder, int position) {
    }

    /**
     * Item长按事件
     *
     * @param position
     */
    @Override
    public void onItemLongClick(int position) {

    }

    /**
     * 被观察者
     */
    protected Observable<T> observable = Observable.create(new ObservableOnSubscribe<T>() {
        @Override
        public void subscribe(ObservableEmitter<T> emitter) throws Exception {
            observableTask(emitter);
        }
    });

    /**
     * 观察者
     */
    protected Observer observer = new Observer() {
        @Override
        public void onSubscribe(Disposable d) {

        }

        @Override
        public void onNext(Object p0) {
            if (!isFetchPrepared) {
                isFetchPrepared = true;
            }
            observerData((T) p0);
        }

        @Override
        public void onError(Throwable e) {
            if (e != null) {
                isFetchDataAll = true;      // 返回为空解析失败或没有更多数据时不再加载数据
                adapter.setLoadingStatus(BaseAdapter.LOAD_FINISH);
            }
        }

        @Override
        public void onComplete() {
            adapter.setLoadingStatus(BaseAdapter.LOAD_MORE);
        }
    };

    /**
     * 订阅(执行异步请求)
     */
    protected void startAsyncTask() {
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    /**
     * 防止内存泄漏
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isViewCreated = false;
        isFetchPrepared = false;
        observable.distinct();
    }
}