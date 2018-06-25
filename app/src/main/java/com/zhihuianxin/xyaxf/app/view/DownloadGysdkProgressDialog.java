package com.zhihuianxin.xyaxf.app.view;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.zhihuianxin.axutil.Util;
import com.zhihuianxin.xyaxf.App;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.service.DownloadAPKService;

import static com.zhihuianxin.xyaxf.app.view.DownloadGysdkDialog.INSTALL_GYAPK_NAME;

/**
 * Created by Vincent on 2018/4/18.
 */

public class DownloadGysdkProgressDialog extends Dialog{
    private Context mContext;
    private ProgressBar progressBar;
    private TextView mStatusTxt,progressTxt;
    private View clickView;

    public DownloadGysdkProgressDialog(@NonNull Context context) {
        super(context,R.style.logoutDialog);
        mContext = context;

        IntentFilter filter = new IntentFilter(DownloadAPKService.BROADCAST_AKP_DOWNLOAD);
        mContext.registerReceiver(GysdkReceiver,filter);
    }

    public void setStart(){
        isPause = false;
    }

    BroadcastReceiver GysdkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(!isPause){
                if(!isShowing()){
                    show();
                }

                Message msg = new Message();
                msg.setData(intent.getExtras());
                handler.sendMessage(msg);
            } else {
                cancel();
            }
        }
    };

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            if(bundle.getInt(DownloadAPKService.APK_DOWNLOAD_TYPE) == 1){
                mStatusTxt.setText("安装插件");
                setCanceledOnTouchOutside(true);
            } else{
                mStatusTxt.setText("暂时关闭");//
                setCanceledOnTouchOutside(false);
            }

            progressTxt.setText(bundle.getInt(DownloadAPKService.APK_DOWNLOAD_PROGRESS)+"%");
            progressBar.setProgress(bundle.getInt(DownloadAPKService.APK_DOWNLOAD_PROGRESS));

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.download_gysdk_progress_dialog);

        progressTxt = (TextView) findViewById(R.id.progressindex);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        mStatusTxt = (TextView) findViewById(R.id.statustxt);
        clickView = findViewById(R.id.dialogclick);
        clickView.setOnClickListener(clickListener);
    }

    private boolean isPause = false;
    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            isPause = true;
            if(mStatusTxt.getText().equals("安装插件")){
                Intent intent = new Intent(DownloadGysdkDialog.INSTALL_GY_APK);
                Bundle bundle = new Bundle();
                bundle.putString(INSTALL_GYAPK_NAME, Util.getAPKNameFromUrl(App.mAxLoginSp.getGysdkUrl()));
                intent.putExtras(bundle);
                mContext.sendBroadcast(intent);
            }
            cancel();
        }
    };
}
