package com.zhihuianxin.xyaxf.app.ecard.account;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.TextView;

import modellib.thrift.ecard.ECardRecord;
import com.pacific.adapter.RecyclerAdapter;
import com.pacific.adapter.RecyclerAdapterHelper;
import com.zhihuianxin.xyaxf.app.base.axutil.Util;
import com.zhihuianxin.xyaxf.R;

import java.util.List;

/**
 * Created by zcrpro on 2016/11/12.
 */
public class EcardAccountBookAdapter extends RecyclerAdapter<ECardRecord> {

    public EcardAccountBookAdapter(Context context, @Nullable List<ECardRecord> data, @NonNull int... layoutResIds) {
        super(context, data, layoutResIds);
    }

    @Override
    protected void convert(RecyclerAdapterHelper helper, ECardRecord eCardRecord) {
        helper.setText(R.id.tv_name, eCardRecord.title);
        helper.setText(R.id.tv_time, formatDate(eCardRecord.time));
        if (Float.parseFloat(eCardRecord.amount) > 0) {
            helper.setText(R.id.tv_num, "+" + eCardRecord.amount);
            ((TextView) helper.getView(R.id.tv_num)).setTextColor(context.getResources().getColor(R.color.axf_common_green));
        } else {
            helper.setText(R.id.tv_num, eCardRecord.amount);
            ((TextView) helper.getView(R.id.tv_num)).setTextColor(context.getResources().getColor(R.color.axf_common_red));
        }
    }

    public static String formatDate(String milli) {
        int[] timeItems = milli != null? Util.getTimeItems(milli): null;
        return timeItems!=null ?
                String.format("%02d-%02d-%02d %02d:%02d",timeItems[0],timeItems[1],timeItems[2],timeItems[3],timeItems[4]) : "";
    }
}
