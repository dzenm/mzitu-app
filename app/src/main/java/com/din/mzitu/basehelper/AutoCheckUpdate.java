package com.din.mzitu.basehelper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.app.AlertDialog;

public class AutoCheckUpdate {

    private static Activity sActivity;
    private static final String HEAD = "file://";
    private static final String TYPE = "application/vnd.android.package-archive";

    private AutoCheckUpdate() {
    }

    private static class Instance {
        private static final AutoCheckUpdate INSTANCE = new AutoCheckUpdate();
    }

    public static AutoCheckUpdate getInstance() {
        return Instance.INSTANCE;
    }

    public static AutoCheckUpdate with(Activity activity) {
        sActivity = activity;
        return Instance.INSTANCE;
    }

    /**
     * 获取版本号
     *
     * @return
     */
    public int getVersionCode() {
        int version = 0;
        try {
            PackageInfo info = sActivity.getPackageManager().getPackageInfo(sActivity.getPackageName(), 0);
            version = info.versionCode;
            return version;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }

    /**
     * 显示更新dialog
     *
     * @return
     */
    public AutoCheckUpdate showUpdateDialog(final String downloadUrl) {
        AlertDialog.Builder builder = new AlertDialog.Builder(sActivity);
        builder.setTitle("版本升级")
                .setMessage("发现新版本！请及时更新")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startUpdate(downloadUrl);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
        return Instance.INSTANCE;
    }

    /**
     * 更新Dialog
     *
     * @param downloadUrl
     * @return
     */
    public AutoCheckUpdate startUpdate(String downloadUrl) {
        ProgressDialog progressDialog = new ProgressDialog(sActivity);
        progressDialog.setMessage("正在下载新版本...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(false);        //不能手动取消下载进度对话框

        return Instance.INSTANCE;
    }

    /**
     * 下载完成安装apk，给系统发送一个intent
     *
     * @return
     */
    public AutoCheckUpdate openApk() {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(HEAD + getFileSavePath()), TYPE);
        sActivity.startActivity(intent);
        return Instance.INSTANCE;
    }

    /**
     * 获取apk文件存储路径
     * @return
     */
    private String getFileSavePath() {
        return FileHelper.getInstance().getFolders("apk");
    }
}
