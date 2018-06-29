package com.zhihuianxin.xyaxf.app.me.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import modellib.thrift.unqr.NewUnionSwepRecordDetail;
import modellib.thrift.unqr.UPQROrderType;
import modellib.thrift.unqr.UPQRPayRecord;
import com.zhihuianxin.xyaxf.app.base.axutil.Util;
import com.zhihuianxin.xyaxf.R;

import java.io.Serializable;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * Created by Vincent on 2017/12/11.
 */

public class UnionRecordListAdapter extends ArrayAdapter<UnionRecordListAdapter.PaymentRecordExt> implements StickyListHeadersAdapter,Serializable {
    private Context mContext;
    public UnionRecordListAdapter(Context context) {
        super(context, 0);
        mContext = context;
    }

    public static class PaymentRecordExt {
        public UPQRPayRecord paymentRecord;
        public String category_value;
        public String category;
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        PaymentRecordExt payExt = getItem(position);

        View headerView;
        if(convertView != null){
            headerView = convertView;
        }
        else{
            headerView = ((Activity)mContext).getLayoutInflater().inflate(R.layout.header_item, parent, false);
        }
        TextView title = (TextView) headerView.findViewById(R.id.title);
        title.setText(payExt.category);
        return headerView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if(convertView != null){
            view = convertView;
        }
        else{
            view = ((Activity)mContext).getLayoutInflater().inflate(R.layout.me_paylist_item, parent, false);
        }

        PaymentRecordExt payExt = getItem(position);
        ((TextView)view.findViewById(R.id.title)).setText(getPayStatus(payExt.paymentRecord.status));
        ((TextView)view.findViewById(R.id.amount)).setText(payExt.paymentRecord.amount);
        if(Util.isEmpty(getType(payExt.paymentRecord.order_type))){
            ((TextView)view.findViewById(R.id.type)).setText(payExt.paymentRecord.merchant_name);
        } else{
            ((TextView)view.findViewById(R.id.type)).setText(getType(payExt.paymentRecord.order_type)+"-"+payExt.paymentRecord.merchant_name);
        }

        int[] timeItems = payExt.paymentRecord.order_time != null? Util.getTimeItems(payExt.paymentRecord.order_time): null;
        ((TextView)view.findViewById(R.id.time)).setText(timeItems!=null ?
                String.format("%02d-%02d %02d:%02d",timeItems[1],timeItems[2],timeItems[3],timeItems[4]) : "");
        view.setTag(tranToUnSerBody(getItem(position).paymentRecord));

        return view;
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
        newCoupon.status = upqrPayRecord.status;
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

    @Override
    public long getHeaderId(int position) {
        return getItem(position).category.hashCode();
    }
}
