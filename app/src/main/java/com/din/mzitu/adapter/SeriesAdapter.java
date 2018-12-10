package com.din.mzitu.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.din.mzitu.R;
import com.din.mzitu.base.BaseAdapter;
import com.din.mzitu.bean.SeriesBean;
import com.din.mzitu.utill.GlideApp;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class SeriesAdapter extends BaseAdapter {

    public SeriesAdapter(Fragment fragment) {
        super(fragment);
    }

    @Override
    protected int layoutContentID() {
        return R.layout.rv_series;
    }

    @Override
    protected void setBindViewHolderData(ViewHolder viewHolder, final int position) {
        SeriesBean seriesBean = (SeriesBean) beans.get(position);
        TextView title = viewHolder.get(R.id.text);
        final ImageView imageView = viewHolder.get(R.id.image);
        title.setText(seriesBean.getTitle());

        String header = "";
        if (seriesBean.getType() == TYPE_MZITU) {
            header = HEADER_MZITU;
        } else if (seriesBean.getType() == TYPE_LIGUI) {
            header = HEADER_LIGUI;
        }
        String tag = (String) imageView.getTag(R.id.image_url);
        String url = seriesBean.getImage();

        // 防盗链，图片会加载失败，需要更换请求头
        GlideUrl glideUrl = new GlideUrl(url == tag ? tag : url, header(header));
        if (tag == url) {
            GlideApp.with(fragment)
                    .load(glideUrl)
                    .thumbnail(0.1f)
                    .centerCrop()
                    .skipMemoryCache(false)
                    .transition(withCrossFade())
                    .into(imageView);
        } else {
            GlideApp.with(fragment)
                    .asBitmap()
                    .load(glideUrl)
                    .centerCrop()
                    .skipMemoryCache(false)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder_error)
                    .into(new SimpleTarget<Bitmap>(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL) {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            imageView.setImageBitmap(resource);
                        }
                    });
            imageView.setTag(R.id.image_url, url);
        }
    }
}