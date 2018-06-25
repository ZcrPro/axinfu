package com.zhihuianxin.xyaxf.app.me.presenter;

import android.content.Context;

import com.zhihuianxin.xyaxf.app.AppConstant;
import com.axinfu.modellib.service.CustomerService;
import com.axinfu.modellib.service.LoginService;
import com.axinfu.modellib.service.MeService;
import com.axinfu.modellib.thrift.base.BaseResponse;
import com.axinfu.modellib.thrift.customer.Customer;
import com.axinfu.modellib.thrift.customer.CustomerBaseInfo;
import com.axinfu.modellib.thrift.secure_code.VerifyFor;
import com.google.gson.Gson;
import com.zhihuianxin.xyaxf.BuildConfig;
import com.zhihuianxin.xyaxf.app.ApiFactory;
import com.zhihuianxin.xyaxf.app.BaseSubscriber;
import com.zhihuianxin.xyaxf.app.RetrofitFactory;
import com.zhihuianxin.xyaxf.app.login.presenter.LoginHasPwdPresenter;
import com.zhihuianxin.xyaxf.app.login.presenter.LoginSetPwdOrRegistPresenter;
import com.zhihuianxin.xyaxf.app.me.contract.IMeMsgContract;
import com.zhihuianxin.xyaxf.app.utils.NetUtils;

import java.util.HashMap;
import java.util.Map;

import io.realm.RealmObject;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Vincent on 2016/11/7.
 */

public class MeMsgPresenter implements IMeMsgContract.IMeMsgPresenter{
    private Context mContext;
    private IMeMsgContract.IMeMsgView mView;

    public static class UpdateBaseInfoResponse extends RealmObject{
        public BaseResponse resp;
        public Customer customer;
    }

    public MeMsgPresenter(Context context,IMeMsgContract.IMeMsgView view){
        mContext = context;
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void modifyBaseInfo(CustomerBaseInfo baseInfo) {
        RetrofitFactory.setBaseUrl(AppConstant.URL);

        Map<String,Object> map = new HashMap<>();
        map.put("base_info",baseInfo);
        MeService meService = ApiFactory.getFactory().create(MeService.class);
        meService.modifyBaseInfo(NetUtils.getRequestParams(mContext,map),NetUtils.getSign(NetUtils.getRequestParams(mContext,map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(mContext,true,mView) {

                    @Override
                    public void onNext(Object o) {
                        UpdateBaseInfoResponse response = new Gson().fromJson(o.toString(),UpdateBaseInfoResponse.class);
                        mView.modifyBaseInfoSuccess(response.customer);
                    }
                });
    }

    @Override
    public void modifyMobile(String mobile,String verCode,String newPwd) {
        Map<String,Object> map = new HashMap<>();
        map.put("new_mobile",mobile);
        map.put("security_code", verCode);
        map.put("login_password", newPwd);
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        MeService meService = ApiFactory.getFactory().create(MeService.class);
        meService.modifyMoblie(NetUtils.getRequestParams(mContext,map), NetUtils.getSign(NetUtils.getRequestParams(mContext,map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(mContext,true,mView) {

                    @Override
                    public void onNext(Object o) {
                        UpdateBaseInfoResponse response = new Gson().fromJson(o.toString(),UpdateBaseInfoResponse.class);
                        mView.modifyMobileSuccess(response.customer);
                    }
                });

    }

    @Override
    public void getVerCode(String mobile) {
        Map<String,Object> map = new HashMap<>();
        map.put("mobile",mobile);
        map.put("verify_for", VerifyFor.ChangeMobile.name());
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        LoginService loginService = ApiFactory.getFactory().create(LoginService.class);
        loginService.getVerCode(NetUtils.getRequestParams(mContext,map), NetUtils.getSign(NetUtils.getRequestParams(mContext,map)))
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new BaseSubscriber<Object>(mContext,false,mView) {

                            @Override
                            public void onNext(Object o) {
                                if(BuildConfig.AnXinDEBUG){
                                    LoginSetPwdOrRegistPresenter.GetVerCodeResponse vercode = new Gson().fromJson(o.toString(),LoginSetPwdOrRegistPresenter.GetVerCodeResponse.class);
                                    mView.getVerCodeSuccess(vercode.code);
                                }
                            }
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
