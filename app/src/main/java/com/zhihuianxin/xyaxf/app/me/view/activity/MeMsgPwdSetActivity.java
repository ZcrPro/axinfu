package com.zhihuianxin.xyaxf.app.me.view.activity;

import android.os.Bundle;

import com.zhihuianxin.xyaxf.BaseRealmActionBarActivity;
import com.zhihuianxin.xyaxf.R;

import butterknife.ButterKnife;

/**
 * Created by Vincent on 2017/12/20.
 */

public class MeMsgPwdSetActivity extends BaseRealmActionBarActivity{
    @Override
    protected int getContentViewId() {
        return R.layout.me_msg_set_pwd_activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
    }
}
