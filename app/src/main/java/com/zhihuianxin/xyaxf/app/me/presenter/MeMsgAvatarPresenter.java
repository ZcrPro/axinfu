package com.zhihuianxin.xyaxf.app.me.presenter;

import android.content.Context;

import com.zhihuianxin.xyaxf.app.AppConstant;
import modellib.service.CustomerService;
import modellib.service.MeService;
import modellib.thrift.customer.CustomerBaseInfo;
import com.google.gson.Gson;
import com.zhihuianxin.xyaxf.app.ApiFactory;
import com.zhihuianxin.xyaxf.app.BaseSubscriber;
import com.zhihuianxin.xyaxf.app.RetrofitFactory;
import com.zhihuianxin.xyaxf.app.me.contract.IMeMsgAvatarContract;
import com.zhihuianxin.xyaxf.app.unionqr_pay.presenter.UnionPayPwdPresenter;
import com.zhihuianxin.xyaxf.app.utils.NetUtils;

import java.util.HashMap;
import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Vincent on 2016/11/15.
 */

public class MeMsgAvatarPresenter implements IMeMsgAvatarContract.IMeMsgAvatarPresenter{
    private Context mContext;
    private IMeMsgAvatarContract.IMeMsgAvatarView mView;

    public MeMsgAvatarPresenter(Context context,IMeMsgAvatarContract.IMeMsgAvatarView view){
        mContext = context;
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void getQiNiuAccess(String purpose, String ext, int file_count) {
        RetrofitFactory.setBaseUrl(AppConstant.URL);

        Map<String,Object> map = new HashMap<>();
        map.put("purpose",purpose);
        map.put("ext",ext);
        map.put("file_number",file_count);
        MeService meService = ApiFactory.getFactory().create(MeService.class);
        meService.getQiNiuAccess(NetUtils.getRequestParams(mContext,map),NetUtils.getSign(NetUtils.getRequestParams(mContext,map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(mContext,true,mView) {

                    @Override
                    public void onNext(Object o) {
                        MeFeedBackPresenter.GetQiNiuAccess response = new Gson().fromJson(o.toString(),MeFeedBackPresenter.GetQiNiuAccess.class);
                        mView.getQiNiuAccessSuccess(response.accesses);

                    }
                });
    }

    @Override
    public void modifyCustomerBaseInfo(CustomerBaseInfo baseInfo) {
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
                        MeMsgPresenter.UpdateBaseInfoResponse response = new Gson().fromJson(o.toString(),MeMsgPresenter.UpdateBaseInfoResponse.class);
                        mView.modifyBaseInfoSuccess(response.customer);
                    }
                });
    }

    @Override
    public void getRealName() {
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String,Object> map = new HashMap<>();
        CustomerService meService = ApiFactory.getFactory().create(CustomerService.class);
        meService.getRealNameQR(NetUtils.getRequestParams(mContext,map),NetUtils.getSign(NetUtils.getRequestParams(mContext,map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(mContext,true,mView) {

                    @Override
                    public void onNext(Object o) {
                        UnionPayPwdPresenter.getRealNameResponse response = new Gson().fromJson(o.toString(),UnionPayPwdPresenter.getRealNameResponse.class);
                        mView.getRealNameResult(response.realname);
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
