package com.zhihuianxin.xyaxf.app.unionqr_pay.presenter;

import android.content.Context;

import com.zhihuianxin.xyaxf.app.AppConstant;
import modellib.service.LoginService;
import modellib.service.PaymentService;
import com.google.gson.Gson;
import com.zhihuianxin.xyaxf.app.ApiFactory;
import com.zhihuianxin.xyaxf.app.BaseSubscriber;
import com.zhihuianxin.xyaxf.app.RetrofitFactory;
import com.zhihuianxin.xyaxf.app.login.presenter.LoginSetPwdOrRegistPresenter;
import com.zhihuianxin.xyaxf.app.unionqr_pay.contract.IunionClearPwdContract;
import com.zhihuianxin.xyaxf.app.utils.NetUtils;

import java.util.HashMap;
import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Vincent on 2017/11/16.
 */

public class UnionClearPayPwdPresenter implements IunionClearPwdContract.IunionClearPwdPresenter{

    private Context mContext;
    private IunionClearPwdContract.IunionClearPwd mView;

    public UnionClearPayPwdPresenter(Context context, IunionClearPwdContract.IunionClearPwd view){
        mContext = context;
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void getVerCode(String verify_for,String mobile) {
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
                            LoginSetPwdOrRegistPresenter.GetVerCodeResponse vercode =
                                    new Gson().fromJson(o.toString(),LoginSetPwdOrRegistPresenter.GetVerCodeResponse.class);
                            mView.getVerCodeResult(vercode.code);
                    }
                });
    }

    @Override
    public void setPayPwd(String payment_password) {

    }

    @Override
    public void slearPayPwd(String secure_code) {
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String,Object> map = new HashMap<>();
        map.put("secure_code",secure_code);
        PaymentService pa = ApiFactory.getFactory().create(PaymentService.class);
        pa.clearPaymentPwd(NetUtils.getRequestParams(mContext,map),NetUtils.getSign(NetUtils.getRequestParams(mContext,map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(mContext,true,mView) {

                    @Override
                    public void onNext(Object o) {
                        mView.slearPayPwdResult();
                    }
                });
    }
}
