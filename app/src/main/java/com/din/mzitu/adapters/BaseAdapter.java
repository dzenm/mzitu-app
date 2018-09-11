package com.din.mzitu.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.load.model.Headers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<T> beans;
    private Context context;
    private String[] urls;

    public static final int ITEM_TYPE_HEAD_COUNT = 1;
    public static final int ITEM_TYPE_FOOT_COUNT = 1;

    public static final int ITEM_TYPE_HEAD = 1;
    public static final int ITEM_TYPE_CONTENT = 2;
    public static final int ITEM_TYPE_FOOT = 3;

    protected boolean addFootViewHolder() {
        return false;
    }

    protected boolean addHeadViewHolder() {
        return false;
    }

    protected boolean addHeadAndFootViewHolder() {
        return false;
    }

    protected abstract int layoutContentID();

    protected int layoutFootID() {
        return 0;
    }

    protected int layoutHeadID() {
        return 0;
    }

    protected void createHeadHolder(Context context, HeadViewHolder holder, List<T> beans) {
        return;
    }

    protected void createContentHolder(Context context, ViewHolder holder, List<T> beans) {
        return;
    }

    protected void createFootHolder(Context context, FootViewHolder holder, List<T> beans) {
        return;
    }

    protected void setBindHeadViewHolderData(HeadViewHolder viewHolder, T bean) {
        return;
    }

    protected void setBindFootViewHolderData(FootViewHolder viewHolder, T bean) {
        return;
    }

    protected abstract void setBindViewHolderData(ViewHolder viewHolder, T bean, String url);

    public void setListBean(List<T> listBean) {
        if (listBean == null) {
            return;
        }
        this.beans = listBean;
        notifyDataSetChanged();
    }

    public void addInsertData(int start, int end) {
        if (urls == null) {
            urls = new String[beans.size()];
        }
        for (int i = start; i <= end + 6 && i < beans.size(); i++) {
            if (urls[i] == null) {
                addBeanData(urls, beans, i);
                notifyItemChanged(i);
            }
        }
    }

    protected abstract void addBeanData(String[] urls, List<T> oldBeans, int position);

    @Override
    public int getItemViewType(int position) {
        if (addHeadAndFootViewHolder()) {
            return addHeadAndFootItem(position);
        } else if (addHeadViewHolder()) {
            return addHeadItem(position);
        } else if (addFootViewHolder()) {
            return addFootItem(position);
        } else {
            return ITEM_TYPE_CONTENT;
        }
    }

    protected int addHeadAndFootItem(int position) {
        if (ITEM_TYPE_HEAD_COUNT != 0 && position == 0) {
            return ITEM_TYPE_HEAD;
        } else if (ITEM_TYPE_FOOT_COUNT != 0 && position == ITEM_TYPE_HEAD_COUNT + beans.size()) {
            return ITEM_TYPE_FOOT;
        } else {
            return ITEM_TYPE_CONTENT;
        }
    }

    protected int addFootItem(int position) {
        if (ITEM_TYPE_FOOT_COUNT != 0 && position == ITEM_TYPE_HEAD_COUNT + beans.size()) {

            return ITEM_TYPE_FOOT;
        } else {
            return ITEM_TYPE_CONTENT;
        }
    }

    protected int addHeadItem(int position) {
        if (ITEM_TYPE_HEAD_COUNT != 0 && position == 0) {
            return ITEM_TYPE_HEAD;
        } else {
            return ITEM_TYPE_CONTENT;
        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        if (viewType == ITEM_TYPE_HEAD) {
            View view = LayoutInflater.from(context).inflate(layoutHeadID(), parent, false);
            HeadViewHolder viewHolder = new HeadViewHolder(view);
            createHeadHolder(context, viewHolder, beans);
            return viewHolder;
        } else if (viewType == ITEM_TYPE_CONTENT) {
            View view = LayoutInflater.from(context).inflate(layoutContentID(), parent, false);
            ViewHolder viewHolder = new ViewHolder(view);
            createContentHolder(context, viewHolder, beans);
            return viewHolder;
        } else if (viewType == ITEM_TYPE_FOOT) {
            View view = LayoutInflater.from(context).inflate(layoutFootID(), parent, false);
            FootViewHolder viewHolder = new FootViewHolder(view);
            createFootHolder(context, viewHolder, beans);
            return viewHolder;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeadViewHolder) {
            HeadViewHolder viewHolder = (HeadViewHolder) holder;
        } else if (holder instanceof ViewHolder) {
            ViewHolder viewHolder = (ViewHolder) holder;
            if (addHeadViewHolder()) {
                position -= 1;
            }
            T t = beans.get(position);
            String url = urls[position];
            setBindViewHolderData(viewHolder, t, url);
        } else if (holder instanceof FootViewHolder) {
            FootViewHolder viewHolder = (FootViewHolder) holder;
        }
    }

    @Override
    public int getItemCount() {
        if (addHeadViewHolder()) {
            return beans == null ? 0 : beans.size() + ITEM_TYPE_HEAD_COUNT;
        } else if (addFootViewHolder()) {
            return beans == null ? 0 : beans.size() + ITEM_TYPE_FOOT_COUNT;
        } else if (addHeadAndFootViewHolder()) {
            return beans == null ? 0 : beans.size() + ITEM_TYPE_HEAD_COUNT + ITEM_TYPE_FOOT_COUNT;
        } else {
            return beans == null ? 0 : beans.size();
        }
    }

    /**
     * 实现Headers接口，重写getHeaders方法
     */
    protected Headers header = new Headers() {
        @Override
        public Map<String, String> getHeaders() {
            Map<String, String> header = new HashMap<>();
            header.put("Referer", "http://www.mzitu.com/all/");
            return header;
        }
    };
}