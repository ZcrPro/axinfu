package com.zhihuianxin.xyaxf.app.fee.adapter;

import android.graphics.Color;
import android.text.TextUtils;
import android.widget.TextView;

import modellib.thrift.fee.Fee;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zhihuianxin.xyaxf.app.base.axutil.Util;
import com.zhihuianxin.xyaxf.R;

import java.util.Date;
import java.util.List;

/**
 * Created by zcrpro on 2016/10/19.
 */

public class FeeNotFullAdapter extends BaseQuickAdapter<Fee> {

    public FeeNotFullAdapter(List data, int... layoutResId) {
        super(layoutResId[0], data);
    }

    private String getValidDateText(String endDate) {

        long endMilli = new Date(
                Util.parseInteger(endDate.substring(0, 4), 0) - 1900,
                Util.parseInteger(endDate.substring(4, 6), 0) - 1,
                Util.parseInteger(endDate.substring(6, 8), 0),
                Util.parseInteger(endDate.substring(8, 10), 0),
                Util.parseInteger(endDate.substring(10, 12), 0)).getTime();

        float leftDays = (float) (endMilli - System.currentTimeMillis()) / (24 * 3600 * 1000);

        if (leftDays<0){
            return "已过期";
        }else {
            return String.format("剩%s天",(int)(leftDays+1));
        }

    }

    @Override
    protected void convert(BaseViewHolder helper, Fee item) {
        helper.setText(R.id.name, item.title);
        helper.setImageResource(R.id.iv_fee_logo, R.drawable.project1_icon);
        if (TextUtils.isEmpty(item.end_date)) {
            helper.setText(R.id.tv_end_date, "永久有效");
        } else {
            try {
                if (getValidDateText(item.end_date).equals("剩1天")||getValidDateText(item.end_date).equals("剩01天")){
                    ((TextView)helper.getView(R.id.tv_end_date)).setTextColor(Color.parseColor("#ff4867"));
                }else {
                    ((TextView)helper.getView(R.id.tv_end_date)).setTextColor(Color.parseColor("#666666"));
                }
                helper.setText(R.id.tv_end_date, getValidDateText(item.end_date));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        helper.getConvertView().setTag(item);
    }

    @Override

    public int getItemCount() {
        return mData.size() + getFooterViewsCount();
    }

}
