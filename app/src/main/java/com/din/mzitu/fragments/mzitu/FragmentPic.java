package com.din.mzitu.fragments.mzitu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;

import com.din.mzitu.R;
import com.din.mzitu.activities.PicSingleActivity;
import com.din.mzitu.adapter.PicAdapter;
import com.din.mzitu.adapter.ViewHolder;
import com.din.mzitu.api.LiGui;
import com.din.mzitu.api.Mzitu;
import com.din.mzitu.base.BaseFragment;
import com.din.mzitu.bean.PicBean;
import com.din.mzitu.fragments.main.FragmentLiGui;
import com.din.mzitu.fragments.main.FragmentMzitu;
import com.din.mzitu.utill.BackToTopListener;

import java.util.List;

import io.reactivex.ObservableEmitter;

public class FragmentPic extends BaseFragment implements BackToTopListener.BackToTopView {

    public static final String POST_URL = "post_url";
    public static final String POST_TITLE = "post_title";
    public static final String POST_WEBSITE = "post_website";
    public static final String POST_SELF = "self";

    public static final String POSITION = "position";

    /**
     * 创建一个实例
     *
     * @param url 爬取数据的url
     * @return
     */
    public static FragmentPic newInstance(String url, int position) {
        Bundle bundle = new Bundle();
        bundle.putString(POST_SELF, url);
        bundle.putInt(POSITION, position);
        FragmentPic fragmentSelf = new FragmentPic();
        fragmentSelf.setArguments(bundle);
        return fragmentSelf;
    }

    @Override
    protected int getPageFragment() {
        return getArguments().getInt(POSITION);
    }

    @Override
    protected void observableTask(ObservableEmitter emitter) {
        // 获取页面加载的url
        String url;
        Intent intent = getActivity().getIntent();
        String website = intent.getStringExtra(POST_WEBSITE);
        if (website.equals(FragmentMzitu.MZITU)) {
            url = intent.getStringExtra(POST_URL);
            emitter.onNext(Mzitu.getInstance().parseMzituContentData(page++, url));
        } else if (website.equals(FragmentLiGui.LIGUI)) {
            url = intent.getStringExtra(POST_URL);
            emitter.onNext(LiGui.getInstance().parseLiGuiContentData(page++, url));
        }
    }

    @Override
    public PicAdapter getAdapter() {
        // 双击状态栏回到顶部
        final Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setOnClickListener(new BackToTopListener(this));
        return new PicAdapter(this);
    }

    @Override
    public LinearLayoutManager getLayoutManager() {
        return new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
    }

    @Override
    protected void observerData(Object p0) {
        listBeans.addAll((List<PicBean>) p0);
        adapter.addBeanData(listBeans);
        swipeRefresh.setRefreshing(false);          // 获取数据之后，刷新停止
    }

    @Override
    public void onItemClick(ViewHolder holder, int position) {
        PicBean picBean = (PicBean) listBeans.get(position - 1);
        Intent intent = new Intent(getActivity(), PicSingleActivity.class);
        String title = getActivity().getIntent().getStringExtra(POST_TITLE);
        if (title == null) {
            title = getArguments().getString(POST_TITLE);
        }
        intent.putExtra(PicSingleActivity.PICTURE_TITLE, title);
        intent.putExtra(PicSingleActivity.PICTURE_IMAGE, picBean.getImage());
        intent.putExtra(PicSingleActivity.PICTURE_POSITION, position - 1);
        startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation
                (getActivity(), holder.itemView, "transition_name_picture").toBundle());
        getActivity().overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
    }

    @Override
    public void onItemLongClick(int position) {
    }

    @Override
    public void backToTop() {
        recyclerView.smoothScrollToPosition(0);
    }
}