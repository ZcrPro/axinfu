package com.zhihuianxin.xyaxf.app.fee.check;

import android.content.Context;

import com.zhihuianxin.xyaxf.app.AppConstant;
import com.axinfu.modellib.service.FeeService;
import com.axinfu.modellib.thrift.base.BaseResponse;
import com.axinfu.modellib.thrift.fee.FeeAccount;
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
 * Created by zcrpro on 2016/11/10.
 */
public class FeeCheckPresenter implements FeeCheckContract.PaymentCheckPresenter {

    private FeeCheckContract.PaymentCheckView mView;
    private Context mContext;
    private CompositeSubscription mSubscriptions;
    private BaseSchedulerProvider mSchedulerProvider;

    public static class PaymentCheckResponse extends RealmObject {
        public BaseResponse resp;
        public FeeAccount account;
    }

    public FeeCheckPresenter(FeeCheckContract.PaymentCheckView mView, Context mContext, BaseSchedulerProvider mSchedulerProvider) {
        this.mView = mView;
        this.mContext = mContext;
        this.mSchedulerProvider = mSchedulerProvider;
        mView.setPresenter(this);
        mSubscriptions = new CompositeSubscription();
    }

    @Override
    public void openPaymentAccount(String name, String student_no, String id_card_no) {
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("student_no", student_no);
        map.put("id_card_no", id_card_no);
        FeeService feeService = ApiFactory.getFactory().create(FeeService.class);
        feeService.openFeeAccount(NetUtils.getRequestParams(mContext, map), NetUtils.getSign(NetUtils.getRequestParams(mContext, map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(mContext,true,mView) {
                    @Override
                    public void onNext(Object o) {
                        PaymentCheckResponse paymentCheckResponse = new Gson().fromJson(o.toString(), PaymentCheckResponse.class);
                        mView.paymentCheckSuccess(paymentCheckResponse.account);
                    }
                });
    }

    @Override
    public void openPaymentAccount(String name, String id_card_no) {
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("id_card_no", id_card_no);
        map.put("student_no", "");
        FeeService feeService = ApiFactory.getFactory().create(FeeService.class);
        feeService.openFeeAccount(NetUtils.getRequestParams(mContext, map), NetUtils.getSign(NetUtils.getRequestParams(mContext, map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(mContext,true,mView) {
                    @Override
                    public void onNext(Object o) {
                        PaymentCheckResponse paymentCheckResponse = new Gson().fromJson(o.toString(), PaymentCheckResponse.class);
                        mView.paymentCheckSuccess(paymentCheckResponse.account);
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
