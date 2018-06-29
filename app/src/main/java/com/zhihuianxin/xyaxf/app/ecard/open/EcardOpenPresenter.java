package com.zhihuianxin.xyaxf.app.ecard.open;

import android.content.Context;

import com.zhihuianxin.xyaxf.app.AppConstant;
import modellib.service.EcardService;
import modellib.thrift.base.BaseResponse;
import modellib.thrift.ecard.ECard;
import modellib.thrift.ecard.ECardAccount;
import com.google.gson.Gson;
import com.zhihuianxin.xyaxf.app.base.axutil.BaseSchedulerProvider;
import com.zhihuianxin.xyaxf.app.ApiFactory;
import com.zhihuianxin.xyaxf.app.BaseSubscriber;
import com.zhihuianxin.xyaxf.app.RetrofitFactory;
import com.zhihuianxin.xyaxf.app.utils.NetUtils;

import java.util.HashMap;
import java.util.Map;

import io.realm.RealmObject;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by zcrpro on 2016/11/14.
 */
public class EcardOpenPresenter implements EcardOpenContract.EcardOpenPresenter {

    private EcardOpenContract.EcardOpenView mView;
    private Context mContext;
    private CompositeSubscription mSubscriptions;

    public static class EcardResponse extends RealmObject {
        public BaseResponse resp;
        public ECardAccount account;
        public ECard ecard;
    }

    public EcardOpenPresenter(EcardOpenContract.EcardOpenView mView, Context mContext, BaseSchedulerProvider mSchedulerProvider) {
        this.mView = mView;
        this.mContext = mContext;
        mView.setPresenter(this);
        mSubscriptions = new CompositeSubscription();
    }

    @Override
    public void openEcard(String name, String account_no, String password) {
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("account_no", account_no);
        map.put("password", password);
        EcardService ecardService = ApiFactory.getFactory().create(EcardService.class);
        ecardService.openEcard(NetUtils.getRequestParams(mContext, map), NetUtils.getSign(NetUtils.getRequestParams(mContext, map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(mContext,true,mView) {
                    @Override
                    public void onNext(Object o) {
                        final EcardResponse ecardResponse = new Gson().fromJson(o.toString(), EcardResponse.class);
                        mView.ecardOpenSuccess(ecardResponse.account, ecardResponse.ecard);
                    }
                });
    }

    @Override
    public void subscribe() {

    }

    @Override
    public void unsubscribe() {
        mSubscriptions.clear();
    }
}
