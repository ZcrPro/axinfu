package com.zhihuianxin.xyaxf.app.lock.create;

import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
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
import com.zhihuianxin.xyaxf.app.verification.UnlockView;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SetGestureActivity extends BaseRealmActionBarActivity {


    public static final String INITIATIVE = "INITIATIVE";
    @InjectView(R.id.avatar_id)
    CustomShapeImageView avatarId;
    @InjectView(R.id.mobile)
    TextView mobile;
    @InjectView(R.id.gesTitleTxt)
    TextView titleTxt;
    @InjectView(R.id.gesContentTxt)
    TextView contentTxt;
    @InjectView(R.id.unlock)
    UnlockView mUnlockView;
    @InjectView(R.id.link_finger)
    TextView linkFinger;
    @InjectView(R.id.link_shoushi)
    TextView linkShoushi;
    @InjectView(R.id.ll_link)
    LinearLayout llLink;

    private String pwd; //全局密码
    FingerprintManagerCompat manager;
    KeyguardManager mKeyManager;

    private boolean initiativeSet = false; //是否是主动设置 主动设置可以返回 被动设置不可返回 并且如果是主动 指纹解锁按钮也不显示

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
        manager = FingerprintManagerCompat.from(this);//FingerprintManager) this.getSystemService(Context.FINGERPRINT_SERVICE);
        mKeyManager = (KeyguardManager) this.getSystemService(Context.KEYGUARD_SERVICE);
        initLockData();
    }

    @Override
    protected int getContentViewId() {
        return R.layout.set_gesture_activity;
    }

    private void initLockData() {

        //获取是否是主动设置或者被动设置
        if (getIntent().getExtras() != null) {
            this.initiativeSet = getIntent().getExtras().getBoolean(INITIATIVE);
        }

        //如果是被动设置解锁
        if (!initiativeSet && isFinger()) {
            llLink.setVisibility(View.VISIBLE);
            linkFinger.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //无需判断是否存在指纹解锁
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(SetGestureActivity.INITIATIVE, false);
                    Intent i = new Intent(SetGestureActivity.this, CreateFingerActivity.class);
                    i.putExtras(bundle);
                    startActivity(i);
                    finish();
                }
            });
        }


        mobile.setText(App.mAxLoginSp.getUserMobil().substring(0, 3) + "****" + App.mAxLoginSp.getUserMobil().substring(7));
        setQiniuPicUrlToUI();

        mUnlockView.setGestureListener(new UnlockView.CreateGestureListener() {
            @Override
            public void onGestureCreated(String result) {
                if (result.length() < 4) {
                    contentTxt.setText("手势密码长度不能少于4个点");
                    Toast.makeText(SetGestureActivity.this, "手势密码长度不能少于4个点", Toast.LENGTH_SHORT).show();
                } else {
                    titleTxt.setText("请再次确认你设置的手势密码");
                    contentTxt.setText("");
                    Toast.makeText(SetGestureActivity.this, "请再次设置手势密码", Toast.LENGTH_SHORT).show();
                    mUnlockView.setMode(UnlockView.CHECK_MODE);
                    pwd = result;
                }
            }
        });

        mUnlockView.setMode(UnlockView.CREATE_MODE);
        titleTxt.setText("请设置手势密码");

        //The listener for verifing gesture;
        mUnlockView.setOnUnlockListener(new UnlockView.OnUnlockListener() {
            @Override
            public boolean isUnlockSuccess(String result) {
                return pwd.equals(result);
            }

            @Override
            public void onSuccess() {
                contentTxt.setText("");
                //存储数据
                LockInfo lockInfo = new LockInfo();
                lockInfo.regist_serial = App.mAxLoginSp.getRegistSerial();
                lockInfo.gesturePassword = pwd;
                lockInfo.gestureStatus = true;
                lockInfo.unionGestureEorTimes = 0;
                lockInfo.save();

                //发送一个重置密码成功的消息给上一个验证界面 从而去关闭验证界面
                RxBus.getDefault().send("close");
                //发送一个消息 被动从指纹设置跳转到手势设置 设置完了把指纹设置关闭了
                RxBus.getDefault().send("手势密码设置完成");

                if (isFinger() && !initiativeSet) {
                    AlertDialog dialog = new AlertDialog.Builder(SetGestureActivity.this)
                            .setTitle("手势解锁设置成功")
                            .setMessage("你还可以在“我的-设置”下开启指纹解锁，体验更快捷的解锁方式。")
                            .setNegativeButton("知道了", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    App.mAxLoginSp.setUnionGestureEorTimes("0");
                                    App.mAxLoginSp.setUnionGesture5EorTimes("0");
                                    setResult(RESULT_OK);
                                    finish();
                                }
                            })
                            .create();
                    dialog.show();
                }else {
                    Toast.makeText(SetGestureActivity.this, "设置成功", Toast.LENGTH_SHORT).show();
                    finish();
                }

            }

            @Override
            public void onFailure() {
                mUnlockView.setMode(UnlockView.CREATE_MODE);
                titleTxt.setText("输入不一致 请重新设置");
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

}
