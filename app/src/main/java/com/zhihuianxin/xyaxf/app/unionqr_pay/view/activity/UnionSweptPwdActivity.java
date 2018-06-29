package com.zhihuianxin.xyaxf.app.unionqr_pay.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import modellib.thrift.fee.PaymentRecord;
import modellib.thrift.message.UPC2BQRVerifyPasswordPushContent;
import modellib.thrift.payment.PaymentOrder;
import modellib.thrift.unqr.PaymentConfig;
import modellib.thrift.unqr.RealName;
import modellib.thrift.unqr.RealNameAuthStatus;
import modellib.thrift.unqr.UPBankCard;
import com.zhihuianxin.xyaxf.BaseRealmActionBarActivity;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.unionqr_pay.contract.IunionSweptContract;
import com.zhihuianxin.xyaxf.app.unionqr_pay.presenter.UnionSweptMainPresenter;
import com.zhihuianxin.xyaxf.app.view.KeyBoardPwdView;
import com.zhihuianxin.xyaxf.app.view.PasswordInputEdtView;
import com.zhihuianxin.xyaxf.app.view.UnionPayErrorDialog;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.realm.RealmList;

/**
 * Created by Vincent on 2017/12/4.
 */

public class UnionSweptPwdActivity extends BaseRealmActionBarActivity implements
        KeyBoardPwdView.OnNumberClickListener,IunionSweptContract.IunionSweptView{
    public static final String EXTRA_PWD_ORDER = "extra_pwd_order";

    @InjectView(R.id.edt)
    PasswordInputEdtView passwordInputEdt;
    @InjectView(R.id.am_nkv_keyboard)
    KeyBoardPwdView mNkvKeyboard;
    @InjectView(R.id.forget_pwd)
    TextView forgetPwdTxt;
    @InjectView(R.id.amount)
    TextView amountTxt;
    @InjectView(R.id.couponamount)
    TextView couponAmountTxt;
    @InjectView(R.id.order_msg)
    TextView orderMsgTxt;
    @InjectView(R.id.pay_way)
    TextView payWayTxt;
    @InjectView(R.id.coupon_msg)
    TextView couponMsgTxt;


    private UnionPayErrorDialog payPwdErrorDialog;
    private String pwd = "";
    private DisplayMetrics metrics;
    private UPC2BQRVerifyPasswordPushContent content;
    private IunionSweptContract.IunionSweptPresenter presenter;

    @Override
    protected int getContentViewId() {
        return R.layout.union_swept_pwd_activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);

        content = (UPC2BQRVerifyPasswordPushContent) getIntent().getExtras().getSerializable(EXTRA_PWD_ORDER);
        initViews();
    }

    private void initViews(){
        new UnionSweptMainPresenter(this,this);
        metrics = new DisplayMetrics();
        mNkvKeyboard.setOnNumberClickListener(this);

        amountTxt.setText("￥"+ content.getAmount());
        orderMsgTxt.setText("订单信息："+ content.getMerchant_name());
        payWayTxt.setText("付款方式："+ content.getCard_info().getIss_ins_name()+ content.getCard_info().getCard_type_name()+"("+ content.getCard_info().getCard_no()+")");

        passwordInputEdt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(passwordInputEdt.getWindowToken(), 0);
                passwordInputEdt.closeKeybord();
            }
        });
        payPwdErrorDialog = new UnionPayErrorDialog(getActivity());
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
                for(int i = 0;i < pwd.length();i++){
                    passwordInputEdt.onKeyDown(67,null);
                }
                pwd = "";// 清楚密码框内容
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
        forgetPwdTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i = 0;i < pwd.length();i++){
                    passwordInputEdt.onKeyDown(67,null);
                }
                pwd = "";// 清楚密码框内容
                presenter.getRealName();
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
                setPwdOk(pwd);
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

    private void setPwdOk(String pwd) {
        presenter.c2bQrVerifyPpaymentPassword(content.getQr_code(),content.getAmount(),pwd);
    }

    @Override
    public void c2bQrVerifyPpaymentPasswordResult(boolean is_match, int left_retry_count) {
        if (is_match){
            finish();
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
    public void getRealNameResult(RealName realName) {
        if(realName.status.equals(RealNameAuthStatus.OK.name())){
            startActivity(new Intent(getActivity(), UnionForgetPayPwdCodeActivity.class));
        } else{
            Intent i = new Intent(getActivity(), UnionForgetPayPwdCodeActivity.class);
            Bundle b = new Bundle();
            b.putBoolean(UnionForgetPayPwdCodeActivity.EXTRA_SHOWIMG,false);
            i.putExtras(b);
            startActivity(i);
        }
    }

    @Override
    public void verifyPayPwdResult(boolean is_match, int left_retry_count) {}
    @Override
    public void payOrderResult(PaymentOrder order) {}
    @Override
    public void loadError(String errorMsg) {}
    @Override
    public void setPayList(RealmList<PaymentRecord> payList) {}
    @Override
    public void setPresenter(IunionSweptContract.IunionSweptPresenter presenter) {this.presenter = presenter;}
    @Override
    public void getBankCardResult(RealmList<UPBankCard> bankCards) {}
    @Override
    public void JudgePayPwdResult(PaymentConfig config) {}
    @Override
    public void getC2BCodeResult(String qr_code) {}
    @Override
    public void swepPayPwdResult() {}
    @Override
    public void loadStart() {}
    @Override
    public void loadComplete() {}
}
