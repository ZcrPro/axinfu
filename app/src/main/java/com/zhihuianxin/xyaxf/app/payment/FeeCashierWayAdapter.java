package com.zhihuianxin.xyaxf.app.payment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.zhihuianxin.xyaxf.app.AppConstant;
import modellib.service.EcardService;
import modellib.service.LoanService;
import modellib.service.MeService;
import modellib.service.PaymentService;
import modellib.thrift.app.PluginInfo;
import modellib.thrift.app.Update;
import modellib.thrift.bankcard.UPCardType;
import modellib.thrift.base.BaseResponse;
import modellib.thrift.base.PayChannel;
import modellib.thrift.base.PayMethod;
import modellib.thrift.customer.Customer;
import modellib.thrift.payment.BankLimit;
import modellib.thrift.payment.PaymentOrder;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.pacific.adapter.RecyclerAdapter;
import com.pacific.adapter.RecyclerAdapterHelper;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.xyaxf.axpay.Util;
import com.xyaxf.axpay.modle.PayECardRequestData;
import com.xyaxf.axpay.modle.PayFeeRequestData;
import com.xyaxf.axpay.modle.PayFor;
import com.xyaxf.axpay.modle.PayRequest;
import com.xyaxf.axpay.modle.PaymentInfo;
import com.zhihuianxin.xyaxf.App;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.ApiFactory;
import com.zhihuianxin.xyaxf.app.BaseSubscriber;
import com.zhihuianxin.xyaxf.app.RetrofitFactory;
import com.zhihuianxin.xyaxf.app.ecard.Ecardpresenter;
import com.zhihuianxin.xyaxf.app.me.presenter.MeCheckUpdatePresenter;
import com.zhihuianxin.xyaxf.app.pay.MyPayActivity;
import com.zhihuianxin.xyaxf.app.pay.guiyang.AutonymSuccActivity;
import com.zhihuianxin.xyaxf.app.pay.guiyang.IntroduceActivity;
import com.zhihuianxin.xyaxf.app.pay.guiyang.status.ErrorActivity;
import com.zhihuianxin.xyaxf.app.pay.guiyang.status.ProcessingActivity;
import com.zhihuianxin.xyaxf.app.service.DownloadAPKService;
import com.zhihuianxin.xyaxf.app.unionqr_pay.view.activity.UnionHtmlActivity;
import com.zhihuianxin.xyaxf.app.unionqr_pay.view.activity.UnionSweptEmptyCardActivity;
import com.zhihuianxin.xyaxf.app.utils.AnimatorUtil;
import com.zhihuianxin.xyaxf.app.utils.DensityUtil;
import com.zhihuianxin.xyaxf.app.utils.NetUtils;
import com.zhihuianxin.xyaxf.app.view.DownloadGysdkDialog;
import com.zhihuianxin.xyaxf.app.view.DownloadGysdkProgressDialog;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.realm.RealmObject;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.zhihuianxin.xyaxf.R.id.ll_item;
import static com.zhihuianxin.xyaxf.R.id.tv_bank_name;
import static com.zhihuianxin.xyaxf.R.id.tv_bank_trade;

/**
 * Created by zcrpro on 2016/11/29.
 */
public class FeeCashierWayAdapter extends RecyclerAdapter<PayMethod> {
    private PayFeeRequestData payFeeRequestData;
    private PayECardRequestData payECardRequestData;
    private Float amount;

    private Context context;
    private static final int SDK_PAY_FLAG = 1;

    private onDissMiss listener2;

    private PayRequest payRequest;
    private PayOderResponse payOderResponse;
    private GyPayOderResponses gyPayOderResponses;
    private String name;
    private String idCard;
    private DownloadGysdkDialog downloadGysdkDialog = null;
    private DownloadGysdkProgressDialog progressDialog = null;

    private boolean isLoading = true;
    private GetPayRequest getPayRequest;

    private List<PayMethod> bank_pay_list;

    private int cardConut; //银行卡支付的个数

    /**
     * 贵阳银行相关信息
     */
    public boolean GuiyangIsOpen = false;
    public boolean GuiyangIsAble = false;
    public float GuiyangMin;
    public String realname_auth_status;

    public class GetCustomer {
        public BaseResponse resp;
        public Customer customer;
    }

    public class LoanAccountInfoRep {
        public BaseResponse resp;
        public LoanAccountInfo loan_account_info;
        public String realname_auth_status;
    }

    public class LoanAccountInfo {
        public BaseResponse resp;
        public String id_card_no;
        public String name;
        public List<LoanWayAccountInfo> valid_loan_way_account_info;
    }

    public class LoanWayAccountInfo {
        public String card_no;                                            // 卡号
        public String credit_max_amt;                                        // 授信最大金额额度
        public String loan_amt;                                            // 借款总金额
        public LoanWay loan_way;                                        // 通道信息
        public String status;
    }

    public class LoanWay {
        public String name;                        // 通道名称
        public String type;                    // 通道类型
        public String remark;            // 通道备注
        public String min_loan_amt;                // 最低贷款金额
    }

    /**
     * 缴费
     *
     * @param context
     * @param amount
     * @param payFeeRequestData
     * @param data
     * @param layoutResIds
     */
    public FeeCashierWayAdapter(Context context, Float amount, PayFeeRequestData payFeeRequestData, @Nullable List<PayMethod> data, @Nullable List<PayMethod> bank_pay_list, @NonNull int... layoutResIds) {
        super(context, data, layoutResIds);
        this.payFeeRequestData = payFeeRequestData;
        this.amount = amount;
        this.context = context;
        this.bank_pay_list = bank_pay_list;
        //初始化一个被选中的支付方式
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).is_default) {
                data.get(i).isSelected = true;
            }
        }

        assert bank_pay_list != null;
        for (int i = 0; i < bank_pay_list.size(); i++) {
            if (bank_pay_list.get(i).channel.equals("UPTokenPay")) {
                cardConut = cardConut + 1;
            }
        }

        //去除银行卡支付非默认的支付方式
        notifyDataSetChanged();
        getAccountPayMethodInfo(amount);
    }

    private void initDialog(){
        if(downloadGysdkDialog == null){
            downloadGysdkDialog = new DownloadGysdkDialog(context);
        }
        if(progressDialog == null){
            progressDialog = new DownloadGysdkProgressDialog(context);
        }
    }

    public FeeCashierWayAdapter(Context context, Float amount, PayECardRequestData payECardRequestData, @Nullable List<PayMethod> data, @Nullable List<PayMethod> bank_pay_list, @NonNull int... layoutResIds) {
        super(context, data, layoutResIds);
        this.payECardRequestData = payECardRequestData;
        this.amount = amount;
        this.context = context;
        this.bank_pay_list = bank_pay_list;
        //初始化一个被选中的支付方式
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).is_default) {
                data.get(i).isSelected = true;
            }
        }

        assert bank_pay_list != null;
        for (int i = 0; i < bank_pay_list.size(); i++) {
            if (bank_pay_list.get(i).channel.equals("UPTokenPay")) {
                cardConut = cardConut + 1;
            }
        }

        //去除银行卡支付非默认的支付方式
        notifyDataSetChanged();
        getAccountPayMethodInfo(amount);
    }

    private void getAccountPayMethodInfo(final Float amount) {
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String, Object> map = new HashMap<>();
        map.put("loan_way_type", "GuiYangCreditLoanPay");
        LoanService loanService = ApiFactory.getFactory().create(LoanService.class);
        loanService.get_account_info(NetUtils.getRequestParams(context, map), NetUtils.getSign(NetUtils.getRequestParams(context, map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(context, true, null) {
                    @Override
                    public void onNext(Object o) {
                        final LoanAccountInfoRep loanAccountInfoRep = new Gson().fromJson(o.toString(), LoanAccountInfoRep.class);
                        if (loanAccountInfoRep.resp.resp_code.equals(AppConstant.SUCCESS)) {
                            isLoading = false;
                            realname_auth_status = loanAccountInfoRep.realname_auth_status;
                            name = loanAccountInfoRep.loan_account_info.name;
                            idCard = loanAccountInfoRep.loan_account_info.id_card_no;
                            for (int i = 0; i < loanAccountInfoRep.loan_account_info.valid_loan_way_account_info.size(); i++) {
                                if (loanAccountInfoRep.loan_account_info.valid_loan_way_account_info.get(i).loan_way.type.equals("GuiYangCreditLoanPay") && loanAccountInfoRep.loan_account_info.valid_loan_way_account_info.get(i).status.equals("OK")) {
                                    //贵阳银行开通
                                    GuiyangIsOpen = true;
                                    if (loanAccountInfoRep.loan_account_info.valid_loan_way_account_info.get(i).loan_way.min_loan_amt != null) {
                                        if (amount < Float.parseFloat(loanAccountInfoRep.loan_account_info.valid_loan_way_account_info.get(i).loan_way.min_loan_amt)) {
                                            GuiyangIsAble = false;
                                            GuiyangMin = Float.parseFloat(loanAccountInfoRep.loan_account_info.valid_loan_way_account_info.get(i).loan_way.min_loan_amt);
                                        } else {
                                            GuiyangIsAble = true;
                                        }
                                    }
                                } else {
                                    //未开通
                                    GuiyangIsOpen = false;
                                }
                            }
                        }
                        notifyDataSetChanged();
                    }
                });
    }


    @SuppressLint("ResourceAsColor")
    @Override
    protected void convert(final RecyclerAdapterHelper helper, final PayMethod item) {

        helper.setImageResource(R.id.img_way, R.drawable.default_image);
        helper.getView(R.id.tv_xinyong_no_ent).setVisibility(View.GONE);
        helper.getView(R.id.into_more).setVisibility(View.GONE);

        if (item.isSelected) {
            helper.getView(R.id.rl_item).setBackgroundResource(R.drawable.fee_cashier_item_bg);
            /**
             *外派这个支付方式
             */
            if (getPayRequest != null)
                getPayRequest.getPayRequest(item);

        } else {
            helper.getView(R.id.rl_item).setBackgroundResource(0);
        }

        if (item.channel.equals("UnionPay")) {
            ((TextView) helper.getView(R.id.text)).setTextColor(Color.parseColor("#000000"));
            ((TextView) helper.getView(R.id.promotion_id)).setTextColor(Color.parseColor("#ff4867"));
            helper.setText(R.id.text, "银联在线支付");
            helper.setImageResource(R.id.img_way, R.mipmap.shanfu);
            if (!Util.isEmpty(item.promotion_hint)) {
                helper.getView(R.id.promotion_id).setVisibility(View.VISIBLE);
                helper.setText(R.id.promotion_id, item.promotion_hint);
            } else {
                helper.getView(R.id.promotion_id).setVisibility(View.GONE);
            }

            helper.getView(R.id.uppay_log).setVisibility(View.VISIBLE);
        }

        if (item.channel.equals("CCBWapPay")) {
            ((TextView) helper.getView(R.id.text)).setTextColor(Color.parseColor("#000000"));
            ((TextView) helper.getView(R.id.promotion_id)).setTextColor(Color.parseColor("#ff4867"));
            helper.setText(R.id.text, "建设银行");
            helper.setImageResource(R.id.img_way, R.drawable.ccb);
            if (!Util.isEmpty(item.promotion_hint)) {
                helper.getView(R.id.promotion_id).setVisibility(View.VISIBLE);
                helper.setText(R.id.promotion_id, item.promotion_hint);
            } else {
                helper.getView(R.id.promotion_id).setVisibility(View.GONE);
            }

            helper.getView(R.id.uppay_log).setVisibility(View.GONE);
        }

        if (item.channel.equals("RFID")) {
            ((TextView) helper.getView(R.id.text)).setTextColor(Color.parseColor("#000000"));
            ((TextView) helper.getView(R.id.promotion_id)).setTextColor(Color.parseColor("#ff4867"));
            helper.setText(R.id.text, "一卡通");
            helper.setImageResource(R.id.img_way, R.drawable.cardpay);
            if (!Util.isEmpty(item.promotion_hint)) {
                helper.getView(R.id.promotion_id).setVisibility(View.VISIBLE);
                helper.setText(R.id.promotion_id, item.promotion_hint);
            } else {
                helper.getView(R.id.promotion_id).setVisibility(View.GONE);
            }

            helper.getView(R.id.uppay_log).setVisibility(View.GONE);
        }

        if (item.channel.equals("QuickPay")) {
            ((TextView) helper.getView(R.id.text)).setTextColor(Color.parseColor("#000000"));
            ((TextView) helper.getView(R.id.promotion_id)).setTextColor(Color.parseColor("#ff4867"));
            helper.setText(R.id.text, "农行卡支付");
            helper.setImageResource(R.id.img_way, R.drawable.nonghang_pay_logo);
            if (!Util.isEmpty(item.promotion_hint)) {
                helper.getView(R.id.promotion_id).setVisibility(View.VISIBLE);
                helper.setText(R.id.promotion_id, item.promotion_hint);
            } else {
                helper.getView(R.id.promotion_id).setVisibility(View.GONE);
            }

            helper.getView(R.id.uppay_log).setVisibility(View.GONE);
        }

        if (item.channel.equals("AliAppPay")) {
            ((TextView) helper.getView(R.id.text)).setTextColor(Color.parseColor("#000000"));
            ((TextView) helper.getView(R.id.promotion_id)).setTextColor(Color.parseColor("#ff4867"));
            helper.setText(R.id.text, "支付宝支付");
            helper.setImageResource(R.id.img_way, R.drawable.alipay);
            if (!Util.isEmpty(item.promotion_hint)) {
                helper.getView(R.id.promotion_id).setVisibility(View.VISIBLE);
                helper.setText(R.id.promotion_id, item.promotion_hint);
            } else {
                helper.getView(R.id.promotion_id).setVisibility(View.GONE);
            }

            helper.getView(R.id.uppay_log).setVisibility(View.GONE);
        }

        if (item.channel.equals("WxAppPay")) {
            ((TextView) helper.getView(R.id.text)).setTextColor(Color.parseColor("#000000"));
            ((TextView) helper.getView(R.id.promotion_id)).setTextColor(Color.parseColor("#ff4867"));
            helper.setText(R.id.text, "微信支付");
            helper.setImageResource(R.id.img_way, R.drawable.weixinpay);
            if (!Util.isEmpty(item.promotion_hint)) {
                helper.getView(R.id.promotion_id).setVisibility(View.VISIBLE);
                helper.setText(R.id.promotion_id, item.promotion_hint);
            } else {
                helper.getView(R.id.promotion_id).setVisibility(View.GONE);
            }

            helper.getView(R.id.uppay_log).setVisibility(View.GONE);
        }

        if (item.channel.equals("UPTokenPay")) {
            if (cardConut > 1) {
                helper.getView(R.id.into_more).setVisibility(View.VISIBLE);
                helper.getView(R.id.into_more).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        BankPayWayDialog bankPayWayDialog = new BankPayWayDialog(context, bank_pay_list);
                        bankPayWayDialog.show();
                        bankPayWayDialog.setOnNextListener(new BankPayWayDialog.OnNextListener() {
                            @Override
                            public void onNext(PayMethod data) {
                                //将目前的选择替换给当前选择的item
                                clearSelected();
                                FeeCashierWayAdapter.this.data.remove(item);
                                data.is_default = true;
                                FeeCashierWayAdapter.this.data.add(0, data);
                                notifyDataSetChanged();
                                if (getPayRequest != null)
                                    getPayRequest.getPayRequest(data);
                            }
                        });
                    }
                });
            } else {
                helper.getView(R.id.into_more).setVisibility(View.GONE);
            }

            if (item.purpose != null) {
                ((TextView) helper.getView(R.id.text)).setTextColor(Color.parseColor("#000000"));
                ((TextView) helper.getView(R.id.promotion_id)).setTextColor(Color.parseColor("#ff4867"));
                helper.setText(R.id.text, "添加新卡付款");
                helper.setImageResource(R.id.img_way, R.drawable.union_icon);
                if (!Util.isEmpty(item.promotion_hint)) {
                    helper.getView(R.id.promotion_id).setVisibility(View.VISIBLE);
                    helper.setText(R.id.promotion_id, item.promotion_hint);
                } else {
                    helper.getView(R.id.promotion_id).setVisibility(View.GONE);
                }

                helper.getView(R.id.uppay_log).setVisibility(View.GONE);
            } else {
                ((TextView) helper.getView(R.id.text)).setTextColor(Color.parseColor("#9a9a9a"));
                ((TextView) helper.getView(R.id.promotion_id)).setTextColor(Color.parseColor("#9a9a9a"));
                if (item.card.getCard_type().equals(UPCardType.Credit) || item.card.getCard_type().equals(UPCardType.QuasiCredit)) {
                    insert(item, data.size());
                    remove(helper.getAdapterPosition());
                    helper.setText(R.id.text, item.card.getIss_ins_name() + item.card.getCard_type_name() + "(" + item.card.getCard_no() + ")");
                    helper.setImageUrl(R.id.img_way, item.card.getIss_ins_icon());
                    helper.getView(R.id.promotion_id).setVisibility(View.VISIBLE);
                    helper.setText(R.id.promotion_id, "该银行卡不支持当前交易");
                } else {
                    ((TextView) helper.getView(R.id.text)).setTextColor(Color.parseColor("#000000"));
                    ((TextView) helper.getView(R.id.promotion_id)).setTextColor(Color.parseColor("#ff4867"));
                    helper.setText(R.id.text, item.card.getIss_ins_name() + item.card.getCard_type_name() + "(" + item.card.getCard_no() + ")");
                    helper.setImageUrl(R.id.img_way, item.card.getIss_ins_icon());
                    if (!Util.isEmpty(item.promotion_hint)) {
                        helper.getView(R.id.promotion_id).setVisibility(View.VISIBLE);
                        helper.setText(R.id.promotion_id, item.promotion_hint);
                    } else {
                        helper.getView(R.id.promotion_id).setVisibility(View.GONE);
                    }

                }

                helper.getView(R.id.uppay_log).setVisibility(View.GONE);
            }
        }

        if (item.channel.equals("GuiYangCreditLoanPay")) {
            ((TextView) helper.getView(R.id.text)).setTextColor(Color.parseColor("#000000"));
            ((TextView) helper.getView(R.id.promotion_id)).setTextColor(Color.parseColor("#ff4867"));
            helper.setText(R.id.text, "安心信用付");
            if (!Util.isEmpty(item.promotion_hint)) {
                helper.getView(R.id.promotion_id).setVisibility(View.VISIBLE);
                helper.setText(R.id.promotion_id, item.promotion_hint);
            } else {
                helper.getView(R.id.promotion_id).setVisibility(View.GONE);
            }

            if (isLoading) {
                helper.setImageResource(R.id.img_way, R.mipmap.axxyf_not_can_use);
                helper.getItemView().setEnabled(true);
                helper.getView(R.id.tv_xinyong_no_open).setVisibility(View.GONE);
            } else {
                if (GuiyangIsOpen) {
                    if (GuiyangIsAble) {
                        helper.setImageResource(R.id.img_way, R.mipmap.axxyf_can_use);
                        helper.getItemView().setEnabled(true);
                        helper.getView(R.id.tv_xinyong_no_open).setVisibility(View.GONE);
                        ((TextView) helper.getView(R.id.tv_xinyong_no_ent)).setVisibility(View.GONE);
                        ((TextView) helper.getView(R.id.text)).setTextColor(Color.parseColor("#000000"));
                    } else {
                        helper.setImageResource(R.id.img_way, R.mipmap.axxyf_not_can_use);
                        helper.getItemView().setEnabled(false);
                        helper.getView(R.id.tv_xinyong_no_open).setVisibility(View.GONE);
                        ((TextView) helper.getView(R.id.tv_xinyong_no_ent)).setVisibility(View.VISIBLE);
                        ((TextView) helper.getView(R.id.text)).setTextColor(Color.parseColor("#999999"));
                        ((TextView) helper.getView(R.id.tv_xinyong_no_ent)).setText("单笔支付金额" + GuiyangMin + "元起");
                    }

                } else {
                    helper.setImageResource(R.id.img_way, R.mipmap.axxyf_not_can_use);
                    helper.getView(R.id.tv_xinyong_no_open).setVisibility(View.VISIBLE);
                    ((TextView) helper.getView(R.id.text)).setTextColor(Color.parseColor("#999999"));
                }

            }

            helper.getView(R.id.uppay_log).setVisibility(View.GONE);
        }

        helper.getItemView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //直接去添加银行卡
                if (item.channel.equals("UPTokenPay") && item.purpose != null) {
                    //直接去添加银行卡
                    if (App.hasBankCard) {
                        Intent intent = new Intent(context, UnionHtmlActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putBoolean(UnionHtmlActivity.EXTRA_FROM_BANKCARDLIST, false);
                        intent.putExtras(bundle);
                        context.startActivity(new Intent(intent));
                    } else {
                        context.startActivity(new Intent(context, UnionSweptEmptyCardActivity.class));
                    }
                }  else if (item.channel.equals("GuiYangCreditLoanPay")) {
                    if (realname_auth_status != null && (realname_auth_status.equals("OK") || realname_auth_status.equals("Pending"))) {
                        //检查预授信信息
                        if (GuiyangIsOpen) {
                            if (item.isSelected) {
                                for (int i = 0; i < data.size(); i++) {
                                    data.get(i).isSelected = false;
                                }

                                if (getPayRequest != null)
                                    getPayRequest.getPayRequest(null);

                            } else {
                                for (int i = 0; i < data.size(); i++) {
                                    data.get(i).isSelected = false;
                                }
                                item.isSelected = true;

                                if (getPayRequest != null)
                                    getPayRequest.getPayRequest(item);

                            }
                            notifyDataSetChanged();
                        } else {
                            //进入介绍界面--然后进入检查预授信信息
                            //进入实名认证
//                            Bundle bundle = new Bundle();
//                            bundle.putString("name", name);
//                            bundle.putString("idCard", idCard);
//                            bundle.putFloat("amount", amount);
//                            bundle.putBoolean("isVrName", true);
//                            Intent intent = new Intent(context, IntroduceActivity.class);
//                            intent.putExtras(bundle);
//                            context.startActivity(intent);
                            checkApprovalInfo(idCard,name,amount);
                        }
                    } else if (realname_auth_status != null && realname_auth_status.equals("NotAuth")) {
                        Bundle bundle = new Bundle();
                        bundle.putString("name", name);
                        bundle.putString("idCard", idCard);
                        bundle.putFloat("amount", amount);
                        bundle.putBoolean("isVrName", false);
                        Intent intent = new Intent(context, IntroduceActivity.class);
                        intent.putExtras(bundle);
                        context.startActivity(intent);
                    } else {
                        //实名认证失败
                        context.startActivity(new Intent(context, ErrorActivity.class));
                    }
                }else {
                    if (item.isSelected) {
                        for (int i = 0; i < data.size(); i++) {
                            data.get(i).isSelected = false;
                        }

                        if (getPayRequest != null)
                            getPayRequest.getPayRequest(null);

                    } else {
                        for (int i = 0; i < data.size(); i++) {
                            data.get(i).isSelected = false;
                        }
                        item.isSelected = true;

                        if (getPayRequest != null)
                            getPayRequest.getPayRequest(item);

                    }
                    notifyDataSetChanged();
                }
            }
        });
    }

    private void getECardData(final PayRequest request) {
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String, Object> map = new HashMap<>();
        EcardService ecardService = ApiFactory.getFactory().create(EcardService.class);
        ecardService.getEcard(NetUtils.getRequestParams(context, map), NetUtils.getSign(NetUtils.getRequestParams(context, map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(context, true, null) {
                    @Override
                    public void onNext(Object o) {
                        Ecardpresenter.EcardResponse response = new Gson().fromJson(o.toString(), Ecardpresenter.EcardResponse.class);
                        if (response.ecard.status.equals("OK")) {
                            Intent intent = new Intent(mContext, CashierRFIDPayActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable(CashierRFIDPayActivity.EXTRA_PAY_REQUEST, request);
                            bundle.putString(CashierRFIDPayActivity.EXTRA_BALANCE, response.ecard.card_balance);

                            PayMethod payMethod = null;
                            for (PayMethod obj : response.ecard.pay_methods) {
                                if (obj.channel.equals(PayChannel.RFID.name())) {
                                    payMethod = obj;
                                }
                            }
                            bundle.putSerializable(CashierRFIDPayActivity.EXTRA_PAY_METHOD, payMethod);
                            intent.putExtras(bundle);
                            mContext.startActivity(intent);
                        } else {
                            Toast.makeText(mContext, "获取一卡通状态失败", Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }

    private void pay(final PayRequest payRequest) {
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String, Object> map = new HashMap<>();
        map.put("pay_req", payRequest);
        PaymentService paymentService = ApiFactory.getFactory().create(PaymentService.class);
        paymentService.payOder(NetUtils.getRequestParams(context, map), NetUtils.getSign(NetUtils.getRequestParams(context, map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(context, true, null) {

                    @Override
                    public void onNext(Object o) {
                        PayOderResponse payOderResponse = new Gson().fromJson(o.toString(), PayOderResponse.class);
                        //获取到paymentoder
                        if (payOderResponse.resp.resp_code.equals(AppConstant.SUCCESS)) {
                            FeeCashierWayAdapter.this.payRequest = payRequest;
                            FeeCashierWayAdapter.this.payOderResponse = payOderResponse;
                            if (payRequest.pay_method.channel.equals("AliAppPay")) {
                                //支付宝支付
                                AliPay(payOderResponse.order.pay_data);
                            } else if (payRequest.pay_method.channel.equals("WxAppPay")) {
                                if (isWeixinAvilible(context)) {
                                    DataBean dataBean = new Gson().fromJson(payOderResponse.order.pay_data, DataBean.class);
                                    weixinPay(dataBean);

                                    FeeCashierDeskActivity.payOderResponse = payOderResponse;
                                    FeeCashierDeskActivity.payRequest = payRequest;
                                } else {
                                    Toast.makeText(context, "请先安装微信", Toast.LENGTH_SHORT).show();
                                }

                            }

                            if (payRequest.pay_method.channel.equals("UPTokenPay") && payRequest.pay_method.purpose == null) {
                                Intent intent = new Intent(context, PaymentStatusActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putString(PaymentStatusActivity.AMOUNT, payOderResponse.order.payment_amt);
                                bundle.putString(PaymentStatusActivity.NAME, payOderResponse.order.trade_summary);
                                bundle.putSerializable(PaymentStatusActivity.WAY, payOderResponse.order.pay_method);
                                bundle.putString(PaymentStatusActivity.TIME, payOderResponse.order.order_time);
                                bundle.putString(PaymentStatusActivity.ODER, payOderResponse.order.order_no);
                                intent.putExtras(bundle);
                                context.startActivity(intent);
                                ((Activity) context).finish();
                            }

                            if (payRequest.pay_method.channel.equals("UnionPay")) {
                                PaymentInfo paymentInfo = new PaymentInfo();
                                paymentInfo.pay_method = payRequest.pay_method;
                                paymentInfo.payment_amt = payOderResponse.order.payment_amt;
                                paymentInfo.channel_orderno = payOderResponse.order.order_no;
                                paymentInfo.pay_info = payOderResponse.order.pay_data;
                                paymentInfo.fee_name = payOderResponse.order.trade_summary + "-支付成功";
                                paymentInfo.fee_time = payOderResponse.order.order_time;
                                Intent intent = new Intent(context, MyPayActivity.class);
                                intent.putExtra(MyPayActivity.EXTRA_AMOUNT, payOderResponse.order.payment_amt);
                                if (payFeeRequestData != null) {
                                    intent.putExtra(MyPayActivity.EXTRA_PAY_FOR, PayFor.SchoolFee);
                                }
                                if (payECardRequestData != null) {
                                    intent.putExtra(MyPayActivity.EXTRA_PAY_FOR, PayFor.RechargeECard);
                                }
                                intent.putExtra(MyPayActivity.EXTRA_PAYMENT_INFO, paymentInfo);
                                context.startActivity(intent);
                            }
                        }

                    }
                });
    }

    public static class PayOderResponse extends RealmObject {
        public BaseResponse resp;
        public PaymentOrder order;
    }

    /**
     * 显示popupWindow
     */
    private void showPopwindow(int root, float Damount, final PayMethod payMethod) {
        if (context == null) {
            return;
        }
        // 利用layoutInflater获得View
        @SuppressLint("WrongConstant") LayoutInflater inflater = (LayoutInflater) context.getSystemService("layout_inflater");//Context.LAYOUT_INFLATER_SERVICE
        @SuppressLint("InflateParams") final View view = inflater.inflate(R.layout.popup_window_hint, null);
        final PopupWindow window = new PopupWindow(view,
                WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        // 设置popWindow弹出窗体可点击，这句话必须添加，并且是true
        window.setFocusable(true);
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0x7DC0C0C0);
        window.setBackgroundDrawable(dw);
        // 设置背景颜色变暗
        WindowManager.LayoutParams lp = ((Activity) context).getWindow().getAttributes();
        lp.alpha = 0.5f;
        ((Activity) context).getWindow().setAttributes(lp);
        // 设置popWindow的显示和消失动画
        if (root == 1)
            window.setAnimationStyle(R.style.mypopwindow_anim_style);
        // 在底部显示
        window.showAtLocation(((CashierDeskActivity) context).findViewById(R.id.ll_name),
                Gravity.BOTTOM, 0, 0);

        final RecyclerView listview = (RecyclerView) view.findViewById(R.id.recyclerview);

        final LinearLayout ll_selected_bank = (LinearLayout) view.findViewById(R.id.ll_selected_bank);
//        final ImageView bank_logo = (ImageView) view.findViewById(R.id.iv_bank_logo);
//
        final TextView daizhifu = (TextView) view.findViewById(R.id.daizhifu);
        final TextView GoPay = (TextView) view.findViewById(R.id.btn_go_pay);
        GoPay.setEnabled(false);
        GoPay.setBackgroundColor(Color.rgb(102, 102, 102));
//
//        final TextView tvTradeLimit = (TextView) view.findViewById(R.id.tv_bank_trade);

        final TextView TvtradeLimitShowHint = (TextView) view.findViewById(R.id.tv_trade_limit);
//        final ImageView IconHelp = (ImageView) view.findViewById(R.id.icon_help);
        final ImageView Back = (ImageView) view.findViewById(R.id.back);
//        final Button GoPay = (Button) view.findViewById(R.id.btn_go_pay);

        window.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                // 设置背景颜色不透明
                WindowManager.LayoutParams lp = ((Activity) context).getWindow().getAttributes();
                lp.alpha = 1f;
                ((Activity) context).getWindow().setAttributes(lp);
                if (listener2 != null)
                    listener2.dissMiss();
            }
        });

        daizhifu.setText(new DecimalFormat("0.00").format(Damount) + "元");

        GoPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PayRequest payRequest = new PayRequest();
                payRequest.amount = amount + "";
                if (payFeeRequestData != null) {
                    payRequest.pay_for = PayFor.SchoolFee;
                    payRequest.request_data = payFeeRequestData;
                }
                if (payECardRequestData != null) {
                    payRequest.pay_for = PayFor.RechargeECard;
                    payRequest.request_data = payECardRequestData;
                }
                payRequest.pay_method = payMethod;
                pay(payRequest);
            }
        });

        ArrayList<BankLimit> datas = new ArrayList<BankLimit>();
        datas.clear();
        datas.addAll(BankInfoCache.getInstance().getBankInfo());
        BankAdapter adapter = new BankAdapter(context, R.layout.item_bank_trade_limit, datas);

        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                window.dismiss();
            }
        });

        listview.setLayoutManager(new LinearLayoutManager(context));

        listview.setAdapter(adapter);

//        BankLimit BankLimit = new BankLimit();
//        BankLimit.name = "其他银行";
//        BankLimit.local_logo = R.drawable.a_icon;
//        BankLimit.local = true;
//        adapter.addOtherBankItem(BankLimit);
        adapter.setOnClickGetBankInfoListener(new BankAdapter.IGetBankInfo() {

            @Override
            public void getBankInfoByPosition(BankLimit BankLimit) {
                GoPay.setEnabled(true);
                GoPay.setBackgroundColor(Color.rgb(32, 138, 240));
                if (!BankLimit.local) {
//                    ImageLoader.getInstance().displayImage(BankLimit.logo, bank_logo, getDisplayImageOptions(R.drawable.white_bg));
//                    tvBankName.setText(BankLimit.name);
//                    tvTradeLimit.setText("单笔" + BankLimit.limit_per_time + "元" + "," + "单日" + BankLimit.limit_per_day + "元");
                    if (amount > Float.parseFloat(BankLimit.limit_per_time)) {
                        LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) ll_selected_bank.getLayoutParams();
                        linearParams.height = DensityUtil.dip2px(context, 55);
                        ll_selected_bank.setLayoutParams(linearParams);
                        ll_selected_bank.setVisibility(View.VISIBLE);
                        TranslateAnimation ab = new TranslateAnimation(0, 0, 100, 0);
                        ab.setDuration(300);
                        ll_selected_bank.startAnimation(ab);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                AnimatorUtil.animHeightToView((Activity) context, ll_selected_bank, true, 300);
                            }
                        }, 300);

                        TvtradeLimitShowHint.setText(BankInfoCache.getInstance().getTradeLimit().out_of_limit_notice);
//                        GoPay.setText("仍要支付");
//                        GoPay.setBackgroundResource(R.drawable.white_bg);
//                        GoPay.setTextColor(context.getResources().getColor(R.color.white));
//                        GoPay.setEnabled(true);
                    } else {
                        LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) ll_selected_bank.getLayoutParams();
                        linearParams.height = DensityUtil.dip2px(context, 55);
                        ll_selected_bank.setLayoutParams(linearParams);
                        ll_selected_bank.setVisibility(View.GONE);
//                        TranslateAnimation ab = new TranslateAnimation(0, 0, 0, 100);
//                        ab.setDuration(300);
//                        ll_selected_bank.startAnimation(ab);
//                        GoPay.setText("去支付");
//                        GoPay.setTextColor(context.getResources().getColor(R.color.white));
//                        GoPay.setBackgroundResource(R.color.axf_btn_uncheck_blue);
//                        GoPay.setEnabled(true);
                    }
                } else {
                    ll_selected_bank.setVisibility(View.VISIBLE);
//                    bank_logo.setImageResource(BankLimit.local_logo);
//                    tvBankName.setText(BankLimit.name);
//                    tvTradeLimit.setText("单笔限额可能相对较低");
                    LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) ll_selected_bank.getLayoutParams();
                    linearParams.height = DensityUtil.dip2px(context, 55);
                    ll_selected_bank.setLayoutParams(linearParams);
                    TranslateAnimation ab = new TranslateAnimation(0, 0, 100, 0);
                    ab.setDuration(300);
                    ll_selected_bank.startAnimation(ab);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            AnimatorUtil.animHeightToView((Activity) context, ll_selected_bank, true, 300);
                        }
                    }, 300);
                    TvtradeLimitShowHint.setText(BankInfoCache.getInstance().getTradeLimit().other_bank_notice);
//                    GoPay.setText("仍要支付");
//                    GoPay.setTextColor(context.getResources().getColor(R.color.white));
//                    GoPay.setBackgroundResource(R.color.axf_btn_uncheck_blue);
//                    GoPay.setEnabled(true);
                }
            }
        });
    }


    public static class BankAdapter extends RecyclerAdapter<BankLimit> {
        private IGetBankInfo listener;
        private List<BankLimit> datas;
        private int defaultSelection = -1;

        public BankAdapter(Context context, int layoutId, List<BankLimit> datas) {
            super(context, datas, layoutId);
            this.datas = datas;
        }

        @Override
        protected void convert(final RecyclerAdapterHelper helper, final BankLimit bankInfo) {
            helper.setText(tv_bank_name, bankInfo.name);

            if (helper.getAdapterPosition() == defaultSelection) {// 选中时设置单纯颜色
                helper.getView(ll_item).setBackgroundColor(context.getResources().getColor(R.color.axf_bg_blue));
                ((TextView) helper.getView(tv_bank_trade)).setTextColor(context.getResources().getColor(R.color.axf_common_blue));
                ((TextView) helper.getView(tv_bank_name)).setTextColor(context.getResources().getColor(R.color.axf_common_blue));
            } else {// 未选中时设置selector
                helper.getView(ll_item).setBackgroundColor(context.getResources().getColor(R.color.white));
                ((TextView) helper.getView(tv_bank_trade)).setTextColor(context.getResources().getColor(R.color.axf_text_content_gray));
                ((TextView) helper.getView(tv_bank_name)).setTextColor(context.getResources().getColor(R.color.axf_text_content_gray));
            }

            if (!bankInfo.local) {
                helper.setText(tv_bank_trade, "单笔" + bankInfo.limit_per_time + "元" + "," + "单日" + bankInfo.limit_per_day + "元");
            } else {
                helper.setText(tv_bank_trade, "单笔限额可能相对较低");
            }
            if (!bankInfo.local) {
                helper.setImageUrl(R.id.iv_bank_logo, bankInfo.logo_url);
            } else {
                helper.setImageResource(R.id.iv_bank_logo, bankInfo.local_logo);
            }
            helper.getItemView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null)
                        listener.getBankInfoByPosition(bankInfo);
                    setSelectPosition(helper.getAdapterPosition());
                }
            });
        }

        interface IGetBankInfo {
            void getBankInfoByPosition(BankLimit bankInfo);
        }

        public void setOnClickGetBankInfoListener(IGetBankInfo listener) {
            this.listener = listener;
        }

        /*
        添加一个其他银行的表示
        */
        public void addOtherBankItem(BankLimit data) {
            Iterator<BankLimit> sListIterator = datas.iterator();
            while (sListIterator.hasNext()) {
                BankLimit e = sListIterator.next();
                if (e.local) {
                    sListIterator.remove();
                }
            }
            datas.add(data);
            notifyDataSetChanged();
        }

        /**
         * @param position 设置高亮状态的item
         */
        public void setSelectPosition(int position) {
            if (!(position < 0 || position > data.size())) {
                defaultSelection = position;
                notifyDataSetChanged();
            }
        }

    }


    public interface onDissMiss {
        void dissMiss();
    }

    public void setOnDissMissListener(onDissMiss listener2) {
        this.listener2 = listener2;
    }

    public DisplayImageOptions getDisplayImageOptions(int defaultPicture) {
        return new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .resetViewBeforeLoading(true)
                .showImageOnLoading(defaultPicture)
                .showImageOnFail(defaultPicture)
                .showImageForEmptyUri(defaultPicture)
                .build();
    }

    private static long lastClickTime;

    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < 500) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    /**
     * 判断 用户是否安装微信客户端
     */
    public static boolean isWeixinAvilible(Context context) {
        final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mm")) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * @param info
     */
    private void AliPay(String info) {
        Log.d("CashierWayAdapter", info);
        final String orderInfo = info;   // 订单信息
        Runnable payRunnable = new Runnable() {
            @Override
            public void run() {
                PayTask alipay = new PayTask((Activity) context);
                Map<String, String> result = alipay.payV2(orderInfo, true);
                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };
        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    public class PayResult {
        private String resultStatus;
        private String result;
        private String memo;

        public PayResult(Map<String, String> rawResult) {
            if (rawResult == null) {
                return;
            }

            for (String key : rawResult.keySet()) {
                if (TextUtils.equals(key, "resultStatus")) {
                    resultStatus = rawResult.get(key);
                } else if (TextUtils.equals(key, "result")) {
                    result = rawResult.get(key);
                } else if (TextUtils.equals(key, "memo")) {
                    memo = rawResult.get(key);
                }
            }
        }

        @Override
        public String toString() {
            return "resultStatus={" + resultStatus + "};memo={" + memo
                    + "};result={" + result + "}";
        }

        /**
         * @return the resultStatus
         */
        public String getResultStatus() {
            return resultStatus;
        }

        /**
         * @return the memo
         */
        public String getMemo() {
            return memo;
        }

        /**
         * @return the result
         */
        public String getResult() {
            return result;
        }
    }

    public class DataBean {
        /**
         * appid : wx2ad3975aae8c94d3
         * noncestr : AwjChrYkdIMHLdKiZhAV1Ow25rPbFc0E
         * package : Sign=WXPay
         * partnerid : 1490896912
         * prepayid : wx201710261022379b5b205ecc0998958156
         * sign : 0E13E87607783C011F3636BFBCEBAEB9
         * timestamp : 1508984555
         */

        private String appid;
        private String noncestr;
        @SerializedName("package")
        private String packageX;
        private String partnerid;
        private String prepayid;
        private String sign;
        private String timestamp;

        public String getAppid() {
            return appid;
        }

        public void setAppid(String appid) {
            this.appid = appid;
        }

        public String getNoncestr() {
            return noncestr;
        }

        public void setNoncestr(String noncestr) {
            this.noncestr = noncestr;
        }

        public String getPackageX() {
            return packageX;
        }

        public void setPackageX(String packageX) {
            this.packageX = packageX;
        }

        public String getPartnerid() {
            return partnerid;
        }

        public void setPartnerid(String partnerid) {
            this.partnerid = partnerid;
        }

        public String getPrepayid() {
            return prepayid;
        }

        public void setPrepayid(String prepayid) {
            this.prepayid = prepayid;
        }

        public String getSign() {
            return sign;
        }

        public void setSign(String sign) {
            this.sign = sign;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }
    }


    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        PaymentInfo mPaymentInfo = new PaymentInfo();
                        mPaymentInfo.pay_method = payRequest.pay_method;
                        mPaymentInfo.payment_amt = payOderResponse.order.payment_amt;
                        mPaymentInfo.channel_orderno = payOderResponse.order.order_no;
                        mPaymentInfo.pay_info = payOderResponse.order.pay_data;
                        mPaymentInfo.fee_name = payOderResponse.order.trade_summary + "-支付成功";
                        mPaymentInfo.fee_time = payOderResponse.order.order_time;
                        Intent intent = new Intent(context, PaymentStatusActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString(PaymentStatusActivity.AMOUNT, mPaymentInfo.payment_amt);
                        bundle.putString(PaymentStatusActivity.NAME, mPaymentInfo.fee_name);
                        bundle.putSerializable(PaymentStatusActivity.WAY, mPaymentInfo.pay_method);
                        bundle.putString(PaymentStatusActivity.TIME, mPaymentInfo.fee_time);
                        bundle.putString(PaymentStatusActivity.ODER, mPaymentInfo.channel_orderno);
                        intent.putExtras(bundle);
                        ((Activity) context).startActivity(intent);
                        ((Activity) context).finish();
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        Toast.makeText(context, "支付取消", Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                default:
                    break;
            }
        }
    };

    private void weixinPay(DataBean dataBean) {
        Log.d("CashierWayAdapter", new Gson().toJson(dataBean));
        App.WXAPPID = dataBean.getAppid();
        IWXAPI api = WXAPIFactory.createWXAPI(context, dataBean.getAppid());
        PayReq req = new PayReq();
        req.appId = dataBean.getAppid();
        req.partnerId = dataBean.getPartnerid();
        req.prepayId = dataBean.getPrepayid();
        req.nonceStr = dataBean.getNoncestr();
        req.timeStamp = dataBean.getTimestamp();
        req.packageValue = dataBean.getPackageX();
        req.sign = dataBean.getSign();
        api.registerApp(dataBean.getAppid());
        api.sendReq(req);
        App.isNeedCheck = true;
    }

    /**
     * 通知金融服务端--支付
     *
     * @param amount
     */
    private void GuiyangPay(final String name, final String idCard, final String amount) {

        PayRequest payRequest = new PayRequest();
        payRequest.amount = amount + "";

        if (payFeeRequestData != null) {
            payRequest.pay_for = PayFor.SchoolFee;
            payRequest.request_data = payFeeRequestData;

        }
        if (payECardRequestData != null) {
            payRequest.pay_for = PayFor.RechargeECard;
            payRequest.request_data = payECardRequestData;
        }

        HashMap hashMap = new HashMap();
        hashMap.put("id_card_no", idCard);
        hashMap.put("name", name);
        payRequest.channel_request_data = hashMap;
        PayMethod payMethod = new PayMethod();
        payMethod.channel = "GuiYangCreditLoanPay";
        payRequest.pay_method = payMethod;

        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String, Object> map = new HashMap<>();
        map.put("pay_req", payRequest);
        PaymentService paymentService = ApiFactory.getFactory().create(PaymentService.class);
        paymentService.payOder(NetUtils.getRequestParams(context, map), NetUtils.getSign(NetUtils.getRequestParams(context, map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(context, true, null) {
                    @Override
                    public void onNext(Object o) {
                        final GyPayOderResponses payOderResponse = new Gson().fromJson(o.toString(), GyPayOderResponses.class);
                        FeeCashierWayAdapter.this.gyPayOderResponses = payOderResponse;
                        if (payOderResponse.resp.resp_code.equals(AppConstant.SUCCESS)) {
                            final Tn tn = new Gson().fromJson(payOderResponse.order.pay_data, Tn.class);
                            Intent it = new Intent(CashierDeskActivity.GYSDK_PACKAGE_ACTIVITY_NAME);
                            Bundle bundle = new Bundle();
                            bundle.putString("type", "pay");
                            bundle.putString("idCard", idCard);
                            bundle.putString("tn", tn.tn);
                            it.putExtras(bundle);
                            try {
                                ((FeeCashierDeskActivity) context).startActivityForResult(it, CashierDeskActivity.REQUEST_GR_PAY);
                            } catch (Exception e) {
                                PluginInfo pluginInfo = new PluginInfo();
                                pluginInfo.package_name = CashierDeskActivity.GYSDK_PACKAGE_NAME;
                                pluginInfo.version = "1.0.0";
                                ArrayList<PluginInfo> arrayList = new ArrayList<>();
                                arrayList.add(pluginInfo);
                                checkUpdate(arrayList);
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    private void checkUpdate(ArrayList<PluginInfo> pluginInfos) {
        RetrofitFactory.setBaseUrl(AppConstant.URL);

        Map<String, Object> map = new HashMap<>();
        map.put("pluginInfos", pluginInfos);
        MeService meService = ApiFactory.getFactory().create(MeService.class);
        meService.getCheckUpdate(NetUtils.getRequestParams(context, map), NetUtils.getSign(NetUtils.getRequestParams(context, map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(context, true, null) {
                    @Override
                    public void onNext(Object o) {
                        MeCheckUpdatePresenter.GetCheckUpdateResponse response =
                                new Gson().fromJson(o.toString(), MeCheckUpdatePresenter.GetCheckUpdateResponse.class);
                        checkSuccess(response.plugin_updates);
                    }
                });

    }

    private void checkSuccess(ArrayList<Update> plugin_updates) {
        String url = null;
        for (Update item : plugin_updates) {
            if (!item.update_type.equals("None") &&
                    item.name.equals(CashierDeskActivity.GYSDK_PACKAGE_NAME) &&
                    !Util.isEmpty(item.update_url)) {
                url = item.update_url;

                Message msg = new Message();
                msg.what = 1;
                Bundle bundle = new Bundle();
                bundle.putString("url",url);
                msg.setData(bundle);
                handlerGY.sendMessage(msg);
            }
        }

        if (Util.isEmpty(url)) {
            Toast.makeText(context, "下载失败", Toast.LENGTH_LONG).show();
        } else {
            initDialog();

            if (App.mAxLoginSp.getGysdkDone() || DownloadAPKService.isRunning()) {
                // 下载完成；正在下载.
                downloadGysdkDialog.gotoService();
                progressDialog.setStart();
            } else {
                downloadGysdkDialog.show();
            }
        }
    }

    @SuppressLint("HandlerLeak")
    private
    Handler handlerGY = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 1){
                String url = msg.getData().getString("url");
                App.mAxLoginSp.setGysdkUrl(url);
            }
        }
    };

    public void gysdkReturn(String tn) {
        guiyangPayResult(tn);
        //成功
        Intent intent = new Intent(context, PaymentStatusActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(PaymentStatusActivity.AMOUNT, FeeCashierWayAdapter.this.gyPayOderResponses.order.payment_amt);
        bundle.putString(PaymentStatusActivity.NAME, FeeCashierWayAdapter.this.gyPayOderResponses.order.trade_summary + "-支付成功");

        PayMethod payMethod = new PayMethod();
        payMethod.channel = "GuiYangCreditLoanPay";

        bundle.putSerializable(PaymentStatusActivity.WAY, payMethod);
        bundle.putString(PaymentStatusActivity.TIME, FeeCashierWayAdapter.this.gyPayOderResponses.order.order_time);
        bundle.putString(PaymentStatusActivity.ODER, FeeCashierWayAdapter.this.gyPayOderResponses.order.order_no);
        intent.putExtras(bundle);
        context.startActivity(intent);
        ((Activity) context).finish();

    }

    private void guiyangPayResult(String serial_no) {
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String, Object> map = new HashMap<>();
        map.put("serial_no", serial_no);
        LoanService loanService = ApiFactory.getFactory().create(LoanService.class);
        loanService.disburse_success_notify(NetUtils.getRequestParams(context, map), NetUtils.getSign(NetUtils.getRequestParams(context, map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(context, false, null) {

                    @Override
                    public void onNext(Object o) {

                    }
                });
    }

    /**
     * 开户
     */
    public static class OpenAccountResponse extends RealmObject {
        public BaseResponse resp;
        public CreditInfo credit_info;
    }

    public class CreditInfo extends RealmObject {
        public String id_card_no;
        public String name;
        public String serial_no;
        public String loan_way_type;
    }

    public class Tn {
        public String tn;
    }

    public static class GyPayOderResponses extends RealmObject {
        public BaseResponse resp;
        public GuiyangPaymentOrder order;
    }

    public class GuiyangPaymentOrder implements Serializable {
        public PayMethod pay_method;  // required
        public String pay_data;  // required
        public String order_no;  // optional
        public String payment_amt;  // optional
        public String trade_summary;  // required
        public String success_notice;  // required
        public String order_time;  // required
    }

    public class UPQRQuickPayChannelRequest {
        public String pay_password;        // 支付密码, aes128加密
        public String bank_card_code;
    }

    public void remove(int arg0) {// 删除指定位置的item
        data.remove(arg0);
        Handler handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                notifyItemChanged(getItemCount() - 1);
            }
        };
        handler.post(r);
    }

    public void insert(PayMethod item, int arg0) {// 在指定位置插入item
        data.add(arg0, item);
        Handler handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                notifyItemChanged(getItemCount() - 1);
            }
        };
        handler.post(r);
    }


    /**
     * 外派一个payrequest去支付
     */
    interface GetPayRequest {
        void getPayRequest(PayMethod payMethod);
    }

    public void setPayRequestListener(GetPayRequest getPayRequest) {
        this.getPayRequest = getPayRequest;
    }

    public void clearSelected() {
        for (int i = 0; i < data.size(); i++) {
            data.get(i).isSelected = false;
            notifyDataSetChanged();
        }
    }

    private void checkApprovalInfo(final String idCard,final String name,final float amount) {
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String, Object> map = new HashMap<>();
        map.put("loan_way_type", "GuiYangCreditLoanPay");
        LoanService loanService = ApiFactory.getFactory().create(LoanService.class);
        loanService.check_pre_approval(NetUtils.getRequestParams(context, map), NetUtils.getSign(NetUtils.getRequestParams(context, map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(context, true, null) {
                    @Override
                    public void onNext(Object o) {
                        final IntroduceActivity.ApprovalResponse approvalResponse = new Gson().fromJson(o.toString(), IntroduceActivity.ApprovalResponse.class);
                        if (approvalResponse.resp.resp_code.equals(AppConstant.SUCCESS)) {
                            //判断预授信信息
                            if (approvalResponse.status.equals("Success")) {
                                Bundle bundle = new Bundle();
                                bundle.putString("name", name);
                                bundle.putString("idCard", idCard);
                                bundle.putFloat("amount", amount);
                                Intent intent = new Intent(context, AutonymSuccActivity.class);
                                intent.putExtras(bundle);
                                context.startActivity(intent);
                            } else if (approvalResponse.status.equals("Processing")) {
                                (context).startActivity(new Intent((context), ProcessingActivity.class));
                            } else if (approvalResponse.status.equals("Error")) {
                                (context).startActivity(new Intent((context), ErrorActivity.class));
                            } else if (approvalResponse.status.equals("AccountNotExist")) {
                                //进入预售信息补全界面
                                Bundle bundle = new Bundle();
                                bundle.putString("name", name);
                                bundle.putString("idCard", idCard);
                                bundle.putFloat("amount", amount);
                                Intent intent = new Intent(context, AutonymSuccActivity.class);
                                intent.putExtras(bundle);
                                context.startActivity(intent);
                            } else if (approvalResponse.status.equals("RealNameAuthError")) {
                                //实名认证失败
                                (context).startActivity(new Intent((context), ErrorActivity.class));
                            } else {

                            }
                        }
                    }
                });
    }

    public static class ApprovalResponse extends RealmObject {
        public BaseResponse resp;
        public String status;
    }
}
