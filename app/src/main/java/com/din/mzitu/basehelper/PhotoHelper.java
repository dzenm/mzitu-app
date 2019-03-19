package com.din.mzitu.basehelper;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PhotoHelper {

    public static final int NONE = 0;                                       //  随意图片类型
    public static final int PHOTOGRAPH = 1;                                 //  拍照
    public static final int PHOTOZOOM = 2;                                  //  缩放
    public static final int PHOTORESOULT = 3;                               //  结果
    public static final String IMAGE_UNSPECIFIED = "image/*";
    public static final int PICTURE_HEIGHT = 500;
    public static final int PICTURE_WIDTH = 500;
    public static String imageName;

    /**
     * 从系统相册中选取照片上传
     *
     * @param activity
     */
    public static void selectPictureFromAlbum(Activity activity) {
        // 调用系统的相册
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_UNSPECIFIED);
        // 调用剪切功能
        activity.startActivityForResult(intent, PHOTOZOOM);
    }

    /**
     * 从系统相册中选取照片上传
     *
     * @param fragment
     */
    public static void selectPictureFromAlbum(Fragment fragment) {
        // 调用系统的相册
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_UNSPECIFIED);
        // 调用剪切功能
        fragment.startActivityForResult(intent, PHOTOZOOM);
    }

    /**
     * 拍照
     *
     * @param activity
     */
    public static void photograph(Activity activity) {
        imageName = File.separator + getStringToday() + ".jpg";
        // 调用系统的拍照功能
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.getExternalStorageDirectory(), imageName)));
        } else {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(activity.getFilesDir(), imageName)));
        }
        activity.startActivityForResult(intent, PHOTOGRAPH);
    }

    /**
     * 拍照
     *
     * @param fragment
     */
    public static void photograph(Fragment fragment) {
        imageName = "/" + getStringToday() + ".jpg";
        // 调用系统的拍照功能
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.getExternalStorageDirectory(), imageName)));
        } else {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(fragment.getActivity().getFilesDir(), imageName)));
        }
        fragment.startActivityForResult(intent, PHOTOGRAPH);
    }

    /**
     * 图片裁剪
     *
     * @param activity
     * @param uri
     */
    public static void startPhotoZoom(Activity activity, Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, IMAGE_UNSPECIFIED);
        intent.putExtra("crop", "true");
        //  aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        //  outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("noFaceDetection", true); //关闭人脸检測
        intent.putExtra("return-data", true);//假设设为true则返回bitmap
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);//输出文件
        activity.startActivityForResult(intent, PHOTORESOULT);
    }


    /**
     * 图片裁剪
     *
     * @param activity
     * @param uri      原图的地址
     * @param height   指定的剪辑图片的高
     * @param width    指定的剪辑图片的宽
     * @param destUri  剪辑后的图片存放地址
     */
    public static void startPhotoZoom(Activity activity, Uri uri, int height, int width, Uri destUri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, IMAGE_UNSPECIFIED);
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", width);
        intent.putExtra("outputY", height);
        intent.putExtra("noFaceDetection", true); //关闭人脸检測
        intent.putExtra("return-data", false);//假设设为true则返回bitmap
        intent.putExtra(MediaStore.EXTRA_OUTPUT, destUri);//输出文件

        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        activity.startActivityForResult(intent, PHOTORESOULT);
    }


    /**
     * 图片裁剪
     *
     * @param activity
     * @param uri      原图的地址
     * @param height   指定的剪辑图片的高
     * @param width    指定的剪辑图片的宽
     * @param destUri  剪辑后的图片存放地址
     */
    public static void startPhotoZooms(Activity activity, Uri uri, int width, int height, Uri destUri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, IMAGE_UNSPECIFIED);
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", width);
        intent.putExtra("aspectY", height);
        intent.putExtra("noFaceDetection", true); //关闭人脸检測
        intent.putExtra("return-data", false);//假设设为true则返回bitmap
        intent.putExtra(MediaStore.EXTRA_OUTPUT, destUri);//输出文件
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        activity.startActivityForResult(intent, PHOTORESOULT);
    }

    /**
     * 图片裁剪
     *
     * @param fragment
     * @param uri
     * @param height
     * @param width
     */
    public static void startPhotoZoom(Fragment fragment, Uri uri, int height, int width) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, IMAGE_UNSPECIFIED);
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", height);
        intent.putExtra("outputY", width);
        intent.putExtra("return-data", true);
        fragment.startActivityForResult(intent, PHOTORESOULT);
    }

    /**
     * 将图片路径转化为Uri
     *
     * @param path
     * @return
     */
    public static Uri getPathToUri(String path) {
//        File tempDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File picture = new File(path, "temp");
        Uri uri = null;
        if (picture.exists()) {
            uri = Uri.fromFile(picture);
        }
        return uri;
    }

    /**
     * 获取当前系统时间并格式化
     *
     * @return
     */
    public static String getStringToday() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    /**
     * 制作图片的路径地址
     *
     * @param context
     * @return
     */
    public static String getPath(Context context) {
        String path = null;
        File file = null;
        long tag = System.currentTimeMillis();
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            //SDCard是否可用
            path = Environment.getExternalStorageDirectory() + File.separator + "myimages/";
            file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
            path = Environment.getExternalStorageDirectory() + File.separator + "myimages/" + tag + ".png";
        } else {
            path = context.getFilesDir() + File.separator + "myimages/";
            file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
            path = context.getFilesDir() + File.separator + "myimages/" + tag + ".png";
        }
        return path;
    }

    /**
     * 按比例获取bitmap
     *
     * @param path
     * @param w
     * @param h
     * @return
     */
    public static Bitmap convertToBitmap(String path, int w, int h) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        // 设置为ture仅仅获取图片大小
        opts.inJustDecodeBounds = true;
        opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
        BitmapFactory.decodeFile(path, opts);
        int width = opts.outWidth;
        int height = opts.outHeight;
        float scaleWidth = 0.f, scaleHeight = 0.f;
        if (width > w || height > h) {
            // 缩放
            scaleWidth = ((float) width) / w;
            scaleHeight = ((float) height) / h;
        }
        opts.inJustDecodeBounds = false;
        float scale = Math.max(scaleWidth, scaleHeight);
        opts.inSampleSize = (int) scale;
        WeakReference<Bitmap> weak = new WeakReference<Bitmap>(BitmapFactory.decodeFile(path, opts));
        return Bitmap.createScaledBitmap(weak.get(), w, h, true);
    }

    /**
     * 获取原图bitmap
     *
     * @param path
     * @return
     */
    public static Bitmap convertToBitmap2(String path) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        // 设置为ture仅仅获取图片大小
        opts.inJustDecodeBounds = true;
        opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
        // 返回为空
        BitmapFactory.decodeFile(path, opts);
        return BitmapFactory.decodeFile(path, opts);
    }


    /**
     * 通过Uri删除图片
     *
     * @param activity
     * @param uri
     */
    public static void deleteUriBitmap(Activity activity, Uri uri) {
        if (uri.toString().startsWith("content://")) {
            // content://开头的Uri
            activity.getContentResolver().delete(uri, null, null);
        } else {
            File file = new File(getRealFilePath(activity, uri));
            if (file.exists() && file.isFile()) {
                file.delete();
            }
        }
    }

    /**
     * 通过Uri获取图片真实路径
     *
     * @param context
     * @param uri
     * @return
     */
    public static String getRealFilePath(final Context context, final Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null) {
            data = uri.getPath();
            if (ContentResolver.SCHEME_FILE.equals(scheme)) {
                data = uri.getPath();
            } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
                Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
                if (null != cursor) {
                    if (cursor.moveToFirst()) {
                        int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                        if (index > -1) {
                            data = cursor.getString(index);
                        }
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    /**
     * 裁剪图片为缩略图
     *
     * @param activity
     * @param imageUri
     * @return
     */
    public static Bitmap convertToBitmap(Activity activity, Uri imageUri) {

        String[] filePathColumns = {MediaStore.Images.Media.DATA};
        Cursor cursor = activity.getContentResolver().query(imageUri, filePathColumns, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumns[0]);
        String imagePath = cursor.getString(columnIndex);
        cursor.close();
        //  设置参数
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // 只获取图片的大小信息，而不是将整张图片载入在内存中，避免内存溢出
        BitmapFactory.decodeFile(imagePath, options);
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 2; // 默认像素压缩比例，压缩为原图的1/2
        int minLen = Math.min(height, width); // 原图的最小边长
        if (minLen > 100) { // 如果原始图像的最小边长大于100dp（此处单位我认为是dp，而非px）
            float ratio = (float) minLen / 100.0f; // 计算像素压缩比例
            inSampleSize = (int) ratio;
        }
        options.inJustDecodeBounds = false; // 计算好压缩比例后，这次可以去加载原图了
        options.inSampleSize = inSampleSize; // 设置为刚才计算的压缩比例
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options); // 解码文件
        return bitmap;
    }
}