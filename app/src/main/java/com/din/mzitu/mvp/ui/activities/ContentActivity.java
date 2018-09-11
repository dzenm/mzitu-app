package com.din.mzitu.mvp.ui.activities;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.din.mzitu.R;
import com.din.mzitu.mvp.ui.fragments.mzitus.FragmentContent;

public class ContentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        Toolbar toolbar = findViewById(R.id.toolbar);

        Intent intent = getIntent();
        String title = intent.getStringExtra(FragmentContent.CONTENT_TITLE);      // 获取上个页面传过来的数据

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(title);        // 设置标题
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        FragmentContent fragmentSelf = FragmentContent.newInstance(title, 1);   
        transaction.add(R.id.framelayout, fragmentSelf);
        transaction.show(fragmentSelf).commit();        // 切换Fragment
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return true;
    }
}