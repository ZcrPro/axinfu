package com.zhihuianxin.xyaxf.app.verification;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import modellib.thrift.lock.Lock;
import com.zhihuianxin.xyaxf.App;
import com.zhihuianxin.xyaxf.BaseRealmActionBarActivity;
import com.zhihuianxin.xyaxf.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.realm.LockRealmProxy;

/**
 * Created by zcrpro on 2018/1/29.
 */

public class LockActivity extends BaseRealmActionBarActivity {


    @InjectView(R.id.gestureSwitch)
    Switch gestureSwitch;
    @InjectView(R.id.gestureView)
    RelativeLayout gestureView;
    @InjectView(R.id.gestureSwitch2)
    Switch gestureSwitch2;
    @InjectView(R.id.rl_finger)
    RelativeLayout rlFinger;
    @InjectView(R.id.unGestureTxt)
    TextView unGestureTxt;
    @InjectView(R.id.unGestureTxt2)
    TextView unGestureTxt2;

    public static final String EXTRA_VER_GES_FROM_SETTING = "verGesFromSetting";// 设置页面打开关闭手势进入的标示(生活也会进入这个页面)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);


    }

    @Override
    protected int getContentViewId() {
        return R.layout.lock_activity;
    }


    @Override
    protected void onResume() {
        super.onResume();

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                final Lock lock = realm.where(Lock.class).equalTo("mobile", App.mAxLoginSp.getUserMobil()).findFirst();

                if (lock != null) {
                    if (((LockRealmProxy) lock).realmGet$gestureStatus()) {
                        gestureSwitch.setChecked(true);
                    } else {
                        gestureSwitch.setChecked(false);
                    }

                    if (((LockRealmProxy) lock).realmGet$fingerStatus()) {
                        gestureSwitch2.setChecked(true);
                    } else {
                        gestureSwitch2.setChecked(false);
                    }

                } else {
                    gestureSwitch.setChecked(false);
                    gestureSwitch2.setChecked(false);
                }

                gestureSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (!buttonView.isPressed()) return;
                        if (isChecked) {// 关闭状态，需要打开 isChecked 是将要变为的状态
                            if (lock == null || TextUtils.isEmpty(((LockRealmProxy) lock).realmGet$gesturePassword())) {// 未设置,进行设置

                                Intent i = new Intent(LockActivity.this, UnionSetGestureActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putBoolean("NotNeedFinger", true);
                                bundle.putBoolean("canBACK", true);
                                bundle.putBoolean("no_direct", true);
                                i.putExtras(bundle);
                                startActivity(i);
                            } else {
                                Intent intent = new Intent(LockActivity.this, UnionVerGestureActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putString(EXTRA_VER_GES_FROM_SETTING, "1");
                                bundle.putBoolean("canBACK", true);
                                intent.putExtras(bundle);
                                startActivity(intent);
                            }
                        } else {
                            Intent intent = new Intent(LockActivity.this, UnionVerGestureActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString(EXTRA_VER_GES_FROM_SETTING, "0");
                            bundle.putBoolean("canBACK", true);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                    }
                });

                gestureSwitch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (!buttonView.isPressed()) return;
                        if (isChecked) {// 关闭状态，需要打开 isChecked 是将要变为的状态
                            Intent intent = new Intent(LockActivity.this, FingerActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putBoolean("open", true);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(LockActivity.this, FingerActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putBoolean("close", true);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                    }
                });
            }
        });

    }
}
