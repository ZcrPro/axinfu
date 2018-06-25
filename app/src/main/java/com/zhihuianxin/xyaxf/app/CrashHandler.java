package com.zhihuianxin.xyaxf.app;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
/**
 * Created by Vincent on 2016/11/25.
 */

public class CrashHandler implements UncaughtExceptionHandler {
    private static final String TAG = "NorrisInfo";
    private UncaughtExceptionHandler mDefaultHandler;
    private static CrashHandler mInstance = new CrashHandler();
    private Context mContext;
    private Map<String, String> mLogInfo = new HashMap();
    private SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyyMMdd_HH-mm-ss");

    private CrashHandler() {
    }

    public static CrashHandler getInstance() {
        return mInstance;
    }

    public void init(Context paramContext) {
        this.mContext = paramContext;
        this.mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    public void uncaughtException(Thread paramThread, Throwable paramThrowable) {
        if(!this.handleException(paramThrowable) && this.mDefaultHandler != null) {
            this.mDefaultHandler.uncaughtException(paramThread, paramThrowable);
        } else {
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException var4) {
                var4.printStackTrace();
            }

            Process.killProcess(Process.myPid());
            System.exit(1);
        }

    }

    public boolean handleException(Throwable paramThrowable) {
        if(paramThrowable == null) {
            return false;
        } else {
            (new Thread() {
                public void run() {
                    Looper.prepare();
                    Toast.makeText(CrashHandler.this.mContext, "很抱歉,程序出现异常,即将退出", Toast.LENGTH_LONG).show();
                    Looper.loop();
                }
            }).start();
            this.getDeviceInfo(this.mContext);
            this.saveCrashLogToFile(paramThrowable);
            return true;
        }
    }

    public void getDeviceInfo(Context paramContext) {
        try {
            PackageManager mFields = paramContext.getPackageManager();
            PackageInfo field = mFields.getPackageInfo(paramContext.getPackageName(), 1);
            if(field != null) {
                String versionName = field.versionName == null?"null":field.versionName;
                String versionCode = String.valueOf(field.versionCode);
                this.mLogInfo.put("versionName", versionName);
                this.mLogInfo.put("versionCode", versionCode);
            }
        } catch (NameNotFoundException var10) {
            var10.printStackTrace();
        }

        Field[] var11 = Build.class.getDeclaredFields();
        Field[] var6 = var11;
        int var14 = var11.length;

        for(int var13 = 0; var13 < var14; ++var13) {
            Field var12 = var6[var13];

            try {
                var12.setAccessible(true);
                this.mLogInfo.put(var12.getName(), var12.get("").toString());
                Log.d("NorrisInfo", var12.getName() + ":" + var12.get(""));
            } catch (IllegalArgumentException var8) {
                var8.printStackTrace();
            } catch (IllegalAccessException var9) {
                var9.printStackTrace();
            }
        }

    }

    private String saveCrashLogToFile(Throwable paramThrowable) {
        StringBuffer mStringBuffer = new StringBuffer();
        Iterator mPrintWriter = this.mLogInfo.entrySet().iterator();

        String mResult;
        while(mPrintWriter.hasNext()) {
            Entry mWriter = (Entry)mPrintWriter.next();
            String mThrowable = (String)mWriter.getKey();
            mResult = (String)mWriter.getValue();
            mStringBuffer.append(mThrowable + "=" + mResult + "\r\n");
        }

        StringWriter mWriter1 = new StringWriter();
        PrintWriter mPrintWriter1 = new PrintWriter(mWriter1);
        paramThrowable.printStackTrace(mPrintWriter1);

        for(Throwable mThrowable1 = paramThrowable.getCause(); mThrowable1 != null; mThrowable1 = mThrowable1.getCause()) {
            mThrowable1.printStackTrace(mPrintWriter1);
            mPrintWriter1.append("\r\n");
        }

        mPrintWriter1.close();
        mResult = mWriter1.toString();
        mStringBuffer.append(mResult);
        String mTime = this.mSimpleDateFormat.format(new Date());
        String mFileName = "CrashLog-" + mTime + ".log";
        if(Environment.getExternalStorageState().equals("mounted")) {
            try {
                File e = new File(Environment.getExternalStorageDirectory() + "/CrashInfos");
                Log.v("NorrisInfo", e.toString());
                if(!e.exists()) {
                    e.mkdir();
                }

                FileOutputStream mFileOutputStream = new FileOutputStream(e + "/" + mFileName);
                mFileOutputStream.write(mStringBuffer.toString().getBytes());
                mFileOutputStream.close();
                return mFileName;
            } catch (FileNotFoundException var11) {
                var11.printStackTrace();
            } catch (IOException var12) {
                var12.printStackTrace();
            }
        }

        return null;
    }
}
