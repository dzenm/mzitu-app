package com.din.mzitu.utill;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class FileUtil {
    /*
     *  获取外部存储状态
     */
    private static boolean externalStatus() {
        String state = Environment.getExternalStorageState();
        //  如果状态不是mounted，无法读写
        if (!state.equals(Environment.MEDIA_MOUNTED)) {
            return false;
        }
        return true;
    }

    /**
     * 创建文件夹
     *
     * @param folderName
     * @return
     */
    public static String getDirect(String... folderName) {
        StringBuffer path = new StringBuffer();
        for (String s : folderName) {
            path.append(s + "/");
        }
        File file = new File(Environment.getExternalStorageDirectory() + File.separator + path);
        //  判断外部存储状态
        if (externalStatus()) {
            if (!file.exists()) {   //  如果该文件夹不存在，则进行创建
                file.mkdirs();      //  创建文件夹
            }
        }
        return file.getAbsolutePath();
    }

    /**
     * 存储图片到SD卡根目录下
     *
     * @param bitmap
     */
    public static boolean savePhoto(Bitmap bitmap, String direct, String photoName) {
        String path = getDirect("SexyPicture", direct);
        File file = new File(path, photoName);
        OutputStream outputStream = null;
        try {
            if (file.isDirectory()) {
                return false;
            }
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            if (outputStream == null) {
                outputStream = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                outputStream.flush();
                outputStream.close();
                return true;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 读取图片
     *
     * @param direct
     * @param photoName
     * @return Bitmap
     */
    public static Bitmap readPhoto(String direct, String photoName) {
        String path = getDirect(direct) + File.separator + photoName;
        if (path != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            return bitmap;
        }
        return null;
    }
}