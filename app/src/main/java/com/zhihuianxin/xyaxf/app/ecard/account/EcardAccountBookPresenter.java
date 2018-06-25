package com.zhihuianxin.xyaxf.app.ecard.account;

import android.content.Context;

import com.zhihuianxin.xyaxf.app.AppConstant;
import com.axinfu.modellib.service.EcardService;
import com.axinfu.modellib.thrift.base.BaseResponse;
import com.axinfu.modellib.thrift.ecard.ECardRecord;
import com.google.gson.Gson;
import com.zhihuianxin.axutil.BaseSchedulerProvider;
import com.zhihuianxin.xyaxf.app.ApiFactory;
import com.zhihuianxin.xyaxf.app.BaseSubscriber;
import com.zhihuianxin.xyaxf.app.RetrofitFactory;
import com.zhihuianxin.xyaxf.app.utils.NetUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.RealmObject;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by zcrpro on 2016/11/12.
 */
public class EcardAccountBookPresenter implements EcardAccountBookContract.EcardAccountBookPresenter {

    private EcardAccountBookContract.EcardAccountBookView mView;
    private Context mContext;
    private CompositeSubscription mSubscriptions;

    public EcardAccountBookPresenter(EcardAccountBookContract.EcardAccountBookView mView, Context mContext, BaseSchedulerProvider mSchedulerProvider) {
        this.mView = mView;
        this.mContext = mContext;
        mView.setPresenter(this);
        mSubscriptions = new CompositeSubscription();
    }

    @Override
    public void loadEcardAccountBook(String start_date, String end_date, int page_index, int page_size, boolean refresh, final boolean isLoadMore) {
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String, Object> map = new HashMap<>();
        map.put("start_date", start_date);
        map.put("end_date", end_date);
        map.put("page_index", page_index);
        map.put("page_size", page_size);
        EcardService ecardService = ApiFactory.getFactory().create(EcardService.class);
        ecardService.getRecords(NetUtils.getRequestParams(mContext, map), NetUtils.getSign(NetUtils.getRequestParams(mContext, map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(mContext,!refresh,mView) {

                    @Override
                    public void onNext(Object o) {
                        RecordResponse recordResponse = new Gson().fromJson(o.toString(), RecordResponse.class);
                        mView.ecardAccountBookSuccess(recordResponse.records,isLoadMore);
                    }
                });
    }


    @Override
    public void loadEcardAccountBook(String start_date, String end_date, int page_index, int page_size, boolean refresh, final boolean isLoadMore,String password) {
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String, Object> map = new HashMap<>();
        map.put("start_date", start_date);
        map.put("end_date", end_date);
        map.put("page_index", page_index);
        map.put("page_size", page_size);
        map.put("password", password);
        EcardService ecardService = ApiFactory.getFactory().create(EcardService.class);
        ecardService.getRecords(NetUtils.getRequestParams(mContext, map), NetUtils.getSign(NetUtils.getRequestParams(mContext, map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(mContext,!refresh,mView) {

                    @Override
                    public void onNext(Object o) {
                        RecordResponse recordResponse = new Gson().fromJson(o.toString(), RecordResponse.class);
                        mView.ecardAccountBookSuccess(recordResponse.records,isLoadMore);
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


    public static class RecordResponse extends RealmObject {
        public BaseResponse resp;
        public List<ECardRecord> records;
    }
}
