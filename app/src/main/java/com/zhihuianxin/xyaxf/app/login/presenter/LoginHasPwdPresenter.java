package com.zhihuianxin.xyaxf.app.login.presenter;

import android.content.Context;

import com.zhihuianxin.xyaxf.app.AppConstant;
import com.axinfu.modellib.service.CustomerService;
import com.axinfu.modellib.service.LoginService;
import com.axinfu.modellib.thrift.base.BaseResponse;
import com.axinfu.modellib.thrift.customer.MobileStatus;
import com.axinfu.modellib.thrift.customer.VerifyField;
import com.axinfu.modellib.thrift.secure_code.VerifyFor;
import com.google.gson.Gson;
import com.zhihuianxin.xyaxf.App;
import com.zhihuianxin.xyaxf.BuildConfig;
import com.zhihuianxin.xyaxf.app.ApiFactory;
import com.zhihuianxin.xyaxf.app.BaseSubscriber;
import com.zhihuianxin.xyaxf.app.RetrofitFactory;
import com.zhihuianxin.xyaxf.app.login.contract.ILoginHasPwdContract;
import com.zhihuianxin.xyaxf.app.utils.NetUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.realm.RealmObject;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Vincent on 2016/10/18.
 */

public class LoginHasPwdPresenter implements ILoginHasPwdContract.ILoginHasPwdPresenter{
    private ILoginHasPwdContract.ILoginHasPwdView mView;
    private Context mContext;
    private CompositeSubscription mSubscriptions;

    public static class GetPwdVerInfoResponse{
        public BaseResponse resp;
        public ArrayList<VerifyField> verify_fields;
    }

    public static class HasLoginStatusResponse extends RealmObject{
        public BaseResponse resp;
        public MobileStatus status;
    }

    public LoginHasPwdPresenter(Context context, ILoginHasPwdContract.ILoginHasPwdView view){
        mContext = context;
        mView = view;
        mView.setPresenter(this);
        mSubscriptions = new CompositeSubscription();
    }

    private void isSetPwd(String mobile){
        Map<String,Object> map = new HashMap<>();
        map.put("mobile",mobile);
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        LoginService loginService = ApiFactory.getFactory().create(LoginService.class);
        Subscription subscription =
                loginService.getLoginStatus(NetUtils.getRequestParams(mContext,map),
                        NetUtils.getSign(NetUtils.getRequestParams(mContext,map)))
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new BaseSubscriber<Object>(mContext,true,mView) {
                            @Override
                            public void onNext(Object o) {
                                HasLoginStatusResponse status = new Gson().fromJson(o.toString(),HasLoginStatusResponse.class);
                                mView.serverPwd(status.status);
                            }
                        });
        mSubscriptions.add(subscription);
    }

    @Override
    public void hasSetPwd(String url) {
        isSetPwd(url);
    }

    @Override
    public void login(String mobile, String pwd, String uuid) {
        Map<String,Object> map = new HashMap<>();
        map.put("mobile",mobile);
        map.put("password",pwd);
        map.put("attribute_code",uuid);
        LoginService loginService = ApiFactory.getFactory().create(LoginService.class);
        loginService.login(NetUtils.getRequestParams(mContext,map), NetUtils.getSign(NetUtils.getRequestParams(mContext,map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(mContext,true,mView) {
                    @Override
                    public void onNext(Object o) {
                        LoginVerPwdPresenter.LoginResponse response = new Gson().fromJson(o.toString(),LoginVerPwdPresenter.LoginResponse.class);
                        mView.loginSuccess(response.customer,response.session);                            }
                });
    }

    @Override
    public void getVerCode(String mobile,String UserStatue) {
         Map<String,Object> map = new HashMap<>();
        map.put("mobile",mobile);
        if(UserStatue.equals(MobileStatus.NotRegistered.name())){
            map.put("verify_for", VerifyFor.Register.name());
        } else{// 老用户，是注册了了的，需要重新设置密码。
            map.put("verify_for", VerifyFor.ResetPassword.name());
        }
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        LoginService loginService = ApiFactory.getFactory().create(LoginService.class);
        Subscription subscription =
                loginService.getVerCode(NetUtils.getRequestParams(mContext,map), NetUtils.getSign(NetUtils.getRequestParams(mContext,map)))
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new BaseSubscriber<Object>(mContext,false,mView) {
                            @Override
                            public void onNext(Object o) {
                                if(BuildConfig.BUILD_TYPE.equals("debug")){
                                    if(BuildConfig.AnXinDEBUG){
                                        LoginSetPwdOrRegistPresenter.GetVerCodeResponse vercode = new Gson().fromJson(o.toString(),LoginSetPwdOrRegistPresenter.GetVerCodeResponse.class);
                                        mView.getVerCodeSuccess(vercode.code);
                                    }
                                }
                            }
                        });
        mSubscriptions.add(subscription);
    }

    @Override
    public void setPwdOrRegistAndLogin(String mobile, String verCode, String pwd, String machineId) {
        Map<String,Object> map = new HashMap<>();
        map.put("mobile",mobile);
        map.put("secureity_code",verCode);
        map.put("attribute_code",machineId);
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        LoginService loginService = ApiFactory.getFactory().create(LoginService.class);

        Observable<String> objectObservable;
        if(App.mAxLoginSp.getUserStatue().equals(MobileStatus.NotRegistered.name())){
            map.put("password",pwd);
            objectObservable = loginService.regist(NetUtils.getRequestParams(mContext,map), NetUtils.getSign(NetUtils.getRequestParams(mContext,map)));
        } else{
            map.put("new_password",pwd);
            objectObservable = loginService.setPwdAndLogin(NetUtils.getRequestParams(mContext,map), NetUtils.getSign(NetUtils.getRequestParams(mContext,map)));
        }
        Subscription subscription = objectObservable
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(mContext,true,mView) {
                    @Override
                    public void onNext(Object o) {
                        LoginSetPwdOrRegistPresenter.LoginAndSetPasswordResponse response = new Gson().fromJson(o.toString(),LoginSetPwdOrRegistPresenter.LoginAndSetPasswordResponse.class);
                        mView.setPwdOrRegistAndLoginSuccess(response.customer,response.session);                            }
                });
        mSubscriptions.add(subscription);
    }

    @Override
    public void getmodifyPwdInfo(String mobile) {
        Map<String,Object> map = new HashMap<>();
        map.put("mobile",mobile);
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        CustomerService loginService = ApiFactory.getFactory().create(CustomerService.class);
        Subscription subscription =
                loginService.modifyPwdGetVerMsg(NetUtils.getRequestParams(mContext,map), NetUtils.getSign(NetUtils.getRequestParams(mContext,map)))
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new BaseSubscriber<Object>(mContext,false,mView) {
                            @Override
                            public void onNext(Object o) {
                                GetPwdVerInfoResponse response = new Gson().fromJson(o.toString(),GetPwdVerInfoResponse.class);
                                mView.getmodifyPwdInfoResult(response.verify_fields);
                            }
                        });
        mSubscriptions.add(subscription);
    }

    @Override
    public void subscribe() {

    }

    @Override
    public void unsubscribe() {
        mSubscriptions.clear();
    }
}
