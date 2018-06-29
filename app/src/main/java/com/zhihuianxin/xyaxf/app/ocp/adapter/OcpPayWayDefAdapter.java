package com.zhihuianxin.xyaxf.app.ocp.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.xyaxf.axpay.Util;
import com.zhihuianxin.xyaxf.app.AppConstant;
import com.axinfu.modellib.service.CustomerService;
import com.axinfu.modellib.thrift.unqr.RealName;
import com.axinfu.modellib.thrift.unqr.RealNameAuthStatus;
import com.google.gson.Gson;
import com.pacific.adapter.RecyclerAdapter;
import com.pacific.adapter.RecyclerAdapterHelper;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.zhihuianxin.xyaxf.App;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.ApiFactory;
import com.zhihuianxin.xyaxf.app.RetrofitFactory;
import com.zhihuianxin.xyaxf.app.ocp.OcpPayWayDialog;
import com.zhihuianxin.xyaxf.app.ocp.PayWayTagData;
import com.zhihuianxin.xyaxf.app.ocp.QrResultActivity;
import com.zhihuianxin.xyaxf.app.unionqr_pay.presenter.UnionPayPwdPresenter;
import com.zhihuianxin.xyaxf.app.unionqr_pay.view.activity.UnionCertificationActivity;
import com.zhihuianxin.xyaxf.app.unionqr_pay.view.activity.UnionHtmlActivity;
import com.zhihuianxin.xyaxf.app.unionqr_pay.view.activity.UnionSweptEmptyCardActivity;
import com.zhihuianxin.xyaxf.app.utils.NetUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by zcrpro on 2017/11/17.
 */

public class OcpPayWayDefAdapter extends RecyclerAdapter<QrResultActivity.PayMethod> {

    private fristSelectPayWay fristSelectPayWay;
    private Context context;
    private List<QrResultActivity.PayMethod> payMethods;

    public static boolean hasNews = false;

    public OcpPayWayDefAdapter(Context context, @NonNull int... layoutResIds) {
        super(context, layoutResIds);
    }

    public OcpPayWayDefAdapter(Context context, List<QrResultActivity.PayMethod> payMethods, @Nullable List<QrResultActivity.PayMethod> data, @NonNull int... layoutResIds) {
        super(context, data, layoutResIds);
        this.context = context;
        this.payMethods = payMethods;
        data.get(0).isSelected = true;

        hasNews=false;
        List<PayWayTagData> payWayTagData = SQLite.select().from(PayWayTagData.class).queryList();
        for (int i = 0; i < payWayTagData.size(); i++) {
            if (payWayTagData.get(i).isNews) {
                hasNews = true;
            }
        }
        notifyDataSetChanged();
    }

    @Override
    protected void convert(final RecyclerAdapterHelper helper, final QrResultActivity.PayMethod item) {

        if (payMethods.size() == 1) {
            helper.getView(R.id.tv_more_pay_way).setVisibility(View.GONE);
            helper.getItemView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getRealName();
                }
            });
        } else {
            if (hasNews) {
                Drawable top = context.getResources().getDrawable(R.mipmap.new_tag);
                ((TextView) helper.getView(R.id.tv_more_pay_way)).setCompoundDrawablesWithIntrinsicBounds(null, top, null, null);
            }
        }

        if (item.purpose != null && item.purpose.equals("UPQRQuickPayOpenCard")) {
            helper.setText(R.id.text, "添加新银行卡付款");
            if (!Util.isEmpty(item.promotion_hint)) {
                helper.setText(R.id.promotion_id, item.promotion_hint);
                helper.getView(R.id.promotion_id).setVisibility(View.VISIBLE);
            } else {
                helper.getView(R.id.promotion_id).setVisibility(View.GONE);
            }
            helper.setImageResource(R.id.img_way, R.drawable.union_icon);
            helper.getView(R.id.ll).setOnClickListener(new View.OnClickListener() {
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

            helper.getItemView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    OcpPayWayDialog ocpPayWayDialog = new OcpPayWayDialog(context, payMethods);
                    ocpPayWayDialog.show();
                    ocpPayWayDialog.setOnNextListener(new OcpPayWayDialog.OnNextListener() {
                        @Override
                        public void onNext(QrResultActivity.PayMethod data) {
                            changePayWay(data);
                        }
                    });
                }
            });

        } else {
            if (item.channel.equals("UPQRQuickPay") && item.purpose == null) {
                helper.getItemView().setVisibility(View.VISIBLE);
                helper.setText(R.id.text, item.card.iss_ins_name + item.card.card_type_name + " " + "(" + item.card.card_no + ")");
                helper.setImageUrl(R.id.img_way, item.card.iss_ins_icon);
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
                    if (payMethods.size() == 1) {

                    } else {
                        hasNews = false;
                        OcpPayWayDialog ocpPayWayDialog = new OcpPayWayDialog(context, payMethods);
                        ocpPayWayDialog.show();
                        ocpPayWayDialog.setOnNextListener(new OcpPayWayDialog.OnNextListener() {
                            @Override
                            public void onNext(QrResultActivity.PayMethod data) {
                                changePayWay(data);
                            }
                        });
                    }
                }
            });
        }
        if (item.isSelected && item.enabled) {
            if (fristSelectPayWay != null) {
                fristSelectPayWay.way(item);
            }
        } else if (item.isSelected && !item.enabled) {
            if (fristSelectPayWay != null) {
                fristSelectPayWay.way(data.get(data.size() - 1));
            }
        }

    }

    public interface fristSelectPayWay {
        void way(QrResultActivity.PayMethod item);
    }

    public void getFristPayWay(fristSelectPayWay fristSelectPayWay) {
        this.fristSelectPayWay = fristSelectPayWay;
    }

    public void changePayWay(QrResultActivity.PayMethod payMethod) {
        data.clear();
        data.add(payMethod);
        notifyDataSetChanged();
    }

    public void updatePayWay(List<QrResultActivity.PayMethod> payMethods) {
        OcpPayWayDialog ocpPayWayDialog = new OcpPayWayDialog(context, payMethods);
        ocpPayWayDialog.show();
        ocpPayWayDialog.setOnNextListener(new OcpPayWayDialog.OnNextListener() {
            @Override
            public void onNext(QrResultActivity.PayMethod data) {
                changePayWay(data);
            }
        });
    }


    @Override
    public int getItemCount() {
        return 1;
    }


    public void getRealName() {
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String, Object> map = new HashMap<>();
        CustomerService meService = ApiFactory.getFactory().create(CustomerService.class);
        meService.getRealNameQR(NetUtils.getRequestParams(context, map), NetUtils.getSign(NetUtils.getRequestParams(context, map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Object>() {

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }

                    @Override
                    public void onNext(Object o) {
                        UnionPayPwdPresenter.getRealNameResponse response = new Gson().fromJson(o.toString(), UnionPayPwdPresenter.getRealNameResponse.class);
                        if (response.resp.resp_code.equals(AppConstant.SUCCESS)) {
                            RealName realName = response.realname;
                            if (realName.status.equals(RealNameAuthStatus.OK.name())) {
                                Intent i = new Intent(context, UnionHtmlActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putBoolean(UnionHtmlActivity.EXTRA_FROM_UPQRCASHIER, false);
                                i.putExtras(bundle);
                                context.startActivity(i);
                            } else if (realName.status.equals(RealNameAuthStatus.FAILED.name())) {

                            } else if (realName.status.equals(RealNameAuthStatus.NotAuth.name())) {
                                Intent i = new Intent(context, UnionCertificationActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putBoolean(UnionHtmlActivity.EXTRA_FROM_UPQRCASHIER, true);
                                bundle.putBoolean("isOcp", true);
                                if (((Activity) context).getIntent().getExtras() != null && ((Activity) context).getIntent().getExtras().getString(UnionSweptEmptyCardActivity.EXTRA_FROM_EMPTY) != null) {
                                    bundle.putString(UnionSweptEmptyCardActivity.EXTRA_FROM_EMPTY, ((Activity) context).getIntent().getExtras().getString(UnionSweptEmptyCardActivity.EXTRA_FROM_EMPTY));
                                }
                                i.putExtras(bundle);
                                context.startActivity(i);
                            } else {// Pending

                            }
                        }

                    }
                });
    }
}
