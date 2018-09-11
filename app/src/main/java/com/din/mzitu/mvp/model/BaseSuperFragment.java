package com.din.mzitu.mvp.model;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.din.mzitu.mvp.ui.fragments.mzitus.FragmentContent;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseSuperFragment<T> extends Fragment {

    private boolean hasPrePared = false;
    private PictureAsyncTask asyncTask;
    private List<PictureAsyncTask> asyncTasks;

    /**
     * 布局文件
     *
     * @return
     */
    protected abstract int layoutID();

    /**
     * 初始化View
     *
     * @param view
     */
    protected abstract void initView(View view);

    /**
     * 异步加载的后台任务
     *
     * @return
     */
    protected abstract T doInBackgroundTask();

    /**
     * 异步加载结束的数据处理
     *
     * @param t
     */
    protected abstract void postExecuteTask(T t);

    /**
     * 是否是Fragment的第一个页面
     *
     * @return
     */
    protected int getPageFragment() {
        return FragmentContent.PAGE_DEFAULT;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(layoutID(), null);
        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint() && hasPrePared) {
            startAsyncTask();       // 页面可见并且View创建后开始加载异步任务
        }
        if (!getUserVisibleHint() && hasPrePared) {
            stopAsyncTask();
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        if (getPageFragment() == FragmentContent.PAGE_ONE) {
            startAsyncTask();       // 第一个Fragment加载异步任务
        }
        hasPrePared = true;     // 页面创建完成的标志
    }

    /**
     * 开始异步加载任务
     */
    protected void startAsyncTask() {
        asyncTask = new PictureAsyncTask();
        asyncTask.execute();
        asyncTasks = new ArrayList<>();
        asyncTasks.add(asyncTask);
    }

    /**
     * 结束异步加载任务
     */
    protected void stopAsyncTask() {
        if (taskIsRunning()) {
            asyncTask.cancel(true);
        }
    }

    /**
     * 判断异步加载任务是否正在运行
     *
     * @return
     */
    protected boolean taskIsRunning() {
        if (asyncTask != null && asyncTask.getStatus() == AsyncTask.Status.RUNNING) {
            return true;
        }
        return false;
    }

    /**
     * 取消加载任务
     */
    protected void canaelAllTask() {
        if (asyncTasks != null) {
            for (PictureAsyncTask asyncTask : asyncTasks) {
                asyncTask.cancel(true);
            }
        }
    }

    // 创建异步加载任务
    private class PictureAsyncTask extends AsyncTask<String, Void, T> {
        @Override
        protected T doInBackground(String... strings) {
            return doInBackgroundTask();    // 后台任务
        }

        @Override
        protected void onPostExecute(T t) {
            super.onPostExecute(t);
            postExecuteTask(t);    // 后台任务结束执行的任务
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        hasPrePared = false;
    }
}