package com.zhihuianxin.xyaxf.app.unionqr_pay.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import modellib.thrift.unqr.NewUnionPayResult;
import modellib.thrift.unqr.NewUnionSwepRecordDetail;
import modellib.thrift.unqr.UPQRPayRecord;
import com.xyaxf.axpay.Util;
import com.zhihuianxin.xyaxf.App;
import com.zhihuianxin.xyaxf.BaseRealmActionBarActivity;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.unionqr_pay.contract.IunionGetUnionPayResultDetail;
import com.zhihuianxin.xyaxf.app.unionqr_pay.presenter.UnionGetUnionPayResultDetailPresenter;
import com.zhihuianxin.xyaxf.app.unionqr_pay.view.SwpeStrategyAdapter;

import java.text.DecimalFormat;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Vincent on 2017/12/7.
 */

public class UnionSwepPayResultActivity extends BaseRealmActionBarActivity implements IunionGetUnionPayResultDetail.IunionGetUnionPayResultView {
    public static final String EXTRA_ORDER = "swept_order_result";

    @InjectView(R.id.payforTxt)
    TextView payForTxt;
    @InjectView(R.id.payResultTxt)
    TextView payResult;
    @InjectView(R.id.amount)
    TextView amountTxt;
    @InjectView(R.id.couponid)
    TextView couponTxt;
    @InjectView(R.id.payWay)
    TextView payWayTxt;
    @InjectView(R.id.payTime)
    TextView payTimeTxt;
    @InjectView(R.id.next)
    Button mNextBtn;
    @InjectView(R.id.showpaydetail)
    TextView mPayDetailTxt;
    @InjectView(R.id.rightIcon)
    ImageView resultImg;
    @InjectView(R.id.errormsg)
    TextView errormsg;
    @InjectView(R.id.orderno)
    TextView orderNoTxt;
    @InjectView(R.id.remark)
    TextView remarkTxt;
    @InjectView(R.id.recyclerview)
    RecyclerView recyclerview;

    private NewUnionPayResult order;
    private IunionGetUnionPayResultDetail.IunionGetUnionPayResultPresenter presenter;

    @Override
    protected int getContentViewId() {
        return R.layout.unionpay_result_activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);

        order = (NewUnionPayResult) getIntent().getExtras().getSerializable(EXTRA_ORDER);
        new UnionGetUnionPayResultDetailPresenter(this, this);
        initViews();
    }


    private void initViews() {
        if (!Util.isEmpty(order.resp_code) && order.resp_code.equals("AS1800")) {
            mNextBtn.setText("回首页");
            payResult.setText("支付成功");
            resultImg.setBackgroundResource(R.drawable.upqrpay_success);
            errormsg.setVisibility(View.GONE);
        } else {
            mNextBtn.setText("返回");
            resultImg.setBackgroundResource(R.drawable.upqr_pay_error);
            payResult.setText("支付失败");
            errormsg.setVisibility(View.VISIBLE);
            errormsg.setText(order.resp_desc);
        }

        if (!Util.isEmpty(order.couponOffstAmt)) {
            couponTxt.setVisibility(View.VISIBLE);
            couponTxt.setText("优惠抵扣" + order.couponOffstAmt + "元");// 优惠金额
            DecimalFormat decimalFormat = new DecimalFormat("############0.00");
            amountTxt.setText("￥" + decimalFormat.format(Double.parseDouble(order.ori_amt)
                    - Double.parseDouble(order.couponOffstAmt)));// 真实支付的金额
        } else {
            couponTxt.setVisibility(View.GONE);
            amountTxt.setText("￥" + order.amount);
        }

        payForTxt.setText(order.trade_summary);
        payWayTxt.setText("付款方式:" + order.iss_ins_name + order.card_type_name + "(" + order.bankcard_no + ")");
        orderNoTxt.setText("订单号：" + order.order_no);
        if (!TextUtils.isEmpty(App.mAxLoginSp.getUnionReMark())) {
            remarkTxt.setVisibility(View.VISIBLE);
            remarkTxt.setText("备注：" + App.mAxLoginSp.getUnionReMark());
        } else {
            remarkTxt.setVisibility(View.GONE);
        }
        remarkTxt.setText("备注：" + App.mAxLoginSp.getUnionReMark());
        int[] timeItems = order.pay_time != null ? Util.getTimeItems(order.pay_time) : null;
        payTimeTxt.setText("交易时间:" + (timeItems != null ?
                String.format("%02d-%02d-%02d %02d:%02d:%02d", timeItems[0], timeItems[1], timeItems[2], timeItems[3], timeItems[4], timeItems[5]) : ""));
        mNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mNextBtn.getText().equals("返回")) {
                    finish();
                } else {
                    App.finishAllActivity();
                    finish();
                }
            }
        });
        mPayDetailTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.getPayResultDeatil(order.order_no);
            }
        });

        if (order.strategy!=null&&order.strategy.size()>0){
            recyclerview.setVisibility(View.VISIBLE);
            recyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true));
            recyclerview.setHasFixedSize(true);
            SwpeStrategyAdapter swpeStrategyAdapter = new SwpeStrategyAdapter(this,order.strategy,R.layout.strategy_list_item);
            recyclerview.setAdapter(swpeStrategyAdapter);
            swpeStrategyAdapter.notifyDataSetChanged();
        }

    }

    private String getPayStatus(String key) {
        String str;
        if (key.equals("unpay")) {
            str = "未支付";
        } else if (key.equals("paied")) {
            str = "已支付";
        } else if (key.equals("success")) {
            str = "已成功";
        } else if (key.equals("fail")) {
            str = "处理失败";
        } else if (key.equals("refunding")) {
            str = "退款中";
        } else if (key.equals("refundsucc")) {
            str = "已退款";
        } else if (key.equals("refundfail")) {
            str = "退款失败";
        } else if (key.equals("cancel")) {
            str = "取消";
        } else {
            str = "";
        }
        return str;
    }

    private NewUnionSwepRecordDetail tranToUnSerBody(UPQRPayRecord upqrPayRecord) {
        NewUnionSwepRecordDetail newCoupon = new NewUnionSwepRecordDetail();
        newCoupon.qr_code = upqrPayRecord.qr_code;
        newCoupon.status = upqrPayRecord.status;
        newCoupon.amount = upqrPayRecord.amount;
        newCoupon.merchant_id = upqrPayRecord.merchant_id;
        newCoupon.merchant_name = upqrPayRecord.merchant_name;
        newCoupon.orig_amt = upqrPayRecord.orig_amt;
        newCoupon.order_no = upqrPayRecord.order_no;
        newCoupon.order_time = upqrPayRecord.order_time;
        newCoupon.voucher_num = upqrPayRecord.voucher_num;
        newCoupon.order_type = upqrPayRecord.order_type;
        newCoupon.mode = upqrPayRecord.mode;

        if (upqrPayRecord.card != null) {
            newCoupon.bankid = upqrPayRecord.card.getId();
            newCoupon.bank_name = upqrPayRecord.card.getBank_name();
            newCoupon.card_type_name = upqrPayRecord.card.getCard_type_name();
            newCoupon.bankcard_no = upqrPayRecord.card.getCard_no();
            newCoupon.iss_ins_code = upqrPayRecord.card.getIss_ins_code();
            newCoupon.iss_ins_name = upqrPayRecord.card.getIss_ins_name();
            newCoupon.iss_ins_icon = upqrPayRecord.card.getIss_ins_icon();
        }
        if (upqrPayRecord.couponInfo != null && upqrPayRecord.couponInfo.size() > 0) {
            newCoupon.couponType = upqrPayRecord.couponInfo.get(0).type;
            newCoupon.couponOffstAmt = upqrPayRecord.couponInfo.get(0).offst_amt;
            newCoupon.couponDesc = upqrPayRecord.couponInfo.get(0).desc;
            newCoupon.couponAddnInfo = upqrPayRecord.couponInfo.get(0).addn_info;
            newCoupon.couponId = upqrPayRecord.couponInfo.get(0).id;
            newCoupon.coupunSpnsrId = upqrPayRecord.couponInfo.get(0).spnsr_id;
        }

        return newCoupon;
    }

    @Override
    public void getPayResultDeatilResult(UPQRPayRecord pay_record_detail) {
        Intent intent = new Intent(this, UnionSweptRecordDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(UnionSweptRecordDetailActivity.EXTRA_UNION_SWEP_DETAIL, tranToUnSerBody(pay_record_detail));
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    @Override
    public void setPresenter(IunionGetUnionPayResultDetail.IunionGetUnionPayResultPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void loadStart() {
    }

    @Override
    public void loadError(String errorMsg) {
    }

    @Override
    public void loadComplete() {
    }
}
