package com.din.mzitu.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.din.mzitu.base.BaseActivity;

/**
 * @author: dinzhenyan
 * @time: 2018/12/28 下午2:53
 */
public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        finish();
    }
}
