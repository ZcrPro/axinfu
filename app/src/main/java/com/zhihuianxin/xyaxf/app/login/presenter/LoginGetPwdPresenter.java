package com.zhihuianxin.xyaxf.app.login.presenter;

import android.content.Context;

import com.zhihuianxin.xyaxf.app.AppConstant;
import modellib.service.LoginService;
import com.google.gson.Gson;
import com.zhihuianxin.xyaxf.BuildConfig;
import com.zhihuianxin.xyaxf.app.ApiFactory;
import com.zhihuianxin.xyaxf.app.BaseSubscriber;
import com.zhihuianxin.xyaxf.app.RetrofitFactory;
import com.zhihuianxin.xyaxf.app.login.contract.ILoginGetPwdContract;
import com.zhihuianxin.xyaxf.app.utils.NetUtils;

import java.util.HashMap;
import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Vincent on 2016/11/12.
 */

public class LoginGetPwdPresenter implements ILoginGetPwdContract.ILoingGetPwdPresenter{
    private Context mContext;
    private ILoginGetPwdContract.ILoginGetPwdView mView;

    public LoginGetPwdPresenter(Context context, ILoginGetPwdContract.ILoginGetPwdView view){
        mContext = context;
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void getVerCode(String verify_for, String mobile) {
        mView.loadStart();

        Map<String,Object> map = new HashMap<>();
        map.put("mobile",mobile);
        map.put("verify_for", verify_for);
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        LoginService loginService = ApiFactory.getFactory().create(LoginService.class);
        loginService.getVerCode(NetUtils.getRequestParams(mContext,map), NetUtils.getSign(NetUtils.getRequestParams(mContext,map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(mContext,false,mView) {

                    @Override
                    public void onNext(Object o) {
                        if(BuildConfig.AnXinDEBUG){
                            LoginSetPwdOrRegistPresenter.GetVerCodeResponse vercode =
                                    new Gson().fromJson(o.toString(),LoginSetPwdOrRegistPresenter.GetVerCodeResponse.class);
                            mView.getVerCodeSuccess(vercode.code);
                        }
                    }
                });

    }

    @Override
    public void getPwd(String mobile, String secureity_code, String new_password, String attribute_code) {
        Map<String,Object> map = new HashMap<>();
        map.put("mobile",mobile);
        map.put("secureity_code", secureity_code);
        map.put("new_password", new_password);
        map.put("attribute_code", attribute_code);
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        LoginService loginService = ApiFactory.getFactory().create(LoginService.class);
        loginService.setPwdAndLogin(NetUtils.getRequestParams(mContext,map), NetUtils.getSign(NetUtils.getRequestParams(mContext,map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(mContext,true,mView) {
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        if(e.getMessage().contains(AppConstant.MODIFY_PWD_FIELD_ERROR_TIME_BYCODE)){
                            mView.getPwdSuccess(null,e.getMessage().split("\\(")[0]);
                            // 要弹框
                        } else {
                            // 底层Toast
                        }
                    }

                    @Override
                    public void onNext(Object o) {
                        LoginSetPwdOrRegistPresenter.LoginAndSetPasswordResponse response =
                                new Gson().fromJson(o.toString(),LoginSetPwdOrRegistPresenter.LoginAndSetPasswordResponse.class);
                        mView.getPwdSuccess(response.customer,response.session);
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
