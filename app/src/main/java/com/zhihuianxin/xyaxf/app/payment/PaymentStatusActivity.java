package com.zhihuianxin.xyaxf.app.payment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.axinfu.modellib.thrift.base.PayMethod;
import com.zhihuianxin.xyaxf.BaseRealmActionBarActivity;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.utils.RxBus;

import java.text.DecimalFormat;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by zcrpro on 2016/12/13.
 */

public class PaymentStatusActivity extends BaseRealmActionBarActivity {

    @InjectView(R.id.fee_name)
    TextView feeName;
    @InjectView(R.id.fee_amount)
    TextView feeAmount;
    @InjectView(R.id.fee_time)
    TextView feeTime;
    @InjectView(R.id.fee_oder)
    TextView feeOder;
    @InjectView(R.id.fee_way)
    TextView feeWay;
    @InjectView(R.id.btn_ok)
    Button btnOk;

    public static final String AMOUNT = "AMOUNT";
    public static final String NAME = "NAME";
    public static final String TIME = "TIME";
    public static final String ODER = "ODER";
    public static final String WAY = "WAY";

    @Override
    protected int getContentViewId() {
        return R.layout.payment_status_activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            initData(bundle);
        }
    }

    @SuppressLint("SetTextI18n")
    private void initData(Bundle bundle) {
        String name = bundle.getString(NAME);
        String time = bundle.getString(TIME);
        String oder = bundle.getString(ODER);
        final PayMethod way = (PayMethod) bundle.getSerializable(WAY);
        String amount = bundle.getString(AMOUNT);

        assert way != null;
        if (way.channel.equals("UnionPay")) {
            feeWay.setText("银联在线");
        } else if (way.channel.equals("RFID")) {
            feeWay.setText("一卡通余额");
        } else if (way.channel.equals("CCBWapPay")) {
            feeWay.setText("建设银行支付");
        } else if (way.channel.equals("NoNeed")) {
            feeWay.setText("溢缴款抵扣");
        } else if (way.channel.equals("QuickPay")) {
            feeWay.setText("快捷支付");
        } else if (way.channel.equals("AliAppPay")) {
            feeWay.setText("支付宝");
        } else if (way.channel.equals("WxAppPay")) {
            feeWay.setText("微信支付");
        } else if (way.channel.equals("UPTokenPay")) {
            if (way.card != null) {
                feeWay.setText(way.card.getIss_ins_name() + way.card.getCard_type_name() + " " + "(" + way.card.getCard_no() + ")");
            }
        } else if (way.channel.equals("GuiYangCreditLoanPay")) {
            feeWay.setText("安心信用付");
        }
        if (time != null) {
            feeTime.setText(String.format("%s-%s-%s %s:%s:%s",
                    time.substring(0, 4), time.substring(4, 6), time.substring(6, 8), time.substring(8, 10), time.substring(10, 12), time.substring(12, 14)));
        }
        feeAmount.setText(getDoubleNum(amount) + "元");
        feeOder.setText(oder);
        feeName.setText(name);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(PaymentStatusActivity.this, MainActivity.class));
                RxBus.getDefault().send("succeed");
                RxBus.getDefault().send("succeed2");
                finish();
            }
        });
    }

    private String getDoubleNum(String amt) {
        try {
            DecimalFormat decimalFormat = new DecimalFormat("############0.00");
            return decimalFormat.format(Float.parseFloat(amt));
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.reset(this);
    }
}
