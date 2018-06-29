package com.zhihuianxin.xyaxf.app.ocp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.pacific.adapter.RecyclerAdapter;
import com.pacific.adapter.RecyclerAdapterHelper;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.zhihuianxin.xyaxf.app.base.axutil.Util;
import com.zhihuianxin.xyaxf.App;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.ocp.PayWayTagData;
import com.zhihuianxin.xyaxf.app.ocp.QrResultActivity;
import com.zhihuianxin.xyaxf.app.unionqr_pay.view.activity.UnionHtmlActivity;
import com.zhihuianxin.xyaxf.app.unionqr_pay.view.activity.UnionSweptEmptyCardActivity;

import java.util.List;

/**
 * Created by zcrpro on 2017/11/24.
 */

public class OcpAllPayWayAdapter extends RecyclerAdapter<QrResultActivity.PayMethod> {

    private fristSelectPayWay fristSelectPayWay;
    private clickListener clickListener;
    private clickListener2 clickListener2;
    private Context context;

    List<PayWayTagData> payWayTagData;

    public OcpAllPayWayAdapter(Context context, @NonNull int... layoutResIds) {
        super(context, layoutResIds);
    }

    public OcpAllPayWayAdapter(Context context, @Nullable List<QrResultActivity.PayMethod> data, @NonNull int... layoutResIds) {
        super(context, data, layoutResIds);
        Log.d("OcpAllPayWayAdapter", new Gson().toJson(data));
        this.context = context;
        assert data != null;
        payWayTagData = SQLite.select().from(PayWayTagData.class).queryList();
        notifyDataSetChanged();
    }

    @SuppressLint("NewApi")
    @Override
    protected void convert(final RecyclerAdapterHelper helper, final QrResultActivity.PayMethod item) {

        for (int i = 0; i <payWayTagData.size() ; i++) {
            if (item.card!=null){
                if ((payWayTagData.get(i).cardId.equals(item.channel)||payWayTagData.get(i).cardId.equals(item.card.id))&&payWayTagData.get(i).isNews){
                    helper.getView(R.id.img_red).setVisibility(View.VISIBLE);
                }else {
                    helper.getView(R.id.img_red).setVisibility(View.GONE);
                }
            }else {
                if ((payWayTagData.get(i).cardId.equals(item.channel)&&payWayTagData.get(i).isNews)){
                    helper.getView(R.id.img_red).setVisibility(View.VISIBLE);
                }else {
                    helper.getView(R.id.img_red).setVisibility(View.GONE);
                }
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

                    if (App.hasBankCard){
                        Intent intent = new Intent(context, UnionHtmlActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putBoolean(UnionHtmlActivity.EXTRA_FROM_BANKCARDLIST, false);
                        intent.putExtras(bundle);
                        context.startActivity(new Intent(intent));
                    }else {
                        context.startActivity(new Intent(context, UnionSweptEmptyCardActivity.class));
                    }
                }
            });

        } else {
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
            } else if (item.channel.equals("WxAppPay")) {
                helper.getItemView().setVisibility(View.VISIBLE);
                helper.setText(R.id.text, "微信支付");
                helper.setImageResource(R.id.img_way, R.drawable.weixinpay);
                if (!Util.isEmpty(item.promotion_hint)) {
                    helper.setText(R.id.promotion_id, item.promotion_hint);
                    helper.getView(R.id.promotion_id).setVisibility(View.VISIBLE);
                } else {
                    helper.getView(R.id.promotion_id).setVisibility(View.GONE);
                }
            } else if (item.channel.equals("AliAppPay")) {
                helper.getItemView().setVisibility(View.VISIBLE);
                helper.setText(R.id.text, "支付宝");
                helper.setImageResource(R.id.img_way, R.drawable.alipay);
                if (!Util.isEmpty(item.promotion_hint)) {
                    helper.setText(R.id.promotion_id, item.promotion_hint);
                    helper.getView(R.id.promotion_id).setVisibility(View.VISIBLE);
                } else {
                    helper.getView(R.id.promotion_id).setVisibility(View.GONE);
                }
            } else if (item.channel.equals("UPQRQuickPay")) {
                helper.getItemView().setVisibility(View.VISIBLE);
                helper.setText(R.id.text, item.card.iss_ins_name + item.card.card_type_name + "(" + item.card.card_no + ")");
                helper.setImageUrl(R.id.img_way, item.card.iss_ins_icon);
                if (item.enabled){
                    if (!Util.isEmpty(item.promotion_hint)) {
                        helper.setText(R.id.promotion_id, item.promotion_hint);
                        helper.getView(R.id.promotion_id).setVisibility(View.VISIBLE);
                        ((TextView)helper.getView(R.id.promotion_id)).setTextColor(context.getColor(R.color.axf_common_blue));
                    } else {
                        helper.getView(R.id.promotion_id).setVisibility(View.GONE);
                    }
                }else {
                    helper.getView(R.id.promotion_id).setVisibility(View.VISIBLE);
                    ((TextView)helper.getView(R.id.promotion_id)).setTextColor(context.getColor(R.color.axf_light_gray));
                    ((TextView)helper.getView(R.id.promotion_id)).setText("该银行卡不支持当前交易");
                }
            } else {
                helper.getItemView().setVisibility(View.GONE);
            }

            helper.getItemView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (item.enabled){
                        if (clickListener != null) {
                            clickListener.way(item);
                        }
                        if (item.isSelected) {

                        } else {
                            for (int i = 0; i < data.size(); i++) {
                                data.get(i).isSelected = false;
                            }
                            item.isSelected = true;
                        }
                        notifyDataSetChanged();
                    }
                }
            });
        }
    }

    public interface fristSelectPayWay {
        void way(QrResultActivity.PayMethod item);
    }

    public void getFristPayWay(fristSelectPayWay fristSelectPayWay) {
        this.fristSelectPayWay = fristSelectPayWay;
    }


    public interface clickListener {
        void way(QrResultActivity.PayMethod item);
    }

    public void onItemclickListener(clickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface clickListener2 {
        void way();
    }

    public void onItemclickListener2(OcpAllPayWayAdapter.clickListener2 clickListener2) {
        this.clickListener2 = clickListener2;
    }
}
