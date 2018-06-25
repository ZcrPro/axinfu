package com.zhihuianxin.xyaxf.app.me.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.axinfu.modellib.thrift.fee.PaymentRecord;
import com.zhihuianxin.axutil.Util;
import com.zhihuianxin.xyaxf.R;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * Created by Vincent on 2016/10/24.
 */

public class MePayListAdapter extends ArrayAdapter<MePayListAdapter.PaymentRecordExt> implements StickyListHeadersAdapter {
    private Context mContext;
    public MePayListAdapter(Context context) {
        super(context, 0);
        mContext = context;
    }

    public static class PaymentRecordExt {
        public PaymentRecord paymentRecord;
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
        } else{
            view = ((Activity)mContext).getLayoutInflater().inflate(R.layout.me_paylist_item, parent, false);
        }

        PaymentRecordExt payExt = getItem(position);
        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText(payExt.paymentRecord.order_status_desc);
        setAmount(payExt.paymentRecord.pay_amt,view);
        ((TextView)view.findViewById(R.id.type)).setText(payExt.paymentRecord.trade_summary);
        int[] timeItems = payExt.paymentRecord.order_time != null? Util.getTimeItems(payExt.paymentRecord.order_time): null;
        ((TextView)view.findViewById(R.id.time)).setText(timeItems!=null ?
                String.format("%02d-%02d %02d:%02d:%02d",timeItems[1],timeItems[2],timeItems[3],timeItems[4],timeItems[5]) : "");
        view.setTag(getItem(position).paymentRecord);

        return view;
    }

    private void setAmount(String amt, View view){
        if(amt.contains(".")){
            String[] amts = amt.split("\\.");
            ((TextView)view.findViewById(R.id.amoungbig)).setText(amts[0]+".");
            ((TextView)view.findViewById(R.id.amount)).setText(amts[1]);
        } else {
            ((TextView)view.findViewById(R.id.amount)).setText(amt);
        }
    }

    private String getPayFor(String key){
        String str;
        if(key.equals("RechargeECard")){
            str = "一卡通充值";
        } else if(key.equals("SchoolFee")){
            str = "缴费";
        } else if(key.equals("ScanPay")){
            str = "";
        } else if(key.equals("GatewayPay")){
            str = "";
        } else if(key.equals("RechargeMobile")){
            str = "话费充值";
        } else if(key.equals("RechargeFlow")){
            str = "流量充值";
        } else{ // TdtcFee
            str = "";
        }
        return str;
    }

    @Override
    public long getHeaderId(int position) {
        return getItem(position).category.hashCode();
    }

}
