package com.din.mzitu.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class NetReceiver extends BroadcastReceiver {

    private OnNetworkListener onNetworkListener;

    public NetReceiver setOnNetworkListener(OnNetworkListener onNetworkListener) {
        this.onNetworkListener = onNetworkListener;
        return this;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean isConnect = isConnectNet(context);
        onNetworkListener.onNetwork(isConnect);
        if (isConnect) {

        } else {
            Toast.makeText(context, "网络连接失败,请稍后重试", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 判断是否连接到网络
     *
     * @param context
     * @return
     */
    private boolean isConnectNet(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info != null && info.isAvailable()) {
            return true;
        }
        return false;
    }

    public interface OnNetworkListener {

        void onNetwork(boolean isConnect);
    }
}
