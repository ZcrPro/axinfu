package com.zhihuianxin.xyaxf.app.ecard.view;

import android.app.Activity;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhihuianxin.xyaxf.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by zcrpro on 2016/10/17.
 */
public class EcradPaymentActivity extends Activity {

    @InjectView(R.id.title)
    TextView title;
    @InjectView(R.id.ico_left)
    ImageView icoLeft;
    @InjectView(R.id.txt_left)
    TextView txtLeft;
    @InjectView(R.id.btn_left)
    FrameLayout btnLeft;
    @InjectView(R.id.ico_right)
    ImageView icoRight;
    @InjectView(R.id.txt_right)
    TextView txtRight;
    @InjectView(R.id.btn_right)
    FrameLayout btnRight;
    @InjectView(R.id.action_bar)
    FrameLayout actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ecrad_payment_activity);
        ButterKnife.inject(this);
        title.setText("支付确认");
    }
}
