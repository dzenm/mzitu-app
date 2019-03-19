package com.din.mzitu.basehelper;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * log日志统计保存
 */
public class LogHelper {

    private static final String TAG = "DZY";                // 日志TAG
    private static final String SUFFIX = ".log";            // 日志文件后缀
    private static String logcatPath;                       // log文件路径
    private static boolean isDebug = true;                  // 是否是debug模式
    private LogDumper logDumper;                            // log输出文件线程
    private int PID;                                        // 进程的pid

    private static final int VERBOSE = 1;
    private static final int DEBUG = 2;
    private static final int INFO = 3;
    private static final int WARN = 4;
    private static final int ERROR = 5;
    private static final int ONTHING = 6;
    private static final int level = VERBOSE;

    private LogHelper() {
        init();
        PID = android.os.Process.myPid();
    }

    private static class Instance {
        private static final LogHelper INSTANCE = new LogHelper();
    }

    public static LogHelper getInstance() {
        return Instance.INSTANCE;
    }

    /**
     * 初始化目录
     */
    public LogHelper init() {
        logcatPath = FileHelper.getInstance().getFolders("log", "temp");
        File dir = new File(logcatPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return this;
    }

    /**
     * 开始输出日志
     *
     * @return
     */
    public LogHelper start() {
        if (logDumper == null) {
            logDumper = new LogDumper(String.valueOf(PID), logcatPath);
        }
        if (!logDumper.isAlive()) {
            logDumper.start();
        }
        return this;
    }

    /**
     * 停止输出日志
     *
     * @return
     */
    public LogHelper stop() {
        if (logDumper == null) {
            logDumper.stopLogs();
            logDumper = null;
        }
        return this;
    }

    public static void v(String msg) {
        if (level <= VERBOSE) {
            if (isDebug) {
                Log.v(TAG, msg);
            }
        }
    }

    public static void d(String msg) {
        if (level <= DEBUG) {
            if (isDebug) {
                Log.d(TAG, msg);
            }
        }
    }

    public static void i(String msg) {
        if (level <= INFO) {
            if (isDebug) {
                Log.i(TAG, msg);
            }
        }
    }

    public static void w(String msg) {
        if (level <= WARN) {
            if (isDebug) {
                Log.w(TAG, msg);
            }
        }
    }

    public static void e(String msg) {
        if (level <= ERROR) {
            if (isDebug) {
                Log.e(TAG, msg);
            }
        }
    }

    /**
     * 打印所有的日志信息
     *
     * @return
     */
    public LogHelper all() {
        logDumper.all();
        return this;
    }

    /**
     * 打印tag的日志信息
     *
     * @return
     */
    public LogHelper tag() {
        logDumper.tag();
        return this;
    }

    public LogHelper v() {
        logDumper.v();
        return this;
    }

    public LogHelper d() {
        logDumper.d();
        return this;
    }

    public LogHelper i() {
        logDumper.i();
        return this;
    }

    public LogHelper w() {
        logDumper.w();
        return this;
    }

    public LogHelper e() {
        logDumper.e();
        return this;
    }

    /**
     * 输出日志文件的线程
     */
    private class LogDumper extends Thread {

        private Process logcatProc;
        private BufferedReader mReader;
        private boolean mRunning = true;
        private String cmds;
        private String mPID;
        private FileOutputStream out;

        public LogDumper(String pid, String dir) {
            mPID = pid;
            try {
                out = new FileOutputStream(new File(dir, DateHelper.getTimeSecond() + SUFFIX));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            if (isDebug) {
                tag();
            } else {
                all();
            }
        }

        private LogDumper all() {
            cmds = "logcat  | grep \"(" + mPID + ")\"";
            return this;
        }

        private LogDumper tag() {
            cmds = "logcat -s " + TAG;
            return this;
        }

        private LogDumper v() {
            cmds = "logcat *:e *:v | grep \"(" + mPID + ")\"";
            return this;
        }

        private LogDumper d() {
            cmds = "logcat *:e *:d | grep \"(" + mPID + ")\"";
            return this;
        }

        private LogDumper i() {
            cmds = "logcat *:e *:i | grep \"(" + mPID + ")\"";
            return this;
        }

        private LogDumper w() {
            cmds = "logcat *:e *:w | grep \"(" + mPID + ")\"";
            return this;
        }

        private LogDumper e() {
            cmds = "logcat *:e | grep \"(" + mPID + ")\"";
            return this;
        }

        public LogDumper stopLogs() {
            mRunning = false;
            return this;
        }

        @Override
        public void run() {
            super.run();
            try {
                logcatProc = Runtime.getRuntime().exec(cmds);
                mReader = new BufferedReader(new InputStreamReader(logcatProc.getInputStream()), 1024);
                String line = null;
                while (mRunning && (line = mReader.readLine()) != null) {
                    if (!mRunning) {
                        break;
                    }
                    if (line.length() == 0) {
                        continue;
                    }
                    if (out != null && line.contains(mPID)) {
                        out.write(("|======|" + DateHelper.getTimeSecond() + " " + line + "\n").getBytes());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (logcatProc != null) {
                    logcatProc.destroy();
                    logcatProc = null;
                }
                if (mReader != null) {
                    try {
                        mReader.close();
                        mReader = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    out = null;
                }
            }
        }
    }
}