package com.zhihuianxin.xyaxf.app.verification;

import android.content.Context;

import com.zhihuianxin.xyaxf.app.AppConstant;
import modellib.service.CustomerService;
import modellib.thrift.base.BaseResponse;
import com.google.gson.Gson;
import com.zhihuianxin.xyaxf.app.ApiFactory;
import com.zhihuianxin.xyaxf.app.BaseSubscriber;
import com.zhihuianxin.xyaxf.app.RetrofitFactory;
import com.zhihuianxin.xyaxf.app.utils.NetUtils;

import java.util.HashMap;
import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Vincent on 2018/1/12.
 */

public class LoginVerityLoginPwdPresenter implements ILoginVerityLoginPasswordContract.ILoginVerityLoginPasswordPresenter {
    private Context mContext;
    private ILoginVerityLoginPasswordContract.ILoginVerityLoginPasswordView mView;

    public LoginVerityLoginPwdPresenter(Context context, ILoginVerityLoginPasswordContract.ILoginVerityLoginPasswordView view){
        mContext = context;
        mView = view;
        mView.setPresenter(this);
    }

    class VerityLoginPwdResponse {
        public BaseResponse resp;
        public String verify_status;
    }

    @Override
    public void verityLoginPwd(String login_password) {
        RetrofitFactory.setBaseUrl(AppConstant.URL);

        Map<String,Object> map = new HashMap<>();
        map.put("login_password",login_password);
        CustomerService loginService = ApiFactory.getFactory().create(CustomerService.class);
        loginService.verifyLoginPwd(NetUtils.getRequestParams(mContext,map), NetUtils.getSign(NetUtils.getRequestParams(mContext,map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(mContext,true,mView) {
                    @Override
                    public void onNext(Object o) {
                        VerityLoginPwdResponse baseResponse = new Gson().fromJson(o.toString(),VerityLoginPwdResponse.class);
                        mView.verityLoginPwdResult();
                    }
                });

    }
}
