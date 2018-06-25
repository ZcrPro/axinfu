package com.zhihuianxin.xyaxf.app.axxyf.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.pacific.adapter.RecyclerAdapter;
import com.pacific.adapter.RecyclerAdapterHelper;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.axxyf.HisDelActivity;
import com.zhihuianxin.xyaxf.app.axxyf.fragment.HisFragment;

import java.util.List;

/**
 * Created by zcrpro on 2018/1/2.
 */

public class HisAdapter extends RecyclerAdapter<HisFragment.BillsHeader> {


    public HisAdapter(Context context, @NonNull int... layoutResIds) {
        super(context, layoutResIds);
    }

    public HisAdapter(Context context, @Nullable List<HisFragment.BillsHeader> data, @NonNull int... layoutResIds) {
        super(context, data, layoutResIds);
    }

    @Override
    protected void convert(RecyclerAdapterHelper helper, final HisFragment.BillsHeader item) {
            helper.setText(R.id.current_balance,item.current_balance);
            helper.setText(R.id.name,item.year_month+"月账单");
            helper.getItemView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle =new Bundle();
                    bundle.putSerializable("BillsHeader",item);
                    Intent intent =new Intent(context, HisDelActivity.class);
                    intent.putExtras(bundle);
                    ((Activity)context).startActivity(intent);
                }
            });
    }

    public void update(List<HisFragment.BillsHeader> billsHeaders){
        data.clear();
        data.addAll(billsHeaders);
        notifyDataSetChanged();
    }
}
