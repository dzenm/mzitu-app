package com.din.mzitu.utill;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import java.util.List;

public class FragmentUtil {

    private AppCompatActivity activity;
    private List<Fragment> list;
    private int layoutID;

    public FragmentUtil(AppCompatActivity activity, int layoutID, List<Fragment> list) {
        this.activity = activity;
        this.layoutID = layoutID;
        this.list = list;
    }

    public FragmentUtil hideFragment() {
        FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).isVisible()) {
                transaction.hide(list.get(i));
            }
        }
        transaction.commitAllowingStateLoss();
        return this;
    }

    public FragmentUtil showFragment(Fragment fragment) {
        FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
        if (fragment.isAdded()) {
            transaction.show(fragment);
        } else {
            transaction.add(layoutID, fragment);
            list.add(fragment);
        }
        transaction.commitAllowingStateLoss();
        return this;
    }
}