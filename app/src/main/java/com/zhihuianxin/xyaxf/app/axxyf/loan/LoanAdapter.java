package com.zhihuianxin.xyaxf.app.axxyf.loan;

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

import java.util.List;

/**
 * Created by zcrpro on 2018/1/2.
 */

public class LoanAdapter extends RecyclerAdapter<LoanFragment.LoanBills> {


    public LoanAdapter(Context context, @NonNull int... layoutResIds) {
        super(context, layoutResIds);
    }

    public LoanAdapter(Context context, @Nullable List<LoanFragment.LoanBills> data, @NonNull int... layoutResIds) {
        super(context, data, layoutResIds);
    }

    @Override
    protected void convert(RecyclerAdapterHelper helper, final LoanFragment.LoanBills item) {
        helper.setText(R.id.loan_amount,item.loan_amount);
        helper.setText(R.id.loan_date1,"贷款日期："+item.instalment_trade_date);
        helper.setText(R.id.loan_date2,"到期日期："+item.loan_date);

        helper.getItemView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle =new Bundle();
                bundle.putSerializable("LoanBills",item);
                Intent intent =new Intent(context, LoanDelActivity.class);
                intent.putExtras(bundle);
                ((Activity)context).startActivity(intent);
            }
        });

    }

    public void update(List<LoanFragment.LoanBills> loanBills){
        data.clear();
        data.addAll(loanBills);
        notifyDataSetChanged();
    }
}
