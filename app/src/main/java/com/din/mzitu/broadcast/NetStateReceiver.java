package com.din.mzitu.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import com.din.mzitu.base.BaseActivity;
import com.din.mzitu.utill.NetUtil;

public class NetStateReceiver extends BroadcastReceiver {

    private NetUtil.INetListener iNetListener = BaseActivity.iNetListener;

    @Override
    public void onReceive(Context context, Intent intent) {
        // 如果相等的话就说明网络状态发生了变化
        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            if (iNetListener != null) { // 容错机制
                iNetListener.isAvailable(NetUtil.getNetworkType(context));
            }
        }
    }
}
