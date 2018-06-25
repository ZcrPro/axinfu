package com.zhihuianxin.xyaxf.app.ocp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.ActionMode;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.zhihuianxin.xyaxf.app.AppConstant;
import com.axinfu.modellib.service.AxfQRPayService;
import com.axinfu.modellib.service.PaymentService;
import com.axinfu.modellib.thrift.base.BaseResponse;
import com.axinfu.modellib.thrift.base.PayMethod;
import com.axinfu.modellib.thrift.fee.PaymentRecord;
import com.axinfu.modellib.thrift.ocp.OcpAccount;
import com.axinfu.modellib.thrift.payment.PaymentOrder;
import com.axinfu.modellib.thrift.unqr.PaymentConfig;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.xyaxf.axpay.modle.PayFor;
import com.xyaxf.axpay.modle.PayRequest;
import com.xyaxf.axpay.modle.PaymentInfo;
import com.zhihuianxin.axutil.Util;
import com.zhihuianxin.secure.Secure;
import com.zhihuianxin.xyaxf.App;
import com.zhihuianxin.xyaxf.BaseRealmActionBarActivity;
import com.zhihuianxin.xyaxf.MainActivity;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.ApiException;
import com.zhihuianxin.xyaxf.app.ApiFactory;
import com.zhihuianxin.xyaxf.app.BaseSubscriber;
import com.zhihuianxin.xyaxf.app.RetrofitFactory;
import com.zhihuianxin.xyaxf.app.me.view.activity.MePayListNewActivity;
import com.zhihuianxin.xyaxf.app.ocp.adapter.OcpPayWayDefAdapter;
import com.zhihuianxin.xyaxf.app.ocp.view.PasswordWindow;
import com.zhihuianxin.xyaxf.app.pay.MyPayActivity;
import com.zhihuianxin.xyaxf.app.unionqr_pay.view.activity.UnionHtmlActivity;
import com.zhihuianxin.xyaxf.app.unionqr_pay.view.activity.UnionInputPayPwdActivity;
import com.zhihuianxin.xyaxf.app.unionqr_pay.view.activity.UnionSetPayPwdActivity;
import com.zhihuianxin.xyaxf.app.unionqr_pay.view.activity.UnionSweptEmptyCardActivity;
import com.zhihuianxin.xyaxf.app.utils.NetUtils;
import com.zhihuianxin.xyaxf.app.utils.RxBus;
import com.zhihuianxin.xyaxf.app.view.KeyBoardPwdPointView;
import com.zhihuianxin.xyaxf.app.view.LoadingDialog;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.realm.RealmObject;
import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static com.zhihuianxin.xyaxf.app.me.view.activity.AddBankCardActivity.bytesToHexString;

/**
 * Created by zcrpro on 2017/11/17.
 */

public class OcpPayFixedDeskActivity extends BaseRealmActionBarActivity implements KeyBoardPwdPointView.OnNumberClickListener {


    public static final String FIXED = "fixed";
    @InjectView(R.id.tv_shop_name)
    TextView tvShopName;
    @InjectView(R.id.tv_fixed_amount)
    TextView tvFixedAmount;
    @InjectView(R.id.ed_amount)
    EditText edAmount;
    @InjectView(R.id.ll_not_fiexd)
    LinearLayout llNotFiexd;
    @InjectView(R.id.recyclerview)
    RecyclerView recyclerview;
    @InjectView(R.id.tv_mark)
    EditText tvMark;
    @InjectView(R.id.next)
    Button next;
    @InjectView(R.id.am_nkv_keyboard)
    KeyBoardPwdPointView amNkvKeyboard;
    @InjectView(R.id.tv_mark_value)
    TextView tvMarkValue;
    @InjectView(R.id.mark_det)
    TextView markDet;
    @InjectView(R.id.rl_marked)
    LinearLayout rlMarked;
    @InjectView(R.id.ll_fix)
    LinearLayout llFix;
    @InjectView(R.id.rl)
    RelativeLayout rl;
    @InjectView(R.id.auth_hint)
    TextView authHint;

    private QrResultActivity.PayInfo payInfo;
    private OcpPayWayDefAdapter ocpPayWayDefAdapter;

    private String markData;
    private QrResultActivity.PayMethod payMethod;
    private String amount = "";
    private String qrId;
    private boolean isFixed = false;

    private static final int SDK_PAY_FLAG = 1;

    private PaymentOrder paymentOrder;

    private PaymentOrder paymentOrder2;
    private String order;

    private AlertDialog dialog;

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
                        //取消全局的正在支付tag
                        App.isNeedCheck = false;
                        //支付成功
                        Log.d("OcpPayFixedDeskActivity", "支付宝跳转成功页面");
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("PaymentOrder", paymentOrder);
                        bundle.putSerializable("PayInfo", payInfo);
                        Intent intent = new Intent(OcpPayFixedDeskActivity.this, OcpPaySucActivity.class);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        finish();
                    } else {
                        App.isNeedCheck = false;
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        Toast.makeText(OcpPayFixedDeskActivity.this, "支付取消", Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                default:
                    break;
            }
        }
    };

    @Override
    protected int getContentViewId() {
        return R.layout.ocp_pay_desk_fixed;
    }

    private Subscription rxSubscription;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);

        rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                amNkvKeyboard.setVisibility(View.GONE);
            }
        });

        hintKbTwo();

        llNotFiexd.setFocusable(false);
        llNotFiexd.setFocusableInTouchMode(false);

        edAmount.setEnabled(true);
        edAmount.setFocusableInTouchMode(true);
        edAmount.setFocusable(true);

        hintKbTwo();

        edAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hintKbTwo();
                amNkvKeyboard.setVisibility(View.VISIBLE);
            }
        });

        tvMark.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    amNkvKeyboard.setVisibility(View.GONE);
                } else {
                    hintKbTwo();
                    amNkvKeyboard.setVisibility(View.VISIBLE);
                }
            }
        });

        edAmount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    amNkvKeyboard.setVisibility(View.VISIBLE);
                    hintKbTwo();
                } else {
                    amNkvKeyboard.setVisibility(View.GONE);
                }
            }
        });

        rxSubscription = RxBus.getDefault().toObservable().subscribe(new Action1<Object>() {
            @Override
            public void call(Object event) {
                if (event.equals("wechat_suc")) {
                    Log.d("OcpPayFixedDeskActivity", "微信支付结果");
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("PaymentOrder", paymentOrder);
                    bundle.putSerializable("PayInfo", payInfo);
                    Intent intent = new Intent(OcpPayFixedDeskActivity.this, OcpPaySucActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish();
                } else if (event.equals("succeed")) {
                    finish();
                } else if (event.equals("open")) {
                    next.setText("确认付款");
                    acquiring(qrId);
                } else if (event.equals("ag")) {
                    //如果同意去判断是否有支付密码
                    JudgePayPwd();
                } else if (event.equals("fixed_activity_add_bank")) {
                    //重新加载支付方式
                    acquiring2(qrId);
                } else if (event.equals("fixed_activity_add_bank_def")) {
                    //重新加载支付方式
                    acquiring(qrId);
                } else if (event.equals("no_add_card_and_pay")) {
                    //去支付
                    hintKbTwo();
                    if (!isFixed) {
                        if (TextUtils.isEmpty(edAmount.getText().toString().trim()) || Float.parseFloat(edAmount.getText().toString().trim()) == 0) {
                            Toast.makeText(OcpPayFixedDeskActivity.this, "请输入支付金额", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        try {
                            Float.parseFloat(edAmount.getText().toString().trim());
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                            Toast.makeText(OcpPayFixedDeskActivity.this, "请输入支付金额", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    //获取新的计价策略
                    if (isFixed) {
                        amount = Float.parseFloat(payInfo.payee_info.amounts.get(0).amount) + "";
                    } else {
                        amount = Float.parseFloat(edAmount.getText().toString().trim()) + "";
                    }

                    if (payInfo.has_strategy) {
                        getPricingStrategy2(amount, payMethod.channel, qrId);
                    } else {
                        try {
                            SQLite.delete()
                                    .from(PricingStrategy.class)
                                    .query();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        final AxfQRPayRequestData axfQRPayRequestData = new AxfQRPayRequestData();
                        if (isFixed) {
                            axfQRPayRequestData.orig_amt = payInfo.payee_info.amounts.get(0).amount;
                            axfQRPayRequestData.amount = payInfo.payee_info.amounts.get(0).amount;
                        } else {
                            axfQRPayRequestData.orig_amt = edAmount.getText().toString().trim();
                            axfQRPayRequestData.amount = edAmount.getText().toString().trim();
                        }

                        final PayRequest payRequest = new PayRequest();
                        if (isFixed) {
                            payRequest.amount = Float.parseFloat(payInfo.payee_info.amounts.get(0).amount) + "";
                        } else {
                            payRequest.amount = Float.parseFloat(edAmount.getText().toString().trim()) + "";
                        }
                        axfQRPayRequestData.merchant_name = payInfo.payee_info.merchant_name;
                        axfQRPayRequestData.shop_name = payInfo.payee_info.shop_name;
                        axfQRPayRequestData.merchant_name = payInfo.payee_info.merchant_name;
                        axfQRPayRequestData.shop_name = payInfo.payee_info.shop_name;
                        goPay(axfQRPayRequestData, payRequest);
                    }
                }
            }
        });

        edAmount.setLongClickable(false);
        edAmount.setTextIsSelectable(false);
        // 取消横屏复制粘贴
        edAmount.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        // 取消复制粘贴
        edAmount.setCustomSelectionActionModeCallback(new ActionMode.Callback() {

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }
        });

        recyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true));
        recyclerview.setHasFixedSize(true);

        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        if (bundle.getSerializable("payinfo") != null) {
            payInfo = (QrResultActivity.PayInfo) bundle.getSerializable("payinfo");
        }

        if (bundle.getString("qr") != null) {
            qrId = bundle.getString("qr");
        }

        isFixed = bundle.getBoolean(FIXED);

        initPayMethod(payInfo);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hintKbTwo();
                if (!isFixed) {
                    if (TextUtils.isEmpty(edAmount.getText().toString().trim()) || Float.parseFloat(edAmount.getText().toString().trim()) == 0) {
                        Toast.makeText(OcpPayFixedDeskActivity.this, "请输入支付金额", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try {
                        Float.parseFloat(edAmount.getText().toString().trim());
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        Toast.makeText(OcpPayFixedDeskActivity.this, "请输入支付金额", Toast.LENGTH_SHORT).show();
                        return;
                    }

                }
                //获取新的计价策略

                if (isFixed) {
                    amount = Float.parseFloat(payInfo.payee_info.amounts.get(0).amount) + "";
                } else {
                    amount = Float.parseFloat(edAmount.getText().toString().trim()) + "";
                }

                if (payInfo.has_strategy) {
                    getPricingStrategy(amount, payMethod.channel, qrId);
                } else {
                    try {
                        SQLite.delete()
                                .from(PricingStrategy.class)
                                .query();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    final AxfQRPayRequestData axfQRPayRequestData = new AxfQRPayRequestData();
                    if (isFixed) {
                        axfQRPayRequestData.orig_amt = payInfo.payee_info.amounts.get(0).amount;
                        axfQRPayRequestData.amount = payInfo.payee_info.amounts.get(0).amount;
                    } else {
                        axfQRPayRequestData.orig_amt = edAmount.getText().toString().trim();
                        axfQRPayRequestData.amount = edAmount.getText().toString().trim();
                    }

                    final PayRequest payRequest = new PayRequest();
                    if (isFixed) {
                        payRequest.amount = Float.parseFloat(payInfo.payee_info.amounts.get(0).amount) + "";
                    } else {
                        payRequest.amount = Float.parseFloat(edAmount.getText().toString().trim()) + "";
                    }
                    axfQRPayRequestData.merchant_name = payInfo.payee_info.merchant_name;
                    axfQRPayRequestData.shop_name = payInfo.payee_info.shop_name;
                    axfQRPayRequestData.merchant_name = payInfo.payee_info.merchant_name;
                    axfQRPayRequestData.shop_name = payInfo.payee_info.shop_name;
                    goPay(axfQRPayRequestData, payRequest);
                }
            }
        });
    }

    private void getPricingStrategy(final String pay_amt, String pay_channel, String qr_id) {
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String, Object> map = new HashMap<>();
        map.put("pay_amt", pay_amt);
        map.put("pay_channel", pay_channel);
        map.put("qr_id", qr_id);
        AxfQRPayService axfQRPayService = ApiFactory.getFactory().create(AxfQRPayService.class);
        axfQRPayService.get_pricing_strategy(NetUtils.getRequestParams(getActivity(), map), NetUtils.getSign(NetUtils.getRequestParams(getActivity(), map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(OcpPayFixedDeskActivity.this, true, true, null) {
                    @Override
                    public void onNext(Object o) {
                        final StrategyResponse strategyResponse = new Gson().fromJson(o.toString(), StrategyResponse.class);

                        final AxfQRPayRequestData axfQRPayRequestData = new AxfQRPayRequestData();
                        if (isFixed) {
                            axfQRPayRequestData.orig_amt = payInfo.payee_info.amounts.get(0).amount;
                            axfQRPayRequestData.amount = Float.parseFloat(payInfo.payee_info.amounts.get(0).amount) + Float.parseFloat(strategyResponse.float_amt) + "";
                        } else {
                            axfQRPayRequestData.orig_amt = edAmount.getText().toString().trim();
                            axfQRPayRequestData.amount = Float.parseFloat(edAmount.getText().toString().trim()) + Float.parseFloat(strategyResponse.float_amt) + "";
                        }

                        final PayRequest payRequest = new PayRequest();
                        if (isFixed) {
                            payRequest.amount = Float.parseFloat(payInfo.payee_info.amounts.get(0).amount) + Float.parseFloat(strategyResponse.float_amt) + "";
                        } else {
                            payRequest.amount = Float.parseFloat(edAmount.getText().toString().trim()) + Float.parseFloat(strategyResponse.float_amt) + "";
                        }

                        //先清除这个优惠数据库
                        try {
                            SQLite.delete()
                                    .from(PricingStrategy.class)
                                    .query();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        for (int i = 0; i < strategyResponse.strategies.size(); i++) {
                            PricingStrategy pricingStrategy = new PricingStrategy();
                            pricingStrategy.id = strategyResponse.strategies.get(i).id;
                            pricingStrategy.name = strategyResponse.strategies.get(i).name;
                            pricingStrategy.float_amt = strategyResponse.strategies.get(i).float_amt;
                            pricingStrategy.save();
                        }

                        if (strategyResponse.float_amt != null && !strategyResponse.float_amt.equals("0") && !strategyResponse.float_amt.equals("0.00") && !strategyResponse.float_amt.equals("0.0")) {
                            /**
                             * 是否有优惠活动
                             */
                            final StrategyDialog strategyDialog = new StrategyDialog(OcpPayFixedDeskActivity.this, strategyResponse.strategies, strategyResponse.org_amt, strategyResponse.pay_amt);
                            strategyDialog.show();
                            strategyDialog.setListener(new StrategyDialog.OnNextCilckListener() {
                                @Override
                                public void next(String payAmount) {
                                    strategyDialog.dismiss();
                                    axfQRPayRequestData.amount = payAmount;
                                    axfQRPayRequestData.merchant_name = payInfo.payee_info.merchant_name;
                                    axfQRPayRequestData.shop_name = payInfo.payee_info.shop_name;
                                    axfQRPayRequestData.pricing_strategy = strategyResponse.strategies;
                                    goPay(axfQRPayRequestData, payRequest);
                                }
                            });
                        } else {
                            axfQRPayRequestData.amount = strategyResponse.pay_amt;
                            axfQRPayRequestData.merchant_name = payInfo.payee_info.merchant_name;
                            axfQRPayRequestData.shop_name = payInfo.payee_info.shop_name;
                            axfQRPayRequestData.pricing_strategy = strategyResponse.strategies;
                            axfQRPayRequestData.merchant_name = payInfo.payee_info.merchant_name;
                            axfQRPayRequestData.shop_name = payInfo.payee_info.shop_name;
                            goPay(axfQRPayRequestData, payRequest);
                        }
                    }
                });
    }


    /**
     * 自动支付
     *
     * @param pay_amt
     * @param pay_channel
     * @param qr_id
     */
    private void getPricingStrategy2(final String pay_amt, String pay_channel, String qr_id) {
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String, Object> map = new HashMap<>();
        map.put("pay_amt", pay_amt);
        map.put("pay_channel", pay_channel);
        map.put("qr_id", qr_id);
        AxfQRPayService axfQRPayService = ApiFactory.getFactory().create(AxfQRPayService.class);
        axfQRPayService.get_pricing_strategy(NetUtils.getRequestParams(getActivity(), map), NetUtils.getSign(NetUtils.getRequestParams(getActivity(), map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(OcpPayFixedDeskActivity.this, true,true, null) {
                    @Override
                    public void onNext(Object o) {
                        final StrategyResponse strategyResponse = new Gson().fromJson(o.toString(), StrategyResponse.class);
                        final AxfQRPayRequestData axfQRPayRequestData = new AxfQRPayRequestData();
                        if (isFixed) {
                            axfQRPayRequestData.orig_amt = payInfo.payee_info.amounts.get(0).amount;
                            axfQRPayRequestData.amount = Float.parseFloat(payInfo.payee_info.amounts.get(0).amount) + Float.parseFloat(strategyResponse.float_amt) + "";
                        } else {
                            axfQRPayRequestData.orig_amt = edAmount.getText().toString().trim();
                            axfQRPayRequestData.amount = Float.parseFloat(edAmount.getText().toString().trim()) + Float.parseFloat(strategyResponse.float_amt) + "";
                        }

                        final PayRequest payRequest = new PayRequest();
                        if (isFixed) {
                            payRequest.amount = Float.parseFloat(payInfo.payee_info.amounts.get(0).amount) + Float.parseFloat(strategyResponse.float_amt) + "";
                        } else {
                            payRequest.amount = Float.parseFloat(edAmount.getText().toString().trim()) + Float.parseFloat(strategyResponse.float_amt) + "";
                        }


                        try {
                            SQLite.delete()
                                    .from(PricingStrategy.class)
                                    .query();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        //先清除这个优惠数据库
                        for (int i = 0; i < strategyResponse.strategies.size(); i++) {
                            PricingStrategy pricingStrategy = new PricingStrategy();
                            pricingStrategy.id = strategyResponse.strategies.get(i).id;
                            pricingStrategy.name = strategyResponse.strategies.get(i).name;
                            pricingStrategy.float_amt = strategyResponse.strategies.get(i).float_amt;
                            pricingStrategy.save();
                        }

                        if (strategyResponse.float_amt != null && !strategyResponse.float_amt.equals("0") && !strategyResponse.float_amt.equals("0.00") && !strategyResponse.float_amt.equals("0.0")) {
                            final StrategyDialog strategyDialog = new StrategyDialog(OcpPayFixedDeskActivity.this, strategyResponse.strategies, strategyResponse.org_amt, strategyResponse.pay_amt);
                            strategyDialog.show();
                            strategyDialog.setListener(new StrategyDialog.OnNextCilckListener() {
                                @Override
                                public void next(String payAmount) {
                                    strategyDialog.dismiss();
                                    axfQRPayRequestData.amount = payAmount;
                                    axfQRPayRequestData.merchant_name = payInfo.payee_info.merchant_name;
                                    axfQRPayRequestData.shop_name = payInfo.payee_info.shop_name;
                                    axfQRPayRequestData.pricing_strategy = strategyResponse.strategies;
                                    payRequest.amount = payAmount;
                                    goPay2(axfQRPayRequestData, payRequest);
                                }
                            });
                        } else {
                            axfQRPayRequestData.amount = strategyResponse.pay_amt;
                            axfQRPayRequestData.merchant_name = payInfo.payee_info.merchant_name;
                            axfQRPayRequestData.shop_name = payInfo.payee_info.shop_name;
                            axfQRPayRequestData.pricing_strategy = strategyResponse.strategies;
                            axfQRPayRequestData.merchant_name = payInfo.payee_info.merchant_name;
                            axfQRPayRequestData.shop_name = payInfo.payee_info.shop_name;
                            goPay2(axfQRPayRequestData, payRequest);
                        }
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
                .subscribe(new BaseSubscriber<Object>(OcpPayFixedDeskActivity.this, true, null) {

                    @Override
                    public void onNext(Object o) {
                        PaymentRecordResponse recordResponse = new Gson().fromJson(o.toString(), PaymentRecordResponse.class);
                        if (recordResponse.payment_result.order_status.equals("paied")) {
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("PaymentOrder", paymentOrder2);
                            bundle.putSerializable("PayInfo", payInfo);
                            Intent intent = new Intent(OcpPayFixedDeskActivity.this, OcpPaySucActivity.class);
                            intent.putExtras(bundle);
                            startActivity(intent);
                            finish();
                        } else if (recordResponse.payment_result.order_status.equals("unpay") || recordResponse.payment_result.order_status.equals("processing")) {
                            //弹窗
                            if (dialog != null) {

                            } else {
                                dialog = new AlertDialog.Builder(OcpPayFixedDeskActivity.this)
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
                                                finish();
                                                Toast.makeText(OcpPayFixedDeskActivity.this, "你可以在“我的-交易记录”下查看支付状态", Toast.LENGTH_LONG).show();
                                            }
                                        })
                                        .create();

                                dialog.show();
                            }

                        }
                    }
                });
    }

    public static class PaymentRecordResponse extends RealmObject {
        public BaseResponse resp;
        public PaymentRecord payment_result;
    }

    public static class StrategyResponse extends RealmObject {
        public BaseResponse resp;
        String org_amt;
        String float_amt;
        String pay_amt;
        List<PricingStrategy> strategies;
    }

    public class UPQRQuickPayMethod extends PayMethod implements Serializable {
        public String channel;
        public UPBankCard card;
    }

    public class UPBankCard {
        public String id;
        public String bank_name;
        public String card_type_name;
        public String card_no;
        public String iss_ins_code;
        public String iss_ins_name;
        public String iss_ins_icon;
    }

    public class UPQRQuickPayChannelRequest {
        public String pay_password;        // 支付密码, aes128加密
        public String bank_card_code;
    }

    @SuppressLint("SetTextI18n")
    private void initPayMethod(final QrResultActivity.PayInfo payInfo) {

        if (payInfo != null) {
            if (isFixed) {
                tvFixedAmount.setVisibility(View.VISIBLE);
                tvFixedAmount.setText("¥ " + new DecimalFormat("0.00").format(totalMoney(Float.parseFloat(payInfo.payee_info.amounts.get(0).amount))));
                llNotFiexd.setVisibility(View.GONE);
                next.setVisibility(View.VISIBLE);
            } else {
                //不定额
                tvFixedAmount.setVisibility(View.GONE);
                llNotFiexd.setVisibility(View.VISIBLE);
                next.setVisibility(View.VISIBLE);
                llFix.setVisibility(View.VISIBLE);
                amNkvKeyboard.setOnNumberClickListener(OcpPayFixedDeskActivity.this);
                amNkvKeyboard.setVisibility(View.VISIBLE);
            }

            tvShopName.setText(payInfo.payee_info.name);

            /**
             * 默认的支付方式列表
             */
            ArrayList<QrResultActivity.PayMethod> defpayMethods = new ArrayList<>();
            defpayMethods.addAll(payInfo.payee_info.pay_methods);
            //剔除不需要的数据
            Iterator<QrResultActivity.PayMethod> it = defpayMethods.iterator();
            while (it.hasNext()) {
                QrResultActivity.PayMethod x = it.next();
                if (!x.is_default) {
                    it.remove();
                }
            }

            if (defpayMethods.size() == 0) {
                if (payInfo.payee_info.pay_methods.size() != 0)
                    defpayMethods.add(payInfo.payee_info.pay_methods.get(0));
            }

            ocpPayWayDefAdapter = new OcpPayWayDefAdapter(OcpPayFixedDeskActivity.this, payInfo.payee_info.pay_methods, defpayMethods, R.layout.ocp_pay_method_item);
            recyclerview.setAdapter(ocpPayWayDefAdapter);
            ocpPayWayDefAdapter.getFristPayWay(new OcpPayWayDefAdapter.fristSelectPayWay() {
                @Override
                public void way(QrResultActivity.PayMethod item) {
                    payMethod = item;
                }
            });

            //每次去查询
            List<PayWayTagData> payWayTagData = SQLite.select().from(PayWayTagData.class).queryList();
            if (payWayTagData.size() > 0 && payWayTagData.size() < payInfo.payee_info.pay_methods.size()) {
                //存在新的支付方式
                //查询有没有各个id的值
                for (int i = 0; i < payInfo.payee_info.pay_methods.size(); i++) {
                    if (payInfo.payee_info.pay_methods.get(i).card != null) {
                        PayWayTagData data = new Select()
                                .from(PayWayTagData.class)
                                .where(PayWayTagData_Table.cardId.is(payInfo.payee_info.pay_methods.get(i).card.id))
                                .querySingle();
                        if (data == null || TextUtils.isEmpty(data.toString())) {
                            PayWayTagData data2 = new PayWayTagData();
                            data2.cardId = payInfo.payee_info.pay_methods.get(i).card.id;
                            data2.channel = payInfo.payee_info.pay_methods.get(i).channel;
                            data2.isNews = true;
                            data2.save();
                            OcpPayWayDefAdapter.hasNews = true;
                            ocpPayWayDefAdapter.notifyDataSetChanged();
                        }
                    } else {
                        PayWayTagData data = new Select()
                                .from(PayWayTagData.class)
                                .where(PayWayTagData_Table.cardId.is(payInfo.payee_info.pay_methods.get(i).channel))
                                .querySingle();
                        if (data == null || TextUtils.isEmpty(data.toString())) {
                            PayWayTagData data2 = new PayWayTagData();
                            data2.cardId = payInfo.payee_info.pay_methods.get(i).channel;
                            data2.channel = payInfo.payee_info.pay_methods.get(i).channel;
                            data2.isNews = true;
                            data2.save();
                            OcpPayWayDefAdapter.hasNews = true;
                            ocpPayWayDefAdapter.notifyDataSetChanged();
                        }
                    }
                }
            } else if (payWayTagData.size() == 0) {
                for (int i = 0; i < payInfo.payee_info.pay_methods.size(); i++) {
                    if (payInfo.payee_info.pay_methods.get(i).card != null) {
                        PayWayTagData data = new PayWayTagData();
                        data.cardId = payInfo.payee_info.pay_methods.get(i).card.id;
                        data.channel = payInfo.payee_info.pay_methods.get(i).channel;
                        data.purpose = payInfo.payee_info.pay_methods.get(i).purpose;
                        data.isNews = false;
                        data.save();
                        OcpPayWayDefAdapter.hasNews = false;
                        ocpPayWayDefAdapter.notifyDataSetChanged();
                    } else {
                        //channel作为id
                        PayWayTagData data = new PayWayTagData();
                        data.cardId = payInfo.payee_info.pay_methods.get(i).channel;
                        data.channel = payInfo.payee_info.pay_methods.get(i).channel;
                        data.purpose = payInfo.payee_info.pay_methods.get(i).purpose;
                        data.isNews = false;
                        data.save();
                        OcpPayWayDefAdapter.hasNews = false;
                        ocpPayWayDefAdapter.notifyDataSetChanged();
                    }
                }
            }
        }

        //是否存在开通
        if (!TextUtils.isEmpty(payInfo.auth_hint) && !App.ocpAccount.account.status.equals("OK")) {
            authHint.setVisibility(View.VISIBLE);
            authHint.setText(payInfo.auth_hint);
            authHint.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
            authHint.getPaint().setAntiAlias(true);//抗锯齿
            authHint.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putString("qr", qrId);
                    bundle.putBoolean("result", true);
                    bundle.putString("amount", edAmount.getText().toString().trim());
                    bundle.putSerializable("pay_method", payMethod);
                    Intent intent = new Intent(OcpPayFixedDeskActivity.this, OcpVerActivity.class);
                    intent.putExtras(bundle);
                    startActivityForResult(intent, 2);
                }
            });
        } else {
            authHint.setVisibility(View.GONE);
        }
    }

    private void pay(final PayRequest payRequest) {
        final LoadingDialog loadingDialog = new LoadingDialog(this,true);
        loadingDialog.show();
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String, Object> map = new HashMap<>();
        map.put("pay_req", payRequest);
        PaymentService paymentService = ApiFactory.getFactory().create(PaymentService.class);
        paymentService.payOder(NetUtils.getRequestParams(OcpPayFixedDeskActivity.this, map), NetUtils.getSign(NetUtils.getRequestParams(OcpPayFixedDeskActivity.this, map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Object>() {

                    @Override
                    public void onNext(Object o) {
                        if (loadingDialog != null)
                            loadingDialog.dismiss();

                        PayOderResponse payOderResponse = new Gson().fromJson(o.toString(), PayOderResponse.class);
                        if (payOderResponse.resp.resp_code.equals(AppConstant.FREE)) {
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("PaymentOrder", payOderResponse.order);
                            bundle.putSerializable("PayInfo", payInfo);
                            Intent intent = new Intent(OcpPayFixedDeskActivity.this, OcpPaySucActivity.class);
                            intent.putExtras(bundle);
                            startActivity(intent);
                            finish();
                            return;
                        }

                        if (payMethod.channel.equals("UPQRQuickPay")) {
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("PaymentOrder", payOderResponse.order);
                            bundle.putSerializable("PayInfo", payInfo);
                            Intent intent = new Intent(OcpPayFixedDeskActivity.this, OcpPaySucActivity.class);
                            intent.putExtras(bundle);
                            startActivity(intent);
                            finish();
                        } else {
                            paymentOrder = payOderResponse.order;
                            //重新查询
                            order = payOderResponse.order.order_no;
                            //下单成功
                            if (payMethod.channel.equals("AliAppPay")) {
                                //支付宝支付
                                AliPay(payOderResponse.order.pay_data);
                            } else if (payMethod.channel.equals("WxAppPay")) {
                                if (isWeixinAvilible(OcpPayFixedDeskActivity.this)) {
                                    DataBean dataBean = new Gson().fromJson(payOderResponse.order.pay_data, DataBean.class);
                                    weixinPay(dataBean);
                                } else {
                                    Toast.makeText(OcpPayFixedDeskActivity.this, "请先安装微信", Toast.LENGTH_SHORT).show();
                                }

                            } else if (payMethod.channel.equals("UnionPay")) {
                                Unpay(payOderResponse);
                            } else if (payMethod.channel.equals("UPQRQuickPay")) {
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("PaymentOrder", paymentOrder);
                                bundle.putSerializable("PayInfo", payInfo);
                                Intent intent = new Intent(OcpPayFixedDeskActivity.this, OcpPaySucActivity.class);
                                intent.putExtras(bundle);
                                startActivity(intent);
                                finish();
                            }
                        }
                    }

                    @Override
                    public void onCompleted() {
                        if (loadingDialog != null)
                            loadingDialog.dismiss();
                    }

                    @Override
                    public void onError(Throwable e) {

                        if (loadingDialog != null)
                            loadingDialog.dismiss();

                        if (e instanceof HttpException) {
                            if (e.getMessage().contains("504")) {
                                final PayStutasDialog payStutasDialog = new PayStutasDialog(OcpPayFixedDeskActivity.this);
                                payStutasDialog.show();
                                payStutasDialog.setListener(new PayStutasDialog.selectItem() {
                                    @Override
                                    public void payed() {
                                        finish();
                                    }

                                    @Override
                                    public void repay() {
                                        if (payStutasDialog != null)
                                            payStutasDialog.dismiss();
                                    }

                                    @Override
                                    public void go_pay_list() {
                                        startActivity(new Intent(OcpPayFixedDeskActivity.this, MePayListNewActivity.class));
                                    }
                                });
                            } else {
                                final PayStutasDialog payStutasDialog = new PayStutasDialog(OcpPayFixedDeskActivity.this);
                                payStutasDialog.show();
                                payStutasDialog.setListener(new PayStutasDialog.selectItem() {
                                    @Override
                                    public void payed() {
                                        finish();
                                    }

                                    @Override
                                    public void repay() {
                                        if (payStutasDialog != null)
                                            payStutasDialog.dismiss();
                                    }

                                    @Override
                                    public void go_pay_list() {
                                        startActivity(new Intent(OcpPayFixedDeskActivity.this, MePayListNewActivity.class));
                                    }
                                });
                            }
                        } else if (e instanceof IOException) {
                            if (!Util.isNetworkConnected(OcpPayFixedDeskActivity.this)) {
                                final PayStutasDialog payStutasDialog = new PayStutasDialog(OcpPayFixedDeskActivity.this);
                                payStutasDialog.show();
                                payStutasDialog.setListener(new PayStutasDialog.selectItem() {
                                    @Override
                                    public void payed() {
                                        finish();
                                    }

                                    @Override
                                    public void repay() {
                                        if (payStutasDialog != null)
                                            payStutasDialog.dismiss();
                                    }

                                    @Override
                                    public void go_pay_list() {
                                        startActivity(new Intent(OcpPayFixedDeskActivity.this, MePayListNewActivity.class));
                                    }
                                });
                            } else {
                                final PayStutasDialog payStutasDialog = new PayStutasDialog(OcpPayFixedDeskActivity.this);
                                payStutasDialog.show();
                                payStutasDialog.setListener(new PayStutasDialog.selectItem() {
                                    @Override
                                    public void payed() {
                                        finish();
                                    }

                                    @Override
                                    public void repay() {
                                        if (payStutasDialog != null)
                                            payStutasDialog.dismiss();
                                    }

                                    @Override
                                    public void go_pay_list() {
                                        startActivity(new Intent(OcpPayFixedDeskActivity.this, MePayListNewActivity.class));
                                    }
                                });
                            }
                        } else if (e instanceof ApiException) {
                            ApiException exception = (ApiException) e;
                            if (exception.mErrorCode == AppConstant.RELOGIN) {
                                OcpPayFixedDeskActivity.this.sendBroadcast(new Intent(MainActivity.RELOGIN_BROADCAST));
                                ((Activity) OcpPayFixedDeskActivity.this).finish();
                            } else {
                                showLocalToast(exception.getMessage());
                            }
                        } else {
                            showLocalToast(e.getMessage());
                        }
                    }
                });
    }

    private void showLocalToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void Unpay(PayOderResponse payOderResponse) {
        //获取到paymentoder
        if (payOderResponse.resp.resp_code.equals(AppConstant.SUCCESS)) {

            paymentOrder = payOderResponse.order;

            PaymentInfo paymentInfo = new PaymentInfo();

            PayMethod payMethods = new PayMethod();
            payMethods.assistance_hint = payMethod.assistance_hint;
            payMethods.channel = payMethod.channel;
            payMethods.is_default = payMethod.is_default;
            payMethods.is_recommended = payMethod.is_recommended;
            payMethods.merchant_code = payMethod.merchant_code;
            payMethods.promotion_hint = payMethod.promotion_hint;
            payMethods.merchant_id = payMethod.merchant_id;
            payMethods.supported_banks = payMethod.supported_banks;

            paymentInfo.pay_method = payMethods;

            paymentInfo.payment_amt = payOderResponse.order.payment_amt;
            paymentInfo.channel_orderno = payOderResponse.order.order_no;
            paymentInfo.pay_info = payOderResponse.order.pay_data;
            paymentInfo.fee_name = payOderResponse.order.trade_summary + "-支付成功";
            paymentInfo.fee_time = payOderResponse.order.order_time;

            Bundle bundle = new Bundle();
            bundle.putSerializable("PaymentOrder", paymentOrder);
            bundle.putSerializable("PayInfo", payInfo);

            Intent intent = new Intent(this, MyPayActivity.class);
            intent.putExtra(MyPayActivity.EXTRA_AMOUNT, payOderResponse.order.payment_amt);
            intent.putExtra(MyPayActivity.EXTRA_PAY_FOR, PayFor.AxfQRPay);
            intent.putExtra(MyPayActivity.EXTRA_PAYMENT_INFO, paymentInfo);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    private void weixinPay(DataBean dataBean) {
        App.WXAPPID = dataBean.getAppid();
        IWXAPI api = WXAPIFactory.createWXAPI(this, dataBean.getAppid());
        PayReq req = new PayReq();
        req.appId = dataBean.getAppid();
        req.partnerId = dataBean.getPartnerid();
        req.prepayId = dataBean.getPrepayid();
        req.nonceStr = dataBean.getNoncestr();
        req.timeStamp = dataBean.getTimestamp();
        req.packageValue = dataBean.getPackageX();
        req.sign = dataBean.getSign();
        api.registerApp(dataBean.getAppid());
        Log.d("OcpPayFixedDeskActivity", "req:" + new Gson().toJson(req));
        Log.d("OcpPayFixedDeskActivity", "req2:" + req.appId);
        Log.d("OcpPayFixedDeskActivity", "req3:" + req.partnerId);
        api.sendReq(req);
        App.isNeedCheck = true;
    }

    @Override
    public void onNumberReturn(String number) {

        if (number.equals(".") && amount.length() == 0) {
            return;
        }
        if (number.equals("0") && amount.equals("0")) {
            return;
        }
        if (amount.contains(".") && number.equals(".")) {
            return;
        }

        if (amount.contains(".")) {
            String arr[] = amount.split("\\.");
            if (arr.length > 1 && arr[1].length() == 2) {
                return;
            }
            if (arr.length > 1 && arr[1].length() > 2) {
                return;
            }
            amount += number;
        } else {
            if (amount.contains(".")) {
                if (amount.length() > 7) {
                    return;
                } else {
                    amount += number;
                }
            } else {
                if (amount.length() > 5) {
                    return;
                } else {
                    amount += number;
                }
            }
        }
        setEditContain();
    }

    private void setEditContain() {
        edAmount.setText(amount);
        edAmount.setSelection(edAmount.getText().length());
    }

    @Override
    public void onNumberDelete() {
        if (amount.length() <= 1) {
            amount = "";
        } else {
            amount = amount.substring(0, amount.length() - 1);
        }
        setEditContain();
    }

    public static class PayOderResponse extends RealmObject {
        public BaseResponse resp;
        public PaymentOrder order;
    }

    public class UPQRQuickPayData implements Serializable {
        public String amt;                                        // 交易金额
        public String orig_amt;                               // 交易原金额
        public List<UPCoupon> up_coupon_info;                    // 银联优惠信息
    }

    public class UPCoupon implements Serializable {
        public String type;        // 类型
        public String spnsr_id;    //
        public String offst_amt;    // 优惠金额
        public String id;        // ID
        public String desc;        // 描述
        public String addn_info;    // 附加信息
    }

    public class AxfQRPayRequestData {
        public String amount;
        public String pay_remark;
        public String qr;
        public String qr_id;
        public String service_fee;
        public String bank_card_code;
        public String merchant_name;
        public String shop_name;
        public String orig_amt;
        public List<PricingStrategy> pricing_strategy;
    }

    /**
     * @param info
     */
    private void AliPay(String info) {
        final String orderInfo = info;   // 订单信息
        Runnable payRunnable = new Runnable() {
            @Override
            public void run() {
                PayTask alipay = new PayTask(OcpPayFixedDeskActivity.this);
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

        App.isNeedCheck = true;
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

    //此方法只是关闭软键盘
    private void hintKbTwo() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm.isActive() && getCurrentFocus() != null) {
                    if (getCurrentFocus().getWindowToken() != null) {
                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                }
            }
        }, 0);

    }

    @Override
    protected void onResume() {
        super.onResume();
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                llNotFiexd.setFocusable(false);
                llNotFiexd.setFocusableInTouchMode(false);

                tvMark.setFocusable(false);

                tvMark.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        tvMark.setFocusable(true);
                        tvMark.setFocusableInTouchMode(true);
                    }
                });

                edAmount.setEnabled(true);
                edAmount.setFocusableInTouchMode(true);
                edAmount.setFocusable(true);
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
                    if (paymentOrder != null && paymentOrder.pay_for != null && order != null)
                        get_pay_result(paymentOrder.pay_for, order);
                }
            }
        }, 1000);
    }

    public void JudgePayPwd() {
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String, Object> map = new HashMap<>();
        PaymentService meService = ApiFactory.getFactory().create(PaymentService.class);
        meService.getPaymentConfig(NetUtils.getRequestParams(this, map), NetUtils.getSign(NetUtils.getRequestParams(this, map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(this, true, null) {

                    @Override
                    public void onNext(Object o) {
                        GetPaymentConfigResponse response = new Gson().fromJson(o.toString(), GetPaymentConfigResponse.class);
                        if (response.config.has_pay_password) {
                            Intent intent = new Intent(OcpPayFixedDeskActivity.this, UnionInputPayPwdActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putBoolean(UnionHtmlActivity.EXTRA_FROM_BANKCARDLIST, true);
                            intent.putExtras(bundle);
                            startActivity(new Intent(intent));
                        } else {
                            Bundle bundle = new Bundle();
                            bundle.putBoolean("isOcp", true);
                            Intent intent = new Intent(OcpPayFixedDeskActivity.this, UnionSetPayPwdActivity.class);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                    }
                });
    }

    public class GetPaymentConfigResponse extends RealmObject {
        public BaseResponse resp;
        public PaymentConfig config;
    }

    /**
     * 收单
     */
    private void acquiring(final String qrId) {
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String, Object> map = new HashMap<>();
        map.put("qr", qrId);
        AxfQRPayService axfQRPayService = ApiFactory.getFactory().create(AxfQRPayService.class);
        axfQRPayService.acquiring(NetUtils.getRequestParams(getActivity(), map), NetUtils.getSign(NetUtils.getRequestParams(getActivity(), map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(OcpPayFixedDeskActivity.this, true, null) {

                    @Override
                    public void onNext(Object o) {
                        final QrResultActivity.PayInfoResp payInfoResp = new Gson().fromJson(o.toString(), QrResultActivity.PayInfoResp.class);
                        payInfo = payInfoResp.pay_info;
                        /**
                         * 默认的支付方式列表
                         */
                        ArrayList<QrResultActivity.PayMethod> defpayMethods = new ArrayList<>();
                        defpayMethods.addAll(payInfo.payee_info.pay_methods);
                        //剔除不需要的数据
                        Iterator<QrResultActivity.PayMethod> it = defpayMethods.iterator();
                        while (it.hasNext()) {
                            QrResultActivity.PayMethod x = it.next();
                            if (!x.is_default) {
                                it.remove();
                            }
                        }

                        if (defpayMethods.size() == 0) {
                            if (payInfo.payee_info.pay_methods.size() != 0)
                                defpayMethods.add(payInfo.payee_info.pay_methods.get(0));
                        }

                        ocpPayWayDefAdapter = new OcpPayWayDefAdapter(OcpPayFixedDeskActivity.this, payInfo.payee_info.pay_methods, defpayMethods, R.layout.ocp_pay_method_item);
                        recyclerview.setAdapter(ocpPayWayDefAdapter);
                        ocpPayWayDefAdapter.getFristPayWay(new OcpPayWayDefAdapter.fristSelectPayWay() {
                            @Override
                            public void way(QrResultActivity.PayMethod item) {
                                payMethod = item;
                            }
                        });
                        loadOcpAccountStatus(payInfo);

                        for (int i = 0; i < payInfo.payee_info.pay_methods.size(); i++) {
                            if (payInfo.payee_info.pay_methods.get(i).card != null) {
                                App.hasBankCard = true;
                            }
                        }
                    }
                });
    }

    /**
     * 收单
     */
    private void acquiring2(final String qrId) {
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String, Object> map = new HashMap<>();
        map.put("qr", qrId);
        AxfQRPayService axfQRPayService = ApiFactory.getFactory().create(AxfQRPayService.class);
        axfQRPayService.acquiring(NetUtils.getRequestParams(getActivity(), map), NetUtils.getSign(NetUtils.getRequestParams(getActivity(), map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(OcpPayFixedDeskActivity.this, true, null) {

                    @Override
                    public void onNext(Object o) {
                        final QrResultActivity.PayInfoResp payInfoResp = new Gson().fromJson(o.toString(), QrResultActivity.PayInfoResp.class);
                        ocpPayWayDefAdapter.updatePayWay(payInfoResp.pay_info.payee_info.pay_methods);
                        ocpPayWayDefAdapter.notifyDataSetChanged();
                    }
                });

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
     * 获取一码通账户状态
     */
    private void loadOcpAccountStatus(final QrResultActivity.PayInfo payInfo) {
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String, Object> map = new HashMap<>();
        AxfQRPayService axfQRPayService = ApiFactory.getFactory().create(AxfQRPayService.class);
        axfQRPayService.get_account_info(NetUtils.getRequestParams(OcpPayFixedDeskActivity.this, map), NetUtils.getSign(NetUtils.getRequestParams(OcpPayFixedDeskActivity.this, map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(OcpPayFixedDeskActivity.this, true, null) {

                    @Override
                    public void onNext(Object o) {
                        final OcpAccount ocpAccount = new Gson().fromJson(o.toString(), OcpAccount.class);
                        //获取到之后存入数据
                        App.ocpAccount = ocpAccount;
                        initPayMethod(payInfo);
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!rxSubscription.isUnsubscribed()) {
            rxSubscription.unsubscribe();
        }
    }

    /**
     * 四舍五入保留两位
     *
     * @param money
     * @return
     */
    public static double totalMoney(double money) {
        BigDecimal bigDec = new BigDecimal(money);
        double total = bigDec.setScale(2, BigDecimal.ROUND_HALF_UP)
                .doubleValue();
        DecimalFormat df = new DecimalFormat("0.00");
        return Double.parseDouble(df.format(total));
    }

    public class PayMethod2 implements Serializable {
        public String channel;
        public String merchant_id;
        public String merchant_code;
        public String promotion_hint;
        public String assistance_hint;
        public UPBankCard2 card;
        public String purpose;
    }

    public class UPBankCard2 implements Serializable {
        public String id;
        public String bank_name;
        public String card_type_name;
        public String card_no;
        public String iss_ins_code;
        public String iss_ins_name;
        public String iss_ins_icon;
        public String card_type;
    }


    private void goPay(AxfQRPayRequestData axfQRPayRequestData, final PayRequest payRequest) {
        if (payMethod != null && payInfo != null) {
            if (!TextUtils.isEmpty(tvMark.getText().toString().trim())) {
                App.mAxLoginSp.setUnionReMark(tvMark.getText().toString().trim());
                axfQRPayRequestData.pay_remark = tvMark.getText().toString().trim();
            } else {
                axfQRPayRequestData.pay_remark = "";
            }
            axfQRPayRequestData.qr = qrId;

            if (payMethod.card != null)
                axfQRPayRequestData.bank_card_code = payMethod.card.id;

            payRequest.request_data = axfQRPayRequestData;
            payRequest.pay_for = PayFor.AxfQRPay;

            final PayMethod payMethods = new PayMethod();
            payMethods.assistance_hint = payMethod.assistance_hint;
            payMethods.channel = payMethod.channel;
            payMethods.is_default = payMethod.is_default;
            payMethods.is_recommended = payMethod.is_recommended;
            payMethods.merchant_code = payMethod.merchant_code;
            payMethods.promotion_hint = payMethod.promotion_hint;
            payMethods.merchant_id = payMethod.merchant_id;
            payMethods.supported_banks = payMethod.supported_banks;
            payMethods.payment_config = payMethod.payment_config;
            payRequest.pay_method = payMethods;

            if (payRequest.pay_method.channel.equals("UPQRQuickPay") && payMethod.purpose == null) {
                if (!isFixed) {
                    if (payMethod.payment_config.pin_free && Float.parseFloat(payRequest.amount) <= Float.parseFloat(payMethod.payment_config.pin_free_amount) && Float.parseFloat(payRequest.amount) <= Float.parseFloat(payMethod.payment_config.trade_limit_per_day)) {
                        //免密支付
                        UPQRQuickPayChannelRequest upqrQuickPayChannelRequest = new UPQRQuickPayChannelRequest();
                        upqrQuickPayChannelRequest.bank_card_code = payMethod.card.id;
                        payRequest.channel_request_data = upqrQuickPayChannelRequest;
                        payRequest.pay_method.card = new com.axinfu.modellib.thrift.unqr.UPBankCard();
                        payRequest.pay_method.card.setCard_no(payMethod.card.card_no);
                        payRequest.pay_method.card.setIss_ins_name(payMethod.card.iss_ins_name);
                        payRequest.pay_method.card.setIss_ins_icon(payMethod.card.iss_ins_icon);
                        payRequest.pay_method.card.setIss_ins_code(payMethod.card.iss_ins_code);
                        payRequest.pay_method.card.setId(payMethod.card.id);
                        payRequest.pay_method.card.setCard_type_name(payMethod.card.card_type_name);
                        pay(payRequest);
                    } else {
                        PasswordWindow passwordWindow = new PasswordWindow(OcpPayFixedDeskActivity.this);
                        Display display = getWindow().getWindowManager().getDefaultDisplay();
                        passwordWindow.setWidth(display.getWidth());
                        passwordWindow.setHeight(display.getHeight() / 2 + 100);
                        passwordWindow.showAtLocation(next, Gravity.BOTTOM, 0, 0);
                        passwordWindow.getPassword(new PasswordWindow.IGetpassword() {
                            @Override
                            public void getpass(String password) {
                                if (password != null) {
                                    UPQRQuickPayChannelRequest upqrQuickPayChannelRequest = new UPQRQuickPayChannelRequest();
                                    upqrQuickPayChannelRequest.bank_card_code = payMethod.card.id;
                                    try {
                                        upqrQuickPayChannelRequest.pay_password = bytesToHexString(Secure.encodeMessageField(password.getBytes("UTF-8")));
                                    } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                    }
                                    payRequest.channel_request_data = upqrQuickPayChannelRequest;
                                    payRequest.pay_method.card = new com.axinfu.modellib.thrift.unqr.UPBankCard();
                                    payRequest.pay_method.card.setCard_no(payMethod.card.card_no);
                                    payRequest.pay_method.card.setIss_ins_name(payMethod.card.iss_ins_name);
                                    payRequest.pay_method.card.setIss_ins_icon(payMethod.card.iss_ins_icon);
                                    payRequest.pay_method.card.setIss_ins_code(payMethod.card.iss_ins_code);
                                    payRequest.pay_method.card.setId(payMethod.card.id);
                                    payRequest.pay_method.card.setCard_type_name(payMethod.card.card_type_name);
                                    pay(payRequest);
                                }
                            }
                        });
//

                    }
                } else {
                    if (payMethod.payment_config.pin_free && Float.parseFloat(payRequest.amount) <= Float.parseFloat(payMethod.payment_config.pin_free_amount) && Float.parseFloat(payRequest.amount) <= Float.parseFloat(payMethod.payment_config.trade_limit_per_day)) {
                        //免密支付
                        UPQRQuickPayChannelRequest upqrQuickPayChannelRequest = new UPQRQuickPayChannelRequest();
                        upqrQuickPayChannelRequest.bank_card_code = payMethod.card.id;
                        payRequest.channel_request_data = upqrQuickPayChannelRequest;
                        payRequest.pay_method.card = new com.axinfu.modellib.thrift.unqr.UPBankCard();
                        payRequest.pay_method.card.setCard_no(payMethod.card.card_no);
                        payRequest.pay_method.card.setIss_ins_name(payMethod.card.iss_ins_name);
                        payRequest.pay_method.card.setIss_ins_icon(payMethod.card.iss_ins_icon);
                        payRequest.pay_method.card.setIss_ins_code(payMethod.card.iss_ins_code);
                        payRequest.pay_method.card.setId(payMethod.card.id);
                        payRequest.pay_method.card.setCard_type_name(payMethod.card.card_type_name);
                        pay(payRequest);
                    } else {
                        PasswordWindow passwordWindow = new PasswordWindow(OcpPayFixedDeskActivity.this);
                        Display display = getWindow().getWindowManager().getDefaultDisplay();
                        passwordWindow.setWidth(display.getWidth());
                        passwordWindow.setHeight(display.getHeight() / 2 + 100);
                        passwordWindow.showAtLocation(next, Gravity.BOTTOM, 0, 0);
                        passwordWindow.getPassword(new PasswordWindow.IGetpassword() {
                            @Override
                            public void getpass(String password) {
                                if (password != null) {
                                    UPQRQuickPayChannelRequest upqrQuickPayChannelRequest = new UPQRQuickPayChannelRequest();
                                    upqrQuickPayChannelRequest.bank_card_code = payMethod.card.id;
                                    try {
                                        upqrQuickPayChannelRequest.pay_password = bytesToHexString(Secure.encodeMessageField(password.getBytes("UTF-8")));
                                    } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                    }
                                    payRequest.channel_request_data = upqrQuickPayChannelRequest;
                                    payRequest.pay_method.card = new com.axinfu.modellib.thrift.unqr.UPBankCard();
                                    payRequest.pay_method.card.setCard_no(payMethod.card.card_no);
                                    payRequest.pay_method.card.setIss_ins_name(payMethod.card.iss_ins_name);
                                    payRequest.pay_method.card.setIss_ins_icon(payMethod.card.iss_ins_icon);
                                    payRequest.pay_method.card.setIss_ins_code(payMethod.card.iss_ins_code);
                                    payRequest.pay_method.card.setId(payMethod.card.id);
                                    payRequest.pay_method.card.setCard_type_name(payMethod.card.card_type_name);
                                    pay(payRequest);
                                }
                            }
                        });
                    }
//
                }
            } else if (payRequest.pay_method.channel.equals("UPQRQuickPay") && payMethod.purpose != null) {
                if (App.hasBankCard) {
                    RxBus.getDefault().send("ag");
                } else {
                    startActivity(new Intent(OcpPayFixedDeskActivity.this, UnionSweptEmptyCardActivity.class));
                }
            } else {
                pay(payRequest);
            }
        }

    }

    private void goPay2(AxfQRPayRequestData axfQRPayRequestData, final PayRequest payRequest) {
        if (payMethod != null && payInfo != null) {
            if (!TextUtils.isEmpty(tvMark.getText().toString().trim())) {
                App.mAxLoginSp.setUnionReMark(tvMark.getText().toString().trim());
                axfQRPayRequestData.pay_remark = tvMark.getText().toString().trim();
            } else {
                axfQRPayRequestData.pay_remark = "";
            }
            axfQRPayRequestData.qr = qrId;

            if (payMethod.card != null)
                axfQRPayRequestData.bank_card_code = payMethod.card.id;

            payRequest.request_data = axfQRPayRequestData;
            payRequest.pay_for = PayFor.AxfQRPay;

            final PayMethod payMethods = new PayMethod();
            payMethods.assistance_hint = payMethod.assistance_hint;
            payMethods.channel = payMethod.channel;
            payMethods.is_default = payMethod.is_default;
            payMethods.is_recommended = payMethod.is_recommended;
            payMethods.merchant_code = payMethod.merchant_code;
            payMethods.promotion_hint = payMethod.promotion_hint;
            payMethods.merchant_id = payMethod.merchant_id;
            payMethods.supported_banks = payMethod.supported_banks;
            payMethods.payment_config = payMethod.payment_config;
            payRequest.pay_method = payMethods;

            if (payRequest.pay_method.channel.equals("UPQRQuickPay") && payMethod.purpose == null) {
                if (!isFixed) {
                    if (payMethod.payment_config.pin_free && Float.parseFloat(payRequest.amount) <= Float.parseFloat(payMethod.payment_config.pin_free_amount) && Float.parseFloat(payRequest.amount) <= Float.parseFloat(payMethod.payment_config.trade_limit_per_day)) {
                        //免密支付
                        UPQRQuickPayChannelRequest upqrQuickPayChannelRequest = new UPQRQuickPayChannelRequest();
                        upqrQuickPayChannelRequest.bank_card_code = payMethod.card.id;
                        payRequest.channel_request_data = upqrQuickPayChannelRequest;
                        payRequest.pay_method.card = new com.axinfu.modellib.thrift.unqr.UPBankCard();
                        payRequest.pay_method.card.setCard_no(payMethod.card.card_no);
                        payRequest.pay_method.card.setIss_ins_name(payMethod.card.iss_ins_name);
                        payRequest.pay_method.card.setIss_ins_icon(payMethod.card.iss_ins_icon);
                        payRequest.pay_method.card.setIss_ins_code(payMethod.card.iss_ins_code);
                        payRequest.pay_method.card.setId(payMethod.card.id);
                        payRequest.pay_method.card.setCard_type_name(payMethod.card.card_type_name);
                        pay(payRequest);
                    } else {
                        UPQRQuickPayChannelRequest upqrQuickPayChannelRequest = new UPQRQuickPayChannelRequest();
                        upqrQuickPayChannelRequest.bank_card_code = payMethod.card.id;
                        try {
                            upqrQuickPayChannelRequest.pay_password = bytesToHexString(Secure.encodeMessageField((App.mAxLoginSp.getUnionPayPwd()).getBytes("UTF-8")));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        payRequest.channel_request_data = upqrQuickPayChannelRequest;
                        payRequest.pay_method.card = new com.axinfu.modellib.thrift.unqr.UPBankCard();
                        payRequest.pay_method.card.setCard_no(payMethod.card.card_no);
                        payRequest.pay_method.card.setIss_ins_name(payMethod.card.iss_ins_name);
                        payRequest.pay_method.card.setIss_ins_icon(payMethod.card.iss_ins_icon);
                        payRequest.pay_method.card.setIss_ins_code(payMethod.card.iss_ins_code);
                        payRequest.pay_method.card.setId(payMethod.card.id);
                        payRequest.pay_method.card.setCard_type_name(payMethod.card.card_type_name);
                        pay(payRequest);
                    }
                } else {
                    if (payMethod.payment_config.pin_free && Float.parseFloat(payRequest.amount) <= Float.parseFloat(payMethod.payment_config.pin_free_amount) && Float.parseFloat(payRequest.amount) <= Float.parseFloat(payMethod.payment_config.trade_limit_per_day)) {
                        //免密支付
                        UPQRQuickPayChannelRequest upqrQuickPayChannelRequest = new UPQRQuickPayChannelRequest();
                        upqrQuickPayChannelRequest.bank_card_code = payMethod.card.id;
                        payRequest.channel_request_data = upqrQuickPayChannelRequest;
                        payRequest.pay_method.card = new com.axinfu.modellib.thrift.unqr.UPBankCard();
                        payRequest.pay_method.card.setCard_no(payMethod.card.card_no);
                        payRequest.pay_method.card.setIss_ins_name(payMethod.card.iss_ins_name);
                        payRequest.pay_method.card.setIss_ins_icon(payMethod.card.iss_ins_icon);
                        payRequest.pay_method.card.setIss_ins_code(payMethod.card.iss_ins_code);
                        payRequest.pay_method.card.setId(payMethod.card.id);
                        payRequest.pay_method.card.setCard_type_name(payMethod.card.card_type_name);
                        pay(payRequest);
                    } else {
                        UPQRQuickPayChannelRequest upqrQuickPayChannelRequest = new UPQRQuickPayChannelRequest();
                        upqrQuickPayChannelRequest.bank_card_code = payMethod.card.id;
                        try {
                            upqrQuickPayChannelRequest.pay_password = bytesToHexString(Secure.encodeMessageField((App.mAxLoginSp.getUnionPayPwd()).getBytes("UTF-8")));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        payRequest.channel_request_data = upqrQuickPayChannelRequest;
                        payRequest.pay_method.card = new com.axinfu.modellib.thrift.unqr.UPBankCard();
                        payRequest.pay_method.card.setCard_no(payMethod.card.card_no);
                        payRequest.pay_method.card.setIss_ins_name(payMethod.card.iss_ins_name);
                        payRequest.pay_method.card.setIss_ins_icon(payMethod.card.iss_ins_icon);
                        payRequest.pay_method.card.setIss_ins_code(payMethod.card.iss_ins_code);
                        payRequest.pay_method.card.setId(payMethod.card.id);
                        payRequest.pay_method.card.setCard_type_name(payMethod.card.card_type_name);
                        pay(payRequest);
                    }
//
                }
            } else if (payRequest.pay_method.channel.equals("UPQRQuickPay") && payMethod.purpose != null) {
                if (App.hasBankCard) {
                    RxBus.getDefault().send("ag");
                } else {
                    startActivity(new Intent(OcpPayFixedDeskActivity.this, UnionSweptEmptyCardActivity.class));
                }
            } else {
                pay(payRequest);
            }
        }

    }
}
