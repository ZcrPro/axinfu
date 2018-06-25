package com.zhihuianxin.xyaxf.app.lock.create;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.axinfu.basetools.shapeimageview.CustomShapeImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.zhihuianxin.axutil.Util;
import com.zhihuianxin.xyaxf.App;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.utils.RxBus;
import com.zhihuianxin.xyaxf.app.verification.ILoginVerityLoginPasswordContract;
import com.zhihuianxin.xyaxf.app.verification.LoginVerityLoginPwdPresenter;
import com.zhihuianxin.xyaxf.app.verification.UnlockView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import rx.Subscription;
import rx.functions.Action1;

public class GestureActivity extends Activity implements ILoginVerityLoginPasswordContract.ILoginVerityLoginPasswordView {


    @InjectView(R.id.back)
    ImageView back;
    @InjectView(R.id.touxiang)
    CustomShapeImageView mAvatarImg;
    @InjectView(R.id.gesTitleTxt)
    TextView gesTitleTxt;
    @InjectView(R.id.gesContentTxt)
    TextView gesContextTxt;
    @InjectView(R.id.unlock)
    UnlockView mUnlockView;
    @InjectView(R.id.verloginPwdtxt)
    TextView verloginPwdtxt;
    @InjectView(R.id.forgetGesPwdtxt)
    TextView forgetGesPwdtxt;
    @InjectView(R.id.grayBg)
    View mGrayBg;
    @InjectView(R.id.share_title)
    TextView dialogTitleTxt;
    @InjectView(R.id.pwdEdit)
    EditText pwdEdit;
    @InjectView(R.id.okBnt)
    Button okBnt;
    @InjectView(R.id.click_focus)
    TextView closeAniTxt;
    @InjectView(R.id.backAnimView)
    RelativeLayout mBackAlertView;
    @InjectView(R.id.rl_topbar)
    RelativeLayout topbar;

    public static final String INITIATIVE = "INITIATIVE";
    public static final String CHANGE = "change_to_finger";
    public static final String SETTING = "SETTING";
    @InjectView(R.id.mobile)
    TextView mobile;

    private FingerprintManagerCompat manager;
    private KeyguardManager mKeyManager;
    private DisplayMetrics metrics;

    private boolean initiativeSet = false; //是否是主动验证 主动可以返回 被动不可返回 并且如果是主动 重置密码按钮不显示
    private boolean change_to_finger = false; //是否是切换为指纹识别解锁
    private boolean setting = false; //是否是从设置关闭进来的

    private int errorTimes;

    private String pwdInMem; //正确的手势密码
    private String currPwd;  //用户当前输入的手势密码

    private ILoginVerityLoginPasswordContract.ILoginVerityLoginPasswordPresenter presenter;

    private LockType type;

    private Subscription rxSubscription;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gesture_activity);
        ButterKnife.inject(this);
        manager = FingerprintManagerCompat.from(this);//FingerprintManager) this.getSystemService(Context.FINGERPRINT_SERVICE);
        mKeyManager = (KeyguardManager) this.getSystemService(Context.KEYGUARD_SERVICE);
        initLockData();

        rxSubscription = RxBus.getDefault().toObservable().subscribe(new Action1<Object>() {
            @Override
            public void call(Object event) {
                if (event.equals("close")) {
                    finish();
                }
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        final LockInfo lockInfo = SQLite.select()
                .from(LockInfo.class)
                .where(LockInfo_Table.regist_serial.is(App.mAxLoginSp.getRegistSerial()))
                .querySingle();

        if (lockInfo != null && lockInfo.unionGestureEorTimes > 0) {
            errorTimes = lockInfo.unionGestureEorTimes;
            gesContextTxt.setText("手势密码输入错误,你还有" + (5 - lockInfo.unionGestureEorTimes) + "次机会");
        }
        if (errorTimes >= 5) {
            if (setting){
                type = LockType.FORGHT;
                dialogTitleTxt.setText("请输入登录密码，以验证身份");
                showAlertAnim();
            }else {
                type = LockType.RESET;
                dialogTitleTxt.setText("请输入登录密码，以重置手势密码");
                showAlertAnim();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!rxSubscription.isUnsubscribed()) {
            rxSubscription.unsubscribe();
        }
    }

    @SuppressLint("SetTextI18n")
    private void initLockData() {
        //获取是否是主动设置或者被动设置
        if (getIntent().getExtras() != null) {
            this.initiativeSet = getIntent().getExtras().getBoolean(INITIATIVE);
        }

        if (getIntent().getExtras() != null) {
            this.change_to_finger = getIntent().getExtras().getBoolean(CHANGE);
        }

        if (getIntent().getExtras() != null) {
            this.setting = getIntent().getExtras().getBoolean(SETTING);
        }

        //判断是否应该有titlebar
        if (!initiativeSet) {
            topbar.setVisibility(View.GONE);
        }

        //如果是主动设置解锁
        if (initiativeSet) {
            forgetGesPwdtxt.setVisibility(View.GONE);
        }

        //查询关于该用户下的解锁数据
        final LockInfo lockInfo = SQLite.select()
                .from(LockInfo.class)
                .where(LockInfo_Table.regist_serial.is(App.mAxLoginSp.getRegistSerial()))
                .querySingle();

        new LoginVerityLoginPwdPresenter(this, this);
        assert lockInfo != null;
        errorTimes = lockInfo.unionGestureEorTimes;
        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        initImg();
        String mobile = App.mAxLoginSp.getUserMobil();
        gesTitleTxt.setText(mobile.substring(0, 3) + "****" + mobile.substring(mobile.length() - 4, mobile.length()));
        pwdInMem = lockInfo.gesturePassword;
        mUnlockView.setGestureListener(new UnlockView.CreateGestureListener() {
            @Override
            public void onGestureCreated(String result) {
            }
        });
        mUnlockView.setMode(UnlockView.CHECK_MODE);
        mUnlockView.setOnUnlockListener(new UnlockView.OnUnlockListener() {
            @Override
            public boolean isUnlockSuccess(String result) {
                currPwd = result;
                if (result.length() < 4) {
                    Toast.makeText(GestureActivity.this, "手势密码长度不能少于4个点", Toast.LENGTH_SHORT).show();
                    return false;
                } else {
                    return result.equals(pwdInMem);
                }
            }

            @Override
            public void onSuccess() {
                lockInfo.unionGestureEorTimes = 0;
                lockInfo.save();
                if (!initiativeSet) {
                    //被动解锁不是设置不需要开关
                    finish();
                } else {
                    //需要开关
                    //查询关于该用户下的解锁数据
                    final LockInfo lockInfo = SQLite.select()
                            .from(LockInfo.class)
                            .where(LockInfo_Table.regist_serial.is(App.mAxLoginSp.getRegistSerial()))
                            .querySingle();

                    if (lockInfo.gestureStatus) {
                        if (change_to_finger) {
                            lockInfo.fingerStatus = true;
                        }
                        lockInfo.gestureStatus = false;
                        lockInfo.remindStatus = true;
                        lockInfo.save();
                    } else {
                        lockInfo.gestureStatus = true;
                        lockInfo.remindStatus = true;
                        lockInfo.save();
                    }
                }
                finish();
            }

            @Override
            public void onFailure() {
                App.mAxLoginSp.setLcokFalse(false);
                if (currPwd.length() >= 4) {
                    lockInfo.unionGestureEorTimes = lockInfo.unionGestureEorTimes + 1;
                    lockInfo.save();
                    errorTimes++;
                    App.mAxLoginSp.setUnionGestureEorTimes(errorTimes + "");

                    if (errorTimes >= 5) {
                        gesContextTxt.setText("手势密码已被锁定,请点击\"验证登录密码\"验证");
                        dialogTitleTxt.setText("请输入登录密码，以验证身份");
                        type = LockType.FORGHT;
                        showAlertAnim();
                        lockInfo.unionGestureEorTimes = 5;
                        lockInfo.save();
                        mUnlockView.setEnable(false);
                    } else {
                        gesContextTxt.setText("手势密码输入错误,你还有" + (5 - errorTimes) + "次机会");
                    }
                }
            }
        });


        if (errorTimes >= 5 || lockInfo.unionGestureEorTimes >= 5) {
            gesContextTxt.setText("手势密码已被锁定,请点击\"忘记手势密码\"重置");
            mUnlockView.setEnable(false);
            dialogTitleTxt.setText("请输入登录密码，以重置手势密码");
            type = LockType.RESET;
            showAlertAnim();
        } else {
            mUnlockView.setEnable(true);
        }


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void verityLoginPwdResult() {
        if (type.equals(LockType.FORGHT)) {
            if (initiativeSet) {
                //是设置界面关闭或打开解锁
                final LockInfo lockInfo = SQLite.select()
                        .from(LockInfo.class)
                        .where(LockInfo_Table.regist_serial.is(App.mAxLoginSp.getRegistSerial()))
                        .querySingle();

                if (lockInfo.gestureStatus) {
                    //指纹解锁开启--- 关闭
                    if (change_to_finger) {
                        lockInfo.fingerStatus = true;
                    }
                    lockInfo.gestureStatus = false;
                    lockInfo.remindStatus = true;
                    lockInfo.save();
                } else {
                    //指纹解锁关闭--- 开启
                    lockInfo.gestureStatus = true;
                    lockInfo.remindStatus = true;
                    lockInfo.save();
                }
                finish();
            } else {
                //如果是验证密码直接验证完了关闭该界面
                finish();
            }
        } else if (type.equals(LockType.RESET)) {
            Bundle bundle = new Bundle();
            bundle.putBoolean(SetGestureActivity.INITIATIVE, true);
            Intent i = new Intent(GestureActivity.this, SetGestureActivity.class);
            i.putExtras(bundle);
            startActivity(i);
            hideBackAlertAnim();
            pwdEdit.setText("");
        }


    }

    @Override
    public void setPresenter(ILoginVerityLoginPasswordContract.ILoginVerityLoginPasswordPresenter presenter) {
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

    private void initImg() {
        final String url = App.mAxLoginSp.getAvatarUrl();
        DisplayImageOptions config = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.loadImage(url, config, new SimpleImageLoadingListener() {

            @Override
            public void onLoadingComplete(String imageUri, View view,
                                          Bitmap loadedImage) {
                if (!Util.isEmpty(url)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        mAvatarImg.setBackground(null);
                    }
                }

                mAvatarImg.setImageBitmap(loadedImage);
            }
        });
    }

    private void showAlertAnim() {
        int halfScreen = (metrics.heightPixels / 2) + 200;
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(mBackAlertView, "translationY", 0,
                halfScreen, halfScreen + 50, halfScreen + 10, halfScreen + 35, halfScreen + 30);//450 410 435 430
        animator2.setDuration(700);
        animator2.start();
        mGrayBg.setVisibility(View.VISIBLE);

        closeAniTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideBackAlertAnim();
            }
        });
    }

    private void hideBackAlertAnim() {
        int halfScreen = (metrics.heightPixels / 2) + 200;
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(mBackAlertView, "translationY", halfScreen + 30, 0);
        animator2.setDuration(600);
        animator2.start();
        mGrayBg.setVisibility(View.GONE);
    }


    @OnClick(R.id.okBnt)
    public void okBtnClick(View view) {
        if (Util.isEmpty(pwdEdit.getText().toString().trim())) {
            Toast.makeText(this, "请输入登录密码", Toast.LENGTH_SHORT).show();
        } else {
            presenter.verityLoginPwd(pwdEdit.getText().toString().trim());
        }
    }

    @OnClick(R.id.verloginPwdtxt)
    public void onVerLoginPwdClick(View view) {
        type = LockType.FORGHT;
        dialogTitleTxt.setText("请输入登录密码，以验证身份");
        showAlertAnim();
    }

    @OnClick(R.id.forgetGesPwdtxt)
    public void onForgetGesTureClick(View view) {
        type = LockType.RESET;
        dialogTitleTxt.setText("请输入登录密码，以重置手势密码");
        showAlertAnim();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void exit() {
        if (initiativeSet) {
            finish();
        }
    }
}
