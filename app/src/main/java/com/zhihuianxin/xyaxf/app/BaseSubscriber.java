package com.zhihuianxin.xyaxf.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.zhihuianxin.axutil.Util;
import com.zhihuianxin.xyaxf.MainActivity;
import com.zhihuianxin.xyaxf.app.view.LoadingDialog;

import java.io.IOException;

import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;

/**
 * Created by Vincent on 2016/12/9.
 */

public abstract class BaseSubscriber <T> extends Subscriber<T> {
    private LoadingDialog dialog;
    protected Context mContext;
    private BaseView mView;
    private boolean isNotCanCanceld;

    public BaseSubscriber(Context context, boolean isShowDialog,BaseView view) {
        dialog = new LoadingDialog(context);
        this.mContext = context;
        mView = view;
        if(isLegalView()){
            mView.loadStart();
        }
        if(isShowDialog){
            dialog.show();
        }
    }

    public BaseSubscriber(Context context, boolean isShowDialog,boolean isNotCanCanceld,BaseView view) {
        if (isNotCanCanceld){
            dialog = new LoadingDialog(context,true);
        }else {
            dialog = new LoadingDialog(context);
        }
        this.isNotCanCanceld = isNotCanCanceld;
        this.mContext = context;
        mView = view;
        if(isLegalView()){
            mView.loadStart();
        }
        if(isShowDialog){
            dialog.show();
        }
    }

    @Override
    public void onCompleted() {
        dialog.dismiss();
        if(isLegalView()){
            mView.loadComplete();
        }
    }

    private boolean isLegalView(){
        return mView != null;
    }

    @Override
    public void onError(final Throwable e) {
        if (e instanceof HttpException) {
            if(e.getMessage().contains("504")){
                showLocalToast("请求超时");
            } else{
                showLocalToast(e.getMessage());// 网络异常，请稍后重试!
            }
        } else if (e instanceof IOException) {
            if(!Util.isNetworkConnected(mContext)){
                showLocalToast("请检查您的网络连接情况！");
            } else{
                showLocalToast("网络连接超时！");
            }
        } else if (e instanceof ApiException) {
            ApiException exception = (ApiException) e;
            if (exception.mErrorCode == AppConstant.RELOGIN) {
                mContext.sendBroadcast(new Intent(MainActivity.RELOGIN_BROADCAST));
                ((Activity)mContext).finish();
            } else {
                showLocalToast(exception.getMessage());
            }
        } else{
            showLocalToast(e.getMessage());
        }

        if(isLegalView()){
            mView.loadError(e.getMessage());
        }
        dialog.dismiss();
    }

    private void showLocalToast(String msg){
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }
}
