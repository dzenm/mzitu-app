package com.din.mzitu.ui.activities;

import android.app.Application;
import android.content.Context;

import com.din.mzitu.utill.CrashHandler;

/**
 * @author: dinzhenyan
 * @time: 2018/12/22 下午5:23
 */
public class App extends Application {

    private static App app;

    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler.getInstance().init(this);
        app = this;
    }

    public static App getApp() {
        return app;
    }
}
