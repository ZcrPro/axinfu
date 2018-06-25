package com.zhihuianxin.xyaxf.app.verification;

import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.axinfu.modellib.thrift.lock.Lock;
import com.zhihuianxin.xyaxf.App;
import com.zhihuianxin.xyaxf.BaseRealmActionBarActivity;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.utils.RxBus;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.realm.LockRealmProxy;
import io.realm.Realm;

/**
 * Created by Vincent on 2018/1/5.
 */

public class UnionSetGestureActivity extends BaseRealmActionBarActivity {
    @InjectView(R.id.unlock)
    UnlockView mUnlockView;
    @InjectView(R.id.link_xieyi)
    TextView linkXieyi;
    private String pwd;
    @InjectView(R.id.gesTitleTxt)
    TextView titleTxt;
    @InjectView(R.id.gesContentTxt)
    TextView contentTxt;

    FingerprintManagerCompat manager;
    KeyguardManager mKeyManager;
    private boolean reset;
    private boolean noback;
    private boolean noShowDialog;
    private boolean NotNeedFinger;
    private boolean canBACK;
    private boolean no_direct;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);

        manager = FingerprintManagerCompat.from(this);//FingerprintManager) this.getSystemService(Context.FINGERPRINT_SERVICE);
        mKeyManager = (KeyguardManager) this.getSystemService(Context.KEYGUARD_SERVICE);

        if (getIntent().getExtras() != null) {
            this.reset = getIntent().getExtras().getBoolean("reset");
        }

        if (getIntent().getExtras() != null) {
            this.noback = getIntent().getExtras().getBoolean("noback");
        }

        if (getIntent().getExtras() != null) {
            this.noShowDialog = getIntent().getExtras().getBoolean("noShowDialog");
        }

        if (getIntent().getExtras() != null) {
            this.NotNeedFinger = getIntent().getExtras().getBoolean("NotNeedFinger");
        }

        if (getIntent().getExtras() != null) {
            this.canBACK = getIntent().getExtras().getBoolean("canBACK");
        }

        if (getIntent().getExtras() != null) {
            this.no_direct = getIntent().getExtras().getBoolean("no_direct");
        }

        if (!isFinger()) {
            linkXieyi.setVisibility(View.GONE);
        }

        if (NotNeedFinger || reset) {
            linkXieyi.setVisibility(View.GONE);
        }

        linkXieyi.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
        linkXieyi.getPaint().setAntiAlias(true);//抗锯齿

        linkXieyi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFinger()) {
                    Intent intent = new Intent(UnionSetGestureActivity.this, SetFingerActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("finger", true);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish();
                } else {

                }

            }
        });

        //The listener for creating gesture;
        mUnlockView.setGestureListener(new UnlockView.CreateGestureListener() {
            @Override
            public void onGestureCreated(String result) {
//                if (Util.isEmpty(pwd)) {
//
//                }
                if (result.length() < 4) {
                    contentTxt.setText("手势密码长度不能少于4个点");
                    Toast.makeText(UnionSetGestureActivity.this, "手势密码长度不能少于4个点", Toast.LENGTH_SHORT).show();
                } else {
                    titleTxt.setText("请再次确认你设置的手势密码");
                    contentTxt.setText("");
                    Toast.makeText(UnionSetGestureActivity.this, "请再次设置手势密码", Toast.LENGTH_SHORT).show();
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
                RxBus.getDefault().send("no_add_card");
                contentTxt.setText("");
                App.mAxLoginSp.setUnionGesture(pwd);
                App.mAxLoginSp.setUnionGestureEorTimes("0");
                App.mAxLoginSp.setUnionGesture5EorTimes("0");
                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm bgRealm) {
                        com.axinfu.modellib.thrift.lock.Lock lock = new Lock();
                        lock.mobile = App.mAxLoginSp.getUserMobil();
                        lock.gesturePassword = pwd;
                        lock.gestureStatus = true;
                        bgRealm.copyToRealmOrUpdate(lock);
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        if (noShowDialog) {
                            App.mAxLoginSp.setUnionGestureEorTimes("0");
                            App.mAxLoginSp.setUnionGesture5EorTimes("0");
                            setResult(RESULT_OK);
                            finish();
                            Toast.makeText(UnionSetGestureActivity.this, "设置成功", Toast.LENGTH_SHORT).show();
                        } else {
                            if (isFinger()) {
                                AlertDialog dialog = new AlertDialog.Builder(UnionSetGestureActivity.this)
                                        .setTitle("手势解锁设置成功")
                                        .setMessage("你还可以在“我的-设置”下开启指纹解锁，体验更快捷的解锁方式。")
                                        .setNegativeButton("知道了", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                App.mAxLoginSp.setUnionGestureEorTimes("0");
                                                App.mAxLoginSp.setUnionGesture5EorTimes("0");
                                                setResult(RESULT_OK);
                                                finish();
//                                        Toast.makeText(UnionSetGestureActivity.this, "设置成功", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .create();

                                dialog.show();
                            } else {
                                App.mAxLoginSp.setUnionGestureEorTimes("0");
                                App.mAxLoginSp.setUnionGesture5EorTimes("0");
                                setResult(RESULT_OK);
                                finish();
                                Toast.makeText(UnionSetGestureActivity.this, "设置成功", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }
                }, new Realm.Transaction.OnError() {
                    @Override
                    public void onError(Throwable error) {
                        // Transaction failed and was automatically canceled.

                    }
                });

            }

            @Override
            public void onFailure() {
                mUnlockView.setMode(UnlockView.CREATE_MODE);
                titleTxt.setText("输入不一致 请重新设置");
            }
        });
    }

    @Override
    protected int getContentViewId() {
        return R.layout.union_gesture_activity;
    }

    @Override
    public String getRightButtonText() {
        if (no_direct){
            return "";
        }else {
            if (reset) {
                return "";
            } else {
                return "跳过";
            }
        }
    }

    @Override
    public void onRightButtonClick(View view) {
        super.onRightButtonClick(view);
        if (reset) {
            finish();
        } else {
            //弹窗
            LockDialog lockDialog = new LockDialog(this);
            lockDialog.show();
            lockDialog.setListener(new LockDialog.selectItem() {
                @Override
                public void shaohou() {
                    //稍后设置
                    Realm.getDefaultInstance().executeTransactionAsync(new Realm.Transaction() {
                        @Override
                        public void execute(Realm bgRealm) {
                            Lock lock = new Lock();
                            final Lock lock2 = Realm.getDefaultInstance().where(Lock.class).equalTo("mobile", App.mAxLoginSp.getUserMobil()).findFirst();
                            lock.mobile = App.mAxLoginSp.getUserMobil();
                            lock.laterStatus = true;
                            if (lock2 != null)
                                lock.fingerStatus = ((LockRealmProxy) lock2).realmGet$fingerStatus();
                            bgRealm.copyToRealmOrUpdate(lock);
                        }
                    });
                    App.later=true;
                    finish();
                }

                @Override
                public void buzai() {
                    //不在提醒
                    Realm.getDefaultInstance().executeTransactionAsync(new Realm.Transaction() {
                        @Override
                        public void execute(Realm bgRealm) {
                            Lock lock = new Lock();
                            final Lock lock2 = Realm.getDefaultInstance().where(Lock.class).equalTo("mobile", App.mAxLoginSp.getUserMobil()).findFirst();
                            lock.mobile = App.mAxLoginSp.getUserMobil();
                            lock.remindStatus = true;
                            if (lock2 != null)
                                lock.fingerStatus = ((LockRealmProxy) lock2).realmGet$fingerStatus();
                            bgRealm.copyToRealmOrUpdate(lock);
                        }
                    });
                    finish();
                }
            });
        }
    }

    @Override
    public boolean rightButtonEnabled() {
        if (no_direct){
            return false;
        }else {
            if (reset) {
                return false;
            } else {
                return true;
            }
        }
    }

    @Override
    public boolean leftButtonEnabled() {

        if (getIntent().getExtras() != null && getIntent().getExtras().getBoolean("canBACK")) {
            return true ;
        } else {
            return false;
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

    }
}
