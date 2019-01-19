package com.din.mzitu.fragments.mzitu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;

import com.din.mzitu.activities.PicActivity;
import com.din.mzitu.adapter.PostDateAdapter;
import com.din.mzitu.adapter.ViewHolder;
import com.din.mzitu.api.Mzitu;
import com.din.mzitu.base.BaseFragment;
import com.din.mzitu.bean.PostDateBean;
import com.din.mzitu.fragments.main.FragmentMzitu;

import java.util.List;

import io.reactivex.ObservableEmitter;

public class FragmentPostDate extends BaseFragment {

    public static final String POST_DATE = "post_date";

    public static FragmentPostDate newInstance(String url) {
        Bundle bundle = new Bundle();
        bundle.putString(POST_DATE, url);
        FragmentPostDate fragmentPostDate = new FragmentPostDate();
        fragmentPostDate.setArguments(bundle);
        return fragmentPostDate;
    }

    @Override
    protected void observableTask(ObservableEmitter emitter) {
        String url = getArguments().getString(POST_DATE);
        emitter.onNext(Mzitu.getInstance().parseMzituUpdateData(++page, url));
        emitter.onComplete();
    }

    @Override
    public PostDateAdapter getAdapter() {
        return new PostDateAdapter(this);
    }

    @Override
    public LinearLayoutManager getLayoutManager() {
        return new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
    }

    @Override
    protected void observerData(Object p0) {
        listBeans.addAll((List<PostDateBean>) p0);
        adapter.addBeanData(listBeans);
        swipeRefresh.setRefreshing(false);          // 获取数据之后，刷新停止
    }

    @Override
    public void onItemClick(ViewHolder holder, int position) {
        PostDateBean bean = (PostDateBean) listBeans.get(position - 1);
        Intent intent = new Intent(getActivity(), PicActivity.class);
        intent.putExtra(FragmentPic.POST_URL, bean.getUrl());      // 将URL和TITLE传递到下一个页面
        intent.putExtra(FragmentPic.POST_TITLE, bean.getTitle());
        intent.putExtra(FragmentPic.POST_WEBSITE, FragmentMzitu.MZITU);
        startActivity(intent);
    }
}