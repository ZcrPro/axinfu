package com.zhihuianxin.xyaxf.app.ecard.view;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zhihuianxin.xyaxf.app.AppConstant;
import com.axinfu.modellib.service.EcardService;
import com.axinfu.modellib.thrift.base.BaseResponse;
import com.axinfu.modellib.thrift.base.PayMethod;
import com.axinfu.modellib.thrift.ecard.ECard;
import com.axinfu.modellib.thrift.ecard.ECardChargeRecord;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.zhihuianxin.xyaxf.app.base.axutil.SchedulerProvider;
import com.zhihuianxin.xyaxf.app.base.axutil.Util;
import com.zhihuianxin.xyaxf.App;
import com.zhihuianxin.xyaxf.BaseRealmActionBarActivity;
import com.zhihuianxin.xyaxf.BuildConfig;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.ApiFactory;
import com.zhihuianxin.xyaxf.app.BaseSubscriber;
import com.zhihuianxin.xyaxf.app.RetrofitFactory;
import com.zhihuianxin.xyaxf.app.customview.OverScrollView;
import com.zhihuianxin.xyaxf.app.customview.shimmer.Shimmer;
import com.zhihuianxin.xyaxf.app.customview.shimmer.ShimmerTextView;
import com.zhihuianxin.xyaxf.app.ecard.EcardContract;
import com.zhihuianxin.xyaxf.app.ecard.Ecardpresenter;
import com.zhihuianxin.xyaxf.app.ecard.account.EcardAccountBookActivity;
import com.zhihuianxin.xyaxf.app.fee.FeeDetailActivity;
import com.zhihuianxin.xyaxf.app.payment.FeeCashierDeskActivity;
import com.zhihuianxin.xyaxf.app.utils.NetUtils;
import com.zhihuianxin.xyaxf.app.utils.RxBus;
import com.zhihuianxin.xyaxf.app.view.LoadingDialog;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.realm.RealmObject;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by zcrpro on 2016/10/12.
 */

public class EcardActivity extends BaseRealmActionBarActivity implements EcardContract.EcardView {


    @InjectView(R.id.iv_bg_ecard)
    ImageView ivBgEcard;
    @InjectView(R.id.tv_card_no)
    TextView tvCardNo;
    @InjectView(R.id.tv_card_name)
    TextView tvCardName;
    @InjectView(R.id.tv_balance)
    TextView tvBalance;
    @InjectView(R.id.rd_rechage_amount_1)
    RadioButton rdRechageAmount1;
    @InjectView(R.id.rd_rechage_amount_2)
    RadioButton rdRechageAmount2;
    @InjectView(R.id.rd_rechage_amount_3)
    RadioButton rdRechageAmount3;
    @InjectView(R.id.rd_rechage_amount_4)
    RadioButton rdRechageAmount4;
    @InjectView(R.id.radio)
    RadioGroup radio;
    @InjectView(R.id.ed_payment_num)
    EditText edPaymentNum;
    @InjectView(R.id.ll_payment)
    LinearLayout llPayment;
    @InjectView(R.id.btn_ok)
    Button btnOk;
    @InjectView(R.id.iv_w_logo)
    ImageView ivWLogo;
    @InjectView(R.id.w_text)
    TextView wText;
    @InjectView(R.id.swiperefreshlayout)
    SwipeRefreshLayout swiperefreshlayout;
    @InjectView(R.id.tv_shimmer)
    ShimmerTextView tvShimmer;
    @InjectView(R.id.ll_push)
    LinearLayout llPush;
    @InjectView(R.id.scrollview)
    OverScrollView scrollview;
    @InjectView(R.id.grayBg)
    View grayBg;
    @InjectView(R.id.ed_re_password)
    EditText edRePassword;
    @InjectView(R.id.click_errorbtn)
    Button clickErrorbtn;
    @InjectView(R.id.exit)
    TextView exit;
    @InjectView(R.id.neePasswordView)
    RelativeLayout neePasswordView;
    @InjectView(R.id.ic_back)
    ImageView icBack;
    @InjectView(R.id.more)
    ImageView more;
    @InjectView(R.id.tv_oder_amt1)
    TextView tvOderAmt1;
    @InjectView(R.id.tv_oder_status_1)
    TextView tvOderStatus1;
    @InjectView(R.id.ll_oder_one)
    LinearLayout llOderOne;
    @InjectView(R.id.tv_oder_amt2)
    TextView tvOderAmt2;
    @InjectView(R.id.tv_oder_status_2)
    TextView tvOderStatus2;
    @InjectView(R.id.ll_oder_two)
    LinearLayout llOderTwo;
    @InjectView(R.id.iv_call_lost)
    ImageView ivCallLost;
    @InjectView(R.id.ed_lost_password)
    EditText edLostPassword;
    @InjectView(R.id.btn_lost_ok)
    Button btnLostOk;
    @InjectView(R.id.btn_cancel)
    TextView btnCancel;
    @InjectView(R.id.lostView)
    LinearLayout lostView;
    @InjectView(R.id.tv_lost_err_text)
    TextView tvLostErrText;
    @InjectView(R.id.ed_not_lost_password)
    EditText edNotLostPassword;
    @InjectView(R.id.tv_not_lost_err_text)
    TextView tvNotLostErrText;
    @InjectView(R.id.btn_not_lost_ok)
    Button btnNotLostOk;
    @InjectView(R.id.btn_not_cancel)
    TextView btnNotCancel;
    @InjectView(R.id.not_lostView)
    LinearLayout notLostView;
    @InjectView(R.id.pull_icon)
    ImageView pullIcon;
    @InjectView(R.id.mx_ed_re_password)
    EditText mxEdRePassword;
    @InjectView(R.id.mx_click_errorbtn)
    Button mxClickErrorbtn;
    @InjectView(R.id.mx_exit)
    TextView mxExit;
    @InjectView(R.id.mx_neePasswordView)
    RelativeLayout mxNeePasswordView;
    @InjectView(R.id.text)
    TextView text;
    private EcardContract.EcardPresenter presenter;
    private Ecardpresenter ecardPresenter;
    private EcardRecordWindow ecardRecordWindow;
    private DisplayMetrics metrics;
    private LoadingDialog loadingDialog;

    private Subscription rxSbscription;
    private boolean into_recard; //是否是进入交易记录

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ecardPresenter.unsubscribe();
        ecardRecordWindow = null;
        if (!rxSbscription.isUnsubscribed()) {
            rxSbscription.unsubscribe();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (App.ECARD_PASSWORD!=null){
            more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(EcardActivity.this, EcardAccountBookActivity.class));
                }
            });
        }

    }

    @Override
    protected int getContentViewId() {
        return R.layout.ecard_activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ecard_activity);
        findViewById(R.id.action_bar).setVisibility(View.GONE);
        ButterKnife.inject(this);
        swiperefreshlayout.setOnRefreshListener(refreshListener);
        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        loadingDialog = new LoadingDialog(this);
        initView();
        ecardPresenter = new Ecardpresenter(this, this, SchedulerProvider.getInstance());
        presenter.loadEcardData(false);
        loadEccrdBillData(null, null, 0);

        clickErrorbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(edRePassword.getText().toString())) {
                    Toast.makeText(EcardActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                } else {
                    verifyPassword(edRePassword.getText().toString().trim());
                }
            }
        });

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideBackAlertAnim();
            }
        });

        icBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

//阻尼效果
        llPush.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Shimmer si = new Shimmer();
                        si.setDuration(3000);
                        si.start(tvShimmer);
                        AlphaAnimation aa = new AlphaAnimation(0f, 1.0f);
                        aa.setDuration(500);
                        aa.setFillAfter(true);
                        llPush.startAnimation(aa);
                        llPush.setVisibility(View.VISIBLE);
                        break;

                    case MotionEvent.ACTION_UP:

                        break;
                }
                return true;
            }
        });

        btnLostOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //挂失操作
                if (!TextUtils.isEmpty(edLostPassword.getText().toString())) {
                    callLost(edLostPassword.getText().toString().trim());
                } else {
                    Toast.makeText(EcardActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideLostAlertAnim();
            }
        });

        btnNotLostOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //挂失操作
                if (!TextUtils.isEmpty(edNotLostPassword.getText().toString())) {
                    callNotLost(edNotLostPassword.getText().toString().trim());
                } else {
                    Toast.makeText(EcardActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnNotCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideNotLostAlertAnim();
            }
        });


        rxSbscription = RxBus.getDefault().toObservable().subscribe(new Action1<Object>() {
            @Override
            public void call(Object event) {
                if (event.equals("succeed")) {
                    presenter.loadEcardData(false);
                }
            }
        });

    }

    SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            presenter.loadEcardData(true);
        }
    };

    private void initView() {
//        getSupportFragmentManager().beginTransaction().add(R.id.frame_bill, EcardBillFragment.newInstance("bill")).commitAllowingStateLoss();
        //进入界面50元按钮获取焦点
        radio.check(R.id.rd_rechage_amount_1);
        radio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                edPaymentNum.setText("");
                edPaymentNum.setBackgroundResource(R.drawable.ecard_edittext_normal);
                llPayment.setFocusableInTouchMode(true);
                llPayment.setFocusable(true);
            }
        });

        edPaymentNum.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (hasFocus) {
                    radio.clearCheck();
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            edPaymentNum.setBackgroundResource(R.drawable.ecard_edittext_focused);
                        }
                    });
                }
            }
        });

        edPaymentNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radio.clearCheck();
                edPaymentNum.setBackgroundResource(R.drawable.ecard_edittext_focused);
            }
        });

        edPaymentNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if (Float.parseFloat(s.toString()) > 300) {
                        String temp = edPaymentNum.getText().toString();
                        edPaymentNum.setText(temp.substring(0, temp.length() - 1));
                        edPaymentNum.setSelection(edPaymentNum.getText().length());
                        Toast.makeText(EcardActivity.this, "金额不能超过300元", Toast.LENGTH_SHORT).show();
                    }
                    if (Float.parseFloat(s.toString()) < 1) {
                        edPaymentNum.setText("1");
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void ecardSuccess(final ECard eCard) {
        Log.d("TAGS", eCard.charge_notice);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                rdRechageAmount1.setText(eCard.recharge_amts.get(0) + "元");
                rdRechageAmount2.setText(eCard.recharge_amts.get(1) + "元");
                rdRechageAmount3.setText(eCard.recharge_amts.get(2) + "元");
                if (eCard.recharge_amts.size() == 4) {
                    rdRechageAmount4.setVisibility(View.VISIBLE);
                    rdRechageAmount4.setText(eCard.recharge_amts.get(3) + "元");
                    edPaymentNum.setVisibility(View.GONE);
                } else {
                    rdRechageAmount4.setVisibility(View.GONE);
                    edPaymentNum.setVisibility(View.VISIBLE);
                }
                if (!TextUtils.isEmpty(eCard.card_no)) {
                    if (tvCardNo.length() == 18) {
                        tvCardNo.setText(eCard.card_no.substring(0, 2) + "**************" +
                                eCard.card_no.substring(eCard.card_no.length() - 2, eCard.card_no.length()));
                    } else {
                        tvCardNo.setText(eCard.card_no.length() >= 2 ?
                                Util.getXingHao(eCard.card_no.length() - 2) + eCard.card_no.substring(eCard.card_no.length() - 2, eCard.card_no.length()) :
                                eCard.card_no);
                    }

                } else {
                    tvCardNo.setText("");
                }
                tvCardName.setText(eCard.student_name);
                if (eCard.status.equals("OK"))
                    tvBalance.setText("￥" + new DecimalFormat("0.00").format(Float.parseFloat(eCard.card_balance)));
                if (eCard.status.equals("OK")) {
                    if (eCard.charge_notice != null) {
                        if (TextUtils.isEmpty(eCard.charge_notice)) {
                            ivWLogo.setVisibility(View.GONE);
                        } else {
                            wText.setText(eCard.charge_notice);
                            ivWLogo.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    if (eCard.status_desc != null) {
                        if (TextUtils.isEmpty(eCard.status_desc)) {
                            ivWLogo.setVisibility(View.GONE);
                        } else {
                            wText.setText(eCard.status_desc);
                            ivWLogo.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString(FeeCashierDeskActivity.WHICH, FeeCashierDeskActivity.ECARD);
                bundle.putSerializable(FeeDetailActivity.FEE_WAY, (ArrayList<PayMethod>) eCard.pay_methods);
                if (rdRechageAmount1.isChecked()) {
                    if (!BuildConfig.AnXinDEBUG) {
                        bundle.putString(FeeCashierDeskActivity.PAY_AMOUNT, eCard.recharge_amts.get(0) + "");
                    } else {
                        bundle.putString(FeeCashierDeskActivity.PAY_AMOUNT, "0.01");
                    }
                } else if (rdRechageAmount2.isChecked()) {
                    bundle.putString(FeeCashierDeskActivity.PAY_AMOUNT, eCard.recharge_amts.get(1) + "");
                } else if (rdRechageAmount3.isChecked()) {
                    bundle.putString(FeeCashierDeskActivity.PAY_AMOUNT, eCard.recharge_amts.get(2) + "");
                } else if (rdRechageAmount4.isChecked()) {
                    bundle.putString(FeeCashierDeskActivity.PAY_AMOUNT, eCard.recharge_amts.get(3) + "");
                } else if (!TextUtils.isEmpty(edPaymentNum.getText().toString().trim())) {
                    bundle.putString(FeeCashierDeskActivity.PAY_AMOUNT, Float.parseFloat(edPaymentNum.getText().toString().trim()) + "");
                }
                if (!rdRechageAmount1.isChecked() && !rdRechageAmount2.isChecked() && !rdRechageAmount3.isChecked() && !rdRechageAmount4.isChecked()) {
                    if (!TextUtils.isEmpty(edPaymentNum.getText().toString().trim())) {
                        Intent intent = new Intent(EcardActivity.this, FeeCashierDeskActivity.class);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    } else {
                        Toast.makeText(EcardActivity.this, "请填写正确的充值金额", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Intent intent = new Intent(EcardActivity.this, FeeCashierDeskActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }

            }
        });

        //如果一卡通状态为挂失
        if (eCard.status.equals("Error")) {
            ivBgEcard.setImageResource(R.drawable.red_card);
            tvBalance.setText("一卡通异常!");
            rdRechageAmount1.setEnabled(false);
            rdRechageAmount2.setEnabled(false);
            rdRechageAmount3.setEnabled(false);
            rdRechageAmount4.setEnabled(false);
            rdRechageAmount1.setChecked(false);
            rdRechageAmount2.setChecked(false);
            rdRechageAmount3.setChecked(false);
            rdRechageAmount4.setChecked(false);
            rdRechageAmount1.setTextColor(getResources().getColor(R.color.axf_light_gray));
            rdRechageAmount2.setTextColor(getResources().getColor(R.color.axf_light_gray));
            rdRechageAmount3.setTextColor(getResources().getColor(R.color.axf_light_gray));
            rdRechageAmount4.setTextColor(getResources().getColor(R.color.axf_light_gray));
            edPaymentNum.setEnabled(false);
            btnOk.setEnabled(false);
            ivCallLost.setVisibility(View.GONE);
        } else if (eCard.status.equals("ReportLoss")) {
            ivBgEcard.setImageResource(R.drawable.red_card);
            tvBalance.setText("一卡通已挂失!");
            rdRechageAmount1.setEnabled(false);
            rdRechageAmount2.setEnabled(false);
            rdRechageAmount3.setEnabled(false);
            rdRechageAmount4.setEnabled(false);
            rdRechageAmount1.setChecked(false);
            rdRechageAmount2.setChecked(false);
            rdRechageAmount3.setChecked(false);
            rdRechageAmount4.setChecked(false);
            rdRechageAmount1.setTextColor(getResources().getColor(R.color.axf_light_gray));
            rdRechageAmount2.setTextColor(getResources().getColor(R.color.axf_light_gray));
            rdRechageAmount3.setTextColor(getResources().getColor(R.color.axf_light_gray));
            rdRechageAmount4.setTextColor(getResources().getColor(R.color.axf_light_gray));
            edPaymentNum.setEnabled(false);
            btnOk.setEnabled(false);
            if (eCard.status_desc != null) {
                if (TextUtils.isEmpty(eCard.status_desc)) {
                    ivWLogo.setVisibility(View.GONE);
                } else {
                    wText.setText(eCard.status_desc);
                    ivWLogo.setVisibility(View.VISIBLE);
                }
            }
            //解挂按钮
            ivCallLost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (eCard.losscard_type.equals("normal")) {
                        showNotLostAlertAnim("normal");
                    } else if (eCard.losscard_type.equals("password")) {
                        showNotLostAlertAnim("password");
                    } else {
                        Toast.makeText(EcardActivity.this, "一卡通状态有误", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            ivBgEcard.setImageResource(R.drawable.blue_card);
            rdRechageAmount1.setText(eCard.recharge_amts.get(0) + "元");
            rdRechageAmount2.setText(eCard.recharge_amts.get(1) + "元");
            rdRechageAmount3.setText(eCard.recharge_amts.get(2) + "元");
            if (eCard.recharge_amts.size() == 4) {
                rdRechageAmount4.setVisibility(View.VISIBLE);
                rdRechageAmount4.setText(eCard.recharge_amts.get(3) + "元");
                edPaymentNum.setVisibility(View.GONE);
            } else {
                rdRechageAmount4.setVisibility(View.GONE);
                edPaymentNum.setVisibility(View.VISIBLE);
            }
            if (!TextUtils.isEmpty(eCard.card_no)) {
                if (tvCardNo.length() == 18) {
                    tvCardNo.setText(eCard.card_no.substring(0, 2) + "**************" +
                            eCard.card_no.substring(eCard.card_no.length() - 2, eCard.card_no.length()));

                } else {
                    tvCardNo.setText(eCard.card_no.length() >= 2 ?
                            Util.getXingHao(eCard.card_no.length() - 2) + eCard.card_no.substring(eCard.card_no.length() - 2, eCard.card_no.length()) :
                            eCard.card_no);
                }
            } else {
                tvCardNo.setText("");
            }
            tvCardName.setText(eCard.student_name);
            if (eCard.status.equals("OK"))
                tvBalance.setText("￥" + new DecimalFormat("0.00").format(Float.parseFloat(eCard.card_balance)));
            if (eCard.charge_notice != null) {
                if (TextUtils.isEmpty(eCard.charge_notice)) {
                    ivWLogo.setVisibility(View.GONE);
                } else {
                    wText.setText(eCard.charge_notice);
                    ivWLogo.setVisibility(View.VISIBLE);
                }
            }
            rdRechageAmount1.setEnabled(true);
            rdRechageAmount2.setEnabled(true);
            rdRechageAmount3.setEnabled(true);
            rdRechageAmount4.setEnabled(true);
            rdRechageAmount1.setChecked(true);
            rdRechageAmount2.setChecked(true);
            rdRechageAmount3.setChecked(true);
            rdRechageAmount4.setChecked(true);
            rdRechageAmount1.setChecked(true);
            rdRechageAmount1.setTextColor(getResources().getColor(R.color.axf_common_blue));
            rdRechageAmount2.setTextColor(getResources().getColor(R.color.axf_common_blue));
            rdRechageAmount3.setTextColor(getResources().getColor(R.color.axf_common_blue));
            rdRechageAmount4.setTextColor(getResources().getColor(R.color.axf_common_blue));
            edPaymentNum.setEnabled(true);
            btnOk.setEnabled(true);
            //挂失按钮
            ivCallLost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (eCard.losscard_type.equals("normal")) {
                        showLostAlertAnim("normal");
                    } else if (eCard.losscard_type.equals("password")) {
                        showLostAlertAnim("password");
                    } else {
                        Toast.makeText(EcardActivity.this, "一卡通状态有误", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        if (eCard.consumption_query_type == null || eCard.consumption_query_type.equals("unsupported")) {
            more.setVisibility(View.GONE);
        } else if (eCard.consumption_query_type.equals("password")&&TextUtils.isEmpty(App.ECARD_PASSWORD)) {
            into_recard = true;
            text.setText("请输入一卡通密码");
            more.setVisibility(View.VISIBLE);
            more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showBackAlertAnim();
                }
            });
        } else if (eCard.consumption_query_type.equals("password")&&!TextUtils.isEmpty(App.ECARD_PASSWORD)) {
            more.setVisibility(View.VISIBLE);
            more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(EcardActivity.this, EcardAccountBookActivity.class));
                }
            });
        } else {
            more.setVisibility(View.VISIBLE);
            more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(EcardActivity.this, EcardAccountBookActivity.class));
                }
            });
        }

        //检测一卡通是否支持挂失
        if (eCard.status.equals("OK") && !eCard.losscard_type.equals("unsupported")) {
            ivCallLost.setVisibility(View.VISIBLE);
            ivCallLost.setImageResource(R.drawable.call_lost);
        } else if (eCard.status.equals("ReportLoss") && !eCard.losscard_type.equals("unsupported")) {
            ivCallLost.setVisibility(View.VISIBLE);
            ivCallLost.setImageResource(R.drawable.call_not_lost);
        } else {
            ivCallLost.setVisibility(View.GONE);
        }
    }

    @Override
    public void ecardFailure() {

    }

    @Override
    public void needPassword() {
        showBackAlertAnim();
    }

    @Override
    public void setPresenter(EcardContract.EcardPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void loadStart() {

    }

    @Override
    public void loadError(String errorMsg) {

    }

    @Override
    public void loadComplete() {
        swiperefreshlayout.setRefreshing(false);
    }


    private void showBackAlertAnim() {
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

    private void verifyPassword(final String password) {
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String, Object> map = new HashMap<>();
        map.put("password", password);
        EcardService ecardService = ApiFactory.getFactory().create(EcardService.class);
        ecardService.verifyPassword(NetUtils.getRequestParams(this, map), NetUtils.getSign(NetUtils.getRequestParams(this, map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(this, false, this) {

                    @Override
                    public void onNext(Object o) {
                        final VerifyPasswordResponse verifyPasswordResponse = new Gson().fromJson(o.toString(), VerifyPasswordResponse.class);
                        if (verifyPasswordResponse.resp.resp_code.equals(AppConstant.SUCCESS)) {
                            hideBackAlertAnim();
                            swiperefreshlayout.setRefreshing(true);
                            if (into_recard){
                                App.ECARD_PASSWORD = password;
                                startActivity(new Intent(EcardActivity.this, EcardAccountBookActivity.class));
                            }
                        } else {
                            Toast.makeText(EcardActivity.this, "验证失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public static class VerifyPasswordResponse extends RealmObject {
        public BaseResponse resp;
    }

    public void loadEccrdBillData(String start_date, String end_date, int page_index) {
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String, Object> map = new HashMap<>();
        map.put("start_date", start_date);
        map.put("end_date", end_date);
        map.put("page_index", page_index);
        map.put("page_size", 10);
        EcardService ecardService = ApiFactory.getFactory().create(EcardService.class);
        ecardService.getChargeRecords(NetUtils.getRequestParams(EcardActivity.this, map), NetUtils.getSign(NetUtils.getRequestParams(EcardActivity.this, map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Object>() {

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Object o) {
                        EcardBillResponse ecardBillResponse = new Gson().fromJson(o.toString(), EcardBillResponse.class);
                        if (ecardBillResponse.records.size() > 0) {
                            tvShimmer.setVisibility(View.VISIBLE);
                            pullIcon.setVisibility(View.VISIBLE);
                            if (ecardBillResponse.records.size() > 2) {
                                tvShimmer.setText("上拉查看更多付款记录");
                                pullIcon.setVisibility(View.VISIBLE);
                                tvShimmer.setVisibility(View.GONE);
                                ecardRecordWindow = new EcardRecordWindow(EcardActivity.this);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        scrollview.setOnSc(new OverScrollView.ScrollViewListener() {
                                            @Override
                                            public void onScrollChanged(OverScrollView scrollView, int x, int y, int oldx, int oldy) {
                                                if (y - oldy > 10) {
                                                    tvShimmer.setVisibility(View.VISIBLE);
                                                    new Handler().postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            tvShimmer.setVisibility(View.GONE);
                                                        }
                                                    }, 2000);
                                                    if (ecardRecordWindow != null)
                                                        ecardRecordWindow.showAtLocation(btnOk, Gravity.BOTTOM, 0, 0);
                                                }
                                            }
                                        });
                                    }
                                }, 200);

                                llPush.setOnTouchListener(new View.OnTouchListener() {
                                    @Override
                                    public boolean onTouch(View v, MotionEvent event) {
                                        switch (event.getAction()) {
                                            case MotionEvent.ACTION_DOWN:
                                                tvShimmer.setVisibility(View.VISIBLE);
                                                new Handler().postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        tvShimmer.setVisibility(View.GONE);
                                                    }
                                                }, 2000);
                                                break;

                                            case MotionEvent.ACTION_UP:
                                                break;
                                        }
                                        return true;
                                    }
                                });

                            } else if (ecardBillResponse.records.size() == 2 || ecardBillResponse.records.size() == 1) {
                                pullIcon.setVisibility(View.INVISIBLE);
                                tvShimmer.setVisibility(View.GONE);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        scrollview.setOnSc(new OverScrollView.ScrollViewListener() {
                                            @Override
                                            public void onScrollChanged(OverScrollView scrollView, int x, int y, int oldx, int oldy) {
                                                if (y - oldy > 10) {
                                                    tvShimmer.setVisibility(View.VISIBLE);
                                                    tvShimmer.setText("暂未查到更多付款记录");
                                                    new Handler().postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            tvShimmer.setVisibility(View.GONE);
                                                        }
                                                    }, 2000);
                                                }
                                            }
                                        });
                                    }
                                }, 200);
                            }

                            if (ecardBillResponse.records.size() == 0) {
                                llOderTwo.setVisibility(View.GONE);
                                llOderOne.setVisibility(View.GONE);
                            } else if (ecardBillResponse.records.size() > 1) {
                                llOderTwo.setVisibility(View.VISIBLE);
                                llOderOne.setVisibility(View.VISIBLE);
                                tvOderAmt1.setText(new DecimalFormat("0.00").format(Float.parseFloat(ecardBillResponse.records.get(0).amount)));
                                tvOderAmt2.setText(new DecimalFormat("0.00").format(Float.parseFloat(ecardBillResponse.records.get(1).amount)));

                                if (ecardBillResponse.records.get(0).status.equals("notify_processing")) {
                                    tvOderStatus1.setText("已支付");
                                } else if (ecardBillResponse.records.get(0).status.equals("notify_success")) {
                                    tvOderStatus1.setText("已上账");
                                } else {
                                    tvOderStatus1.setText("未知");
                                }

                                if (ecardBillResponse.records.get(1).status.equals("notify_processing")) {
                                    tvOderStatus2.setText("已支付");
                                } else if (ecardBillResponse.records.get(1).status.equals("notify_success")) {
                                    tvOderStatus2.setText("已上账");
                                } else {
                                    tvOderStatus2.setText("未知");
                                }
                            } else if (ecardBillResponse.records.size() == 1) {
                                llOderTwo.setVisibility(View.GONE);
                                llOderOne.setVisibility(View.VISIBLE);
                                tvOderAmt1.setText(ecardBillResponse.records.get(0).amount);
                                if (ecardBillResponse.records.get(0).status.equals("notify_processing")) {
                                    tvOderStatus1.setText("已支付");
                                } else if (ecardBillResponse.records.get(0).status.equals("notify_success")) {
                                    tvOderStatus1.setText("已上账");
                                } else {
                                    tvOderStatus1.setText("未知");
                                }
                            }

                        } else {
                            pullIcon.setVisibility(View.INVISIBLE);
                            tvShimmer.setText("还没有付款记录哦!");
                        }
                    }
                });
    }

    public static class EcardBillResponse extends RealmObject {
        public BaseResponse resp;
        public List<ECardChargeRecord> records;
    }

    /**
     * 一卡通挂失 0-不要密码的挂失 1-需要密码
     */
    private void callLost(final String password) {
        loadingDialog.show();
        tvLostErrText.setText("");
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String, Object> map = new HashMap<>();
        if (password != null) {
            map.put("password", password);
        } else {
            map.put("password", "");
        }
        EcardService ecardService = ApiFactory.getFactory().create(EcardService.class);
        ecardService.reportLoss(NetUtils.getRequestParams(EcardActivity.this, map), NetUtils.getSign(NetUtils.getRequestParams(EcardActivity.this, map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Object>() {

                    @Override
                    public void onCompleted() {
                        if (loadingDialog != null)
                            loadingDialog.dismiss();
                        if (edLostPassword != null)
                            edLostPassword.setText("");
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (loadingDialog != null)
                            loadingDialog.dismiss();
                        if (password != null) {
                            tvLostErrText.setText(e.getMessage());
                        } else {
                            Toast.makeText(EcardActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onNext(Object o) {
                        try {
                            lostResponse lostResponse = new Gson().fromJson(o.toString(), lostResponse.class);
                            if (lostResponse.resp.resp_code.equals(AppConstant.SUCCESS)) {
                                Toast.makeText(EcardActivity.this, "挂失成功！", Toast.LENGTH_SHORT).show();
                                if (password != null) {
                                    //关闭输入密码框
                                    hideLostAlertAnim();
                                }
                                swiperefreshlayout.setRefreshing(true);
                                presenter.loadEcardData(true);
                            }
                        } catch (JsonSyntaxException e) {
                            e.printStackTrace();
                            if (password != null) {
                                tvLostErrText.setText(e.getMessage());
                            } else {
                                Toast.makeText(EcardActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    private void showLostAlertAnim(String type) {
        lostView.setVisibility(View.VISIBLE);
        if (type.equals("normal")) {
            edLostPassword.setHint("请重新输登陆密码");
        } else if (type.equals("password")) {
            edLostPassword.setHint("请重新输入一卡通密码");
        }
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

    /**
     * 一卡通挂失 0-不要密码的挂失 1-需要密码
     */
    private void callNotLost(final String password) {
        loadingDialog.show();
        tvLostErrText.setText("");
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String, Object> map = new HashMap<>();
        if (password != null) {
            map.put("password", password);
        } else {
            map.put("password", "");
        }
        EcardService ecardService = ApiFactory.getFactory().create(EcardService.class);
        ecardService.cancelFreeze(NetUtils.getRequestParams(EcardActivity.this, map), NetUtils.getSign(NetUtils.getRequestParams(EcardActivity.this, map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Object>() {

                    @Override
                    public void onCompleted() {
                        if (loadingDialog != null)
                            loadingDialog.dismiss();
                        if (edNotLostPassword != null)
                            edNotLostPassword.setText("");
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (loadingDialog != null)
                            loadingDialog.dismiss();
                        if (password != null) {
                            tvNotLostErrText.setText(e.getMessage());
                        } else {
                            Toast.makeText(EcardActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onNext(Object o) {
                        try {
                            lostResponse lostResponse = new Gson().fromJson(o.toString(), lostResponse.class);
                            if (lostResponse.resp.resp_code.equals(AppConstant.SUCCESS)) {
                                Toast.makeText(EcardActivity.this, "解除挂失成功！", Toast.LENGTH_SHORT).show();
                                if (password != null) {
                                    //关闭输入密码框
                                    hideNotLostAlertAnim();
                                }
                                swiperefreshlayout.setRefreshing(true);
                                presenter.loadEcardData(true);
                            }
                        } catch (JsonSyntaxException e) {
                            e.printStackTrace();
                            if (password != null) {
                                tvNotLostErrText.setText(e.getMessage());
                            } else {
                                Toast.makeText(EcardActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    private void showNotLostAlertAnim(String type) {
        notLostView.setVisibility(View.VISIBLE);
        if (type.equals("normal")) {
            edLostPassword.setHint("请重新输登陆密码");
        } else if (type.equals("password")) {
            edLostPassword.setHint("请重新输入一卡通密码");
        }
        int halfScreen = (metrics.heightPixels / 2) + 200;
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(notLostView, "translationY", 0, halfScreen + 50);
        animator2.setDuration(600);
        animator2.start();
        grayBg.setVisibility(View.VISIBLE);
        grayBg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void hideNotLostAlertAnim() {
        int halfScreen = (metrics.heightPixels / 2) + 200;
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(notLostView, "translationY", halfScreen + 50, 0);
        animator2.setDuration(600);
        animator2.start();
        grayBg.setVisibility(View.GONE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                notLostView.setVisibility(View.GONE);
            }
        }, 590);
    }


    public static class lostResponse extends RealmObject {
        public BaseResponse resp;
    }

}
