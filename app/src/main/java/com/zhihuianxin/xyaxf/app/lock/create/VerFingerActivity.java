package com.zhihuianxin.xyaxf.app.lock.create;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v4.os.CancellationSignal;
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
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.zhihuianxin.axutil.Util;
import com.zhihuianxin.xyaxf.App;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.utils.RxBus;
import com.zhihuianxin.xyaxf.app.verification.ILoginVerityLoginPasswordContract;
import com.zhihuianxin.xyaxf.app.verification.LoginVerityLoginPwdPresenter;

import java.util.Timer;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import rx.Subscription;
import rx.functions.Action1;

public class VerFingerActivity extends Activity implements ILoginVerityLoginPasswordContract.ILoginVerityLoginPasswordView {

    @InjectView(R.id.avatar_id)
    CustomShapeImageView avatarId;
    @InjectView(R.id.mobile)
    TextView mobile;
    @InjectView(R.id.next2)
    ImageView next2;
    @InjectView(R.id.next)
    TextView next;
    @InjectView(R.id.grayBg)
    View mGrayBg;
    @InjectView(R.id.share_title)
    TextView dialogTitleTxt;
    @InjectView(R.id.pwdEdit)
    EditText pwdEdit;
    @InjectView(R.id.click_focus)
    TextView closeAniTxt;
    @InjectView(R.id.link_xieyi)
    TextView link_xieyi;
    @InjectView(R.id.okBnt)
    Button okBnt;
    @InjectView(R.id.backAnimView)
    RelativeLayout mBackAlertView;
    @InjectView(R.id.back)
    ImageView back;
    @InjectView(R.id.rl_topbar)
    RelativeLayout rlTopbar;

    private FingerprintManagerCompat manager;
    private KeyguardManager mKeyManager;
    private DisplayMetrics metrics;

    private int errorTimes;
    private AlertDialog scanDialog;
    private AlertDialog reScanDialog;
    private Timer timer;

    public static final String INITIATIVE = "INITIATIVE";
    public static final String CHANGE = "CHANGE";
    public static final String VER_LOGIN = "VER_LOGIN";
    private boolean initiativeSet = false; //是否是主动设置 主动设置可以返回 被动设置不可返回 并且如果是主动 指纹解锁按钮也不显示
    private boolean change_gesture = false; //是否是主动设置 主动设置可以返回 被动设置不可返回 并且如果是主动 指纹解锁按钮也不显示
    private boolean verlogin = false; //是否是突然关闭了指纹解锁

    private ILoginVerityLoginPasswordContract.ILoginVerityLoginPasswordPresenter presenter;

    private Subscription rxSubscription;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ver_finger_activity);
        ButterKnife.inject(this);
        init();

        rxSubscription = RxBus.getDefault().toObservable().subscribe(new Action1<Object>() {
            @Override
            public void call(Object event) {
                if (event.equals("手势密码设置完成")) {
                    finish();
                }
            }
        });

        if (isFinger()) {
            if (errorTimes < 5) {
                scanDialog = new AlertDialog.Builder(this)
                        .setTitle("指纹解锁")
                        .setMessage("请扫描指纹")
                        .setIcon(R.mipmap.finger)
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create();

                scanDialog.show();

                startListening(null);
            } else {
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("指纹解锁")
                        .setMessage("指纹识别错误次数超过5次请稍后再试")
                        .setIcon(R.mipmap.finger)
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create();
                dialog.show();
            }
        }else {
            dialogTitleTxt.setText("请输入登录密码，以重置手势密码");
            showAlertAnim();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!rxSubscription.isUnsubscribed()) {
            rxSubscription.unsubscribe();
        }

        stopListening();
    }

    @SuppressLint("SetTextI18n")
    private void init() {

        //获取是否是主动解锁或者被动打开关闭解锁
        if (getIntent().getExtras() != null) {
            this.initiativeSet = getIntent().getExtras().getBoolean(INITIATIVE);
        }

        if (getIntent().getExtras() != null) {
            this.change_gesture = getIntent().getExtras().getBoolean(CHANGE);
        }

        if (getIntent().getExtras() != null) {
            this.verlogin = getIntent().getExtras().getBoolean(VER_LOGIN);
        }

        if (initiativeSet){
            rlTopbar.setVisibility(View.VISIBLE);
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {finish();
                }
            });
        }

        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        new LoginVerityLoginPwdPresenter(this, this);

        mobile.setText(App.mAxLoginSp.getUserMobil().substring(0, 3) + "****" + App.mAxLoginSp.getUserMobil().substring(7));
        setQiniuPicUrlToUI();

        manager = FingerprintManagerCompat.from(this);//FingerprintManager) this.getSystemService(Context.FINGERPRINT_SERVICE);
        mKeyManager = (KeyguardManager) this.getSystemService(Context.KEYGUARD_SERVICE);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFinger()) {
                    if (errorTimes < 5) {
                        scanDialog = new AlertDialog.Builder(VerFingerActivity.this)
                                .setTitle("指纹解锁")
                                .setMessage("请扫描指纹")
                                .setIcon(R.mipmap.finger)
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .create();

                        scanDialog.show();

                        startListening(null);
                    } else {
                        AlertDialog dialog = new AlertDialog.Builder(VerFingerActivity.this)
                                .setTitle("指纹解锁")
                                .setMessage("指纹识别错误次数超过5次请稍后再试")
                                .setIcon(R.mipmap.finger)
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .create();
                        dialog.show();
                    }
                }
            }
        });

        next2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFinger()) {
                    if (errorTimes < 5) {
                        scanDialog = new AlertDialog.Builder(VerFingerActivity.this)
                                .setTitle("指纹解锁")
                                .setMessage("请扫描指纹")
                                .setIcon(R.mipmap.finger)
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .create();

                        scanDialog.show();

                        startListening(null);
                    } else {
                        AlertDialog dialog = new AlertDialog.Builder(VerFingerActivity.this)
                                .setTitle("指纹解锁")
                                .setMessage("指纹识别错误次数超过5次请稍后再试")
                                .setIcon(R.mipmap.finger)
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .create();
                        dialog.show();
                    }
                }
            }
        });

        if (verlogin){
            //如果需要去验证登录密码
            showAlertAnim();
        }
    }

    CancellationSignal mCancellationSignal = new CancellationSignal();

    public void startListening(FingerprintManagerCompat.CryptoObject cryptoObject) {
        //android studio 上，没有这个会报错// 运行时检查权限
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "没有指纹识别权限", Toast.LENGTH_SHORT).show();
            return;
        }
        // 30s 可以启动一次
        manager.authenticate(cryptoObject, 0, mCancellationSignal, mSelfCancelled, null);
    }

    //回调方法
    FingerprintManagerCompat.AuthenticationCallback mSelfCancelled = new FingerprintManagerCompat.AuthenticationCallback() {
        @Override
        public void onAuthenticationError(int errMsgId, CharSequence errString) {
            super.onAuthenticationError(errMsgId, errString);
        }

        @Override
        public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
            super.onAuthenticationHelp(helpMsgId, helpString);
        }

        @Override
        public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
            super.onAuthenticationSucceeded(result);
            if (!initiativeSet) {
                if (change_gesture){
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(SetGestureActivity.INITIATIVE, true);
                    Intent i = new Intent(VerFingerActivity.this, SetGestureActivity.class);
                    i.putExtras(bundle);
                    startActivity(i);
                    finish();
                }else {
                    finish();
                }
            } else {
                if (change_gesture){
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(SetGestureActivity.INITIATIVE, true);
                    Intent i = new Intent(VerFingerActivity.this, SetGestureActivity.class);
                    i.putExtras(bundle);
                    startActivity(i);
                    finish();
                }else {
                    LockInfo lockInfo = SQLite.select()
                            .from(LockInfo.class)
                            .where(LockInfo_Table.regist_serial.is(App.mAxLoginSp.getRegistSerial()))
                            .querySingle();// 区别与 queryList()

                    assert lockInfo != null;
                    if (lockInfo.fingerStatus) {
                        lockInfo.fingerStatus = false;
                        lockInfo.remindStatus=true;
                        lockInfo.save();
                    } else {
                        lockInfo.fingerStatus = true;
                        lockInfo.save();
                    }
                }
                finish();
            }
        }

        @Override
        public void onAuthenticationFailed() {
            super.onAuthenticationFailed();
            errorTimes = errorTimes + 1;
            if (errorTimes >= 5) {
                timer = new Timer();
                if (reScanDialog != null)
                    reScanDialog.dismiss();
                reScanDialog = new AlertDialog.Builder(VerFingerActivity.this)
                        .setTitle("指纹解锁")
                        .setMessage("指纹识别错误次数过多请稍后再试")
                        .setIcon(R.mipmap.finger)
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create();
                reScanDialog.show();
            } else {
                if (scanDialog != null)
                    scanDialog.dismiss();

                if (reScanDialog != null) {
                    reScanDialog.show();
                } else {
                    reScanDialog = new AlertDialog.Builder(VerFingerActivity.this)
                            .setTitle("指纹解锁")
                            .setMessage("请再试一次")
                            .setIcon(R.mipmap.finger)
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .create();
                    reScanDialog.show();
                }
            }
        }
    };


    public boolean isFinger() {
        if (!manager.isHardwareDetected()) {
            return false;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            if (!mKeyManager.isKeyguardSecure()) {
                return false;
            }
        }
        if (!manager.hasEnrolledFingerprints()) {
            return false;
        }
        return true;
    }

    private void setQiniuPicUrlToUI() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                avatarId.setImageResource(R.drawable.default_avatar);
            }
        });
        final String url = App.mAxLoginSp.getAvatarUrl();
        DisplayImageOptions config = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(configuration);
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.loadImage(url, config, new SimpleImageLoadingListener() {

            @Override
            public void onLoadingComplete(String imageUri, View view,
                                          Bitmap loadedImage) {
                if (!Util.isEmpty(url)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        avatarId.setBackground(null);
                    }
                }
                avatarId.setImageBitmap(loadedImage);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (initiativeSet) {
                finish();
            }
            return false;
        }
        return super.onKeyDown(keyCode, event);
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

    @Override
    public void verityLoginPwdResult() {
        if (verlogin){
            Bundle bundle = new Bundle();
            bundle.putBoolean(SetGestureActivity.INITIATIVE, true);
            Intent i = new Intent(VerFingerActivity.this, SetGestureActivity.class);
            i.putExtras(bundle);
            startActivity(i);
        }else {
            if (initiativeSet) {
                if (change_gesture){
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(SetGestureActivity.INITIATIVE, true);
                    Intent i = new Intent(VerFingerActivity.this, SetGestureActivity.class);
                    i.putExtras(bundle);
                    startActivity(i);
                    finish();
                }else {
                    LockInfo lockInfo = SQLite.select()
                            .from(LockInfo.class)
                            .where(LockInfo_Table.regist_serial.is(App.mAxLoginSp.getRegistSerial()))
                            .querySingle();// 区别与 queryList()

                    assert lockInfo != null;
                    if (lockInfo.fingerStatus) {
                        lockInfo.fingerStatus = false;
                        lockInfo.remindStatus = true;
                        lockInfo.save();
                    } else {
                        lockInfo.fingerStatus = true;
                        lockInfo.remindStatus = true;
                        lockInfo.save();
                    }
                }
                finish();
            } else {
                //如果是验证密码直接验证完了关闭该界面
                if (change_gesture){
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(SetGestureActivity.INITIATIVE, true);
                    Intent i = new Intent(VerFingerActivity.this, SetGestureActivity.class);
                    i.putExtras(bundle);
                    startActivity(i);
                    finish();
                }else {
                    finish();
                }
            }
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

    @OnClick(R.id.link_xieyi)
    public void onVerLoginPwdClick(View view) {
        verlogin=false;
        dialogTitleTxt.setText("请输入登录密码，以验证身份");
        showAlertAnim();
    }

    @OnClick(R.id.okBnt)
    public void okBtnClick(View view) {
        if (Util.isEmpty(pwdEdit.getText().toString().trim())) {
            Toast.makeText(this, "请输入登录密码", Toast.LENGTH_SHORT).show();
        } else {
            presenter.verityLoginPwd(pwdEdit.getText().toString().trim());
        }
    }

    public void stopListening() {
        if (mCancellationSignal != null) {
            mCancellationSignal.cancel();
            mCancellationSignal = null;
        }
    }

}
