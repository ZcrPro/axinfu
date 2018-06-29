package com.zhihuianxin.xyaxf.app.unionqr_pay.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.xyaxf.axpay.Util;
import com.zhihuianxin.xyaxf.app.AppConstant;
import modellib.service.CustomerService;
import modellib.thrift.base.PayMethod;
import modellib.thrift.unqr.RealName;
import modellib.thrift.unqr.RealNameAuthStatus;
import com.google.gson.Gson;
import com.pacific.adapter.RecyclerAdapter;
import com.pacific.adapter.RecyclerAdapterHelper;
import com.zhihuianxin.xyaxf.App;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.ApiFactory;
import com.zhihuianxin.xyaxf.app.BaseSubscriber;
import com.zhihuianxin.xyaxf.app.RetrofitFactory;
import com.zhihuianxin.xyaxf.app.unionqr_pay.presenter.UnionPayPwdPresenter;
import com.zhihuianxin.xyaxf.app.unionqr_pay.view.activity.UnionCertificationActivity;
import com.zhihuianxin.xyaxf.app.unionqr_pay.view.activity.UnionHtmlActivity;
import com.zhihuianxin.xyaxf.app.unionqr_pay.view.activity.UnionSweptEmptyCardActivity;
import com.zhihuianxin.xyaxf.app.utils.NetUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by zcrpro on 2017/11/17.
 */

public class UnionPayWayDefAdapter extends RecyclerAdapter<PayMethod> {

    private fristSelectPayWay fristSelectPayWay;
    private Context context;
    private List<PayMethod> payMethods;

    public UnionPayWayDefAdapter(Context context, @NonNull int... layoutResIds) {
        super(context, layoutResIds);
    }

    public UnionPayWayDefAdapter(Context context, List<PayMethod> payMethods, @Nullable List<PayMethod> data, @NonNull int... layoutResIds) {
        super(context, data, layoutResIds);
        this.context = context;
        this.payMethods = payMethods;
    }

    @Override
    protected void convert(final RecyclerAdapterHelper helper, final PayMethod item) {
        if (item.purpose != null && item.purpose.equals("UPQRQuickPayOpenCard")) {
            helper.setText(R.id.text, "添加新银行卡付款");
            if (!Util.isEmpty(item.promotion_hint)) {
                helper.setText(R.id.promotion_id, item.promotion_hint);
                helper.getView(R.id.promotion_id).setVisibility(View.VISIBLE);
            } else {
                helper.getView(R.id.promotion_id).setVisibility(View.GONE);
            }
            helper.setImageResource(R.id.img_way, R.drawable.union_icon);

            helper.getItemView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
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

            helper.getView(R.id.tv_more_pay_way).setVisibility(View.GONE);

        } else {
            if (item.channel.equals("UPQRQuickPay")&&item.purpose == null||item.channel.equals("UPQRPay")&&item.purpose == null) {
                helper.getItemView().setVisibility(View.VISIBLE);
                helper.setText(R.id.text, item.card.getIss_ins_name() +item.card.getCard_type_name() +" " + "(" + item.card.getCard_no() + ")");
                helper.setImageUrl(R.id.img_way,item.card.getIss_ins_icon());
                if (!Util.isEmpty(item.promotion_hint)) {
                    helper.setText(R.id.promotion_id, item.promotion_hint);
                    helper.getView(R.id.promotion_id).setVisibility(View.VISIBLE);
                } else {
                    helper.getView(R.id.promotion_id).setVisibility(View.GONE);
                }
            }

            if (item.channel.equals("UnionPay")) {
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

            helper.getItemView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final UnionPayWayDialog unionPayWayDialog = new UnionPayWayDialog(context, payMethods);
                    unionPayWayDialog.show();
                    unionPayWayDialog.setOnNextListener(new UnionPayWayDialog.OnNextListener() {
                        @Override
                        public void onNext(PayMethod data) {
                            unionPayWayDialog.dismiss();
                            if (fristSelectPayWay!=null)
                                fristSelectPayWay.way(data);
                        }
                    });
                }
            });
        }
    }

    public interface fristSelectPayWay {
        void way(PayMethod item);
    }

    public void getFristPayWay(fristSelectPayWay fristSelectPayWay) {
        this.fristSelectPayWay = fristSelectPayWay;
    }

    public void changePayWay(PayMethod payMethod) {
        data.clear();
        data.add(payMethod);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return 1;
    }


    public void getRealName() {
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String,Object> map = new HashMap<>();
        CustomerService meService = ApiFactory.getFactory().create(CustomerService.class);
        meService.getRealNameQR(NetUtils.getRequestParams(context,map),NetUtils.getSign(NetUtils.getRequestParams(context,map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(context,true,null) {
                    @Override
                    public void onNext(Object o) {
                        UnionPayPwdPresenter.getRealNameResponse response = new Gson().fromJson(o.toString(),UnionPayPwdPresenter.getRealNameResponse.class);
                        if (response.resp.resp_code.equals(AppConstant.SUCCESS)){
                            RealName realName =  response.realname;
                            if(realName.status.equals(RealNameAuthStatus.OK.name())){
                                Intent i = new Intent(context,UnionHtmlActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putBoolean(UnionHtmlActivity.EXTRA_FROM_UPQRCASHIER,false);
                                i.putExtras(bundle);
                                context.startActivity(i);
                            } else if(realName.status.equals(RealNameAuthStatus.FAILED.name())){

                            } else if(realName.status.equals(RealNameAuthStatus.NotAuth.name())){
                                Intent i = new Intent(context,UnionCertificationActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putBoolean(UnionHtmlActivity.EXTRA_FROM_UPQRCASHIER,true);
                                bundle.putBoolean("isOcp",true);
                                if(((Activity)context).getIntent().getExtras()!=null && ((Activity)context).getIntent().getExtras().getString(UnionSweptEmptyCardActivity.EXTRA_FROM_EMPTY)!=null){
                                    bundle.putString(UnionSweptEmptyCardActivity.EXTRA_FROM_EMPTY,((Activity)context).getIntent().getExtras().getString(UnionSweptEmptyCardActivity.EXTRA_FROM_EMPTY));
                                }
                                i.putExtras(bundle);
                                context.startActivity(i);
                            } else{// Pending

                            }
                        }

                    }
                });
    }
}
