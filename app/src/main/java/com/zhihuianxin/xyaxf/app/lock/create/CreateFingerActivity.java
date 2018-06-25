package com.zhihuianxin.xyaxf.app.lock.create;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.support.v4.app.ActivityCompat;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v4.os.CancellationSignal;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.zhihuianxin.xyaxf.BaseRealmActionBarActivity;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.utils.RxBus;
import com.zhihuianxin.xyaxf.app.verification.LockDialog;

import java.util.Timer;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Subscription;
import rx.functions.Action1;

public class CreateFingerActivity extends BaseRealmActionBarActivity {

    @InjectView(R.id.tiaoguo)
    TextView tiaoguo;
    @InjectView(R.id.rl)
    RelativeLayout rl;
    @InjectView(R.id.avatar_id)
    CustomShapeImageView avatarId;
    @InjectView(R.id.mobile)
    TextView mobile;
    @InjectView(R.id.next2)
    ImageView next2;
    @InjectView(R.id.next)
    TextView next;
    @InjectView(R.id.link_shoushi)
    TextView link_shoushi;
    @InjectView(R.id.grayBg)
    View grayBg;
    @InjectView(R.id.share_title)
    TextView shareTitle;
    @InjectView(R.id.pwdEdit)
    EditText pwdEdit;
    @InjectView(R.id.okBnt)
    Button okBnt;
    @InjectView(R.id.click_focus)
    TextView clickFocus;
    @InjectView(R.id.backAnimView)
    RelativeLayout backAnimView;

    FingerprintManagerCompat manager;
    KeyguardManager mKeyManager;
    @InjectView(R.id.link_zhiwen)
    TextView linkZhiwen;
    @InjectView(R.id.ll_link)
    LinearLayout llLink;


    private int errorTimes;
    private AlertDialog scanDialog;
    private AlertDialog reScanDialog;
    private Timer timer;

    public static final String INITIATIVE = "INITIATIVE";
    private boolean initiativeSet = false; //是否是主动设置 主动设置可以返回 被动设置不可返回 并且如果是主动 指纹解锁按钮也不显示

    private Subscription rxSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
        //获取是否是主动设置或者被动设置
        if (getIntent().getExtras() != null) {
            this.initiativeSet = getIntent().getExtras().getBoolean(INITIATIVE);
        }

        //如果解锁
        if (!initiativeSet) {
            llLink.setVisibility(View.VISIBLE);
            link_shoushi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //去手势密码解锁
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(SetGestureActivity.INITIATIVE, false);
                    Intent i = new Intent(CreateFingerActivity.this, SetGestureActivity.class);
                    i.putExtras(bundle);
                    startActivity(i);
                    finish();
                }
            });
        }

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
                scanDialog = new AlertDialog.Builder(CreateFingerActivity.this)
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
                AlertDialog dialog = new AlertDialog.Builder(CreateFingerActivity.this)
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

    @SuppressLint("SetTextI18n")
    private void init() {
        mobile.setText(App.mAxLoginSp.getUserMobil().substring(0, 3) + "****" + App.mAxLoginSp.getUserMobil().substring(7));
        setQiniuPicUrlToUI();

        manager = FingerprintManagerCompat.from(this);//FingerprintManager) this.getSystemService(Context.FINGERPRINT_SERVICE);
        mKeyManager = (KeyguardManager) this.getSystemService(Context.KEYGUARD_SERVICE);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFinger()) {
                    if (errorTimes < 5) {
                        scanDialog = new AlertDialog.Builder(CreateFingerActivity.this)
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
                        AlertDialog dialog = new AlertDialog.Builder(CreateFingerActivity.this)
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
                        scanDialog = new AlertDialog.Builder(CreateFingerActivity.this)
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
                        AlertDialog dialog = new AlertDialog.Builder(CreateFingerActivity.this)
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

            LockInfo lockInfo = SQLite.select()
                    .from(LockInfo.class)
                    .where(LockInfo_Table.regist_serial.is(App.mAxLoginSp.getRegistSerial()))
                    .querySingle();// 区别与 queryList()

            try {
                lockInfo.fingerStatus = true;
                lockInfo.gestureStatus = false;
                lockInfo.save();
            } catch (Exception e) {
                e.printStackTrace();
                LockInfo lockInfo1 =new LockInfo();
                lockInfo1.regist_serial = App.mAxLoginSp.getRegistSerial();
                lockInfo1.fingerStatus = true;
                lockInfo1.gestureStatus = false;
                lockInfo1.remindStatus=true;
                lockInfo1.save();
            }

            if (!initiativeSet) {
                scanDialog = new AlertDialog.Builder(CreateFingerActivity.this)
                        .setTitle("指纹解锁已开启")
                        .setMessage("你后续可以在“我的-设置”下更改解锁方式")
                        .setNegativeButton("知道了", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .create();

                scanDialog.show();
            }else {
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
                reScanDialog = new AlertDialog.Builder(CreateFingerActivity.this)
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
                    reScanDialog = new AlertDialog.Builder(CreateFingerActivity.this)
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

    @Override
    protected int getContentViewId() {
        return R.layout.create_finger_activity;
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


    @Override
    public boolean leftButtonEnabled() {
        if (initiativeSet) {
            return true;
        } else {
            return false;
        }
    }


    @Override
    public String getRightButtonText() {
        if (initiativeSet) {
            return "";
        } else {
            return "跳过";
        }
    }

    @Override
    public void onRightButtonClick(View view) {
        super.onRightButtonClick(view);

        LockDialog lockDialog = new LockDialog(this);
        lockDialog.show();
        lockDialog.setListener(new LockDialog.selectItem() {
            @Override
            public void shaohou() {
                LockInfo lockInfo = SQLite.select()
                        .from(LockInfo.class)
                        .where(LockInfo_Table.regist_serial.is(App.mAxLoginSp.getRegistSerial()))
                        .querySingle();// 区别与 queryList()
                if (lockInfo != null) {
                    lockInfo.laterStatus = true;
                    lockInfo.remindStatus = false;
                    lockInfo.save();
                }
                finish();
            }

            @Override
            public void buzai() {
                //不在提醒
                LockInfo lockInfo = SQLite.select()
                        .from(LockInfo.class)
                        .where(LockInfo_Table.regist_serial.is(App.mAxLoginSp.getRegistSerial()))
                        .querySingle();// 区别与 queryList()
                if (lockInfo != null) {
                    lockInfo.remindStatus = true;
                    lockInfo.laterStatus = false;
                    lockInfo.save();
                }
                finish();
            }
        });
    }

    @Override
    public boolean rightButtonEnabled() {
        if (initiativeSet) {
            return false;
        } else {
            return true;
        }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!rxSubscription.isUnsubscribed()) {
            rxSubscription.unsubscribe();
        }
        stopListening();
    }

    public void stopListening() {
        if (mCancellationSignal != null) {
            mCancellationSignal.cancel();
            mCancellationSignal = null;
        }
    }
}
