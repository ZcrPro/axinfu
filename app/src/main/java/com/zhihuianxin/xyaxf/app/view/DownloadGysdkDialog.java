package com.zhihuianxin.xyaxf.app.view;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.view.View;

import com.zhihuianxin.xyaxf.app.base.axutil.Util;
import com.zhihuianxin.xyaxf.App;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.payment.CashierDeskActivity;
import com.zhihuianxin.xyaxf.app.service.DownloadAPKService;

import java.io.File;

/**
 * Created by Vincent on 2018/3/16.
 */

public class DownloadGysdkDialog extends Dialog{
    public static final String INSTALL_GY_APK = "install_gy_apk";
    public static final String INSTALL_GYAPK_NAME = "installApkName";
    private Context context;

    public DownloadGysdkDialog(@NonNull Context context) {
        super(context,R.style.logoutDialog);
        this.context = context;

        installApkListener();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.download_gysdk_dialog);

        findViewById(R.id.download).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoService();
            }
        });
        findViewById(R.id.logcancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(broadcastReceiver != null)
        context.unregisterReceiver(broadcastReceiver);
    }

    public void gotoService(){
        Intent intent = new Intent(context,DownloadAPKService.class);
        Bundle bundle = new Bundle();
        bundle.putString(DownloadAPKService.DOWNLOAD_TYPE,DownloadAPKService.DOWNLOAD_GYSDK);
        bundle.putString(DownloadAPKService.DOWNLOAD_URL,App.mAxLoginSp.getGysdkUrl());
        intent.putExtras(bundle);
        context.startService(intent);
        dismiss();
    }

    private Uri getApkUri(File[] files,String apkName){
        Uri uri = null;
        for (File file : files){
            if(file.getName().equals(apkName)){
                uri = Uri.fromFile(file);
            }
        }
        return uri;
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            File sdcardDir = Environment.getExternalStorageDirectory();
            File _file = new File(sdcardDir.getPath()+File.separator+ CashierDeskActivity.EXTRA_APK_DIR);
            if(DownloadAPKService.isLegalInstallingAPK(Util.getAPKNameFromUrl(App.mAxLoginSp.getGysdkUrl()),DownloadAPKService.DOWNLOAD_GYSDK)){
                Intent installIntent = new Intent(Intent.ACTION_VIEW);
                installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                installIntent.setDataAndType(getApkUri(_file.listFiles(),intent.getExtras().getString(INSTALL_GYAPK_NAME)), "application/vnd.android.package-archive");
                context.startActivity(installIntent);
            } else{
                App.mAxLoginSp.setGysdkDone(false);
                gotoService();
            }
        }
    };

    private void installApkListener() {
        IntentFilter intentFilter = new IntentFilter(INSTALL_GY_APK);
        context.registerReceiver(broadcastReceiver, intentFilter);
    }
}
