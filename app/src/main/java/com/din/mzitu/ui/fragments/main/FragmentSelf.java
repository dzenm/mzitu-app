package com.din.mzitu.ui.fragments.main;

import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.din.mzitu.R;
import com.din.mzitu.adapters.ContentAdapter;
import com.din.mzitu.adapters.ViewHolder;
import com.din.mzitu.api.Mzitu;
import com.din.mzitu.base.BaseFragment;
import com.din.mzitu.bean.ContentBean;
import com.din.mzitu.ui.activities.PictureActivity;
import com.din.mzitu.utill.Url;

import java.util.List;

public class FragmentSelf extends BaseFragment {

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
        return R.layout.fragment_self;
    }

    @Override
    protected List<ContentBean> doInBackgroundTask(int page) {
        return Mzitu.getInstance().parseMzituSelfData(page, Url.MZITU_SELF);
    }

    @Override
    protected void nextPageData(int position) {
        adapter.setLoadingStatus(ContentAdapter.LOADING_STATE_RUNNING);
        adapter.setNotifyStart(position);
        startAsyncTask(++page);
    }

    @Override
    public ContentAdapter getAdapter() {
        return new ContentAdapter(getActivity());
    }

    @Override
    public LinearLayoutManager getLayoutManager() {
        return new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
    }

    @Override
    protected void postExecuteTask(Object o) {
        if (o != null) {
            if (!isFetchPrepared) {
                isFetchPrepared = true;
            }
            listBeans.addAll((List<ContentBean>) o);
            adapter.addBeanData(listBeans);
            adapter.setLoadingStatus(ContentAdapter.LOADING_STATE_MORE);
        } else {
            isFetchDataAll = true;
        }
        swipeRefresh.setRefreshing(false);          // 获取数据之后，刷新停止
    }

    @Override
    public void onItemClick(ViewHolder holder, int position) {
        ContentBean contentBean = (ContentBean) listBeans.get(position - 1);
        Intent intent = new Intent(getActivity(), PictureActivity.class);
        intent.putExtra(PictureActivity.PICTURE_TITLE, contentBean.getTitle());
        intent.putExtra(PictureActivity.PICTURE_IMAGE, contentBean.getImage());
        intent.putExtra(PictureActivity.PICTURE_POSITION, position - 1);
        startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation
                (getActivity(), holder.itemView, "transition_name_picture").toBundle());
        getActivity().overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
    }
}
