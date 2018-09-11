package com.din.mzitu.mvp.ui.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.din.mzitu.R;
import com.din.mzitu.utills.FileUtils;

public class PictureActivity extends AppCompatActivity {

    public static final String PICTURE_TITLE = "picture_title";
    public static final String PICTURE_IMAGE = "picture_image";
    public static final String PICTURE_POSITION = "picture_position";
    private ImageView imageView;
    private String title;
    private Bitmap bitmap;
    private int position;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);

        verifyPermissions(this);
        Intent intent = getIntent();
        title = intent.getStringExtra(PICTURE_TITLE);
        final String image = intent.getStringExtra(PICTURE_IMAGE);
        position = intent.getIntExtra(PICTURE_POSITION, 0);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        imageView = findViewById(R.id.image);
        getSupportActionBar().setTitle(title);

        Glide.with(this).asBitmap().load(image).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                bitmap = resource;
                imageView.setImageBitmap(bitmap);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
    }

    // 动态存储权限申请
    private static final String[] PERMISSIONS_DANGEROUS = new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"};

    public static void verifyPermissions(Activity activity) {
        try {
            //  检测是否有指定的权限
            int permission = ActivityCompat.checkSelfPermission(
                    activity, PERMISSIONS_DANGEROUS[0]);
            //  如果包管理没有该权限,动态的申请权限
            if (permission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, PERMISSIONS_DANGEROUS, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.use_picture, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.save:
                if (bitmap != null) {
                    boolean saveSuccess = FileUtils.savePhoto(bitmap, title, String.valueOf(position) + ".jpg");
                    if (saveSuccess) {
                        Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "保存失败", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.set_desktop:
                break;
            default:
                break;
        }
        return true;
    }
}