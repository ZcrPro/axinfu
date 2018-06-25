package com.zhihuianxin.xyaxf.app.payment;

import android.content.Context;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.zhihuianxin.xyaxf.app.AppConstant;
import com.axinfu.modellib.service.PaymentService;
import com.axinfu.modellib.thrift.payment.CCBWapPay;
import com.axinfu.modellib.thrift.payment.PayResult;
import com.google.gson.Gson;
import com.xyaxf.axpay.Util;
import com.xyaxf.axpay.modle.PayFor;
import com.xyaxf.axpay.modle.PaymentInfo;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.ApiFactory;
import com.zhihuianxin.xyaxf.app.BaseSubscriber;
import com.zhihuianxin.xyaxf.app.RetrofitFactory;
import com.zhihuianxin.xyaxf.app.WebPageActivity;
import com.zhihuianxin.xyaxf.app.utils.NetUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class CCBWapPayActivity extends WebPageActivity {
    public static final String EXTRA_PAYMENT_INFO = "payment_info";
    public static final String EXTRA_PAY_FOR = "pay_for";
    private static final String TAG = "CCBWapPayActivity";

//    private WebView mWebView;
    private PaymentInfo mPaymentInfo;
    private PayFor mPayFor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPaymentInfo = (PaymentInfo) getIntent().getSerializableExtra(EXTRA_PAYMENT_INFO);
        mPayFor = (PayFor) getIntent().getSerializableExtra(EXTRA_PAY_FOR);

        CCBWapPay payInfo = new Gson().fromJson(mPaymentInfo.pay_info, CCBWapPay.class);

//        mWebView = new WebView(this);
//        setContentView(getWebView());

        getWebView().getSettings().setJavaScriptEnabled(true);
        getWebView().getSettings().setAppCacheEnabled(true);
        getWebView().setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed(); // Ignore SSL certificate errors
            }
        });

        getWebView().setWebChromeClient(new WebChromeClient() {

        });

        String htmlData;
        try {
            htmlData = Util.getString(this, "raw://ccb_wap_pay_template", "utf-8");
        } catch (IOException e) {
            // ignore
            Util.showToastLong(this, "加载支付页面出现了一点状况...");
            finish();

            return;
        }

        htmlData = String.format(htmlData, payInfo.post_to_url, payInfo.json, payInfo.signature);

        getWebView().loadData(htmlData, "text/html", "utf-8");
    }

    @Override
    public void onLeftButtonClick(View view) {
        showExitConfirmDialog();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (getWebView().canGoBack()) {
                getWebView().goBack();
            } else {
                showExitConfirmDialog();
            }

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    private void showExitConfirmDialog() {
        SelectPayResultDialog dlg = new SelectPayResultDialog(this) {
            @Override
            public void onBtnPaySuccessClick(View view) {
                super.onBtnPaySuccessClick(view);

                pay(mPayFor, mPaymentInfo.channel_orderno);
            }

            @Override
            public void onBtnExitClick(View view) {
                super.onBtnExitClick(view);

                finish();
            }
        };
        dlg.show();
    }

    private void pay(PayFor pay_for, String order_no) {
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String, Object> map = new HashMap<>();
        map.put("pay_for", pay_for);
        map.put("order_no", order_no);
        PaymentService paymentService = ApiFactory.getFactory().create(PaymentService.class);
        paymentService.getPayResult(NetUtils.getRequestParams(this, map), NetUtils.getSign(NetUtils.getRequestParams(this, map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(this, true, null) {
                    @Override
                    public void onNext(Object o) {
                        GetPayResultResponse getPayResultResponse = new Gson().fromJson(o.toString(), GetPayResultResponse.class);
                        if (getPayResultResponse.result == PayResult.Payed) {
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            SimpleDialog dlg = new SimpleDialog(CCBWapPayActivity.this);
                            dlg.setTitle("提示");
                            dlg.setMessage("未查询到支付信息, 稍后您可以从我的账本中查询支付状态. ");
                            dlg.setPositiveButton("确定", null);
                            dlg.show();
                        }
                    }
                });
    }

    private class SelectPayResultDialog extends SimpleDialog {


        public SelectPayResultDialog(Context context) {
            super(context);

            setTitle("是否已经完成支付? ");

            mBtnClose.setImageResource(R.drawable.h5_close);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            setContentView(R.layout.ccb_wap_pay_select_pay_result_dialog);

            findViewById(R.id.btn_pay_success).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBtnPaySuccessClick(v);
                }
            });

            findViewById(R.id.btn_stay).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBtnStayClick(v);
                }
            });

            findViewById(R.id.btn_exit).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBtnExitClick(v);
                }
            });
        }

        public void onBtnPaySuccessClick(View view) {
            pay(mPayFor, mPaymentInfo.channel_orderno);
        }

        public void onBtnStayClick(View view) {
            dismiss();
        }

        public void onBtnExitClick(View view) {
            dismiss();
            CCBWapPayActivity.this.finish();
        }
    }

    @Override
    public String getViewName() {
        return "ccb_wap_pay";
    }

    public static class GetPayResultResponse {
        public PayResult result;
    }
}
