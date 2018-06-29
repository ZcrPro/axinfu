package com.zhihuianxin.xyaxf.app.login.view.activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zhihuianxin.xyaxf.app.AppConstant;
import com.axinfu.modellib.service.MeService;
import com.axinfu.modellib.thrift.app.Update;
import com.axinfu.modellib.thrift.customer.Customer;
import com.axinfu.modellib.thrift.customer.MobileStatus;
import com.axinfu.modellib.thrift.customer.VerifyField;
import com.google.gson.Gson;
import com.zhihuianxin.xyaxf.app.base.axutil.Util;
import com.zhihuianxin.xyaxf.App;
import com.zhihuianxin.xyaxf.BaseRealmActionBarActivity;
import com.zhihuianxin.xyaxf.BuildConfig;
import com.zhihuianxin.xyaxf.MainActivity;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.ApiFactory;
import com.zhihuianxin.xyaxf.app.BaseSubscriber;
import com.zhihuianxin.xyaxf.app.RetrofitFactory;
import com.zhihuianxin.xyaxf.app.login.contract.ILoginHasPwdContract;
import com.zhihuianxin.xyaxf.app.login.presenter.LoginHasPwdPresenter;
import com.zhihuianxin.xyaxf.app.login.view.fragment.LoginSelectCityActivity;
import com.zhihuianxin.xyaxf.app.me.presenter.MeCheckUpdatePresenter;
import com.zhihuianxin.xyaxf.app.me.view.fragment.MeFragment;
import com.zhihuianxin.xyaxf.app.utils.NetUtils;
import com.zhihuianxin.xyaxf.app.view.DownloadAppDialog;
import com.zhihuianxin.xyaxf.app.view.DownloadAppForceDialog;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import io.realm.Realm;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.zhihuianxin.xyaxf.app.login.view.activity.LoginGetPwdActivity.EXTRA_VERIFY_DATA;

/**
 * Created by Vincent on 2016/12/19.
 */

public class LoginInputMobilActivityNew extends BaseRealmActionBarActivity implements ILoginHasPwdContract.ILoginHasPwdView {
    public static final int DEFAULT_COUNT = AppConstant.DEFAULT_COUNT;
    private static final HashMap<String, Long> sStartTicks = new HashMap<String, Long>();
    @InjectView(R.id.tv_version)
    TextView tvVersion;
    @InjectView(R.id.check_update_next)
    TextView checkUpdateNext;

    private int mCountDownDuration = DEFAULT_COUNT;

    enum STATUE {
        INPUT(), SETPWD(), VERPWD()
    }

    private STATUE mCurrStatus = STATUE.INPUT;

    @InjectView(R.id.login_forgetpwd)
    TextView mForgetPwdText;
    @InjectView(R.id.back_icon)
    View mBackImg;
    @InjectView(R.id.inputEdit)
    EditText mEditTxt;
    @InjectView(R.id.next)
    Button mBtn;
//    @InjectView(R.id.tv_debug_version)
//    TextView tvDebugVersion;
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
    @InjectView(R.id.setPwdAllView)
    View mSetPwdAllView;
    @InjectView(R.id.gifView)
    GifImageView mGifView;
    @InjectView(R.id.top_title)
    TextView mTopText;

    private boolean isHiddenVer = true;
    private boolean isSetPwdHiddenVer = true;
    private ILoginHasPwdContract.ILoginHasPwdPresenter presenter;
    private DisplayMetrics metrics;
    private String mMobile;
    private VerController verController;
    private DownloadAppForceDialog appUpdateForceDialog;
    private DownloadAppDialog appUpdateDialog;

    @Override
    protected void onResume() {
        super.onResume();
        mEditTxt.setText("");
        mPwdEdit.setText("");
        mSetPwdEdit.setText("");
        mSetPwdVerCodeEdit.setText("");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);

        initView();
        showAnim();
    }

    private void initView() {
        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        appUpdateDialog = new DownloadAppDialog(this);
        //appUpdateDialog.create();
        appUpdateForceDialog = new DownloadAppForceDialog(this);
        //appUpdateForceDialog.create();
        final RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int) (metrics.widthPixels * 0.9), metrics.heightPixels / 3);
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        mGifView.post(new Runnable() {
            @Override
            public void run() {
                mGifView.setLayoutParams(params);
            }
        });
        verController = new VerController();
        new LoginHasPwdPresenter(this, this);
        mEditTxt.addTextChangedListener(watcherInput);
        //mEditTxt.setText(App.mAxLoginSp.getUserMobil());
        mPwdEdit.addTextChangedListener(watchVer);
        mSetPwdEdit.addTextChangedListener(watcherPwd);
        mSetPwdVerCodeEdit.addTextChangedListener(verCodeWatch);
        findViewById(R.id.action_bar).setVisibility(View.GONE);
        mTopText.setText("输入手机号");
//        if (BuildConfig.AnXinDEBUG) {
//            PackageInfo pi = Util.getPackageInfo(this);
//            if (tvDebugVersion != null && pi != null) {
//                tvDebugVersion.setText(String.format("v%s(%s)", pi.versionName, pi.versionCode));
//            }
//        }

        PackageInfo pi = Util.getPackageInfo(this);
        if (tvVersion != null && pi != null) {
            tvVersion.setText(String.format("版本号：v%s(%s)", pi.versionName, pi.versionCode));
        }

        checkUpdateNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkUpdateNext.getText().equals("下载更新")){
                    checkUpdateNext.setText("检查更新");
                    //downloadApk(App.mAxLoginSp.getUpdateUrl());
                    showUpdateDialog();
                } else{
                    checkUpdate();
                }
            }
        });

    }

    private void showAnim() {
        mForgetPwdText.setVisibility(View.GONE);
        mBackImg.setVisibility(View.GONE);
        try {
            GifDrawable gifFromResource = new GifDrawable(getResources(), R.raw.comp_3);
            mGifView.setImageDrawable(gifFromResource);
            gifFromResource.setLoopCount(1);
            gifFromResource.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showVerPwdView() {
        mForgetPwdText.setVisibility(View.VISIBLE);
        mBackImg.setVisibility(View.VISIBLE);
        mCurrStatus = STATUE.VERPWD;
        mTopText.setText("输入密码");
        //Glide.with(this).load(R.raw.comp_4).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(new GlideDrawableImageViewTarget(mGifView,1));
        try {
            GifDrawable gifFromResource = new GifDrawable(getResources(), R.raw.comp_4);
            mGifView.setImageDrawable(gifFromResource);
            gifFromResource.setLoopCount(1);
            gifFromResource.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mVerPwdView.setVisibility(View.VISIBLE);
        ObjectAnimator anim = ObjectAnimator.ofFloat(mEditTxt, "alpha", 1f, 0f);
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mEditTxt.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        anim.setDuration(1000);
        anim.start();
    }

    private void showUpdateDialog(){
        if(App.mAxLoginSp.getAppApkDone()){
            appUpdateDialog.startDownloadService();
            appUpdateDialog.setStart();
        } else{
            appUpdateDialog.show();
        }
    }

    private void showUpdateForceDialog() {
        if(App.mAxLoginSp.getAppApkDone()){
            appUpdateForceDialog.startDownloadService();
        } else{
            appUpdateForceDialog.show();
        }
    }

    private void showSetPwdView() {
        mBackImg.setVisibility(View.VISIBLE);
        mCurrStatus = STATUE.SETPWD;
        if (App.mAxLoginSp.getUserStatue().equals(MobileStatus.NotRegistered.name())) {
            mTopText.setText("注册");
        } else {
            mTopText.setText("登录密码设置");
        }

        //Glide.with(this).load(R.raw.comp_4).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(new GlideDrawableImageViewTarget(mGifView,1));
        try {
            GifDrawable gifFromResource = new GifDrawable(getResources(), R.raw.comp_4);
            mGifView.setImageDrawable(gifFromResource);
            gifFromResource.setLoopCount(1);
            gifFromResource.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mVerPwdView.setVisibility(View.GONE);
        mEditTxt.setVisibility(View.GONE);
        ObjectAnimator.ofFloat(mEditTxt, "alpha", 1f, 0f).setDuration(1000).start();

        ObjectAnimator animator = ObjectAnimator.ofFloat(mSetPwdCoverView, "alpha", 1f, 0f);
        animator.setDuration(1000);
        animator.setDuration(1000);
        animator.start();
        mSetPwdCoverView.setVisibility(View.GONE);

        ObjectAnimator animBottom = ObjectAnimator.ofFloat(mBottomView, "translationY", 0f, metrics.density * 40);
        animBottom.setDuration(1000);
        animBottom.setDuration(1000);
        animBottom.start();
    }

    private void showBackToInputAnim() {
        mForgetPwdText.setVisibility(View.GONE);
        mBackImg.setVisibility(View.GONE);
        try {
            GifDrawable gifFromResource = new GifDrawable(getResources(), R.raw.comp_4reverse);
            mGifView.setImageDrawable(gifFromResource);
            gifFromResource.setLoopCount(1);
            gifFromResource.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (mCurrStatus.equals(STATUE.VERPWD)) {
            mPwdEdit.setText("");

            mVerPwdView.setVisibility(View.GONE);
            mEditTxt.setVisibility(View.VISIBLE);
            ObjectAnimator anim = ObjectAnimator.ofFloat(mEditTxt, "alpha", 0f, 1f);
            anim.setDuration(1000).start();
        } else {
            mPwdEdit.setText("");
            mSetPwdEdit.setText("");
            mSetPwdVerCodeEdit.setText("");

            mEditTxt.setVisibility(View.VISIBLE);
            ObjectAnimator.ofFloat(mEditTxt, "alpha", 0f, 1f).setDuration(1000).start();
            mSetPwdCoverView.setVisibility(View.VISIBLE);
            ObjectAnimator animator = ObjectAnimator.ofFloat(mSetPwdCoverView, "alpha", 0f, 1f);
            animator.setDuration(1000);
            animator.setDuration(1000);
            animator.start();

            ObjectAnimator animBottom = ObjectAnimator.ofFloat(mBottomView, "translationY", metrics.density * 40, 0);
            animBottom.setDuration(1000);
            animBottom.setDuration(1000);
            animBottom.start();
        }
        mCurrStatus = STATUE.INPUT;
    }

    @Override
    protected int getContentViewId() {
        return R.layout.login_input_activity;
    }

    @OnClick(R.id.setPwdCoverView)
    public void onBtnPwdCoverView() {

    }

    @OnClick(R.id.getver)
    public void onBtnGetVer() {
        presenter.getVerCode(mMobile, App.mAxLoginSp.getUserStatue());
        LoginSendVerTask task = new LoginSendVerTask();
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @OnClick(R.id.next)
    public void onBtnNext() {
        if (mCurrStatus.equals(STATUE.INPUT)) {
            presenter.hasSetPwd(mEditTxt.getText().toString().trim());
        } else if (mCurrStatus.equals(STATUE.VERPWD)) {
            presenter.login(mMobile, mPwdEdit.getText().toString().trim(), App.mAxLoginSp.getUUID().replace("-", ""));
        } else {
            presenter.setPwdOrRegistAndLogin(mMobile, mSetPwdVerCodeEdit.getText().toString().trim(),
                    mSetPwdEdit.getText().toString().trim(), App.mAxLoginSp.getUUID().replace("-", ""));
        }
        mBtn.setEnabled(false);
    }

    @OnClick(R.id.login_forgetpwd)
    public void onBtnGetPwd() {
        presenter.getmodifyPwdInfo(mMobile);
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
    public void onBtnPwdLookSet() {
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
            if (text.length() >= 6 && mSetPwdVerCodeEdit.getText().length() == 4) {
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

            if (text.length() == 4) {
                mSetPwdEdit.requestFocus();
            }

            if (text.length() == 4 && mSetPwdEdit.getText().length() >= 6) {
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
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            Pattern p = Pattern.compile(BuildConfig.AnXinDEBUG ? Util.REGEX_MOBILE_DEBUG : Util.REGEX_MOBILE);
            if (p.matcher(charSequence.toString()).matches()) {
                mBtn.setEnabled(true);
            } else {
                mBtn.setEnabled(false);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            String text = s.toString();
            if (text.equals(lastInputInput)) {
                return;
            }

            if (text.length() == 11) {
                InputMethodManager imm = (InputMethodManager) getSystemService("input_method");//Context.INPUT_METHOD_SERVICE
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
        App.mAxLoginSp.setUserMobil(mEditTxt.getText().toString().trim());
        if (status.name().equals(MobileStatus.OK.name())) {
            showVerPwdView();
        } else if (status.name().equals(MobileStatus.NoPassword.name())) {
            showSetPwdView();// 已经注册过的老用户，不用注册。
        } else {
            showSetPwdView();
        }
    }

    private void goLoginSelectCityActivity() {
        Intent intent = new Intent(this, LoginSelectCityActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(LoginSelectCityActivity.EXTRA_FROM_LOGIN, "from_login");
        intent.putExtras(bundle);
        startActivity(intent);
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

        if (customer.school != null) {
            App.mAxLoginSp.setLoginSign(true);
            startActivity(new Intent(this, MainActivity.class));
        } else {
            goLoginSelectCityActivity();
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
        App.mSession.setSession(session);
        App.mAxLoginSp.setRegistSerial(customer.base_info.regist_serial);

        if (customer.school != null) {
            startActivity(new Intent(this, MainActivity.class));
        } else {
            goLoginSelectCityActivity();
        }
        finish();
    }

    @Override
    public void getVerCodeSuccess(String verCode) {
        App.mAxLoginSp.setVerCode(verCode);
        Toast.makeText(this, verCode, Toast.LENGTH_LONG).show();
    }

    @Override
    public void getmodifyPwdInfoResult(ArrayList<VerifyField> verify_fields) {
        if (verify_fields != null && verify_fields.size() > 0) {
            Intent intent = new Intent(this, LoginGetPwdActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(EXTRA_VERIFY_DATA, verify_fields);
            bundle.putString(LoginGetPwdActivity.EXTRA_FROM_UNLOGIN, "from_unlogin");
            intent.putExtras(bundle);
            startActivity(intent);
        } else {
            startActivity(new Intent(this, LoginGetPwdByCodeActivity.class));
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && (mCurrStatus.equals(STATUE.VERPWD) || mCurrStatus.equals(STATUE.SETPWD))) {
            backToInputMobilResetVer();
            mTopText.setText("输入手机号");
            mEditTxt.setText("");
            showBackToInputAnim();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private boolean stopVerCode = false;
    public class LoginSendVerTask extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] params) {
            Long startMilli = sStartTicks.get(verController.getCountDownTag());
            if (startMilli == null || System.currentTimeMillis() - startMilli > mCountDownDuration) {
                startMilli = System.currentTimeMillis();
                sStartTicks.put(verController.getCountDownTag(), startMilli);
            }

            while (true) {
                long currentMilli = System.currentTimeMillis();

                long duration = currentMilli - startMilli;
                long left = mCountDownDuration - duration;
                if (left <= 0 || stopVerCode) {
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

            if (values.length > 0 && mVerCodeText != null) {
                long left = (Long) values[0];
                mVerCodeText.setText(Math.round((float) left / 1000f) + "s后重新获取");
            }
        }
    }

    public class VerController {
        private String mNormalText;
        private String mCountDownTag = getClass().getName();

        public VerController() {
            mCountDownTag = getClass().getName();
            mNormalText = mVerCodeText.getText().toString();
        }

        void verStart() {
            stopVerCode = false;
            mVerCodeText.setTextColor(getResources().getColor(R.color.axf_light_gray));
            setState(false);
        }

        void reset() {
            stopVerCode = true;

            setState(true);
            mVerCodeText.setText(mNormalText);
            mVerCodeText.setTextColor(getResources().getColor(R.color.axf_common_blue));
            sStartTicks.remove(mCountDownTag);
        }

        public void setState(boolean enabled) {
            mVerCodeText.setEnabled(enabled);
        }

        public String getCountDownTag() {
            return mCountDownTag;
        }
    }

    @OnClick(R.id.back_icon)
    public void onBtnBackIcon() {
        if (mCurrStatus.equals(STATUE.INPUT)) {
            finish();
        } else {
            backToInputMobilResetVer();
            mTopText.setText("输入手机号");
            ((TextView) findViewById(R.id.title)).setText("");
            mEditTxt.setText("");
            showBackToInputAnim();
            mCurrStatus = STATUE.INPUT;
        }
    }

    private void backToInputMobilResetVer(){
        try {
            Thread.currentThread().sleep(300);//毫秒
        }
        catch(Exception e){}
        verController.reset();
    }

    @Override
    public int getLeftButtonImageId() {
        return R.drawable.back;
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


    /**
     * 检查更新
     */
    public void checkUpdate() {
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String,Object> map = new HashMap<>();
        MeService meService = ApiFactory.getFactory().create(MeService.class);
        meService.getCheckUpdate(NetUtils.getRequestParams(this,map),NetUtils.getSign(NetUtils.getRequestParams(this,map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(this,false,null) {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(Object o) {
                        MeCheckUpdatePresenter.GetCheckUpdateResponse response = new Gson().fromJson(o.toString(),MeCheckUpdatePresenter.GetCheckUpdateResponse.class);
                        Update update = response.app_update;

                        if (update == null) {
                            return;
                        }
                        Message message = new Message();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("update",update);
                        message.setData(bundle);
                        handler.sendMessage(message);

                        if (Util.isEmpty(update.update_type) || update.update_type.equals("None")) {
                           Toast.makeText(LoginInputMobilActivityNew.this, "当前版本已是最新版", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LoginInputMobilActivityNew.this,"检测到更新，前点击下载更新",Toast.LENGTH_SHORT).show();
                            checkUpdateNext.setText("下载更新");
                        }

                        if (App.mAxLoginSp.getUpdateType().equals("Required")) {// 强制更新
                            //updateDialog(App.mAxLoginSp.getUpdateUrl());
                            showUpdateForceDialog();
                        }
                    }
                });
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Update update = (Update) msg.getData().getSerializable("update");
            App.mAxLoginSp.setVersionFromServer(Util.isEmpty(update.current_version) ? "" : update.current_version);
            App.mAxLoginSp.setUpdateType(Util.isEmpty(update.update_type) ? "" : update.update_type);
            App.mAxLoginSp.setUpdateUrl(Util.isEmpty(update.update_url) ? "" : update.update_url);
        }
    };

    private void downloadApk(String url){
        if (Util.isHTTPUrl(url) || Util.isHTTPSUrl(url)) {
            viewHttpUrl(url);
        } else {
            installLocalAPK(url);
        }
    }

    private void updateDialog(final String url) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.logoutDialog);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.update_app_force_dialog, null);
        builder.setView(view).create();
        builder.setCancelable(false);
        final AlertDialog alertDialog = builder.show();

        View update = view.findViewById(R.id.update);
        View exit = view.findViewById(R.id.exit);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadApk(url);
            }
        });

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                MeFragment.logoutOperat();

                Intent intent = new Intent(MeFragment.BROADCAST_MEFRAGMENT_CLOSE);
                sendBroadcast(intent);
                startActivity(new Intent(getActivity(), LoginInputMobilActivityNew.class));
                finish();
            }
        });
    }

    private void viewHttpUrl(String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    private void installLocalAPK(String path) {
        if (Util.isHTTPUrl(path) || Util.isHTTPSUrl(path)) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(new File(path)), "application/vnd.android.package-archive");
            startActivity(intent);
        } else {
            Toast.makeText(getActivity(), "下载路径有误！", Toast.LENGTH_LONG).show();
        }
    }

}
