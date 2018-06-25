package com.zhihuianxin.xyaxf.app.login.presenter;

import android.content.Context;

import com.zhihuianxin.xyaxf.app.AppConstant;
import com.axinfu.modellib.service.CustomerService;
import com.axinfu.modellib.service.LoginService;
import com.axinfu.modellib.thrift.base.BaseResponse;
import com.axinfu.modellib.thrift.customer.Customer;
import com.google.gson.Gson;
import com.zhihuianxin.xyaxf.app.ApiFactory;
import com.zhihuianxin.xyaxf.app.BaseSubscriber;
import com.zhihuianxin.xyaxf.app.RetrofitFactory;
import com.zhihuianxin.xyaxf.app.login.contract.ILoginVerPwdContract;
import com.zhihuianxin.xyaxf.app.utils.NetUtils;

import java.util.HashMap;
import java.util.Map;

import io.realm.RealmObject;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Vincent on 2016/10/11.
 */

public class LoginVerPwdPresenter implements ILoginVerPwdContract.ILoginVerPwdPresenter {
    private ILoginVerPwdContract.ILoginVerPwdView mView;
    private Context mContext;

    public static class LoginResponse extends RealmObject{
        public BaseResponse resp ;
        public Customer customer ;
        public String session ;
    }

    public LoginVerPwdPresenter(Context context, ILoginVerPwdContract.ILoginVerPwdView v){
        mContext = context;
        mView = v;
        mView.setPresenter(this);
    }

    @Override
    public void login(String mobile,String pwd,String uuid) {
        Map<String,Object> map = new HashMap<>();
        map.put("mobile",mobile);
        map.put("password",pwd);
        map.put("attribute_code",uuid);
        LoginService loginService = ApiFactory.getFactory().create(LoginService.class);
        Subscription subscription =
                loginService.login(NetUtils.getRequestParams(mContext,map), NetUtils.getSign(NetUtils.getRequestParams(mContext,map)))
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new BaseSubscriber<Object>(mContext,true,mView) {
                            @Override
                            public void onNext(Object o) {
                                LoginResponse response = new Gson().fromJson(o.toString(),LoginResponse.class);
                                mView.loginSuccess(response.customer,response.session);                            }
                        });

    }

    @Override
    public void getmodifyPwdInfo(String mobile) {
        Map<String,Object> map = new HashMap<>();
        map.put("mobile",mobile);
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        CustomerService loginService = ApiFactory.getFactory().create(CustomerService.class);
        loginService.modifyPwdGetVerMsg(NetUtils.getRequestParams(mContext,map), NetUtils.getSign(NetUtils.getRequestParams(mContext,map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(mContext,true,mView) {
                    @Override
                    public void onNext(Object o) {
                        LoginHasPwdPresenter.GetPwdVerInfoResponse response = new Gson().fromJson(o.toString(),LoginHasPwdPresenter.GetPwdVerInfoResponse.class);
                        mView.getmodifyPwdInfoResult(response.verify_fields);
                    }
                });
    }

    @Override
    public void subscribe() {

    }

    @Override
    public void unsubscribe() {

    }
}
