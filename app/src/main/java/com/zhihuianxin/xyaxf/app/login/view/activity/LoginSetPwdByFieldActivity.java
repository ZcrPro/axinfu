package com.zhihuianxin.xyaxf.app.login.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import modellib.service.LoginService;
import modellib.thrift.customer.Customer;
import com.google.gson.Gson;
import com.zhihuianxin.xyaxf.app.base.axutil.Util;
import com.zhihuianxin.xyaxf.App;
import com.zhihuianxin.xyaxf.BaseRealmActionBarActivity;
import com.zhihuianxin.xyaxf.MainActivity;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.ApiFactory;
import com.zhihuianxin.xyaxf.app.BaseSubscriber;
import com.zhihuianxin.xyaxf.app.login.contract.ILoginVerPwdFieldContract;
import com.zhihuianxin.xyaxf.app.login.presenter.LoginVerPwdFieldPresenter;
import com.zhihuianxin.xyaxf.app.login.view.fragment.LoginSelectCityActivity;
import com.zhihuianxin.xyaxf.app.utils.NetUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import io.realm.Realm;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Vincent on 2017/12/21.
 */

public class LoginSetPwdByFieldActivity extends BaseRealmActionBarActivity implements ILoginVerPwdFieldContract.ILoginVerPwdFieldView{
    @InjectView(R.id.pwdid)
    EditText pwdEdit;
    @InjectView(R.id.next)
    Button nextBtn;

    private ILoginVerPwdFieldContract.ILoginVerPwdFieldPresenter presenter;
    @Override
    protected int getContentViewId() {
        return R.layout.login_setpwd_byfoeld_activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);

        new LoginVerPwdFieldPresenter(this,this);
    }

    @OnClick(R.id.next)
    public void nextOnClick(View view){
        if(Util.isEmpty(pwdEdit.getText().toString().trim())){
            Toast.makeText(this,"请输入密码",Toast.LENGTH_LONG).show();
        } else{
            presenter.setPwdByField(App.mAxLoginSp.getUserMobil(),
                    pwdEdit.getText().toString().trim(),
                    App.mAxLoginSp.getUUID().replace("-", ""));
        }
    }

    @Override
    public void setPwdByFieldResult(final Customer customer, String session) {
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
        App.mSession.setSession(session);
        Toast.makeText(LoginSetPwdByFieldActivity.this, "修改成功", Toast.LENGTH_LONG).show();
        if(getIntent().getExtras() != null && getIntent().getExtras().getString(LoginGetPwdActivity.EXTRA_FROM_UNLOGIN) != null){
            login(App.mAxLoginSp.getUserMobil(), pwdEdit.getText().toString().trim(), App.mAxLoginSp.getUUID().replace("-", ""));
        } else{
            finish();
        }
    }

    public void login(String mobile, String pwd, String uuid) {
        Map<String, Object> map = new HashMap<>();
        map.put("mobile", mobile);
        map.put("password", pwd);
        map.put("attribute_code", uuid);
        LoginService loginService = ApiFactory.getFactory().create(LoginService.class);
        loginService.login(NetUtils.getRequestParams(this, map), NetUtils.getSign(NetUtils.getRequestParams(this, map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(this, true, this) {
                    @Override
                    public void onNext(Object o) {
                        final LoginGetPwdByCodeActivity.LoginResponse response = new Gson().fromJson(o.toString(), LoginGetPwdByCodeActivity.LoginResponse.class);

                        /**
                         * 存入customer
                         */
                        realm.executeTransactionAsync(new Realm.Transaction() {
                            @Override
                            public void execute(Realm bgRealm) {
                                response.customer.mobile = App.mAxLoginSp.getUserMobil();
                                bgRealm.copyToRealmOrUpdate(response.customer);
                            }
                        }, new Realm.Transaction.OnSuccess() {
                            @Override
                            public void onSuccess() {
                                // Transaction was a success.
                                Log.d("LoginVerPwdActivity", "存储customer数据成功!");
                            }
                        }, new Realm.Transaction.OnError() {
                            @Override
                            public void onError(Throwable error) {
                                // Transaction failed and was automatically canceled.
                                Log.d("LoginVerPwdActivity", "存储customer数据失败!");
                            }
                        });

                        App.mSession.setSession(response.session);

                        App.mAxLoginSp.setUserMobil(response.customer.base_info.mobile);

                        //App.mAxLoginSp.setLoginSign(true);
                        if (response.customer.school != null) {
                            startActivity(new Intent(LoginSetPwdByFieldActivity.this, MainActivity.class));
                            //ActivityCollector.removeAllActivities();
                        } else {
                            startActivity(new Intent(LoginSetPwdByFieldActivity.this, LoginSelectCityActivity.class));
                        }
                        finish();
                        App.finishAllLoginActivity();
                    }
                });
    }

    @Override
    public void verpwdFieldResult(String errorMsg) {}
    @Override
    public void setPresenter(ILoginVerPwdFieldContract.ILoginVerPwdFieldPresenter presenter) {this.presenter = presenter;}
    @Override
    public void loadStart() {}
    @Override
    public void loadError(String errorMsg) {}
    @Override
    public void loadComplete() {}
}
