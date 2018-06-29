package com.zhihuianxin.xyaxf.app.me.presenter;

import android.content.Context;

import com.zhihuianxin.xyaxf.app.AppConstant;
import modellib.service.CustomerService;
import modellib.service.MeService;
import com.google.gson.Gson;
import com.zhihuianxin.xyaxf.app.ApiFactory;
import com.zhihuianxin.xyaxf.app.BaseSubscriber;
import com.zhihuianxin.xyaxf.app.RetrofitFactory;
import com.zhihuianxin.xyaxf.app.login.presenter.LoginHasPwdPresenter;
import com.zhihuianxin.xyaxf.app.me.contract.IMeModifyPwdContract;
import com.zhihuianxin.xyaxf.app.utils.NetUtils;

import java.util.HashMap;
import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Vincent on 2016/11/9.
 */

public class MeModifyPwdPresenter implements IMeModifyPwdContract.IMeModifyPwdPresenter{
    private Context mContext;
    private IMeModifyPwdContract.IMeModifyPwdView mView;

    public MeModifyPwdPresenter(Context context,IMeModifyPwdContract.IMeModifyPwdView view){
        mContext = context;
        mView = view;
        view.setPresenter(this);
    }

    @Override
    public void modifyPwd(String password, String new_password) {
        mView.loadStart();
        Map<String,Object> map = new HashMap<>();
        map.put("password", password);
        map.put("new_password", new_password);
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        MeService meService = ApiFactory.getFactory().create(MeService.class);
        meService.modifyPwd(NetUtils.getRequestParams(mContext,map), NetUtils.getSign(NetUtils.getRequestParams(mContext,map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(mContext,true,mView) {

                    @Override
                    public void onNext(Object o) {
                        mView.modifySuccess();
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
    public void resetPwdByVerInfo(String mobile, String new_password, String attribute_code) {

    }

    @Override
    public void subscribe() {

    }

    @Override
    public void unsubscribe() {

    }
}
