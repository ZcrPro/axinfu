package com.zhihuianxin.xyaxf.app.axxyf.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pacific.adapter.RecyclerAdapter;
import com.pacific.adapter.RecyclerAdapterHelper;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.axxyf.fragment.OutBillFragment;

import java.util.List;

/**
 * Created by zcrpro on 2018/1/2.
 */

public class OutBillListAdapter extends RecyclerAdapter<OutBillFragment.BillDetails> {


    public OutBillListAdapter(Context context, @NonNull int... layoutResIds) {
        super(context, layoutResIds);
    }

    public OutBillListAdapter(Context context, @Nullable List<OutBillFragment.BillDetails> data, @NonNull int... layoutResIds) {
        super(context, data, layoutResIds);
    }

    @Override
    protected void convert(RecyclerAdapterHelper helper, OutBillFragment.BillDetails item) {
        helper.setText(R.id.trade_date, item.trade_date);
        helper.setText(R.id.trade_name, item.remark);
        helper.setText(R.id.trade_amt, item.trade_amt);
    }

    public void update(List<OutBillFragment.BillDetails> details){
            data.clear();
            data.addAll(details);
            notifyDataSetChanged();
    }
}
