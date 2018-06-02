package com.din.mzitudemo.activities;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.din.mzitudemo.R;
import com.din.mzitudemo.fragments.FragmentContent;

public class ContentActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        Toolbar toolbar = findViewById(R.id.toolbar);

        Intent intent = getIntent();
        String title = intent.getStringExtra("TITLE");      // 获取上个页面传过来的数据

        toolbar.setTitle(title);        // 设置标题

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        FragmentContent fragmentContent = new FragmentContent();
        transaction.add(R.id.framelayout, fragmentContent);
        transaction.show(fragmentContent).commit();
    }
}