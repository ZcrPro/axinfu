package com.zhihuianxin.xyaxf.app.unionqr_pay.view.activity;

import android.os.Bundle;

import com.zhihuianxin.xyaxf.BaseRealmActionBarActivity;
import com.zhihuianxin.xyaxf.R;

/**
 * Created by Vincent on 2017/11/9.
 */

public class UnionLimitAmountActivity extends BaseRealmActionBarActivity{
    @Override
    protected int getContentViewId() {
        return R.layout.union_pay_new_activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView(){

    }
}
