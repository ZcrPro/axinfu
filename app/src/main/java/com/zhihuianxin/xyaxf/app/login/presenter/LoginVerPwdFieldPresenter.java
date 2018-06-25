package com.zhihuianxin.xyaxf.app.login.presenter;

import android.content.Context;

import com.zhihuianxin.xyaxf.app.AppConstant;
import com.axinfu.modellib.service.CustomerService;
import com.google.gson.Gson;
import com.zhihuianxin.xyaxf.app.ApiFactory;
import com.zhihuianxin.xyaxf.app.BaseSubscriber;
import com.zhihuianxin.xyaxf.app.RetrofitFactory;
import com.zhihuianxin.xyaxf.app.login.contract.ILoginVerPwdFieldContract;
import com.zhihuianxin.xyaxf.app.utils.NetUtils;

import java.util.HashMap;
import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Vincent on 2017/12/21.
 */

public class LoginVerPwdFieldPresenter implements ILoginVerPwdFieldContract.ILoginVerPwdFieldPresenter {
    private Context mContext;
    private ILoginVerPwdFieldContract.ILoginVerPwdFieldView mView;

    public LoginVerPwdFieldPresenter(Context context,ILoginVerPwdFieldContract.ILoginVerPwdFieldView view){
        mContext = context;
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void verpwdField(String mobile, String name, String student_no, String id_card_no, String bank_card_no) {
        Map<String,Object> map = new HashMap<>();
        map.put("mobile",mobile);
        map.put("name",name);
        map.put("student_no",student_no);
        map.put("id_card_no",id_card_no);
        map.put("bank_card_no",bank_card_no);
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        CustomerService loginService = ApiFactory.getFactory().create(CustomerService.class);
        loginService.verfieldMsg(NetUtils.getRequestParams(mContext,map), NetUtils.getSign(NetUtils.getRequestParams(mContext,map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(mContext,true,mView) {
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        if(e.getMessage().contains(AppConstant.MODIFY_PWD_FIELD_ERROR_TIME)){
                            mView.verpwdFieldResult(e.getMessage().split("\\(")[0]);
                            // 要弹框
                        } else {
                            // 底层Toast
                        }
                    }

                    @Override
                    public void onNext(Object o) {
                        mView.verpwdFieldResult("");
                    }
                });
    }

    @Override
    public void setPwdByField(String mobile, String new_password, String attribute_code) {
        Map<String,Object> map = new HashMap<>();
        map.put("mobile",mobile);
        map.put("new_password",new_password);
        map.put("attribute_code",attribute_code);
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        CustomerService customerService = ApiFactory.getFactory().create(CustomerService.class);
        customerService.resetPwdByField(NetUtils.getRequestParams(mContext,map), NetUtils.getSign(NetUtils.getRequestParams(mContext,map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(mContext,true,mView) {

                    @Override
                    public void onNext(Object o) {
                        LoginSetPwdOrRegistPresenter.LoginAndSetPasswordResponse response =
                                new Gson().fromJson(o.toString(),LoginSetPwdOrRegistPresenter.LoginAndSetPasswordResponse.class);
                        mView.setPwdByFieldResult(response.customer,response.session);
                    }
                });

    }

    @Override
    public void subscribe() {}
    @Override
    public void unsubscribe() {}
}
