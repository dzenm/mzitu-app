package com.din.mzitu.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.request.RequestOptions;
import com.din.mzitu.R;
import com.din.mzitu.beans.ContentBean;
import com.din.mzitu.mvp.ui.activities.PictureActivity;

import java.util.List;

public class ContentAdapter extends BaseAdapter {

    private String title;
    private Activity activity;

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    protected int layoutContentID() {
        return R.layout.rv_content;
    }

    @Override
    protected void createContentHolder(final Context context, final ViewHolder holder, List beans) {
        final List<ContentBean> contentBeans = beans;
        View view = holder.itemView;
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                Intent intent = new Intent(context, PictureActivity.class);
                intent.putExtra(PictureActivity.PICTURE_TITLE, title);
                intent.putExtra(PictureActivity.PICTURE_IMAGE, contentBeans.get(position).getImage());
                intent.putExtra(PictureActivity.PICTURE_POSITION, position);
                context.startActivity(intent);
                activity.overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
            }
        });
    }

    @Override
    protected void setBindViewHolderData(ViewHolder viewHolder, Object bean, String url) {
        ContentBean contentBean = (ContentBean) bean;
        TextView title = viewHolder.get(R.id.text);
        ImageView imageView = viewHolder.get(R.id.image);
        title.setText(contentBean.getTitle());
        if (url != null) {
            GlideUrl glideUrl = new GlideUrl(url, header);  // 防盗链，图片会加载失败，需要更换请求头
            Glide.with(activity).load(glideUrl).into(imageView);
        } else {
            RequestOptions options = new RequestOptions();
            options.skipMemoryCache(true).placeholder(R.drawable.placeholder);
            Glide.with(activity).setDefaultRequestOptions(options).load("").into(imageView);
        }
    }

    @Override
    protected void addBeanData(String[] url, List oldBeans, int position) {
        List<ContentBean> beanList = oldBeans;
        url[position] = beanList.get(position).getImage();
    }
}