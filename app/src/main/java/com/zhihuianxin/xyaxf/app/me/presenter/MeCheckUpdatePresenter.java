package com.zhihuianxin.xyaxf.app.me.presenter;

import android.content.Context;

import com.zhihuianxin.xyaxf.app.AppConstant;
import com.axinfu.modellib.service.CustomerService;
import com.axinfu.modellib.service.MeService;
import com.axinfu.modellib.service.PaymentService;
import com.axinfu.modellib.thrift.app.PluginInfo;
import com.axinfu.modellib.thrift.app.Update;
import com.axinfu.modellib.thrift.base.BaseResponse;
import com.google.gson.Gson;
import com.zhihuianxin.xyaxf.app.ApiFactory;
import com.zhihuianxin.xyaxf.app.BaseSubscriber;
import com.zhihuianxin.xyaxf.app.RetrofitFactory;
import com.zhihuianxin.xyaxf.app.me.contract.IMeCheckUpdateContract;
import com.zhihuianxin.xyaxf.app.unionqr_pay.presenter.UnionPayPwdPresenter;
import com.zhihuianxin.xyaxf.app.unionqr_pay.presenter.UnionQrMainPresenter;
import com.zhihuianxin.xyaxf.app.utils.NetUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.realm.RealmObject;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Vincent on 2016/11/16.
 */

public class MeCheckUpdatePresenter implements IMeCheckUpdateContract.IMeCheckUpdatePresenter{
    private Context mContext;
    private IMeCheckUpdateContract.IMeCheckUpdateView mView;

    public MeCheckUpdatePresenter(Context context,IMeCheckUpdateContract.IMeCheckUpdateView view){
        mContext = context;
        mView = view;
        mView.setPresenter(this);
    }

    public class GetCheckUpdateResponse extends RealmObject{
        public BaseResponse resp;
        public Update app_update;
        public ArrayList<Update> plugin_updates;
    }

    @Override
    public void checkUpdate(ArrayList<PluginInfo> pluginInfos) {
        RetrofitFactory.setBaseUrl(AppConstant.URL);

        Map<String,Object> map = new HashMap<>();
        map.put("pluginInfos",pluginInfos);
        MeService meService = ApiFactory.getFactory().create(MeService.class);
        meService.getCheckUpdate(NetUtils.getRequestParams(mContext,map),NetUtils.getSign(NetUtils.getRequestParams(mContext,map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(mContext, true,mView) {
                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.loadError(e.getMessage());
                    }

                    @Override
                    public void onNext(Object o) {
                        GetCheckUpdateResponse response = new Gson().fromJson(o.toString(),GetCheckUpdateResponse.class);
                        mView.checkUpdateSuccess(response.app_update,response.plugin_updates);
                    }
                });

    }

    @Override
    public void JudgePayPwd() {
        RetrofitFactory.setBaseUrl(AppConstant.URL);

        Map<String,Object> map = new HashMap<>();
        PaymentService meService = ApiFactory.getFactory().create(PaymentService.class);
        meService.getPaymentConfig(NetUtils.getRequestParams(mContext,map),NetUtils.getSign(NetUtils.getRequestParams(mContext,map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(mContext,true,mView) {

                    @Override
                    public void onNext(Object o) {
                        UnionQrMainPresenter.GetPaymentConfigResponse response = new Gson().fromJson(o.toString(),UnionQrMainPresenter.GetPaymentConfigResponse.class);
                        mView.judgePayPwdResult(response.config);
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
    public void subscribe() {}
    @Override
    public void unsubscribe() {}
}
