package com.zhihuianxin.xyaxf.app.login.presenter;

import android.content.Context;

import com.zhihuianxin.xyaxf.app.AppConstant;
import modellib.service.CustomerService;
import com.zhihuianxin.xyaxf.app.ApiFactory;
import com.zhihuianxin.xyaxf.app.BaseSubscriber;
import com.zhihuianxin.xyaxf.app.RetrofitFactory;
import com.zhihuianxin.xyaxf.app.login.contract.ILoginCancelAccountContrat;
import com.zhihuianxin.xyaxf.app.utils.NetUtils;

import java.util.HashMap;
import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Vincent on 2017/12/21.
 */

public class LoginCancelAccountPresenter implements ILoginCancelAccountContrat.ILoginCancelAccountPresenter {
    private Context mContext;
    private ILoginCancelAccountContrat.ILoginCancelAccountView mView;


    public LoginCancelAccountPresenter(Context context,ILoginCancelAccountContrat.ILoginCancelAccountView view){
        mContext = context;
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void cancelAccount(String pwd) {
        Map<String,Object> map = new HashMap<>();
        map.put("password", pwd);
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        CustomerService loginService = ApiFactory.getFactory().create(CustomerService.class);
        loginService.cancelAccount(NetUtils.getRequestParams(mContext,map), NetUtils.getSign(NetUtils.getRequestParams(mContext,map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(mContext,true,mView) {
                    @Override
                    public void onNext(Object o) {
                        mView.cancelAccountResult();
                    }
                });

    }
}
