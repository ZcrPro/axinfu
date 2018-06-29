package com.zhihuianxin.xyaxf.app.payment.quk;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zhihuianxin.xyaxf.app.AppConstant;
import modellib.service.PaymentService;
import modellib.service.QuickPayService;
import modellib.thrift.base.BaseResponse;
import modellib.thrift.payment.BankCardInfo;
import modellib.thrift.payment.PaymentOrder;
import modellib.thrift.payment.QuickPayBankCardInfo;
import modellib.thrift.payment.QuickPayChannelRequest;
import modellib.thrift.payment.QuickPayMethod;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.xyaxf.axpay.modle.PayRequest;
import com.zhihuianxin.xyaxf.app.base.axutil.Util;
import com.zhihuianxin.secure.Secure;
import com.zhihuianxin.xyaxf.App;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.ApiFactory;
import com.zhihuianxin.xyaxf.app.BaseSubscriber;
import com.zhihuianxin.xyaxf.app.RetrofitFactory;
import com.zhihuianxin.xyaxf.app.payment.PaymentStatusActivity;
import com.zhihuianxin.xyaxf.app.utils.NetUtils;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.realm.RealmObject;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.zhihuianxin.xyaxf.app.me.view.activity.AddBankCardActivity.bytesToHexString;


/**
 * Created by zcrprozcrpro on 2017/7/4.
 */

public class QukActivity extends Activity {

    @InjectView(R.id.logo)
    ImageView logo;
    @InjectView(R.id.bank_name)
    TextView bankName;
    @InjectView(R.id.card_no)
    TextView cardNo;
    @InjectView(R.id.top)
    RelativeLayout top;
    @InjectView(R.id.inputEdit_name)
    EditText inputEditName;
    @InjectView(R.id.inputEdit_id_card)
    EditText inputEditIdCard;
    @InjectView(R.id.inputEdit_mobile)
    EditText inputEditMobile;
    @InjectView(R.id.inputEdit)
    EditText inputEdit;
    @InjectView(R.id.next)
    Button next;
    @InjectView(R.id.tv_fee_num)
    TextView tvFeeNum;
    @InjectView(R.id.btn_ok)
    Button btnOk;
    @InjectView(R.id.rl_check)
    RelativeLayout rlCheck;
    @InjectView(R.id.card_type)
    TextView cardType;
    @InjectView(R.id.tv_lost_err_text)
    TextView tvLostErrText;
    @InjectView(R.id.btn_lost_ok)
    Button btnLostOk;
    @InjectView(R.id.btn_cancel)
    TextView btnCancel;
    @InjectView(R.id.lostView)
    LinearLayout lostView;
    @InjectView(R.id.grayBg)
    View grayBg;
    @InjectView(R.id.title)
    TextView title;
    @InjectView(R.id.back_icon)
    RelativeLayout backIcon;
    @InjectView(R.id.bar)
    RelativeLayout bar;

    /**
     * 是否是第一次点击发送按钮
     */
    private boolean isFrist = true;

    public static final String BANKINFO = "BANK_INFO";

    private QuickPayMethod quickPayMethod;
    private BankCardInfo bankCardInfo;
    private String card_no;
    private Float amount;
    private DisplayMetrics metrics;
    private PayRequest payRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quk_activity);
        ButterKnife.inject(this);

        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            amount = bundle.getFloat("amount");
            payRequest = (PayRequest) bundle.getSerializable("payRequest");
            quickPayMethod = (QuickPayMethod) bundle.getSerializable(QukMobileActivity.METHOD);
            bankCardInfo = (BankCardInfo) bundle.getSerializable(BANKINFO);
            card_no = bundle.getString("card_no");
            if (bankCardInfo != null && card_no != null && amount != null)
                initBankUI();

        }

        inputEditMobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.equals("")) {
                    next.setEnabled(false);
                } else {
                    next.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String name = inputEditName.getText().toString().trim();
                final String mobile = inputEditMobile.getText().toString().trim();
                final String idcard = inputEditIdCard.getText().toString().trim();
                final String code = inputEdit.getText().toString().trim();

                if (TextUtils.isEmpty(inputEditMobile.getText().toString().trim())) {
                    Toast.makeText(QukActivity.this, "请先填写手机号", Toast.LENGTH_SHORT).show();
                } else {
                    if (isFrist) {
                        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(mobile) || TextUtils.isEmpty(idcard)) {
                            Toast.makeText(QukActivity.this, "请填写完整信息", Toast.LENGTH_SHORT).show();
                        } else {
                            if (quickPayMethod != null && bankCardInfo != null) {
                                try {
                                    QuickPayBankCardInfo quickPayBankCardInfo = new QuickPayBankCardInfo();
                                    quickPayBankCardInfo.bank_card_no = bytesToHexString(Secure.encodeMessageField(card_no.getBytes("UTF-8")));
                                    quickPayBankCardInfo.id_card_no = bytesToHexString(Secure.encodeMessageField(idcard.getBytes("UTF-8")));
                                    quickPayBankCardInfo.mobile = bytesToHexString(Secure.encodeMessageField(mobile.getBytes("UTF-8")));
                                    quickPayBankCardInfo.name = bytesToHexString(Secure.encodeMessageField(name.getBytes("UTF-8")));

                                    applyContract(quickPayMethod, quickPayBankCardInfo);

                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                    } else {
                        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(mobile) || TextUtils.isEmpty(idcard)) {
                            Toast.makeText(QukActivity.this, "请填写完整信息", Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            if (quickPayMethod != null && bankCardInfo != null) {
                                try {
                                    QuickPayBankCardInfo quickPayBankCardInfo = new QuickPayBankCardInfo();
                                    quickPayBankCardInfo.bank_card_no = bytesToHexString(Secure.encodeMessageField(card_no.getBytes("UTF-8")));
                                    quickPayBankCardInfo.id_card_no = bytesToHexString(Secure.encodeMessageField(idcard.getBytes("UTF-8")));
                                    quickPayBankCardInfo.mobile = bytesToHexString(Secure.encodeMessageField(mobile.getBytes("UTF-8")));
                                    quickPayBankCardInfo.name = bytesToHexString(Secure.encodeMessageField(name.getBytes("UTF-8")));

                                    resendSmsCode(quickPayMethod, quickPayBankCardInfo);

                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }


                            }
                        }

                    }
                }
            }
        });

        btnLostOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideLostAlertAnim();
            }
        });

        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLostAlertAnim();
            }
        });


        btnOk.setEnabled(true);
        btnOk.setBackgroundResource(R.color.axf_common_blue);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = inputEditName.getText().toString().trim();
                String mobile = inputEditMobile.getText().toString().trim();
                String idcard = inputEditIdCard.getText().toString().trim();
                String code = inputEdit.getText().toString().trim();

                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(QukActivity.this, "请填写姓名", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(idcard)) {
                    Toast.makeText(QukActivity.this, "请填写身份证号", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(mobile)) {
                    Toast.makeText(QukActivity.this, "请填写手机号", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(code)) {
                    Toast.makeText(QukActivity.this, "请填写验证码", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        QuickPayBankCardInfo quickPayBankCardInfo = new QuickPayBankCardInfo();
                        quickPayBankCardInfo.bank_card_no = bytesToHexString(Secure.encodeMessageField(card_no.getBytes("UTF-8")));
                        quickPayBankCardInfo.id_card_no = bytesToHexString(Secure.encodeMessageField(idcard.getBytes("UTF-8")));
                        quickPayBankCardInfo.mobile = bytesToHexString(Secure.encodeMessageField(mobile.getBytes("UTF-8")));
                        quickPayBankCardInfo.name = bytesToHexString(Secure.encodeMessageField(name.getBytes("UTF-8")));
                        confirmContract(quickPayMethod, quickPayBankCardInfo);

                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    private void initBankUI() {
        bankName.setText(bankCardInfo.bank_name);
        cardNo.setText(getIDValue(card_no));
        cardType.setText(bankCardInfo.card_type);

        DisplayImageOptions config = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();

        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.loadImage(bankCardInfo.bank_icon, config, new SimpleImageLoadingListener() {

            @Override
            public void onLoadingComplete(String imageUri, View view,
                                          Bitmap loadedImage) {
                if (!Util.isEmpty(bankCardInfo.bank_icon)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        logo.setBackground(null);
                    }
                }

                logo.setImageBitmap(loadedImage);
            }
        });

        tvFeeNum.setText(amount + "");

    }

    class TimeCount extends CountDownTimer {

        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            next.setClickable(false);
            next.setText("(" + millisUntilFinished / 1000 + ") 重新发送");
        }

        @Override
        public void onFinish() {
            next.setText("重新获取验证码");
            next.setClickable(true);
        }
    }


    /**
     * 第一次发送签约申请
     */
    private void applyContract(QuickPayMethod payMethod, QuickPayBankCardInfo quickPayBankCardInfo) {
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String, Object> map = new HashMap<>();
        map.put("pay_method", payMethod);
        map.put("bank_card_info", quickPayBankCardInfo);
        QuickPayService quickPayService = ApiFactory.getFactory().create(QuickPayService.class);
        quickPayService.apply_contract(NetUtils.getRequestParams(this, map), NetUtils.getSign(NetUtils.getRequestParams(this, map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(this, true, null) {
                    @Override
                    public void onNext(Object o) {
                        final AnResponse anResponse = new Gson().fromJson(o.toString(), AnResponse.class);
                        if (anResponse.resp.resp_code.equals(AppConstant.SUCCESS)) {
                            isFrist = false;
                            Toast.makeText(QukActivity.this, "验证码已发送", Toast.LENGTH_SHORT).show();
                            if (anResponse.serial != null) {
                                App.serials = anResponse.serial;
                            } else {
                                Toast.makeText(QukActivity.this, "服务端出错:serial空", Toast.LENGTH_SHORT).show();
                            }
                            TimeCount time = new TimeCount(120000, 1000);
                            time.start();
                        }
                    }
                });
    }

    /**
     * 重新发送验证码
     */
    private void resendSmsCode(QuickPayMethod payMethod, QuickPayBankCardInfo quickPayBankCardInfo) {
        if (App.serials == null) {
            Toast.makeText(this, "请填写正确的信息", Toast.LENGTH_SHORT).show();
            return;
        }
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String, Object> map = new HashMap<>();
        map.put("pay_method", payMethod);
        map.put("serial", App.serials);
        map.put("bank_card_info", quickPayBankCardInfo);
        QuickPayService quickPayService = ApiFactory.getFactory().create(QuickPayService.class);
        quickPayService.resend_sms_code(NetUtils.getRequestParams(this, map), NetUtils.getSign(NetUtils.getRequestParams(this, map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(this, true, null) {
                    @Override
                    public void onNext(Object o) {
                        final AnResponse anResponse = new Gson().fromJson(o.toString(), AnResponse.class);
                        if (anResponse.resp.resp_code.equals(AppConstant.SUCCESS)) {
                            Toast.makeText(QukActivity.this, "验证码已发送", Toast.LENGTH_SHORT).show();
                            TimeCount time = new TimeCount(120000, 1000);
                            time.start();
                        }
                    }
                });
    }

    /**
     * 下单
     */
    private void confirmContract(QuickPayMethod payMethod, final QuickPayBankCardInfo quickPayBankCardInfo) {
        if (App.serials == null) {
            Toast.makeText(this, "请填写正确的信息", Toast.LENGTH_SHORT).show();
            return;
        }
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String, Object> map = new HashMap<>();
        map.put("pay_method", payMethod);
        map.put("serial", App.serials);
        map.put("sms_code", inputEdit.getText().toString().trim());
        map.put("bank_card_info", quickPayBankCardInfo);
        QuickPayService quickPayService = ApiFactory.getFactory().create(QuickPayService.class);
        quickPayService.confirm_contract(NetUtils.getRequestParams(this, map), NetUtils.getSign(NetUtils.getRequestParams(this, map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(this, true, null) {
                    @Override
                    public void onNext(Object o) {
                        final AnResponse anResponse = new Gson().fromJson(o.toString(), AnResponse.class);
                        if (anResponse.resp.resp_code.equals(AppConstant.SUCCESS)) {
                            //如果签约成功 --去下单支付
                            if (payRequest != null) {
                                QuickPayChannelRequest quickPayChannelRequest = new QuickPayChannelRequest();
                                quickPayChannelRequest.bank_card_no = quickPayBankCardInfo.bank_card_no;
                                quickPayChannelRequest.id_card_no = quickPayBankCardInfo.id_card_no;
                                quickPayChannelRequest.mobile = quickPayBankCardInfo.mobile;
                                quickPayChannelRequest.name = quickPayBankCardInfo.name;
                                payRequest.channel_request_data = quickPayChannelRequest;
                                pay(payRequest);
                            }
                        }
                    }
                });
    }

    private String getIDValue(String oriID) {
        if (Util.isEmpty(oriID)) {
            return oriID;
        } else {
            return "**** **** **** **** " + oriID.substring(oriID.length() - 4, oriID.length());
        }
    }

    public static class AnResponse {
        public BaseResponse resp;
        public String serial;
    }


    private void showLostAlertAnim() {
        lostView.setVisibility(View.VISIBLE);
        int halfScreen = (metrics.heightPixels / 2) + 200;
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(lostView, "translationY", 0, halfScreen + 50);
        animator2.setDuration(600);
        animator2.start();
        grayBg.setVisibility(View.VISIBLE);
        grayBg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void hideLostAlertAnim() {
        int halfScreen = (metrics.heightPixels / 2) + 200;
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(lostView, "translationY", halfScreen + 50, 0);
        animator2.setDuration(600);
        animator2.start();
        grayBg.setVisibility(View.GONE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                lostView.setVisibility(View.GONE);
            }
        }, 590);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (lostView.getVisibility() == View.GONE)
                showLostAlertAnim();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }


    private void pay(final PayRequest payRequest) {
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String, Object> map = new HashMap<>();
        map.put("pay_req", payRequest);
        PaymentService paymentService = ApiFactory.getFactory().create(PaymentService.class);
        paymentService.payOder(NetUtils.getRequestParams(QukActivity.this, map), NetUtils.getSign(NetUtils.getRequestParams(QukActivity.this, map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(QukActivity.this, true, null) {
                    @Override
                    public void onNext(Object o) {
                        PayOderResponse payOderResponse = new Gson().fromJson(o.toString(), PayOderResponse.class);
                        //获取到paymentoder
                        if (payOderResponse.resp.resp_code.equals(AppConstant.SUCCESS)) {
                            //成功
                            Intent intent = new Intent(QukActivity.this, PaymentStatusActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString(PaymentStatusActivity.AMOUNT, payOderResponse.order.payment_amt);
                            bundle.putString(PaymentStatusActivity.NAME, payOderResponse.order.trade_summary + "-支付成功");
                            bundle.putSerializable(PaymentStatusActivity.WAY, payOderResponse.order.pay_method);
                            bundle.putString(PaymentStatusActivity.TIME, payOderResponse.order.order_time);
                            bundle.putString(PaymentStatusActivity.ODER, payOderResponse.order.order_no);
                            intent.putExtras(bundle);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
    }

    public static class PayOderResponse extends RealmObject {
        public BaseResponse resp;
        public PaymentOrder order;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isFrist = true;
        App.serials = null;
    }
}