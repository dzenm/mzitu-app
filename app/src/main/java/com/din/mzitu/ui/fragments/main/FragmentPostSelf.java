package com.din.mzitu.ui.fragments.main;

import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.din.mzitu.R;
import com.din.mzitu.adapter.PostSingleAdapter;
import com.din.mzitu.adapter.ViewHolder;
import com.din.mzitu.api.Mzitu;
import com.din.mzitu.base.BaseFragment;
import com.din.mzitu.bean.PostSingleBean;
import com.din.mzitu.ui.activities.PictureSingleActivity;
import com.din.mzitu.utill.Url;

import java.util.List;

import io.reactivex.ObservableEmitter;

public class FragmentPostSelf extends BaseFragment {

    @Override
    protected int getPageFragment() {
        return PAGE_ONE;
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("自拍");
    }

    @Override
    protected int layoutID() {
        return R.layout.fragment_post_self;
    }

    @Override
    protected void observableTask(ObservableEmitter emitter) {
        emitter.onNext(Mzitu.getInstance().parseMzituSelfData(++page, Url.MZITU_SELF));
    }

    @Override
    protected void pagingData(int position) {
        adapter.setNotifyStart(position);
        startAsyncTask();
    }

    @Override
    public PostSingleAdapter getAdapter() {
        return new PostSingleAdapter(this);
    }

    @Override
    public LinearLayoutManager getLayoutManager() {
        return new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
    }

    @Override
    protected void observerData(Object o) {
        listBeans.addAll((List<PostSingleBean>) o);
        adapter.addBeanData(listBeans);
        swipeRefresh.setRefreshing(false);          // 获取数据之后，刷新停止
    }

    @Override
    public void onItemClick(ViewHolder holder, int position) {
        PostSingleBean postSingleBean = (PostSingleBean) listBeans.get(position - 1);
        Intent intent = new Intent(getActivity(), PictureSingleActivity.class);
        intent.putExtra(PictureSingleActivity.PICTURE_TITLE, postSingleBean.getTitle());
        intent.putExtra(PictureSingleActivity.PICTURE_IMAGE, postSingleBean.getImage());
        intent.putExtra(PictureSingleActivity.PICTURE_POSITION, position - 1);
        startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation
                (getActivity(), holder.itemView, "transition_name_picture").toBundle());
        getActivity().overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
    }
}
