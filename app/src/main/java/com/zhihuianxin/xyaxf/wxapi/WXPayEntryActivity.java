package com.zhihuianxin.xyaxf.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.zhihuianxin.xyaxf.App;
import com.zhihuianxin.xyaxf.app.utils.RxBus;

/**
 * Created by zcrpro on 2017/11/27.
 */
public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api = WXAPIFactory.createWXAPI(this, App.WXAPPID);
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
        Toast.makeText(getApplicationContext(),"onReq",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResp(BaseResp resp) {
        int code = resp.errCode;
        Log.d("WXPayEntryActivity", "微信code:" + new Gson().toJson(resp));
        Log.d("WXPayEntryActivity", "微信code:" + resp);
        if (code == 0){
            App.isNeedCheck =false;
            RxBus.getDefault().send("wechat_suc");
            finish();
        }

        if (code == -1){
            //错误
            App.isNeedCheck =false;
            Toast.makeText(this, "支付失败", Toast.LENGTH_SHORT).show();
            finish();
        }

        if (code == -2){
            App.isNeedCheck =false;
            Toast.makeText(this, "支付取消", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}