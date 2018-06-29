package com.zhihuianxin.xyaxf.app.login.view.activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zhihuianxin.xyaxf.app.AppConstant;
import modellib.thrift.customer.Customer;
import modellib.thrift.customer.MobileStatus;
import modellib.thrift.customer.VerifyField;
import com.zhihuianxin.xyaxf.app.base.axutil.Util;
import com.zhihuianxin.xyaxf.App;
import com.zhihuianxin.xyaxf.BaseRealmActionBarActivity;
import com.zhihuianxin.xyaxf.BuildConfig;
import com.zhihuianxin.xyaxf.MainActivity;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.login.contract.ILoginHasPwdContract;
import com.zhihuianxin.xyaxf.app.login.presenter.LoginHasPwdPresenter;
import com.zhihuianxin.xyaxf.app.login.view.fragment.LoginSelectCityActivity;
import com.zhihuianxin.xyaxf.app.view.GifView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import io.realm.Realm;

/**
 * Created by Vincent on 2016/10/11.
 * 登录过程输入手机号界面
 */

public class LoginInputMobilActivity extends BaseRealmActionBarActivity implements ILoginHasPwdContract.ILoginHasPwdView {
    public static final String EXTRA_MOBILE = "mobile";
    public static final int DEFAULT_COUNT = AppConstant.DEFAULT_COUNT;
    private static final HashMap<String, Long> sStartTicks = new HashMap<String, Long>();

    private int mCountDownDuration = DEFAULT_COUNT;

    enum STATUE{
        INPUT(),SETPWD(),VERPWD()
    }
    private STATUE mCurrStatus = STATUE.INPUT;

    @InjectView(R.id.inputEdit)
    EditText mEditTxt;
    @InjectView(R.id.next)
    Button mBtn;
    @InjectView(R.id.tv_debug_version)
    TextView tvDebugVersion;
    @InjectView(R.id.gif_view_input)
    GifView mGifViewInput;
    @InjectView(R.id.gif_view_next)
    GifView mGifViewNext;
    @InjectView(R.id.input_pwd_view)
    View mSetPwdView;
    @InjectView(R.id.bottom_view)
    View mBottomView;
    @InjectView(R.id.setPwdCoverView)
    View mSetPwdCoverView;
    @InjectView(R.id.verpwd_view)
    View mVerPwdView;
    @InjectView(R.id.editText_ver)
    EditText mPwdEdit;
    @InjectView(R.id.pwdlookok_ver)
    ImageView mPwdLookOkImg;
    @InjectView(R.id.pwdlookun_ver)
    ImageView mPwdLookunImg;
    @InjectView(R.id.edit_pwd)
    EditText mSetPwdEdit;
    @InjectView(R.id.editText)
    EditText mSetPwdVerCodeEdit;
    @InjectView(R.id.getver)
    TextView mVerCodeText;
    @InjectView(R.id.pwdlookok)
    ImageView mPwdLookOkImgSet;
    @InjectView(R.id.pwdlookun)
    ImageView mPwdLookunImgSet;
    @InjectView(R.id.gif_view_back)
    GifView mBackGifView;
    @InjectView(R.id.setPwdAllView)
    View mSetPwdAllView;
    @InjectView(R.id.gif_view_next_set)
    GifView mGitViewNextSet;

    private boolean isHiddenVer = true;
    private boolean isSetPwdHiddenVer = true;
    private ILoginHasPwdContract.ILoginHasPwdPresenter presenter;
    private DisplayMetrics metrics;
    private String mMobile;
    private VerController verController;
    private Timer timer = new Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);

        showAnim();
        initView();
    }

    private void initView(){
        verController = new VerController();
        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        new LoginHasPwdPresenter(this, this);
        mEditTxt.addTextChangedListener(watcherInput);
        mEditTxt.setText(App.mAxLoginSp.getUserMobil());
        mPwdEdit.addTextChangedListener(watchVer);
        mSetPwdEdit.addTextChangedListener(watcherPwd);
        mSetPwdVerCodeEdit.addTextChangedListener(verCodeWatch);
        //ActivityCollector.addActivity(this);
        findViewById(R.id.action_bar).setBackgroundColor(getResources().getColor(R.color.axf_btn_uncheck_blue));
        if (BuildConfig.AnXinDEBUG){
            PackageInfo pi = Util.getPackageInfo(this);
            if(tvDebugVersion != null && pi != null){
                tvDebugVersion.setText(String.format("v%s(%s)", pi.versionName, pi.versionCode));
            }
        }
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            mBtn.setEnabled(true);
            switch (msg.what){
                case 0:
                    mGifViewInput.setPaused(true);
                    break;
                case 1:
                    mGifViewNext.setPaused(true);
                    break;
                case 2:
                    mGitViewNextSet.setPaused(true);
                    break;
                case 3:
                    mBackGifView.setPaused(true);
                    break;
            }
        }
    };
    private void showAnim(){
        mGifViewInput.setMovieResource(R.raw.comp_3);
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Message msg = new Message();
                msg.what = 0;
                handler.sendMessage(msg);
                //mGifViewInput.setPaused(true);
            }
        };
        timer.schedule(timerTask,mGifViewInput.getDuration());
//        mGifViewInput.setMovieResource(R.raw.comp_3);
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                mGifViewInput.setPaused(true);
//            }
//        }, mGifViewInput.getDuration());
    }

    private void showVerPwdView(){
        mCurrStatus = STATUE.VERPWD;

        mGifViewInput.setVisibility(View.GONE);
        mGitViewNextSet.setVisibility(View.GONE);
        mBackGifView.setVisibility(View.GONE);
        if(mGifViewNext.isPaused()){
            mGifViewNext.setPaused(false);
        } else{
            mGifViewNext.setMovieResource(R.raw.comp_4);
        }
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Message msg = new Message();
                msg.what = 1;
                handler.sendMessage(msg);
                //mGifViewNext.setPaused(true);
            }
        };
        timer.schedule(timerTask,mGifViewNext.getDuration());
        //mGifViewNext.setVisibility(View.VISIBLE);
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                mGifViewNext.setPaused(true);
//            }
//        }, mGifViewNext.getDuration());

        mVerPwdView.setVisibility(View.VISIBLE);
        ObjectAnimator anim = ObjectAnimator.ofFloat(mEditTxt, "alpha", 1f, 0f);
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mEditTxt.setVisibility(View.GONE);
            }
            @Override
            public void onAnimationStart(Animator animation) {}
            @Override
            public void onAnimationCancel(Animator animation) {}
            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
        anim.setDuration(1000);
        anim.start();
    }

    private void showSetPwdView(){
        mCurrStatus = STATUE.SETPWD;

        mGifViewNext.setVisibility(View.GONE);
        mGifViewInput.setVisibility(View.GONE);
        mBackGifView.setVisibility(View.GONE);
        if(mGitViewNextSet.isPaused()){
            mGitViewNextSet.setPaused(false);
        } else{
            mGitViewNextSet.setMovieResource(R.raw.comp_4);
        }

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Message msg = new Message();
                msg.what = 2;
                handler.sendMessage(msg);
                //mGitViewNextSet.setPaused(true);
            }
        };
        timer.schedule(timerTask,mGitViewNextSet.getDuration());
        mGitViewNextSet.setVisibility(View.VISIBLE);
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                mGitViewNextSet.setPaused(true);
//            }
//        }, mGitViewNextSet.getDuration()-20);

        mVerPwdView.setVisibility(View.GONE);
        mEditTxt.setVisibility(View.GONE);
        ObjectAnimator.ofFloat(mEditTxt, "alpha", 1f, 0f).setDuration(1000).start();

        ObjectAnimator animator = ObjectAnimator.ofFloat(mSetPwdCoverView, "alpha", 1f, 0f);
        animator.setDuration(1000);
        animator.setDuration(1000);
        animator.start();
        mSetPwdCoverView.setVisibility(View.GONE);

        ObjectAnimator animBottom = ObjectAnimator.ofFloat(mBottomView, "translationY", 0f, metrics.density*40);
        animBottom.setDuration(1000);
        animBottom.setDuration(1000);
        animBottom.start();
    }

    @Override
    protected int getContentViewId() {
        return R.layout.login_input_activity;
    }

    @OnClick(R.id.setPwdCoverView)
    public void onBtnPwdCoverView(){

    }

    @OnClick(R.id.getver)
    public void onBtnGetVer(){
        presenter.getVerCode(mMobile,App.mAxLoginSp.getUserStatue());
        LoginSendVerTask task = new LoginSendVerTask();
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @OnClick(R.id.next)
    public void onBtnNext() {
        if(mCurrStatus.equals(STATUE.INPUT)){
            presenter.hasSetPwd(mEditTxt.getText().toString().trim());
        } else if(mCurrStatus.equals(STATUE.VERPWD)){
            presenter.login(mMobile, mPwdEdit.getText().toString().trim(), App.mAxLoginSp.getUUID().replace("-", ""));
        } else{
            presenter.setPwdOrRegistAndLogin(mMobile,mSetPwdVerCodeEdit.getText().toString().trim(),
                    mSetPwdEdit.getText().toString().trim(),App.mAxLoginSp.getUUID().replace("-", ""));
        }
        mBtn.setEnabled(false);
    }

    @OnClick(R.id.login_forgetpwd)
    public void onBtnGetPwd() {
        startActivity(new Intent(this, LoginGetPwdActivity.class));
    }

    @OnClick(R.id.pwdlook_ver)
    public void onBtnPwdLook() {
        if (isHiddenVer) {
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
        isHiddenVer = !isHiddenVer;
        mPwdEdit.postInvalidate();
        //切换后将EditText光标置于末尾
        CharSequence charSequence = mPwdEdit.getText();
        if (charSequence instanceof Spannable) {
            Spannable spanText = (Spannable) charSequence;
            Selection.setSelection(spanText, charSequence.length());
        }
    }

    @OnClick(R.id.pwdlook)
    public void onBtnPwdLookSet(){
        if (isSetPwdHiddenVer) {
            //设置EditText文本为可见的
            mSetPwdEdit.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            mPwdLookOkImgSet.setVisibility(View.VISIBLE);
            mPwdLookunImgSet.setVisibility(View.GONE);

        } else {
            //设置EditText文本为隐藏的
            mSetPwdEdit.setTransformationMethod(PasswordTransformationMethod.getInstance());
            mPwdLookOkImgSet.setVisibility(View.GONE);
            mPwdLookunImgSet.setVisibility(View.VISIBLE);
        }
        isSetPwdHiddenVer = !isSetPwdHiddenVer;
        mSetPwdEdit.postInvalidate();
        //切换后将EditText光标置于末尾
        CharSequence charSequence = mSetPwdEdit.getText();
        if (charSequence instanceof Spannable) {
            Spannable spanText = (Spannable) charSequence;
            Selection.setSelection(spanText, charSequence.length());
        }
    }

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
            if(text.length() >= 6 && mSetPwdVerCodeEdit.getText().length() == 4){
                mBtn.setEnabled(true);
            } else {
                mBtn.setEnabled(false);
            }
            lastInput = text;
        }
    };


    TextWatcher verCodeWatch = new TextWatcher() {
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
                mSetPwdEdit.requestFocus();
            }

            if(text.length() == 4 && mPwdEdit.getText().length() >= 6){
                mBtn.setEnabled(true);
            } else {
                mBtn.setEnabled(false);
            }

            lastInput = text;
        }
    };

    TextWatcher watchVer = new TextWatcher() {
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
                mBtn.setEnabled(true);
            } else {
                mBtn.setEnabled(false);
            }

            lastInput = text;
        }
    };

    TextWatcher watcherInput = new TextWatcher() {
        private String lastInputInput;

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            Pattern p = Pattern.compile(BuildConfig.AnXinDEBUG ? Util.REGEX_MOBILE_DEBUG : Util.REGEX_MOBILE);
            if (p.matcher(charSequence.toString()).matches()) {
                mBtn.setEnabled(true);
            } else {
                mBtn.setEnabled(false);
            }

//            mBtn.setEnabled(true);
        }

        @Override
        public void afterTextChanged(Editable s) {
            String text = s.toString();
            if (text.equals(lastInputInput)) {
                return;
            }

            if (text.length() == 11) {
                @SuppressLint("WrongConstant") InputMethodManager imm = (InputMethodManager) getSystemService("input_method");//Context.INPUT_METHOD_SERVICE
                imm.hideSoftInputFromWindow(mEditTxt.getWindowToken(), 0);
                mEditTxt.clearFocus();
            }

            lastInputInput = text;
        }
    };

    @Override
    public void serverPwd(MobileStatus status) {
        mMobile = mEditTxt.getText().toString().trim();
        mBtn.setEnabled(false);
        App.mAxLoginSp.setUserStatue(status.name());
        if (status.name().equals(MobileStatus.OK.name())) {
            showVerPwdView();
        } else if (status.name().equals(MobileStatus.NoPassword.name())) {
            showSetPwdView();
        } else {//NotRegistered same as LoginSetPwd ,easy to see.
            showSetPwdView();
        }
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
    public void getmodifyPwdInfoResult(ArrayList<VerifyField> verify_fields) {}

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && (mCurrStatus.equals(STATUE.VERPWD) || mCurrStatus.equals(STATUE.SETPWD))){
            mEditTxt.setText("");
            showBackToInputAnim();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void showBackToInputAnim(){
        mGifViewNext.setVisibility(View.GONE);
        mGifViewInput.setVisibility(View.GONE);
        mGitViewNextSet.setVisibility(View.GONE);

        if(mBackGifView.isPaused()){
            mBackGifView.setPaused(false);
        } else{
            mBackGifView.setMovieResource(R.raw.comp_4reverse);
        }
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Message msg = new Message();
                msg.what = 3;
                handler.sendMessage(msg);
                //mBackGifView.setPaused(true);
            }
        };
        timer.schedule(timerTask,mBackGifView.getDuration()-20);
        mBackGifView.setVisibility(View.VISIBLE);
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                mBackGifView.setPaused(true);
//            }
//        }, mBackGifView.getDuration()-20);

        if(mCurrStatus.equals(STATUE.VERPWD)){
            mPwdEdit.setText("");
            mSetPwdEdit.setText("");

            mVerPwdView.setVisibility(View.GONE);
            mEditTxt.setVisibility(View.VISIBLE);
            ObjectAnimator anim = ObjectAnimator.ofFloat(mEditTxt, "alpha", 0f, 1f);
            anim.setDuration(1000).start();
        } else{
            mPwdEdit.setText("");

            mEditTxt.setVisibility(View.VISIBLE);
            ObjectAnimator.ofFloat(mEditTxt, "alpha", 0f, 1f).setDuration(1000).start();
            mSetPwdCoverView.setVisibility(View.VISIBLE);
            ObjectAnimator animator = ObjectAnimator.ofFloat(mSetPwdCoverView, "alpha", 0f, 1f);
            animator.setDuration(1000);
            animator.setDuration(1000);
            animator.start();

            ObjectAnimator animBottom = ObjectAnimator.ofFloat(mBottomView, "translationY", metrics.density*40, 0);
            animBottom.setDuration(1000);
            animBottom.setDuration(1000);
            animBottom.start();
        }
        mCurrStatus = STATUE.INPUT;
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

            if(values.length > 0 && mVerCodeText != null){
                long left = (Long) values[0];
                mVerCodeText.setText(Math.round((float)left / 1000f)+"s后重新获取");
            }
        }
    }

    public class VerController{
        private String mNormalText;
        private String mCountDownTag = getClass().getName();
        public VerController(){
            mCountDownTag = getClass().getName();
            mNormalText = mVerCodeText.getText().toString();
        }
        void verStart(){
            mVerCodeText.setTextColor(getResources().getColor(R.color.axf_light_gray));
            setState(false);
        }

        void reset(){
            setState(true);
            mVerCodeText.setText(mNormalText);
            mVerCodeText.setTextColor(getResources().getColor(R.color.axf_common_blue));
            sStartTicks.remove(mCountDownTag);
        }

        public void setState(boolean enabled){
            mVerCodeText.setEnabled(enabled);
        }

        public String getCountDownTag(){
            return mCountDownTag;
        }
    }

    @Override
    public boolean leftButtonEnabled() {
        return false;
    }

    @Override
    public void setPresenter(ILoginHasPwdContract.ILoginHasPwdPresenter presenter) {
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
}
