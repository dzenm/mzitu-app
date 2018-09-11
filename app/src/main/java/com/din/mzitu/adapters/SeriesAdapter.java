package com.din.mzitu.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.request.RequestOptions;
import com.din.mzitu.R;
import com.din.mzitu.beans.SeriesBean;
import com.din.mzitu.mvp.ui.activities.ContentActivity;
import com.din.mzitu.mvp.ui.fragments.mzitus.FragmentContent;

import java.util.List;

public class SeriesAdapter extends BaseAdapter {

    private Context context;
    private String webSite;

    public void setWebSite(String webSite) {
        this.webSite = webSite;
    }

    @Override
    protected int layoutContentID() {
        return R.layout.rv_series;
    }

    @Override
    protected void createContentHolder(final Context context, final ViewHolder holder, List beans) {
        this.context = context;
        final List<SeriesBean> seriesBeans = beans;
        View view = holder.itemView;
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                SeriesBean bean = seriesBeans.get(position);         // 获取数据的item
                Intent intent = new Intent(context, ContentActivity.class);
                intent.putExtra(FragmentContent.CONTENT_URL, bean.getUrl());      // 将URL和TITLE传递到下一个页面
                intent.putExtra(FragmentContent.CONTENT_TITLE, bean.getTitle());
                intent.putExtra(FragmentContent.CONTENT_WEBSITE, webSite);
                context.startActivity(intent);
            }
        });
    }

    @Override
    protected void setBindViewHolderData(ViewHolder viewHolder, Object bean, String url) {
        SeriesBean seriesBean = (SeriesBean) bean;
        TextView title = viewHolder.get(R.id.text);
        ImageView imageView = viewHolder.get(R.id.image);
        title.setText(seriesBean.getTitle());
        if (url != null) {
            GlideUrl glideUrl = new GlideUrl(url, header);  // 防盗链，图片会加载失败，需要更换请求头
            Glide.with(context).load(glideUrl).into(imageView);
        } else {
            RequestOptions options = new RequestOptions();
            options.skipMemoryCache(true).placeholder(R.drawable.placeholder);
            Glide.with(context).setDefaultRequestOptions(options).load("").into(imageView);
        }
    }

    @Override
    protected void addBeanData(String[] url, List oldBeans, int position) {
        List<SeriesBean> beanList = oldBeans;
        url[position] = beanList.get(position).getImage();
    }
}