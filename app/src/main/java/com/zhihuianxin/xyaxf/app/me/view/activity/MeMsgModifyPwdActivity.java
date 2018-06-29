package com.zhihuianxin.xyaxf.app.me.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Selection;
import android.text.Spannable;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.zhihuianxin.xyaxf.app.base.BaseActionBarActivity;
import com.axinfu.modellib.thrift.customer.Customer;
import com.axinfu.modellib.thrift.customer.VerifyField;
import com.zhihuianxin.xyaxf.app.base.axutil.Util;
import com.zhihuianxin.xyaxf.App;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.login.view.activity.LoginGetPwdActivity;
import com.zhihuianxin.xyaxf.app.login.view.activity.LoginGetPwdByCodeActivity;
import com.zhihuianxin.xyaxf.app.me.contract.IMeModifyPwdContract;
import com.zhihuianxin.xyaxf.app.me.presenter.MeModifyPwdPresenter;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static com.zhihuianxin.xyaxf.app.login.view.activity.LoginGetPwdActivity.EXTRA_VERIFY_DATA;

/**
 * Created by Vincent on 2016/10/19.
 */

public class MeMsgModifyPwdActivity extends BaseActionBarActivity implements IMeModifyPwdContract.IMeModifyPwdView{
    private IMeModifyPwdContract.IMeModifyPwdPresenter mPwdPresenter;

    @InjectView(R.id.editText)
    EditText mOldPwdEdit;
    @InjectView(R.id.edit_pwd)
    EditText mNewPwdEdit;
    @InjectView(R.id.next)
    Button mNextBtn;
    @InjectView(R.id.pwdlookok)
    ImageView mPwdLookOkImg;
    @InjectView(R.id.pwdlookun)
    ImageView mPwdLookunImg;

    private boolean isHidden = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);

        mNextBtn.setEnabled(true);
        new MeModifyPwdPresenter(this,this);
    }

    @OnClick(R.id.next)
    public void onBtnModifyPwd(){
        String oldpwd = mOldPwdEdit.getText().toString().trim();
        String newpwd = mNewPwdEdit.getText().toString().trim();

        if(Util.isEmpty(oldpwd) || Util.isEmpty(newpwd) || oldpwd.length() < 6 || newpwd.length() < 6){
            Toast.makeText(this,"请输入正确的密码格式",Toast.LENGTH_LONG).show();
            return;
        }

        mNextBtn.setEnabled(false);
        mPwdPresenter.modifyPwd(oldpwd,newpwd);
    }

    @OnClick(R.id.pwdlook)
    public void onBtnPwdLook(){
        if (isHidden) {
            //设置EditText文本为可见的
            mNewPwdEdit.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            mPwdLookOkImg.setVisibility(View.VISIBLE);
            mPwdLookunImg.setVisibility(View.GONE);

        } else {
            //设置EditText文本为隐藏的
            mNewPwdEdit.setTransformationMethod(PasswordTransformationMethod.getInstance());
            mPwdLookOkImg.setVisibility(View.GONE);
            mPwdLookunImg.setVisibility(View.VISIBLE);
        }
        isHidden = !isHidden;
        mNewPwdEdit.postInvalidate();
        //切换后将EditText光标置于末尾
        CharSequence charSequence = mNewPwdEdit.getText();
        if (charSequence instanceof Spannable) {
            Spannable spanText = (Spannable) charSequence;
            Selection.setSelection(spanText, charSequence.length());
        }
    }

    @OnClick(R.id.login_forgetpwd)
    public void onBtnGetPwd() {
        mPwdPresenter.getmodifyPwdInfo(App.mAxLoginSp.getUserMobil());
    }

    @Override
    protected int getContentViewId() {
        return R.layout.me_msg_modifypwd_activity;
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
    public void modifySuccess() {
        Toast.makeText(this,"修改成功",Toast.LENGTH_LONG).show();
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
    public void resetPwdByVerInfoResult(Customer customer, String session) {}
    @Override
    public void setPresenter(IMeModifyPwdContract.IMeModifyPwdPresenter presenter) {
        mPwdPresenter = presenter;
    }
    @Override
    public void loadStart() {}
    @Override
    public void loadError(String errorMsg) {
        mNextBtn.setEnabled(true);
    }
    @Override
    public void loadComplete() {
        mNextBtn.setEnabled(true);
    }
}
