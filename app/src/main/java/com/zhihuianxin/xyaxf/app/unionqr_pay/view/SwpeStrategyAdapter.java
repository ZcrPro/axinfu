package com.zhihuianxin.xyaxf.app.unionqr_pay.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.axinfu.modellib.thrift.message.PricingStrategy;
import com.pacific.adapter.RecyclerAdapter;
import com.pacific.adapter.RecyclerAdapterHelper;
import com.zhihuianxin.xyaxf.R;

import java.util.List;

public class SwpeStrategyAdapter extends RecyclerAdapter<PricingStrategy> {

    public SwpeStrategyAdapter(Context context, @NonNull int... layoutResIds) {
        super(context, layoutResIds);
    }

    public SwpeStrategyAdapter(Context context, @Nullable List<PricingStrategy> data, @NonNull int... layoutResIds) {
        super(context, data, layoutResIds);
    }

    @Override
    protected void convert(RecyclerAdapterHelper helper, PricingStrategy pricingStrategy) {
        helper.setText(R.id.name,pricingStrategy.name);
        helper.setText(R.id.amount,pricingStrategy.float_amt);
    }

}