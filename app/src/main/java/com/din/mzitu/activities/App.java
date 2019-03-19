package com.din.mzitu.activities;

import android.app.Application;

import com.din.mzitu.basehelper.CrashHandler;

/**
 * @author: dinzhenyan
 * @time: 2018/12/22 下午5:23
 */
public class App extends Application {

    private static App app;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        CrashHandler.getInstance().init(this);
    }

    public static App getApp() {
        return app;
    }
}