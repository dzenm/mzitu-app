package com.din.mzitu.base;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;

import com.bumptech.glide.load.model.Headers;
import com.din.mzitu.R;
import com.din.mzitu.adapter.FootViewHolder;
import com.din.mzitu.adapter.HeadViewHolder;
import com.din.mzitu.adapter.ViewHolder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int ITEM_TYPE_HEAD_COUNT = 1;
    public static final int ITEM_TYPE_FOOT_COUNT = 1;
    public static final int ITEM_TYPE_HEAD = 11;
    public static final int ITEM_TYPE_CONTENT = 12;
    public static final int ITEM_TYPE_FOOT = 13;

    public final static int TYPE_MZITU = 21;
    public final static int TYPE_LIGUI = 22;

    public final static int LOADING_STATE_RUNNING = 31;
    public final static int LOADING_STATE_MORE = 32;
    public final static int LOADING_STATE_FINISH = 33;

    public static final String HEADER_MZITU = "http://www.mzitu.com/all/";
    public static final String HEADER_LIGUI = "http://www.ligui.org/";

    protected List<T> beans;
    protected OnItemClickListener onItemClickListener;
    protected Fragment fragment;
    protected int loadingStatus = 0;
    private int notifyStart = 0;

    public BaseAdapter(Fragment fragment) {
        this.fragment = fragment;
    }

    public void setNotifyStart(int notifyStart) {
        this.notifyStart = notifyStart;
    }

    public void addBeanData(List<T> listBean) {
        beans = listBean;
        notifyItemRangeChanged(notifyStart, beans.size() - notifyStart);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setLoadingStatus(int loadingStatus) {
        this.loadingStatus = loadingStatus;

    }

    protected abstract int layoutContentID();

    protected abstract void setBindViewHolderData(ViewHolder viewHolder, int position);

    protected int layoutFootID() {
        return R.layout.rv_foot;
    }

    protected int layoutHeadID() {
        return R.layout.rv_head;
    }

    /**
     * 根据个数判断Item的类型
     *
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        if (ITEM_TYPE_HEAD_COUNT != 0 && position == 0) {
            return ITEM_TYPE_HEAD;
        } else if (ITEM_TYPE_FOOT_COUNT != 0 && position == ITEM_TYPE_HEAD_COUNT + beans.size()) {
            return ITEM_TYPE_FOOT;
        } else {
            return ITEM_TYPE_CONTENT;
        }
    }

    /**
     * 创建ViewHolder
     *
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        if (viewType == ITEM_TYPE_HEAD) {
            View view = LayoutInflater.from(context).inflate(layoutHeadID(), parent, false);
            HeadViewHolder viewHolder = new HeadViewHolder(view);
            return viewHolder;
        } else if (viewType == ITEM_TYPE_CONTENT) {
            View view = LayoutInflater.from(context).inflate(layoutContentID(), parent, false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        } else if (viewType == ITEM_TYPE_FOOT) {
            View view = LayoutInflater.from(context).inflate(layoutFootID(), parent, false);
            FootViewHolder viewHolder = new FootViewHolder(view);
            return viewHolder;
        }
        return null;
    }

    /**
     * 绑定ViewHolder
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof HeadViewHolder) {
            HeadViewHolder viewHolder = (HeadViewHolder) holder;
        } else if (holder instanceof ViewHolder) {
            final ViewHolder viewHolder = (ViewHolder) holder;
            setBindViewHolderData(viewHolder, position - ITEM_TYPE_HEAD_COUNT);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null && position > 0) {
                        onItemClickListener.onItemClick(viewHolder, position);
                    }
                }
            });
            // item进入动画
            Context context = holder.itemView.getContext();
            LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down);
            ((ViewGroup) holder.itemView).setLayoutAnimation(controller);
        } else if (holder instanceof FootViewHolder) {
            FootViewHolder viewHolder = (FootViewHolder) holder;
            TextView tipText = viewHolder.get(R.id.tipText);
            tipText.setText(loadingStatus == LOADING_STATE_RUNNING ? R.string.loading_status_running :
                    loadingStatus == LOADING_STATE_FINISH ? R.string.loading_status_finish : R.string.loading_status_more);
        }
    }

    @Override
    public int getItemCount() {
        return beans == null ? 0 : beans.size() + ITEM_TYPE_HEAD_COUNT + ITEM_TYPE_FOOT_COUNT;
    }

    /**
     * 实现Headers接口，重写getHeaders方法
     */
    protected Headers header(final String baseUrl) {
        return new Headers() {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> header = new HashMap<>();
                header.put("Referer", baseUrl);
                return header;
            }
        };
    }

    /**
     * 点击事件
     */
    public interface OnItemClickListener {

        void onItemClick(ViewHolder viewHolder, int position);
    }
}