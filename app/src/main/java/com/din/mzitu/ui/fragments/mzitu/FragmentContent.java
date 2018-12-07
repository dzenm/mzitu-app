package com.din.mzitu.ui.fragments.mzitu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.LinearLayoutManager;

import com.din.mzitu.R;
import com.din.mzitu.adapters.ContentAdapter;
import com.din.mzitu.adapters.ViewHolder;
import com.din.mzitu.api.LiGui;
import com.din.mzitu.api.Mzitu;
import com.din.mzitu.base.BaseAdapter;
import com.din.mzitu.base.BaseFragment;
import com.din.mzitu.bean.ContentBean;
import com.din.mzitu.ui.activities.PictureActivity;
import com.din.mzitu.ui.fragments.main.FragmentLiGui;
import com.din.mzitu.ui.fragments.main.FragmentMzitu;

import java.util.List;

public class FragmentContent extends BaseFragment implements BaseAdapter.OnItemClickListener {

    public static final String CONTENT_URL = "content_url";
    public static final String CONTENT_TITLE = "content_title";
    public static final String CONTENT_WEBSITE = "content_website";
    public static final String CONTENT = "self";

    public static final String POSITION = "position";

    /**
     * 创建一个实例
     *
     * @param url 爬取数据的url
     * @return
     */
    public static FragmentContent newInstance(String url, int position) {
        Bundle bundle = new Bundle();
        bundle.putString(CONTENT, url);
        bundle.putInt(POSITION, position);
        FragmentContent fragmentSelf = new FragmentContent();
        fragmentSelf.setArguments(bundle);
        return fragmentSelf;
    }

    @Override
    protected int getPageFragment() {
        return getArguments().getInt(POSITION);
    }

    @Override
    protected List<ContentBean> doInBackgroundTask(int page) {
        // 获取页面加载的url
        String url;
        Intent intent = getActivity().getIntent();
        String website = intent.getStringExtra(CONTENT_WEBSITE);
        if (website.equals(FragmentMzitu.MZITU)) {
            url = intent.getStringExtra(CONTENT_URL);
            return Mzitu.getInstance().parseMzituContentData(page, url);
        } else if (website.equals(FragmentLiGui.LIGUI)) {
            url = intent.getStringExtra(CONTENT_URL);
            return LiGui.getInstance().parseLiGuiContentData(page, url);
        }
        return null;
    }

    @Override
    protected void nextPageData(int position) {
        adapter.setLoadingStatus(ContentAdapter.LOADING_STATE_MORE);
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
    protected void postExecuteTask(Object p0) {
        if (p0 != null) {
            if (!isFetchPrepared) {
                isFetchPrepared = true;
            }
            listBeans.addAll((List<ContentBean>) p0);
            adapter.addBeanData(listBeans);
        } else {
            // 返回为空解析失败或没有更多数据时不再加载数据
            isFetchDataAll = true;
        }
        swipeRefresh.setRefreshing(false);          // 获取数据之后，刷新停止
    }

    @Override
    public void onItemClick(ViewHolder holder, int position) {
        ContentBean contentBean = (ContentBean) listBeans.get(position - 1);
        Intent intent = new Intent(getActivity(), PictureActivity.class);
        intent.putExtra(PictureActivity.PICTURE_TITLE, getArguments().getString(CONTENT_TITLE));
        intent.putExtra(PictureActivity.PICTURE_IMAGE, contentBean.getImage());
        intent.putExtra(PictureActivity.PICTURE_POSITION, position - 1);
        startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation
                (getActivity(), holder.itemView, "transition_name_picture").toBundle());
        getActivity().overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
    }
}