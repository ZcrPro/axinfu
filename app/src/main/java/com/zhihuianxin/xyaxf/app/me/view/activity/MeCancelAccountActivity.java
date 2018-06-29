package com.zhihuianxin.xyaxf.app.me.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.zhihuianxin.xyaxf.app.base.axutil.Util;
import com.zhihuianxin.xyaxf.BaseRealmActionBarActivity;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.login.contract.ILoginCancelAccountContrat;
import com.zhihuianxin.xyaxf.app.login.presenter.LoginCancelAccountPresenter;
import com.zhihuianxin.xyaxf.app.login.view.activity.LoginInputMobilActivityNew;
import com.zhihuianxin.xyaxf.app.me.view.fragment.MeFragment;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Vincent on 2017/12/20.
 */

public class MeCancelAccountActivity extends BaseRealmActionBarActivity implements ILoginCancelAccountContrat.ILoginCancelAccountView{
    @InjectView(R.id.pwd)
    EditText pwdEdit;
    @InjectView(R.id.next)
    Button nextBtn;

    private ILoginCancelAccountContrat.ILoginCancelAccountPresenter presenter;
    @Override
    protected int getContentViewId() {
        return R.layout.me_cancelaccount_activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);

        new LoginCancelAccountPresenter(this,this);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Util.isEmpty(pwdEdit.getText().toString().trim())){
                    Toast.makeText(MeCancelAccountActivity.this,"请输入登录密码",Toast.LENGTH_LONG).show();
                } else{
                    presenter.cancelAccount(pwdEdit.getText().toString().trim());
                }
            }
        });
    }

    @Override
    public void cancelAccountResult() {
        Toast.makeText(this,"注销成功",Toast.LENGTH_LONG).show();
        sendBroadcast(new Intent(MeFragment.BROADCAST_MEFRAGMENT_CLOSE));// close MeActivity and MainActivity
        sendBroadcast(new Intent(MeSettingActivity.BROADCAST_CANCELACCOUNT_CLOSE_MESETTING));// close MeSettingActivity
        MeFragment.logoutOperat();
        startActivity(new Intent(getActivity(), LoginInputMobilActivityNew.class));
        finish();
    }

    @Override
    public void setPresenter(ILoginCancelAccountContrat.ILoginCancelAccountPresenter presenter) {this.presenter = presenter;}
    @Override
    public void loadStart() {}
    @Override
    public void loadError(String errorMsg) {}
    @Override
    public void loadComplete() {}
}
