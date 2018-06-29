package com.zhihuianxin.xyaxf.app.ecard.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import modellib.thrift.ecard.ECardChargeRecord;
import com.pacific.adapter.RecyclerAdapter;
import com.pacific.adapter.RecyclerAdapterHelper;
import com.zhihuianxin.xyaxf.R;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by zcrpro on 2016/11/1.
 */
public class PaymentRecordAdapter extends RecyclerAdapter<ECardChargeRecord> {

    public PaymentRecordAdapter(Context context, @NonNull int... layoutResIds) {
        super(context, layoutResIds);
    }

    @Override
    protected void convert(RecyclerAdapterHelper helper, ECardChargeRecord eCardChargeRecord) {
        helper.setText(R.id.tv_num,new DecimalFormat("0.00").format(Float.parseFloat(eCardChargeRecord.amount))+"");
        if (eCardChargeRecord.status.equals("notify_processing")){
            helper.setText(R.id.tv_status,"已支付");
        }else if (eCardChargeRecord.status.equals("notify_success")){
            helper.setText(R.id.tv_status,"已上账");
        }
        helper.setText(R.id.tv_time,getValidDateText(eCardChargeRecord.time));
    }
    public PaymentRecordAdapter(Context context, @Nullable List data, @NonNull int... layoutResIds) {
        super(context, data, layoutResIds);
    }

    private String getValidDateText(String endDate) {
        return String.format("%s-%s %s:%s",
                endDate.substring(4, 6), endDate.substring(6, 8),endDate.substring(8, 10),endDate.substring(10, 12));
    }
}
