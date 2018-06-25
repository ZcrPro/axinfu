package com.zhihuianxin.xyaxf.app.verification;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
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
import com.zhihuianxin.xyaxf.app.AppConstant;
import com.axinfu.modellib.service.CustomerService;
import com.axinfu.modellib.thrift.lock.Lock;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.zhihuianxin.axutil.Util;
import com.zhihuianxin.xyaxf.App;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.ApiFactory;
import com.zhihuianxin.xyaxf.app.BaseSubscriber;
import com.zhihuianxin.xyaxf.app.RetrofitFactory;
import com.zhihuianxin.xyaxf.app.utils.NetUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.realm.LockRealmProxy;
import io.realm.Realm;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by zcrpro on 2018/1/29.
 */

public class FingerActivity extends Activity {

    @InjectView(R.id.avatar_id)
    CustomShapeImageView avatarId;
    @InjectView(R.id.next)
    TextView next;
    @InjectView(R.id.link_xieyi)
    TextView linkXieyi;
    @InjectView(R.id.mobile)
    TextView mobile;


    FingerprintManagerCompat manager;
    FingerprintManagerCompat fingerprintManagerCompat;
    KeyguardManager mKeyManager;
    private final static int REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS = 0;
    private final static String TAG = "finger_log";
    @InjectView(R.id.grayBg)
    View mGrayBg;
    @InjectView(R.id.share_title)
    TextView shareTitle;
    @InjectView(R.id.pwdEdit)
    EditText pwdEdit;
    @InjectView(R.id.okBnt)
    Button okBnt;
    @InjectView(R.id.click_focus)
    TextView closeAniTxt;
    @InjectView(R.id.backAnimView)
    RelativeLayout mBackAlertView;
    @InjectView(R.id.next2)
    ImageView next2;

    private DisplayMetrics metrics;

    private boolean open;
    private boolean close;
    private boolean finger;

    private AlertDialog scanDialog;
    private AlertDialog reScanDialog;

    private int errorTimes;
    private Timer timer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.finger_activity);
        ButterKnife.inject(this);


        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);


        if (getIntent().getExtras() != null) {
            this.open = getIntent().getExtras().getBoolean("open");
        }

        if (getIntent().getExtras() != null) {
            this.close = getIntent().getExtras().getBoolean("close");
        }

        if (getIntent().getExtras() != null) {
            this.finger = getIntent().getExtras().getBoolean("finger");
        }

        init();

        App.mAxLoginSp.setLcokFalse(false);

    }

    private void init() {
        //获取customer
        mobile.setText(App.mAxLoginSp.getUserMobil().substring(0, 3) + "****" + App.mAxLoginSp.getUserMobil().substring(7));
        setQiniuPicUrlToUI();


        if(open||close){
            linkXieyi.setVisibility(View.GONE);
        }

        linkXieyi.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
        linkXieyi.getPaint().setAntiAlias(true);//抗锯齿

        linkXieyi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertAnim();
            }
        });


        manager = FingerprintManagerCompat.from(this);//FingerprintManager) this.getSystemService(Context.FINGERPRINT_SERVICE);
        mKeyManager = (KeyguardManager) this.getSystemService(Context.KEYGUARD_SERVICE);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFinger()) {
                    if (errorTimes < 5) {
                        scanDialog = new AlertDialog.Builder(FingerActivity.this)
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
                        AlertDialog dialog = new AlertDialog.Builder(FingerActivity.this)
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
                        scanDialog = new AlertDialog.Builder(FingerActivity.this)
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
                        AlertDialog dialog = new AlertDialog.Builder(FingerActivity.this)
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

        okBnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Util.isEmpty(pwdEdit.getText().toString().trim())) {
                    Toast.makeText(FingerActivity.this, "请输入登录密码", Toast.LENGTH_SHORT).show();
                } else {
                    verityLoginPwd(pwdEdit.getText().toString().trim());
                }
            }
        });


        if (isFinger()) {
            scanDialog = new AlertDialog.Builder(FingerActivity.this)
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
        }

    }

    private void verityLoginPwd(String login_password) {
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String, Object> map = new HashMap<>();
        map.put("login_password", login_password);
        CustomerService loginService = ApiFactory.getFactory().create(CustomerService.class);
        loginService.verifyLoginPwd(NetUtils.getRequestParams(this, map), NetUtils.getSign(NetUtils.getRequestParams(this, map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(this, true, null) {
                    @Override
                    public void onNext(Object o) {
                        if (open) {
                            scanDialog = new AlertDialog.Builder(FingerActivity.this)
                                    .setTitle("指纹解锁已开启")
                                    .setMessage("你后续可以在“我的-设置”下更改解锁方式")
                                    .setNegativeButton("知道了", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Realm.getDefaultInstance().executeTransactionAsync(new Realm.Transaction() {
                                                @Override
                                                public void execute(Realm bgRealm) {
                                                    Lock lock = new Lock();
                                                    lock.mobile = App.mAxLoginSp.getUserMobil();
                                                    lock.fingerStatus = true;
                                                    bgRealm.copyToRealmOrUpdate(lock);
                                                    finish();
                                                }
                                            });
                                        }
                                    })
                                    .create();

                            scanDialog.show();
                        } else if (close) {
                            Realm.getDefaultInstance().executeTransactionAsync(new Realm.Transaction() {
                                @Override
                                public void execute(Realm bgRealm) {
                                    Lock lock = new Lock();
                                    lock.mobile = App.mAxLoginSp.getUserMobil();
                                    lock.fingerStatus = false;
                                    bgRealm.copyToRealmOrUpdate(lock);
                                    finish();
                                }
                            });
                        } else if (finger) {
                            scanDialog = new AlertDialog.Builder(FingerActivity.this)
                                    .setTitle("指纹解锁已开启")
                                    .setMessage("你后续可以在“我的-设置”下更改解锁方式")
                                    .setNegativeButton("知道了", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Realm.getDefaultInstance().executeTransactionAsync(new Realm.Transaction() {
                                                @Override
                                                public void execute(Realm bgRealm) {
                                                    Lock lock = new Lock();
                                                    lock.mobile = App.mAxLoginSp.getUserMobil();
                                                    lock.gestureStatus = false;
                                                    lock.fingerStatus = true;
                                                    bgRealm.copyToRealmOrUpdate(lock);
                                                    finish();
                                                }
                                            });
                                        }
                                    })
                                    .create();
                            scanDialog.show();
                        } else {
                            finish();
                        }

                    }
                });
    }

    public void startListening(FingerprintManagerCompat.CryptoObject cryptoObject) {
        //android studio 上，没有这个会报错// 运行时检查权限
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "没有指纹识别权限", Toast.LENGTH_SHORT).show();
            return;
        }
        // 30s 可以启动一次
        manager.authenticate(cryptoObject, 0, mCancellationSignal, mSelfCancelled, null);
    }


    CancellationSignal mCancellationSignal = new CancellationSignal();
    //回调方法
    FingerprintManagerCompat.AuthenticationCallback mSelfCancelled = new FingerprintManagerCompat.AuthenticationCallback() {
        @Override
        public void onAuthenticationError(int errMsgId, CharSequence errString) {
            super.onAuthenticationError(errMsgId, errString);
//            Toast.makeText(FingerActivity.this, "验证失败", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
            super.onAuthenticationHelp(helpMsgId, helpString);
        }

        @Override
        public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
            super.onAuthenticationSucceeded(result);
            if (open) {
                Realm.getDefaultInstance().executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm bgRealm) {
                        final Lock lock2 = Realm.getDefaultInstance().where(Lock.class).equalTo("mobile", App.mAxLoginSp.getUserMobil()).findFirst();
                        Lock lock = new Lock();
                        lock.mobile = App.mAxLoginSp.getUserMobil();
                        lock.fingerStatus = true;
                        if (lock2 != null)
                            lock.gesturePassword = ((LockRealmProxy) lock2).realmGet$gesturePassword();
                        bgRealm.copyToRealmOrUpdate(lock);
                        App.mAxLoginSp.setUserLcok(false);
                    }
                });
            }

            if (close) {
                Realm.getDefaultInstance().executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm bgRealm) {
                        final Lock lock2 = Realm.getDefaultInstance().where(Lock.class).equalTo("mobile", App.mAxLoginSp.getUserMobil()).findFirst();
                        Lock lock = new Lock();
                        lock.mobile = App.mAxLoginSp.getUserMobil();
                        lock.fingerStatus = false;
                        if (lock2 != null)
                            lock.gesturePassword = ((LockRealmProxy) lock2).realmGet$gesturePassword();
                        bgRealm.copyToRealmOrUpdate(lock);
                        App.mAxLoginSp.setUserLcok(true);
                    }
                });
            }

            if (finger) {
                Realm.getDefaultInstance().executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm bgRealm) {
                        final Lock lock2 = Realm.getDefaultInstance().where(Lock.class).equalTo("mobile", App.mAxLoginSp.getUserMobil()).findFirst();
                        Lock lock = new Lock();
                        lock.mobile = App.mAxLoginSp.getUserMobil();
                        lock.gestureStatus = false;
                        lock.fingerStatus = true;
                        if (lock2 != null)
                            lock.gesturePassword = ((LockRealmProxy) lock2).realmGet$gesturePassword();
                        bgRealm.copyToRealmOrUpdate(lock);
                    }
                });
            }
            App.mAxLoginSp.setLcokFalse(true);
            finish();
        }

        @Override
        public void onAuthenticationFailed() {
            super.onAuthenticationFailed();

            App.mAxLoginSp.setLcokFalse(false);

            errorTimes = errorTimes + 1;
            if (errorTimes >= 5) {
                timer = new Timer();
                if (reScanDialog != null)
                    reScanDialog.dismiss();
                reScanDialog = new AlertDialog.Builder(FingerActivity.this)
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

                stopListening();

            } else {
                if (scanDialog != null)
                    scanDialog.dismiss();

                if (reScanDialog != null) {
                    reScanDialog.show();
                } else {
                    reScanDialog = new AlertDialog.Builder(FingerActivity.this)
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
//            Toast.makeText(this, "没有指纹识别模块", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            if (!mKeyManager.isKeyguardSecure()) {
//                Toast.makeText(this, "没有开启锁屏密码", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        if (!manager.hasEnrolledFingerprints()) {
//            Toast.makeText(this, "没有录入指纹", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void setQiniuPicUrlToUI() {
        final String url = App.mAxLoginSp.getAvatarUrl();
        DisplayImageOptions config = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.default_avatar)
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void exit() {
        if (open || close) {
            finish();
        }
    }

    public void stopListening() {
        if (mCancellationSignal != null) {
            mCancellationSignal.cancel();
            mCancellationSignal = null;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopListening();
    }
}
