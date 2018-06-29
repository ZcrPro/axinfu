package com.zhihuianxin.xyaxf.app.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.zhihuianxin.xyaxf.app.base.axutil.Util;
import com.zhihuianxin.xyaxf.App;
import com.zhihuianxin.xyaxf.app.payment.CashierDeskActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;

/**
 * Created by Vincent on 2017/12/28.
 */

public class DownloadAPKService extends Service {
    public static final String BROADCAST_AKP_DOWNLOAD = "broadcast_download_apk";// 广播注册
    public static final String APK_DOWNLOAD_PROGRESS = "download_apk_rogress";   // 进度
    public static final String APK_DOWNLOAD_TYPE = "download_apk_type";          // 0 ing ; 1 done

    public static final String DOWNLOAD_TYPE = "downlaod_type";  // 下载什么文件
    public static final String DOWNLOAD_URL = "downlaod_url";    // 下载url
    public static final String DOWNLOAD_GYSDK = "downlaod_gysdk";// gysdk
    public static final String DOWNLOAD_APP = "downlaod_app";    // app本身

    public static final String fileRootPath = Environment.getExternalStorageDirectory() + File.separator;

    protected int fileSzie;//文件大小
    protected int fileCache;//文件缓存
    protected String fileName = "";//文件名
    protected File downloaddir, downloadfile;
    private static boolean isDownloading = false;
    private String url,type;
    private DecimalFormat decimalFormat;
    private int preIndex = -1;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initViews(intent);
        judgeStatus();
        return START_NOT_STICKY;
    }

    private void initViews(Intent intent){
        preIndex = -1;
        decimalFormat = new DecimalFormat("############0");
        type = intent.getExtras().getString(DOWNLOAD_TYPE);
        url = intent.getExtras().getString(DOWNLOAD_URL);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static boolean isRunning(){
        return isDownloading;
    }

    private void judgeStatus(){
        if(isLegalInstallingAPK(Util.getAPKNameFromUrl(url),type)){
            sendBroadCast(100,1);
        } else{
            if(isDownloading){
                //正在下载,不管，正在发广播
            } else{
                //开始下载
                if(!DownloadFile(url)){
                    //APK下载失败
                    isDownloading = false;
                    downloadFail();
                } else{
                    //APK下载中,已经启动异步下载
                    isDownloading = true;
                }
            }
        }
    }

    public static boolean isLegalInstallingAPK(String gyAKPName,String apkType){
        boolean apkDownloadDone = App.mAxLoginSp.getGysdkDone();
        File fileDir = new File(DownloadAPKService.fileRootPath + CashierDeskActivity.EXTRA_APK_DIR);
        boolean dirExist = fileDir.exists();
        File apkFile = null;
        if(fileDir.exists()) {
            apkFile = new File(DownloadAPKService.fileRootPath + CashierDeskActivity.EXTRA_APK_DIR + File.separator+ gyAKPName);
        }
        boolean apkExist = (apkFile!=null && apkFile.exists());
        return (!apkType.equals(DownloadAPKService.DOWNLOAD_GYSDK) || apkDownloadDone) && dirExist && apkExist;
    }

    @SuppressLint("StaticFieldLeak")
    protected boolean DownloadFile(String downloadUrl) {
        /*文件名*/
        fileName = Util.getAPKNameFromUrl(url);
        if(!fileName.contains("apk")){
            return false;// 非APK文件
        }
        if(!downloadUrl.startsWith("http")){
            downloadUrl = "http://" + downloadUrl;
        }
        /*下载目录*/
        downloaddir = new File(fileRootPath + CashierDeskActivity.EXTRA_APK_DIR);
        downloadfile = new File(fileRootPath + CashierDeskActivity.EXTRA_APK_DIR + File.separator+fileName);

        if (!downloaddir.exists()) {
            downloaddir.mkdirs();
        }

        new AsyncTask<String, String, Boolean>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }
            @Override
            protected void onProgressUpdate(String... values) {
                if(preIndex != Integer.parseInt(values[0])){
                    preIndex = Integer.parseInt(values[0]);
                    sendBroadCast(Integer.parseInt(values[0]),0);
                    Log.e("wbq1",values[0]);
                }
            }
            @Override
            protected Boolean doInBackground(String... params) {
                try {
                    fileName = Util.getAPKNameFromUrl(url);
                    //获取文件名
                    URL myURL = new URL(params[0]);

                    HttpURLConnection conn = (HttpURLConnection) myURL.openConnection();
                    conn.setDoInput(true);
                    conn.connect();
                    InputStream is;
                    if(conn.getResponseCode() == 200){
                        is = conn.getInputStream();
                    } else{
                        return false;
                    }

                    fileSzie = conn.getContentLength();//根据响应获取文件大小
                    if (fileSzie <= 0) {
                        throw new RuntimeException("无法获知文件大小 ");
                    }
                    if (is == null) throw new RuntimeException("stream is null");
                        /*下载目录*/
                    if (!downloaddir.exists()) {
                        downloaddir.mkdirs();
                    }
                    //把数据存入 路径+文件名
                    FileOutputStream fos = new FileOutputStream(downloadfile);
                    byte buf[] = new byte[1024];
                    fileCache = 0;
                    do {
                        //循环读取
                        int numread = is.read(buf);
                        if (numread == -1) {
                            break;
                        }
                        fos.write(buf, 0, numread);
                        fileCache += numread;
                        this.publishProgress(decimalFormat.format((float)fileCache*100/fileSzie));
                    } while (true);

                    try {
                        is.close();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        return false;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
                return true;
            }

            @Override
            protected void onPostExecute(Boolean s) {
                super.onPostExecute(s);
                if(s){
                    Message message = new Message();
                    message.what = 1;
                    handler.sendMessage(message);

                    sendBroadCast(100,1);
                } else{
                    Message message = new Message();
                    message.what = 2;
                    handler.sendMessage(message);

                    Toast.makeText(getApplicationContext(),"APK下载失败", Toast.LENGTH_SHORT).show();
                }
                isDownloading = false;
            }
        }.execute(downloadUrl);
        return true;
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 1){
                downloadSuc();
            } else{
                downloadFail();
            }
        }
    };
    private void downloadSuc(){
        if(type.equals(DOWNLOAD_GYSDK)){
            App.mAxLoginSp.setGysdkDone(true);
        }
        if(type.equals(DOWNLOAD_APP)){
            App.mAxLoginSp.setAppApkDone(true);
        }

    }

    private void downloadFail(){
        if(type.equals(DOWNLOAD_GYSDK)){
            App.mAxLoginSp.setGysdkDone(false);
        }
        if(type.equals(DOWNLOAD_APP)){
            App.mAxLoginSp.setAppApkDone(false);
        }
    }

    private void sendBroadCast(int progress,int type){
        Intent intent = new Intent(BROADCAST_AKP_DOWNLOAD);
        Bundle bundle = new Bundle();
        bundle.putInt(APK_DOWNLOAD_PROGRESS,progress);
        bundle.putInt(APK_DOWNLOAD_TYPE,type);
        intent.putExtras(bundle);
        sendBroadcast(intent);

        if(type == 1){
            DownloadAPKService.this.stopSelf();
        }
    }
}
