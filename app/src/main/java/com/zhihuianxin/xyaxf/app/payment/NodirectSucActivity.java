package com.zhihuianxin.xyaxf.app.payment;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.axinfu.modellib.thrift.payment.PaymentOrder;
import com.google.gson.Gson;
import com.zhihuianxin.axutil.Util;
import com.zhihuianxin.xyaxf.BaseRealmActionBarActivity;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.ocp.OcpPaySucActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by zcrpro on 2018/3/13.
 */

public class NodirectSucActivity extends BaseRealmActionBarActivity {

    @InjectView(R.id.amount)
    TextView amount;
    @InjectView(R.id.youhui)
    TextView youhui;
    @InjectView(R.id.way)
    TextView way;
    @InjectView(R.id.paytime)
    TextView paytime;
    @InjectView(R.id.payno)
    TextView payno;
    @InjectView(R.id.remark)
    TextView remark;
    @InjectView(R.id.next)
    Button next;

    private PaymentOrder paymentOrder;

    @Override
    protected int getContentViewId() {
        return R.layout.no_direct_pay_suc_activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.inject(this);
        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        if (bundle.getSerializable("PaymentOrder") != null) {
            paymentOrder = (PaymentOrder) bundle.getSerializable("PaymentOrder");
        }

        if (paymentOrder != null) {
            amount.setText("¥ " + paymentOrder.payment_amt);
            way.setText(paymentOrder.pay_method.channel);
            int[] timeItems = paymentOrder.order_time != null ? Util.getTimeItems(paymentOrder.order_time) : null;
            paytime.setText(timeItems == null ? "" : String.format("%04d-%02d-%02d %02d:%02d:%02d",
                    timeItems[0], timeItems[1], timeItems[2], timeItems[3], timeItems[4], timeItems[5]));

            try {
                OcpPaySucActivity.UPQRQuickPayData upqrQuickPayData = new Gson().fromJson(paymentOrder.pay_data, OcpPaySucActivity.UPQRQuickPayData.class);
                if (upqrQuickPayData != null && upqrQuickPayData.up_coupon_info.size() != 0)
                    youhui.setText("优惠抵扣：" + upqrQuickPayData.up_coupon_info.get(0).offst_amt + "元");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
