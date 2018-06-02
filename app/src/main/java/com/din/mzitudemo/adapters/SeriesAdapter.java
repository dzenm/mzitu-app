package com.din.mzitudemo.adapters;

import android.content.Context;
import android.content.Intent;
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
import com.din.mzitudemo.activities.ContentActivity;
import com.din.mzitudemo.javabeans.SeriesBean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SeriesAdapter extends RecyclerView.Adapter<SeriesAdapter.ViewHolder> {
    private Context context;
    private List<SeriesBean> list;

    public SeriesAdapter(Context context, List<SeriesBean> list) {
        this.context = context;
        this.list = list;
    }

//    public void addData(List<SeriesBean> beans) {
//        if (beans.isEmpty()) {
//            return;
//        }
//        beans = list;
//        notifyDataSetChanged();
//    }
//
//    public void setData(List<SeriesBean> beans) {
//        if (beans.isEmpty()) {
//            return;
//        }
//        int oldSize = list.size();
//        list.addAll(beans);
//        int newSize = list.size();
//        notifyItemRangeInserted(oldSize, newSize);
//    }

    @NonNull
    @Override
    public SeriesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_rx_item, null);
        final SeriesAdapter.ViewHolder holder = new SeriesAdapter.ViewHolder(view);
        // 点击事件
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();     // 获取点击的当前item
                SeriesBean bean = list.get(position);         // 获取数据的item
                Intent intent = new Intent(context, ContentActivity.class);
                intent.putExtra("URL", bean.getUrl());      // 将URL和TITLE传递到下一个页面
                intent.putExtra("TITLE", bean.getTitle());
                context.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull SeriesAdapter.ViewHolder holder, int position) {
        SeriesBean bean = list.get(position);
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
        private View view;

        public ViewHolder(View itemView) {
            super(itemView);
            this.view = itemView;
            imageView = itemView.findViewById(R.id.image);
            title = itemView.findViewById(R.id.text);
        }

        // 绑定数据
        private void dataBind(SeriesBean bean) {
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
            header.put("Referer", "http://www.mzitu.com");
            return header;
        }
    };
}