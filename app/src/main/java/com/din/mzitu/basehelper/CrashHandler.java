package com.din.mzitu.basehelper;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private static final String TAG = CrashHandler.class.getSimpleName();
    private static final String FILE_PATH = "/Crash";
    private static final String FILE_NAME = "crash_";
    private static final String FILE_SUFFIX = ".txt";
    private static final boolean isDebug = true;

    private Context context;                                            // 程序的Context对象
    private Thread.UncaughtExceptionHandler defaultHandler;             // 系统默认的UncaughtException处理类

    private CrashHandler() {
    }

    private static class Instance {
        private static CrashHandler INSTANCE = new CrashHandler();      // CrashHandler实例
    }

    public static CrashHandler getInstance() {
        return Instance.INSTANCE;
    }

    public CrashHandler init(Context context) {
        this.context = context.getApplicationContext();
        defaultHandler = Thread.getDefaultUncaughtExceptionHandler();   // 获取系统默认的UncaughtException处理器
        Thread.setDefaultUncaughtExceptionHandler(this);                // 设置该CrashHandler为程序的默认处理器
        return this;
    }

    /**
     * 当程序中有未被捕获的异常，系统将会自动调用uncaughtException方法
     *
     * @param thread 出现未捕获异常的线程
     * @param ex     未捕获的异常
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (catchCrashException(ex) && defaultHandler != null) {    // 如果系统提供了默认处理，则交给系统处理，否则就自己结束
            defaultHandler.uncaughtException(thread, ex);
        } else {
            Process.killProcess(Process.myPid());                   // 退出应用
        }
    }

    private boolean catchCrashException(Throwable ex) {
        if (ex == null) {
            return false;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(context, "程序发生崩溃，请重新打开", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        }).start();
        saveCrashInfo(ex);                                  // 保存日志文件
        return true;
    }

    /**
     * 保存错误信息到文件中
     *
     * @param ex
     * @return 返回文件名称, 便于将文件传送到服务器
     */
    private String saveCrashInfo(Throwable ex) {
        Map<String, String> infos = saveDeviceInfo();           // 收集设备参数信息
        StringBuffer stringBuffer = new StringBuffer();         // 输出手机、系统、软件信息
        stringBuffer.append("-------- 输出手机、系统、软件信息 --------\n\n");
        for (Map.Entry<String, String> entry : infos.entrySet()) {
            stringBuffer.append(entry.getKey() + " ------ " + entry.getValue() + "\n");
        }
        stringBuffer.append("\n-------- 手机、系统、软件信息收集完成 --------\n");

        Writer writer = new StringWriter();                     // 输出异常信息
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            printWriter.append("\r\n");                         // 换行 每个个异常栈之间换行
            cause = cause.getCause();
        }
        printWriter.close();

        stringBuffer.append("\n\n\n-------- 开始收集异常信息 --------\n\n");
        stringBuffer.append(writer.toString());
        stringBuffer.append("\n-------- 异常信息收集完成 --------");

        String fileName = saveFile(stringBuffer.toString());
        return fileName;
    }

    /**
     * 收集设备参数信息
     */
    private Map<String, String> saveDeviceInfo() {
        Map<String, String> infos = new HashMap<String, String>();                          // 用来存储设备信息
        try {
            // 获取应用包参数信息
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (info != null) {
                infos.put("安卓版本号== ", Build.VERSION.RELEASE + String.valueOf(Build.VERSION.SDK_INT));     // Android版本号
                infos.put("手机制造商== ", Build.MANUFACTURER);                   // 手机制造商
                infos.put("App版本名称== ", info.versionName == null ? "null" : info.versionName);     // App版本名称
                infos.put("App版本号== ", info.versionCode + "");                // App版本号
                infos.put("手机型号== ", Build.MODEL);                           // 手机型号
                infos.put("CPU架构== ", Build.CPU_ABI);                         // CPU架构
            }

            // 获取设备硬件信息
            Field[] fields = Build.class.getDeclaredFields();
            for (Field field : fields) { // 迭代Build的字段key-value 此处的信息主要是为了在服务器端手机各种版本手机报错的原因
                field.setAccessible(true);
                infos.put(field.getName() + "== ", field.get(null).toString());
                Log.d(TAG, field.getName() + "== " + field.get(null));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return infos;
    }

    /**
     * 存储为文件
     *
     * @param causeInfos
     */
    private String saveFile(String causeInfos) {
        String fileName = FILE_NAME + DateHelper.getTimeSecond() + FILE_SUFFIX;                // 文件名
        try {
            String folders = FileHelper.getInstance().getFolders("Log", "crash");
            if (folders == null) {
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    return null;
                }
                File directory = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + FILE_PATH);
                if (!directory.exists()) {
                    directory.mkdirs();
                }
                folders = directory.getAbsolutePath();
                Log.d(TAG, "the new create folders is: " + folders);
            }
            FileOutputStream fos = new FileOutputStream(folders + File.separator + fileName);
            fos.write(causeInfos.getBytes("UTF-8"));
            fos.close();
            return fileName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}