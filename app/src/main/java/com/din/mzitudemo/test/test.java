//package com.din.mzitudemo.test;
//
//import android.app.Fragment;
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.design.widget.FloatingActionButton;
//import android.support.v4.widget.SwipeRefreshLayout;
//import android.support.v7.widget.DefaultItemAnimator;
//import android.support.v7.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import com.bumptech.glide.Glide;
//import com.din.mzitudemo.R;
//
//import java.util.List;
//
//public class test {
//    public abstract class BaseFragment extends Fragment {
//
//        private boolean isViewPrepared; // 是否Fragment初始化完毕
//        private boolean hasFetchData;   // 是否触发懒加载
//
//        private int page = 1;
//        private RecyclerView recyclerView;
//        private SwipeRefreshLayout swipeRefresh;
//        private FloatingActionButton floatButton;
//
//        protected abstract List<?> list();      // List 数据
//
//        protected abstract RecyclerView.Adapter adapter();      // RecyclerView适配器
//
//        protected abstract RecyclerView.LayoutManager layoutManager();      // RecyclerView LayoutManager
//
//        protected abstract void getData();
//
//        @Nullable
//        @Override
//        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
//            View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment, null);
//            recyclerView = view.findViewById(R.id.RecyclerView);
//            swipeRefresh = view.findViewById(R.id.swipeRefresh);
//            floatButton = view.findViewById(R.id.floatbtn);
//            initView();
//            return view;
//        }
//
//        private void initView() {
//            startRefresh();
//            recyclerView.setLayoutManager(layoutManager());
//            recyclerView.setAdapter(adapter());
//            recyclerView.setItemAnimator(new DefaultItemAnimator());
//            floatButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    recyclerView.smoothScrollToPosition(0);
//                }
//            });
//            swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//                @Override
//                public void onRefresh() {
//                    startRefresh();// 下拉刷新时异步加载数据
//                }
//            });
//            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//                @Override
//                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                    super.onScrollStateChanged(recyclerView, newState);
//                    recyclerViewScrollStateChanged(newState);
//                }
//
//                @Override
//                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                    super.onScrolled(recyclerView, dx, dy);
//                    int totalCount = adapter().getItemCount();
////                if (totalCount - lastVisibility() <= 3) {
////                    page++;
////                }
//                }
//            });
//        }
//
//        /**
//         * 如果初始化完毕，懒加载数据，加上标记
//         */
//        private void lazyFetchDataPrepared() {
//            if (getUserVisibleHint() && !hasFetchData && isViewPrepared) {
//                hasFetchData = true;
//            }
//        }
//
//        /**
//         * RecyclerView滑动状态改变
//         *
//         * @param newState
//         */
//        private void recyclerViewScrollStateChanged(int newState) {
//            switch (newState) {
//                case RecyclerView.SCROLL_STATE_IDLE:
//                    Glide.with(getActivity()).resumeRequests();
//                    break;
//                case RecyclerView.SCROLL_STATE_DRAGGING:
//                    Glide.with(getActivity()).pauseRequests();
//                    break;
//                case RecyclerView.SCROLL_STATE_SETTLING:
//                    Glide.with(getActivity()).resumeRequests();
//                    break;
//                default:
//                    break;
//            }
//        }
//
//        /**
//         * 页面可见，执行懒加载数据
//         *
//         * @param isVisibleToUser
//         */
//        @Override
//        public void setUserVisibleHint(boolean isVisibleToUser) {
//            super.setUserVisibleHint(isVisibleToUser);
//            if (isVisibleToUser) {
//                lazyFetchDataPrepared();
//            }
//        }
//
//        /**
//         * 页面创建时，初始化数据
//         *
//         * @param view
//         * @param savedInstanceState
//         */
//        @Override
//        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//            super.onViewCreated(view, savedInstanceState);
//            isViewPrepared = true;
//            lazyFetchDataPrepared();
//        }
//
//        /**
//         * 停止刷新数据
//         *
//         * @param list
//         */
//        public void finishRefresh(List<?> list) {
//            if (swipeRefresh.isRefreshing() && list != null) {   // 获取数据之后，刷新停止
//                swipeRefresh.setRefreshing(false);
//            }
//        }
//
//        /**
//         * 开始刷新数据
//         */
//        private void startRefresh() {
//            swipeRefresh.setRefreshing(true);
//            getData();
//        }
//
//
//        /**
//         * view被销毁时，设置取消标记
//         */
//        @Override
//        public void onDestroyView() {
//            super.onDestroyView();
//            hasFetchData = false;
//            isViewPrepared = false;
//        }
//    }
//}
