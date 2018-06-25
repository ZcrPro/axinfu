package com.zhihuianxin.xyaxf.app.unionqr_pay.view.activity;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.axinfu.modellib.thrift.base.BaseResponse;
import com.axinfu.modellib.thrift.payment.PaymentOrder;
import com.axinfu.modellib.thrift.unqr.PaymentConfig;
import com.axinfu.modellib.thrift.unqr.RealName;
import com.axinfu.modellib.thrift.unqr.UPCoupon;
import com.axinfu.modellib.thrift.unqr.UPQROrder;
import com.zhihuianxin.xyaxf.BaseRealmActionBarActivity;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.unionqr_pay.contract.IunionPayPwdContract;
import com.zhihuianxin.xyaxf.app.unionqr_pay.presenter.UnionPayPwdPresenter;
import com.zhihuianxin.xyaxf.app.view.KeyBoardPwdView;
import com.zhihuianxin.xyaxf.app.view.PasswordInputEdtView;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Vincent on 2017/11/15.
 */

public class UnionForgetPwdSetNewActivity extends BaseRealmActionBarActivity
        implements IunionPayPwdContract.IJudgePayPwd,KeyBoardPwdView.OnNumberClickListener{
    public static final String EXTRA_ORIPWD = "qrOriPwd";
    @InjectView(R.id.edt)
    PasswordInputEdtView passwordInputEdt;
    @InjectView(R.id.next)
    Button mNextBtn;

    @InjectView(R.id.text_title)
    TextView title;
    @InjectView(R.id.am_nkv_keyboard)
    KeyBoardPwdView mNkvKeyboard;
    private String pwd = "";
    private IunionPayPwdContract.IJudgePayPwdPresenter presenter;
    private String oriPwd;

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
        new UnionPayPwdPresenter(this,this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        initView();
    }

    private void initView() {
        oriPwd = getIntent().getExtras().getString(EXTRA_ORIPWD);
        mNextBtn.setText("完成");
        title.setText("请输入新密码");
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
            public void onInputOver(String text) {}
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
        presenter.modifyPayPwd(oriPwd,pwd);
    }

    @Override
    public void setPresenter(IunionPayPwdContract.IJudgePayPwdPresenter presenter) {
        this.presenter = presenter;
    }
    @Override
    public void modifyPayPwdResult(int left_retry_count) {
        Toast.makeText(this,"修改成功",Toast.LENGTH_LONG).show();

        finish();
    }


    @Override
    public void verifyPayPwdResult(boolean is_match,int left_retry_count) {}

    @Override
    public void getRealNameResult(RealName realName) {}
    @Override
    public void setPayPwdResult(BaseResponse baseResponse) {}
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
