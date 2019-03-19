package com.din.mzitu.activities;

import android.annotation.SuppressLint;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.din.mzitu.R;
import com.din.mzitu.base.BaseActivity;
import com.din.mzitu.utill.ColorUtil;
import com.din.mzitu.basehelper.FileHelper;
import com.din.mzitu.utill.GlideApp;

import java.io.IOException;

public class PicSingleActivity extends BaseActivity {

    public static final String PICTURE_TITLE = "picture_title";
    public static final String PICTURE_IMAGE = "picture_image";
    public static final String PICTURE_POSITION = "picture_position";
    private ImageView imageView;
    private String title;
    private Bitmap bitmap;
    private Toolbar toolbar;
    private int position;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_picture);

        // 获取标题
        Intent intent = getIntent();
        title = intent.getStringExtra(PICTURE_TITLE);
        // 获取图片
        String image = intent.getStringExtra(PICTURE_IMAGE);
        position = intent.getIntExtra(PICTURE_POSITION, 0);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(title);

        imageView = findViewById(R.id.image);

        // 加载图片
        GlideApp.with(this)
                .asBitmap()
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder_error)
                .load(image)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        bitmap = resource;
                        imageView.setImageBitmap(bitmap);
                        paletteColor(bitmap);
                    }
                });
    }

    private void paletteColor(Bitmap bitmap) {
        // 对图片拾色
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(@NonNull Palette palette) {
                Palette.Swatch muted = palette.getLightVibrantSwatch();
                if (muted == null) {
                    for (Palette.Swatch swatch : palette.getSwatches()) {
                        muted = swatch;
                        break;
                    }
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (muted != null) {
                        // 拾色成功，设置颜色
                        int rgb = muted.getRgb();
                        Window window = getWindow();
                        // 状态栏改变颜色
                        int color = ColorUtil.changeColor(rgb);
                        window.setStatusBarColor(color);
                        toolbar.setBackgroundColor(color);
                    }
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.picture_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        } else if (item.getItemId() == R.id.save) {
            if (bitmap != null) {
                boolean saveSuccess = FileHelper.getInstance().savePhoto(bitmap, title, String.valueOf(position + 1) + ".jpg");
                if (saveSuccess) {
                    Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "保存失败", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (item.getItemId() == R.id.set_desktop) {
            setWallpaper();
            Toast.makeText(this, "设置成功", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    public void setWallpaper() {
        if (bitmap != null) {
            try {
                WallpaperManager.getInstance(getApplicationContext()).setBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}