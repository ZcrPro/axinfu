package com.zhihuianxin.xyaxf.app.login.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.zhihuianxin.xyaxf.BaseRealmActionBarActivity;
import com.zhihuianxin.xyaxf.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Vincent on 2016/10/28.
 */

public class LoginGetPwdActivity extends BaseRealmActionBarActivity {
    public static final String EXTRA_VERIFY_DATA = "pwd_ver_firld";
    public static final String EXTRA_FROM_UNLOGIN = "from_unlogin";
    @Override
    protected int getContentViewId() {
        return R.layout.login_getpwd_main_activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
    }


    @OnClick(R.id.modify_pwd_bycode)
    public void onFindByCode(View view){
        Intent intent = new Intent(this,LoginGetPwdByCodeActivity.class);
        if(getIntent().getExtras() != null && getIntent().getExtras().getString(EXTRA_FROM_UNLOGIN) != null){
            Bundle bundle = new Bundle();
            bundle.putString(EXTRA_FROM_UNLOGIN,getIntent().getExtras().getString(EXTRA_FROM_UNLOGIN));
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    @OnClick(R.id.modify_pwd_byidmsg)
    public void onFindByIdMsg(View view){
        Intent intent = new Intent(this,LoginGetPwdByVerMsgActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(EXTRA_VERIFY_DATA,getIntent().getExtras().getSerializable(EXTRA_VERIFY_DATA));
        if(getIntent().getExtras() != null && getIntent().getExtras().getString(EXTRA_FROM_UNLOGIN) != null){
            bundle.putString(EXTRA_FROM_UNLOGIN,getIntent().getExtras().getString(EXTRA_FROM_UNLOGIN));
        }
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
