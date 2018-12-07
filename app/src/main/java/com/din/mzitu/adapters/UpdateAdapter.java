package com.din.mzitu.adapters;

import android.app.Activity;
import android.widget.TextView;

import com.din.mzitu.R;
import com.din.mzitu.base.BaseAdapter;
import com.din.mzitu.bean.UpdateBean;

public class UpdateAdapter extends BaseAdapter {

    public UpdateAdapter(Activity activity) {
        super(activity);
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