package com.zhihuianxin.xyaxf.app.me.view.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Selection;
import android.text.Spannable;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zhihuianxin.xyaxf.app.AppConstant;
import modellib.thrift.customer.Customer;
import modellib.thrift.customer.VerifyField;
import com.zhihuianxin.xyaxf.app.base.axutil.Util;
import com.zhihuianxin.xyaxf.App;
import com.zhihuianxin.xyaxf.BaseRealmActionBarActivity;
import com.zhihuianxin.xyaxf.BuildConfig;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.login.view.activity.LoginGetPwdActivity;
import com.zhihuianxin.xyaxf.app.login.view.activity.LoginGetPwdByCodeActivity;
import com.zhihuianxin.xyaxf.app.me.contract.IMeMsgContract;
import com.zhihuianxin.xyaxf.app.me.presenter.MeMsgPresenter;
import com.zhihuianxin.xyaxf.app.me.view.fragment.MeFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import io.realm.Realm;

import static com.zhihuianxin.xyaxf.app.login.view.activity.LoginGetPwdActivity.EXTRA_VERIFY_DATA;

/**
 * Created by Vincent on 2016/10/20.
 */

public class MeMsgModifyMobileActivity extends BaseRealmActionBarActivity implements IMeMsgContract.IMeMsgView{
    private IMeMsgContract.IMeMsgPresenter mPresenter;
    private boolean isHidden = true;
    public static final int DEFAULT_COUNT = AppConstant.DEFAULT_COUNT;
    private static final HashMap<String, Long> sStartTicks = new HashMap<String, Long>();
    private String mModifyMobileVerCode;

    private int mCountDownDuration = DEFAULT_COUNT;
    private VerController verController;

    @InjectView(R.id.ver_value)
    TextView mVerValueEdit;
    @InjectView(R.id.editText)
    TextView mNewMobileEdit;
    @InjectView(R.id.pwdlookok)
    ImageView mPwdLookOkImg;
    @InjectView(R.id.pwdlookun)
    ImageView mPwdLookunImg;
    @InjectView(R.id.edit_pwd)
    EditText mPwdEdit;
    @InjectView(R.id.getver)
    TextView mVerText;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ButterKnife.inject(this);
        verController = new VerController();
        new MeMsgPresenter(this,this);
    }

    @OnClick(R.id.forget_pwd)
    public void onBtnGetPwd(){
        mPresenter.getmodifyPwdInfo(App.mAxLoginSp.getUserMobil());
    }

    @OnClick(R.id.getver)
    public void onBtnGetVer(){
        Pattern p = Pattern.compile(BuildConfig.AnXinDEBUG?Util.REGEX_MOBILE_DEBUG:Util.REGEX_MOBILE);
        if(Util.isEmpty(mNewMobileEdit.getText().toString().trim()) ||
                !p.matcher(mNewMobileEdit.getText().toString().trim()).matches()){
            Toast.makeText(this,"请输入正确的新手机号",Toast.LENGTH_LONG).show();
            return;
        }
        mPresenter.getVerCode(mNewMobileEdit.getText().toString().trim());
        LoginSendVerTask task = new LoginSendVerTask();
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @OnClick(R.id.next)
    public void onBtnModifyMobile(){
        String newMobile = mNewMobileEdit.getText().toString().trim();
        String verCode = mVerValueEdit.getText().toString().trim();
        String newPwd = mPwdEdit.getText().toString().trim();

        Pattern p = Pattern.compile(BuildConfig.AnXinDEBUG?Util.REGEX_MOBILE_DEBUG:Util.REGEX_MOBILE);
        if(!p.matcher(newMobile).matches()){
            Toast.makeText(this,"请输入正确的新手机号",Toast.LENGTH_LONG).show();
            return;
        }
        if(Util.isEmpty(verCode) || verCode.length() != 4){
            Toast.makeText(this,"请输入正确的验证码",Toast.LENGTH_LONG).show();
            return;
        }
        if(Util.isEmpty(newPwd) || newPwd.length() < 6){
            Toast.makeText(this,"请输入6位以上的密码",Toast.LENGTH_LONG).show();
            return;
        }
        mPresenter.modifyMobile(newMobile,verCode,newPwd);
    }

    @Override
    public void modifyMobileSuccess(final Customer customer) {
        /**
         * 存入customer
         */
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                customer.mobile = App.mAxLoginSp.getUserMobil();
                bgRealm.copyToRealmOrUpdate(customer);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Intent intent = new Intent(MeFragment.BROADCAST_MEFRAGMENT);
                sendBroadcast(intent);
                Toast.makeText(MeMsgModifyMobileActivity.this,"修改成功",Toast.LENGTH_LONG).show();
                finish();
            }
        });
        App.mAxLoginSp.setUserMobil(customer.base_info.mobile);
        App.mAxLoginSp.setRegistSerial(customer.base_info.regist_serial);
    }

    @Override
    public void getVerCodeSuccess(String code) {
        mModifyMobileVerCode = code;
        Toast.makeText(this,code,Toast.LENGTH_LONG).show();
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
        return R.layout.me_msg_mobile_activity;
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

    @Override
    public void modifyBaseInfoSuccess(Customer customer) {

    }

    @Override
    public void setPresenter(IMeMsgContract.IMeMsgPresenter presenter) {
        mPresenter = presenter;
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
}
