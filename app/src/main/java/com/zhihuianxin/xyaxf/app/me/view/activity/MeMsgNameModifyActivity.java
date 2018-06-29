package com.zhihuianxin.xyaxf.app.me.view.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import modellib.thrift.customer.Customer;
import modellib.thrift.customer.CustomerBaseInfo;
import modellib.thrift.customer.VerifyField;
import com.zhihuianxin.xyaxf.app.base.axutil.Util;
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

public class MeMsgNameModifyActivity extends BaseRealmActionBarActivity implements IMeMsgContract.IMeMsgView{
    private IMeMsgContract.IMeMsgPresenter mPresenter;

    @InjectView(R.id.nickname_edit)
    EditText mNickNameEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);

        new MeMsgPresenter(this,this);
        String nickName = getNameFromDB();
        mNickNameEdit.setText(nickName);
    }

    private String getNameFromDB(){
        RealmResults<Customer> realmResults = realm.
                where(Customer.class).equalTo("mobile", App.mAxLoginSp.getUserMobil()).findAll();
        if(realmResults.size() <= 0){
            return "";
        }
        String name = ((CustomerBaseInfoRealmProxy)(((CustomerRealmProxy)
                realmResults.get(0))).realmGet$base_info()).realmGet$nickname();
        return Util.isEmpty(name)?"":name;
    }

    @OnClick(R.id.submit)
    public void onBtnModifyNickName(){
        String nickName = mNickNameEdit.getText().toString().trim();
        if(!Util.isEmpty(nickName)){
            Customer customer = realm.where(Customer.class).findAll().get(0);
            if(customer.base_info == null){
                CustomerBaseInfo baseInfo = new CustomerBaseInfo();
                baseInfo.nickname = nickName;
                customer.base_info = baseInfo;
            } else{
                customer.base_info.nickname = nickName;
            }
            mPresenter.modifyBaseInfo(customer.base_info);
        }
    }

    @Override
    protected int getContentViewId() {
        return R.layout.me_msg_namemodify_activity;
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
                Log.d("msgName", "存储customer数据成功!");
                Toast.makeText(MeMsgNameModifyActivity.this,"修改成功",Toast.LENGTH_LONG).show();
                finish();

            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                // Transaction failed and was automatically canceled.
                Log.d("msgName", "存储customer数据失败!");
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
