package com.zhihuianxin.xyaxf.app.fee.adapter;

import modellib.thrift.fee.PayFeeRecord;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zhihuianxin.xyaxf.R;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by zcrpro on 2016/11/13.
 */
public class FeeFullDetailAdapter extends BaseQuickAdapter<PayFeeRecord> {


    public FeeFullDetailAdapter(List data, int... layoutResId) {
        super(layoutResId[0], data);
    }

    public static String formatDate(long milli) {
        SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
        return format.format(new Date(milli));
    }

    private String getValidDateText(String endDate) {
        return String.format("%s-%s %s:%s",
                endDate.substring(4, 6), endDate.substring(6, 8), endDate.substring(8, 10), endDate.substring(10, 12));
    }

    @Override
    protected void convert(BaseViewHolder helper, PayFeeRecord payFeeRecord) {
        helper.setText(R.id.tv_water, "流水号：" + payFeeRecord.serial);
        helper.setText(R.id.tv_fee_time, getValidDateText(payFeeRecord.time));
        helper.setText(R.id.tv_fee_amount, new DecimalFormat("0.00").format(Float.parseFloat(payFeeRecord.amount)) + "元");
    }
}
