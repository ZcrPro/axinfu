package com.zhihuianxin.xyaxf.app.fee.feelist;

import android.content.Context;
import android.content.res.Resources;

import com.zhihuianxin.xyaxf.app.AppConstant;
import modellib.service.FeeService;
import modellib.thrift.base.BaseResponse;
import modellib.thrift.fee.Fee;
import modellib.thrift.fee.SchoolRoll;
import modellib.thrift.fee.SubFeeDeduction;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.zhihuianxin.xyaxf.app.base.axutil.BaseSchedulerProvider;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.ApiFactory;
import com.zhihuianxin.xyaxf.app.BaseSubscriber;
import com.zhihuianxin.xyaxf.app.RetrofitFactory;
import com.zhihuianxin.xyaxf.app.utils.NetUtils;
import com.zhihuianxin.xyaxf.test.TestDataConfig;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.RealmObject;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by zcrpro on 2016/11/10.
 */
public class FeeNotFullPresenter implements FeeNotFullContract.FeeNotFullPresenter {

    private FeeNotFullContract.FeeNotFullView mView;
    private Context mContext;
    private CompositeSubscription mSubscriptions;

    public static class FeeNotFullResponse extends RealmObject implements Serializable {
        public BaseResponse resp;
        public List<Fee> fees;
        public List<SubFeeDeduction> deductible_fees;
        public SchoolRoll school_roll;
        public String pay_limit_hint;
    }

    public FeeNotFullPresenter(FeeNotFullContract.FeeNotFullView mView, Context mContext, BaseSchedulerProvider mSchedulerProvider) {
        this.mView = mView;
        this.mContext = mContext;
        mView.setPresenter(this);
        mSubscriptions = new CompositeSubscription();
    }

    @Override
    public void loadFeeList() {
            RetrofitFactory.setBaseUrl(AppConstant.URL);
            Map<String, Object> map = new HashMap<>();
            FeeService feeService = ApiFactory.getFactory().create(FeeService.class);
            feeService.getFees(NetUtils.getRequestParams(mContext, map), NetUtils.getSign(NetUtils.getRequestParams(mContext, map)))
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new BaseSubscriber<Object>(mContext,false,mView) {

                        @Override
                        public void onNext(Object o) {
                            FeeNotFullResponse feeNotFullResponse = new Gson().fromJson(o.toString(), FeeNotFullResponse.class);
                            if (feeNotFullResponse.resp.resp_code.equals(AppConstant.SUCCESS))
                            mView.feeNotFullSuccess(feeNotFullResponse.fees,feeNotFullResponse.school_roll,feeNotFullResponse.deductible_fees,feeNotFullResponse.pay_limit_hint);
                        }
                    });
    }

    @Override
    public void loadOtherFeeList(String name, String other_no) {
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String, Object> map = new HashMap<>();
        map.put("name",name);
        map.put("other_no",other_no);
        FeeService feeService = ApiFactory.getFactory().create(FeeService.class);
        feeService.getOtherFees(NetUtils.getRequestParams(mContext, map), NetUtils.getSign(NetUtils.getRequestParams(mContext, map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(mContext,false,mView) {

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        if(e instanceof Exception){
                            mView.loadError("获取缴费项失败！");
                        } else {
                            mView.loadError(e.getMessage());
                        }
                    }

                    @Override
                    public void onNext(Object o) {
                        FeeNotFullResponse feeNotFullResponse = new Gson().fromJson(o.toString(), FeeNotFullResponse.class);
                        mView.feeNotFullSuccess(feeNotFullResponse.fees,feeNotFullResponse.school_roll,feeNotFullResponse.deductible_fees,feeNotFullResponse.pay_limit_hint);
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
