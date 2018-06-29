package com.zhihuianxin.xyaxf.app.me.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.zhihuianxin.xyaxf.app.AppConstant;
import com.axinfu.modellib.service.MeService;
import com.axinfu.modellib.thrift.customer.Customer;
import com.axinfu.modellib.thrift.customer.VerifyField;
import com.axinfu.modellib.thrift.ecard.ECard;
import com.axinfu.modellib.thrift.ecard.ECardAccount;
import com.axinfu.modellib.thrift.fee.FeeAccount;
import com.google.gson.Gson;
import com.zhihuianxin.xyaxf.app.base.axutil.Util;
import com.zhihuianxin.xyaxf.App;
import com.zhihuianxin.xyaxf.BaseRealmActionBarActivity;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.ApiFactory;
import com.zhihuianxin.xyaxf.app.BaseSubscriber;
import com.zhihuianxin.xyaxf.app.RetrofitFactory;
import com.zhihuianxin.xyaxf.app.login.view.activity.LoginGetPwdActivity;
import com.zhihuianxin.xyaxf.app.login.view.activity.LoginGetPwdByCodeActivity;
import com.zhihuianxin.xyaxf.app.me.contract.IMeStuMsgContract;
import com.zhihuianxin.xyaxf.app.me.presenter.MeStuMsgPresenter;
import com.zhihuianxin.xyaxf.app.utils.NetUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import io.realm.AccountVerifyItemRealmProxy;
import io.realm.CustomerRealmProxy;
import io.realm.ECardAccountRealmProxy;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.zhihuianxin.xyaxf.app.login.view.activity.LoginGetPwdActivity.EXTRA_VERIFY_DATA;

/**
 * Created by Vincent on 2016/10/20.
 */

public class MeStuXHModifyActivity extends BaseRealmActionBarActivity implements IMeStuMsgContract.IMeStuMsgView{
    private IMeStuMsgContract.IMeStuMsgPresenter mPresenter;
    @InjectView(R.id.xh)
    EditText mXHEdit;
    @InjectView(R.id.pwd)
    EditText mLoginPwdEdit;
    @InjectView(R.id.ecard_pwd)
    EditText mEcardEdit;
    @InjectView(R.id.next)
    Button next;

    private boolean mNeedEcardPwd;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);


        new MeStuMsgPresenter(this,this);
        final RealmResults<Customer> customerRealmResults = realm.where(Customer.class).equalTo("mobile", App.mAxLoginSp.getUserMobil())
                .findAllAsync();
        customerRealmResults.addChangeListener(new RealmChangeListener<RealmResults<Customer>>() {
            @Override
            public void onChange(RealmResults<Customer> customers) {
                if (!customers.isEmpty()){
                    // 一卡通密码去掉了
                    mNeedEcardPwd = false;//((ECardAccountRealmProxy) ((CustomerRealmProxy) customers.get(0)).realmGet$ecard_account()).realmGet$need_password();
                    if(mNeedEcardPwd){
                        mEcardEdit.setVisibility(View.VISIBLE);
                        mEcardEdit.setHint(((AccountVerifyItemRealmProxy) ((ECardAccountRealmProxy) ((CustomerRealmProxy) customers.get(0)).realmGet$ecard_account()).realmGet$account_no_verify_config()).realmGet$hint());
                    }
                }
            }
        });

        mLoginPwdEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                    if (editable.length()>0){
                        next.setEnabled(true);
                    }else {
                        next.setEnabled(false);
                    }
            }
        });
    }

    @OnClick(R.id.next)
    public void onBtnStuXHUpdate(){
        String xhAccount = mXHEdit.getText().toString().trim();
        String loginPwd = mLoginPwdEdit.getText().toString().trim();
        String ecardPwd = mEcardEdit.getText().toString().trim();

        if(Util.isEmpty(xhAccount) || Util.isEmpty(loginPwd)){
            Toast.makeText(this,"内容不可为空！",Toast.LENGTH_LONG).show();
            return;
        }
        if(loginPwd.length() < 6){
            Toast.makeText(this,"密码格式错误！",Toast.LENGTH_LONG).show();
            return;
        }
        if(mNeedEcardPwd){
            if(mEcardEdit.getText().length() == 0){
                Toast.makeText(this,"请输入一卡通密码！",Toast.LENGTH_LONG).show();
                return;
            }
        }
        mPresenter.modifyStuNo(xhAccount,"",loginPwd);
    }

    @OnClick(R.id.getPwdid)
    public void onBtnGetPwd(){
        mPresenter.getmodifyPwdInfo(App.mAxLoginSp.getUserMobil());
    }

    @Override
    protected int getContentViewId() {
        return R.layout.me_stu_xh_modify_activity;
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
    public void modifyStuNoSuccess(final FeeAccount feeAccount) {
        getNewCustomerAndUpdate();// 修改的是学号是主键，只有重新更新customer
    }

    private void getNewCustomerAndUpdate(){
        RetrofitFactory.setBaseUrl(AppConstant.URL);

        Map<String, Object> map = new HashMap<>();
        MeService meService = ApiFactory.getFactory().create(MeService.class);
        meService.getCustomer(NetUtils.getRequestParams(this, map), NetUtils.getSign(NetUtils.getRequestParams(this, map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(this,true,null) {

                    @Override
                    public void onNext(Object o) {
                        final MeStuAccountModifyActivity.GetCustomer response = new Gson().fromJson(o.toString(), MeStuAccountModifyActivity.GetCustomer.class);

                        realm.executeTransactionAsync(new Realm.Transaction() {
                            @Override
                            public void execute(Realm bgRealm) {
                                response.customer.mobile = App.mAxLoginSp.getUserMobil();
                                bgRealm.copyToRealmOrUpdate(response.customer);
                            }
                        }, new Realm.Transaction.OnSuccess() {
                            @Override
                            public void onSuccess() {
                                Toast.makeText(MeStuXHModifyActivity.this,"修改成功",Toast.LENGTH_LONG).show();
                                finish();
                            }
                        }, new Realm.Transaction.OnError() {
                            @Override
                            public void onError(Throwable error) {

                            }
                        });
                    }
                });
    }

    @Override
    public void modifyIDSuccess(FeeAccount feeAccount) {
        //no
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
