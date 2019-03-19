package com.din.mzitu.basehelper;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class FileHelper {

    private static final String TAG = FileHelper.class.getSimpleName();
    private String appFolder;            // app名称目录
    private boolean isInit = false;
    private Context context;

    private FileHelper() {
    }

    private static class Instane {
        private static FileHelper INSTANCE = new FileHelper();
    }

    public static FileHelper getInstance() {
        return Instane.INSTANCE;
    }

    /**
     * @param context
     * @return
     */
    public FileHelper init(Context context) {
        createAppFolder(getAppName(context));     // 创建以软件名为名称的软件的根目录文件夹
        this.context = context;
        return this;
    }

    /**
     * @param appName
     * @return
     */
    public FileHelper init(String appName) {
        createAppFolder(appName);                 // 创建自定义名称的软件的根目录文件夹
        return this;
    }

    /**
     * 创建该软件的根目录文件夹
     *
     * @param appName
     */
    private void createAppFolder(String appName) {
        if (!isInit) {
            isInit = true;
        } else {
            Log.d(TAG, "the app folder is init");
            return;
        }
        if ("".equals(appName) || appName == null) {
            Log.d(TAG, "the app folder is null");
            return;
        }
        File file = new File(Environment.getExternalStorageDirectory() + File.separator + appName);
        if (!isExternal()) {
            Log.d(TAG, "the app folder is not external");
            return;
        }
        if (!file.exists()) {
            file.mkdir();
        }
        this.appFolder = file.getAbsolutePath();
        Log.d(TAG, "init application path: " + appFolder);
    }

    /**
     * 获取应用名称
     *
     * @return
     */
    public String getAppName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
            if (applicationInfo == null) {
                return null;
            }
            if (packageManager == null) {
                return null;
            }
            String appName = packageManager.getApplicationLabel(applicationInfo).toString();
            Log.d(TAG, "init application path: " + appName);
            return appName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 创建文件夹
     *
     * @param folderName
     * @return
     */
    public String getFolders(String... folderName) {
        if (appFolder == null) {
            Log.d(TAG, "the app folder is null");
            return null;
        }
        StringBuffer path = new StringBuffer();
        for (String s : folderName) {
            path.append("/" + s);
        }
        File file = new File(appFolder + path);
        //  判断外部存储状态
        if (!isExternal()) {
            Log.d(TAG, "the external is unuseful");
            return null;
        }
        if (!file.exists()) {   //  如果该文件夹不存在，则进行创建
            file.mkdirs();      //  创建文件夹
            Log.d(TAG, "create new folder's path: " + file.getAbsolutePath());
        }
        if (!file.isDirectory()) {
            Log.d(TAG, "the file is not directory");
            return null;
        }
        String filePath = file.getAbsolutePath();
        Log.d(TAG, "return the folder's path: " + filePath);
        return filePath;
    }

    /**
     * 存储图片
     *
     * @param bitmap
     */
    public boolean savePhoto(Bitmap bitmap, String folder, String photoName) {
        String path = getFolders("cache", folder);
        if (path == null) {
            return false;
        }
        File file = new File(path, photoName);
        OutputStream outputStream = null;
        try {
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
     * @param folder
     * @param photoName
     * @return Bitmap
     */
    public Bitmap readPhoto(String folder, String photoName) {
        String path = getFolders(folder);
        if (path != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(path + File.separator + photoName);
            return bitmap;
        }
        return null;
    }

    /*
     *  获取外部存储状态
     */
    private boolean isExternal() {
        String state = Environment.getExternalStorageState();
        //  如果状态不是mounted，无法读写
        if (!state.equals(Environment.MEDIA_MOUNTED)) {
            return false;
        }
        return true;
    }

    /**
     * 存储List数据
     *
     * @param tArrayList
     */
    public void saveArrayListFile(ArrayList tArrayList, String fileName) {
        FileOutputStream fileOutputStream = null;
        ObjectOutputStream objectOutputStream = null;
        FileInputStream fileInputStream = null;
        try {
            File file = new File(getFolders(fileName));
            fileOutputStream = new FileOutputStream(file.toString());  //新建一个内容为空的文件
            objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(tArrayList);
            objectOutputStream.close();
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取本地的List数据
     *
     * @return
     */
    public ArrayList getArrayListFile(String fileName) {
        ObjectInputStream objectInputStream = null;
        FileInputStream fileInputStream = null;
        ArrayList savedArrayList = new ArrayList<>();
        try {
            File file = new File(getFolders(fileName));
            fileInputStream = new FileInputStream(file);
            objectInputStream = new ObjectInputStream(fileInputStream);
            savedArrayList = (ArrayList) objectInputStream.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return savedArrayList;
    }

    /**
     * 复制文件到SD卡
     *
     * @param databaseName 数据库名称
     */
    public boolean copyDBToSDcard(Activity activity, String databaseName) {
        String oldPath = activity.getDatabasePath(databaseName).getPath();
        File file = new File(getFolders("Databases"));
        if (!file.exists()) {
            file.mkdirs();
        }
        String newPath = file.getAbsolutePath() + File.separator + databaseName;
        Log.d(TAG, newPath);
        return copyDBFile(oldPath, newPath);
    }

    /**
     * 复制文件到SD卡
     *
     * @param databaseName 数据库名称
     */
    public boolean copySDcardToDB(Activity activity, String databaseName) {
        File file = new File(getFolders("Databases"));
        if (!file.exists()) {
            file.mkdirs();
        }
        String oldPath = file.getAbsolutePath() + File.separator + databaseName;
        String newPath = activity.getDatabasePath(databaseName).getPath();
        return copyDBFile(oldPath, newPath);
    }

    /**
     * 复制文件
     *
     * @param oldPath
     * @param newPath
     */
    private boolean copyDBFile(String oldPath, String newPath) {
        try {
            File oldfile = new File(oldPath);
            File newfile = new File(newPath);
            if (!newfile.exists()) {
                newfile.createNewFile();
            }
            if (oldfile.exists()) { // 文件存在时
                InputStream inStream = new FileInputStream(oldPath); // 读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1024];
                int len = 0, byteread = 0;
                while ((byteread = inStream.read(buffer)) != -1) {
                    len += byteread; // 字节数 文件大小
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
                return true;
            }
        } catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();
            return false;
        }
        return false;
    }

    /**
     * 删除文件
     *
     * @param context  程序上下文
     * @param fileName 文件名，要在系统内保持唯一
     * @return boolean 存储成功的标志
     */
    public boolean deleteFile(Context context, String fileName) {
        return context.deleteFile(fileName);
    }

    /**
     * 文件是否存在
     *
     * @param context
     * @param fileName
     * @return
     */
    public boolean exists(Context context, String fileName) {
        return new File(context.getFilesDir(), fileName).exists();
    }

    /**
     * 存储文本数据
     *
     * @param context  程序上下文
     * @param fileName 文件名，要在系统内保持唯一
     * @param content  文本内容
     * @return boolean 存储成功的标志
     */
    public boolean writeFile(Context context, String fileName, String content) {
        FileOutputStream fos = null;
        try {
            fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            byte[] byteContent = content.getBytes();
            fos.write(byteContent);
            fos.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 存储文本数据
     *
     * @param content 文本内容
     * @return boolean 存储成功的标志
     */
    public boolean writeFile(String filePath, String content) throws IOException {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(filePath);
            byte[] byteContent = content.getBytes();
            fos.write(byteContent);
            fos.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 读取文本数据
     *
     * @return String, 读取到的文本内容，失败返回null
     */
    public String readFile(String filePath) {
        if (!new File(filePath).exists()) {
            return null;
        }
        FileInputStream fis = null;
        String content = null;
        try {
            fis = new FileInputStream(filePath);
            if (fis != null) {
                byte[] buffer = new byte[1024];
                ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
                while (true) {
                    int readLength = fis.read(buffer);
                    if (readLength == -1) break;
                    arrayOutputStream.write(buffer, 0, readLength);
                }
                fis.close();
                arrayOutputStream.close();
                content = new String(arrayOutputStream.toByteArray());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

    /**
     * 读取文本数据
     *
     * @param fileName 文件名
     * @return String, 读取到的文本内容，失败返回null
     */
    public String readAssets(String fileName) {
        InputStream is = null;
        String content = "";
        try {
            AssetManager assetManager = context.getAssets();
            is = assetManager.open(fileName);
            if (is != null) {
                byte[] buffer = new byte[1024];
                ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
                while (true) {
                    int readLength = is.read(buffer);
                    if (readLength == -1) break;
                    arrayOutputStream.write(buffer, 0, readLength);
                }
                is.close();
                arrayOutputStream.close();
                content = new String(arrayOutputStream.toByteArray());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

    /**
     * 存储单个Parcelable对象
     *
     * @param fileName     文件名，要在系统内保持唯一
     * @param parcelObject 对象必须实现Parcelable
     * @return boolean 存储成功的标志
     */
    public boolean writeParcelable(String fileName, Parcelable parcelObject) {
        FileOutputStream fos = null;
        try {
            fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            Parcel parcel = Parcel.obtain();
            parcel.writeParcelable(parcelObject, Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
            byte[] data = parcel.marshall();
            fos.write(data);
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 存储List对象
     *
     * @param fileName 文件名，要在系统内保持唯一
     * @param list     对象数组集合，对象必须实现Parcelable
     * @return boolean 存储成功的标志
     */
    public boolean writeParcelableList(String fileName, List<Parcelable> list) {
        FileOutputStream fos = null;
        try {
            if (list instanceof List) {
                fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
                Parcel parcel = Parcel.obtain();
                parcel.writeList(list);
                byte[] data = parcel.marshall();
                fos.write(data);
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
     * 存储单个数据对象
     *
     * @param fileName 文件名
     * @return Parcelable, 读取到的Parcelable对象，失败返回null
     */
    public Parcelable readParcelable(String fileName, ClassLoader classLoader) {
        Parcelable parcelable = null;
        FileInputStream fis = null;
        ByteArrayOutputStream bos = null;
        try {
            fis = context.openFileInput(fileName);
            if (fis != null) {
                bos = new ByteArrayOutputStream();
                byte[] b = new byte[4096];
                int bytesRead;
                while ((bytesRead = fis.read(b)) != -1) {
                    bos.write(b, 0, bytesRead);
                }
                byte[] data = bos.toByteArray();
                Parcel parcel = Parcel.obtain();
                parcel.unmarshall(data, 0, data.length);
                parcel.setDataPosition(0);
                parcelable = parcel.readParcelable(classLoader);
                fis.close();
                bos.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return parcelable;
    }

    /**
     * 存储数据对象列表
     *
     * @param fileName 文件名
     * @return List, 读取到的对象数组，失败返回null
     */
    public List<Parcelable> readParcelableList(String fileName, ClassLoader classLoader) {
        List<Parcelable> results = null;
        FileInputStream fis = null;
        ByteArrayOutputStream bos = null;
        try {
            fis = context.openFileInput(fileName);
            if (fis != null) {
                bos = new ByteArrayOutputStream();
                byte[] b = new byte[4096];
                int bytesRead;
                while ((bytesRead = fis.read(b)) != -1) {
                    bos.write(b, 0, bytesRead);
                }
                byte[] data = bos.toByteArray();
                Parcel parcel = Parcel.obtain();
                parcel.unmarshall(data, 0, data.length);
                parcel.setDataPosition(0);
                results = parcel.readArrayList(classLoader);
                fis.close();
                bos.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return results;
    }

    /**
     * 存储数据对象列表
     *
     * @param fileName 文件名
     * @return List, 读取到的对象数组，失败返回null
     */
    public boolean saveSerializable(String fileName, Serializable data) {
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(context.openFileOutput(fileName, Context.MODE_PRIVATE));
            oos.writeObject(data);
            oos.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 读取数据对象列表
     *
     * @param fileName 文件名
     * @return Serializable, 读取到的序列化对象
     */
    public Serializable readSerialLizable(String fileName) {
        Serializable data = null;
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(context.openFileInput(fileName));
            data = (Serializable) ois.readObject();
            ois.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * 从assets里边读取字符串
     *
     * @param fileName
     * @return
     */
    public String getFromAssets(String fileName) {
        try {
            InputStreamReader inputReader = new InputStreamReader(context.getResources().getAssets().open(fileName));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line = "";
            String Result = "";
            while ((line = bufReader.readLine()) != null) {
                Result += line;
            }
            return Result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取二进制文件流
     *
     * @param fileName
     * @return
     */
    public byte[] getByteArrayFromFile(String fileName) {
        File file = new File(fileName);
        byte[] byteArray = null;
        try {
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int count;
            byte buffer[] = new byte[4096];
            while ((count = fis.read(buffer)) > 0) {
                baos.write(buffer, 0, count);
            }
            byteArray = baos.toByteArray();
            fis.close();
            baos.flush();
            baos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return byteArray;
    }

    /**
     * drawable转Bitmap
     *
     * @param drawable
     * @return
     */
    public Bitmap drawableToBitmap(Drawable drawable) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * Bitmap转二进制
     *
     * @param bitmap
     * @return
     */
    public static byte[] getBitmapByte(Bitmap bitmap) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        try {
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out.toByteArray();
    }

    /**
     * Save content to specified file.
     *
     * @param filePath file path indicate the file which be written content.
     * @param content
     * @return if save success, return true, otherwise return false.
     */
    public boolean saveFile(String filePath, String content) {
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            saveContent(file, content);
            return true;
        }
        return false;
    }

    /**
     * Writing content to file.
     *
     * @param file
     * @param content
     */
    public void saveContent(File file, String content) {
        try {
            FileWriter fileWriter;
            fileWriter = new FileWriter(file.getAbsolutePath());
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(content);
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Read content from specified path.
     *
     * @param pathName pathName of file
     * @return
     */
    public String readContentFromPath(String pathName) {
        return readContent(new File(pathName), true);
    }

    private String readContent(File file, boolean lineBreak) {
        StringBuilder content = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line);
                if (lineBreak) {
                    content.append("\n");
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content.toString();
    }

    //  计算String的MD5值
    public String md5(String string) {
        if (string.length() < 1) {
            return "";
        }
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5Util");
            byte[] bytes = md5.digest(string.getBytes());
            String result = "";
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result += temp;
            }
            return result;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    //  计算文件的MD5值
    public String md5(File file) {
        String result = "";
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            MappedByteBuffer byteBuffer = in.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, file.length());
            MessageDigest md5 = MessageDigest.getInstance("MD5Util");
            md5.update(byteBuffer);
            byte[] bytes = md5.digest();
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result += temp;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }
}