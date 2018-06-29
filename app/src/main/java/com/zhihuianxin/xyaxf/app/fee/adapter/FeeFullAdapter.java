package com.zhihuianxin.xyaxf.app.fee.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import modellib.thrift.fee.FeeRecord;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.pacific.adapter.RecyclerAdapter;
import com.pacific.adapter.RecyclerAdapterHelper;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.fee.feelist.view.FeeFullItemDetailActivity;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by zcrpro on 2016/11/13.
 */
public class FeeFullAdapter extends RecyclerAdapter<FeeRecord> implements View.OnClickListener{
    private OnItemClickListener mOnItemClickListener = null;
    private Context mContext;

    public FeeFullAdapter(Context context, @Nullable List<FeeRecord> data, @NonNull int... layoutResIds) {
        super(context, data, layoutResIds);
        mContext = context;
    }

    @Override
    protected void convert(RecyclerAdapterHelper helper, final FeeRecord feeRecord) {
        helper.setText(R.id.feeId,feeRecord.fee_id);
        helper.setText(R.id.tv_fee_name, feeRecord.title);
        helper.setText(R.id.tv_fee_amount,new DecimalFormat("0.00").format(Float.parseFloat(feeRecord.amount)) + "元");
        FeeFullDetailAdapter feeFullDetailAdapter = new FeeFullDetailAdapter(feeRecord.pay_records, R.layout.fee_full_detail_item);
        feeFullDetailAdapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int i) {
                Intent intent = new Intent(mContext,FeeFullItemDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(FeeFullItemDetailActivity.EXTRA_DATA,feeRecord.fee_id);
                intent.putExtras(bundle);
                mContext.startActivity(intent);
            }
        });
        ((RecyclerView) helper.getView(R.id.recyclerview)).setLayoutManager(new LinearLayoutManager(context));
        ((RecyclerView) helper.getView(R.id.recyclerview)).setAdapter(feeFullDetailAdapter);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        holder.itemView.setOnClickListener(this);
        holder.itemView.setTag(position);
        super.onBindViewHolder(holder, position);
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(v,(int) v.getTag());
        }
    }

    //define interface
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
}
