package com.zhihuianxin.xyaxf.app.payment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zhihuianxin.xyaxf.app.AppConstant;
import com.axinfu.modellib.service.EcardService;
import com.axinfu.modellib.service.FeeService;
import com.axinfu.modellib.service.PaymentService;
import com.axinfu.modellib.thrift.base.BaseResponse;
import com.axinfu.modellib.thrift.base.PayMethod;
import com.axinfu.modellib.thrift.customer.Customer;
import com.axinfu.modellib.thrift.fee.Fee;
import com.axinfu.modellib.thrift.fee.SchoolRoll;
import com.axinfu.modellib.thrift.fee.SubFee;
import com.axinfu.modellib.thrift.fee.SubFeeItem;
import com.axinfu.modellib.thrift.payment.PaymentOrder;
import com.google.gson.Gson;
import com.xyaxf.axpay.modle.PayECardRequestData;
import com.xyaxf.axpay.modle.PayFeeExtRequest;
import com.xyaxf.axpay.modle.PayFeeRequestData;
import com.xyaxf.axpay.modle.PayFor;
import com.xyaxf.axpay.modle.PayRequest;
import com.xyaxf.axpay.modle.PaymentInfo;
import com.zhihuianxin.xyaxf.app.base.axutil.Util;
import com.zhihuianxin.secure.Secure;
import com.zhihuianxin.xyaxf.App;
import com.zhihuianxin.xyaxf.BaseRealmActionBarActivity;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.ApiFactory;
import com.zhihuianxin.xyaxf.app.BaseSubscriber;
import com.zhihuianxin.xyaxf.app.RetrofitFactory;
import com.zhihuianxin.xyaxf.app.ecard.Ecardpresenter;
import com.zhihuianxin.xyaxf.app.me.view.activity.MeHelpCenterActivity;
import com.zhihuianxin.xyaxf.app.ocp.OcpPayFixedDeskActivity;
import com.zhihuianxin.xyaxf.app.utils.FullyLinearLayoutManager;
import com.zhihuianxin.xyaxf.app.utils.NetUtils;
import com.zhihuianxin.xyaxf.app.utils.RxBus;
import com.zhihuianxin.xyaxf.app.view.LoadingDialog;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.realm.AccountVerifyItemRealmProxy;
import io.realm.CustomerRealmProxy;
import io.realm.ECardAccountRealmProxy;
import io.realm.FeeAccountRealmProxy;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.SchoolRollRealmProxy;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static com.zhihuianxin.xyaxf.app.me.view.activity.AddBankCardActivity.bytesToHexString;

/**
 * Created by zcrpro on 2016/11/12.
 */
public class CashierDeskActivity extends BaseRealmActionBarActivity {
    public static final int REQUEST_GR_PAY = 1000;
    public static final String EXTRA_APK_DIR = "axin_apk";
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

    private AlertDialog dialog;

    @InjectView(R.id.payment_total)
    TextView paymentTotal;
    @InjectView(R.id.tv_name)
    TextView tvName;
    @InjectView(R.id.ll_name)
    LinearLayout llName;
    @InjectView(R.id.ll_ecard_account)
    LinearLayout llEcardAccount;
    @InjectView(R.id.tv_id_card)
    TextView tvIdCard;
    @InjectView(R.id.ll_id_card)
    LinearLayout llIdCard;
    @InjectView(R.id.tv_other_info)
    TextView tvOtherInfo;
    @InjectView(R.id.ll_other_info)
    LinearLayout llOtherInfo;
    @InjectView(R.id.tv_fee_title)
    TextView tvFeeTitle;
    @InjectView(R.id.recyclerview)
    RecyclerView recyclerview;
    @InjectView(R.id.ll_fee_detail)
    LinearLayout llFeeDetail;
    @InjectView(R.id.tv_ecard_account)
    TextView tv_ecard_account;
    @InjectView(R.id.tv_ecard_rechage)
    TextView tv_ecard_rechage;
    @InjectView(R.id.rc_way)
    RecyclerView rcWay;
    @InjectView(R.id.cashier_current_number)
    TextView mCurrentNumberText;
    @InjectView(R.id.btn_ok)
    Button btn_ok;
    @InjectView(R.id.tv_decu)
    TextView tvDecu;
    @InjectView(R.id.ll_decu)
    LinearLayout llDecu;
    @InjectView(R.id.tv_pay_way)
    TextView tvPayWay;
    @InjectView(R.id.ll_pay_way_line)
    View llPayWayLine;
    @InjectView(R.id.tv_xueji)
    TextView tvXueji;
    @InjectView(R.id.ll_xueji)
    LinearLayout llXueji;

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

    private LoadingDialog loadingDialog;
    private List<PayFeeExtRequest> payFeeExtRequests;
    private PayFeeExtRequest payFeeExtRequest;
    private CashierWayAdapter cashierWayAdapter;

    private Subscription rxSbscription;

    private float decu;

    /**
     * 微信支付用
     *
     * @return
     */

    public static PayRequest payRequest;
    public static CashierWayAdapter.PayOderResponse payOderResponse;

    @Override
    protected int getContentViewId() {
        return R.layout.cashier_desk_activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);


        tvPayWay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CashierDeskActivity.this, MeHelpCenterActivity.class));
            }
        });

        payFeeExtRequests = new ArrayList<>();

        loadingDialog = new LoadingDialog(this);
        loadingDialog.show();

        FullyLinearLayoutManager mLayoutManager = new FullyLinearLayoutManager(this);
        FullyLinearLayoutManager mLayoutManager2 = new FullyLinearLayoutManager(this);
        recyclerview.setLayoutManager(mLayoutManager);
        rcWay.setLayoutManager(mLayoutManager2);

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
                    ;
                }
            }

            channel_codes = (List<PayMethod>) bundle.getSerializable(FEE_WAY);
        }

        //查询customer中的数据
        final RealmResults<Customer> customers = realm.where(Customer.class).equalTo("mobile", App.mAxLoginSp.getUserMobil()).findAll();
        new Handler().post(new Runnable() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                loadingDialog.dismiss();
                if (customers.size() <= 0) {
                    return;
                }
                if (from.equals(FEE)) {
                    tvName.setText(((FeeAccountRealmProxy) (((CustomerRealmProxy) customers.get(0)).realmGet$fee_account())).realmGet$name());
                    String idStr = ((FeeAccountRealmProxy) (((CustomerRealmProxy) customers.get(0)).realmGet$fee_account())).realmGet$id_card_no();
                    if (idStr != null) {
                        tvIdCard.setText(getIDValue(idStr));
                        llIdCard.setVisibility(View.VISIBLE);
                    } else {
                        tvIdCard.setText("无");
                        llIdCard.setVisibility(View.GONE);
                    }

                    if (!Util.isEmpty(App.mAxLoginSp.getOtherFeeNo())) {
                        mCurrentNumberText.setText("缴费账号：");
                        tvOtherInfo.setText(App.mAxLoginSp.getOtherFeeNo());
                    } else if (((FeeAccountRealmProxy) (((CustomerRealmProxy) customers.get(0)).realmGet$fee_account())).realmGet$other_no() != null) {
                        if (((AccountVerifyItemRealmProxy) ((FeeAccountRealmProxy) (((CustomerRealmProxy) customers.get(0)).realmGet$fee_account())).realmGet$new_stu_verify_config()).realmGet$title() != null) {
                            mCurrentNumberText.setText(((AccountVerifyItemRealmProxy) ((FeeAccountRealmProxy) (((CustomerRealmProxy) customers.get(0)).realmGet$fee_account())).realmGet$new_stu_verify_config()).realmGet$title() + ": ");
                        } else {
                            mCurrentNumberText.setText("新生缴费: ");
                        }
                        tvOtherInfo.setText(((FeeAccountRealmProxy) (((CustomerRealmProxy) customers.get(0)).realmGet$fee_account())).realmGet$other_no());
                    } else {
                        if (TextUtils.isEmpty(((FeeAccountRealmProxy) (((CustomerRealmProxy) customers.get(0)).realmGet$fee_account())).realmGet$student_no())) {
                            mCurrentNumberText.setText("学号：");
                            tvOtherInfo.setText("无");
                        } else {
                            mCurrentNumberText.setText("学号：");
                            tvOtherInfo.setText(((FeeAccountRealmProxy) (((CustomerRealmProxy) customers.get(0)).realmGet$fee_account())).realmGet$student_no());
                        }
                    }

                    if (amout != null) {
                        if (decu == Float.parseFloat(amout)) {
                            paymentTotal.setText("0元");
                        } else {
                            paymentTotal.setText(amout + "元");
                        }
                    }
                } else if (from.equals(ECARD)) {
                    tvName.setText(((ECardAccountRealmProxy) (((CustomerRealmProxy) customers.get(0)).realmGet$ecard_account())).realmGet$name());
                    llIdCard.setVisibility(View.GONE);
                    llOtherInfo.setVisibility(View.GONE);
                    llEcardAccount.setVisibility(View.VISIBLE);
                    if (((ECardAccountRealmProxy) (((CustomerRealmProxy) customers.get(0)).realmGet$ecard_account())).realmGet$account_no() != null)
                        tv_ecard_account.setText(((ECardAccountRealmProxy) (((CustomerRealmProxy) customers.get(0)).realmGet$ecard_account())).realmGet$account_no());
                    if (amout != null) {
                        if (decu == Float.parseFloat(amout)) {
                            paymentTotal.setText("0元");
                        } else {
                            paymentTotal.setText(amout + "元");
                        }
                    }
                    tvFeeTitle.setText("一卡通充值");
                    tv_ecard_rechage.setVisibility(View.VISIBLE);
                    tv_ecard_rechage.setText("一卡通充值:" + amout + "元");
                }

                final RealmResults<SchoolRoll> schoolRolls = realm.where(SchoolRoll.class).equalTo("mobile", App.mAxLoginSp.getUserMobil())
                        .findAll();

                if (schoolRolls.size() > 0) {
                    tvXueji.setVisibility(View.VISIBLE);
                    llXueji.setVisibility(View.VISIBLE);
                    if (((SchoolRollRealmProxy) schoolRolls.get(0)).realmGet$district() != null) {
                        tvXueji.setText(((SchoolRollRealmProxy) schoolRolls.get(0)).realmGet$district() + ((SchoolRollRealmProxy) schoolRolls.get(0)).realmGet$academy() + ((SchoolRollRealmProxy) schoolRolls.get(0)).realmGet$major() +
                                ((SchoolRollRealmProxy) schoolRolls.get(0)).realmGet$grade() + ((SchoolRollRealmProxy) schoolRolls.get(0)).realmGet$clazz());
                    } else {
                        if (((SchoolRollRealmProxy) schoolRolls.get(0)).realmGet$academy() == null || ((SchoolRollRealmProxy) schoolRolls.get(0)).realmGet$major() == null ||
                                ((SchoolRollRealmProxy) schoolRolls.get(0)).realmGet$grade() == null || ((SchoolRollRealmProxy) schoolRolls.get(0)).realmGet$clazz() == null) {
                            tvXueji.setVisibility(View.GONE);
                            llXueji.setVisibility(View.GONE);
                        }
                        tvXueji.setText(((SchoolRollRealmProxy) schoolRolls.get(0)).realmGet$academy() + ((SchoolRollRealmProxy) schoolRolls.get(0)).realmGet$major() +
                                ((SchoolRollRealmProxy) schoolRolls.get(0)).realmGet$grade() + ((SchoolRollRealmProxy) schoolRolls.get(0)).realmGet$clazz());
                    }
                } else {
                    tvXueji.setVisibility(View.GONE);
                    llXueji.setVisibility(View.GONE);
                }

            }
        });

        if (fee_title != null)
            tvFeeTitle.setText(fee_title);

//        if (fee_normals != null) {
//            CashierDeskFeeNormalAdapter cashierDeskFeeAdapter = new CashierDeskFeeNormalAdapter(CashierDeskActivity.this, fee_normals, R.layout.cashier_desk_fee_item);
//            recyclerview.setAdapter(cashierDeskFeeAdapter);
//            cashierDeskFeeAdapter.notifyDataSetChanged();
//        }
//
//        if (fee_packs != null) {
//            CashierDeskFeeNormalAdapter cashierDeskFeeAdapter = new CashierDeskFeeNormalAdapter(CashierDeskActivity.this, fee_packs, R.layout.cashier_desk_fee_item);
//            recyclerview.setAdapter(cashierDeskFeeAdapter);
//            cashierDeskFeeAdapter.notifyDataSetChanged();
//        }
//
//        if (fee_singles != null) {
//            CashierDeskFeeNormalAdapter cashierDeskFeeAdapter = new CashierDeskFeeNormalAdapter(CashierDeskActivity.this, fee_singles, R.layout.cashier_desk_fee_item);
//            recyclerview.setAdapter(cashierDeskFeeAdapter);
//            cashierDeskFeeAdapter.notifyDataSetChanged();
//        }
//
//        if (fee_fu != null) {
//            CashierDeskFeeNormalAdapter cashierDeskFeeAdapter = new CashierDeskFeeNormalAdapter(CashierDeskActivity.this, fee_fu, R.layout.cashier_desk_fee_item);
//            recyclerview.setAdapter(cashierDeskFeeAdapter);
//            cashierDeskFeeAdapter.notifyDataSetChanged();
//        }

        if (Math.abs(decu) > 0) {
            String s = null;
            //除此之外还添加app中的抵扣项目
            if (App.subFeeDeductionHashMap.size() > 0) {
                Iterator iter = App.subFeeDeductionHashMap.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    Object value = entry.getValue();
                    if (busss_type != null) {
                        if (busss_type.equals(((SubFeeItem) value).business_channel) && ((SubFeeItem) value).isSelect) {
                            if (s != null) {
                                s = s + "\n" + ((SubFeeItem) value).title + ":" + ((SubFeeItem) value).amount;
                            } else {
                                s = ((SubFeeItem) value).title + ":" + ((SubFeeItem) value).amount;
                            }
                        }
                    }
                }
                tvDecu.setText(s);
                tvPayWay.setVisibility(View.VISIBLE);
                llDecu.setVisibility(View.VISIBLE);
                llPayWayLine.setVisibility(View.VISIBLE);
            }

            if (Float.parseFloat(amout) == 0) {
                tvPayWay.setVisibility(View.GONE);
                llPayWayLine.setVisibility(View.GONE);
            }

        } else {
            tvPayWay.setVisibility(View.VISIBLE);
            llDecu.setVisibility(View.GONE);
            llPayWayLine.setVisibility(View.VISIBLE);
        }


        rxSbscription = RxBus.getDefault().toObservable().subscribe(new Action1<Object>() {
            @Override
            public void call(Object event) {
                if (event.equals("succeed")) {
                    RxBus.getDefault().send("fee_succeed");
                    finish();
                } else if (event.equals("onPayCancelled")) {
                    com.xyaxf.axpay.Util.myShowToast(CashierDeskActivity.this, "取消支付", 300);
                } else if (event.equals("wechat_suc")) {
                    PaymentInfo mPaymentInfo = new PaymentInfo();
                    mPaymentInfo.pay_method = payRequest.pay_method;
                    mPaymentInfo.payment_amt = payOderResponse.order.payment_amt;
                    mPaymentInfo.channel_orderno = payOderResponse.order.order_no;
                    mPaymentInfo.pay_info = payOderResponse.order.pay_data;
                    mPaymentInfo.fee_name = payOderResponse.order.trade_summary + "-支付成功";
                    mPaymentInfo.fee_time = payOderResponse.order.order_time;
                    Intent intent = new Intent(CashierDeskActivity.this, PaymentStatusActivity.class);
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
//                    if (App.formCasher.equals(ECARD)) {
//                        loadEcardData();
//                    } else if ((App.formCasher.equals(FEE))) {
//                        //获取缴费详情
//                        loadFee();
//                    }
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
    }

    private void loadFee() {
        payFeeExtRequests.clear();
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String, Object> map = new HashMap<>();
        map.put("fee_id", FeeId);
        FeeService feeserice = ApiFactory.getFactory().create(FeeService.class);
        feeserice.get_fee_by_id(NetUtils.getRequestParams(CashierDeskActivity.this, map), NetUtils.getSign(NetUtils.getRequestParams(CashierDeskActivity.this, map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(CashierDeskActivity.this, true, null) {
                    @Override
                    public void onNext(Object o) {
                        ISResponse isResponse = new Gson().fromJson(o.toString(), ISResponse.class);
                        for (int i = 0; i < payfees.size(); i++) {
                            for (Iterator iter = payfees.get(i).entrySet().iterator(); iter.hasNext(); ) {
                                Map.Entry element = (Map.Entry) iter.next();
                                Object strKey = element.getKey();
                                Object strValue = element.getValue();
                                System.out.println(strKey + "/" + strValue);
                                String[] sourceStrArray = strValue.toString().split(":");
//                for (int j = 0; j < sourceStrArray.length; j++) {
                                payFeeExtRequest = new PayFeeExtRequest();
                                payFeeExtRequest.id = (String) element.getKey();
                                payFeeExtRequest.amount = sourceStrArray[0];
                                payFeeExtRequest.pay_amount = sourceStrArray[1];
                                payFeeExtRequest.title = sourceStrArray[2];
                                payFeeExtRequests.add(payFeeExtRequest);
                                num = num + Float.parseFloat(sourceStrArray[1]);
//                }
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
                        final PayFeeRequestData payFeeRequestData = new PayFeeRequestData();
                        payFeeRequestData.id = FeeId;
                        payFeeRequestData.exts = payFeeExtRequests;
                        if (fee_title != null)
                            payFeeRequestData.title = fee_title;
                        if (0 == Float.parseFloat(amout)) {
                            rcWay.setVisibility(View.GONE);
                            btn_ok.setVisibility(View.VISIBLE);
                        } else {
                            rcWay.setVisibility(View.VISIBLE);
                            btn_ok.setVisibility(View.GONE);
                            cashierWayAdapter = new CashierWayAdapter(CashierDeskActivity.this, num, payFeeRequestData, isResponse.fee_details.pay_methods, R.layout.cashier_way_item);
                            rcWay.setAdapter(cashierWayAdapter);
                            cashierWayAdapter.notifyDataSetChanged();
                        }

                        btn_ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                PayRequest payRequest = new PayRequest();
                                payRequest.pay_for = PayFor.SchoolFee;
                                payRequest.amount = amout;
                                payRequest.pay_method = new PayMethod();
                                payRequest.request_data = payFeeRequestData;
                                pay(payRequest);
                            }
                        });


                        Log.d("CashierDeskActivity", new Gson().toJson(payFeeRequestData.exts));
                    }
                });
    }

    private String getIDValue(String oriID) {
        if (oriID != null) {
            if (oriID.length() != 18) {
                return oriID;
            } else {
                return oriID.substring(0, 2) + "**************" + oriID.substring(16);
            }
        } else {
            return null;
        }

    }

    float num;

    private void initEcardData() {
        //装载ecard的支付请求
        PayECardRequestData payECardRequestData = new PayECardRequestData();
        payECardRequestData.amount = amout;
        cashierWayAdapter = new CashierWayAdapter(this, Float.parseFloat(amout), payECardRequestData, channel_codes, R.layout.cashier_way_item);
        rcWay.setAdapter(cashierWayAdapter);
        cashierWayAdapter.notifyDataSetChanged();
    }

    private void initData() {
        for (int i = 0; i < payfees.size(); i++) {
            for (Iterator iter = payfees.get(i).entrySet().iterator(); iter.hasNext(); ) {
                Map.Entry element = (Map.Entry) iter.next();
                Object strKey = element.getKey();
                Object strValue = element.getValue();
                System.out.println(strKey + "/" + strValue);
                String[] sourceStrArray = strValue.toString().split(":");
//                for (int j = 0; j < sourceStrArray.length; j++) {
                payFeeExtRequest = new PayFeeExtRequest();
                payFeeExtRequest.id = (String) element.getKey();
                payFeeExtRequest.amount = sourceStrArray[0];
                payFeeExtRequest.pay_amount = sourceStrArray[1];
                payFeeExtRequest.title = sourceStrArray[2];
                payFeeExtRequests.add(payFeeExtRequest);
                num = num + Float.parseFloat(sourceStrArray[1]);
//                }
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
        final PayFeeRequestData payFeeRequestData = new PayFeeRequestData();
        payFeeRequestData.id = FeeId;
        payFeeRequestData.exts = payFeeExtRequests;
        if (fee_title != null)
            payFeeRequestData.title = fee_title;
        if (0 == Float.parseFloat(amout)) {
            rcWay.setVisibility(View.GONE);
            btn_ok.setVisibility(View.VISIBLE);
        } else {
            rcWay.setVisibility(View.VISIBLE);
            btn_ok.setVisibility(View.GONE);
            cashierWayAdapter = new CashierWayAdapter(this, num, payFeeRequestData, channel_codes, R.layout.cashier_way_item);
            rcWay.setAdapter(cashierWayAdapter);
            cashierWayAdapter.notifyDataSetChanged();
        }

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PayRequest payRequest = new PayRequest();
                payRequest.pay_for = PayFor.SchoolFee;
                payRequest.amount = amout;
                payRequest.pay_method = new PayMethod();
                payRequest.request_data = payFeeRequestData;
                pay(payRequest);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (data != null && data.getExtras() != null && requestCode == REQUEST_GR_PAY) {
                cashierWayAdapter.gysdkReturn(data.getExtras().getString("tn"));
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (App.formCasher.equals(ECARD)) {
            loadEcardData();
        } else if ((App.formCasher.equals(FEE))) {
            //获取缴费详情
            loadFee();
        }

    }

    //刚好完成抵扣的支付下单
    private void pay(final PayRequest payRequest) {
        final LoadingDialog loadingDialog2 = new LoadingDialog(this);
        loadingDialog2.show();
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String, Object> map = new HashMap<>();
        payRequest.pay_method.channel = "NoNeed";
        map.put("pay_req", payRequest);
        PaymentService paymentService = ApiFactory.getFactory().create(PaymentService.class);
        paymentService.payOder(NetUtils.getRequestParams(CashierDeskActivity.this, map), NetUtils.getSign(NetUtils.getRequestParams(CashierDeskActivity.this, map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Object>() {
                    @Override
                    public void onCompleted() {
                        if (loadingDialog2 != null)
                            loadingDialog2.dismiss();
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (loadingDialog2 != null)
                            loadingDialog2.dismiss();
                        Toast.makeText(CashierDeskActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(Object o) {
                        PayOderResponse payOderResponse = new Gson().fromJson(o.toString(), PayOderResponse.class);
                        //获取到paymentoder
                        if (payOderResponse.resp.resp_code.equals(AppConstant.SUCCESS)) {
                            //成功
                            Intent intent = new Intent(CashierDeskActivity.this, PaymentStatusActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString(PaymentStatusActivity.AMOUNT, payOderResponse.order.payment_amt);
                            bundle.putString(PaymentStatusActivity.NAME, payOderResponse.order.trade_summary);
                            bundle.putSerializable(PaymentStatusActivity.WAY, payOderResponse.order.pay_method);
                            bundle.putString(PaymentStatusActivity.TIME, payOderResponse.order.order_time);
                            bundle.putString(PaymentStatusActivity.ODER, payOderResponse.order.order_no);
                            intent.putExtras(bundle);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(CashierDeskActivity.this, payOderResponse.resp.resp_desc, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public static class PayOderResponse extends RealmObject {
        public BaseResponse resp;
        public PaymentOrder order;
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

    public void loadEcardData() {
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String, Object> map = new HashMap<>();
        EcardService ecardService = ApiFactory.getFactory().create(EcardService.class);
        ecardService.getEcard(NetUtils.getRequestParams(CashierDeskActivity.this, map), NetUtils.getSign(NetUtils.getRequestParams(CashierDeskActivity.this, map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(CashierDeskActivity.this, true, null) {
                    @Override
                    public void onNext(Object o) {
                        Ecardpresenter.EcardResponse ecardResponse = new Gson().fromJson(o.toString(), Ecardpresenter.EcardResponse.class);
                        PayECardRequestData payECardRequestData = new PayECardRequestData();
                        payECardRequestData.amount = amout;
                        cashierWayAdapter = new CashierWayAdapter(CashierDeskActivity.this, Float.parseFloat(amout), payECardRequestData, ecardResponse.ecard.pay_methods, R.layout.cashier_way_item);
                        rcWay.setAdapter(cashierWayAdapter);
                        cashierWayAdapter.notifyDataSetChanged();
                    }
                });

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
                .subscribe(new BaseSubscriber<Object>(CashierDeskActivity.this, true, null) {

                    @Override
                    public void onNext(Object o) {
                        OcpPayFixedDeskActivity.PaymentRecordResponse recordResponse = new Gson().fromJson(o.toString(), OcpPayFixedDeskActivity.PaymentRecordResponse.class);
                        if (recordResponse.payment_result.order_status.equals("paied")) {
                            //成功
                            Intent intent = new Intent(CashierDeskActivity.this, PaymentStatusActivity.class);
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
                                dialog = new AlertDialog.Builder(CashierDeskActivity.this)
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
                                                Toast.makeText(CashierDeskActivity.this, "你可以在“我的-交易记录”下查看支付状态", Toast.LENGTH_LONG).show();
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
        paymentService.payOder(NetUtils.getRequestParams(CashierDeskActivity.this, map), NetUtils.getSign(NetUtils.getRequestParams(CashierDeskActivity.this, map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(CashierDeskActivity.this, true, null) {

                    @Override
                    public void onNext(Object o) {
                        CashierWayAdapter.PayOderResponse payOderResponse = new Gson().fromJson(o.toString(), CashierWayAdapter.PayOderResponse.class);
                        //获取到paymentoder
                        if (payOderResponse.resp.resp_code.equals(AppConstant.SUCCESS)) {
                            if (payRequest.pay_method.channel.equals("UPTokenPay") && payRequest.pay_method.purpose == null) {
                                Intent intent = new Intent(CashierDeskActivity.this, PaymentStatusActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putString(PaymentStatusActivity.AMOUNT, payOderResponse.order.payment_amt);
                                bundle.putString(PaymentStatusActivity.NAME, payOderResponse.order.trade_summary);
                                bundle.putSerializable(PaymentStatusActivity.WAY, payOderResponse.order.pay_method);
                                bundle.putString(PaymentStatusActivity.TIME, payOderResponse.order.order_time);
                                bundle.putString(PaymentStatusActivity.ODER, payOderResponse.order.order_no);
                                intent.putExtras(bundle);
                                CashierDeskActivity.this.startActivity(intent);
                                ((Activity) CashierDeskActivity.this).finish();
                            }
                        }

                    }
                });
    }
}

