package com.zhihuianxin.xyaxf.app.ocp;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.axinfu.modellib.thrift.payment.PaymentOrder;
import com.google.gson.Gson;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.zhihuianxin.xyaxf.app.base.axutil.Util;
import com.zhihuianxin.xyaxf.App;
import com.zhihuianxin.xyaxf.BaseRealmActionBarActivity;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.ocp.adapter.StrategyAdapter;
import com.zhihuianxin.xyaxf.app.utils.RxBus;
import com.zhihuianxin.xyaxf.app.view.GifView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by zcrpro on 2017/11/24.
 */

public class OcpPaySucActivity extends BaseRealmActionBarActivity {

    @InjectView(R.id.amount)
    TextView amount;
    @InjectView(R.id.payshop)
    TextView payshop;
    @InjectView(R.id.paytime)
    TextView paytime;
    @InjectView(R.id.next)
    Button next;
    @InjectView(R.id.youhui)
    TextView youhui;
    @InjectView(R.id.payway)
    TextView payway;
    @InjectView(R.id.payno)
    TextView payno;
    @InjectView(R.id.payremark)
    TextView payremark;
    @InjectView(R.id.gif_view)
    GifView gifView;
    @InjectView(R.id.recyclerview)
    RecyclerView recyclerview;

    private PaymentOrder paymentOrder;
    private QrResultActivity.PayInfo payInfo;

    private String pay_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);

        recyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerview.setHasFixedSize(true);

        loadStrategy();


        Bundle bundle = getIntent().getExtras();
        assert bundle != null;

        if (bundle.getSerializable("PaymentOrder") != null && bundle.getSerializable("PayInfo") != null) {
            paymentOrder = (PaymentOrder) bundle.getSerializable("PaymentOrder");
            payInfo = (QrResultActivity.PayInfo) bundle.getSerializable("PayInfo");
        }

        if (bundle.getSerializable("PaymentOrder") != null && bundle.getString("pay_name") != null) {
            paymentOrder = (PaymentOrder) bundle.getSerializable("PaymentOrder");
            pay_name = bundle.getString("pay_name");
        }

        if (paymentOrder != null) {
            amount.setText("¥ " + paymentOrder.payment_amt);
            if (payInfo == null && pay_name != null) {
                payshop.setText(pay_name);
            } else {
                payshop.setText(payInfo.payee_info.name);
            }
            int[] timeItems = paymentOrder.order_time != null ? Util.getTimeItems(paymentOrder.order_time) : null;
            paytime.setText(timeItems == null ? "" : String.format(paytime.getText().toString().trim() + "   " + "%04d-%02d-%02d %02d:%02d:%02d",
                    timeItems[0], timeItems[1], timeItems[2], timeItems[3], timeItems[4], timeItems[5]));

            try {
                UPQRQuickPayData upqrQuickPayData = new Gson().fromJson(paymentOrder.pay_data, UPQRQuickPayData.class);
                if (upqrQuickPayData != null && upqrQuickPayData.up_coupon_info.size() != 0)
                    youhui.setText("优惠抵扣：" + upqrQuickPayData.up_coupon_info.get(0).offst_amt + "元");
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (paymentOrder.pay_method.channel.equals("UnionPay")) {
                payway.setText(payway.getText().toString().trim() + "   " + "银联在线");
            } else if (paymentOrder.pay_method.channel.equals("RFID")) {
                payway.setText(payway.getText().toString().trim() + "   " + "一卡通余额");
            } else if (paymentOrder.pay_method.channel.equals("CCBWapPay")) {
                payway.setText(payway.getText().toString().trim() + "   " + "建设银行wap支付");
            } else if (paymentOrder.pay_method.channel.equals("NoNeed")) {
                payway.setText(payway.getText().toString().trim() + "   " + "溢缴款抵扣");
            } else if (paymentOrder.pay_method.channel.equals("QuickPay")) {
                payway.setText(payway.getText().toString().trim() + "   " + "快捷支付");
            } else if (paymentOrder.pay_method.channel.equals("AliAppPay")) {
                payway.setText(payway.getText().toString().trim() + "   " + "支付宝");
            } else if (paymentOrder.pay_method.channel.equals("WxAppPay")) {
                payway.setText(payway.getText().toString().trim() + "   " + "微信支付");
            } else if (paymentOrder.pay_method.channel.equals("UPTokenPay") || paymentOrder.pay_method.channel.equals("AxfQRPay") || paymentOrder.pay_method.channel.equals("UPQRQuickPay")) {
                payway.setText(payway.getText().toString().trim() + "   " + paymentOrder.pay_method.card.getIss_ins_name() + paymentOrder.pay_method.card.getCard_type_name() + " " + "(" + paymentOrder.pay_method.card.getCard_no() + ")");
            } else if (paymentOrder.pay_method.channel.equals("QuickPay")) {
                payway.setText(payway.getText().toString().trim() + "   " + "快捷支付");
            }

            payno.setText(payno.getText().toString().trim() + "   " + paymentOrder.order_no);
            if (!TextUtils.isEmpty(App.mAxLoginSp.getUnionReMark())) {
                payremark.setText(payremark.getText().toString().trim() + "   " + App.mAxLoginSp.getUnionReMark());
            } else {
                payremark.setVisibility(View.GONE);
            }

        }

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RxBus.getDefault().send("succeed");
                finish();
            }
        });

        gifView.setMovieResource(R.raw.pay_suc);

    }

    private void loadStrategy() {
        List<PricingStrategy> pricingStrategies = SQLite.select().from(PricingStrategy.class).queryList();// 查询所有记录
        if (pricingStrategies != null && pricingStrategies.size() > 0) {
            StrategyAdapter strategyAdapter =new StrategyAdapter(OcpPaySucActivity.this,pricingStrategies,R.layout.strategy_list_item);
            recyclerview.setAdapter(strategyAdapter);
            strategyAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected int getContentViewId() {
        return R.layout.ocp_pay_suc_activity;
    }

    public class UPQRQuickPayData {
        public String amt;                                        // 交易金额
        public String orig_amt;                               // 交易原金额
        public List<UPCoupon> up_coupon_info;                    // 银联优惠信息
    }

    public class UPCoupon {
        public String type;        // 类型
        public String spnsr_id;    //
        public String offst_amt;    // 优惠金额
        public String id;        // ID
        public String desc;        // 描述
        public String addn_info;    // 附加信息
    }

    @Override
    public void onLeftButtonClick(View view) {
        super.onLeftButtonClick(view);
    }

}
