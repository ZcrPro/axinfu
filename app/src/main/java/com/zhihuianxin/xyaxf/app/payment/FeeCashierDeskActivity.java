package com.zhihuianxin.xyaxf.app.payment;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.zhihuianxin.xyaxf.app.AppConstant;
import modellib.service.EcardService;
import modellib.service.FeeService;
import modellib.service.LoanService;
import modellib.service.MeService;
import modellib.service.PaymentService;
import modellib.thrift.app.PluginInfo;
import modellib.thrift.app.Update;
import modellib.thrift.bankcard.UPCardType;
import modellib.thrift.base.BaseResponse;
import modellib.thrift.base.PayChannel;
import modellib.thrift.base.PayMethod;
import modellib.thrift.fee.Fee;
import modellib.thrift.fee.SchoolRoll;
import modellib.thrift.fee.SubFee;
import modellib.thrift.fee.SubFeeItem;
import modellib.thrift.payment.BankLimit;
import modellib.thrift.payment.PaymentOrder;
import modellib.thrift.payment.QuickPayMethod;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.xyaxf.axpay.Util;
import com.xyaxf.axpay.modle.PayECardRequestData;
import com.xyaxf.axpay.modle.PayFeeExtRequest;
import com.xyaxf.axpay.modle.PayFeeRequestData;
import com.xyaxf.axpay.modle.PayFor;
import com.xyaxf.axpay.modle.PayRequest;
import com.xyaxf.axpay.modle.PaymentInfo;
import com.zhihuianxin.secure.Secure;
import com.zhihuianxin.xyaxf.App;
import com.zhihuianxin.xyaxf.BaseRealmActionBarActivity;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.ApiFactory;
import com.zhihuianxin.xyaxf.app.BaseSubscriber;
import com.zhihuianxin.xyaxf.app.RetrofitFactory;
import com.zhihuianxin.xyaxf.app.ecard.Ecardpresenter;
import com.zhihuianxin.xyaxf.app.me.presenter.MeCheckUpdatePresenter;
import com.zhihuianxin.xyaxf.app.me.view.activity.MeHelpCenterActivity;
import com.zhihuianxin.xyaxf.app.ocp.OcpPayFixedDeskActivity;
import com.zhihuianxin.xyaxf.app.ocp.view.PasswordWindow;
import com.zhihuianxin.xyaxf.app.pay.MyPayActivity;
import com.zhihuianxin.xyaxf.app.pay.guiyang.AutonymSuccActivity;
import com.zhihuianxin.xyaxf.app.pay.guiyang.IntroduceActivity;
import com.zhihuianxin.xyaxf.app.pay.guiyang.status.ErrorActivity;
import com.zhihuianxin.xyaxf.app.pay.guiyang.status.ProcessingActivity;
import com.zhihuianxin.xyaxf.app.payment.quk.QukMobileActivity;
import com.zhihuianxin.xyaxf.app.service.DownloadAPKService;
import com.zhihuianxin.xyaxf.app.unionqr_pay.view.activity.UnionHtmlActivity;
import com.zhihuianxin.xyaxf.app.unionqr_pay.view.activity.UnionSweptEmptyCardActivity;
import com.zhihuianxin.xyaxf.app.utils.AnimatorUtil;
import com.zhihuianxin.xyaxf.app.utils.DensityUtil;
import com.zhihuianxin.xyaxf.app.utils.FullyLinearLayoutManager;
import com.zhihuianxin.xyaxf.app.utils.NetUtils;
import com.zhihuianxin.xyaxf.app.utils.RxBus;
import com.zhihuianxin.xyaxf.app.view.DownloadGysdkDialog;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.realm.RealmObject;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static com.zhihuianxin.xyaxf.app.me.view.activity.AddBankCardActivity.bytesToHexString;

/**
 * Created by zcrpro on 2016/11/12.
 */
public class FeeCashierDeskActivity extends BaseRealmActionBarActivity {
    public static final int REQUEST_GR_PAY = 1000;
    public static final String EXTRA_GYAPK_DIR = "axin_gyapk";
    public static final String EXTRA_GYAPK_URL = "http://192.168.0.19:8081/安装包/android/安心信用付插件APK/axinfu_creditpay_sdk_1.0.1.apk";
    public static final String GYSDK_PACKAGE_ACTIVITY_NAME = "com.zhihuianxin.creditpay.MainActivity";
    public static final String GYSDK_PACKAGE_NAME = "com.zhihuianxin.creditpay";
    //总金额用于显示
    public static final String PAY_AMOUNT = "amount";
    //费用的名字
    public static final String FEE_TITLE = "FEE_TITLE";
    //接收支付渠道列表
    public static final String FEE_WAY = "FEE_WAY";
    //勾选了的费用ID
    public static final String FEE_ID = "FEE_ID";
    public static final String buss_type = "buss_type";

    public static final String ECARD = "ecard";
    public static final String FEE = "fee";
    //判断是来自于ecard还是fee
    public static final String WHICH = "which";
    //接受normal下的选择项的信息
    public static final String FEE_NORMAL = "FEE_NORMAL";
    //支付需要信息
    public static final String PAY_FEE = "PAY_FEE";
    //整包
    public static final String FEE_PACK = "FEE_PACK";
    //单选
    public static final String FEE_SINGLE = "FEE_SINGLE";
    //父类
    public static final String FEE_FU = "FEE_FU";

    /**
     * 是否全部抵扣
     */
    public static final String HAS_DECU = "HAS_DECU";
    @InjectView(R.id.tv_pay_way)
    TextView tvPayWay;
    @InjectView(R.id.btn_ok)
    Button btnOk;
    @InjectView(R.id.rc_recommend)
    RecyclerView rcRecommend;
    @InjectView(R.id.rc_common)
    RecyclerView rcCommon;
    @InjectView(R.id.ll_pay_way_line)
    View llPayWayLine;
    @InjectView(R.id.ll)
    LinearLayout ll;
    @InjectView(R.id.grayBg)
    View grayBg;
    @InjectView(R.id.change_pay_way)
    Button changePayWay;
    @InjectView(R.id.goon)
    TextView goon;
    @InjectView(R.id.neePasswordView)
    RelativeLayout neePasswordView;
    @InjectView(R.id.bank_name)
    TextView bankName;
    @InjectView(R.id.limt_content)
    TextView limtContent;

    private AlertDialog dialog;

    private String from;
    private String amout;
    private String fee_title;
    private static String FeeId;
    private String busss_type;
    private List<PayMethod> channel_codes;
    private List<HashMap<String, String>> fee_normals;
    private List<HashMap<String, String>> fee_packs;
    private List<HashMap<String, String>> fee_singles;
    private List<HashMap<String, String>> fee_fu;
    private List<HashMap<String, String>> payfees;

    private List<PayFeeExtRequest> payFeeExtRequests;
    private PayFeeExtRequest payFeeExtRequest;
    private FeeCashierWayAdapter feeCashierWayAdapter;
    private FeeCashierWayAdapter feeCashierWayAdapter2;

    private Subscription rxSbscription;

    private float decu;

    private List<PayMethod> allPayMethods; //所有的支付方式
    private List<PayMethod> recommendPayMethods; //推荐支付方式
    private List<PayMethod> commonPayMethods; //非推荐的支付方式
    private List<PayMethod> bankPayMethods; //所有的银行卡支付方式

    private PayMethod payMethod;  //选中的paymethod


    private FeeCashierWayAdapter.GyPayOderResponses gyPayOderResponses;
    private String name;
    private String idCard;
    private DownloadGysdkDialog downloadGysdkDialog;

    private boolean isLoading = true;

    private DisplayMetrics metrics;

    /**
     * 贵阳银行相关信息
     */
    public boolean GuiyangIsOpen = false;
    public boolean GuiyangIsAble = false;
    public float GuiyangMin;
    public String realname_auth_status;

    private PayFeeRequestData payFeeRequestData;
    private PayECardRequestData payECardRequestData;

    private static final int SDK_PAY_FLAG = 1;


    /**
     * 微信支付用
     *
     * @return
     */

    public static PayRequest payRequest;
    public static FeeCashierWayAdapter.PayOderResponse payOderResponse;

    @Override
    protected int getContentViewId() {
        return R.layout.fee_cashier_desk_activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);

        btnOk.setEnabled(false);


        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        tvPayWay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FeeCashierDeskActivity.this, MeHelpCenterActivity.class));
            }
        });

        payFeeExtRequests = new ArrayList<>();
        allPayMethods = new ArrayList<>();
        recommendPayMethods = new ArrayList<>();
        commonPayMethods = new ArrayList<>();
        bankPayMethods = new ArrayList<>();

        FullyLinearLayoutManager mLayoutManager = new FullyLinearLayoutManager(this);
        rcRecommend.setLayoutManager(mLayoutManager);

        FullyLinearLayoutManager mLayoutManager2 = new FullyLinearLayoutManager(this);
        rcCommon.setLayoutManager(mLayoutManager2);

        //获取是为哪个模块所支付
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            from = bundle.getString(WHICH);
            amout = bundle.getString(PAY_AMOUNT);
            fee_title = bundle.getString(FEE_TITLE);
            FeeId = bundle.getString(FEE_ID);
            decu = bundle.getFloat(HAS_DECU);
            busss_type = bundle.getString(buss_type);
            if (bundle.getSerializable(FEE_NORMAL) != null) {
                fee_normals = (List<HashMap<String, String>>) bundle.getSerializable(FEE_NORMAL);
            }
            if (bundle.getSerializable(FEE_PACK) != null) {
                fee_packs = (List<HashMap<String, String>>) bundle.getSerializable(FEE_PACK);
            }
            if (bundle.getSerializable(FEE_SINGLE) != null) {
                fee_singles = (List<HashMap<String, String>>) bundle.getSerializable(FEE_SINGLE);
            }
            if (bundle.getSerializable(FEE_FU) != null) {
                fee_fu = (List<HashMap<String, String>>) bundle.getSerializable(FEE_FU);
            }

            if (bundle.getSerializable(PAY_FEE) != null) {
                payfees = (List<HashMap<String, String>>) bundle.getSerializable(PAY_FEE);
            }

            if (from != null) {
                if (from.equals(FEE)) {
                    from = FEE;
                    App.formCasher = FEE;
                }
                if (from.equals(ECARD)) {
                    from = ECARD;
                    App.formCasher = ECARD;
                }
            }

            channel_codes = (List<PayMethod>) bundle.getSerializable(FEE_WAY);

            btnOk.setText("确认支付：" + amout + "元");
        }

        rxSbscription = RxBus.getDefault().toObservable().subscribe(new Action1<Object>() {
            @Override
            public void call(Object event) {
                if (event.equals("succeed")) {
                    RxBus.getDefault().send("fee_succeed");
                    finish();
                } else if (event.equals("onPayCancelled")) {
                    Util.myShowToast(FeeCashierDeskActivity.this, "取消支付", 300);
                } else if (event.equals("wechat_suc")) {
                    PaymentInfo mPaymentInfo = new PaymentInfo();
                    mPaymentInfo.pay_method = payRequest.pay_method;
                    mPaymentInfo.payment_amt = payOderResponse.order.payment_amt;
                    mPaymentInfo.channel_orderno = payOderResponse.order.order_no;
                    mPaymentInfo.pay_info = payOderResponse.order.pay_data;
                    mPaymentInfo.fee_name = payOderResponse.order.trade_summary + "-支付成功";
                    mPaymentInfo.fee_time = payOderResponse.order.order_time;
                    Intent intent = new Intent(FeeCashierDeskActivity.this, PaymentStatusActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(PaymentStatusActivity.AMOUNT, mPaymentInfo.payment_amt);
                    bundle.putString(PaymentStatusActivity.NAME, mPaymentInfo.fee_name);
                    bundle.putSerializable(PaymentStatusActivity.WAY, mPaymentInfo.pay_method);
                    bundle.putString(PaymentStatusActivity.TIME, mPaymentInfo.fee_time);
                    bundle.putString(PaymentStatusActivity.ODER, mPaymentInfo.channel_orderno);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish();
                }

                if (event.equals("fixed_activity_add_bank_def")) {
                    if (App.formCasher.equals(ECARD)) {
                        bankPayMethods.clear();
                        payMethod=null;
                        loadEcardData();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                btnOk.setEnabled(false);
                            }
                        });
                    } else if ((App.formCasher.equals(FEE))) {
                        //获取缴费详情
                        bankPayMethods.clear();
                        payMethod=null;
                        loadFee();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                btnOk.setEnabled(false);
                            }
                        });
                    }
                }

                if (event.equals("no_add_card_and_pay")) {
                    //去支付
                    if (App.payRequest != null) {
                        UPQRQuickPayChannelRequest upqrQuickPayChannelRequest = new UPQRQuickPayChannelRequest();
                        upqrQuickPayChannelRequest.bank_card_code = App.payRequest.pay_method.card.getId();
                        try {
                            upqrQuickPayChannelRequest.pay_password = bytesToHexString(Secure.encodeMessageField((App.mAxLoginSp.getUnionPayPwd()).getBytes("UTF-8")));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        App.payRequest.channel_request_data = upqrQuickPayChannelRequest;
                        auto_pay(App.payRequest);
                    }
                }
            }
        });

        if (App.formCasher.equals(ECARD)) {
            loadEcardData();
        } else if ((App.formCasher.equals(FEE))) {
            //获取缴费详情
            loadFee();
        }
        getAccountPayMethodInfo(Float.parseFloat(amout));
    }

    public void loadEcardData() {
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String, Object> map = new HashMap<>();
        EcardService ecardService = ApiFactory.getFactory().create(EcardService.class);
        ecardService.getEcard(NetUtils.getRequestParams(FeeCashierDeskActivity.this, map), NetUtils.getSign(NetUtils.getRequestParams(FeeCashierDeskActivity.this, map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(FeeCashierDeskActivity.this, true, null) {
                    @Override
                    public void onNext(Object o) {
                        Ecardpresenter.EcardResponse ecardResponse = new Gson().fromJson(o.toString(), Ecardpresenter.EcardResponse.class);
                        payECardRequestData = new PayECardRequestData();
                        payECardRequestData.amount = amout;
                        allPayMethods.addAll(ecardResponse.ecard.pay_methods);
                        recommendPayMethods.addAll(ecardResponse.ecard.pay_methods);
                        commonPayMethods.addAll(ecardResponse.ecard.pay_methods);

                        //加载推荐的支付方式列表
                        Iterator iter = recommendPayMethods.iterator();
                        while (iter.hasNext()) {
                            if (!((PayMethod) iter.next()).is_recommended) {
                                iter.remove();
                            }
                        }

                        //加载非推荐的支付方式列表
                        Iterator iter2 = commonPayMethods.iterator();
                        while (iter2.hasNext()) {
                            if (((PayMethod) iter2.next()).is_recommended) {
                                iter2.remove();
                            }
                        }

                        boolean recommendHasCard = false;
                        boolean commonHasCard = false;

                        for (int i = 0; i < recommendPayMethods.size(); i++) {
                            if (recommendPayMethods.get(i).channel.equals("UPTokenPay") && recommendPayMethods.get(i).enabled && recommendPayMethods.get(i).purpose == null) {
                                //有一张可用的卡
                                recommendHasCard = true;
                            }
                        }

                        for (int i = 0; i < commonPayMethods.size(); i++) {
                            if (commonPayMethods.get(i).channel.equals("UPTokenPay") && commonPayMethods.get(i).enabled && commonPayMethods.get(i).purpose == null) {
                                //有一张可用的卡
                                commonHasCard = true;
                            }
                        }

                        bankPayMethods.clear();
                        for (int i = 0; i < allPayMethods.size(); i++) {
                            if (allPayMethods.get(i).channel.equals("UPTokenPay")) {
                                bankPayMethods.add(allPayMethods.get(i));
                            }
                        }

                        for (int i = recommendPayMethods.size() - 1; i >= 0; i--) {
                            PayMethod item = recommendPayMethods.get(i);
                            if ((item.channel.equals("UPTokenPay") && !item.is_default && item.purpose == null) || (item.purpose == null && !item.enabled)) {
                                recommendPayMethods.remove(item);
                            }else if (recommendHasCard&&(item.channel.equals("UPTokenPay") && item.purpose != null)) {
                                recommendPayMethods.remove(item);
                            }
                        }

                        for (int i = commonPayMethods.size() - 1; i >= 0; i--) {
                            PayMethod item = commonPayMethods.get(i);
                            if ((item.channel.equals("UPTokenPay") && !item.is_default && item.purpose == null) || (item.purpose == null && !item.enabled)) {
                                commonPayMethods.remove(item);
                            }else if (commonHasCard&&(item.channel.equals("UPTokenPay") && item.purpose != null)) {
                                commonPayMethods.remove(item);
                            }
                        }

                        //推荐里边是否有银行卡
                        boolean recommendCard = false;
                        boolean commonCard = false;

                        for (int i = 0; i < recommendPayMethods.size(); i++) {
                            if (recommendPayMethods.get(i).channel.equals("UPTokenPay")){
                                recommendCard=true;
                            }
                        }

                        for (int i = 0; i < commonPayMethods.size(); i++) {
                            if (commonPayMethods.get(i).channel.equals("UPTokenPay")){
                                commonCard=true;
                            }
                        }

                        if (!recommendCard&&!commonCard){
                            //都没有银行卡去检查all里边是否有银行卡
                            for (int i = 0; i < bankPayMethods.size(); i++) {
                                if (bankPayMethods.get(i).enabled&&bankPayMethods.get(i).purpose==null){
                                    if (bankPayMethods.get(i).is_recommended){
                                        recommendPayMethods.add(0,bankPayMethods.get(i));
                                    }else {
                                        commonPayMethods.add(0,bankPayMethods.get(i));
                                    }
                                }
                            }
                        }

                        feeCashierWayAdapter = new FeeCashierWayAdapter(FeeCashierDeskActivity.this, Float.parseFloat(amout), payECardRequestData, recommendPayMethods, bankPayMethods, R.layout.fee_cashier_way_item);
                        rcRecommend.setAdapter(feeCashierWayAdapter);
                        feeCashierWayAdapter.notifyDataSetChanged();

                        feeCashierWayAdapter2 = new FeeCashierWayAdapter(FeeCashierDeskActivity.this, Float.parseFloat(amout), payECardRequestData, commonPayMethods, bankPayMethods, R.layout.fee_cashier_way_item);
                        rcCommon.setAdapter(feeCashierWayAdapter2);
                        feeCashierWayAdapter2.notifyDataSetChanged();

                        feeCashierWayAdapter.setPayRequestListener(new FeeCashierWayAdapter.GetPayRequest() {
                            @Override
                            public void getPayRequest(PayMethod payMethod) {
                                if (payMethod == null) {
                                    btnOk.setEnabled(false);
                                } else {
                                    btnOk.setEnabled(true);
                                }
                                FeeCashierDeskActivity.this.payMethod = payMethod;
                                feeCashierWayAdapter2.clearSelected();
                            }
                        });

                        feeCashierWayAdapter2.setPayRequestListener(new FeeCashierWayAdapter.GetPayRequest() {
                            @Override
                            public void getPayRequest(PayMethod payMethod) {
                                if (payMethod == null) {
                                    btnOk.setEnabled(false);
                                } else {
                                    btnOk.setEnabled(true);
                                }
                                FeeCashierDeskActivity.this.payMethod = payMethod;
                                feeCashierWayAdapter.clearSelected();
                            }
                        });

                        btnOk.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //去支付
                                if (FeeCashierDeskActivity.this.payMethod == null) {
                                    Toast.makeText(FeeCashierDeskActivity.this, "请先选择支付方式", Toast.LENGTH_SHORT).show();
                                    return;
                                } else {
                                    if (isFastDoubleClick()) {
                                        //防止重复点击
                                    } else {
                                        final PayRequest payRequest = new PayRequest();
                                        num = Float.parseFloat(amout);
                                        payRequest.pay_method = payMethod;
                                        payRequest.amount = num + "";
                                        payRequest.pay_for = PayFor.RechargeECard;
                                        payRequest.request_data = payECardRequestData;
                                        if (payMethod.channel.equals("UnionPay")) {
                                            if (Util.parseFloat(BankInfoCache.getInstance().getTradeLimit().trade_limit_amt, 0f) > 0.001f && num > Float.parseFloat(BankInfoCache.getInstance().getTradeLimit().trade_limit_amt)) {
                                                showPopwindow(1, num, payMethod);
                                            } else {
                                                payRequest.pay_method = payMethod;
                                                pay(payRequest);
                                            }
                                        } else if (payMethod.channel.equals("RFID")) {
                                            payRequest.pay_method = payMethod;
                                            getECardData(payRequest);
                                        } else if (payMethod.channel.equals("CCBWapPay")) {
                                            payRequest.pay_method = payMethod;
                                            pay(payRequest);
                                        } else if (payMethod.channel.equals("QuickPay")) {
                                            payRequest.pay_method = payMethod;
                                            //需要把支付方式一并传入
                                            Bundle bundle = new Bundle();
                                            QuickPayMethod quickPayMethod = new QuickPayMethod();
                                            quickPayMethod.channel = payMethod.channel;
                                            quickPayMethod.assistance_hint = payMethod.assistance_hint;
                                            quickPayMethod.merchant_code = payMethod.merchant_code;
                                            quickPayMethod.supported_banks = payMethod.supported_banks;
                                            quickPayMethod.promotion_hint = payMethod.promotion_hint;

                                            bundle.putSerializable(QukMobileActivity.METHOD, quickPayMethod);
                                            bundle.putFloat("amount", num);
                                            bundle.putSerializable("payRequest", payRequest);
                                            Intent intent = new Intent(FeeCashierDeskActivity.this, QukMobileActivity.class);
                                            intent.putExtras(bundle);
                                            startActivity(intent);
                                        } else if (payMethod.channel.equals("AliAppPay")) {
                                            payRequest.pay_method = payMethod;
                                            pay(payRequest);
                                        } else if (payMethod.channel.equals("WxAppPay")) {
                                            payRequest.pay_method = payMethod;
                                            pay(payRequest);
                                        } else if (payMethod.channel.equals("UPTokenPay") && payMethod.purpose != null) {
                                            if (App.hasBankCard) {
                                                Intent intent = new Intent(FeeCashierDeskActivity.this, UnionHtmlActivity.class);
                                                Bundle bundle = new Bundle();
                                                bundle.putBoolean(UnionHtmlActivity.EXTRA_FROM_BANKCARDLIST, false);
                                                intent.putExtras(bundle);
                                                startActivity(new Intent(intent));
                                            } else {
                                                Bundle bundle = new Bundle();
                                                bundle.putBoolean("no_more", true);
                                                Intent intent = new Intent(FeeCashierDeskActivity.this, UnionSweptEmptyCardActivity.class);
                                                intent.putExtras(bundle);
                                                startActivity(intent);
                                            }
                                        } else if (payMethod.channel.equals("UPTokenPay") && payMethod.purpose == null) {

                                            if (payMethod.card.getCard_type().equals(UPCardType.Credit) || payMethod.card.getCard_type().equals(UPCardType.QuasiCredit)) {

                                            } else {
                                                for (int i = 0; i < BankInfoCache.getInstance().getTradeLimit().bank_infos.size(); i++) {
                                                    if (BankInfoCache.getInstance().getTradeLimit().bank_infos.get(i).iss_ins_code != null && BankInfoCache.getInstance().getTradeLimit().bank_infos.get(i).iss_ins_code.equals(payMethod.card.getIss_ins_code()) && Float.parseFloat(BankInfoCache.getInstance().getTradeLimit().bank_infos.get(i).limit_per_time) < num) {
                                                        //有大额
                                                        showBackAlertAnim();
                                                        bankName.setText(payMethod.card.getIss_ins_name());
                                                        limtContent.setText("单笔限额" + BankInfoCache.getInstance().getTradeLimit().bank_infos.get(i).limit_per_time + "元,单日限额" + BankInfoCache.getInstance().getTradeLimit().bank_infos.get(i).limit_per_day + "元");
                                                        changePayWay.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View view) {
                                                                hideBackAlertAnim();
                                                            }
                                                        });
                                                        goon.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View view) {
                                                                payRequest.pay_method = payMethod;
                                                                App.payRequest = payRequest;

                                                                PasswordWindow passwordWindow = new PasswordWindow(FeeCashierDeskActivity.this);
                                                                Display display = ((Activity) FeeCashierDeskActivity.this).getWindow().getWindowManager().getDefaultDisplay();
                                                                passwordWindow.setWidth(display.getWidth());
                                                                passwordWindow.setHeight(display.getHeight() / 2 + 100);
                                                                passwordWindow.showAtLocation(btnOk, Gravity.BOTTOM, 0, 0);
                                                                passwordWindow.getPassword(new PasswordWindow.IGetpassword() {
                                                                    @Override
                                                                    public void getpass(String password) {
                                                                        if (password != null) {
                                                                            UPQRQuickPayChannelRequest upqrQuickPayChannelRequest = new UPQRQuickPayChannelRequest();
                                                                            upqrQuickPayChannelRequest.bank_card_code = payMethod.card.getId();
                                                                            try {
                                                                                upqrQuickPayChannelRequest.pay_password = bytesToHexString(Secure.encodeMessageField(password.getBytes("UTF-8")));
                                                                            } catch (UnsupportedEncodingException e) {
                                                                                e.printStackTrace();
                                                                            }
                                                                            payRequest.channel_request_data = upqrQuickPayChannelRequest;
                                                                            pay(payRequest);
                                                                        }
                                                                    }
                                                                });
                                                                hideBackAlertAnim();
                                                            }
                                                        });
                                                        return;
                                                    }
                                                }
                                                payRequest.pay_method = payMethod;
                                                App.payRequest = payRequest;

                                                PasswordWindow passwordWindow = new PasswordWindow(FeeCashierDeskActivity.this);
                                                Display display = ((Activity) FeeCashierDeskActivity.this).getWindow().getWindowManager().getDefaultDisplay();
                                                passwordWindow.setWidth(display.getWidth());
                                                passwordWindow.setHeight(display.getHeight() / 2 + 100);
                                                passwordWindow.showAtLocation(btnOk, Gravity.BOTTOM, 0, 0);
                                                passwordWindow.getPassword(new PasswordWindow.IGetpassword() {
                                                    @Override
                                                    public void getpass(String password) {
                                                        if (password != null) {
                                                            UPQRQuickPayChannelRequest upqrQuickPayChannelRequest = new UPQRQuickPayChannelRequest();
                                                            upqrQuickPayChannelRequest.bank_card_code = payMethod.card.getId();
                                                            try {
                                                                upqrQuickPayChannelRequest.pay_password = bytesToHexString(Secure.encodeMessageField(password.getBytes("UTF-8")));
                                                            } catch (UnsupportedEncodingException e) {
                                                                e.printStackTrace();
                                                            }
                                                            payRequest.channel_request_data = upqrQuickPayChannelRequest;
                                                            pay(payRequest);
                                                        }
                                                    }
                                                });
                                            }

                                        } else if (payMethod.channel.equals("GuiYangCreditLoanPay")) {
                                            if (realname_auth_status != null && (realname_auth_status.equals("OK") || realname_auth_status.equals("Pending"))) {
                                                //检查预授信信息
                                                if (GuiyangIsOpen) {
                                                    GuiyangPay(name, idCard, amout);
                                                } else {
                                                    //进入介绍界面--然后进入检查预授信信息
                                                    //进入实名认证
//                                                    Bundle bundle = new Bundle();
//                                                    bundle.putString("name", name);
//                                                    bundle.putString("idCard", idCard);
//                                                    bundle.putFloat("amount", num);
//                                                    bundle.putBoolean("isVrName", true);
//                                                    Intent intent = new Intent(FeeCashierDeskActivity.this, IntroduceActivity.class);
//                                                    intent.putExtras(bundle);
//                                                    startActivity(intent);
                                                    checkApprovalInfo(idCard,name,num);
                                                }
                                            } else if (realname_auth_status != null && realname_auth_status.equals("NotAuth")) {
                                                Bundle bundle = new Bundle();
                                                bundle.putString("name", name);
                                                bundle.putString("idCard", idCard);
                                                bundle.putFloat("amount", num);
                                                bundle.putBoolean("isVrName", false);
                                                Intent intent = new Intent(FeeCashierDeskActivity.this, IntroduceActivity.class);
                                                intent.putExtras(bundle);
                                                startActivity(intent);
                                            } else {
                                                //实名认证失败
                                                startActivity(new Intent(FeeCashierDeskActivity.this, ErrorActivity.class));
                                            }
                                        }
                                    }
                                }
                            }
                        });
                    }
                });

    }

    private void loadFee() {
        payFeeExtRequests.clear();
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String, Object> map = new HashMap<>();
        map.put("fee_id", FeeId);
        FeeService feeserice = ApiFactory.getFactory().create(FeeService.class);
        feeserice.get_fee_by_id(NetUtils.getRequestParams(FeeCashierDeskActivity.this, map), NetUtils.getSign(NetUtils.getRequestParams(FeeCashierDeskActivity.this, map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(FeeCashierDeskActivity.this, true, null) {
                    @Override
                    public void onNext(Object o) {

                        allPayMethods.clear();
                        recommendPayMethods.clear();
                        commonPayMethods.clear();

                        ISResponse isResponse = new Gson().fromJson(o.toString(), ISResponse.class);
                        for (int i = 0; i < payfees.size(); i++) {
                            for (Iterator iter = payfees.get(i).entrySet().iterator(); iter.hasNext(); ) {
                                Map.Entry element = (Map.Entry) iter.next();
                                Object strKey = element.getKey();
                                Object strValue = element.getValue();
                                System.out.println(strKey + "/" + strValue);
                                String[] sourceStrArray = strValue.toString().split(":");
                                payFeeExtRequest = new PayFeeExtRequest();
                                payFeeExtRequest.id = (String) element.getKey();
                                payFeeExtRequest.amount = sourceStrArray[0];
                                payFeeExtRequest.pay_amount = sourceStrArray[1];
                                payFeeExtRequest.title = sourceStrArray[2];
                                payFeeExtRequests.add(payFeeExtRequest);
                                num = num + Float.parseFloat(sourceStrArray[1]);
                            }
                        }
                        //除此之外还添加app中的抵扣项目
                        if (App.subFeeDeductionHashMap.size() > 0) {
                            Iterator iter = App.subFeeDeductionHashMap.entrySet().iterator();
                            while (iter.hasNext()) {
                                Map.Entry entry = (Map.Entry) iter.next();
                                Object value = entry.getValue();
                                if (busss_type != null) {
                                    if (busss_type.equals(((SubFeeItem) value).business_channel) && ((SubFeeItem) value).isSelect) {
                                        payFeeExtRequest = new PayFeeExtRequest();
                                        payFeeExtRequest.id = ((SubFeeItem) value).id;
                                        payFeeExtRequest.amount = ((SubFeeItem) value).amount;
                                        payFeeExtRequest.pay_amount = ((SubFeeItem) value).amount;
                                        payFeeExtRequest.title = ((SubFeeItem) value).title;
                                        payFeeExtRequests.add(payFeeExtRequest);
                                    }
                                }
                            }
                        }
                        payFeeRequestData = new PayFeeRequestData();
                        payFeeRequestData.id = FeeId;
                        payFeeRequestData.exts = payFeeExtRequests;
                        if (fee_title != null)
                            payFeeRequestData.title = fee_title;

                        allPayMethods.addAll(isResponse.fee_details.pay_methods);
                        recommendPayMethods.addAll(isResponse.fee_details.pay_methods);
                        commonPayMethods.addAll(isResponse.fee_details.pay_methods);

                        //加载推荐的支付方式列表
                        Iterator iter = recommendPayMethods.iterator();
                        while (iter.hasNext()) {
                            if (!((PayMethod) iter.next()).is_recommended) {
                                iter.remove();
                            }
                        }

                        //加载非推荐的支付方式列表
                        Iterator iter2 = commonPayMethods.iterator();
                        while (iter2.hasNext()) {
                            if (((PayMethod) iter2.next()).is_recommended) {
                                iter2.remove();
                            }
                        }

                        boolean recommendHasCard = false;
                        boolean commonHasCard = false;

                        for (int i = 0; i < recommendPayMethods.size(); i++) {
                            if (recommendPayMethods.get(i).channel.equals("UPTokenPay") && recommendPayMethods.get(i).enabled && recommendPayMethods.get(i).purpose == null) {
                                //有一张可用的卡
                                recommendHasCard = true;
                            }
                        }

                        for (int i = 0; i < commonPayMethods.size(); i++) {
                            if (commonPayMethods.get(i).channel.equals("UPTokenPay") && commonPayMethods.get(i).enabled && commonPayMethods.get(i).purpose == null) {
                                //有一张可用的卡
                                commonHasCard = true;
                            }
                        }


                        bankPayMethods.clear();
                        for (int i = 0; i < allPayMethods.size(); i++) {
                            if (allPayMethods.get(i).channel.equals("UPTokenPay")) {
                                bankPayMethods.add(allPayMethods.get(i));
                            }
                        }

                        for (int i = recommendPayMethods.size() - 1; i >= 0; i--) {
                            PayMethod item = recommendPayMethods.get(i);
                            if ((item.channel.equals("UPTokenPay") && !item.is_default && item.purpose == null) || (item.purpose == null && !item.enabled)) {
                                recommendPayMethods.remove(item);
                            }else if (recommendHasCard&&(item.channel.equals("UPTokenPay") && item.purpose != null)) {
                                recommendPayMethods.remove(item);
                            }
                        }

                        for (int i = commonPayMethods.size() - 1; i >= 0; i--) {
                            PayMethod item = commonPayMethods.get(i);
                            if ((item.channel.equals("UPTokenPay") && !item.is_default && item.purpose == null) || (item.purpose == null && !item.enabled)) {
                                commonPayMethods.remove(item);
                            }else if (commonHasCard&&(item.channel.equals("UPTokenPay") && item.purpose != null)) {
                                commonPayMethods.remove(item);
                            }
                        }

                        //推荐里边是否有银行卡
                        boolean recommendCard = false;
                        boolean commonCard = false;

                        for (int i = 0; i < recommendPayMethods.size(); i++) {
                            if (recommendPayMethods.get(i).channel.equals("UPTokenPay")){
                                recommendCard=true;
                            }
                        }

                        for (int i = 0; i < commonPayMethods.size(); i++) {
                            if (commonPayMethods.get(i).channel.equals("UPTokenPay")){
                                commonCard=true;
                            }
                        }

                        if (!recommendCard&&!commonCard){
                            //都没有银行卡去检查all里边是否有银行卡
                            for (int i = 0; i < bankPayMethods.size(); i++) {
                                if (bankPayMethods.get(i).enabled&&bankPayMethods.get(i).purpose==null){
                                    if (bankPayMethods.get(i).is_recommended){
                                        recommendPayMethods.add(0,bankPayMethods.get(i));
                                    }else {
                                        commonPayMethods.add(0,bankPayMethods.get(i));
                                    }
                                }
                            }
                        }

                        feeCashierWayAdapter = new FeeCashierWayAdapter(FeeCashierDeskActivity.this, num, payFeeRequestData, recommendPayMethods, bankPayMethods, R.layout.fee_cashier_way_item);
                        rcRecommend.setAdapter(feeCashierWayAdapter);
                        if (feeCashierWayAdapter!=null)
                        feeCashierWayAdapter.notifyDataSetChanged();

                        feeCashierWayAdapter2 = new FeeCashierWayAdapter(FeeCashierDeskActivity.this, num, payFeeRequestData, commonPayMethods, bankPayMethods, R.layout.fee_cashier_way_item);
                        rcCommon.setAdapter(feeCashierWayAdapter2);
                        if (feeCashierWayAdapter2!=null)
                        feeCashierWayAdapter2.notifyDataSetChanged();

                        feeCashierWayAdapter.setPayRequestListener(new FeeCashierWayAdapter.GetPayRequest() {
                            @Override
                            public void getPayRequest(PayMethod payMethod) {
                                if (payMethod == null) {
                                    btnOk.setEnabled(false);
                                } else {
                                    btnOk.setEnabled(true);
                                }
                                FeeCashierDeskActivity.this.payMethod = payMethod;
                                feeCashierWayAdapter2.clearSelected();
                            }
                        });

                        feeCashierWayAdapter2.setPayRequestListener(new FeeCashierWayAdapter.GetPayRequest() {
                            @Override
                            public void getPayRequest(PayMethod payMethod) {
                                if (payMethod == null) {
                                    btnOk.setEnabled(false);
                                } else {
                                    btnOk.setEnabled(true);
                                }
                                FeeCashierDeskActivity.this.payMethod = payMethod;
                                feeCashierWayAdapter.clearSelected();
                            }
                        });

                        btnOk.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //去支付
                                if (FeeCashierDeskActivity.this.payMethod == null) {
                                    Toast.makeText(FeeCashierDeskActivity.this, "请先选择支付方式", Toast.LENGTH_SHORT).show();
                                    return;
                                } else {
                                    if (isFastDoubleClick()) {
                                        //防止重复点击
                                    } else {
                                        final PayRequest payRequest = new PayRequest();
                                        payRequest.amount = num + "";
                                        if (payFeeRequestData != null) {
                                            payRequest.pay_for = PayFor.SchoolFee;
                                            payRequest.request_data = payFeeRequestData;
                                        }
                                        if (payMethod.channel.equals("UnionPay")) {
                                            if (Util.parseFloat(BankInfoCache.getInstance().getTradeLimit().trade_limit_amt, 0f) > 0.001f && num > Float.parseFloat(BankInfoCache.getInstance().getTradeLimit().trade_limit_amt)) {
                                                showPopwindow(1, num, payMethod);
                                            } else {
                                                payRequest.pay_method = payMethod;
                                                pay(payRequest);
                                            }
                                        } else if (payMethod.channel.equals("RFID")) {
                                            payRequest.pay_method = payMethod;
                                            getECardData(payRequest);
                                        } else if (payMethod.channel.equals("CCBWapPay")) {
                                            payRequest.pay_method = payMethod;
                                            pay(payRequest);
                                        } else if (payMethod.channel.equals("QuickPay")) {
                                            payRequest.pay_method = payMethod;
                                            //需要把支付方式一并传入
                                            Bundle bundle = new Bundle();
                                            QuickPayMethod quickPayMethod = new QuickPayMethod();
                                            quickPayMethod.channel = payMethod.channel;
                                            quickPayMethod.assistance_hint = payMethod.assistance_hint;
                                            quickPayMethod.merchant_code = payMethod.merchant_code;
                                            quickPayMethod.supported_banks = payMethod.supported_banks;
                                            quickPayMethod.promotion_hint = payMethod.promotion_hint;

                                            bundle.putSerializable(QukMobileActivity.METHOD, quickPayMethod);
                                            bundle.putFloat("amount", num);
                                            bundle.putSerializable("payRequest", payRequest);
                                            Intent intent = new Intent(FeeCashierDeskActivity.this, QukMobileActivity.class);
                                            intent.putExtras(bundle);
                                            startActivity(intent);
                                        } else if (payMethod.channel.equals("AliAppPay")) {
                                            payRequest.pay_method = payMethod;
                                            pay(payRequest);
                                        } else if (payMethod.channel.equals("WxAppPay")) {
                                            payRequest.pay_method = payMethod;
                                            pay(payRequest);
                                        } else if (payMethod.channel.equals("UPTokenPay") && payMethod.purpose != null) {
                                            if (App.hasBankCard) {
                                                Intent intent = new Intent(FeeCashierDeskActivity.this, UnionHtmlActivity.class);
                                                Bundle bundle = new Bundle();
                                                bundle.putBoolean(UnionHtmlActivity.EXTRA_FROM_BANKCARDLIST, false);
                                                intent.putExtras(bundle);
                                                startActivity(new Intent(intent));
                                            } else {
                                                Bundle bundle = new Bundle();
                                                bundle.putBoolean("no_more", true);
                                                Intent intent = new Intent(FeeCashierDeskActivity.this, UnionSweptEmptyCardActivity.class);
                                                intent.putExtras(bundle);
                                                startActivity(intent);
                                            }
                                        } else if (payMethod.channel.equals("UPTokenPay") && payMethod.purpose == null) {

                                            if (payMethod.card.getCard_type().equals(UPCardType.Credit) || payMethod.card.getCard_type().equals(UPCardType.QuasiCredit)) {

                                            } else {
                                                for (int i = 0; i < BankInfoCache.getInstance().getTradeLimit().bank_infos.size(); i++) {
                                                    if (BankInfoCache.getInstance().getTradeLimit().bank_infos.get(i).iss_ins_code != null && BankInfoCache.getInstance().getTradeLimit().bank_infos.get(i).iss_ins_code.equals(payMethod.card.getIss_ins_code()) && Float.parseFloat(BankInfoCache.getInstance().getTradeLimit().bank_infos.get(i).limit_per_time) < num) {
                                                        //有大额
                                                        showBackAlertAnim();
                                                        bankName.setText(payMethod.card.getIss_ins_name());
                                                        limtContent.setText("单笔限额" + BankInfoCache.getInstance().getTradeLimit().bank_infos.get(i).limit_per_time + "元,单日限额" + BankInfoCache.getInstance().getTradeLimit().bank_infos.get(i).limit_per_day + "元");
                                                        changePayWay.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View view) {
                                                                hideBackAlertAnim();
                                                            }
                                                        });
                                                        goon.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View view) {
                                                                payRequest.pay_method = payMethod;
                                                                App.payRequest = payRequest;

                                                                PasswordWindow passwordWindow = new PasswordWindow(FeeCashierDeskActivity.this);
                                                                Display display = ((Activity) FeeCashierDeskActivity.this).getWindow().getWindowManager().getDefaultDisplay();
                                                                passwordWindow.setWidth(display.getWidth());
                                                                passwordWindow.setHeight(display.getHeight() / 2 + 100);
                                                                passwordWindow.showAtLocation(btnOk, Gravity.BOTTOM, 0, 0);
                                                                passwordWindow.getPassword(new PasswordWindow.IGetpassword() {
                                                                    @Override
                                                                    public void getpass(String password) {
                                                                        if (password != null) {
                                                                            UPQRQuickPayChannelRequest upqrQuickPayChannelRequest = new UPQRQuickPayChannelRequest();
                                                                            upqrQuickPayChannelRequest.bank_card_code = payMethod.card.getId();
                                                                            try {
                                                                                upqrQuickPayChannelRequest.pay_password = bytesToHexString(Secure.encodeMessageField(password.getBytes("UTF-8")));
                                                                            } catch (UnsupportedEncodingException e) {
                                                                                e.printStackTrace();
                                                                            }
                                                                            payRequest.channel_request_data = upqrQuickPayChannelRequest;
                                                                            pay(payRequest);
                                                                        }
                                                                    }
                                                                });
                                                                hideBackAlertAnim();
                                                            }
                                                        });
                                                        return;
                                                    }
                                                }
                                                payRequest.pay_method = payMethod;
                                                App.payRequest = payRequest;

                                                PasswordWindow passwordWindow = new PasswordWindow(FeeCashierDeskActivity.this);
                                                Display display = ((Activity) FeeCashierDeskActivity.this).getWindow().getWindowManager().getDefaultDisplay();
                                                passwordWindow.setWidth(display.getWidth());
                                                passwordWindow.setHeight(display.getHeight() / 2 + 100);
                                                passwordWindow.showAtLocation(btnOk, Gravity.BOTTOM, 0, 0);
                                                passwordWindow.getPassword(new PasswordWindow.IGetpassword() {
                                                    @Override
                                                    public void getpass(String password) {
                                                        if (password != null) {
                                                            UPQRQuickPayChannelRequest upqrQuickPayChannelRequest = new UPQRQuickPayChannelRequest();
                                                            upqrQuickPayChannelRequest.bank_card_code = payMethod.card.getId();
                                                            try {
                                                                upqrQuickPayChannelRequest.pay_password = bytesToHexString(Secure.encodeMessageField(password.getBytes("UTF-8")));
                                                            } catch (UnsupportedEncodingException e) {
                                                                e.printStackTrace();
                                                            }
                                                            payRequest.channel_request_data = upqrQuickPayChannelRequest;
                                                            pay(payRequest);
                                                        }
                                                    }
                                                });
                                            }

                                        } else if (payMethod.channel.equals("GuiYangCreditLoanPay")) {
                                            if (realname_auth_status != null && (realname_auth_status.equals("OK") || realname_auth_status.equals("Pending"))) {
                                                //检查预授信信息
                                                if (GuiyangIsOpen) {
                                                    GuiyangPay(name, idCard, num + "");
                                                } else {
                                                    //进入介绍界面--然后进入检查预授信信息
                                                    //进入实名认证
//                                                    Bundle bundle = new Bundle();
//                                                    bundle.putString("name", name);
//                                                    bundle.putString("idCard", idCard);
//                                                    bundle.putFloat("amount", num);
//                                                    bundle.putBoolean("isVrName", true);
//                                                    Intent intent = new Intent(FeeCashierDeskActivity.this, IntroduceActivity.class);
//                                                    intent.putExtras(bundle);
//                                                    startActivity(intent);
                                                    checkApprovalInfo(idCard,name,num);
                                                }
                                            } else if (realname_auth_status != null && realname_auth_status.equals("NotAuth")) {
                                                Bundle bundle = new Bundle();
                                                bundle.putString("name", name);
                                                bundle.putString("idCard", idCard);
                                                bundle.putFloat("amount", num);
                                                bundle.putBoolean("isVrName", false);
                                                Intent intent = new Intent(FeeCashierDeskActivity.this, IntroduceActivity.class);
                                                intent.putExtras(bundle);
                                                startActivity(intent);
                                            } else {
                                                //实名认证失败
                                                startActivity(new Intent(FeeCashierDeskActivity.this, ErrorActivity.class));
                                            }
                                        }
                                    }
                                }
                            }
                        });
                    }
                });
    }


    float num;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (data != null && data.getExtras() != null && requestCode == REQUEST_GR_PAY) {
                feeCashierWayAdapter.gysdkReturn(data.getExtras().getString("tn"));
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        recommendPayMethods.clear();
        allPayMethods.clear();
        commonPayMethods.clear();
//        bankPayMethods.clear();

    }

    public static class ISResponse extends RealmObject {
        public BaseResponse resp;
        public Fee fee_details;
        public List<SubFee> deductible_fees;
        public SchoolRoll school_roll;
        public String pay_limit_hint;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        payRequest = null;
        payOderResponse = null;
        if (!rxSbscription.isUnsubscribed()) {
            rxSbscription.unsubscribe();
        }
    }

    private void get_pay_result(String pay_for, String order_no) {
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String, Object> map = new HashMap<>();
        map.put("pay_for", pay_for);
        map.put("order_no", order_no);
        PaymentService paymentService = ApiFactory.getFactory().create(PaymentService.class);
        paymentService.query_order_pay_record(NetUtils.getRequestParams(getActivity(), map), NetUtils.getSign(NetUtils.getRequestParams(getActivity(), map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(FeeCashierDeskActivity.this, true, null) {

                    @Override
                    public void onNext(Object o) {
                        OcpPayFixedDeskActivity.PaymentRecordResponse recordResponse = new Gson().fromJson(o.toString(), OcpPayFixedDeskActivity.PaymentRecordResponse.class);
                        if (recordResponse.payment_result.order_status.equals("paied")) {
                            //成功
                            Intent intent = new Intent(FeeCashierDeskActivity.this, PaymentStatusActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString(PaymentStatusActivity.AMOUNT, payOderResponse.order.payment_amt);
                            bundle.putString(PaymentStatusActivity.NAME, payOderResponse.order.trade_summary);
                            bundle.putSerializable(PaymentStatusActivity.WAY, payOderResponse.order.pay_method);
                            bundle.putString(PaymentStatusActivity.TIME, payOderResponse.order.order_time);
                            bundle.putString(PaymentStatusActivity.ODER, payOderResponse.order.order_no);
                            intent.putExtras(bundle);
                            startActivity(intent);
                            finish();
                        } else if (recordResponse.payment_result.order_status.equals("unpay") || recordResponse.payment_result.order_status.equals("processing")) {
                            //弹窗
                            if (dialog != null) {

                            } else {
                                dialog = new AlertDialog.Builder(FeeCashierDeskActivity.this)
                                        .setTitle("支付结果")
                                        .setMessage("暂未查询到支付结果\n" +
                                                "\n" +
                                                "支付结果可能略有延迟，请确认你的支付交易，\n" +
                                                "\n" +
                                                "若未成功，可继续完成支付")
                                        .setNegativeButton("继续支付", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        })
                                        .setPositiveButton("我已支付", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                if (from.equals(FEE)) {
                                                    RxBus.getDefault().send("finish");
                                                    finish();
                                                } else {
                                                    finish();
                                                }
                                                Toast.makeText(FeeCashierDeskActivity.this, "你可以在“我的-交易记录”下查看支付状态", Toast.LENGTH_LONG).show();
                                            }
                                        })
                                        .create();

                                dialog.show();
                            }

                        }
                    }
                });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (App.isNeedCheck) {
                    if (payOderResponse != null && payOderResponse.order != null && payOderResponse.order.pay_for != null && payOderResponse.order.order_no != null) {
                        get_pay_result(payOderResponse.order.pay_for, payOderResponse.order.order_no);
                    }
                }

            }
        }, 1000);
    }

    public class UPQRQuickPayChannelRequest {
        public String pay_password;        // 支付密码, aes128加密
        public String bank_card_code;
    }

    private void auto_pay(final PayRequest payRequest) {
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String, Object> map = new HashMap<>();
        map.put("pay_req", payRequest);
        PaymentService paymentService = ApiFactory.getFactory().create(PaymentService.class);
        paymentService.payOder(NetUtils.getRequestParams(FeeCashierDeskActivity.this, map), NetUtils.getSign(NetUtils.getRequestParams(FeeCashierDeskActivity.this, map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(FeeCashierDeskActivity.this, true, null) {

                    @Override
                    public void onNext(Object o) {
                        CashierWayAdapter.PayOderResponse payOderResponse = new Gson().fromJson(o.toString(), CashierWayAdapter.PayOderResponse.class);
                        //获取到paymentoder
                        if (payOderResponse.resp.resp_code.equals(AppConstant.SUCCESS)) {
                            if (payRequest.pay_method.channel.equals("UPTokenPay") && payRequest.pay_method.purpose == null) {
                                Intent intent = new Intent(FeeCashierDeskActivity.this, PaymentStatusActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putString(PaymentStatusActivity.AMOUNT, payOderResponse.order.payment_amt);
                                bundle.putString(PaymentStatusActivity.NAME, payOderResponse.order.trade_summary);
                                bundle.putSerializable(PaymentStatusActivity.WAY, payOderResponse.order.pay_method);
                                bundle.putString(PaymentStatusActivity.TIME, payOderResponse.order.order_time);
                                bundle.putString(PaymentStatusActivity.ODER, payOderResponse.order.order_no);
                                intent.putExtras(bundle);
                                FeeCashierDeskActivity.this.startActivity(intent);
                                FeeCashierDeskActivity.this.finish();
                            }
                        }

                    }
                });
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


    private void getAccountPayMethodInfo(final Float amount) {
        downloadGysdkDialog = new DownloadGysdkDialog(FeeCashierDeskActivity.this);
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String, Object> map = new HashMap<>();
        map.put("loan_way_type", "GuiYangCreditLoanPay");
        LoanService loanService = ApiFactory.getFactory().create(LoanService.class);
        loanService.get_account_info(NetUtils.getRequestParams(FeeCashierDeskActivity.this, map), NetUtils.getSign(NetUtils.getRequestParams(FeeCashierDeskActivity.this, map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(FeeCashierDeskActivity.this, true, null) {
                    @Override
                    public void onNext(Object o) {
                        final FeeCashierWayAdapter.LoanAccountInfoRep loanAccountInfoRep = new Gson().fromJson(o.toString(), FeeCashierWayAdapter.LoanAccountInfoRep.class);
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
                        if (feeCashierWayAdapter!=null)
                        feeCashierWayAdapter.notifyDataSetChanged();
                        if (feeCashierWayAdapter2!=null)
                        feeCashierWayAdapter2.notifyDataSetChanged();
                    }
                });
    }

    private void pay(final PayRequest payRequest) {
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String, Object> map = new HashMap<>();
        map.put("pay_req", payRequest);
        PaymentService paymentService = ApiFactory.getFactory().create(PaymentService.class);
        paymentService.payOder(NetUtils.getRequestParams(FeeCashierDeskActivity.this, map), NetUtils.getSign(NetUtils.getRequestParams(FeeCashierDeskActivity.this, map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(FeeCashierDeskActivity.this, true, null) {

                    @Override
                    public void onNext(Object o) {
                        FeeCashierWayAdapter.PayOderResponse payOderResponse = new Gson().fromJson(o.toString(), FeeCashierWayAdapter.PayOderResponse.class);
                        //获取到paymentoder
                        if (payOderResponse.resp.resp_code.equals(AppConstant.SUCCESS)) {
                            FeeCashierDeskActivity.payRequest = payRequest;
                            FeeCashierDeskActivity.payOderResponse = payOderResponse;
                            if (payRequest.pay_method.channel.equals("AliAppPay")) {
                                //支付宝支付
                                AliPay(payOderResponse.order.pay_data);
                            } else if (payRequest.pay_method.channel.equals("WxAppPay")) {
                                if (isWeixinAvilible(FeeCashierDeskActivity.this)) {
                                    FeeCashierWayAdapter.DataBean dataBean = new Gson().fromJson(payOderResponse.order.pay_data, FeeCashierWayAdapter.DataBean.class);
                                    weixinPay(dataBean);

                                    FeeCashierDeskActivity.payOderResponse = payOderResponse;
                                    FeeCashierDeskActivity.payRequest = payRequest;
                                } else {
                                    Toast.makeText(FeeCashierDeskActivity.this, "请先安装微信", Toast.LENGTH_SHORT).show();
                                }

                            }

                            if (payRequest.pay_method.channel.equals("UPTokenPay") && payRequest.pay_method.purpose == null) {
                                Intent intent = new Intent(FeeCashierDeskActivity.this, PaymentStatusActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putString(PaymentStatusActivity.AMOUNT, payOderResponse.order.payment_amt);
                                bundle.putString(PaymentStatusActivity.NAME, payOderResponse.order.trade_summary);
                                bundle.putSerializable(PaymentStatusActivity.WAY, payOderResponse.order.pay_method);
                                bundle.putString(PaymentStatusActivity.TIME, payOderResponse.order.order_time);
                                bundle.putString(PaymentStatusActivity.ODER, payOderResponse.order.order_no);
                                intent.putExtras(bundle);
                                startActivity(intent);
                                finish();
                            }

                            if (payRequest.pay_method.channel.equals("UnionPay")) {
                                PaymentInfo paymentInfo = new PaymentInfo();
                                paymentInfo.pay_method = payRequest.pay_method;
                                paymentInfo.payment_amt = payOderResponse.order.payment_amt;
                                paymentInfo.channel_orderno = payOderResponse.order.order_no;
                                paymentInfo.pay_info = payOderResponse.order.pay_data;
                                paymentInfo.fee_name = payOderResponse.order.trade_summary + "-支付成功";
                                paymentInfo.fee_time = payOderResponse.order.order_time;
                                Intent intent = new Intent(FeeCashierDeskActivity.this, MyPayActivity.class);
                                intent.putExtra(MyPayActivity.EXTRA_AMOUNT, payOderResponse.order.payment_amt);
                                intent.putExtra(MyPayActivity.EXTRA_PAY_FOR, PayFor.SchoolFee);
                                intent.putExtra(MyPayActivity.EXTRA_PAYMENT_INFO, paymentInfo);
                                startActivity(intent);
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
        if (FeeCashierDeskActivity.this == null) {
            return;
        }
        // 利用layoutInflater获得View
        @SuppressLint("WrongConstant") LayoutInflater inflater = (LayoutInflater) FeeCashierDeskActivity.this.getSystemService("layout_inflater");//FeeCashierDeskActivity.this.LAYOUT_INFLATER_SERVICE
        @SuppressLint("InflateParams") final View view = inflater.inflate(R.layout.popup_window_hint, null);
        final PopupWindow window = new PopupWindow(view,
                WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        // 设置popWindow弹出窗体可点击，这句话必须添加，并且是true
        window.setFocusable(true);
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0x7DC0C0C0);
        window.setBackgroundDrawable(dw);
        // 设置背景颜色变暗
        WindowManager.LayoutParams lp = ((Activity) FeeCashierDeskActivity.this).getWindow().getAttributes();
        lp.alpha = 0.5f;
        ((Activity) FeeCashierDeskActivity.this).getWindow().setAttributes(lp);
        // 设置popWindow的显示和消失动画
        if (root == 1)
            window.setAnimationStyle(R.style.mypopwindow_anim_style);
        // 在底部显示
        window.showAtLocation(((Activity) FeeCashierDeskActivity.this).findViewById(R.id.btn_ok),
                Gravity.BOTTOM, 0, 0);

        final RecyclerView listview = (RecyclerView) view.findViewById(R.id.recyclerview);

        final LinearLayout ll_selected_bank = (LinearLayout) view.findViewById(R.id.ll_selected_bank);
        final TextView daizhifu = (TextView) view.findViewById(R.id.daizhifu);
        final TextView GoPay = (TextView) view.findViewById(R.id.btn_go_pay);
        GoPay.setEnabled(false);
        GoPay.setBackgroundColor(Color.rgb(102, 102, 102));
        final TextView TvtradeLimitShowHint = (TextView) view.findViewById(R.id.tv_trade_limit);
        final ImageView Back = (ImageView) view.findViewById(R.id.back);

        window.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                // 设置背景颜色不透明
                WindowManager.LayoutParams lp = ((Activity) FeeCashierDeskActivity.this).getWindow().getAttributes();
                lp.alpha = 1f;
                ((Activity) FeeCashierDeskActivity.this).getWindow().setAttributes(lp);
            }
        });

        daizhifu.setText(new DecimalFormat("0.00").format(Damount) + "元");

        GoPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PayRequest payRequest = new PayRequest();
                payRequest.amount = num + "";
                if (payFeeRequestData != null) {
                    payRequest.pay_for = PayFor.SchoolFee;
                    payRequest.request_data = payFeeRequestData;
                }
                payRequest.pay_method = payMethod;
                pay(payRequest);
            }
        });

        ArrayList<BankLimit> datas = new ArrayList<BankLimit>();
        datas.clear();
        datas.addAll(BankInfoCache.getInstance().getBankInfo());
        FeeCashierWayAdapter.BankAdapter adapter = new FeeCashierWayAdapter.BankAdapter(FeeCashierDeskActivity.this, R.layout.item_bank_trade_limit, datas);

        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                window.dismiss();
            }
        });

        listview.setLayoutManager(new LinearLayoutManager(FeeCashierDeskActivity.this));

        listview.setAdapter(adapter);

        adapter.setOnClickGetBankInfoListener(new FeeCashierWayAdapter.BankAdapter.IGetBankInfo() {

            @Override
            public void getBankInfoByPosition(BankLimit BankLimit) {
                GoPay.setEnabled(true);
                GoPay.setBackgroundColor(Color.rgb(32, 138, 240));
                if (!BankLimit.local) {
                    if (num > Float.parseFloat(BankLimit.limit_per_time)) {
                        LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) ll_selected_bank.getLayoutParams();
                        linearParams.height = DensityUtil.dip2px(FeeCashierDeskActivity.this, 55);
                        ll_selected_bank.setLayoutParams(linearParams);
                        ll_selected_bank.setVisibility(View.VISIBLE);
                        TranslateAnimation ab = new TranslateAnimation(0, 0, 100, 0);
                        ab.setDuration(300);
                        ll_selected_bank.startAnimation(ab);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                AnimatorUtil.animHeightToView((Activity) FeeCashierDeskActivity.this, ll_selected_bank, true, 300);
                            }
                        }, 300);

                        TvtradeLimitShowHint.setText(BankInfoCache.getInstance().getTradeLimit().out_of_limit_notice);
                    } else {
                        LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) ll_selected_bank.getLayoutParams();
                        linearParams.height = DensityUtil.dip2px(FeeCashierDeskActivity.this, 55);
                        ll_selected_bank.setLayoutParams(linearParams);
                        ll_selected_bank.setVisibility(View.GONE);
                    }
                } else {
                    ll_selected_bank.setVisibility(View.VISIBLE);
                    LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) ll_selected_bank.getLayoutParams();
                    linearParams.height = DensityUtil.dip2px(FeeCashierDeskActivity.this, 55);
                    ll_selected_bank.setLayoutParams(linearParams);
                    TranslateAnimation ab = new TranslateAnimation(0, 0, 100, 0);
                    ab.setDuration(300);
                    ll_selected_bank.startAnimation(ab);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            AnimatorUtil.animHeightToView((Activity) FeeCashierDeskActivity.this, ll_selected_bank, true, 300);
                        }
                    }, 300);
                    TvtradeLimitShowHint.setText(BankInfoCache.getInstance().getTradeLimit().other_bank_notice);
                }
            }
        });
    }


    private void getECardData(final PayRequest request) {
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String, Object> map = new HashMap<>();
        EcardService ecardService = ApiFactory.getFactory().create(EcardService.class);
        ecardService.getEcard(NetUtils.getRequestParams(FeeCashierDeskActivity.this, map), NetUtils.getSign(NetUtils.getRequestParams(FeeCashierDeskActivity.this, map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(FeeCashierDeskActivity.this, true, null) {
                    @Override
                    public void onNext(Object o) {
                        Ecardpresenter.EcardResponse response = new Gson().fromJson(o.toString(), Ecardpresenter.EcardResponse.class);
                        if (response.ecard.status.equals("OK")) {
                            Intent intent = new Intent(FeeCashierDeskActivity.this, CashierRFIDPayActivity.class);
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
                            startActivity(intent);
                        } else {
                            Toast.makeText(FeeCashierDeskActivity.this, "获取一卡通状态失败", Toast.LENGTH_LONG).show();
                        }
                    }
                });

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
            payRequest.pay_for = com.xyaxf.axpay.modle.PayFor.RechargeECard;
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
        paymentService.payOder(NetUtils.getRequestParams(FeeCashierDeskActivity.this, map), NetUtils.getSign(NetUtils.getRequestParams(FeeCashierDeskActivity.this, map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(FeeCashierDeskActivity.this, true, null) {
                    @Override
                    public void onNext(Object o) {
                        final FeeCashierWayAdapter.GyPayOderResponses payOderResponse = new Gson().fromJson(o.toString(), FeeCashierWayAdapter.GyPayOderResponses.class);
                        FeeCashierDeskActivity.this.gyPayOderResponses = payOderResponse;
                        if (payOderResponse.resp.resp_code.equals(AppConstant.SUCCESS)) {
                            final FeeCashierWayAdapter.Tn tn = new Gson().fromJson(payOderResponse.order.pay_data, FeeCashierWayAdapter.Tn.class);
                            Intent it = new Intent(CashierDeskActivity.GYSDK_PACKAGE_ACTIVITY_NAME);
                            Bundle bundle = new Bundle();
                            bundle.putString("type", "pay");
                            bundle.putString("idCard", idCard);
                            bundle.putString("tn", tn.tn);
                            it.putExtras(bundle);
                            try {
                                startActivityForResult(it, CashierDeskActivity.REQUEST_GR_PAY);
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
        meService.getCheckUpdate(NetUtils.getRequestParams(FeeCashierDeskActivity.this, map), NetUtils.getSign(NetUtils.getRequestParams(FeeCashierDeskActivity.this, map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(FeeCashierDeskActivity.this, true, null) {
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
            }
        }

        if (Util.isEmpty(url)) {
            Toast.makeText(FeeCashierDeskActivity.this, "下载失败", Toast.LENGTH_LONG).show();
        } else {
            if (App.mAxLoginSp.getGysdkDone() || DownloadAPKService.isRunning()) {
                // 下载完成；正在下载.
                downloadGysdkDialog.gotoService();
            } else {
                downloadGysdkDialog.show();
            }
        }
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
                PayTask alipay = new PayTask(FeeCashierDeskActivity.this);
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
                        Intent intent = new Intent(FeeCashierDeskActivity.this, PaymentStatusActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString(PaymentStatusActivity.AMOUNT, mPaymentInfo.payment_amt);
                        bundle.putString(PaymentStatusActivity.NAME, mPaymentInfo.fee_name);
                        bundle.putSerializable(PaymentStatusActivity.WAY, mPaymentInfo.pay_method);
                        bundle.putString(PaymentStatusActivity.TIME, mPaymentInfo.fee_time);
                        bundle.putString(PaymentStatusActivity.ODER, mPaymentInfo.channel_orderno);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        finish();
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        Toast.makeText(FeeCashierDeskActivity.this, "支付取消", Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                default:
                    break;
            }
        }
    };

    private void weixinPay(FeeCashierWayAdapter.DataBean dataBean) {
        Log.d("CashierWayAdapter", new Gson().toJson(dataBean));
        App.WXAPPID = dataBean.getAppid();
        IWXAPI api = WXAPIFactory.createWXAPI(FeeCashierDeskActivity.this, dataBean.getAppid());
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

    private void showBackAlertAnim() {
        btnOk.setVisibility(View.GONE);
        neePasswordView.setVisibility(View.VISIBLE);
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(neePasswordView, "translationY", 0, (int) metrics.density * 450);
        animator2.setDuration(600);
        animator2.start();
        grayBg.setVisibility(View.VISIBLE);
        grayBg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void hideBackAlertAnim() {
        btnOk.setVisibility(View.VISIBLE);
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(neePasswordView, "translationY", (int) metrics.density * 450, 0);
        animator2.setDuration(600);
        animator2.start();
        grayBg.setVisibility(View.GONE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                neePasswordView.setVisibility(View.GONE);
            }
        }, 590);
    }

    @Override
    public boolean rightButtonEnabled() {
        return true;
    }

    @Override
    public String getRightButtonText() {
        return "帮助";
    }

    @Override
    public void onRightButtonClick(View view) {
        super.onRightButtonClick(view);
        startActivity(new Intent(FeeCashierDeskActivity.this, MeHelpCenterActivity.class));
    }

    private void checkApprovalInfo(final String idCard,final String name,final float amount) {
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String, Object> map = new HashMap<>();
        map.put("loan_way_type", "GuiYangCreditLoanPay");
        LoanService loanService = ApiFactory.getFactory().create(LoanService.class);
        loanService.check_pre_approval(NetUtils.getRequestParams(FeeCashierDeskActivity.this, map), NetUtils.getSign(NetUtils.getRequestParams(FeeCashierDeskActivity.this, map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(FeeCashierDeskActivity.this, true, null) {
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
                                Intent intent = new Intent(FeeCashierDeskActivity.this, AutonymSuccActivity.class);
                                intent.putExtras(bundle);
                                FeeCashierDeskActivity.this.startActivity(intent);
                            } else if (approvalResponse.status.equals("Processing")) {
                                (FeeCashierDeskActivity.this).startActivity(new Intent((FeeCashierDeskActivity.this), ProcessingActivity.class));
                            } else if (approvalResponse.status.equals("Error")) {
                                (FeeCashierDeskActivity.this).startActivity(new Intent((FeeCashierDeskActivity.this), ErrorActivity.class));
                            } else if (approvalResponse.status.equals("AccountNotExist")) {
                                //进入预售信息补全界面
                                Bundle bundle = new Bundle();
                                bundle.putString("name", name);
                                bundle.putString("idCard", idCard);
                                bundle.putFloat("amount", amount);
                                Intent intent = new Intent(FeeCashierDeskActivity.this, AutonymSuccActivity.class);
                                intent.putExtras(bundle);
                                FeeCashierDeskActivity.this.startActivity(intent);
                            } else if (approvalResponse.status.equals("RealNameAuthError")) {
                                //实名认证失败
                                (FeeCashierDeskActivity.this).startActivity(new Intent((FeeCashierDeskActivity.this), ErrorActivity.class));
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

