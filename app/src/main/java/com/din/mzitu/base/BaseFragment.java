package com.din.mzitu.base;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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

    private RecyclerView recyclerView;
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
     * 滑倒底部之后加载下一页
     *
     * @param position 最后一个可见item的position
     */
    protected abstract void pagingData(int position);

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
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                // newState的三种状态:
                // RecyclerView.SCROLL_STATE_IDLE（滑动停止状态）
                // RecyclerView.SCROLL_STATE_DRAGGING（自然滑动状态）
                // RecyclerView.SCROLL_STATE_SETTLING（手指滑动状态）
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    int lastPosition = -1;
                    RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                    if (layoutManager instanceof LinearLayoutManager) {
                        // 通过LayoutManager找到当前显示的最后的item的position
                        lastPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
                    } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                        // 瀑布流布局需要获取列数
                        int[] lastPositions = new int[((StaggeredGridLayoutManager) layoutManager).getSpanCount()];
                        // 获取瀑布流最后一行的数组
                        ((StaggeredGridLayoutManager) layoutManager).findLastVisibleItemPositions(lastPositions);
                        lastPosition = findMax(lastPositions);

                        // 解决屏闪
                        ((StaggeredGridLayoutManager) layoutManager).invalidateSpanAssignments();
                    }
                    //时判断界面显示的最后item的position是否等于itemCount总数-1也就是最后一个item的position
                    //如果相等则说明已经滑动到最后了
                    if (lastPosition == recyclerView.getLayoutManager().getItemCount() - 1) {
                        if (!isFetchDataAll) {
                            pagingData(lastPosition);
                        } else {
                            Toast.makeText(getActivity(), "已经滑到底了", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                // 得到当前显示的最后一个item的view
                View lastChildView = recyclerView.getLayoutManager().getChildAt(recyclerView.getLayoutManager().getChildCount() - 1);
                // 得到lastChildView的bottom坐标值
                int lastChildBottom = lastChildView.getBottom();
                // 得到Recyclerview的底部坐标减去底部padding值，也就是显示内容最底部的坐标
                int recyclerBottom = recyclerView.getBottom() - recyclerView.getPaddingBottom();
                // 通过这个lastChildView得到这个view当前的position值
                int lastPosition = recyclerView.getLayoutManager().getPosition(lastChildView);
                //判断lastChildView的bottom值跟recyclerBottom
                //判断lastPosition是不是最后一个position
                //如果两个条件都满足则说明是真正的滑动到了底部
                if (lastChildBottom == recyclerBottom && lastPosition == recyclerView.getLayoutManager().getItemCount() - 1) {
                }
            }
        });

        FloatingActionButton actionButton = view.findViewById(R.id.floatbtn);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView.smoothScrollToPosition(0);
            }
        });
    }

    @Override
    public void onRefresh() {
    }

    /**
     * 获取瀑布流最后一个可见的Item
     *
     * @param positions 需要的比较数组
     * @return 找到的最大数
     */
    private int findMax(int[] positions) {
        int max = positions[0];
        for (int value : positions) {
            if (max < value) {
                max = value;
            }
        }
        return max;
    }

    @Override
    public void onItemClick(ViewHolder viewHolder, int position) {
    }

    protected Observable<T> observable = Observable.create(new ObservableOnSubscribe<T>() {
        @Override
        public void subscribe(ObservableEmitter<T> emitter) throws Exception {
            observableTask(emitter);
        }
    });

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
            }
        }

        @Override
        public void onComplete() {

        }
    };

    protected void startAsyncTask() {
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isViewCreated = false;
        isFetchPrepared = false;
        observable.distinct();
    }
}