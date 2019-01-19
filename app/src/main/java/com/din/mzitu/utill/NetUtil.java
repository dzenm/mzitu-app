package com.din.mzitu.utill;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetUtil {

    public static final int NETWORK_NONE = 1;       // 没有网络连接

    public static final int NETWORK_MOBILE = 2;     // 移动网络连接

    public static final int NETWORK_WIFI = 3;       // Wi-Fi连接

    public static boolean isNetAvailable(Context context) {
        ConnectivityManager manager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager == null) {
            return false;
        }

        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo != null && !networkInfo.isAvailable()) {
            return false;
        }
        return true;
    }

    /**
     * 获取网络连接的类型
     *
     * @param context
     * @return
     */
    public static int getNetworkType(Context context) {
        ConnectivityManager connectManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE); // 得到连接管理器对象

        NetworkInfo isConnectNetworkInfo = connectManager.getActiveNetworkInfo();

        if (isConnectNetworkInfo != null && isConnectNetworkInfo.isConnected() && isConnectNetworkInfo.isAvailable()) {
            if (isConnectNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                return NETWORK_WIFI;
            } else if (isConnectNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                isConnectNetworkInfo.isAvailable();
                return NETWORK_MOBILE;
            }
        }
        return NETWORK_NONE;
    }


    public interface INetListener {
        void isAvailable(int type);
    }
}
