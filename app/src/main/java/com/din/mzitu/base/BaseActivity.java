package com.din.mzitu.base;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.din.mzitu.broadcast.NetReceiver;

public class BaseActivity extends AppCompatActivity implements NetReceiver.OnNetworkListener {

    private NetReceiver receiver;
    private IntentFilter filter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (receiver == null) {
            receiver = new NetReceiver();
            receiver.setOnNetworkListener(this);
        }
        if (filter == null) {
            filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        }
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    @Override
    public void onNetwork(boolean isConnect) {

    }
}
