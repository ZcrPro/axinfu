package com.zhihuianxin.xyaxf.app.base.axutil;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Vincent on 2016/10/9.
 */
public class Util {
    public static final String[] HTTP_URL = {"http://"};
    public static final String HTTPS_PREFIX = "https://";
    /**
     * 正则表达式：验证手机号
     */
    public static final String REGEX_MOBILE = "1[0-9]{10}";
    public static final String REGEX_MOBILE_DEBUG = "1[0-9]{10}";

    public static boolean isEmpty(String text) {
        return text == null || text.trim().length() == 0;
    }

    public static PackageInfo getPackageInfo(Context context) {
        return getPackageInfo(context, context.getPackageName());
    }

    public static PackageInfo getPackageInfo(Context context, String packageName) {
        PackageManager manager = context.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(packageName, 0);
            return info;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String byte2HexStr(byte[] data) {
        if (data == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        for (byte b : data) {
            String bHexStr = Integer.toHexString(0x00ff & b);
            if (bHexStr.length() == 1) {
                sb.append('0');
            }

            sb.append(bHexStr);

        }

        return sb.toString();
    }

    public static boolean isEnabled(String value) {
        return !isEmpty(value);
    }

    public static String readFile(String path, String encoding) throws IOException {
        InputStream is = getFileStream(path);
        String result = readStreamString(is, encoding);
        is.close();

        return result;
    }

    public static String getXingHao(int length) {
        String xh = "";
        for (int i = 0; i < length; i++) {
            xh += "*";
        }
        return xh;
    }

    public static InputStream getFileStream(String path) throws IOException {
        FileInputStream fis = new FileInputStream(path);
        return fis;
    }

    public static String readStreamString(InputStream is, String encoding) throws IOException {
        return new String(readStream(is), encoding);
    }

    public static byte[] readStream(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024 * 10];
        int readlen;
        while ((readlen = is.read(buf)) >= 0) {
            baos.write(buf, 0, readlen);
        }

        baos.close();

        return baos.toByteArray();
    }

    public static byte[] getUtf8Bytes(String str) {
        try {
            return str.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            return new byte[0];
        }
    }

    public static File createTempFile(Context context, String extName) throws IOException {
        File tempDir = getTempDir(context);
        File tempFile = new File(tempDir, createTempFileName(extName));

        tempFile.createNewFile();
        tempFile.deleteOnExit();
        return tempFile;
    }

    public static File getTempDir(Context context) {
        File cacheDir;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            File appDataDir = new File(Environment.getExternalStorageDirectory(), context.getPackageName());
            cacheDir = new File(appDataDir, "cache");
        } else {
            cacheDir = context.getCacheDir();
        }

        File tempDir = new File(cacheDir, "temp");
        tempDir.mkdirs();

        return tempDir;
    }

    private static String createTempFileName(String extName) {
        return String.format("%s.%s", System.currentTimeMillis(), extName);
    }

    public static int[] getTimeItems(String time) {
        if (time == null || !(time.matches("[0-9]{14}") || time.matches("[0-9]{8}"))) {
            return new int[0];
        }

        boolean dateOnly = time.length() == 8;
        String year = time.substring(0, 4);
        String month = time.substring(4, 6);
        String date = time.substring(6, 8);
        if (dateOnly) {
            return new int[]{Integer.valueOf(year), Integer.valueOf(month), Integer.valueOf(date)};
        } else {
            String hour = time.substring(8, 10);
            String minute = time.substring(10, 12);
            String second = time.substring(12, 14);

            return new int[]{Integer.valueOf(year), Integer.valueOf(month), Integer.valueOf(date), Integer.valueOf(hour), Integer.valueOf(minute), Integer.valueOf(second)};
        }
    }

    public static String getRefreshDisplayTime(long time) {
        //1 小时内: xx 分钟前
        //24 小时内: xx 小时前
        //7 天内: xx天前
        //日期

        long currentTime = System.currentTimeMillis();
        long timeDiff = (currentTime - time) / 1000;

        final int MINUTE = 60;
        final int HOUR = 60 * MINUTE;
        final int DAY = 24 * 3600;
        final int WEEK = 7 * DAY;
        if (timeDiff > 0) {
            if (timeDiff < HOUR) {
                int minuteCount = Math.round((float) timeDiff / (float) MINUTE);
                return String.format("%s分钟前", minuteCount == 0 ? 1 : minuteCount);
            } else if (timeDiff < DAY) {
                int hourCount = Math.round((float) timeDiff / (float) HOUR);
                return String.format("%s小时前", hourCount);
            } else if (timeDiff < WEEK) {
                int dayCount = Math.round((float) timeDiff / (float) DAY);
                return String.format("%s天前", dayCount);
            } else {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(time);
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                return String.format("%04d-%02d-%02d", year, month + 1, day);
            }
        } else {
            return "-";
        }
    }


    public static int parseInteger(String value, int defaultValue) {
        try {
            return Integer.valueOf(value);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static String formatDateTime(long milli) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        return format.format(new Date(milli));
    }

    public static String formatDate(long milli) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        return format.format(new Date(milli));
    }

    public static String getUtf8String(byte[] data) {
        try {
            return new String(data, "utf-8");
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    /**
     * 加密
     *
     * @param content  需要加密的内容
     * @param password 加密密码
     * @return
     */
    public static byte[] encrypt(String content, String password) {
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            kgen.init(128, new SecureRandom(password.getBytes()));
            SecretKey secretKey = kgen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
            Cipher cipher = Cipher.getInstance("AES");// 创建密码器
            byte[] byteContent = content.getBytes("utf-8");
            cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化
            byte[] result = cipher.doFinal(byteContent);
            return result; // 加密
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isInForgbround(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService("activity");//Context.ACTIVITY_SERVICE
        List<ActivityManager.RunningTaskInfo> runningTasks = am.getRunningTasks(1);

        ActivityManager.RunningTaskInfo rti = runningTasks.get(0);

        String topActivity = rti.topActivity.getClassName();
        String topPackage = rti.topActivity.getPackageName();
        boolean result = context.getPackageName().equals(rti.topActivity.getPackageName());

        return result;

    }

    private static boolean isUrl(String url, String[] prefix) {
        for (String s : prefix) {
            if (url.startsWith(s)) {
                return true;
            }
        }

        return false;
    }

    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService("connectivity");//Context.CONNECTIVITY_SERVICE
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    public static boolean isNetworkAvailable(Activity mActivity) {
        Context context = mActivity.getApplicationContext();
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService("connectivity");//Context.CONNECTIVITY_SERVICE
        if (connectivity == null) {
            return false;
        } else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean isHTTPUrl(String url) {
        return isUrl(url, HTTP_URL);
    }

    public static boolean isHTTPSUrl(String url) {
        return isUrl(url, new String[]{HTTPS_PREFIX});
    }

    public static String getNowTime() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String date = sDateFormat.format(new Date());
        return date;
    }

    public static Intent createExplicitFromImplicitIntent(Context context, Intent implicitIntent) {
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resolveInfo = pm.queryIntentServices(implicitIntent, 0);
        if (resolveInfo == null || resolveInfo.size() != 1) {
            return null;
        }
        ResolveInfo serviceInfo = resolveInfo.get(0);
        String packageName = serviceInfo.serviceInfo.packageName;
        String className = serviceInfo.serviceInfo.name;
        ComponentName component = new ComponentName(packageName, className);
        Intent explicitIntent = new Intent(implicitIntent);
        explicitIntent.setComponent(component);

        return explicitIntent;
    }

    public static String getAPKNameFromUrl(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }
}
