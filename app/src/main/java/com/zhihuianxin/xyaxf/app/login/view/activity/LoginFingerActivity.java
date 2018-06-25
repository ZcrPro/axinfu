package com.zhihuianxin.xyaxf.app.login.view.activity;

import android.Manifest;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v4.os.CancellationSignal;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.zhihuianxin.xyaxf.BaseRealmActionBarActivity;
import com.zhihuianxin.xyaxf.R;

/**
 * Created by Vincent on 2018/1/25.
 */

public class LoginFingerActivity extends BaseRealmActionBarActivity{
    FingerprintManagerCompat manager;
    FingerprintManagerCompat fingerprintManagerCompat;
    KeyguardManager mKeyManager;
    private final static int REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS = 0;
    private final static String TAG = "finger_log";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.finger_test);
        manager = FingerprintManagerCompat.from(this);//FingerprintManager) this.getSystemService(Context.FINGERPRINT_SERVICE);
        mKeyManager = (KeyguardManager) this.getSystemService(Context.KEYGUARD_SERVICE);
        Button btn_finger = (Button) findViewById(R.id.btn_activity_main_finger);
        btn_finger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFinger()) {
                    Toast.makeText(LoginFingerActivity.this, "请进行指纹识别", Toast.LENGTH_LONG).show();
                    Log(TAG, "keyi");
                    startListening(null);
                }
            }
        });
    }

    @Override
    protected int getContentViewId() {
        return R.layout.finger_test;
    }

    public boolean isFinger() {
        if (!manager.isHardwareDetected()) {
            Toast.makeText(this, "没有指纹识别模块", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!mKeyManager.isKeyguardSecure()) {
            Toast.makeText(this, "没有开启锁屏密码", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!manager.hasEnrolledFingerprints()) {
//            Toast.makeText(this, "没有录入指纹", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    CancellationSignal mCancellationSignal = new CancellationSignal();
    //回调方法
    FingerprintManagerCompat.AuthenticationCallback mSelfCancelled = new FingerprintManagerCompat.AuthenticationCallback() {
        @Override
        public void onAuthenticationError(int errMsgId, CharSequence errString) {
            super.onAuthenticationError(errMsgId, errString);
            Toast.makeText(LoginFingerActivity.this,"ERROR",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
            super.onAuthenticationHelp(helpMsgId, helpString);
            //Toast.makeText(LoginFingerActivity.this,helpString.toString(),Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
            super.onAuthenticationSucceeded(result);
            Toast.makeText(LoginFingerActivity.this,"SUCCESS",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onAuthenticationFailed() {
            super.onAuthenticationFailed();
            Toast.makeText(LoginFingerActivity.this,"Failed",Toast.LENGTH_SHORT).show();
        }
    };

    public void startListening(FingerprintManagerCompat.CryptoObject cryptoObject) {
        //android studio 上，没有这个会报错// 运行时检查权限
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "没有指纹识别权限", Toast.LENGTH_SHORT).show();
            return;
        }
        // 30s 可以启动一次
        manager.authenticate(cryptoObject,0, mCancellationSignal, mSelfCancelled, null);
    }

    private void showAuthenticationScreen() {
        Intent intent = mKeyManager.createConfirmDeviceCredentialIntent("finger", "测试指纹识别");
        if (intent != null) {
            startActivityForResult(intent, REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS) {
            // Challenge completed, proceed with using cipher
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "识别成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "识别失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void Log(String tag, String msg) {
        Log.d(tag, msg);
    }
}
