package com.din.mzitu.ui.fragments.mzitu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;

import com.din.mzitu.R;
import com.din.mzitu.adapter.ContentAdapter;
import com.din.mzitu.adapter.ViewHolder;
import com.din.mzitu.api.LiGui;
import com.din.mzitu.api.Mzitu;
import com.din.mzitu.base.BaseAdapter;
import com.din.mzitu.base.BaseFragment;
import com.din.mzitu.bean.ContentBean;
import com.din.mzitu.ui.activities.PictureActivity;
import com.din.mzitu.ui.fragments.main.FragmentLiGui;
import com.din.mzitu.ui.fragments.main.FragmentMzitu;

import java.util.List;

import io.reactivex.ObservableEmitter;

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
    protected void observableTask(ObservableEmitter emitter) {
        // 获取页面加载的url
        String url;
        Intent intent = getActivity().getIntent();
        String website = intent.getStringExtra(CONTENT_WEBSITE);
        if (website.equals(FragmentMzitu.MZITU)) {
            url = intent.getStringExtra(CONTENT_URL);
            emitter.onNext(Mzitu.getInstance().parseMzituContentData(page++, url));
        } else if (website.equals(FragmentLiGui.LIGUI)) {
            url = intent.getStringExtra(CONTENT_URL);
            emitter.onNext(LiGui.getInstance().parseLiGuiContentData(page++, url));
        }
    }

    @Override
    protected void pagingData(int position) {
        adapter.setNotifyStart(position);
        startAsyncTask();
    }

    @Override
    public ContentAdapter getAdapter() {
        return new ContentAdapter(this);
    }

    @Override
    public LinearLayoutManager getLayoutManager() {
        return new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
    }

    @Override
    protected void observerData(Object p0) {
        listBeans.addAll((List<ContentBean>) p0);
        Log.d("DZY", "p0: " + ((List<ContentBean>) p0).size());
        adapter.addBeanData(listBeans);
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