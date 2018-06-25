package com.zhihuianxin.xyaxf.app.login.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.axinfu.modellib.thrift.customer.Customer;
import com.axinfu.modellib.thrift.customer.VerifyField;
import com.zhihuianxin.xyaxf.App;
import com.zhihuianxin.xyaxf.BaseRealmActionBarActivity;
import com.zhihuianxin.xyaxf.BuildConfig;
import com.zhihuianxin.xyaxf.MainActivity;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.login.contract.ILoginVerPwdContract;
import com.zhihuianxin.xyaxf.app.login.presenter.LoginVerPwdPresenter;
import com.zhihuianxin.xyaxf.app.login.view.fragment.LoginSelectCityActivity;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import io.realm.Realm;

import static com.zhihuianxin.xyaxf.app.login.view.activity.LoginGetPwdActivity.EXTRA_VERIFY_DATA;

/**
 * Created by Vincent on 2016/10/11.
 */

public class LoginVerPwdActivity extends BaseRealmActionBarActivity implements ILoginVerPwdContract.ILoginVerPwdView {
    @InjectView(R.id.editText)
    EditText mPwdEdit;
    @InjectView(R.id.next)
    Button mNextBtn;

    @InjectView(R.id.pwdlookok)
    ImageView mPwdLookOkImg;
    @InjectView(R.id.pwdlookun)
    ImageView mPwdLookunImg;

    private boolean isHidden = true;
    private ILoginVerPwdContract.ILoginVerPwdPresenter presenter;
    private String mMobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
        presenter = new LoginVerPwdPresenter(this, this);
        mPwdEdit.addTextChangedListener(watcher);
        mMobile = "";//getIntent().getExtras().getString(LoginInputMobilActivity.EXTRA_MOBILE);

        findViewById(R.id.action_bar).setBackgroundColor(getResources().getColor(R.color.axf_btn_uncheck_blue));
        mPwdEdit.setText(BuildConfig.AnXinDEBUG?"":"");
    }

    @OnClick(R.id.pwdlook)
    public void onBtnPwdLook() {
        if (isHidden) {
            //设置EditText文本为可见的
            mPwdEdit.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            mPwdLookOkImg.setVisibility(View.VISIBLE);
            mPwdLookunImg.setVisibility(View.GONE);

        } else {
            //设置EditText文本为隐藏的
            mPwdEdit.setTransformationMethod(PasswordTransformationMethod.getInstance());
            mPwdLookOkImg.setVisibility(View.GONE);
            mPwdLookunImg.setVisibility(View.VISIBLE);
        }
        isHidden = !isHidden;
        mPwdEdit.postInvalidate();
        //切换后将EditText光标置于末尾
        CharSequence charSequence = mPwdEdit.getText();
        if (charSequence instanceof Spannable) {
            Spannable spanText = (Spannable) charSequence;
            Selection.setSelection(spanText, charSequence.length());
        }
    }

    @OnClick(R.id.next)
    public void onBtnNext() {
        presenter.login(mMobile, mPwdEdit.getText().toString().trim(), App.mAxLoginSp.getUUID().replace("-", ""));
    }

    @OnClick(R.id.login_forgetpwd)
    public void onBtnGetPwd(){
        presenter.getmodifyPwdInfo(mMobile);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.login_ver_activity;
    }

    @Override
    public void setPresenter(ILoginVerPwdContract.ILoginVerPwdPresenter presenter) {
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
    public void loginSuccess(final Customer customer, String session) {
        /**
         * 存入customer
         */
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                customer.mobile = mMobile;
                bgRealm.copyToRealmOrUpdate(customer);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                // Transaction was a success.
                Log.d("LoginVerPwdActivity", "存储customer数据成功!");
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                // Transaction failed and was automatically canceled.
                Log.d("LoginVerPwdActivity", "存储customer数据失败!");
            }
        });

        App.mSession.setSession(session);

        App.mAxLoginSp.setUserMobil(customer.base_info.mobile);
        App.mAxLoginSp.setRegistSerial(customer.base_info.regist_serial);

        //App.mAxLoginSp.setLoginSign(true);
        if (customer.school != null) {
            startActivity(new Intent(this, MainActivity.class));
            //ActivityCollector.removeAllActivities();
        } else {
            startActivity(new Intent(this, LoginSelectCityActivity.class));
        }
        finish();
    }

    @Override
    public void getmodifyPwdInfoResult(ArrayList<VerifyField> verify_fields) {
        if(verify_fields!= null && verify_fields.size() > 0){
            Intent intent = new Intent(this, LoginGetPwdActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(EXTRA_VERIFY_DATA, verify_fields);
            intent.putExtras(bundle);
            startActivity(intent);
        } else{
            startActivity(new Intent(this,LoginGetPwdByCodeActivity.class));
        }
    }

    TextWatcher watcher = new TextWatcher() {
        private String lastInput;

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            String text = s.toString();
            if (text.equals(lastInput)) {
                return;
            }
            if (text.length() >= 6) {
                mNextBtn.setEnabled(true);
            } else {
                mNextBtn.setEnabled(false);
            }

            lastInput = text;
        }
    };

    @Override
    public boolean leftButtonEnabled() {
        return true;
    }

    @Override
    public int getLeftButtonImageId() {
        return R.drawable.back;
    }

    @Override
    public void onLeftButtonClick(View view) {
        finish();
    }
}
