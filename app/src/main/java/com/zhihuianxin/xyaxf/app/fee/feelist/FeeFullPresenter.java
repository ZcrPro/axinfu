package com.zhihuianxin.xyaxf.app.fee.feelist;

import android.content.Context;
import android.content.res.Resources;

import com.zhihuianxin.xyaxf.app.AppConstant;
import modellib.service.FeeService;
import modellib.thrift.base.BaseResponse;
import modellib.thrift.fee.FeeRecord;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.zhihuianxin.xyaxf.app.base.axutil.BaseSchedulerProvider;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.ApiFactory;
import com.zhihuianxin.xyaxf.app.BaseSubscriber;
import com.zhihuianxin.xyaxf.app.RetrofitFactory;
import com.zhihuianxin.xyaxf.app.utils.NetUtils;
import com.zhihuianxin.xyaxf.test.TestDataConfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.RealmObject;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by zcrpro on 2016/11/13.
 */
public class FeeFullPresenter implements FeeFullContract.FeeFullPresenter {

    private FeeFullContract.FeeFullView mView;
    private Context mContext;
    private CompositeSubscription mSubscriptions;

    public static class FeeFullResponse extends RealmObject {
        public BaseResponse resp;
        public List<FeeRecord> fee_records;
    }

    public FeeFullPresenter(FeeFullContract.FeeFullView mView, Context mContext, BaseSchedulerProvider mSchedulerProvider) {
        this.mView = mView;
        this.mContext = mContext;
        mView.setPresenter(this);
        mSubscriptions = new CompositeSubscription();
    }

    @Override
    public void loadFeeRecordList(String start_date, String end_date, int page_index, int page_size, final boolean isLoadMore) {
            RetrofitFactory.setBaseUrl(AppConstant.URL);
            Map<String, Object> map = new HashMap<>();
            FeeService feeService = ApiFactory.getFactory().create(FeeService.class);
            map.put("start_date", start_date);
            map.put("end_date", end_date);
            map.put("page_index", page_index);
            map.put("page_size", page_size);
            feeService.getFeesRecord(NetUtils.getRequestParams(mContext, map), NetUtils.getSign(NetUtils.getRequestParams(mContext, map)))
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new BaseSubscriber<Object>(mContext,false,mView) {
                        @Override
                        public void onNext(Object o) {
                            FeeFullResponse feeFullResponse = new Gson().fromJson(o.toString(), FeeFullResponse.class);
                            mView.feeFullSuccess(feeFullResponse.fee_records,isLoadMore);
                        }
                    });
    }

    @Override
    public void loadOtherFeeRecordList(String name, String other_no, String start_date, String end_date, int page_index, int page_size, final boolean isLoadMore) {
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String, Object> map = new HashMap<>();
        FeeService feeService = ApiFactory.getFactory().create(FeeService.class);
        map.put("name", name);
        map.put("other_no", other_no);
        map.put("start_date", start_date);
        map.put("end_date", end_date);
        map.put("page_index", page_index);
        map.put("page_size", page_size);
        feeService.getOtherFeesRecord(NetUtils.getRequestParams(mContext, map), NetUtils.getSign(NetUtils.getRequestParams(mContext, map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(mContext,false,mView) {
                    @Override
                    public void onNext(Object o) {
                        FeeFullResponse feeFullResponse = new Gson().fromJson(o.toString(), FeeFullResponse.class);
                        mView.feeFullSuccess(feeFullResponse.fee_records,isLoadMore);
                    }
                });
    }

    @Override
    public void get_new_student_fee_records(String name, String id_card_no, String new_student_no, String start_date, String end_date, int page_index, int page_size, final boolean isLoadMore) {
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String, Object> map = new HashMap<>();
        FeeService feeService = ApiFactory.getFactory().create(FeeService.class);
        map.put("name", name);
        map.put("id_card_no", id_card_no);
        map.put("new_student_no", new_student_no);
        map.put("start_date", start_date);
        map.put("end_date", end_date);
        map.put("page_index", page_index);
        map.put("page_size", page_size);
        feeService.get_new_student_fee_records(NetUtils.getRequestParams(mContext, map), NetUtils.getSign(NetUtils.getRequestParams(mContext, map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(mContext,false,mView) {
                    @Override
                    public void onNext(Object o) {
                        FeeFullResponse feeFullResponse = new Gson().fromJson(o.toString(), FeeFullResponse.class);
                        mView.feeFullSuccess(feeFullResponse.fee_records,isLoadMore);
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
