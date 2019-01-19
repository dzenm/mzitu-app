package com.din.mzitu.base;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.din.mzitu.activities.App;
import com.din.mzitu.utill.NetUtil;

import java.util.ArrayList;
import java.util.List;

public class BaseActivity extends AppCompatActivity implements NetUtil.INetListener {

    protected static final int requestCode = 1;
    protected OnPermissionCallback onPermissionCallback;
    public static NetUtil.INetListener iNetListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        iNetListener = this;
    }

    @Override
    public void isAvailable(int type) {
        if (type != NetUtil.NETWORK_NONE) {
        } else {
            Toast.makeText(this, "无网络连接", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 请求权限
     *
     * @param permission
     */
    protected void requestPermission(String permission, OnPermissionCallback onPermissionCallback) {
        if (!isMarshmallow()) {
            return;
        }
        this.onPermissionCallback = onPermissionCallback;
        if (isGrant(permission)) {
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
        } else {
            onPermissionCallback.onSuccess();
        }
    }

    /**
     * 请求权限
     *
     * @param permissions
     */
    protected void requestPermission(String[] permissions, OnPermissionCallback onPermissionCallback) {
        if (!isMarshmallow()) {
            return;
        }
        this.onPermissionCallback = onPermissionCallback;
        String[] temp = filterPermission(permissions);
        if (temp.length > 0) {
            ActivityCompat.requestPermissions(this, permissions, requestCode);
        } else {
            onPermissionCallback.onSuccess();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == BaseActivity.requestCode) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onPermissionCallback.onSuccess();
            } else {
                openSetting("未授予相应的权限，可能会导致软件崩溃。请前往设置授予相应的权限");
            }
            return;
        }
    }

    /**
     * 打开权限设置界面
     *
     * @param message
     */
    protected void openSetting(String message) {
        new AlertDialog.Builder(this)
                .setTitle("温馨提示")
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 打开软件设置界面
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, requestCode);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onPermissionCallback.onFailed();
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

    /**
     * 过滤未授予的权限
     *
     * @param permissions
     * @return
     */
    protected String[] filterPermission(String[] permissions) {
        List<String> filterPermits = new ArrayList<>();
        for (int i = 0; i < permissions.length; i++) {
            if (isGrant(permissions[i])) {
                filterPermits.add(permissions[i]);
            }
        }
        String[] temps = new String[filterPermits.size()];
        return filterPermits.toArray(temps);
    }

    /**
     * 动态权限只有在Android 6.0之后才有
     *
     * @return
     */
    protected boolean isMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    /**
     * 检查是否授予权限
     *
     * @param permission
     * @return
     */
    protected boolean isGrant(String permission) {
        return ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 是否显示解释权限
     *
     * @param permission
     * @return
     */
    protected boolean isRationale(String permission) {
        return ActivityCompat.shouldShowRequestPermissionRationale(this, permission);
    }

    /**
     * 获取App名称
     *
     * @return
     */
    protected String getAppName() {
        String appName = null;
        try {
            String packageName = App.getApp().getPackageName();
            PackageInfo packageInfo = getPackageManager().getPackageInfo(packageName, 0);
            appName = packageInfo.applicationInfo.loadLabel(getPackageManager()).toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return appName;
    }

    public interface OnPermissionCallback {

        void onSuccess();

        void onFailed();
    }
}
