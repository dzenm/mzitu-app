package com.din.mzitu.basehelper;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import java.util.ArrayList;
import java.util.List;

public class PermitManager {

    public static final int requestCode = 1;

    /*
     * 每次打开软件对未授予的权限只提示一次请求权限，不管授予权限还是未授予权限，onPermissionCallback都会返回true
     */
    public static final int ONCE_REQUEST = 1;

    /*
     * 对请求的权限一定要授予权限才可以使用，否则重复请求
     */
    public static final int REPEAT_REQUEST = 2;

    /*
     * 每次打开软件对未授予的权限提示一次请求权限，如果未授予则会提示手动打开。
     */
    public static final int ONCE_AND_MANUAL_REQUEST = 3;

    private int requestMode = ONCE_AND_MANUAL_REQUEST;          // 请求权限的模式
    private Activity activity;
    private String[] permissions;                               // 需要请求的权限
    private OnRequestPermissionListener onRequestPermissionListener;

    private PermitManager() {
    }

    private static class Instance {
        private static final PermitManager INSTANCE = new PermitManager();
    }

    public static PermitManager getInstance() {
        return Instance.INSTANCE;
    }

    /**
     * 设置activity
     *
     * @param activity
     * @return
     */
    public PermitManager with(Activity activity) {
        this.activity = activity;
        return this;
    }

    /**
     * 请求权限模式
     *
     * @param requestMode
     * @return
     */
    public PermitManager mode(int requestMode) {
        this.requestMode = requestMode;
        return this;
    }

    /**
     * 请求的权限列表
     *
     * @param permissions
     * @return
     */
    public PermitManager load(String[] permissions) {
        this.permissions = onFilterPermission(permissions);                  // 过滤请求
        return this;
    }

    /**
     * 权限请求回掉处理
     *
     * @param onRequestPermissionListener
     * @return
     */
    public PermitManager into(OnRequestPermissionListener onRequestPermissionListener) {
        this.onRequestPermissionListener = onRequestPermissionListener;
        return this;
    }

    /**
     * 请求权限
     *
     * @return
     */
    public PermitManager requestPermission() {
        if (!isMarshmallow()) {
            return this;
        }
        if (permissions.length > 0) {                                        // 处理请求的结果
            ActivityCompat.requestPermissions(activity, permissions, requestCode);
        } else {
            requestSuccess();
        }
        return this;
    }

    /**
     * 权限处理结果（重写onRequestPermissionsResult方法，并调用该方法）
     *
     * @param permissions
     * @param grantResults
     */
    public PermitManager requestPermissionResult(int requestCode, String[] permissions, int[] grantResults) {
        if (PermitManager.requestCode != requestCode) {
            return this;
        }
        String[] grantPermissions = onFilterPermission(permissions);   // 第一次请求的处理结果，过滤已授予的权限
        boolean isAllGrant = true;
        for (int i = 0; i < grantResults.length; i++) {                // 是否所有的权限都被授予
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                isAllGrant = false;
                break;
            }
        }

        if (grantPermissions.length == 0 && isAllGrant) {               // 是否还存在未授予的权限
            requestSuccess();
        } else if (requestMode == ONCE_REQUEST) {                       // 是否对于未授予的权限重复请求
            openFaildDialog("未授予软件运行需要的权限");
        } else if (requestMode == ONCE_AND_MANUAL_REQUEST) {
            openSettingDialog("未授予相应的权限，可能会导致软件崩溃。请前往设置授予相应的权限");
        } else if (requestMode == REPEAT_REQUEST) {
            load(permissions).requestPermission();
        }
        return this;
    }

    /**
     * 动态权限只有在Android 6.0之后才有
     *
     * @return
     */
    public boolean isMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    /**
     * 检查是否授予权限
     *
     * @param permission
     * @return
     */
    public boolean isGrant(String permission) {
        return ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 是否显示解释权限
     *
     * @param permission
     * @return
     */
    public boolean isRationale(String permission) {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission);
    }

    public interface OnRequestPermissionListener {

        void onRequest(boolean isGrant);
    }

    /**
     * 权限请求成功
     */
    private void requestSuccess() {
        onRequestPermissionListener.onRequest(true);
        FileHelper.getInstance().init(activity);                        // 文件夹初始化
        LogHelper.getInstance().start();                                // Logcat初始化
    }

    /**
     * 过滤未授予的权限
     *
     * @param permissions
     * @return
     */
    private String[] onFilterPermission(String[] permissions) {
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
     * 打开设置权限的对话框
     *
     * @param message
     */
    private void openSettingDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("温馨提示");
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onOpenSettingIntent();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onRequestPermissionListener.onRequest(false);
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    /**
     * 打开未授予权限的对话框
     *
     * @param message
     */
    private void openFaildDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("温馨提示");
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                requestSuccess();
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    /**
     * 打开软件权限设置界面
     */
    private void onOpenSettingIntent() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
        intent.setData(uri);
        activity.startActivityForResult(intent, requestCode);
    }
}
