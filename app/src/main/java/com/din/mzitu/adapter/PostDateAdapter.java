package com.din.mzitu.adapter;

import android.support.v4.app.Fragment;
import android.widget.TextView;

import com.din.mzitu.R;
import com.din.mzitu.base.BaseAdapter;
import com.din.mzitu.bean.PostDateBean;

public class PostDateAdapter extends BaseAdapter {

    public PostDateAdapter(Fragment fragment) {
        super(fragment);
    }

    @Override
    protected int layoutContentID() {
        return R.layout.rv_post_date;
    }

    @Override
    protected void setBindViewHolderData(ViewHolder viewHolder, int position) {
        TextView title = viewHolder.get(R.id.title);
        PostDateBean postDateBean = (PostDateBean) beans.get(position);
        title.setText(postDateBean.getTitle());
    }

}