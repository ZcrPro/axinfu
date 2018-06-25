package com.zhihuianxin.xyaxf.app.ocp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.zhihuianxin.xyaxf.BaseRealmActionBarActivity;
import com.zhihuianxin.xyaxf.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by zcrpro on 2017/11/23.
 * 不支持跨校支付
 */

public class NotSupPay extends BaseRealmActionBarActivity {

    @InjectView(R.id.next)
    Button next;
    @InjectView(R.id.text)
    TextView text;
    @InjectView(R.id.tv_c)
    TextView tv_c;

    private String msg;
    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        msg = bundle.getString("msg");
        title = bundle.getString("title");

        if (msg != null)
            text.setText(msg);

        if (title != null)
            tv_c.setText(title);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected int getContentViewId() {
        return R.layout.not_sup_pay;
    }
}
