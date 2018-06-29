package com.zhihuianxin.xyaxf.app.fee.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.RelativeLayout;

import modellib.thrift.fee.SubFeeItem;
import com.pacific.adapter.RecyclerAdapter;
import com.pacific.adapter.RecyclerAdapterHelper;
import com.zhihuianxin.xyaxf.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zcrprozcrpro on 2017/5/16.
 */

public class DeductionAdapter extends RecyclerAdapter<SubFeeItem> {

    private String type;
    private FeeSelectNumListener selectlsitener;
    private FeeNotSelectNumListener notlisterner;

    public DeductionAdapter(Context context, @NonNull int... layoutResIds) {
        super(context, layoutResIds);
    }

    public DeductionAdapter(Context context, @Nullable List<SubFeeItem> data, @NonNull int... layoutResIds) {
        super(context, data, layoutResIds);
    }

    public DeductionAdapter(Context context, String type, @Nullable List<SubFeeItem> data, @NonNull int... layoutResIds) {
        super(context, data, layoutResIds);
        this.type = type;
    }

    @Override
    protected void convert(final RecyclerAdapterHelper helper, final SubFeeItem item) {

        if (type.equals(item.business_channel)) {
            helper.getItemView().setVisibility(View.VISIBLE);
            helper.setText(R.id.tv_fee_name, item.title);
            helper.setText(R.id.tv_fee_amount, item.amount);

            List<String> subFees = new ArrayList<>();
            if (item.amount!=null&&Math.abs(Float.parseFloat(item.amount)) != 0) {
                subFees.add("未缴: " + item.amount);
            }
            if (item.total_amount!=null&&Math.abs(Float.parseFloat(item.total_amount)) != 0) {
                subFees.add("应收: " + item.total_amount);
            }
            if (item.deduct_amount!=null&&Math.abs(Float.parseFloat(item.deduct_amount)) != 0) {
                subFees.add("减免: " + item.deduct_amount);
            }
            if (item.paid_amount!=null&&item.loan_amt!=null&&Math.abs((Float.parseFloat(item.paid_amount)-Float.parseFloat(item.loan_amt))) != 0) {
                subFees.add("已缴: " + new DecimalFormat("0.00").format((Float.parseFloat(item.paid_amount)-Float.parseFloat(item.loan_amt))));
            }
            if (item.loan_amt!=null&&Math.abs(Float.parseFloat(item.loan_amt)) != 0) {
                subFees.add("贷款: " + item.loan_amt);
            }
            if (item.refund_amount!=null&&Math.abs(Float.parseFloat(item.refund_amount)) != 0) {
                subFees.add("退款: " + item.refund_amount);
            }
            ((GridView)helper.getView(R.id.gridview)).setAdapter(new FeeOtherInfoAdapter(context, subFees));
            ((GridView)helper.getView(R.id.gridview)).deferNotifyDataSetChanged();

            if (item.isSelect) {
                ((CheckBox) helper.getView(R.id.checkbox)).setChecked(true);
                ((CheckBox) helper.getView(R.id.checkbox)).setBackgroundResource(R.drawable.choose_icon5);
            } else {
                ((CheckBox) helper.getView(R.id.checkbox)).setChecked(false);
                ((CheckBox) helper.getView(R.id.checkbox)).setBackgroundResource(R.drawable.choose_icon2);
            }

            helper.getItemView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!((CheckBox) helper.getView(R.id.checkbox)).isChecked()) {
                        ((CheckBox) helper.getView(R.id.checkbox)).setChecked(true);
                        ((CheckBox) helper.getView(R.id.checkbox)).setBackgroundResource(R.drawable.choose_icon5);
                        if (selectlsitener != null) {
                            selectlsitener.feedecuNum(item);
                        }

                    } else {
                        ((CheckBox) helper.getView(R.id.checkbox)).setChecked(false);
                        ((CheckBox) helper.getView(R.id.checkbox)).setBackgroundResource(R.drawable.choose_icon2);
                        if (notlisterner != null) {
                            notlisterner.feedecuNum(item);
                        }
                    }
                }
            });
        } else {
            helper.getItemView().setVisibility(View.GONE);
        }


        ((RelativeLayout) helper.getView(R.id.view_ck)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!((CheckBox) helper.getView(R.id.checkbox)).isChecked()) {
                    ((CheckBox) helper.getView(R.id.checkbox)).setChecked(true);
                    ((CheckBox) helper.getView(R.id.checkbox)).setBackgroundResource(R.drawable.choose_icon5);
                    if (selectlsitener != null) {
                        selectlsitener.feedecuNum(item);
                    }

                } else {
                    ((CheckBox) helper.getView(R.id.checkbox)).setChecked(false);
                    ((CheckBox) helper.getView(R.id.checkbox)).setBackgroundResource(R.drawable.choose_icon2);
                    if (notlisterner != null) {
                        notlisterner.feedecuNum(item);
                    }
                }
            }
        });

    }

    /**
     * 抵扣总数传给activity
     */
    public interface FeeSelectNumListener {
        void feedecuNum(SubFeeItem subFeeItem);
    }

    public void feeDecuNum(FeeSelectNumListener selectlsitener) {
        this.selectlsitener = selectlsitener;
    }

    /**
     * 抵扣总数传给activity
     */
    public interface FeeNotSelectNumListener {
        void feedecuNum(SubFeeItem subFeeItem);
    }

    public void feeDecuNum(FeeNotSelectNumListener notlisterner) {
        this.notlisterner = notlisterner;
    }
}
