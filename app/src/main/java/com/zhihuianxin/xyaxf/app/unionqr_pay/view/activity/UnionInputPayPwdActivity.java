package com.zhihuianxin.xyaxf.app.unionqr_pay.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.axinfu.modellib.thrift.base.BaseResponse;
import com.axinfu.modellib.thrift.payment.PaymentOrder;
import com.axinfu.modellib.thrift.unqr.PaymentConfig;
import com.axinfu.modellib.thrift.unqr.RealName;
import com.axinfu.modellib.thrift.unqr.RealNameAuthStatus;
import com.axinfu.modellib.thrift.unqr.UPCoupon;
import com.axinfu.modellib.thrift.unqr.UPQROrder;
import com.zhihuianxin.xyaxf.BaseRealmActionBarActivity;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.unionqr_pay.contract.IunionPayPwdContract;
import com.zhihuianxin.xyaxf.app.unionqr_pay.entity.UnionPayEntity;
import com.zhihuianxin.xyaxf.app.unionqr_pay.presenter.UnionPayPwdPresenter;
import com.zhihuianxin.xyaxf.app.unionqr_pay.view.fragment.UnionCashierFragment;
import com.zhihuianxin.xyaxf.app.view.KeyBoardPwdView;
import com.zhihuianxin.xyaxf.app.view.PasswordInputEdtView;
import com.zhihuianxin.xyaxf.app.view.UnionPayErrorDialog;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Vincent on 2017/11/14.
 */

public class UnionInputPayPwdActivity extends BaseRealmActionBarActivity implements
        KeyBoardPwdView.OnNumberClickListener,IunionPayPwdContract.IJudgePayPwd {

    @InjectView(R.id.edt)
    PasswordInputEdtView passwordInputEdt;
    @InjectView(R.id.next)
    Button mNextBtn;
    @InjectView(R.id.text_title)
    TextView title;
    @InjectView(R.id.forget_pwd)
    TextView forgetPwdTxt;

    @InjectView(R.id.am_nkv_keyboard)
    KeyBoardPwdView mNkvKeyboard;
    private String pwd = "";
    private IunionPayPwdContract.IJudgePayPwdPresenter presenter;
    private UnionPayEntity entity;
    private UnionPayErrorDialog payPwdErrorDialog;
    private boolean isForgetPwd = false;

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
        initView();
    }

    private void initView() {
        if(getIntent().getExtras() != null && getIntent().getExtras().getSerializable(UnionCashierFragment.EXTRA_ENTITY) != null){
            entity = (UnionPayEntity) getIntent().getExtras().getSerializable(UnionCashierFragment.EXTRA_ENTITY);
        }

        title.setText("请输入支付密码，以验证身份");
        new UnionPayPwdPresenter(this,this);
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
                //Toast.makeText(UnionSetPayPwdActivity.this, text, Toast.LENGTH_SHORT).show();
            }
        });

        forgetPwdTxt.setVisibility(View.VISIBLE);
        forgetPwdTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isForgetPwd = true;
                presenter.getRealName();
            }
        });
        payPwdErrorDialog = new UnionPayErrorDialog(getActivity());
        //payPwdErrorDialog.create();
        payPwdErrorDialog.setListener(new UnionPayErrorDialog.OnPayPwdErrorListener() {
            @Override
            public void reinput() {
                for(int i = 0;i < pwd.length();i++){
                    passwordInputEdt.onKeyDown(67,null);
                }
                pwd = "";// 清楚密码框内容
                payPwdErrorDialog.dismiss();
            }

            @Override
            public void forgetPwd() {
                isForgetPwd = true;
                payPwdErrorDialog.dismiss();
                presenter.getRealName();
            }

            @Override
            public void cancel() {
                for(int i = 0;i < pwd.length();i++){
                    passwordInputEdt.onKeyDown(67,null);
                }
                pwd = "";// 清楚密码框内容
                payPwdErrorDialog.dismiss();
            }
        });
    }

    @Override
    public void onNumberReturn(String number) {
        passwordInputEdt.onTextChanged(number,0,0,1);
        passwordInputEdt.onTextChanged("",0,0,1);
        if(pwd.length() < 6){
            pwd += number;
            if(pwd.length() == 6){
                setPwdOk();
            }
        }
    }

    @Override
    public void onNumberDelete() {
        passwordInputEdt.onKeyDown(67,null);
        if (pwd.length() <= 1) {
            pwd = "";
        } else {
            pwd = pwd.substring(0, pwd.length() - 1);
        }
    }

    private void setPwdOk() {
        presenter.verifyPayPwd(pwd);
    }

    @Override
    public void getRealNameResult(RealName realName) {
        if(isForgetPwd){
            if(realName.status.equals(RealNameAuthStatus.OK.name())){
                startActivity(new Intent(this, UnionForgetPayPwdCodeActivity.class));
            } else{
                Intent i = new Intent(this, UnionForgetPayPwdCodeActivity.class);
                Bundle b = new Bundle();
                b.putBoolean(UnionForgetPayPwdCodeActivity.EXTRA_SHOWIMG,false);
                i.putExtras(b);
                startActivity(i);
            }
        } else{
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
        }

        finish();
    }

    @Override
    public void verifyPayPwdResult(boolean is_match,int left_retry_count) {
        if(is_match){
            presenter.getRealName();
        } else{
            payPwdErrorDialog.show();
            if(left_retry_count == 2){
                payPwdErrorDialog.setBtnText("重新输入");
                payPwdErrorDialog.showErrorText("2");
            } else if(left_retry_count == 1){
                payPwdErrorDialog.setBtnText("重新输入");
                payPwdErrorDialog.showErrorText("1");
            } else if(left_retry_count == 0){
                payPwdErrorDialog.setBtnText("取消");
                payPwdErrorDialog.showEndText();
            }
        }
    }

    @Override
    public void setPresenter(IunionPayPwdContract.IJudgePayPwdPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setPayPwdResult(BaseResponse baseResponse) {}
    @Override
    public void modifyPayPwdResult(int left_retry_count) {}
    @Override
    public void slearPayPwdResult() {}
    @Override
    public void payOrderResult(PaymentOrder order) {}
    @Override
    public void getUpQrOrderResult(UPQROrder upqrOrder) {}
    @Override
    public void getUpQrCouponResult(UPCoupon upCoupon) {}
    @Override
    public void setPinFreeResult(boolean is_match, int left_retry_count) {}
    @Override
    public void judgePayPwdResult(PaymentConfig config) {}
    @Override
    public void loadStart() {}
    @Override
    public void loadError(String errorMsg) {}
    @Override
    public void loadComplete() {}
}
