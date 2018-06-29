package com.zhihuianxin.xyaxf.app.me.presenter;

import android.content.Context;

import com.zhihuianxin.xyaxf.app.AppConstant;
import com.axinfu.modellib.service.CustomerService;
import com.axinfu.modellib.service.MeService;
import com.axinfu.modellib.thrift.base.BaseResponse;
import com.axinfu.modellib.thrift.ecard.ECard;
import com.axinfu.modellib.thrift.ecard.ECardAccount;
import com.axinfu.modellib.thrift.fee.FeeAccount;
import com.google.gson.Gson;
import com.zhihuianxin.xyaxf.app.base.axutil.Util;
import com.zhihuianxin.xyaxf.app.ApiFactory;
import com.zhihuianxin.xyaxf.app.BaseSubscriber;
import com.zhihuianxin.xyaxf.app.RetrofitFactory;
import com.zhihuianxin.xyaxf.app.login.presenter.LoginHasPwdPresenter;
import com.zhihuianxin.xyaxf.app.me.contract.IMeStuMsgContract;
import com.zhihuianxin.xyaxf.app.utils.NetUtils;

import java.util.HashMap;
import java.util.Map;

import io.realm.RealmObject;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Vincent on 2016/11/10.
 */

public class MeStuMsgPresenter implements IMeStuMsgContract.IMeStuMsgPresenter {
    private Context mContext;
    private IMeStuMsgContract.IMeStuMsgView mView;

    public static class UpdateEcardAccountResponse extends RealmObject {
        public BaseResponse resp;
        public ECardAccount account;
        public ECard ecard;
    }

    public static class UpdateFeeAccountResponse extends RealmObject {
        public BaseResponse resp;
        public FeeAccount account;
    }

    public MeStuMsgPresenter(Context context, IMeStuMsgContract.IMeStuMsgView view) {
        mContext = context;
        mView = view;
        view.setPresenter(this);
    }

    @Override
    public void modifyECardAccount(String account_no, String ecard_password, String login_password) {
        RetrofitFactory.setBaseUrl(AppConstant.URL);

        Map<String, Object> map = new HashMap<>();
        map.put("account_no", account_no);
        if(!Util.isEmpty(ecard_password)){
            map.put("ecard_password", ecard_password);
        }
        map.put("login_password", login_password);
        MeService meService = ApiFactory.getFactory().create(MeService.class);
        meService.updateECardAccount(NetUtils.getRequestParams(mContext, map), NetUtils.getSign(NetUtils.getRequestParams(mContext, map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(mContext,true,mView) {

                    @Override
                    public void onNext(Object o) {
                        UpdateEcardAccountResponse response = new Gson().fromJson(o.toString(), UpdateEcardAccountResponse.class);
                        mView.modifyECardAccountSuccess(response.account, response.ecard);
                    }
                });
    }

    @Override
    public void modifyStuNo(String student_no, String id_card_no, String login_password) {
        RetrofitFactory.setBaseUrl(AppConstant.URL);

        Map<String, Object> map = new HashMap<>();
        map.put("student_no", student_no);
        map.put("id_card_no", id_card_no);
        map.put("login_password", login_password);
        MeService meService = ApiFactory.getFactory().create(MeService.class);
        meService.updateFeeAccount(NetUtils.getRequestParams(mContext, map), NetUtils.getSign(NetUtils.getRequestParams(mContext, map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(mContext,true,mView) {

                    @Override
                    public void onNext(Object o) {
                        UpdateFeeAccountResponse response = new Gson().fromJson(o.toString(), UpdateFeeAccountResponse.class);
                        mView.modifyStuNoSuccess(response.account);
                    }
                });

    }

    @Override
    public void modifyID(String student_no, String id_card_no, String login_password) {
        RetrofitFactory.setBaseUrl(AppConstant.URL);

        Map<String, Object> map = new HashMap<>();
        map.put("student_no", student_no);
        map.put("id_card_no", id_card_no);
        map.put("login_password", login_password);
        MeService meService = ApiFactory.getFactory().create(MeService.class);
        meService.updateFeeAccount(NetUtils.getRequestParams(mContext, map), NetUtils.getSign(NetUtils.getRequestParams(mContext, map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(mContext,true,mView) {

                    @Override
                    public void onNext(Object o) {
                        UpdateFeeAccountResponse response = new Gson().fromJson(o.toString(), UpdateFeeAccountResponse.class);
                        mView.modifyIDSuccess(response.account);
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
