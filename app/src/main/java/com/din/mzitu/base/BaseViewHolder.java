package com.din.mzitu.base;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;

public class BaseViewHolder extends RecyclerView.ViewHolder {
    private View view;

    public BaseViewHolder(View itemView) {
        super(itemView);
        this.view = itemView;
    }

    public <T extends View> T get(int viewID) {
        SparseArray<View> sparseArray = (SparseArray<View>) view.getTag();
        if (sparseArray == null) {
             sparseArray = new SparseArray<View>();
            view.setTag(sparseArray);
        }
        View childView = sparseArray.get(viewID);
        if (childView == null) {
            childView = view.findViewById(viewID);
            sparseArray.put(viewID, childView);
        }
        return (T) childView;
    }
}