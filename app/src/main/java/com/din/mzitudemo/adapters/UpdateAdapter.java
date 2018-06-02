package com.din.mzitudemo.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.din.mzitudemo.R;
import com.din.mzitudemo.activities.ContentActivity;
import com.din.mzitudemo.javabeans.UpdateBean;

import java.util.List;

public class UpdateAdapter extends RecyclerView.Adapter<UpdateAdapter.ViewHolder> {

    private Context context;
    private List<UpdateBean> list;

    public UpdateAdapter(Context context, List<UpdateBean> list) {
        this.context = context;
        this.list = list;
    }

    public void addData(List<UpdateBean> beans) {
        if (beans.isEmpty()) {
            return;
        }
        beans = list;
        notifyDataSetChanged();
    }

    public void setData(List<UpdateBean> beans) {
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
    public UpdateAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_day_update_item, null);
        final ViewHolder holder = new ViewHolder(view);
        // 点击事件
        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();     // 获取点击的当前item
                UpdateBean bean = list.get(position);         // 获取数据的item
                Intent intent = new Intent(context, ContentActivity.class);
                intent.putExtra("URL", bean.getUrl());      // 将URL和TITLE传递到下一个页面
                intent.putExtra("TITLE", bean.getTitle());
                context.startActivity(intent);
            }
        });
        return holder;
    }

    // viewholder
    @Override
    public void onBindViewHolder(@NonNull UpdateAdapter.ViewHolder holder, int position) {
        UpdateBean bean = list.get(position);
        holder.bindData(bean);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
        }

        // 绑定数据
        private void bindData(UpdateBean bean) {
            title.setText(bean.getTitle());
        }
    }
}