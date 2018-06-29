package com.zhihuianxin.xyaxf.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import modellib.service.LoginService;
import modellib.thrift.customer.Customer;
import com.google.gson.Gson;
import com.zhihuianxin.xyaxf.App;
import com.zhihuianxin.xyaxf.BaseRealmActionBarActivity;
import com.zhihuianxin.xyaxf.MainActivity;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.login.presenter.LoginVerPwdPresenter;
import com.zhihuianxin.xyaxf.app.utils.NetUtils;

import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by zcrprozcrpro on 2017/6/26.
 */

public class TestLoginActivity extends BaseRealmActionBarActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Map<String, Object> map = new HashMap<>();
        map.put("mobile", "13800138000");
        map.put("password", "111111");
        map.put("attribute_code", App.mAxLoginSp.getUUID().replace("-", ""));
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        LoginService loginService = ApiFactory.getFactory().create(LoginService.class);
        loginService.login(NetUtils.getRequestParams(this, map), NetUtils.getSign(NetUtils.getRequestParams(this, map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Object>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Object o) {
                        LoginVerPwdPresenter.LoginResponse response = new Gson().fromJson(o.toString(), LoginVerPwdPresenter.LoginResponse.class);
                        loginSuccess(response.customer,response.session);
                    }
                });
    }

    public void loginSuccess(final Customer customer, String session) {
        /**
         * 存入customer
         */
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                customer.mobile = "13800138000";
                bgRealm.copyToRealmOrUpdate(customer);
            }
        });
        App.mSession.setSession(session);
        App.mAxLoginSp.setUserMobil(customer.base_info.mobile);
        App.mAxLoginSp.setRegistSerial(customer.base_info.regist_serial);
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_login_test;
    }
}
