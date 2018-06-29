package com.zhihuianxin.xyaxf.app.unionqr_pay.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zhihuianxin.xyaxf.app.AppConstant;
import modellib.service.CustomerService;
import modellib.service.PaymentService;
import modellib.service.UPQRService;
import modellib.thrift.base.PayChannel;
import modellib.thrift.base.PayMethod;
import modellib.thrift.payment.PaymentOrder;
import modellib.thrift.payment.UPQRPayChannelRequest;
import modellib.thrift.unqr.PaymentConfig;
import modellib.thrift.unqr.RealName;
import modellib.thrift.unqr.RealNameAuthStatus;
import modellib.thrift.unqr.UPBankCard;
import modellib.thrift.unqr.UPCoupon;
import modellib.thrift.unqr.UPQROrder;
import modellib.thrift.unqr.UPQROrderType;
import com.google.gson.Gson;
import com.xyaxf.axpay.modle.PayFor;
import com.xyaxf.axpay.modle.PayRequest;
import com.xyaxf.axpay.modle.UPQRPayRequestData;
import com.zhihuianxin.xyaxf.app.base.axutil.Util;
import com.zhihuianxin.secure.Secure;
import com.zhihuianxin.xyaxf.App;
import com.zhihuianxin.xyaxf.BaseRealmActionBarActivity;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.ApiFactory;
import com.zhihuianxin.xyaxf.app.BaseSubscriber;
import com.zhihuianxin.xyaxf.app.RetrofitFactory;
import com.zhihuianxin.xyaxf.app.ocp.OcpPaySucActivity;
import com.zhihuianxin.xyaxf.app.payment.CashierDeskActivity;
import com.zhihuianxin.xyaxf.app.unionqr_pay.contract.IunionQrMainContract;
import com.zhihuianxin.xyaxf.app.unionqr_pay.entity.UnionPayEntity;
import com.zhihuianxin.xyaxf.app.unionqr_pay.presenter.UnionPayPwdPresenter;
import com.zhihuianxin.xyaxf.app.unionqr_pay.presenter.UnionQrMainPresenter;
import com.zhihuianxin.xyaxf.app.unionqr_pay.view.UnionPayWayDefAdapter;
import com.zhihuianxin.xyaxf.app.unionqr_pay.view.fragment.UnionCashierFragment;
import com.zhihuianxin.xyaxf.app.unionqr_pay.view.fragment.UnionCashierInputPwdFragment;
import com.zhihuianxin.xyaxf.app.unionqr_pay.view.fragment.UnionCashierSelectBankFragment;
import com.zhihuianxin.xyaxf.app.utils.NetUtils;
import com.zhihuianxin.xyaxf.app.utils.RxBus;
import com.zhihuianxin.xyaxf.app.view.KeyBoardPwdPointView;
import com.zhihuianxin.xyaxf.app.view.UnionpayOrderErrorDialog;
import com.zhihuianxin.xyaxf.app.view.UnionpayRemarkDialog;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.realm.RealmList;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static com.zhihuianxin.xyaxf.app.me.view.activity.AddBankCardActivity.bytesToHexString;


/**
 * Created by Vincent on 2017/11/7.
 */

public class UnionPayActivity extends BaseRealmActionBarActivity
        implements UnionCashierSelectBankFragment.ITell,
        UnionCashierInputPwdFragment.IputPwdInterface,
        UnionCashierFragment.IToSelectBank,
        View.OnTouchListener,
        KeyBoardPwdPointView.OnNumberClickListener,
        IunionQrMainContract.IGetBankCardInfo {

    @InjectView(R.id.recyclerview)
    RecyclerView recyclerview;
    @InjectView(R.id.tv_mark)
    EditText tvMark;
    @InjectView(R.id.payFor)
    TextView payFor;
    @InjectView(R.id.amountviewid)
    LinearLayout amountviewid;
    @InjectView(R.id.payAmount)
    TextView payAmount;
    @InjectView(R.id.limitAmtView)
    LinearLayout limitAmtView;
    @InjectView(R.id.unlimitAmtEdit)
    EditText unlimitAmtEdit;
    @InjectView(R.id.unlimitAmtView)
    LinearLayout unlimitAmtView;
    @InjectView(R.id.amount2View)
    RelativeLayout amount2View;
    @InjectView(R.id.remarkContentTxt)
    TextView remarkContentTxt;
    @InjectView(R.id.remarkdeteilText)
    TextView remarkdeteilText;
    @InjectView(R.id.remarkViewid)
    RelativeLayout remarkViewid;
    @InjectView(R.id.rl)
    FrameLayout rl;
    private UnionCashierFragment cashierFragment;
    private UnionCashierSelectBankFragment selectBankFragment;
    private UnionCashierInputPwdFragment inputPwdFragment;
    private FragmentTransaction ft;
    private UnionPayEntity entity;
    private String amount = "";
    private UnionpayRemarkDialog dialog;
    private IunionQrMainContract.IGetBankCardInfoPresenter presenter;

    // 备注视图区
    @InjectView(R.id.remarkViewid)
    View reMarkView;
    @InjectView(R.id.limitAmtView)
    View limitView;
    // 非限制
    @InjectView(R.id.unlimitAmtView)
    View unLimitView;
    // 非限制输入框
    @InjectView(R.id.unlimitAmtEdit)
    EditText unlimitEdit;
    // 显示显示框
    @InjectView(R.id.payAmount)
    TextView limitPayAmount;
    // 收款人内容
    @InjectView(R.id.payFor)
    TextView mpayFor;

    @InjectView(R.id.am_nkv_keyboard)
    KeyBoardPwdPointView keyboardAmountView;
    @InjectView(R.id.next)
    Button mNextBtn;
    @InjectView(R.id.cashier)
    View mCashierView;
    @InjectView(R.id.container)
    FrameLayout frameLayout;
    @InjectView(R.id.topimage)
    ImageView mTopImage;

    UnionpayOrderErrorDialog payOrderErrorDialog;

    private PayMethod payMethod;

    private UnionPayWayDefAdapter unionPayWayDefAdapter;

    private Subscription rxSubscription;

    @Override
    protected int getContentViewId() {
        return R.layout.union_pay_new_activity;
    }

    @Override
    protected void onResume() {
        super.onResume();

        rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                keyboardAmountView.setVisibility(View.GONE);
            }
        });

        hintKbTwo();

        amountviewid.setFocusable(false);
        amountviewid.setFocusableInTouchMode(false);

        unlimitAmtEdit.setEnabled(true);
        unlimitAmtEdit.setFocusableInTouchMode(true);
        unlimitAmtEdit.setFocusable(true);

        hintKbTwo();

        unlimitAmtEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hintKbTwo();
                keyboardAmountView.setVisibility(View.VISIBLE);
            }
        });

        tvMark.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    keyboardAmountView.setVisibility(View.GONE);
                } else {
                    hintKbTwo();
                    keyboardAmountView.setVisibility(View.VISIBLE);
                }
            }
        });

        unlimitAmtEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    keyboardAmountView.setVisibility(View.VISIBLE);
                    hintKbTwo();
                } else {
                    keyboardAmountView.setVisibility(View.GONE);
                }
            }
        });

        rxSubscription = RxBus.getDefault().toObservable().subscribe(new Action1<Object>() {
            @Override
            public void call(Object event) {
                if (event.equals("fixed_activity_add_bank_def")) {
                    //重新加载支付方式
                     Log.d("UnionPayActivity", "收到了");
                    getOrderInfo(App.mAxLoginSp.getUnionQrCode());
                }else if (event.equals("no_add_card_and_pay")){
                    //去支付
                    payOrder2();
                }
            }
        });

        showMainView();
    }

    private void payOrder2() {
        DecimalFormat decimalFormat;
        PayRequest payRequest = new PayRequest();
        payRequest.pay_for = PayFor.UPQRPay;
        payRequest.amount = entity.getUpqrOrder().amt;
        payRequest.pay_method = new PayMethod();
        payRequest.pay_method.channel = PayChannel.UPQRPay.name();
        UPQRPayRequestData requestData = new UPQRPayRequestData();
        requestData.tn = entity.getUpqrOrder().tn;
        requestData.orig_amt = entity.getUpqrOrder().amt;
        if(entity.getUpCoupon() != null && !Util.isEmpty(entity.getUpCoupon().offst_amt)){
            decimalFormat = new DecimalFormat(".00");
            requestData.amt = decimalFormat.format(Double.parseDouble(entity.getUpqrOrder().amt) - Double.parseDouble(entity.getUpCoupon().offst_amt));
        } else{
            requestData.amt = entity.getUpqrOrder().amt;
        }
        requestData.bank_card_id = getSelectedBank();
        ArrayList<UPCoupon> coupons = new ArrayList<>();
        if(entity.getUpCoupon() != null){
            coupons.add(entity.getUpCoupon());
        }
        requestData.coupons = coupons;
        requestData.payer_comments = App.mAxLoginSp.getUnionReMark();
        requestData.qr_code = App.mAxLoginSp.getUnionQrCode();
        payRequest.request_data = requestData;


        UPBankCard upBankCard = entity.getBankCards().get(0);
        for(int i = 0;i < entity.getBankCards().size();i++){
            if(App.mAxLoginSp.getUnionSelBankId().equals(entity.getBankCards().get(i).getId())){
                upBankCard = entity.getBankCards().get(i);
            }
        }
        payRequest.pay_method.card = new modellib.thrift.unqr.UPBankCard();
        payRequest.pay_method.card.setCard_no(upBankCard.getCard_no());
        payRequest.pay_method.card.setIss_ins_name(upBankCard.getIss_ins_name());
        payRequest.pay_method.card.setIss_ins_icon(upBankCard.getIss_ins_icon());
        payRequest.pay_method.card.setIss_ins_code(upBankCard.getIss_ins_code());
        payRequest.pay_method.card.setId(upBankCard.getId());
        payRequest.pay_method.card.setCard_type_name(upBankCard.getCard_type_name());

        UPQRQuickPayChannelRequest upqrQuickPayChannelRequest = new UPQRQuickPayChannelRequest();
        upqrQuickPayChannelRequest.bank_card_code = getSelectedBank();
        try {
            upqrQuickPayChannelRequest.pay_password = bytesToHexString(Secure.encodeMessageField((App.mAxLoginSp.getUnionPayPwd()).getBytes("UTF-8")));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        payRequest.channel_request_data = upqrQuickPayChannelRequest;

        payOrder(payRequest);
    }

    public class UPQRQuickPayChannelRequest {
        public String pay_password;        // 支付密码, aes128加密
        public String bank_card_code;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);

        recyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true));
        recyclerview.setHasFixedSize(true);

        initData();

        initViews();

        payOrderErrorDialog = new UnionpayOrderErrorDialog(getActivity());
        payOrderErrorDialog.setOnPayOrderErrorListener(new UnionpayOrderErrorDialog.OnPayOrderErrorListener() {
            @Override
            public void canel() {
                finish();
            }

            @Override
            public void changeOtherCard() {
                payOrderErrorDialog.dismiss();
            }
        });


        rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                keyboardAmountView.setVisibility(View.GONE);
            }
        });

        hintKbTwo();

        rl.setFocusable(false);
        rl.setFocusableInTouchMode(false);

        unlimitAmtEdit.setEnabled(true);
        unlimitAmtEdit.setFocusableInTouchMode(true);
        unlimitAmtEdit.setFocusable(true);

        hintKbTwo();

        unlimitAmtEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hintKbTwo();
                keyboardAmountView.setVisibility(View.VISIBLE);
            }
        });

        tvMark.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    keyboardAmountView.setVisibility(View.GONE);
                } else {
                    hintKbTwo();
                    keyboardAmountView.setVisibility(View.VISIBLE);
                }
            }
        });

        unlimitAmtEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    keyboardAmountView.setVisibility(View.VISIBLE);
                    hintKbTwo();
                } else {
                    keyboardAmountView.setVisibility(View.GONE);
                }
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

    private void initViews() {
        hideSoftKeyboard();
        new UnionQrMainPresenter(this, this);
        dialog = new UnionpayRemarkDialog(this);
        //dialog.create();

        keyboardAmountView.setOnNumberClickListener(this);
        unLimitView.setVisibility(View.VISIBLE);
        keyboardAmountView.setVisibility(View.VISIBLE);

        mNextBtn.setOnClickListener(nextBtnOnClickListener);
        unlimitEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(unlimitEdit.getWindowToken(), 0);
            }
        });

        dialog.setOnNextListener(new UnionpayRemarkDialog.OnNextListener() {
            @Override
            public void onNext(String data) {
                App.mAxLoginSp.setUnionReMark(data);
                unlimitEdit.setFocusable(false);
                unlimitEdit.setFocusableInTouchMode(false);

                hideSoftKeyboard();

            }
        });

        mpayFor.setText(entity.getUpqrOrder().payee_info.name);
        // 到商户
        if (entity.getUpqrOrder().order_type.equals(UPQROrderType.NormalConsumption.toString()) ||
                entity.getUpqrOrder().order_type.equals(UPQROrderType.RestrictCreditConsumption.toString()) ||
                entity.getUpqrOrder().order_type.equals(UPQROrderType.MiniMerchantConsumption.toString()) ||
                entity.getUpqrOrder().order_type.equals(UPQROrderType.ATMEnchashment.toString())) {
            reMarkView.setVisibility(View.VISIBLE);
            mTopImage.setBackgroundResource(R.drawable.union_mer_cashiicon);
        } else {// 到人
            reMarkView.setVisibility(View.VISIBLE);
            mTopImage.setBackgroundResource(R.drawable.union_peo_cashiicon);
        }

        if (Util.isEmpty(entity.getUpqrOrder().amt) || entity.getUpqrOrder().amt.equals("0")) {// 不定额
            unLimitView.setVisibility(View.VISIBLE);
            limitView.setVisibility(View.GONE);
            mNextBtn.setVisibility(View.VISIBLE);
            keyboardAmountView.setVisibility(View.VISIBLE);
        } else {// 定额
            unLimitView.setVisibility(View.GONE);
            limitView.setVisibility(View.VISIBLE);
            mNextBtn.setVisibility(View.VISIBLE);
            keyboardAmountView.setVisibility(View.GONE);

            limitPayAmount.setText(entity.getUpqrOrder().amt);
        }

        unlimitEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence != null && !TextUtils.isEmpty(charSequence)) {
                    mNextBtn.setText("确认付款：" + Float.parseFloat(unlimitEdit.getText().toString().trim()) + "元");
                } else {
                    mNextBtn.setText("确认付款");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void initData() {
        this.entity = (UnionPayEntity) getIntent().getExtras().getSerializable(UnionCashierFragment.EXTRA_ENTITY);

        if (getIntent().getExtras().getBoolean(UnionCashierFragment.EXTRA_SHOW_UNIONCASHIER, false)) {
            prepareToShowCashier();
        }

        /**
         * 默认的支付方式列表
         */
        final ArrayList<PayMethod> defpayMethods = new ArrayList<>();
        defpayMethods.addAll(entity.getUpqrOrder().pay_methods);
        //剔除不需要的数据
        Iterator<PayMethod> it = defpayMethods.iterator();
        while (it.hasNext()) {
            PayMethod x = it.next();
            if (!x.is_default) {
                it.remove();
            }
        }

        if (defpayMethods.size() == 0) {
            if (entity.getUpqrOrder().pay_methods.size() != 0)
                defpayMethods.add(entity.getUpqrOrder().pay_methods.get(0));
        }
        payMethod = defpayMethods.get(0);
        unionPayWayDefAdapter = new UnionPayWayDefAdapter(this, entity.getUpqrOrder().pay_methods, defpayMethods, R.layout.ocp_pay_method_item);
        recyclerview.setAdapter(unionPayWayDefAdapter);
        unionPayWayDefAdapter.notifyDataSetChanged();
        unionPayWayDefAdapter.getFristPayWay(new UnionPayWayDefAdapter.fristSelectPayWay() {
            @Override
            public void way(PayMethod item) {
                payMethod = item;
                final ArrayList<UPBankCard> bankCards = new ArrayList<>();
                bankCards.clear();
                bankCards.add(item.card);
                entity.setBankCards(bankCards);
                unionPayWayDefAdapter.clear();
                unionPayWayDefAdapter.add(item);
//                unionPayWayDefAdapter = new UnionPayWayDefAdapter(UnionPayActivity.this, entity.getUpqrOrder().pay_methods, defpayMethods2, R.layout.ocp_pay_method_item);
//                recyclerview.setAdapter(unionPayWayDefAdapter);
                unionPayWayDefAdapter.notifyDataSetChanged();
            }
        });

    }

    private void initFragments() {
        cashierFragment = (UnionCashierFragment) UnionCashierFragment.newInstance(entity);
        cashierFragment.setItoSelectBank(this);
        selectBankFragment = (UnionCashierSelectBankFragment) UnionCashierSelectBankFragment.newInstance(entity);
        selectBankFragment.setItell(this);
        inputPwdFragment = (UnionCashierInputPwdFragment) UnionCashierInputPwdFragment.newInstance(entity);
        inputPwdFragment.setIputPwdInterface(this);

        FragmentManager manager = getSupportFragmentManager();
        ft = manager.beginTransaction();
        if (!cashierFragment.isAdded()) {
            ft.add(R.id.container, cashierFragment);
        }
        if (!selectBankFragment.isAdded()) {
            ft.add(R.id.container, selectBankFragment);
        }
        if (!inputPwdFragment.isAdded()) {
            ft.add(R.id.container, inputPwdFragment);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null) {
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
        return super.onTouchEvent(event);
    }

    /**
     * Hides the soft keyboard
     */
    public void hideSoftKeyboard() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    View.OnClickListener nextBtnOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (limitAmtView.getVisibility()!=View.VISIBLE&&TextUtils.isEmpty(unlimitEdit.getText().toString().trim())) {
                Toast.makeText(UnionPayActivity.this, "请输入金额", Toast.LENGTH_LONG).show();
                return;
            }

            if (limitAmtView.getVisibility()!=View.VISIBLE&&Float.parseFloat(unlimitEdit.getText().toString().trim()) == 0) {
                Toast.makeText(UnionPayActivity.this, "请输入金额", Toast.LENGTH_LONG).show();
                return;
            }

            if (limitAmtView.getVisibility()!=View.VISIBLE){
                try {
                    Float.parseFloat(unlimitEdit.getText().toString().trim());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    Toast.makeText(UnionPayActivity.this, "请输入金额", Toast.LENGTH_LONG).show();
                    return;
                }
            }

            if (payMethod.purpose != null) {
                if (App.hasBankCard) {
                    Intent intent = new Intent(UnionPayActivity.this, UnionHtmlActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(UnionHtmlActivity.EXTRA_FROM_BANKCARDLIST, false);
                    intent.putExtras(bundle);
                    UnionPayActivity.this.startActivity(new Intent(intent));
                } else {
                    UnionPayActivity.this.startActivity(new Intent(UnionPayActivity.this, UnionSweptEmptyCardActivity.class));
                }
            } else {
                if (limitAmtView.getVisibility()!=View.VISIBLE&&unlimitEdit.getText().toString().trim().length() == 0) {
                    Toast.makeText(UnionPayActivity.this, "请输入金额", Toast.LENGTH_LONG).show();
                    return;
                }
                if (limitAmtView.getVisibility()!=View.VISIBLE&&unlimitEdit.getText().toString().trim().equals("0")) {
                    Toast.makeText(UnionPayActivity.this, "金额不能为零", Toast.LENGTH_LONG).show();
                    return;
                }
                prepareToShowCashier();
            }
            if (!TextUtils.isEmpty(tvMark.getText().toString().trim()))
                App.mAxLoginSp.setUnionReMark(tvMark.getText().toString().trim());

        }
    };

    private void showCashierFrag() {
        mCashierView.setVisibility(View.VISIBLE);
        initFragments();
        ft.hide(selectBankFragment);
        ft.hide(inputPwdFragment);
        ft.show(cashierFragment).commitAllowingStateLoss();
    }

    private void showSelectBankFrag() {
        initFragments();
        ft.hide(cashierFragment);
        ft.hide(inputPwdFragment);
        ft.show(selectBankFragment).commitAllowingStateLoss();
    }

    private void showInputPwdFrag() {
        mCashierView.setVisibility(View.VISIBLE);
        initFragments();
        ft.hide(cashierFragment);
        ft.hide(selectBankFragment);
        ft.show(inputPwdFragment).commitAllowingStateLoss();
    }

    @Override
    public void back() {
        prepareToShowCashier();
    }

    @Override
    public void finishActivity() {
        //addThisToActivityManager();
    }

    @Override
    public void gotoSelectBank() {
        addThisToActivityManager();

        if (entity.getBankCards().size() == 0) {// 第一次添加银行卡
            Intent i = new Intent(this, UnionSweptEmptyCardActivity.class);
            Bundle b = new Bundle();
            b.putSerializable(UnionCashierFragment.EXTRA_ENTITY, entity);
            i.putExtras(b);
            startActivity(i);
        } else {
            showSelectBankFrag();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (selectBankFragment.isHidden()) {// 是第一次添加银行卡
            if (requestCode == UnionServiceProActivity.REQUEST_SURE_PRO) {
                if (resultCode == RESULT_OK) {
                    presenter.JudgePayPwd();// 判断是否设置了支付密码
                }
            }
        } // else 不是第一次添加，要回到selectBankFragment去处理
    }

    @Override
    public void close(UnionPayEntity entity) {
        this.entity = entity;// 收银台Fragment会更新银行卡信息
        mCashierView.setVisibility(View.GONE);
    }

    @Override
    public void gotoPay() {
        showInputPwdFrag();
    }

    @Override
    public void putPwdBack() {
        showMainView();
    }

    @Override
    public void showMainView() {
        App.mAxLoginSp.setReGetUPQR("1");

        mCashierView.setVisibility(View.GONE);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return true;
    }

    private void prepareToShowCashier() {
        if (unLimitView.getVisibility() == View.VISIBLE) {
            entity.getUpqrOrder().amt = new DecimalFormat("0.00").format(Float.parseFloat(unlimitEdit.getText().toString().trim()));
        }
        if (payMethod != null) {
            if (payMethod.payment_config != null) {
                if (payMethod.payment_config.pin_free && Float.parseFloat(unlimitEdit.getText().toString().trim()) <= Float.parseFloat(payMethod.payment_config.pin_free_amount) && Float.parseFloat(unlimitEdit.getText().toString().trim()) <= Float.parseFloat(payMethod.payment_config.trade_limit_per_day)) {
                    payOrder();
                } else {
                    showInputPwdFrag();
                }
            }
        }
    }

    private void setEditContain() {
        unlimitEdit.setText(amount);
        unlimitEdit.setSelection(unlimitEdit.getText().length());
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
            if (amount.length() > 6) {
                return;
            } else {
                amount += number;
            }
        }
        setEditContain();
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

    private void payOrder() {
        DecimalFormat decimalFormat;
        PayRequest payRequest = new PayRequest();
        payRequest.pay_for = PayFor.UPQRPay;
        payRequest.amount = entity.getUpqrOrder().amt;
        payRequest.pay_method = new PayMethod();
        payRequest.pay_method.channel = PayChannel.UPQRPay.name();
        UPQRPayRequestData requestData = new UPQRPayRequestData();
        requestData.tn = entity.getUpqrOrder().tn;
        requestData.orig_amt = entity.getUpqrOrder().amt;
        if (entity.getUpCoupon() != null && !Util.isEmpty(entity.getUpCoupon().offst_amt)) {
            decimalFormat = new DecimalFormat(".00");
            requestData.amt = decimalFormat.format(Double.parseDouble(entity.getUpqrOrder().amt) - Double.parseDouble(entity.getUpCoupon().offst_amt));
        } else {
            requestData.amt = entity.getUpqrOrder().amt;
        }
        requestData.bank_card_id = getSelectedBank();
        ArrayList<UPCoupon> coupons = new ArrayList<>();
        if (entity.getUpCoupon() != null) {
            coupons.add(entity.getUpCoupon());
        }
        requestData.coupons = coupons;
        requestData.payer_comments = App.mAxLoginSp.getUnionReMark();
        requestData.qr_code = App.mAxLoginSp.getUnionQrCode();
        payRequest.request_data = requestData;


        UPBankCard upBankCard = entity.getBankCards().get(0);
        for(int i = 0;i < entity.getBankCards().size();i++){
            if(App.mAxLoginSp.getUnionSelBankId().equals(entity.getBankCards().get(i).getId())){
                upBankCard = entity.getBankCards().get(i);
            }
        }
        payRequest.pay_method.card = new modellib.thrift.unqr.UPBankCard();
        payRequest.pay_method.card.setCard_no(upBankCard.getCard_no());
        payRequest.pay_method.card.setIss_ins_name(upBankCard.getIss_ins_name());
        payRequest.pay_method.card.setIss_ins_icon(upBankCard.getIss_ins_icon());
        payRequest.pay_method.card.setIss_ins_code(upBankCard.getIss_ins_code());
        payRequest.pay_method.card.setId(upBankCard.getId());
        payRequest.pay_method.card.setCard_type_name(upBankCard.getCard_type_name());

        UPQRPayChannelRequest upqrPayChannelRequest = new UPQRPayChannelRequest();
        upqrPayChannelRequest.bank_card_code = payMethod.card.getId();
        payRequest.channel_request_data = upqrPayChannelRequest;
        payOrder(payRequest);

    }

    private String getSelectedBank() {
        String result = "";
        for (int i = 0; i < entity.getBankCards().size(); i++) {
            if (entity.getBankCards().get(i).getId().equals(App.mAxLoginSp.getUnionSelBankId())) {
                result = entity.getBankCards().get(i).getId();
            }
        }
        if (Util.isEmpty(result)) {
            result = entity.getBankCards().get(0).getId();
        }
        return result;
    }

    public void payOrder(PayRequest payRequest) {
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String, Object> map = new HashMap<>();
        map.put("pay_req", payRequest);

        PaymentService meService = ApiFactory.getFactory().create(PaymentService.class);
        meService.payOder(NetUtils.getRequestParams(this, map), NetUtils.getSign(NetUtils.getRequestParams(this, map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(this, true, null) {

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        CashierDeskActivity.PayOderResponse payOderResponse = new CashierDeskActivity.PayOderResponse();
                        payOderResponse.order = new PaymentOrder();
                        payOderResponse.order.error_msg = e.getMessage();
                        payOrderResult(payOderResponse.order);
                    }

                    @Override
                    public void onNext(Object o) {
                        CashierDeskActivity.PayOderResponse payOderResponse = new Gson().fromJson(o.toString(), CashierDeskActivity.PayOderResponse.class);
                        payOrderResult(payOderResponse.order);
                    }
                });
    }

    private void payOrderResult(PaymentOrder order) {
        if (Util.isEmpty(order.error_msg)) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("PaymentOrder",order);
            bundle.putSerializable("pay_name",entity.getUpqrOrder().payee_info.name);
            Intent intent = new Intent(this, OcpPaySucActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);

            getActivity().finish();
        } else {
            App.mAxLoginSp.setReGetUPQR("1");
            if (order.error_msg.contains(AppConstant.UPQR_PAY_PWD_ERROR)) {
                payOrderErrorDialog.show();
                payOrderErrorDialog.setText(order.error_msg);
            } else {
                // 其他情况在底层类（basesubscriber）进行toast提示
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mCashierView.getVisibility() == View.GONE) {
                return super.onKeyDown(keyCode, event);
            } else {
                return false;
            }

        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void getUpQrCouponResult(UPCoupon upCoupon) {
        entity.setUpCoupon(upCoupon);
        if (payMethod != null) {
            if (payMethod.payment_config != null) {
                if (payMethod.payment_config.pin_free && Float.parseFloat(unlimitEdit.getText().toString().trim()) <= Float.parseFloat(payMethod.payment_config.pin_free_amount) && Float.parseFloat(unlimitEdit.getText().toString().trim()) <= Float.parseFloat(payMethod.payment_config.trade_limit_per_day)) {
                    payOrder();
                } else {
                    showInputPwdFrag();
                }
            }
        }

    }

    @Override
    public void judgePayPwdResult(PaymentConfig config) {
        if (config.has_pay_password) {
            Intent i = new Intent(this, UnionInputPayPwdActivity.class);
            Bundle b = new Bundle();
            b.putSerializable(UnionCashierFragment.EXTRA_ENTITY, entity);
            i.putExtras(b);
            startActivity(i);
        } else {
            Intent i = new Intent(this, UnionSetPayPwdActivity.class);
            Bundle b = new Bundle();
            b.putSerializable(UnionCashierFragment.EXTRA_ENTITY, entity);
            i.putExtras(b);
            startActivity(i);
        }
    }

    private void addThisToActivityManager() {
        App.addActivities(this);// 将当前activity装入
    }

    @Override
    public void setPresenter(IunionQrMainContract.IGetBankCardInfoPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void getBankCardResult(RealmList<UPBankCard> bankCards) {
        Log.d("UnionPayActivity", "收到了2");
        ArrayList<UPBankCard> bankCardsNew = new ArrayList<>();
        for (int i = 0; i < bankCards.size(); i++) {
            UPBankCard bankCard = new UPBankCard();
            bankCard.setId(Util.isEmpty(bankCards.get(i).getId()) ? "" : bankCards.get(i).getId());
            bankCard.setIss_ins_name(Util.isEmpty(bankCards.get(i).getIss_ins_name()) ? "" : bankCards.get(i).getIss_ins_name());
            bankCard.setCard_no(Util.isEmpty(bankCards.get(i).getCard_no()) ? "" : "**** **** **** " + bankCards.get(i).getCard_no());
            bankCard.setCard_type_name(Util.isEmpty(bankCards.get(i).getCard_type_name()) ? "" : bankCards.get(i).getCard_type_name());
            bankCard.setIss_ins_icon(Util.isEmpty(bankCards.get(i).getIss_ins_icon()) ? "" : bankCards.get(i).getIss_ins_icon());
            bankCardsNew.add(bankCard);
        }
        if (entity == null) {
            entity = new UnionPayEntity();
        } else {
            entity.getBankCards().clear();
        }
        entity.setBankCards(bankCardsNew);
        /**
         * 默认的支付方式列表
         */
        final ArrayList<PayMethod> defpayMethods = new ArrayList<>();
        defpayMethods.addAll(entity.getUpqrOrder().pay_methods);
        //剔除不需要的数据
        Iterator<PayMethod> it = defpayMethods.iterator();
        while (it.hasNext()) {
            PayMethod x = it.next();
            if (!x.is_default) {
                it.remove();
            }
        }

        if (defpayMethods.size() == 0) {
            if (entity.getUpqrOrder().pay_methods.size() != 0)
                defpayMethods.add(entity.getUpqrOrder().pay_methods.get(0));
        }
        payMethod = defpayMethods.get(0);
        unionPayWayDefAdapter = new UnionPayWayDefAdapter(this, entity.getUpqrOrder().pay_methods, defpayMethods, R.layout.ocp_pay_method_item);
        recyclerview.setAdapter(unionPayWayDefAdapter);
        unionPayWayDefAdapter.notifyDataSetChanged();
        unionPayWayDefAdapter.getFristPayWay(new UnionPayWayDefAdapter.fristSelectPayWay() {
            @Override
            public void way(PayMethod item) {
                final ArrayList<PayMethod> defpayMethods2 = new ArrayList<>();
                payMethod = item;
                defpayMethods2.add(item);
                unionPayWayDefAdapter = new UnionPayWayDefAdapter(UnionPayActivity.this, entity.getUpqrOrder().pay_methods, defpayMethods2, R.layout.ocp_pay_method_item);
                recyclerview.setAdapter(unionPayWayDefAdapter);
                unionPayWayDefAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void applyBankCardResult(String addCardUrl) {
    }

    @Override
    public void removeBankCardResult() {
    }

    @Override
    public void getUpQrOrderResult(UPQROrder upqrOrder) {
    }

    @Override
    public void loadStart() {
    }

    @Override
    public void loadError(String errorMsg) {
    }

    @Override
    public void loadComplete() {
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



    public void getRealName() {
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String,Object> map = new HashMap<>();
        CustomerService meService = ApiFactory.getFactory().create(CustomerService.class);
        meService.getRealNameQR(NetUtils.getRequestParams(this,map),NetUtils.getSign(NetUtils.getRequestParams(this,map)))
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
                        UnionPayPwdPresenter.getRealNameResponse response = new Gson().fromJson(o.toString(),UnionPayPwdPresenter.getRealNameResponse.class);
                        if (response.resp.resp_code.equals(AppConstant.SUCCESS)){
                            RealName realName =  response.realname;
                            if(realName.status.equals(RealNameAuthStatus.OK.name())){
                                Intent i = new Intent(UnionPayActivity.this,UnionHtmlActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putBoolean(UnionHtmlActivity.EXTRA_FROM_UPQRCASHIER,false);
                                i.putExtras(bundle);
                                startActivity(i);
                            } else if(realName.status.equals(RealNameAuthStatus.FAILED.name())){

                            } else if(realName.status.equals(RealNameAuthStatus.NotAuth.name())){
                                Intent i = new Intent(UnionPayActivity.this,UnionCertificationActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putBoolean(UnionHtmlActivity.EXTRA_FROM_UPQRCASHIER,true);
                                bundle.putBoolean("isOcp",true);
                                if(getIntent().getExtras()!=null && getIntent().getExtras().getString(UnionSweptEmptyCardActivity.EXTRA_FROM_EMPTY)!=null){
                                    bundle.putString(UnionSweptEmptyCardActivity.EXTRA_FROM_EMPTY,getIntent().getExtras().getString(UnionSweptEmptyCardActivity.EXTRA_FROM_EMPTY));
                                }
                                i.putExtras(bundle);
                                startActivity(i);
                            } else{// Pending

                            }
                        }

                    }
                });
    }

    public void getOrderInfo(String qrCode) {
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String, Object> map = new HashMap<>();
        map.put("qr_code", qrCode);
        UPQRService upqrService = ApiFactory.getFactory().create(UPQRService.class);
        upqrService.getUPQROrder(NetUtils.getRequestParams(UnionPayActivity.this, map), NetUtils.getSign(NetUtils.getRequestParams(UnionPayActivity.this, map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(UnionPayActivity.this, true, null) {

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                    }
                    @Override
                    public void onNext(Object o) {
                        UnionQrMainPresenter.GetUPQROrderResponse response = new Gson().fromJson(o.toString(), UnionQrMainPresenter.GetUPQROrderResponse.class);
                        if (response.order == null) {
                            finish();
                        } else {
                            entity.setUpqrOrder(response.order);
                            presenter.getBankCard();
                        }
                    }
                });
    }
}
