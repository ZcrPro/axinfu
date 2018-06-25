package com.zhihuianxin.xyaxf.app.fee.deduction;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.axinfu.modellib.thrift.fee.SubFeeItem;
import com.zhihuianxin.xyaxf.App;
import com.zhihuianxin.xyaxf.BaseRealmActionBarActivity;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.fee.adapter.DeductionAdapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by zcrprozcrpro on 2017/5/16.
 */

public class DeductionActivity extends BaseRealmActionBarActivity {


    @InjectView(R.id.recyclerview)
    RecyclerView recyclerview;
    @InjectView(R.id.btn_ok)
    Button btnOk;

    public static final String buss_type = "buss_type";
    public String type;

    private DeductionAdapter adapter;
    private List<SubFeeItem> subFeeItems;

    private List<SubFeeItem> newSelect;

    @Override
    protected int getContentViewId() {
        return R.layout.deduction_activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            type = bundle.getString(buss_type);
        }

        subFeeItems = new ArrayList<>();
        newSelect = new ArrayList<>();

        Iterator iter = App.subFeeDeductionHashMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            Object value = entry.getValue();
            if (((SubFeeItem) value).business_channel.equals(type)) {
                subFeeItems.add((SubFeeItem) value);
            }
        }

        recyclerview.setLayoutManager(new LinearLayoutManager(this));

        adapter = new DeductionAdapter(this, type, subFeeItems, R.layout.deduction_item);
        recyclerview.setAdapter(adapter);

        adapter.feeDecuNum(new DeductionAdapter.FeeSelectNumListener() {
            @Override
            public void feedecuNum(SubFeeItem subFeeItem) {
                SubFeeItem subFeeItem1 = new SubFeeItem();
                subFeeItem1.id = subFeeItem.id;
                subFeeItem1.title = subFeeItem.title;
                subFeeItem1.amount = subFeeItem.amount;
                subFeeItem1.loan_amt = subFeeItem.loan_amt;
                subFeeItem1.min_pay_amount = subFeeItem.min_pay_amount;
                subFeeItem1.is_priority = subFeeItem.is_priority;
                subFeeItem1.deduct_amount = subFeeItem.deduct_amount;
                subFeeItem1.paid_amount = subFeeItem.paid_amount;
                subFeeItem1.refund_amount = subFeeItem.refund_amount;
                subFeeItem1.business_channel = subFeeItem.business_channel;
                subFeeItem1.year = subFeeItem.year;
                subFeeItem1.total_amount = subFeeItem.total_amount;
                subFeeItem1.isSelect = true;
                newSelect.add(subFeeItem1);
            }
        });

        adapter.feeDecuNum(new DeductionAdapter.FeeNotSelectNumListener() {
            @Override
            public void feedecuNum(SubFeeItem subFeeItem) {
                SubFeeItem subFeeItem1 = new SubFeeItem();
                subFeeItem1.id = subFeeItem.id;
                subFeeItem1.title = subFeeItem.title;
                subFeeItem1.amount = subFeeItem.amount;
                subFeeItem1.loan_amt = subFeeItem.loan_amt;
                subFeeItem1.min_pay_amount = subFeeItem.min_pay_amount;
                subFeeItem1.is_priority = subFeeItem.is_priority;
                subFeeItem1.deduct_amount = subFeeItem.deduct_amount;
                subFeeItem1.paid_amount = subFeeItem.paid_amount;
                subFeeItem1.refund_amount = subFeeItem.refund_amount;
                subFeeItem1.business_channel = subFeeItem.business_channel;
                subFeeItem1.year = subFeeItem.year;
                subFeeItem1.total_amount = subFeeItem.total_amount;
                subFeeItem1.isSelect = false;
                newSelect.add(subFeeItem1);
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < newSelect.size(); i++) {
                    App.subFeeDeductionHashMap.remove(newSelect.get(i).id);
                    App.subFeeDeductionHashMap.put(newSelect.get(i).id, newSelect.get(i));
                }
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.reset(this);
    }

    @Override
    public void finish() {
        super.finish();
    }
}
