package com.zhihuianxin.xyaxf.app.home.qrcode;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jaywei.PureVerticalSeekBar;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.ocp.QrResultActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.bingoogolapple.qrcode.core.QRCodeView;

/**
 * Created by zcrprozcrpro on 2017/5/5.
 */

public class QRCodeActivity extends FragmentActivity implements QRCodeView.Delegate {

    private static final String TAG = QRCodeActivity.class.getSimpleName();

    @InjectView(R.id.input_back)
    ImageView inputBack;
    @InjectView(R.id.back_icon)
    RelativeLayout backIcon;
    @InjectView(R.id.zbarview)
    Zview zbarview;
    @InjectView(R.id.grayBg)
    View mGrayBg;

    private String result;
    private float x;

    public static PureVerticalSeekBar vs;
    public static TextView add;
    public static TextView remove;

    private static final int REQUEST_CODE_CHOOSE_QRCODE_FROM_GALLERY = 666;

    private Zview mQRCodeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_code_activity);
        ButterKnife.inject(this);

        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initView() {
        mQRCodeView = (Zview) findViewById(R.id.zbarview);
        vs = (PureVerticalSeekBar) findViewById(R.id.vs);
        add = (TextView) findViewById(R.id.add);
        remove = (TextView) findViewById(R.id.remove);
        mQRCodeView.setDelegate(this);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        vs.setVertical_color(Color.parseColor("#000000"), Color.parseColor("#9a9a9a"));//设置滑竿的颜色，上下两个颜色

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < 5; i++) {
                    QZview.handleZoom(true, mQRCodeView.mCamera);
                }
            }
        });

        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < 5; i++) {
                    QZview.handleZoom(false, mQRCodeView.mCamera);
                }
            }
        });

        vs.setDragable(false);

        mQRCodeView.startSpot();

    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        Log.d(TAG, "QR:"+result);
        vibrate();
        mQRCodeView.startSpot();

        Intent i = new Intent(QRCodeActivity.this, QrResultActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("code", result);
        i.putExtras(bundle);
        startActivity(i);
        finish();

    }

    @Override
    protected void onStart() {
        super.onStart();
        mQRCodeView.startCamera();
        mQRCodeView.showScanRect();
    }


    @Override
    protected void onStop() {
        mQRCodeView.stopCamera();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mQRCodeView.onDestroy();
        vs = null;
        add = null;
        remove = null;
        QZview.currunt = 0;
        super.onDestroy();
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        Log.e(TAG, "打开相机出错");
    }

}
