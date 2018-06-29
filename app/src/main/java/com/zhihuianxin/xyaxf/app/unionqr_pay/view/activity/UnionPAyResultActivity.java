package com.zhihuianxin.xyaxf.app.unionqr_pay.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import modellib.thrift.payment.PaymentOrder;
import modellib.thrift.unqr.NewUnionSwepRecordDetail;
import modellib.thrift.unqr.UPBankCard;
import modellib.thrift.unqr.UPQRPayRecord;
import com.zhihuianxin.xyaxf.app.base.axutil.Util;
import com.zhihuianxin.xyaxf.App;
import com.zhihuianxin.xyaxf.BaseRealmActionBarActivity;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.unionqr_pay.contract.IunionGetUnionPayResultDetail;
import com.zhihuianxin.xyaxf.app.unionqr_pay.entity.UnionPayEntity;
import com.zhihuianxin.xyaxf.app.unionqr_pay.presenter.UnionGetUnionPayResultDetailPresenter;
import com.zhihuianxin.xyaxf.app.unionqr_pay.view.fragment.UnionCashierFragment;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Vincent on 2017/11/10.
 */

public class UnionPAyResultActivity extends BaseRealmActionBarActivity implements IunionGetUnionPayResultDetail.IunionGetUnionPayResultView{
    public static final String EXTRA_UPQR_PAYORDER = "upqrOrder";
    @InjectView(R.id.next)
    Button mNextBtn;
    @InjectView(R.id.amount)
    TextView amountTxt;
    @InjectView(R.id.couponid)
    TextView couponTxt;
    @InjectView(R.id.payWay)
    TextView payWayTxt;
    @InjectView(R.id.showpaydetail)
    TextView mPayDetailTxt;
    @InjectView(R.id.payforTxt)
    TextView payForTxt;
    @InjectView(R.id.payTime)
    TextView payTimeTxt;
    @InjectView(R.id.orderno)
    TextView orderNoTxt;
    @InjectView(R.id.remark)
    TextView remarkTxt;

    private UnionPayEntity entity;
    private PaymentOrder order;
    private IunionGetUnionPayResultDetail.IunionGetUnionPayResultPresenter presenter;

    @Override
    protected int getContentViewId() {
        return R.layout.unionpay_result_activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
        initView();
    }

    private void initView(){
        new UnionGetUnionPayResultDetailPresenter(this,this);
        order = (PaymentOrder) getIntent().getExtras().getSerializable(EXTRA_UPQR_PAYORDER);
        entity = (UnionPayEntity) getIntent().getExtras().getSerializable(UnionCashierFragment.EXTRA_ENTITY);
        mNextBtn.setText("完成");
        if(entity != null){
            payForTxt.setText("向"+entity.getUpqrOrder().payee_info.name+"付款");
            int[] timeItems = order.order_time != null? Util.getTimeItems(order.order_time): null;
            payTimeTxt.setText(timeItems!=null ?
                    String.format("%02d-%02d-%02d %02d:%02d:%02d",timeItems[0],timeItems[1],timeItems[2],timeItems[3],timeItems[4],timeItems[5]) : "");
            payTimeTxt.setText("交易时间: "+payTimeTxt.getText());
            mNextBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            amountTxt.setText("￥" + order.payment_amt);
            if(entity.getUpCoupon() != null){
                couponTxt.setText("优惠已抵扣 "+entity.getUpCoupon().offst_amt+"元");
            } else{
                couponTxt.setText("");
            }
            UPBankCard upBankCard = entity.getBankCards().get(0);
            for(int i = 0;i < entity.getBankCards().size();i++){
                if(App.mAxLoginSp.getUnionSelBankId().equals(entity.getBankCards().get(i).getId())){
                    upBankCard = entity.getBankCards().get(i);
                }
            }
            payWayTxt.setText("付款方式："+upBankCard.getIss_ins_name()+" "+upBankCard.getCard_type_name()+"("+upBankCard.getCard_no()+")");
            orderNoTxt.setText("订单号："+entity.getUpqrOrder().tn);
            remarkTxt.setText("备注："+App.mAxLoginSp.getUnionReMark());
        }

        mNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mPayDetailTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               presenter.getPayResultDeatil(order.order_no);
            }
        });
    }

    @Override
    public void getPayResultDeatilResult(UPQRPayRecord pay_record_detail) {
        Intent intent = new Intent(this,UnionSweptRecordDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(UnionSweptRecordDetailActivity.EXTRA_UNION_SWEP_DETAIL,tranToUnSerBody(pay_record_detail));
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    private NewUnionSwepRecordDetail tranToUnSerBody(UPQRPayRecord upqrPayRecord){
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

        if(upqrPayRecord.card != null){
            newCoupon.bankid = upqrPayRecord.card.getId();
            newCoupon.bank_name = upqrPayRecord.card.getBank_name();
            newCoupon.card_type_name = upqrPayRecord.card.getCard_type_name();
            newCoupon.bankcard_no = upqrPayRecord.card.getCard_no();
            newCoupon.iss_ins_code = upqrPayRecord.card.getIss_ins_code();
            newCoupon.iss_ins_name = upqrPayRecord.card.getIss_ins_name();
            newCoupon.iss_ins_icon = upqrPayRecord.card.getIss_ins_icon();
        }
        if(upqrPayRecord.couponInfo != null && upqrPayRecord.couponInfo.size() > 0){
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
    public void setPresenter(IunionGetUnionPayResultDetail.IunionGetUnionPayResultPresenter presenter) {this.presenter = presenter;}
    @Override
    public void loadStart() {}
    @Override
    public void loadError(String errorMsg) {}
    @Override
    public void loadComplete() {}
}
