package com.zhihuianxin.xyaxf.app.unionqr_pay.view.activity;

import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import modellib.thrift.unqr.NewUnionSwepRecordDetail;
import modellib.thrift.unqr.UPQROrderType;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.zhihuianxin.xyaxf.app.base.axutil.Util;
import com.zhihuianxin.xyaxf.BaseRealmActionBarActivity;
import com.zhihuianxin.xyaxf.R;

import java.text.DecimalFormat;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Vincent on 2017/12/7.
 */

public class UnionSweptRecordDetailActivity extends BaseRealmActionBarActivity{
    public static final String EXTRA_UNION_SWEP_DETAIL = "swep_record_detail";
    @InjectView(R.id.amt)
    TextView amt;
    @InjectView(R.id.bigAmt)
    TextView bigAmt;
    @InjectView(R.id.orderStatus)
    TextView orderStatus;
    @InjectView(R.id.orderType)
    TextView ordertype;
    @InjectView(R.id.ordercouponTxt)
    TextView ordercouponTxt;
    @InjectView(R.id.ordercouponAmt)
    TextView ordercouponAmt;
    @InjectView(R.id.payTxt)
    TextView payTxt;
    @InjectView(R.id.getPayTxt)
    TextView getPayTxt;
    @InjectView(R.id.payTime)
    TextView payTime;
    @InjectView(R.id.order_no)
    TextView order_no;
    @InjectView(R.id.order_pzh)
    TextView order_pzh;
    @InjectView(R.id.couponTxtViewId)
    View couponTxtView;
    @InjectView(R.id.ordercouponTxt)
    TextView couponTxtTxt;
    @InjectView(R.id.couponAmtViewId)
    View couponAmtView;
    @InjectView(R.id.ordercouponAmt)
    TextView couponAmtTxt;
    @InjectView(R.id.codeImg)
    ImageView codeImg;
    @InjectView(R.id.codeTxt)
    TextView codeTxt;
    @InjectView(R.id.qrcodeView)
    View qrcodeView;

    private NewUnionSwepRecordDetail record;

    @Override
    protected int getContentViewId() {
        return R.layout.uinon_swept_record_detail_activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ButterKnife.inject(this);
        record = (NewUnionSwepRecordDetail) getIntent().getExtras().getSerializable(EXTRA_UNION_SWEP_DETAIL);
        initViews();
    }

    private void initViews(){
        if(record.mode.equals("C2B") && !Util.isEmpty(record.qr_code)){
            WindowManager wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
            final int width = wm.getDefaultDisplay().getWidth();

            qrcodeView.setVisibility(View.VISIBLE);
            codeTxt.setText(record.order_no);
            try {
                codeImg.setImageBitmap(UnionSweptCodeActivity.createCode(record.qr_code, BarcodeFormat.CODE_128,width*4/5,180));
            } catch (WriterException e) {
                e.printStackTrace();
            }
        } else{
            qrcodeView.setVisibility(View.GONE);
        }

        if(Util.isEmpty(record.couponOffstAmt)){
            amt.setText("￥"+record.amount);
        } else{
            DecimalFormat decimalFormat=new DecimalFormat("############0.00");
            amt.setText("￥"+decimalFormat.format(Double.parseDouble(record.orig_amt)
                    - Double.parseDouble(record.couponOffstAmt)));// 真实支付的金额
            bigAmt.setText("￥"+record.orig_amt);
            bigAmt.getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG );
        }
        if(Util.isEmpty(record.couponOffstAmt)){
            couponTxtView.setVisibility(View.GONE);
            couponAmtView.setVisibility(View.GONE);
        } else{
            couponTxtView.setVisibility(View.VISIBLE);
            couponAmtView.setVisibility(View.VISIBLE);
            couponTxtTxt.setText(record.couponDesc);
            couponAmtTxt.setText("-"+record.couponOffstAmt);
        }

        orderStatus.setText(getPayStatus(record.status));
        ordertype.setText(getType(record.order_type));
        payTxt.setText(record.iss_ins_name+record.card_type_name+"("+record.bankcard_no+")");
        getPayTxt.setText(record.merchant_name);
        int[] timeItems = record.order_time != null? Util.getTimeItems(record.order_time): null;
        payTime.setText(timeItems!=null ?
                String.format("%02d-%02d-%02d %02d:%02d:%02d",timeItems[0],timeItems[1],timeItems[2],timeItems[3],timeItems[4],timeItems[5]) : "");
        order_no.setText(record.order_no);
        order_pzh.setText(record.voucher_num);
    }

    private String getType(String key){
        if(Util.isEmpty(key)){
            return "";
        }
        if(key.equals(UPQROrderType.NormalConsumption.toString())){
            return "一般消费";
        } else if(key.equals(UPQROrderType.RestrictCreditConsumption.toString())){
            return "非贷记账户消费";
        } else if(key.equals(UPQROrderType.MiniMerchantConsumption.toString())){
            return "小微商户收款";
        } else if(key.equals(UPQROrderType.ATMEnchashment.toString())){
            return "ATM取现";
        } else if(key.equals(UPQROrderType.Transfer.toString())){
            return "人到人转账";
        } else{
            return "";
        }
    }

    private String getPayStatus(String key){
        String str;
        if(key.equals("unpay")){
            str = "未支付";
        } else if(key.equals("paied")){
            str = "已支付";
        } else if(key.equals("success")){
            str = "已成功";
        } else if(key.equals("fail")){
            str = "处理失败";
        } else if(key.equals("refunding")){
            str = "退款中";
        } else if(key.equals("refundsucc")){
            str = "已退款";
        } else if(key.equals("refundfail")){
            str = "退款失败";
        }else if(key.equals("cancel")){
            str = "取消";
        } else{
            str = "";
        }
        return str;
    }
}
