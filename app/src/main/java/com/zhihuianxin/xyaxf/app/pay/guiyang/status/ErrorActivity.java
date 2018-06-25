package com.zhihuianxin.xyaxf.app.pay.guiyang.status;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.zhihuianxin.xyaxf.BaseRealmActionBarActivity;
import com.zhihuianxin.xyaxf.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by zcrpro on 2017/10/31.
 */

public class ErrorActivity extends BaseRealmActionBarActivity {

    @InjectView(R.id.btn_ok)
    Button btnOk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected int getContentViewId() {
        return R.layout.guiyang_error_activity;
    }
}
