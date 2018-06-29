package com.zhihuianxin.xyaxf.app.me.presenter;

import android.content.Context;

import com.zhihuianxin.xyaxf.app.AppConstant;
import modellib.service.MeService;
import modellib.service.UPQRService;
import modellib.thrift.base.BaseResponse;
import modellib.thrift.fee.PaymentRecord;
import modellib.thrift.unqr.UPQRPayRecord;
import com.google.gson.Gson;
import com.xyaxf.axpay.modle.PayFor;
import com.zhihuianxin.xyaxf.app.base.axutil.BaseSchedulerProvider;
import com.zhihuianxin.xyaxf.app.ApiFactory;
import com.zhihuianxin.xyaxf.app.BaseSubscriber;
import com.zhihuianxin.xyaxf.app.base.axutil.BaseSchedulerProvider;
import com.zhihuianxin.xyaxf.app.me.contract.IMePayListContract;
import com.zhihuianxin.xyaxf.app.utils.NetUtils;

import java.util.HashMap;
import java.util.Map;

import io.realm.RealmList;
import io.realm.RealmObject;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Vincent on 2016/10/24.
 */

public class MePayListPresenter implements IMePayListContract.IMePayListPresenter{
    private Context mContext;
    private IMePayListContract.IMePayListView mView;
    private BaseSchedulerProvider mProvider;

    class GetPayListRecords extends RealmObject{
        public BaseResponse resp;
        public RealmList<PaymentRecord> records;
    }

    class GetUpqrSwepRecordResponse extends RealmObject{
        public BaseResponse resp;
        public RealmList<UPQRPayRecord> records;
    }

    public MePayListPresenter (Context context, IMePayListContract.IMePayListView view, BaseSchedulerProvider provider){
        mContext = context;
        mView = view;
        mProvider = provider;
        mView.setPresenter(this);
    }

    @Override
    public void loadPayClosedList(String start_date, String end_date, String page_index, String page_size) {
        Map<String,Object> map = new HashMap<>();
        map.put("start_date",start_date);
        map.put("end_date",end_date);
        map.put("page_index",page_index);
        map.put("page_size",page_size);

        MeService meService = ApiFactory.getFactory().create(MeService.class);
        meService.getClosedPaymentRecords(NetUtils.getRequestParams(mContext,map),NetUtils.getSign(NetUtils.getRequestParams(mContext,map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(mContext,false,mView) {

                    @Override
                    public void onNext(Object o) {
                        GetPayListRecords records = new Gson().fromJson(o.toString(),GetPayListRecords.class);
                        if (records.resp.resp_code.equals(AppConstant.SUCCESS)){
                            mView.setPayClosedList(records.records);
                        }
                    }
                });
    }

    @Override
    public void loadPayList(String start_date, String end_date, String page_index, String page_size, PayFor pay_for) {
        Map<String,Object> map = new HashMap<>();
        map.put("start_date",start_date);
        map.put("end_date",end_date);
        map.put("page_index",page_index);
        map.put("page_size",page_size);
        if(pay_for.equals(PayFor.ALL)){
            map.put("query_type","");
        } else if(pay_for.equals(PayFor.ScanPay)){
            map.put("query_type","B2C");
        } else if(pay_for.equals(PayFor.UPQRPay)){
            map.put("query_type","C2B");
        } else{
            map.put("query_type",pay_for.name());
        }


        MeService meService = ApiFactory.getFactory().create(MeService.class);
        meService.getPaymentRecords(NetUtils.getRequestParams(mContext,map),NetUtils.getSign(NetUtils.getRequestParams(mContext,map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(mContext,false,mView) {

                    @Override
                    public void onNext(Object o) {
                        GetPayListRecords records = new Gson().fromJson(o.toString(),GetPayListRecords.class);
                        if (records.resp.resp_code.equals(AppConstant.SUCCESS)){
                            mView.setPayList(records.records);
                        }
                    }
                });

    }

    @Override
    public void loadUnionSwepPayList(String start_date, String end_date, String page_index, String page_size) {
        Map<String,Object> map = new HashMap<>();
        map.put("start_date",start_date);
        map.put("end_date",end_date);
        map.put("page_index",page_index);
        map.put("page_size",page_size);

        UPQRService meService = ApiFactory.getFactory().create(UPQRService.class);
        meService.getUpqrSwepRecord(NetUtils.getRequestParams(mContext,map),NetUtils.getSign(NetUtils.getRequestParams(mContext,map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(mContext,false,mView) {

                    @Override
                    public void onNext(Object o) {
                        GetUpqrSwepRecordResponse records = new Gson().fromJson(o.toString(),GetUpqrSwepRecordResponse.class);
                        if (records.resp.resp_code.equals(AppConstant.SUCCESS)){
                            mView.setUnionSwepPayList(records.records);
                        }
                    }
                });

    }
    @Override
    public void subscribe() {}
    @Override
    public void unsubscribe() {}
}
