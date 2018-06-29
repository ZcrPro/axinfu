package com.zhihuianxin.xyaxf.app.payment;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.zhihuianxin.xyaxf.app.AppConstant;
import modellib.service.PaymentService;
import modellib.thrift.base.PayMethod;
import com.google.gson.Gson;
import com.xyaxf.axpay.modle.PayRequest;
import com.zhihuianxin.xyaxf.BaseRealmActionBarActivity;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.ApiFactory;
import com.zhihuianxin.xyaxf.app.BaseSubscriber;
import com.zhihuianxin.xyaxf.app.RetrofitFactory;
import com.zhihuianxin.xyaxf.app.utils.NetUtils;
import com.zhihuianxin.xyaxf.app.utils.RxBus;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Vincent on 2016/12/15.
 */

public class CashierRFIDPayActivity extends BaseRealmActionBarActivity implements Serializable {
    public static final String EXTRA_BALANCE = "balance";
    public static final String EXTRA_PAY_REQUEST = "pay_request";
    public static final String EXTRA_PAY_METHOD = "pay_method";

    @InjectView(R.id.tv_balance)
    TextView mBalanceText;
    @InjectView(R.id.amountId)
    TextView mPayAmountText;
    @InjectView(R.id.rfidPay)
    Button mPayBtn;
    @InjectView(R.id.errorTxt)
    TextView mErrorText;
    @InjectView(R.id.error_view)
    View mErrorView;

    private String balance;
    private PayRequest payRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);

        balance = getIntent().getStringExtra(EXTRA_BALANCE);
        payRequest = (PayRequest) getIntent().getSerializableExtra(EXTRA_PAY_REQUEST);
        initViews();
    }

    private void initViews() {
        mBalanceText.setText(balance);
        mPayAmountText.setText(payRequest.amount);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.cashier_rfid_activity;
    }

    @OnClick(R.id.rfidPay)
    public void onBtnRFIDPay() {
        payRequest.pay_method = (PayMethod) getIntent().getExtras().getSerializable(EXTRA_PAY_METHOD);
        pay(payRequest);
    }

    private void pay(final PayRequest payRequest) {
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String, Object> map = new HashMap<>();
        map.put("pay_req", payRequest);
        PaymentService paymentService = ApiFactory.getFactory().create(PaymentService.class);
        paymentService.payOder(NetUtils.getRequestParams(this, map), NetUtils.getSign(NetUtils.getRequestParams(this, map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(this, true, null) {
                    @Override
                    public void onNext(Object o) {
                        CashierWayAdapter.PayOderResponse payOderResponse = new Gson().fromJson(o.toString(), CashierWayAdapter.PayOderResponse.class);
                        Intent intent = new Intent(CashierRFIDPayActivity.this, PaymentStatusActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString(PaymentStatusActivity.AMOUNT, payOderResponse.order.payment_amt);
                        bundle.putString(PaymentStatusActivity.NAME, payOderResponse.order.trade_summary + "-支付成功");
                        bundle.putSerializable(PaymentStatusActivity.WAY, payOderResponse.order.pay_method);
                        bundle.putString(PaymentStatusActivity.TIME, payOderResponse.order.order_time);
                        bundle.putString(PaymentStatusActivity.ODER, payOderResponse.order.order_no);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        finish();
                    }
                });
    }

    private void showErroeMsg(String errorMsg) {
        mErrorView.setVisibility(View.VISIBLE);
        mErrorText.setText(errorMsg);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            RxBus.getDefault().send("onPayCancelled");
            finish();
        }
        return true;
    }

    @Override
    public void onLeftButtonClick(View view) {
        super.onLeftButtonClick(view);
        RxBus.getDefault().send("onPayCancelled");
        finish();
    }
}
