package com.din.mzitu.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;

import com.din.mzitu.R;
import com.din.mzitu.base.BaseAdapter;
import com.din.mzitu.bean.UpdateBean;

public class UpdateAdapter extends BaseAdapter {

    public UpdateAdapter(Fragment fragment) {
        super(fragment);
    }

    @Override
    protected int layoutContentID() {
        return R.layout.rv_update;
    }

    @Override
    protected void setBindViewHolderData(ViewHolder viewHolder, int position) {
        TextView title = viewHolder.get(R.id.title);
        UpdateBean updateBean = (UpdateBean) beans.get(position);
        title.setText(updateBean.getTitle());
    }

}