package com.din.mzitu.ui.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.din.mzitu.R;
import com.din.mzitu.ui.fragments.mzitu.FragmentPostSingle;

public class PostSingleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_post);
        Toolbar toolbar = findViewById(R.id.toolbar);

        String title = getIntent().getStringExtra(FragmentPostSingle.POST_TITLE);      // 获取上个页面传过来的数据

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(title);        // 设置标题
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        FragmentPostSingle fragmentSelf = FragmentPostSingle.newInstance(title, 1);
        transaction.add(R.id.framelayout, fragmentSelf);
        transaction.show(fragmentSelf).commit();        // 切换Fragment
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }
}