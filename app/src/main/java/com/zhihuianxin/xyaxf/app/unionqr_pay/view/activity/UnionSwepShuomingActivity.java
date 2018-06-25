package com.zhihuianxin.xyaxf.app.unionqr_pay.view.activity;

import android.os.Bundle;
import android.view.View;

import com.zhihuianxin.xyaxf.BaseRealmActionBarActivity;
import com.zhihuianxin.xyaxf.R;

/**
 * Created by Vincent on 2018/1/13.
 */

public class UnionSwepShuomingActivity extends BaseRealmActionBarActivity{
    @Override
    protected int getContentViewId() {
        return R.layout.union_swep_shuom_activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean leftButtonEnabled() {
        return true;
    }

    @Override
    public void onLeftButtonClick(View view) {
        finish();
    }
}
