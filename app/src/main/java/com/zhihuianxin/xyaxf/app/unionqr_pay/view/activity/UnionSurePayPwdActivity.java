package com.zhihuianxin.xyaxf.app.unionqr_pay.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
import com.zhihuianxin.xyaxf.app.unionqr_pay.contract.IunionPayPwdContract;
import com.zhihuianxin.xyaxf.app.unionqr_pay.entity.UnionPayEntity;
import com.zhihuianxin.xyaxf.app.unionqr_pay.presenter.UnionPayPwdPresenter;
import com.zhihuianxin.xyaxf.app.unionqr_pay.view.fragment.UnionCashierFragment;
import com.zhihuianxin.xyaxf.app.utils.RxBus;
import com.zhihuianxin.xyaxf.app.view.KeyBoardPwdView;
import com.zhihuianxin.xyaxf.app.view.PasswordInputEdtView;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Vincent on 2017/11/9.
 */

public class UnionSurePayPwdActivity extends BaseRealmActionBarActivity implements
        KeyBoardPwdView.OnNumberClickListener,
        IunionPayPwdContract.IJudgePayPwd {
    @InjectView(R.id.edt)
    PasswordInputEdtView passwordInputEdt;
    @InjectView(R.id.next)
    Button mNextBtn;
    @InjectView(R.id.text_title)
    TextView title;

    private IunionPayPwdContract.IJudgePayPwdPresenter presenter;
    private KeyBoardPwdView mNkvKeyboard;
    private String pwd = "";
    private UnionPayEntity entity;

    private boolean isOcp;
    private boolean priceTag;
    private boolean no_ver_name;

    @Override
    protected int getContentViewId() {
        return R.layout.key_board_pay_pwd;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        initView();
    }

    private void initView() {
        if (getIntent().getExtras() != null && getIntent().getExtras().getSerializable(UnionCashierFragment.EXTRA_ENTITY) != null) {
            entity = (UnionPayEntity) getIntent().getExtras().getSerializable(UnionCashierFragment.EXTRA_ENTITY);
        }
        if (getIntent().getExtras() != null) {
            isOcp = getIntent().getExtras().getBoolean("isOcp");
        }

        if (getIntent().getExtras() != null) {
            priceTag = getIntent().getExtras().getBoolean("priceTag");
        }

        if (getIntent().getExtras() != null) {
            no_ver_name = getIntent().getExtras().getBoolean("no_ver_name");
        }

        title.setText("请再次确认你的支付密码");
        new UnionPayPwdPresenter(this, this);
        mNkvKeyboard = (KeyBoardPwdView) findViewById(R.id.am_nkv_keyboard);
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
                setPwdOk();
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

    private void setPwdOk() {
        if (App.mAxLoginSp.getUnionPayPwd().equals(pwd)) {
            presenter.setPayPwd(pwd);
        } else {
            Toast.makeText(getApplicationContext(), "两次输入的密码不一致！", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void setPayPwdResult(BaseResponse baseResponse) {
        App.mAxLoginSp.setUnionPayPwd("");
        presenter.verifyPayPwd(pwd);
//        if (priceTag){
//            finish();
//            return;
//        }

//        if (entity == null) {
//            if (getIntent().getExtras() != null && getIntent().getExtras().getString(UnionSweptEmptyCardActivity.EXTRA_FROM_EMPTY) != null) {
//                // 空银行卡帮卡进来的
//                Intent i = new Intent(this, UnionInputPayPwdActivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putString(UnionSweptEmptyCardActivity.EXTRA_FROM_EMPTY,getIntent().getExtras().getString(UnionSweptEmptyCardActivity.EXTRA_FROM_EMPTY));
//                i.putExtras(bundle);
//                startActivity(i);
//            }
//
//            if (isOcp){
//                Intent i = new Intent(this, UnionInputPayPwdActivity.class);
//                startActivity(i);
//            }
//
//            finish();
//        } else {
//            // todo test. if 里面是新增swep流程,else 是原流程没有问题
//            Intent i = new Intent(this, UnionInputPayPwdActivity.class);
//            Bundle b = new Bundle();
//            b.putSerializable(UnionCashierFragment.EXTRA_ENTITY, entity);
//            i.putExtras(b);
//            startActivity(i);// 设置完成后验证支付密码
//            finish();
//        }
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

    @Override
    public void getRealNameResult(RealName realName) {
            if(realName.status.equals(RealNameAuthStatus.OK.name())){
                Intent i = new Intent(this,UnionHtmlActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(UnionCashierFragment.EXTRA_ENTITY,entity);
                bundle.putBoolean(UnionHtmlActivity.EXTRA_FROM_UPQRCASHIER,true);
                bundle.putBoolean("isOcp",true);
                if(getIntent().getExtras()!=null && getIntent().getExtras().getBoolean(UnionHtmlActivity.EXTRA_FROM_BANKCARDLIST)){
                    bundle.putBoolean(UnionHtmlActivity.EXTRA_FROM_BANKCARDLIST,true);
                }
                if(getIntent().getExtras()!=null && getIntent().getExtras().getString(UnionSweptEmptyCardActivity.EXTRA_FROM_EMPTY)!=null){
                    bundle.putString(UnionSweptEmptyCardActivity.EXTRA_FROM_EMPTY,getIntent().getExtras().getString(UnionSweptEmptyCardActivity.EXTRA_FROM_EMPTY));
                }
                i.putExtras(bundle);
                startActivity(i);
            } else if(realName.status.equals(RealNameAuthStatus.FAILED.name())){

            } else if(realName.status.equals(RealNameAuthStatus.NotAuth.name())){
                Intent i = new Intent(this,UnionCertificationActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(UnionCashierFragment.EXTRA_ENTITY,entity);
                bundle.putBoolean(UnionHtmlActivity.EXTRA_FROM_UPQRCASHIER,true);
                bundle.putBoolean("isOcp",true);
                if(getIntent().getExtras()!=null && getIntent().getExtras().getString(UnionSweptEmptyCardActivity.EXTRA_FROM_EMPTY)!=null){
                    bundle.putString(UnionSweptEmptyCardActivity.EXTRA_FROM_EMPTY,getIntent().getExtras().getString(UnionSweptEmptyCardActivity.EXTRA_FROM_EMPTY));
                }
                i.putExtras(bundle);
                startActivity(i);
            } else{// Pending

            }

        finish();
    }

    @Override
    public void verifyPayPwdResult(boolean is_match, int left_retry_count) {
        if (App.no_add_card){
            Toast.makeText(this, "重置密码成功", Toast.LENGTH_SHORT).show();
            finish();
        }else if (App.no_add_card_and_pay){
            App.mAxLoginSp.setUnionPayPwd(pwd);
            RxBus.getDefault().send("no_add_card_and_pay");
            Toast.makeText(this, "重置密码成功", Toast.LENGTH_SHORT).show();
            finish();
        }else {
            if(is_match){
                if (no_ver_name){
                    Toast.makeText(this, "设置密码成功", Toast.LENGTH_SHORT).show();
                    finish();
                }else {
                    presenter.getRealName();
                }

            } else{

            }
        }

    }
}
