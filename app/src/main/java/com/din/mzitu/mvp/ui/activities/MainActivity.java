package com.din.mzitu.mvp.ui.activities;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.Toast;

import com.din.mzitu.R;
import com.din.mzitu.mvp.ui.fragments.FragmentLiGui;
import com.din.mzitu.mvp.ui.fragments.FragmentMzitu;
import com.din.mzitu.mvp.ui.fragments.FragmentSelf;
import com.din.mzitu.utills.FragmentUtil;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private FragmentMzitu fragmentMzitu;
    private FragmentLiGui fragmentLiGui;
    private FragmentSelf fragmentSelf;
    private List<Fragment> list;
    private FragmentUtil fragmentUtil;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }


    private void initView() {
        drawerLayout = findViewById(R.id.drawerlayout);
        navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        fragmentMzitu = new FragmentMzitu();
        fragmentLiGui = new FragmentLiGui();
        fragmentSelf = new FragmentSelf();
        list = new ArrayList<>();           // Fragment的add与切换
        fragmentUtil = new FragmentUtil(this, R.id.framelayout, list);
        fragmentUtil.showFragment(fragmentMzitu);
        navigationView.getMenu().getItem(0).setChecked(true);   // 选中的侧滑item着色
    }

    private long currentTime = 0;

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
            drawerLayout.closeDrawers();    // 侧滑打开时，点击返回关闭侧滑
        } else {    // 侧滑关闭时，点击两次返回退出
            long nowTime = System.currentTimeMillis();
            if (nowTime - currentTime >= 2000) {
                currentTime = nowTime;
                Toast.makeText(this, "再次点击返回退出程序", Toast.LENGTH_SHORT).show();
            } else {
                System.exit(0);
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull final MenuItem item) {
        if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
            drawerLayout.closeDrawers();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    switch (item.getItemId()) {
                        case R.id.mzitu:
                            fragmentUtil.hideFragment().showFragment(fragmentMzitu);
                            navigationView.getMenu().getItem(0).setChecked(true);
                            break;
                        case R.id.ligui:
                            fragmentUtil.hideFragment().showFragment(fragmentLiGui);
                            navigationView.getMenu().getItem(1).setChecked(true);
                            break;
                        case R.id.xxoo:
                            fragmentUtil.hideFragment().showFragment(fragmentSelf);
                            navigationView.getMenu().getItem(2).setChecked(true);
                            break;
                        default:
                            break;
                    }
                }
            }, 300);
        }
        return false;
    }
}