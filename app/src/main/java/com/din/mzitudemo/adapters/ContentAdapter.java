package com.din.mzitudemo.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.Headers;
import com.din.mzitudemo.R;
import com.din.mzitudemo.javabeans.ContentBean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.ViewHolder> {

    private Context context;
    private List<ContentBean> list;

    public ContentAdapter(Context context, List<ContentBean> list) {
        this.context = context;
        this.list = list;
    }

    public void addData(List<ContentBean> beans) {
        if (beans.isEmpty()) {
            return;
        }
        beans = list;
        notifyDataSetChanged();
    }

    public void setData(List<ContentBean> beans) {
        if (beans.isEmpty()) {
            return;
        }
        int oldSize = list.size();
        list.addAll(beans);
        int newSize = list.size();
        notifyItemRangeInserted(oldSize, newSize);
    }

    @NonNull
    @Override
    public ContentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_rv_item, null);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ContentAdapter.ViewHolder holder, int position) {
        ContentBean bean = list.get(position);
        holder.dataBind(bean);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    // viewholder
    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView title;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
            title = itemView.findViewById(R.id.text);
        }

        // 绑定数据
        private void dataBind(ContentBean bean) {
            title.setText(bean.getTitle());
            String url = bean.getImage();
            GlideUrl glideUrl = new GlideUrl(url, header);  // 防盗链，图片会加载失败，需要更换请求头
            Glide.with(context).load(glideUrl).into(imageView);
        }
    }

    // 实现Headers接口，重写getHeaders方法
    Headers header = new Headers() {
        @Override
        public Map<String, String> getHeaders() {
            Map<String, String> header = new HashMap<>();
            header.put("Referer", "http://www.mzitu.com/all/");
            return header;
        }
    };
}