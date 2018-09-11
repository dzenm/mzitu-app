package com.din.mzitu.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.din.mzitu.R;
import com.din.mzitu.beans.UpdateBean;
import com.din.mzitu.mvp.ui.activities.ContentActivity;

import java.util.List;

public class UpdateAdapter extends BaseAdapter {

    @Override
    protected int layoutContentID() {
        return R.layout.rv_update;
    }

    @Override
    protected void createContentHolder(final Context context, final ViewHolder holder, List beans) {
        final List<UpdateBean> updateBeans = beans;
        View view = holder.itemView;
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                UpdateBean bean = updateBeans.get(position);
                Intent intent = new Intent(context, ContentActivity.class);
                intent.putExtra("URL", bean.getUrl());      // 将URL和TITLE传递到下一个页面
                intent.putExtra("TITLE", bean.getTitle());
                context.startActivity(intent);
            }
        });
    }

    @Override
    protected void setBindViewHolderData(ViewHolder viewHolder, Object o, String url) {
        TextView title = viewHolder.get(R.id.title);
        title.setText(url);
    }

    @Override
    protected void addBeanData(String[] url, List oldBeans, int position) {
        List<UpdateBean> beanList = oldBeans;
        url[position] = beanList.get(position).getTitle();
    }
}