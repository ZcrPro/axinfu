package com.zhihuianxin.xyaxf.app.me.view.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Toast;

import com.axinfu.modellib.thrift.customer.Customer;
import com.axinfu.modellib.thrift.customer.CustomerBaseInfo;
import com.axinfu.modellib.thrift.customer.VerifyField;
import com.zhihuianxin.axutil.Util;
import com.zhihuianxin.xyaxf.App;
import com.zhihuianxin.xyaxf.BaseRealmActionBarActivity;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.me.contract.IMeMsgContract;
import com.zhihuianxin.xyaxf.app.me.presenter.MeMsgPresenter;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import io.realm.CustomerBaseInfoRealmProxy;
import io.realm.CustomerRealmProxy;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Vincent on 2016/10/20.
 */

public class MeMsgGenderModifyActivity extends BaseRealmActionBarActivity implements IMeMsgContract.IMeMsgView{
    private IMeMsgContract.IMeMsgPresenter mPresenter;

    @InjectView(R.id.radioMale)
    RadioButton mRadioMale;
    @InjectView(R.id.radioFemale)
    RadioButton mRadioFamale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);

        initView();
        new MeMsgPresenter(this,this);
        mRadioMale.setOnCheckedChangeListener(changeListenerMale);
        mRadioFamale.setOnCheckedChangeListener(changeListenerFaMale);
    }

    @OnClick(R.id.modify_genter)
    public void onBtnGenderModify(){
        String gender;
        if(mRadioMale.isChecked()){
            gender = "Male";
        } else{
            gender = "Female";
        }
        Customer customer =  realm.where(Customer.class).findAll().get(0);
        if(customer.base_info == null){
            CustomerBaseInfo baseInfo = new CustomerBaseInfo();
            baseInfo.gender = gender;
            customer.base_info = baseInfo;
        } else{
            customer.base_info.gender = gender;
        }
        mPresenter.modifyBaseInfo(customer.base_info);
    }

    CompoundButton.OnCheckedChangeListener changeListenerMale = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked){
                mRadioFamale.setChecked(false);
            }
        }
    };

    CompoundButton.OnCheckedChangeListener changeListenerFaMale = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked){
                mRadioMale.setChecked(false);
            }
        }
    };

    private void initView(){
        RealmResults<Customer> realmResults = realm.where(Customer.class).equalTo("mobile", App.mAxLoginSp.getUserMobil()).findAll();
        String gender = ((CustomerBaseInfoRealmProxy)(((CustomerRealmProxy)
                realmResults.get(0))).realmGet$base_info()).realmGet$gender();
        if(Util.isEmpty(gender)){
            mRadioMale.setChecked(true);
        } else{
            if(gender.equals("Male")){
                mRadioMale.setChecked(true);
            } else {
                mRadioFamale.setChecked(true);
            }
        }
    }

    @Override
    protected int getContentViewId() {
        return R.layout.me_msg_gender_mod_activity;
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
    public void modifyBaseInfoSuccess(final Customer customer) {
        /**
         * 存入customer
         */
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                customer.mobile = App.mAxLoginSp.getUserMobil();
                bgRealm.copyToRealmOrUpdate(customer);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                // Transaction was a success.
                Log.d("MSGGender", "存储customer数据成功!");
                Toast.makeText(MeMsgGenderModifyActivity.this,"修改成功",Toast.LENGTH_LONG).show();
                finish();
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                // Transaction failed and was automatically canceled.
                Log.d("MSGGender", "存储customer数据失败!");
            }
        });

    }

    @Override
    public void modifyMobileSuccess(Customer customer) {

    }

    @Override
    public void getVerCodeSuccess(String code) {

    }

    @Override
    public void getmodifyPwdInfoResult(ArrayList<VerifyField> verify_fields) {

    }

    @Override
    public void setPresenter(IMeMsgContract.IMeMsgPresenter presenter) {
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
