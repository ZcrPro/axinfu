package com.zhihuianxin.xyaxf.app.unionqr_pay.view.activity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zhihuianxin.xyaxf.app.AppConstant;
import modellib.service.PaymentService;
import modellib.thrift.base.BaseResponse;
import modellib.thrift.payment.PaymentOrder;
import modellib.thrift.unqr.PaymentConfig;
import modellib.thrift.unqr.RealName;
import modellib.thrift.unqr.RealNameAuthStatus;
import modellib.thrift.unqr.UPCoupon;
import modellib.thrift.unqr.UPQROrder;
import com.zhihuianxin.xyaxf.App;
import com.zhihuianxin.xyaxf.BaseRealmActionBarActivity;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.ApiFactory;
import com.zhihuianxin.xyaxf.app.BaseSubscriber;
import com.zhihuianxin.xyaxf.app.RetrofitFactory;
import com.zhihuianxin.xyaxf.app.unionqr_pay.contract.IunionPayPwdContract;
import com.zhihuianxin.xyaxf.app.unionqr_pay.entity.UnionPayEntity;
import com.zhihuianxin.xyaxf.app.unionqr_pay.presenter.UnionPayPwdPresenter;
import com.zhihuianxin.xyaxf.app.unionqr_pay.view.fragment.UnionCashierFragment;
import com.zhihuianxin.xyaxf.app.utils.NetUtils;
import com.zhihuianxin.xyaxf.app.utils.RxBus;
import com.zhihuianxin.xyaxf.app.view.KeyBoardPwdView;
import com.zhihuianxin.xyaxf.app.view.PasswordInputEdtView;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Vincent on 2017/11/7.
 */

public class UnionSetPayPwdActivity extends BaseRealmActionBarActivity implements
        KeyBoardPwdView.OnNumberClickListener, IunionPayPwdContract.IJudgePayPwd {

    @InjectView(R.id.edt)
    PasswordInputEdtView passwordInputEdt;
    @InjectView(R.id.next)
    Button mNextBtn;

    @InjectView(R.id.text_title)
    TextView title;
    @InjectView(R.id.am_nkv_keyboard)
    KeyBoardPwdView mNkvKeyboard;
    @InjectView(R.id.errortxt)
    TextView errorTxt;
    @InjectView(R.id.forget_pwd)
    TextView forgetPwd;
    @InjectView(R.id.grayBg)
    View grayBg;
    @InjectView(R.id.tv_content)
    TextView tvContent;
    @InjectView(R.id.btn_lost_ok)
    Button clickErrorbtn;
    @InjectView(R.id.btn_cancel)
    TextView exit;
    @InjectView(R.id.neePasswordView)
    LinearLayout neePasswordView;

    private String pwd = "";
    private UnionPayEntity entity;

    private boolean isOcp;
    private boolean priceTag;
    private boolean no_ver_name;
    private IunionPayPwdContract.IJudgePayPwdPresenter presenter;

    private DisplayMetrics metrics;
    private PaymentConfig config;

    @Override
    protected int getContentViewId() {
        return R.layout.key_board_pay_pwd;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mNextBtn.setEnabled(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        initView();
    }

    private void initView() {
        new UnionPayPwdPresenter(this, this);
        presenter.JudgePayPwd();
        if (getIntent().getExtras() != null && getIntent().getExtras().getSerializable(UnionCashierFragment.EXTRA_ENTITY) != null) {
            entity = (UnionPayEntity) getIntent().getExtras().getSerializable(UnionCashierFragment.EXTRA_ENTITY);
        }

        if (getIntent().getExtras() != null) {
            priceTag = getIntent().getExtras().getBoolean("priceTag");
        }

        if (getIntent().getExtras() != null) {
            isOcp = getIntent().getExtras().getBoolean("isOcp");
        }

        if (getIntent().getExtras() != null) {
            no_ver_name = getIntent().getExtras().getBoolean("no_ver_name");
        }

        title.setText("请设置六位数字支付密码");
        mNkvKeyboard.setOnNumberClickListener(this);

        passwordInputEdt.setInputType(InputType.TYPE_NULL);
        passwordInputEdt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordInputEdt.closeKeybord();
            }
        });
        passwordInputEdt.setOnInputOverListener(new PasswordInputEdtView.onInputOverListener() {
            @Override
            public void onInputOver(String text) {
            }
        });
    }

    @Override
    public void onNumberReturn(String number) {
        passwordInputEdt.onTextChanged(number, 0, 0, 1);
        passwordInputEdt.onTextChanged("", 0, 0, 1);
        if (pwd.length() < 6) {
            pwd += number;
            if (pwd.length() == 6) {
                setPwdOk(pwd);
            }
        }
    }

    @Override
    public void onNumberDelete() {
        passwordInputEdt.onKeyDown(67, null);
        if (pwd.length() <= 1) {
            pwd = "";
        } else {
            pwd = pwd.substring(0, pwd.length() - 1);
        }
    }

    private boolean sure = false;
    private Bundle sureBundle = null;

    private void setPwdOk(String content) {
        if (sure) {
            if (App.mAxLoginSp.getUnionPayPwd().equals(content)) {
                presenter.setPayPwd(content);
            } else {
                sure = false;
                title.setText("请设置六位数字支付密码");
                errorTxt.setText("两次输入的密码不一致");
                while (pwd.length() > 0) {
                    onNumberDelete();
                }
            }
        } else {
            errorTxt.setText("");
            sure = true;
            title.setText("请再次确认你的支付密码");
            while (pwd.length() > 0) {
                onNumberDelete();
            }
            App.mAxLoginSp.setUnionPayPwd(content);

            sureBundle = new Bundle();
            sureBundle.putSerializable(UnionCashierFragment.EXTRA_ENTITY, entity);
            if (getIntent().getExtras() != null && getIntent().getExtras().getString(UnionSweptEmptyCardActivity.EXTRA_FROM_EMPTY) != null) {
                sureBundle.putString(UnionSweptEmptyCardActivity.EXTRA_FROM_EMPTY, getIntent().getExtras().getString(UnionSweptEmptyCardActivity.EXTRA_FROM_EMPTY));
            }
            sureBundle.putBoolean("isOcp", isOcp);
            sureBundle.putBoolean("priceTag", priceTag);
            sureBundle.putBoolean("no_ver_name", no_ver_name);
            Intent intent = new Intent();
            intent.putExtras(sureBundle);
            setIntent(intent);
        }
    }

    //
    @Override
    public void verifyPayPwdResult(boolean is_match, int left_retry_count) {
        if (App.add_card_back_home) {
            presenter.getRealName();
        } else {
            if (App.no_add_card) {
                Toast.makeText(this, "重置密码成功", Toast.LENGTH_SHORT).show();
                finish();
            } else if (App.no_add_card_and_pay) {
                App.mAxLoginSp.setUnionPayPwd(pwd);
                RxBus.getDefault().send("no_add_card_and_pay");
                Toast.makeText(this, "重置密码成功", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                if (is_match) {
                    if (no_ver_name) {
                        if (App.add_card_back_home) {
                            presenter.getRealName();
                        } else {
//                            Toast.makeText(this, "设置密码成功", Toast.LENGTH_SHORT).show();
//                            finish();
                            showBackAlertAnim(null);
                        }
                    } else {
                        presenter.getRealName();
                    }
                } else {

                }
            }
        }
    }

    @Override
    public void setPayPwdResult(BaseResponse baseResponse) {
        App.mAxLoginSp.setUnionPayPwd("");
        presenter.verifyPayPwd(pwd);
    }

    @Override
    public void getRealNameResult(RealName realName) {
        showBackAlertAnim(realName);
    }

    @Override
    public void modifyPayPwdResult(int left_retry_count) {
    }

    @Override
    public void slearPayPwdResult() {
    }

    @Override
    public void payOrderResult(PaymentOrder order) {
    }

    @Override
    public void getUpQrOrderResult(UPQROrder upqrOrder) {
    }

    @Override
    public void getUpQrCouponResult(UPCoupon upCoupon) {
    }

    @Override
    public void setPinFreeResult(boolean is_match, int left_retry_count) {
    }

    @Override
    public void judgePayPwdResult(PaymentConfig config) {
        this.config = config;
    }

    @Override
    public void setPresenter(IunionPayPwdContract.IJudgePayPwdPresenter presenter) {
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


    private void showBackAlertAnim(final RealName realName) {
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

        if (config.pin_free_amount!=null)
        tvContent.setText("已为你开启小额免密支付功能,扫码支付" + config.pin_free_amount + "元内免输入支付密码");

        clickErrorbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (realName != null) {
                    if (realName.status.equals(RealNameAuthStatus.OK.name())) {
                        Intent i = new Intent(UnionSetPayPwdActivity.this, UnionHtmlActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(UnionCashierFragment.EXTRA_ENTITY, entity);
                        bundle.putBoolean(UnionHtmlActivity.EXTRA_FROM_UPQRCASHIER, true);
                        bundle.putBoolean("isOcp", true);
                        if (getIntent().getExtras() != null && getIntent().getExtras().getBoolean(UnionHtmlActivity.EXTRA_FROM_BANKCARDLIST)) {
                            bundle.putBoolean(UnionHtmlActivity.EXTRA_FROM_BANKCARDLIST, true);
                        }
                        if (getIntent().getExtras() != null && getIntent().getExtras().getString(UnionSweptEmptyCardActivity.EXTRA_FROM_EMPTY) != null) {
                            bundle.putString(UnionSweptEmptyCardActivity.EXTRA_FROM_EMPTY, getIntent().getExtras().getString(UnionSweptEmptyCardActivity.EXTRA_FROM_EMPTY));
                        }
                        i.putExtras(bundle);
                        startActivity(i);
                    } else if (realName.status.equals(RealNameAuthStatus.FAILED.name())) {

                    } else if (realName.status.equals(RealNameAuthStatus.NotAuth.name())) {
                        Intent i = new Intent(UnionSetPayPwdActivity.this, UnionCertificationActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(UnionCashierFragment.EXTRA_ENTITY, entity);
                        bundle.putBoolean(UnionHtmlActivity.EXTRA_FROM_UPQRCASHIER, true);
                        bundle.putBoolean("isOcp", true);
                        if (getIntent().getExtras() != null && getIntent().getExtras().getString(UnionSweptEmptyCardActivity.EXTRA_FROM_EMPTY) != null) {
                            bundle.putString(UnionSweptEmptyCardActivity.EXTRA_FROM_EMPTY, getIntent().getExtras().getString(UnionSweptEmptyCardActivity.EXTRA_FROM_EMPTY));
                        }
                        i.putExtras(bundle);
                        startActivity(i);
                    } else {// Pending

                    }
                    finish();
                } else {
                    finish();
                }
            }
        });


        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //去关闭支付密码
                setPinFree(false,config.pin_free_amount,realName);
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

    public void setPinFree(boolean pin_free, String pin_free_amount, final RealName realName) {
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String,Object> map = new HashMap<>();
        map.put("pin_free",pin_free);
        map.put("pin_free_amount",pin_free_amount);
        PaymentService meService = ApiFactory.getFactory().create(PaymentService.class);
        meService.setPinFree(NetUtils.getRequestParams(UnionSetPayPwdActivity.this,map),NetUtils.getSign(NetUtils.getRequestParams(UnionSetPayPwdActivity.this,map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(UnionSetPayPwdActivity.this,true,null) {
                    @Override
                    public void onNext(Object o) {
                        Toast.makeText(UnionSetPayPwdActivity.this, "小额免密已关闭", Toast.LENGTH_SHORT).show();
                        if (realName != null) {
                            if (realName.status.equals(RealNameAuthStatus.OK.name())) {
                                Intent i = new Intent(UnionSetPayPwdActivity.this, UnionHtmlActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable(UnionCashierFragment.EXTRA_ENTITY, entity);
                                bundle.putBoolean(UnionHtmlActivity.EXTRA_FROM_UPQRCASHIER, true);
                                bundle.putBoolean("isOcp", true);
                                if (getIntent().getExtras() != null && getIntent().getExtras().getBoolean(UnionHtmlActivity.EXTRA_FROM_BANKCARDLIST)) {
                                    bundle.putBoolean(UnionHtmlActivity.EXTRA_FROM_BANKCARDLIST, true);
                                }
                                if (getIntent().getExtras() != null && getIntent().getExtras().getString(UnionSweptEmptyCardActivity.EXTRA_FROM_EMPTY) != null) {
                                    bundle.putString(UnionSweptEmptyCardActivity.EXTRA_FROM_EMPTY, getIntent().getExtras().getString(UnionSweptEmptyCardActivity.EXTRA_FROM_EMPTY));
                                }
                                i.putExtras(bundle);
                                startActivity(i);
                            } else if (realName.status.equals(RealNameAuthStatus.FAILED.name())) {

                            } else if (realName.status.equals(RealNameAuthStatus.NotAuth.name())) {
                                Intent i = new Intent(UnionSetPayPwdActivity.this, UnionCertificationActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable(UnionCashierFragment.EXTRA_ENTITY, entity);
                                bundle.putBoolean(UnionHtmlActivity.EXTRA_FROM_UPQRCASHIER, true);
                                bundle.putBoolean("isOcp", true);
                                if (getIntent().getExtras() != null && getIntent().getExtras().getString(UnionSweptEmptyCardActivity.EXTRA_FROM_EMPTY) != null) {
                                    bundle.putString(UnionSweptEmptyCardActivity.EXTRA_FROM_EMPTY, getIntent().getExtras().getString(UnionSweptEmptyCardActivity.EXTRA_FROM_EMPTY));
                                }
                                i.putExtras(bundle);
                                startActivity(i);
                            } else {// Pending

                            }
                            finish();
                        }else {
                            finish();
                        }
                    }
                });
    }
}
