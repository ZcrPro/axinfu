package com.zhihuianxin.xyaxf.app.verification;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.axinfu.modellib.thrift.lock.Lock;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.zhihuianxin.xyaxf.app.base.axutil.Util;
import com.zhihuianxin.xyaxf.App;
import com.zhihuianxin.xyaxf.MainActivity;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.me.view.activity.MeSettingActivity;
import com.zhihuianxin.xyaxf.app.utils.RxBus;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import io.realm.LockRealmProxy;
import io.realm.Realm;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by zcrpro on 2018/1/29.
 */

public class UnionVerGestureActivity extends Activity implements ILoginVerityLoginPasswordContract.ILoginVerityLoginPasswordView {
    public static final String EXTRA_FROM_VER_GES = "fromVer";
    @InjectView(R.id.unlock)
    UnlockView mUnlockView;
    @InjectView(R.id.touxiang)
    ImageView mAvatarImg;
    @InjectView(R.id.gesTitleTxt)
    TextView gesTitleTxt;
    @InjectView(R.id.gesContentTxt)
    TextView gesContextTxt;
    @InjectView(R.id.grayBg)
    View mGrayBg;
    @InjectView(R.id.backAnimView)
    View mBackAlertView;
    @InjectView(R.id.click_focus)
    TextView closeAniTxt;
    @InjectView(R.id.pwdEdit)
    EditText pwdEdit;
    @InjectView(R.id.share_title)
    TextView dialogTitleTxt;
    @InjectView(R.id.back)
    ImageView back;

    private long exitTime = 0;

    private boolean loginlock = false;

    private ILoginVerityLoginPasswordContract.ILoginVerityLoginPasswordPresenter presenter;
    private String pwdInMem;
    private int errorTimes;
    private String currPwd;
    private DisplayMetrics metrics;
    private boolean isReSetGesture = false;
    private boolean no_finger = false;

    private Subscription rxSubscription;

    private boolean yanzheng =false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.union_gesture_ver_activity);
        ButterKnife.inject(this);
        initViews();

        if (getIntent().getExtras() != null && getIntent().getExtras().getBoolean("canBACK")) {
            back.setVisibility(View.VISIBLE);
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        }

        if (getIntent().getExtras() != null) {
            no_finger = getIntent().getExtras().getBoolean("no_finger");
        }

        rxSubscription = RxBus.getDefault().toObservable().subscribe(new Action1<Object>() {
            @Override
            public void call(Object event) {
                 if (event.equals("no_add_card")) {
                     startActivity(new Intent(UnionVerGestureActivity.this, MainActivity.class));
                     finish();
                }
            }
        });

        App.mAxLoginSp.setLcokFalse(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!rxSubscription.isUnsubscribed()) {
            rxSubscription.unsubscribe();
        }
    }

    private void initViews() {
        new LoginVerityLoginPwdPresenter(this, this);
        errorTimes = Integer.parseInt(App.mAxLoginSp.getUnionGestureEorTimes());
        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        initImg();
        String mobile = App.mAxLoginSp.getUserMobil();
        gesTitleTxt.setText(mobile.substring(0, 3) + "****" + mobile.substring(mobile.length() - 4, mobile.length()));
        pwdInMem = App.mAxLoginSp.getUnionGesture();
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
                    Toast.makeText(UnionVerGestureActivity.this, "手势密码长度不能少于4个点", Toast.LENGTH_SHORT).show();
                    return false;
                } else {
                    return result.equals(pwdInMem);
                }
            }

            @Override
            public void onSuccess() {
                App.mAxLoginSp.setLcokFalse(true);
                App.mAxLoginSp.setUnionGestureEorTimes("0");
                App.mAxLoginSp.setUnionGesture5EorTimes("0");
                if (getIntent().getExtras() != null && getIntent().getExtras().getString(MeSettingActivity.EXTRA_VER_GES_FROM_SETTING) != null) {
//                    App.mAxLoginSp.setUnionGestureOpenStatus(getIntent().getExtras().getString(MeSettingActivity.EXTRA_VER_GES_FROM_SETTING));
                    if (getIntent().getExtras().getString(MeSettingActivity.EXTRA_VER_GES_FROM_SETTING).equals("1")) {
                        Realm.getDefaultInstance().executeTransactionAsync(new Realm.Transaction() {
                            @Override
                            public void execute(Realm bgRealm) {
                                Lock lock = new Lock();
                                lock.mobile = App.mAxLoginSp.getUserMobil();
                                lock.gestureStatus = true;
                                lock.gesturePassword = currPwd;
                                bgRealm.copyToRealmOrUpdate(lock);
                            }
                        });
                    } else {
                        Realm.getDefaultInstance().executeTransactionAsync(new Realm.Transaction() {
                            @Override
                            public void execute(Realm bgRealm) {
                                Lock lock = new Lock();
                                lock.mobile = App.mAxLoginSp.getUserMobil();
                                lock.gestureStatus = false;
                                lock.gesturePassword = currPwd;
                                bgRealm.copyToRealmOrUpdate(lock);
                            }
                        });
                    }
                    finish();
                } else {
                    setResult(RESULT_OK);
                    finish();
                }
            }

            @Override
            public void onFailure() {
                App.mAxLoginSp.setLcokFalse(false);
                if (currPwd.length() >= 4) {
                    errorTimes++;
                    App.mAxLoginSp.setUnionGestureEorTimes(errorTimes + "");

                    if (errorTimes >= 5) {
                        gesContextTxt.setText("手势密码已被锁定,请点击\"忘记手势密码\"重置");
//                        dialogTitleTxt.setText("请输入登录密码，以验证身份");
//                        showAlertAnim();

                        isReSetGesture = false;
                        loginlock =true;
                        dialogTitleTxt.setText("请输入登录密码，以验证身份");
                        showAlertAnim();

                        App.mAxLoginSp.setUnionGesture5EorTimes("1");

                        mUnlockView.setEnable(false);
                    } else {
                        gesContextTxt.setText("手势密码输入错误,你还有" + (5 - errorTimes) + "次机会");
                    }
                }
            }
        });

        if (App.mAxLoginSp.getUnionGesture5EorTimes().equals("1")) {
            gesContextTxt.setText("手势密码已被锁定,请点击\"忘记手势密码\"重置");
            mUnlockView.setEnable(false);
            dialogTitleTxt.setText("请输入登录密码，以重置手势密码");
            showAlertAnim();
            isReSetGesture = true;
        } else {
            mUnlockView.setEnable(true);
        }
    }

    @OnClick(R.id.okBnt)
    public void okBtnClick(View view) {
        if (Util.isEmpty(pwdEdit.getText().toString().trim())) {
            Toast.makeText(this, "请输入登录密码", Toast.LENGTH_SHORT).show();
        } else {
            presenter.verityLoginPwd(pwdEdit.getText().toString().trim());
        }
    }

    @OnClick(R.id.grayBg)
    public void bgOnClick(View view) {
    }

    @OnClick(R.id.verloginPwdtxt)
    public void onVerLoginPwdClick(View view) {
        isReSetGesture = false;
        loginlock =true;
        yanzheng=true;
        dialogTitleTxt.setText("请输入登录密码，以验证身份");
        showAlertAnim();
    }

    @OnClick(R.id.forgetGesPwdtxt)
    public void onForgetGesTureClick(View view) {
        isReSetGesture = true;
        dialogTitleTxt.setText("请输入登录密码，以重置手势密码");
        showAlertAnim();
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

    @Override
    public void verityLoginPwdResult() {
        App.mAxLoginSp.setLcokFalse(true);
        if(!yanzheng){
            if (no_finger){
                Intent intent = new Intent(this, UnionSetGestureActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(MeSettingActivity.EXTRA_VER_GES_FROM_SETTING, getIntent().getExtras().getString(MeSettingActivity.EXTRA_VER_GES_FROM_SETTING));
                bundle.putBoolean("reset", true);
                bundle.putBoolean("no_direct", true);
                bundle.putBoolean("canBACK", true);
                bundle.putBoolean("NotNeedFinger", true);
                intent.putExtras(bundle);
                startActivity(intent);
                return;
            }

            if (getIntent().getExtras() != null && getIntent().getExtras().getString(MeSettingActivity.EXTRA_VER_GES_FROM_SETTING) != null) {
                // 设置进来的
                if (isReSetGesture) {// 重置手势密码
                    if (loginlock){
                        startActivity(new Intent(UnionVerGestureActivity.this, MainActivity.class));
                        finish();
                    }else {
                        Intent intent = new Intent(this, UnionSetGestureActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString(MeSettingActivity.EXTRA_VER_GES_FROM_SETTING, getIntent().getExtras().getString(MeSettingActivity.EXTRA_VER_GES_FROM_SETTING));
                        bundle.putBoolean("reset", true);
                        bundle.putBoolean("canBACK", true);
                        bundle.putBoolean("no_direct", true);
                        bundle.putBoolean("NotNeedFinger", true);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        App.no_add_card= true;
                        App.no_add_card_and_pay= false;
                    }
                } else {// 通过登陆密码打开或者关闭手势密码
                    if (getIntent().getExtras().getString(MeSettingActivity.EXTRA_VER_GES_FROM_SETTING).equals("1")) {
//                            App.mAxLoginSp.setUnionGestureOpenStatus("1");
                        Realm.getDefaultInstance().executeTransactionAsync(new Realm.Transaction() {
                            @Override
                            public void execute(Realm bgRealm) {
                                Lock lock = new Lock();
                                final Lock lock2 = Realm.getDefaultInstance().where(Lock.class).equalTo("mobile", App.mAxLoginSp.getUserMobil()).findFirst();
                                lock.mobile = App.mAxLoginSp.getUserMobil();
                                lock.gestureStatus = true;
                                if (lock2 != null)
                                    lock.gesturePassword = ((LockRealmProxy) lock2).realmGet$gesturePassword();
                                bgRealm.copyToRealmOrUpdate(lock);
                                App.mAxLoginSp.setUserLcok(false);
                            }
                        });
                    } else {
                        Realm.getDefaultInstance().executeTransactionAsync(new Realm.Transaction() {
                            @Override
                            public void execute(Realm bgRealm) {
                                Lock lock = new Lock();
                                final Lock lock2 = Realm.getDefaultInstance().where(Lock.class).equalTo("mobile", App.mAxLoginSp.getUserMobil()).findFirst();
                                lock.mobile = App.mAxLoginSp.getUserMobil();
                                lock.gestureStatus = false;
                                if (lock2 != null)
                                    lock.gesturePassword = ((LockRealmProxy) lock2).realmGet$gesturePassword();
                                bgRealm.copyToRealmOrUpdate(lock);
                                App.mAxLoginSp.setUserLcok(true);
                            }
                        });
                    }
                }
                finish();
            } else {
                if (isReSetGesture) {// 生活进来的,重置手势返回要进入设置手势密码
                    Intent i = new Intent(UnionVerGestureActivity.this,UnionSetGestureActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("no_direct", true);
                    bundle.putBoolean("canBACK", true);
                    bundle.putBoolean("NotNeedFinger", true);
                    bundle.putString(EXTRA_FROM_VER_GES, "1");
                    i.putExtras(bundle);
                    startActivity(i);
                    setResult(RESULT_OK, i);
                    finish();
                } else {// 生活进来的,通过登陆密码验证返回需要进入被扫页面
                    setResult(RESULT_OK);
                    finish();
                }
            }
        }else {
            //只是验证
//            startActivity(new Intent(UnionVerGestureActivity.this, MainActivity.class));
            finish();
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


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void exit() {

    }

}
