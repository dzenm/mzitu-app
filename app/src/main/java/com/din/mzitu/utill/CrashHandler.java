package com.din.mzitu.utill;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Process;
import android.util.Log;

import com.din.mzitu.activities.App;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private static final String TAG = "CrashHandler";
    private static final String LOG_DIR = "/log/crash/";
    private static final String FILE_NAME = "crash";
    private static final boolean DEBUG = true;

    private static CrashHandler INSTANCE = new CrashHandler();          // CrashHandler实例

    private Thread.UncaughtExceptionHandler mDefaultHandler;            // 系统默认的UncaughtException处理类 

    private Context mContext;                                           // 程序的Context对象

    private Map<String, String> infos = new HashMap<String, String>();               // 用来存储设备信息和异常信息

    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss SSS"); // 用于格式化日期,作为日志文件名的一部分

    private CrashHandler() {
    }

    /**
     * 获取CrashHandler实例 ,单例模式
     */
    public static CrashHandler getInstance() {
        return INSTANCE;
    }

    /**
     * 初始化
     *
     * @param context
     */
    public void init(Context context) {
        // 获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        // 设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
        mContext = context.getApplicationContext();
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     *
     * @param thread 出现未捕获异常的线程
     * @param ex     未捕获的异常
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        handleException(ex);                                        // 收集错误信息
        mDefaultHandler.uncaughtException(thread, ex);              // 如果用户没有处理则让系统默认的异常处理器来处理
        ex.printStackTrace();
        if (mDefaultHandler != null) {                                  // 如果系统提供了默认处理，则交给系统处理，否则就自己结束
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            Process.killProcess(Process.myPid());
        }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }
        collectDeviceInfo(mContext);        // 收集设备参数信息
        saveCrashInfo2File(ex);             // 保存日志文件
        return true;
    }

    /**
     * 收集设备参数信息
     *
     * @param ctx
     */
    public void collectDeviceInfo(Context ctx) {
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                infos.put("Android版本号 OS Version: ", Build.VERSION.RELEASE + String.valueOf(Build.VERSION.SDK_INT));     // Android版本号
                infos.put("手机制造商 Vendor: ", Build.MANUFACTURER);        // 手机制造商
                infos.put("App版本名称 App versionName: ", pi.versionName == null ? "null" : pi.versionName);     // App版本名称
                infos.put("App版本号 App versionCode: ", pi.versionCode + "");              // App版本号
                infos.put("手机型号 Model: ", Build.MODEL);        // 手机型号
                infos.put("CPU架构 CPU ABI: ", Build.CPU_ABI);    // CPU架构
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "an error occured when collect package info", e);
        }
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                infos.put(field.getName(), field.get(null).toString());
                Log.d(TAG, field.getName() + " : " + field.get(null));
            } catch (Exception e) {
                Log.e(TAG, "an error occured when collect crash info", e);
            }
        }
    }

    /**
     * 保存错误信息到文件中
     *
     * @param ex
     * @return 返回文件名称, 便于将文件传送到服务器
     */
    private String saveCrashInfo2File(Throwable ex) {
        StringBuffer sb = new StringBuffer();       // 输出手机、系统、软件信息
        sb.append("-------- 输出手机、系统、软件信息 --------\n\n");
        for (Map.Entry<String, String> entry : infos.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key + " =  " + value + "\n");
        }
        sb.append("\n-------- 手机、系统、软件信息收集完成 --------\n");
        Writer writer = new StringWriter();         // 创建文件输出流
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        sb.append("\n\n\n-------- 开始收集异常信息 --------\n\n");
        sb.append(result);
        sb.append("\n\n-------- 异常信息收集完成 --------");
        try {
            String time = formatter.format(new Date());                 // 崩溃的时间
            String fileName = FILE_NAME + "_" + time + ".txt";          // 文件名
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                File dir = new File(FileUtil.getInstance().getDirect("log", "crash"));
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                FileOutputStream fos = new FileOutputStream(dir.getAbsolutePath() + File.separator + fileName);
                fos.write(sb.toString().getBytes("UTF-8"));
                fos.close();
            }
            return fileName;
        } catch (Exception e) {
            Log.e(TAG, "an error occured while writing file...", e);
        }
        return null;
    }
}