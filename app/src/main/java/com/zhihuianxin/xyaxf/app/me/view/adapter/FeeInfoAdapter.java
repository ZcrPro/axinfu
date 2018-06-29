package com.zhihuianxin.xyaxf.app.me.view.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import modellib.thrift.fee.RecordSubFee;
import com.pacific.adapter.RecyclerAdapter;
import com.pacific.adapter.RecyclerAdapterHelper;
import com.zhihuianxin.xyaxf.R;

import java.util.List;

public class FeeInfoAdapter extends RecyclerAdapter<RecordSubFee> {

    public FeeInfoAdapter(Context context, @NonNull int... layoutResIds) {
        super(context, layoutResIds);
    }

    public FeeInfoAdapter(Context context, @Nullable List<RecordSubFee> data, @NonNull int... layoutResIds) {
        super(context, data, layoutResIds);
    }

    @Override
    protected void convert(RecyclerAdapterHelper helper, RecordSubFee subFee) {
        helper.setText(R.id.name,subFee.title);
        helper.setText(R.id.amount,subFee.amount);
    }

}
