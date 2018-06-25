package com.zhihuianxin.xyaxf.app.login.view.activity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.axinfu.modellib.thrift.customer.Customer;
import com.axinfu.modellib.thrift.customer.VerifyField;
import com.zhihuianxin.axutil.Util;
import com.zhihuianxin.xyaxf.App;
import com.zhihuianxin.xyaxf.BaseRealmActionBarActivity;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.login.contract.ILoginVerPwdFieldContract;
import com.zhihuianxin.xyaxf.app.login.presenter.LoginVerPwdFieldPresenter;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Vincent on 2017/12/20.
 */

public class LoginGetPwdByVerMsgActivity extends BaseRealmActionBarActivity implements ILoginVerPwdFieldContract.ILoginVerPwdFieldView{
    @InjectView(R.id.backAnimView)
    View mBackAlertView;
    @InjectView(R.id.grayBg)
    View mGrayBg;
    @InjectView(R.id.click_focus)
    Button closeAniBtn;
    @InjectView(R.id.cancelname)
    EditText nameEdit;
    @InjectView(R.id.cancelxuehao)
    EditText xuehaoEdit;
    @InjectView(R.id.cancelid)
    EditText idEdit;
    @InjectView(R.id.bankid)
    EditText bankidEdit;

    @InjectView(R.id.nameviewid)
    View nameView;
    @InjectView(R.id.xuehaoviewid)
    View xuehaoView;
    @InjectView(R.id.idviewid)
    View idView;
    @InjectView(R.id.bankcardtipstxt)
    TextView bankTipsTxt;
    @InjectView(R.id.tel_desc)
    TextView errorTxt;
    @InjectView(R.id.toplinerviewid)
    View topLinerView;

    private DisplayMetrics metrics;
    private ArrayList<VerifyField> verifyFields;
    private ILoginVerPwdFieldContract.ILoginVerPwdFieldPresenter presenter;
    @Override
    protected int getContentViewId() {
        return R.layout.login_getpwd_byvermsg_activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        new LoginVerPwdFieldPresenter(this,this);
        verifyFields = (ArrayList<VerifyField>) getIntent().getExtras().getSerializable(LoginGetPwdActivity.EXTRA_VERIFY_DATA);
        initView();
    }

    private void initView(){
        for(int i = 0;i < verifyFields.size();i++){
            VerifyField field = verifyFields.get(i);
            if(field.field_name.equals("Name")){
                nameView.setVisibility(View.VISIBLE);
                nameEdit.setHint(field.hint);
                nameEdit.setTag(field.name);
            } else if(verifyFields.get(i).field_name.equals("StudentNO")){
                xuehaoView.setVisibility(View.VISIBLE);
                xuehaoEdit.setHint(field.hint);
                xuehaoEdit.setTag(field.name);
            } else if(verifyFields.get(i).field_name.equals("IDCardNO")){
                idView.setVisibility(View.VISIBLE);
                idEdit.setHint(field.hint);
                idEdit.setTag(field.name);
            } else if(verifyFields.get(i).field_name.equals("BankCardNO")){
                bankTipsTxt.setVisibility(View.VISIBLE);
                bankidEdit.setVisibility(View.VISIBLE);
                bankidEdit.setHint(field.hint);
                bankidEdit.setTag(field.name);
            }
        }

        if(verifyFields.size() == 1 && verifyFields.get(0).field_name.equals("BankCardNO")){
            topLinerView.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.next)
    public void onNextClick(View view){
        if(nameView.getVisibility()==View.VISIBLE && Util.isEmpty(nameEdit.getText().toString().trim())){
            Toast.makeText(this,nameEdit.getTag()+"不能为空",Toast.LENGTH_LONG).show();
            return;
        }
        if(xuehaoView.getVisibility()==View.VISIBLE && Util.isEmpty(xuehaoEdit.getText().toString().trim())){
            Toast.makeText(this,xuehaoEdit.getTag()+"不能为空",Toast.LENGTH_LONG).show();
            return;
        }
        if(idView.getVisibility()==View.VISIBLE && Util.isEmpty(idEdit.getText().toString().trim())){
            Toast.makeText(this,idEdit.getTag()+"不能为空",Toast.LENGTH_LONG).show();
            return;
        }
        if(bankidEdit.getVisibility()==View.VISIBLE && Util.isEmpty(bankidEdit.getText().toString().trim())){
            Toast.makeText(this,bankidEdit.getTag()+"不能为空",Toast.LENGTH_LONG).show();
            return;
        }
        if(getCurrentFocus()!=null) {
            ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
        presenter.verpwdField(App.mAxLoginSp.getUserMobil(),
                nameEdit.getText().toString().trim(),
                xuehaoEdit.getText().toString().trim(),
                idEdit.getText().toString().trim(),
                bankidEdit.getText().toString().trim());
    }

    private void showAlertAnim(){
        int halfScreen = (metrics.heightPixels / 2) + 200;
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(mBackAlertView, "translationY", 0,
                halfScreen,halfScreen + 50,halfScreen + 10,halfScreen + 35,halfScreen + 30);//450 410 435 430
        animator2.setDuration(700);
        animator2.start();
        mGrayBg.setVisibility(View.VISIBLE);

        closeAniBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideBackAlertAnim();
            }
        });
    }

    private void hideBackAlertAnim(){
        int halfScreen = (metrics.heightPixels / 2) + 200;
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(mBackAlertView, "translationY",halfScreen + 30,0);
        animator2.setDuration(600);
        animator2.start();
        mGrayBg.setVisibility(View.GONE);
    }

    @Override
    public void verpwdFieldResult(String errorMsg) {
        if(Util.isEmpty(errorMsg)){
            Intent intent = new Intent(this,LoginSetPwdByFieldActivity.class);
            if(getIntent().getExtras() != null && getIntent().getExtras().getString(LoginGetPwdActivity.EXTRA_FROM_UNLOGIN) != null){
                Bundle bundle = new Bundle();
                bundle.putString(LoginGetPwdActivity.EXTRA_FROM_UNLOGIN,getIntent().getExtras().getString(LoginGetPwdActivity.EXTRA_FROM_UNLOGIN));
                intent.putExtras(bundle);
            }
            startActivity(intent);
            finish();
        } else {
            errorTxt.setText(errorMsg);
            showAlertAnim();
        }
    }

    @Override
    public void setPwdByFieldResult(Customer customer, String session) {}
    @Override
    public void setPresenter(ILoginVerPwdFieldContract.ILoginVerPwdFieldPresenter presenter) {this.presenter = presenter;}
    @Override
    public void loadStart() {}
    @Override
    public void loadError(String errorMsg) {}
    @Override
    public void loadComplete() {}
}
