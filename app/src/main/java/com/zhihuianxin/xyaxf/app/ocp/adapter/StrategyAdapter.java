package com.zhihuianxin.xyaxf.app.ocp.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pacific.adapter.RecyclerAdapter;
import com.pacific.adapter.RecyclerAdapterHelper;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.ocp.PricingStrategy;

import java.util.List;

public class StrategyAdapter extends RecyclerAdapter<PricingStrategy> {

    public StrategyAdapter(Context context, @NonNull int... layoutResIds) {
        super(context, layoutResIds);
    }

    public StrategyAdapter(Context context, @Nullable List<PricingStrategy> data, @NonNull int... layoutResIds) {
        super(context, data, layoutResIds);
    }

    @Override
    protected void convert(RecyclerAdapterHelper helper, PricingStrategy pricingStrategy) {
        helper.setText(R.id.name,pricingStrategy.name);
        helper.setText(R.id.amount,pricingStrategy.float_amt);
    }

}
