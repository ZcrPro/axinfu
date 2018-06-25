package com.zhihuianxin.xyaxf.app.me.view.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
 * Created by Vincent on 2016/11/9.
 */

public class MeStuNameModifyActivity extends BaseRealmActionBarActivity implements IMeMsgContract.IMeMsgView {
    private IMeMsgContract.IMeMsgPresenter mPresenter;
    @InjectView(R.id.name_edit)
    EditText mNameText;
    @InjectView(R.id.submit)
    Button submit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);

        new MeMsgPresenter(this, this);

        mNameText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() == 0) {
                    submit.setEnabled(false);
                } else {
                    submit.setEnabled(true);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        initView();
    }

    private void initView() {
        mNameText.setText(getNameFromDB());
    }

    private String getNameFromDB() {
        RealmResults<Customer> realmResults = realm.
                where(Customer.class).equalTo("mobile", App.mAxLoginSp.getUserMobil()).findAll();
        if (realmResults.size() <= 0) {
            return "";
        }
        String name = ((CustomerBaseInfoRealmProxy) (((CustomerRealmProxy)
                realmResults.get(0))).realmGet$base_info()).realmGet$name();
        return Util.isEmpty(name) ? "" : name;
    }

    @OnClick(R.id.submit)
    public void onBtnModifyName() {
        String name = mNameText.getText().toString().trim();
        if (!Util.isEmpty(name)) {
            Customer customer = realm.where(Customer.class).findAll().get(0);
            if (customer.base_info == null) {
                CustomerBaseInfo baseInfo = new CustomerBaseInfo();
                baseInfo.name = name;
                customer.base_info = baseInfo;
            } else {
                customer.base_info.name = name;
            }
            mPresenter.modifyBaseInfo(customer.base_info);
        }
    }

    @Override
    public void modifyMobileSuccess(final Customer customer) {



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
                Log.d("stuName", "存储customer数据成功!");
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                // Transaction failed and was automatically canceled.
                Log.d("stuName", "存储customer数据失败!");
            }
        });
        Toast.makeText(this, "修改成功", Toast.LENGTH_LONG).show();

        finish();

    }

    @Override
    public void getVerCodeSuccess(String code) {
        // NO USE
    }

    @Override
    public void getmodifyPwdInfoResult(ArrayList<VerifyField> verify_fields) {

    }

    @Override
    protected int getContentViewId() {
        return R.layout.me_stu_namemodify_activity;
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
                Log.d("stuName", "存储customer数据成功!");
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                // Transaction failed and was automatically canceled.
                Log.d("stuName", "存储customer数据失败!");
            }
        });
        Toast.makeText(this, "修改成功", Toast.LENGTH_LONG).show();


        finish();
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
