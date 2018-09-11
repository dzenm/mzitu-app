package com.din.mzitu.utills;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;

import java.util.List;

public class FragmentUtil {

    private Activity activity;
    private List<Fragment> list;
    private int layoutID;

    public FragmentUtil(Activity activity, int layoutID, List<Fragment> list) {
        this.activity = activity;
        this.layoutID = layoutID;
        this.list = list;
    }

    public FragmentUtil hideFragment() {
        FragmentTransaction transaction = activity.getFragmentManager().beginTransaction();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).isVisible()) {
                transaction.hide(list.get(i));
            }
        }
        transaction.commitAllowingStateLoss();
        return this;
    }

    public FragmentUtil showFragment(Fragment fragment) {
        FragmentTransaction transaction = activity.getFragmentManager().beginTransaction();
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