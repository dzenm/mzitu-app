package com.din.mzitu.utill;

import android.app.Activity;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;

import com.din.mzitu.R;

/**
 * @author: dinzhenyan
 * @time: 2018/12/15 上午10:52
 */
public class ScreenHelper {

    public static void addDrawerLayoutToggle(Activity activity, Toolbar toolbar) {
        DrawerLayout drawerLayout = activity.findViewById(R.id.drawerlayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(activity,
                drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }
}
