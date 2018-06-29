package com.zhihuianxin.xyaxf.app.payment;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import modellib.thrift.fee.SubFee;
import com.pacific.adapter.RecyclerAdapter;
import com.pacific.adapter.RecyclerAdapterHelper;
import com.zhihuianxin.xyaxf.R;

import java.util.List;

import io.realm.SubFeeRealmProxy;

/**
 * Created by zcrpro on 2016/11/12.
 */
public class CashierDeskFeeAdapter extends RecyclerAdapter<SubFee> {

    public CashierDeskFeeAdapter(Context context, @Nullable List<SubFee> data, @NonNull int... layoutResIds) {
        super(context, data, layoutResIds);
    }

    @Override
    protected void convert(RecyclerAdapterHelper helper, SubFee item) {
        helper.setText(R.id.tv_sub_fee_name, ((SubFeeRealmProxy) item).realmGet$title() + ":");
        helper.setText(R.id.tv_sub_fee_amount, ((SubFeeRealmProxy) item).realmGet$amount() + "元");
    }
}
