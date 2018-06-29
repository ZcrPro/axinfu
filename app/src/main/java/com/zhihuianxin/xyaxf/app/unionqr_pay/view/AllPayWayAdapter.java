package com.zhihuianxin.xyaxf.app.unionqr_pay.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import modellib.thrift.base.PayMethod;
import com.google.gson.Gson;
import com.pacific.adapter.RecyclerAdapter;
import com.pacific.adapter.RecyclerAdapterHelper;
import com.zhihuianxin.xyaxf.app.base.axutil.Util;
import com.zhihuianxin.xyaxf.App;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.unionqr_pay.view.activity.UnionHtmlActivity;
import com.zhihuianxin.xyaxf.app.unionqr_pay.view.activity.UnionSweptEmptyCardActivity;

import java.util.List;

/**
 * Created by zcrpro on 2017/11/24.
 */

public class AllPayWayAdapter extends RecyclerAdapter<PayMethod> {

    private fristSelectPayWay fristSelectPayWay;
    private clickListener clickListener;
    private clickListener2 clickListener2;
    private Context context;

    public AllPayWayAdapter(Context context, @NonNull int... layoutResIds) {
        super(context, layoutResIds);
    }

    public AllPayWayAdapter(Context context, @Nullable List<PayMethod> data, @NonNull int... layoutResIds) {
        super(context, data, layoutResIds);
        Log.d("OcpAllPayWayAdapter", new Gson().toJson(data));
        this.context = context;
        assert data != null;
    }

    @SuppressLint("NewApi")
    @Override
    protected void convert(final RecyclerAdapterHelper helper, final PayMethod item) {


        if (item.channel.equals("UnionPay") && item.purpose == null) {
            helper.getItemView().setVisibility(View.VISIBLE);
            helper.setText(R.id.text, "银联在线支付");
            helper.setImageResource(R.id.img_way, R.drawable.unionpay_icon);
            if (!Util.isEmpty(item.promotion_hint)) {
                helper.setText(R.id.promotion_id, item.promotion_hint);
                helper.getView(R.id.promotion_id).setVisibility(View.VISIBLE);
            } else {
                helper.getView(R.id.promotion_id).setVisibility(View.GONE);
            }
        }

        if (item.channel.equals("WxAppPay")) {
            helper.getItemView().setVisibility(View.VISIBLE);
            helper.setText(R.id.text, "微信支付");
            helper.setImageResource(R.id.img_way, R.drawable.weixinpay);
            if (!Util.isEmpty(item.promotion_hint)) {
                helper.setText(R.id.promotion_id, item.promotion_hint);
                helper.getView(R.id.promotion_id).setVisibility(View.VISIBLE);
            } else {
                helper.getView(R.id.promotion_id).setVisibility(View.GONE);
            }
        }

        if (item.channel.equals("AliAppPay")) {
            helper.getItemView().setVisibility(View.VISIBLE);
            helper.setText(R.id.text, "支付宝");
            helper.setImageResource(R.id.img_way, R.drawable.alipay);
            if (!Util.isEmpty(item.promotion_hint)) {
                helper.setText(R.id.promotion_id, item.promotion_hint);
                helper.getView(R.id.promotion_id).setVisibility(View.VISIBLE);
            } else {
                helper.getView(R.id.promotion_id).setVisibility(View.GONE);
            }
        }

        if ((item.channel.equals("UPQRQuickPay") || item.channel.equals("UPQRPay"))&&item.purpose == null) {
            helper.getItemView().setVisibility(View.VISIBLE);
            helper.setText(R.id.text, item.card.getIss_ins_name() + item.card.getCard_type_name() + "(" + item.card.getCard_no() + ")");
            helper.setImageUrl(R.id.img_way, item.card.getIss_ins_icon());
            if (item.enabled) {
                if (!Util.isEmpty(item.promotion_hint)) {
                    helper.setText(R.id.promotion_id, item.promotion_hint);
                    helper.getView(R.id.promotion_id).setVisibility(View.VISIBLE);
                    ((TextView) helper.getView(R.id.promotion_id)).setTextColor(context.getColor(R.color.axf_common_blue));
                } else {
                    helper.getView(R.id.promotion_id).setVisibility(View.GONE);
                }
            } else {
                helper.getView(R.id.promotion_id).setVisibility(View.VISIBLE);
                ((TextView) helper.getView(R.id.promotion_id)).setTextColor(context.getColor(R.color.axf_light_gray));
                ((TextView) helper.getView(R.id.promotion_id)).setText("该银行卡不支持当前交易");
            }
        }

        if (item.purpose != null && item.purpose.equals("UPQRQuickPayOpenCard")) {
            if (!Util.isEmpty(item.promotion_hint)) {
                helper.setText(R.id.promotion_id, item.promotion_hint);
                helper.getView(R.id.promotion_id).setVisibility(View.VISIBLE);
            } else {
                helper.getView(R.id.promotion_id).setVisibility(View.GONE);
            }
            helper.setText(R.id.text, "添加新银行卡付款");
            helper.setImageResource(R.id.img_way, R.drawable.union_icon);
            helper.getView(R.id.into).setVisibility(View.VISIBLE);
            helper.getItemView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (clickListener2 != null) {
                        clickListener2.way();
                    }

                    if (App.hasBankCard) {
                        Intent intent = new Intent(context, UnionHtmlActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putBoolean(UnionHtmlActivity.EXTRA_FROM_BANKCARDLIST, false);
                        intent.putExtras(bundle);
                        context.startActivity(new Intent(intent));
                    } else {
                        context.startActivity(new Intent(context, UnionSweptEmptyCardActivity.class));
                    }
                }
            });

        }

        helper.getItemView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (item.enabled) {
                    if (clickListener != null) {
                        clickListener.way(item);
                    }
                }
            }
        });
    }


    public interface fristSelectPayWay {
        void way(PayMethod item);
    }

    public void getFristPayWay(fristSelectPayWay fristSelectPayWay) {
        this.fristSelectPayWay = fristSelectPayWay;
    }


    public interface clickListener {
        void way(PayMethod item);
    }

    public void onItemclickListener(clickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface clickListener2 {
        void way();
    }

    public void onItemclickListener2(AllPayWayAdapter.clickListener2 clickListener2) {
        this.clickListener2 = clickListener2;
    }
}
