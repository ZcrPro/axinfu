package com.zhihuianxin.xyaxf.app.home.qrcode;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.zhihuianxin.xyaxf.app.AppConstant;
import com.axinfu.modellib.service.PaymentService;
import com.axinfu.modellib.thrift.base.PayChannel;
import com.axinfu.modellib.thrift.payment.OrderRecord;
import com.axinfu.modellib.thrift.payment.QROrder;
import com.google.gson.Gson;
import com.xyaxf.axpay.modle.PayFor;
import com.xyaxf.axpay.modle.PayRequest;
import com.xyaxf.axpay.modle.PaymentInfo;
import com.zhihuianxin.xyaxf.BaseRealmActionBarActivity;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.ApiFactory;
import com.zhihuianxin.xyaxf.app.BaseSubscriber;
import com.zhihuianxin.xyaxf.app.RetrofitFactory;
import com.zhihuianxin.xyaxf.app.pay.MyPayActivity;
import com.zhihuianxin.xyaxf.app.utils.NetUtils;
import com.zhihuianxin.xyaxf.app.utils.RxBus;

import java.util.HashMap;
import java.util.Map;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by zcrprozcrpro on 2017/5/5.
 */

public class QRPayActivity extends BaseRealmActionBarActivity {

    public static final String EXTRA_URL = "url";
    public static final String EXTRA_TITLE = "title";
    public static final String EXTRA_REQUEST_CODE = "request_code";

    public WebView mWebView;

    private ProgressBar progressBar;
    private int mRequestCode;

    private String mURL;
    private String mTITLE;
    private WebChromeClient mWebChromeClient;
    private boolean isFrist;
    private Subscription rxSubscription;

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_pay_view);
        mWebView = (WebView) findViewById(R.id.web_view);

        isFrist = true;

        mURL = getIntent().getStringExtra(EXTRA_URL);
        mTITLE = getIntent().getStringExtra(EXTRA_TITLE);
        mRequestCode = getIntent().getIntExtra(EXTRA_REQUEST_CODE, 0);

        mWebView = (WebView) findViewById(R.id.web_view);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setUserAgentString(mWebView.getSettings().getUserAgentString() + " AXinFu.App.V3");
        mWebView.addJavascriptInterface(new axinfuApp(), "axinfuApp");
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebChromeClient = new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (progressBar != null) {
                    if (newProgress < 100) {
                        progressBar.setVisibility(View.VISIBLE);
                        progressBar.setProgress(newProgress);
                    } else if (newProgress == 100) {
                        progressBar.setProgress(newProgress);
                        progressBar.setVisibility(View.GONE);

                    }
                }
                super.onProgressChanged(view, newProgress);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
//                setTitle(title);
            }
        };

        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                mWebView.setVisibility(View.VISIBLE);
                if (isFrist) {
                    mWebView.loadUrl("javascript:paycenter_init()");
                }
                super.onPageFinished(view, url);
            }
        });

        mWebView.setWebChromeClient(mWebChromeClient);

        if (!TextUtils.isEmpty(mURL) && mURL.endsWith("apk")) {
            try {
                Intent viewIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mURL));
                startActivity(viewIntent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (mURL.startsWith("weixin://") || mURL.startsWith("alipays://") || mURL.startsWith("mailto://") || mURL.startsWith("tel://")) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mURL));
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            mWebView.loadUrl(mURL);
        }

        if (mTITLE != null)
            setTitle(mTITLE);

        rxSubscription = RxBus.getDefault().toObservable().subscribe(new Action1<Object>() {
            @Override
            public void call(Object event) {
                if (event.equals("succeed")) {
                    mWebView.loadUrl("javascript:paycenter_callback('0', '支付成功')");
                } else if (event.equals("onPayCancelled")) {
                    Log.d("QRPayActivity", "1ss");
                    mWebView.loadUrl("javascript:paycenter_callback('2', '用户已取消支付')");
                } else if (event.equals("fail")) {
                    Log.d("TAG", "call:失败");
                    Log.d("QRPayActivity", "2ss");
                    mWebView.loadUrl("javascript:paycenter_callback('1', '支付失败')");
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (mWebView.canGoBack()) {
                mWebView.goBack();
            } else {
                finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public class axinfuApp {
        @JavascriptInterface
        public void pay(int channel, String payData, String orderInfo) {
            Log.d("axinfuApp", "logd");
            isFrist = false;
            QROrder oder = new Gson().fromJson(orderInfo, QROrder.class);
            PayRequest payRequest = new PayRequest();
            com.axinfu.modellib.thrift.base.PayMethod payMethod = new com.axinfu.modellib.thrift.base.PayMethod();
            payMethod.channel = PayChannel.UnionPay.name();
            payRequest.pay_method = payMethod;
            PaymentInfo paymentInfo = new PaymentInfo();
            paymentInfo.pay_method = payRequest.pay_method;
            paymentInfo.payment_amt = oder.getAmount();
            paymentInfo.channel_orderno = oder.getNo();
            paymentInfo.pay_info = "{" + "tn:" + payData + "}";
            paymentInfo.fee_name = oder.getSummary() + "-支付成功";
            paymentInfo.fee_time = oder.getTime();
            Intent intent = new Intent(QRPayActivity.this, MyPayActivity.class);
            intent.putExtra(MyPayActivity.EXTRA_PAYMENT_INFO, paymentInfo);
            intent.putExtra(MyPayActivity.EXTRA_AMOUNT, oder.getAmount());
            intent.putExtra(MyPayActivity.EXTRA_PAY_FOR, PayFor.ScanPay);

            OrderRecord orderRecord = new OrderRecord();
            orderRecord.order_no = oder.getNo();
            orderRecord.order_time = oder.getTime();
            orderRecord.pay_amt = oder.getAmount();
            orderRecord.pay_channel = PayChannel.UnionPay;
            orderRecord.payfor = PayFor.ScanPay.name();
            orderRecord.trade_summary = oder.getSummary();
            pays(orderRecord);
            startActivity(intent);

        }
    }

    private void pays(final OrderRecord orderRecord) {
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String, Object> map = new HashMap<>();
        map.put("order_record", orderRecord);
        PaymentService paymentService = ApiFactory.getFactory().create(PaymentService.class);
        paymentService.CommitOrder(NetUtils.getRequestParams(QRPayActivity.this, map), NetUtils.getSign(NetUtils.getRequestParams(QRPayActivity.this, map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(QRPayActivity.this, true, null) {

                    @Override
                    public void onNext(Object o) {

                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isFrist = true;
        if (rxSubscription != null)
            rxSubscription.unsubscribe();
    }

    @Override
    protected int getContentViewId() {
        return R.layout.web_view;
    }

    @Override
    public boolean leftButtonEnabled() {
        return true;
    }

    @Override
    public void onLeftButtonClick(View view) {
        finish();
    }

    @Override
    public int getLeftButtonImageId() {
        return R.drawable.back;
    }


}

