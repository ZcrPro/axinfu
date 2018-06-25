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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.zhihuianxin.axutil.Util;
import com.zhihuianxin.xyaxf.App;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.payment.CashierDeskActivity;
import com.zhihuianxin.xyaxf.app.service.DownloadAPKService;

import java.io.File;

/**
 * Created by Vincent on 2018/4/20.
 */

public class DownloadAppDialog extends Dialog{
    public static final String INSTALL_APP_APK = "install_app_apk";
    private Context mContext;
    private ProgressBar progressBar;
    private TextView progressTxt,exitTxt,updateTxt;
    private View ingView,doneView;
    private String apkUrl;
    private boolean isPause = false;

    public DownloadAppDialog(@NonNull Context context) {
        super(context, R.style.logoutDialog);
        mContext = context;
        setCancelable(true);
        setCanceledOnTouchOutside(false);

        IntentFilter filter = new IntentFilter(DownloadAPKService.BROADCAST_AKP_DOWNLOAD);
        mContext.registerReceiver(sdkReceiver,filter);
    }

    public void setStart(){
        isPause = false;
    }

    BroadcastReceiver sdkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(!isPause){
                if(!isShowing())show();

                if(intent.getExtras().getInt(DownloadAPKService.APK_DOWNLOAD_TYPE) == 1){
                    ingView.setVisibility(View.GONE);
                    doneView.setVisibility(View.VISIBLE);
                } else{
                    ingView.setVisibility(View.VISIBLE);
                    doneView.setVisibility(View.GONE);
                }
                progressTxt.setText(intent.getExtras().getInt(DownloadAPKService.APK_DOWNLOAD_PROGRESS)+"%");
                progressBar.setProgress(intent.getExtras().getInt(DownloadAPKService.APK_DOWNLOAD_PROGRESS));
            } else {
                cancel();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_app_dialog);

        initViews();
    }

    private void initViews(){
        apkUrl = App.mAxLoginSp.getUpdateUrl();
        installApkListener();
        progressTxt = (TextView) findViewById(R.id.progressindex);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        exitTxt = (TextView) findViewById(R.id.exit);
        updateTxt = (TextView) findViewById(R.id.update);
        ingView = findViewById(R.id.ingviewid);
        doneView = findViewById(R.id.doneviewid);

        doneView.setOnClickListener(clickListener);
        exitTxt.setOnClickListener(clickListener);
        updateTxt.setOnClickListener(clickListener);
    }

    public void startDownloadService(){
        gotoService();
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.exit){
                isPause = true;
                dismiss();
            }
            if(v.getId() == R.id.update && !DownloadAPKService.isRunning()){
                gotoService();
            }
            if(v.getId() == R.id.doneviewid){
                mContext.sendBroadcast(new Intent(INSTALL_APP_APK));
            }
        }
    };

    private void gotoService(){
        Intent intent = new Intent(mContext,DownloadAPKService.class);
        Bundle bundle = new Bundle();
        bundle.putString(DownloadAPKService.DOWNLOAD_TYPE,DownloadAPKService.DOWNLOAD_APP);
        bundle.putString(DownloadAPKService.DOWNLOAD_URL,apkUrl==null? App.mAxLoginSp.getUpdateUrl():apkUrl);
        intent.putExtras(bundle);
        mContext.startService(intent);
    }

    private Uri getApkUri(File[] files, String apkName){
        Uri uri = null;
        for (File file : files){
            if(file.getName().equals(apkName)){
                uri = Uri.fromFile(file);
            }
        }
        return uri;
    }

    private void installApkListener() {
        IntentFilter intentFilter = new IntentFilter(INSTALL_APP_APK);
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                File sdcardDir = Environment.getExternalStorageDirectory();
                File _file = new File(sdcardDir.getPath()+File.separator+ CashierDeskActivity.EXTRA_APK_DIR);
                if(DownloadAPKService.isLegalInstallingAPK(Util.getAPKNameFromUrl(apkUrl), DownloadAPKService.DOWNLOAD_APP)){
                    Intent installIntent = new Intent(Intent.ACTION_VIEW);
                    installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    installIntent.setDataAndType(getApkUri(_file.listFiles(),Util.getAPKNameFromUrl(apkUrl)),
                            "application/vnd.android.package-archive");
                    context.startActivity(installIntent);
                } else{
                    //todo
                }
            }
        };
        mContext.registerReceiver(broadcastReceiver, intentFilter);
    }
}
