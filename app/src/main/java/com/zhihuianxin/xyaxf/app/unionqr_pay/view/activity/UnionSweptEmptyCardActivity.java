package com.zhihuianxin.xyaxf.app.unionqr_pay.view.activity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhihuianxin.xyaxf.app.AppConstant;
import modellib.service.CustomerService;
import modellib.service.MeService;
import modellib.thrift.fee.PaymentRecord;
import modellib.thrift.payment.PaymentOrder;
import modellib.thrift.unqr.PaymentConfig;
import modellib.thrift.unqr.RealName;
import modellib.thrift.unqr.RealNameAuthStatus;
import modellib.thrift.unqr.UPBankCard;
import com.google.gson.Gson;
import com.zhihuianxin.xyaxf.App;
import com.zhihuianxin.xyaxf.BaseRealmActionBarActivity;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.ApiFactory;
import com.zhihuianxin.xyaxf.app.BaseSubscriber;
import com.zhihuianxin.xyaxf.app.RetrofitFactory;
import com.zhihuianxin.xyaxf.app.WebPageActivity;
import com.zhihuianxin.xyaxf.app.me.view.activity.MePaySettingActivity;
import com.zhihuianxin.xyaxf.app.me.view.activity.MeStuAccountModifyActivity;
import com.zhihuianxin.xyaxf.app.unionqr_pay.contract.IunionSweptContract;
import com.zhihuianxin.xyaxf.app.unionqr_pay.presenter.UnionPayPwdPresenter;
import com.zhihuianxin.xyaxf.app.unionqr_pay.presenter.UnionSweptMainPresenter;
import com.zhihuianxin.xyaxf.app.unionqr_pay.view.fragment.UnionCashierFragment;
import com.zhihuianxin.xyaxf.app.utils.NetUtils;
import com.zhihuianxin.xyaxf.app.utils.RxBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmList;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Vincent on 2017/12/4.
 */

public class UnionSweptEmptyCardActivity extends BaseRealmActionBarActivity implements IunionSweptContract.IunionSweptView {
    public static final String EXTRA_FROM_EMPTY = "fromEmptyCard";
    @InjectView(R.id.pointShowView)
    View pointShowView;
    @InjectView(R.id.grayBg)
    View mGrayBg;
    @InjectView(R.id.check)
    CheckBox check;
    @InjectView(R.id.link_xieyi)
    TextView linkXieyi;
    @InjectView(R.id.xieyi1)
    RelativeLayout xieyi1;
    @InjectView(R.id.xieyi2)
    RelativeLayout xieyi2;

    @InjectView(R.id.content1)
    TextView content1;
    @InjectView(R.id.content2)
    TextView content2;

    @InjectView(R.id.next)
    Button next;
    @InjectView(R.id.tips_text)
    TextView tipsText;
    @InjectView(R.id.service_window)
    LinearLayout serviceWindow;
    @InjectView(R.id.know)
    Button know;
    @InjectView(R.id.pointView)
    RelativeLayout pointView;

    private IunionSweptContract.IunionSweptPresenter presenter;

    private boolean priceTag = false;
    private boolean NoMore = false;
    private boolean show = false;

    private DisplayMetrics metrics;

    private Subscription rxSubscription;

    @Override
    protected int getContentViewId() {
        return R.layout.union_swept_emptycard_activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ButterKnife.inject(this);
        new UnionSweptMainPresenter(this, this);


        if (getIntent().getExtras() != null) {
            NoMore = getIntent().getExtras().getBoolean("no_more");
            if (NoMore) {
                pointView.setVisibility(View.GONE);
            }
        }

        if (getIntent().getExtras() != null) {
            show = getIntent().getExtras().getBoolean("show");
            if (show) {
                pointView.setVisibility(View.VISIBLE);
            }
        }

        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        findViewById(R.id.action_bar).setVisibility(View.GONE);
        mGrayBg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        linkXieyi.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
        linkXieyi.getPaint().setAntiAlias(true);//抗锯齿

        if (App.protocols.size() != 0) {
            try {
                content1.setText(App.protocols.get(0).name);
                content2.setText(App.protocols.get(1).name);
            } catch (Exception e) {
                e.printStackTrace();
            }

            linkXieyi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showNotLostAlertAnim();
                    xieyi1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getActivity(), WebPageActivity.class);
                            intent.putExtra(WebPageActivity.EXTRA_URL, App.protocols.get(0).content);
                            intent.putExtra(WebPageActivity.EXTRA_TITLE, "服务协议");
                            getActivity().startActivity(intent);
                            hideNotLostAlertAnim();
                        }
                    });

                    xieyi2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getActivity(), WebPageActivity.class);
                            intent.putExtra(WebPageActivity.EXTRA_URL, App.protocols.get(1).content);
                            intent.putExtra(WebPageActivity.EXTRA_TITLE, "服务协议");
                            getActivity().startActivity(intent);
                            hideNotLostAlertAnim();
                        }
                    });
                }
            });

            check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        next.setEnabled(true);
                        tipsText.setVisibility(View.GONE);
                    } else {
                        next.setEnabled(false);
                        tipsText.setVisibility(View.VISIBLE);
                    }
                }
            });

            know.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    hideNotLostAlertAnim();
                }
            });
        }
    }

    @OnClick(R.id.next)
    public void addCardOnClick() {
        commit_protocol();
//        final RealmResults<Customer> customers = realm.where(Customer.class).equalTo("mobile", App.mAxLoginSp.getUserMobil()).findAll();
//
//        if (customers.size() != 0) {
//            if (((CustomerBaseInfoRealmProxy) ((CustomerRealmProxy) customers.get(0)).realmGet$base_info()).realmGet$protocol() != null) {
//                if (((CustomerBaseInfoRealmProxy) ((CustomerRealmProxy) customers.get(0)).realmGet$base_info()).realmGet$protocol().size() > 0) {
//                    //存在同意了的协议
//                    presenter.JudgePayPwd();
//                } else {
//                    Intent intent = new Intent(this, UnionServiceProActivity.class);
//                    Bundle bundle = new Bundle();
//                    bundle.putString(UnionServiceProActivity.EXTRA_SHOW_BTN, "1");
//                    intent.putExtras(bundle);
//                    startActivityForResult(intent, UnionServiceProActivity.REQUEST_SURE_PRO);
//                }
//            }
//        }
//        Intent intent = new Intent(this,UnionServiceProActivity.class);
//        Bundle bundle = new Bundle();
//        bundle.putString(UnionServiceProActivity.EXTRA_SHOW_BTN,"1");
//        intent.putExtras(bundle);
//        startActivityForResult(intent,UnionServiceProActivity.REQUEST_SURE_PRO);
    }


    private void commit_protocol() {
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String, Object> map = new HashMap<>();
        final List<String> list = new ArrayList<>();
        list.add("UP-QRFW-2018.01-01");
        list.add("UP-QRKJZF-2018.01-01");
        map.put("protocol_nos", list);
        CustomerService customerService = ApiFactory.getFactory().create(CustomerService.class);
        customerService.commit_protocol(NetUtils.getRequestParams(this, map), NetUtils.getSign(NetUtils.getRequestParams(this, map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(UnionSweptEmptyCardActivity.this, true, null) {

                    @Override
                    public void onNext(Object o) {
                        getRealName();
                        getNewCustomerAndUpdate();
                    }
                });
    }

    private void getNewCustomerAndUpdate() {
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String, Object> map = new HashMap<>();
        MeService meService = ApiFactory.getFactory().create(MeService.class);
        meService.getCustomer(NetUtils.getRequestParams(this, map), NetUtils.getSign(NetUtils.getRequestParams(this, map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(this, true, null) {

                    @Override
                    public void onNext(Object o) {
                        final MeStuAccountModifyActivity.GetCustomer response = new Gson().fromJson(o.toString(), MeStuAccountModifyActivity.GetCustomer.class);
                        realm.executeTransactionAsync(new Realm.Transaction() {
                            @Override
                            public void execute(Realm bgRealm) {
                                response.customer.mobile = App.mAxLoginSp.getUserMobil();
                                bgRealm.copyToRealmOrUpdate(response.customer);
                            }
                        }, new Realm.Transaction.OnSuccess() {
                            @Override
                            public void onSuccess() {
                            }
                        }, new Realm.Transaction.OnError() {
                            @Override
                            public void onError(Throwable error) {

                            }
                        });
                    }
                });
    }

    @OnClick(R.id.backview)
    public void backViewClick(View view) {
        RxBus.getDefault().send("back");
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UnionServiceProActivity.REQUEST_SURE_PRO) {
            if (resultCode == RESULT_OK) {
                presenter.JudgePayPwd();
            }
        }
    }

    @OnClick(R.id.pointView)
    public void pointClick(View view) {
        mGrayBg.setVisibility(View.VISIBLE);
        pointShowView.setVisibility(View.VISIBLE);
        pointShowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGrayBg.setVisibility(View.GONE);
                pointShowView.setVisibility(View.GONE);
            }
        });
    }

    @OnClick(R.id.pointClickRecordView)
    public void pointRecordClick(View view) {
        startActivity(new Intent(getActivity(), UnionSweptRecordListActivity.class));
    }

//    @OnClick(R.id.pointClickSetView)
//    public void pointSetClick(View view) {
//        priceTag = true;
//        presenter.JudgePayPwd();
//
////        startActivity(new Intent(getActivity(), MePaySettingActivity.class));
//    }

    @OnClick(R.id.pointClickShuomingView)
    public void pointShuomingClick(View view) {
        startActivity(new Intent(getActivity(), UnionSwepShuomingActivity.class));
    }

    @OnClick(R.id.pointClickCancelView)
    public void pointCancelClick(View view) {
        mGrayBg.setVisibility(View.GONE);
        pointShowView.setVisibility(View.GONE);
    }


    @Override
    public boolean leftButtonEnabled() {
        return true;
    }

    @Override
    public void onLeftButtonClick(View view) {
        finish();
    }

    @Override
    public void JudgePayPwdResult(PaymentConfig config) {
        App.addActivities(this);
        if (config.has_pay_password) {

            if (priceTag) { //设置小额免密先判断是否设置过支付密码
                startActivity(new Intent(getActivity(), MePaySettingActivity.class));
            }

            Intent i = new Intent(UnionSweptEmptyCardActivity.this,UnionHtmlActivity.class);
            Bundle bundle = new Bundle();
            bundle.putBoolean(UnionHtmlActivity.EXTRA_FROM_UPQRCASHIER,false);
            bundle.putBoolean("isOcp",true);
            if(getIntent().getExtras()!=null && getIntent().getExtras().getBoolean(UnionHtmlActivity.EXTRA_FROM_BANKCARDLIST)){
                bundle.putBoolean(UnionHtmlActivity.EXTRA_FROM_BANKCARDLIST,true);
            }
            if(getIntent().getExtras()!=null && getIntent().getExtras().getString(UnionSweptEmptyCardActivity.EXTRA_FROM_EMPTY)!=null){
                bundle.putString(UnionSweptEmptyCardActivity.EXTRA_FROM_EMPTY,getIntent().getExtras().getString(UnionSweptEmptyCardActivity.EXTRA_FROM_EMPTY));
            }
            i.putExtras(bundle);
            startActivity(i);
            finish();
        } else {
            Intent i = new Intent(getActivity(), UnionSetPayPwdActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString(EXTRA_FROM_EMPTY, "1");
            bundle.putBoolean("priceTag", true);
            if (getIntent().getExtras() != null && getIntent().getExtras().getSerializable(UnionCashierFragment.EXTRA_ENTITY) != null)
                bundle.putSerializable(UnionCashierFragment.EXTRA_ENTITY, getIntent().getExtras().getSerializable(UnionCashierFragment.EXTRA_ENTITY));
            i.putExtras(bundle);
            startActivity(i);
            finish();
        }
    }

    @Override
    public void getC2BCodeResult(String qr_code) {
    }

    @Override
    public void swepPayPwdResult() {
    }

    @Override
    public void verifyPayPwdResult(boolean is_match, int left_retry_count) {
    }

    @Override
    public void payOrderResult(PaymentOrder order) {
    }

    @Override
    public void setPayList(RealmList<PaymentRecord> payList) {
    }

    @Override
    public void getRealNameResult(RealName realName) {
    }

    @Override
    public void c2bQrVerifyPpaymentPasswordResult(boolean is_match, int left_retry_count) {
    }

    @Override
    public void getBankCardResult(RealmList<UPBankCard> bankCards) {
    }

    @Override
    public void setPresenter(IunionSweptContract.IunionSweptPresenter presenter) {
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
    }


    private void showNotLostAlertAnim() {
        serviceWindow.setVisibility(View.VISIBLE);
        int halfScreen = (metrics.heightPixels / 2) + 200;
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(serviceWindow, "translationY", 0, halfScreen + 50);
        animator2.setDuration(600);
        animator2.start();
        mGrayBg.setVisibility(View.VISIBLE);
        mGrayBg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void hideNotLostAlertAnim() {
        int halfScreen = (metrics.heightPixels / 2) + 200;
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(serviceWindow, "translationY", halfScreen + 50, 0);
        animator2.setDuration(600);
        animator2.start();
        mGrayBg.setVisibility(View.GONE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                serviceWindow.setVisibility(View.GONE);
            }
        }, 590);
    }

    @Override
    protected void onResume() {
        super.onResume();
        rxSubscription = RxBus.getDefault().toObservable().subscribe(new Action1<Object>() {
            @Override
            public void call(Object event) {
                if (event.equals("finish")) {
                    finish();
                }
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (rxSubscription != null)
            rxSubscription.unsubscribe();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            RxBus.getDefault().send("back");
            finish();
            return false;
        }
        return super.onKeyDown(keyCode, event);
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
                                presenter.JudgePayPwd();
                            } else if(realName.status.equals(RealNameAuthStatus.FAILED.name())){

                            } else if(realName.status.equals(RealNameAuthStatus.NotAuth.name())){
                                Intent i = new Intent(UnionSweptEmptyCardActivity.this,UnionCertificationActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putBoolean(UnionHtmlActivity.EXTRA_FROM_UPQRCASHIER,true);
                                bundle.putBoolean("isOcp",true);
                                if(getIntent().getExtras()!=null && getIntent().getExtras().getString(UnionSweptEmptyCardActivity.EXTRA_FROM_EMPTY)!=null){
                                    bundle.putString(UnionSweptEmptyCardActivity.EXTRA_FROM_EMPTY,getIntent().getExtras().getString(UnionSweptEmptyCardActivity.EXTRA_FROM_EMPTY));
                                }
                                i.putExtras(bundle);
                                startActivity(i);
                                finish();
                            } else{// Pending

                            }
                        }

                    }
                });
    }
}
