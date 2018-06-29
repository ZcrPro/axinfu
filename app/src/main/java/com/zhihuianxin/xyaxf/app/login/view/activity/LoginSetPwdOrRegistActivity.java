package com.zhihuianxin.xyaxf.app.login.view.activity;

import android.content.Intent;
import android.os.AsyncTask;
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
import android.widget.TextView;
import android.widget.Toast;

import com.zhihuianxin.xyaxf.app.AppConstant;
import modellib.thrift.customer.Customer;
import com.zhihuianxin.xyaxf.App;
import com.zhihuianxin.xyaxf.BaseRealmActionBarActivity;
import com.zhihuianxin.xyaxf.MainActivity;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.login.contract.ILoginSetPwdOrRegistContract;
import com.zhihuianxin.xyaxf.app.login.presenter.LoginSetPwdOrRegistPresenter;
import com.zhihuianxin.xyaxf.app.login.view.fragment.LoginSelectCityActivity;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import io.realm.Realm;

/**
 * Created by Vincent on 2016/10/11.
 */

public class LoginSetPwdOrRegistActivity extends BaseRealmActionBarActivity implements ILoginSetPwdOrRegistContract.ILoginSetPwdOrRegistView {
    public static final int DEFAULT_COUNT = AppConstant.DEFAULT_COUNT;
    private static final HashMap<String, Long> sStartTicks = new HashMap<String, Long>();

    private int mCountDownDuration = DEFAULT_COUNT;

    @InjectView(R.id.getver)
    TextView mVerText;
    @InjectView(R.id.editText)
    EditText mVerEdit;
    @InjectView(R.id.edit_pwd)
    EditText mPwdEdit;
    @InjectView(R.id.next)
    Button mBtn;
    @InjectView(R.id.pwdlookok)
    ImageView mPwdLookOkImg;
    @InjectView(R.id.pwdlookun)
    ImageView mPwdLookunImg;

    private boolean isHidden = true;
    private ILoginSetPwdOrRegistContract.ILoginSetPwdOrRegistPresenter mPresenter;
    private VerController verController;
    private String mMobile;

    public class VerController{
        private String mNormalText;
        private String mCountDownTag = getClass().getName();
        public VerController(){
            mCountDownTag = getClass().getName();
            mNormalText = mVerText.getText().toString();
        }
        void verStart(){
            mVerText.setTextColor(getResources().getColor(R.color.axf_light_gray));
            setState(false);
        }

        void reset(){
            setState(true);
            mVerText.setText(mNormalText);
            mVerText.setTextColor(getResources().getColor(R.color.axf_common_blue));
            sStartTicks.remove(mCountDownTag);
        }

        public void setState(boolean enabled){
            mVerText.setEnabled(enabled);
        }

        public String getCountDownTag(){
            return mCountDownTag;
        }
    }

    public class LoginSendVerTask extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] params) {
            Long startMilli = sStartTicks.get(verController.getCountDownTag());
            if(startMilli == null || System.currentTimeMillis() - startMilli > mCountDownDuration){
                startMilli = System.currentTimeMillis();
                sStartTicks.put(verController.getCountDownTag(), startMilli);
            }

            while (true){
                long currentMilli = System.currentTimeMillis();

                long duration = currentMilli - startMilli;
                long left = mCountDownDuration - duration;
                if(left <= 0){
                    publishProgress(0l);

                    break;
                }
                publishProgress(left);
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            verController.verStart();
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            verController.reset();
        }

        @Override
        protected void onProgressUpdate(Object[] values) {
            super.onProgressUpdate(values);

            if(values.length > 0 && mVerText != null){
                long left = (Long) values[0];
                mVerText.setText(Math.round((float)left / 1000f)+"s后重新获取");
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
        mVerEdit.addTextChangedListener(watcherVer);
        mPwdEdit.addTextChangedListener(watcherPwd);

        mMobile = "";//getIntent().getExtras().getString(LoginInputMobilActivity.EXTRA_MOBILE);
        verController = new VerController();
        mPresenter = new LoginSetPwdOrRegistPresenter(this,this);
        findViewById(R.id.action_bar).setBackgroundColor(getResources().getColor(R.color.axf_btn_uncheck_blue));
    }

    @OnClick(R.id.next)
    public void onBtnNext(){
        mPresenter.setPwdOrRegistAndLogin(mMobile,mVerEdit.getText().toString().trim(),
                mPwdEdit.getText().toString().trim(),App.mAxLoginSp.getUUID().replace("-", ""));
//        startActivity(new Intent(this,LoginSelectCityActivity.class));
    }

    @OnClick(R.id.getver)
    public void onBtnGetVer(){
        mPresenter.getVerCode(mMobile);
        LoginSendVerTask task = new LoginSendVerTask();
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @OnClick(R.id.pwdlook)
    public void onBtnPwdLook(){
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

    @Override
    protected int getContentViewId() {
        return R.layout.login_setpwd_acctivity;
    }

    @Override
    public void setPresenter(ILoginSetPwdOrRegistContract.ILoginSetPwdOrRegistPresenter presenter) {
        this.mPresenter = presenter;
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
    public void setPwdOrRegistAndLoginSuccess(final Customer customer, String session) {

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
                Log.d("PwdOrRegistActivity", "存储customer数据成功!");
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                // Transaction failed and was automatically canceled.
                Log.d("PwdOrRegistActivity", "存储customer数据失败!");
            }
        });

        App.mAxLoginSp.setUserMobil(mMobile);
        App.mAxLoginSp.setRegistSerial(customer.base_info.regist_serial);
        App.mSession.setSession(session);
        //App.mAxLoginSp.setLoginSign(true);

        if(customer.school != null){
            startActivity(new Intent(this, MainActivity.class));
            //ActivityCollector.removeAllActivities();
        } else{
            startActivity(new Intent(this, LoginSelectCityActivity.class));
        }
        finish();
    }

    @Override
    public void getVerCodeSuccess(String verCode) {
        App.mAxLoginSp.setVerCode(verCode);
        Toast.makeText(this,verCode,Toast.LENGTH_LONG).show();
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
    public int getLeftButtonImageId() {
        return R.drawable.back;
    }

    TextWatcher watcherVer = new TextWatcher() {
        private String lastInput;
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
        @Override
        public void afterTextChanged(Editable s) {
            String text = s.toString();
            if(text.equals(lastInput)){
                return;
            }

            if(text.length() == 4){
                mPwdEdit.requestFocus();
            }

            if(text.length() == 4 && mPwdEdit.getText().length() >= 6){
                mBtn.setEnabled(true);
            } else {
                mBtn.setEnabled(false);
            }

            lastInput = text;
        }
    };

    TextWatcher watcherPwd = new TextWatcher() {
        private String lastInput;
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
        @Override
        public void afterTextChanged(Editable s) {
            String text = s.toString();
            if(text.equals(lastInput)){
                return;
            }
            if(text.length() >= 6 && mVerEdit.getText().length() == 4){
                mBtn.setEnabled(true);
            } else {
                mBtn.setEnabled(false);
            }
            lastInput = text;
        }
    };

}

