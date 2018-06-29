package com.zhihuianxin.xyaxf.app.fee.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;

import modellib.thrift.fee.SubFee;
import com.zhihuianxin.xyaxf.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * Created by zcrprozcrpro on 2017/5/19.
 */

public class ShouldFeeAdapter extends ArrayAdapter<SubFee> implements StickyListHeadersAdapter {

    private Context mContext;

    public ShouldFeeAdapter(@NonNull Context context, @LayoutRes int resource) {
        super(context, resource);
    }

    public ShouldFeeAdapter(Context context) {
        super(context, 0);
        mContext = context;
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        SubFee subFee = getItem(position);

        View headerView;
        if (convertView != null) {
            headerView = convertView;
        } else {
            headerView = ((Activity) mContext).getLayoutInflater().inflate(R.layout.should_title_item, parent, false);
        }
        TextView title = (TextView) headerView.findViewById(R.id.title);
        title.setText(subFee.year);
        return headerView;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view;
        if (convertView != null) {
            view = convertView;
        } else {
            view = ((Activity) mContext).getLayoutInflater().inflate(R.layout.should_fee_item, parent, false);
        }

        SubFee subFee = getItem(position);
        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText(subFee.title);

        GridView gridView = (GridView) view.findViewById(R.id.gridview);
        List<String> subFees = new ArrayList<>();
        if (subFee.amount!=null&&Math.abs(Float.parseFloat(subFee.amount)) != 0) {
            subFees.add("未缴: " + subFee.amount);
        }
        if (subFee.total_amount!=null&&Math.abs(Float.parseFloat(subFee.total_amount)) != 0) {
            subFees.add("应收: " + subFee.total_amount);
        }
        if (subFee.deduct_amount!=null&&Math.abs(Float.parseFloat(subFee.deduct_amount)) != 0) {
            subFees.add("减免: " + subFee.deduct_amount);
        }
        if (subFee.paid_amount!=null&&subFee.loan_amt!=null&&Math.abs((Float.parseFloat(subFee.paid_amount)-Float.parseFloat(subFee.loan_amt))) != 0) {
            subFees.add("已缴: " + new DecimalFormat("0.00").format((Float.parseFloat(subFee.paid_amount)-Float.parseFloat(subFee.loan_amt))));
        }
        if (subFee.loan_amt!=null&&Math.abs(Float.parseFloat(subFee.loan_amt)) != 0) {
            subFees.add("贷款: " + subFee.loan_amt);
        }
        if (subFee.refund_amount!=null&&Math.abs(Float.parseFloat(subFee.refund_amount)) != 0) {
            subFees.add("退款: " + subFee.refund_amount);
        }
        gridView.setAdapter(new FeeOtherInfoAdapter(mContext, subFees));
        gridView.deferNotifyDataSetChanged();
        view.setTag(getItem(position).id);

        return view;
    }

    @Override
    public long getHeaderId(int position) {
        return getItem(position).year.hashCode();
    }
}
