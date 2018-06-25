package com.zhihuianxin.xyaxf.app.payment;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pacific.adapter.RecyclerAdapter;
import com.pacific.adapter.RecyclerAdapterHelper;
import com.zhihuianxin.xyaxf.R;

import java.util.List;

public class CashierDeskFeeNormalAdapter extends RecyclerAdapter<FeeListData> {

    public CashierDeskFeeNormalAdapter(Context context, @NonNull int... layoutResIds) {
        super(context, layoutResIds);
    }

    public CashierDeskFeeNormalAdapter(Context context, @Nullable List<FeeListData> data, @NonNull int... layoutResIds) {
        super(context, data, layoutResIds);
    }

    @Override
    protected void convert(RecyclerAdapterHelper helper, FeeListData feeList) {
        helper.setText(R.id.tv_sub_fee_name, feeList.name);
        helper.setText(R.id.tv_sub_fee_amount, feeList.amount);
    }
}