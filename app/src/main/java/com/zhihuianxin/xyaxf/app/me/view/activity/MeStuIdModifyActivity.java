package com.zhihuianxin.xyaxf.app.me.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.axinfu.modellib.thrift.customer.VerifyField;
import com.axinfu.modellib.thrift.ecard.ECard;
import com.axinfu.modellib.thrift.ecard.ECardAccount;
import com.axinfu.modellib.thrift.fee.FeeAccount;
import com.zhihuianxin.axutil.Util;
import com.zhihuianxin.xyaxf.App;
import com.zhihuianxin.xyaxf.BaseRealmActionBarActivity;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.login.view.activity.LoginGetPwdActivity;
import com.zhihuianxin.xyaxf.app.login.view.activity.LoginGetPwdByCodeActivity;
import com.zhihuianxin.xyaxf.app.me.contract.IMeStuMsgContract;
import com.zhihuianxin.xyaxf.app.me.presenter.MeStuMsgPresenter;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import io.realm.Realm;

import static com.zhihuianxin.xyaxf.app.login.view.activity.LoginGetPwdActivity.EXTRA_VERIFY_DATA;

/**
 * Created by Vincent on 2016/10/20.
 */

public class MeStuIdModifyActivity extends BaseRealmActionBarActivity implements IMeStuMsgContract.IMeStuMsgView {
    private IMeStuMsgContract.IMeStuMsgPresenter mPresenter;
    @InjectView(R.id.id_value)
    EditText mStuNoEdit;
    @InjectView(R.id.pwd)
    EditText mLoginPwd;
    @InjectView(R.id.next)
    Button next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);

        new MeStuMsgPresenter(this, this);

        mLoginPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    next.setEnabled(true);
                } else {
                    next.setEnabled(false);
                }
            }
        });
    }

    @OnClick(R.id.next)
    public void onBtnUpdateID() {
        String id = mStuNoEdit.getText().toString().trim();
        String loginPwd = mLoginPwd.getText().toString().trim();
        if (Util.isEmpty(id) || Util.isEmpty(loginPwd)) {
            Toast.makeText(this, "内容不可为空！", Toast.LENGTH_LONG).show();
            return;
        }
        if (loginPwd.length() < 6) {
            Toast.makeText(this, "密码格式错误！", Toast.LENGTH_LONG).show();
            return;
        }
        mPresenter.modifyID("", id, loginPwd);
    }

    @OnClick(R.id.getPwdId)
    public void onBtnGetPwd() {
        mPresenter.getmodifyPwdInfo(App.mAxLoginSp.getUserMobil());
    }

    @Override
    protected int getContentViewId() {
        return R.layout.me_stu_id_modify_activity;
    }

    @Override
    public boolean leftButtonEnabled() {
        return true;
    }

    @Override
    public void onLeftButtonClick(View view) {
        finish();
    }

    @Override
    public int getLeftButtonImageId() {
        return R.drawable.back;
    }

    @Override
    public void modifyECardAccountSuccess(ECardAccount ecArdAccount, ECard eCard) {
        //no
    }

    @Override
    public void modifyStuNoSuccess(FeeAccount feeAccount) {
        //no
    }

    @Override
    public void modifyIDSuccess(final FeeAccount feeAccount) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(feeAccount);
            }
        });
        Toast.makeText(this, "修改成功", Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public void getmodifyPwdInfoResult(ArrayList<VerifyField> verify_fields) {
        if(verify_fields!= null && verify_fields.size() > 0){
            Intent intent = new Intent(this, LoginGetPwdActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(EXTRA_VERIFY_DATA, verify_fields);
            intent.putExtras(bundle);
            startActivity(intent);
        } else{
            startActivity(new Intent(this,LoginGetPwdByCodeActivity.class));
        }
    }

    @Override
    public void setPresenter(IMeStuMsgContract.IMeStuMsgPresenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void loadStart() {

    }

    @Override
    public void loadError(String errorMsg) {

    }

    @Override
    public void loadComplete() {

    }
}
