package com.zhihuianxin.xyaxf.app.ecard.bill;//package com.zhihuianxin.xyaxf.app.ecard.bill;
//
//import android.content.Context;
//import android.util.Log;
//
//import com.zhihuianxin.xyaxf.app.AppConstant;
//import com.axinfu.modellib.service.EcardService;
//import com.axinfu.modellib.thrift.base.BaseResponse;
//import com.axinfu.modellib.thrift.ecard.ECardChargeRecord;
//import com.google.gson.Gson;
//import com.zhihuianxin.xyaxf.app.base.axutil.BaseSchedulerProvider;
//import com.zhihuianxin.xyaxf.app.ApiFactory;
//import com.zhihuianxin.xyaxf.app.BaseSubscriber;
//import com.zhihuianxin.xyaxf.app.RetrofitFactory;
//import com.zhihuianxin.xyaxf.app.utils.NetUtils;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import io.realm.RealmObject;
//import rx.android.schedulers.AndroidSchedulers;
//import rx.schedulers.Schedulers;
//import rx.subscriptions.CompositeSubscription;
//
///**
// * Created by zcrpro on 2016/11/9.
// */
//public class EcardBillPresenter implements EcardBillContract.EcardBillPresenter {
//
//
//    private EcardBillContract.EcardBillView mView;
//    private Context mContext;
//    private CompositeSubscription mSubscriptions;
//
//    public static class EcardBillResponse extends RealmObject {
//        public BaseResponse resp;
//        public List<ECardChargeRecord> records;
//    }
//
//    public EcardBillPresenter(EcardBillContract.EcardBillView mView, Context mContext, BaseSchedulerProvider mSchedulerProvider) {
//        this.mView = mView;
//        this.mContext = mContext;
//        mView.setPresenter(this);
//        mSubscriptions = new CompositeSubscription();
//    }
//
//
//    @Override
//    public void loadEccrdBillData(String start_date, String end_date, int page_index) {
//        RetrofitFactory.setBaseUrl(AppConstant.URL);
//        Map<String, Object> map = new HashMap<>();
//        map.put("start_date", start_date);
//        map.put("end_date", end_date);
//        map.put("page_index", page_index);
//        map.put("page_size", 1000);
//        EcardService ecardService = ApiFactory.getFactory().create(EcardService.class);
//        ecardService.getChargeRecords(NetUtils.getRequestParams(mContext, map), NetUtils.getSign(NetUtils.getRequestParams(mContext, map)))
//                .subscribeOn(Schedulers.newThread())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new BaseSubscriber<Object>(mContext,false,mView) {
//
//                    @Override
//                    public void onNext(Object o) {
//                        EcardBillResponse ecardBillResponse = new Gson().fromJson(o.toString(), EcardBillResponse.class);
//                        if (AppConstant.DEBUG)
//                            Log.d("EcardBillPresenter", ecardBillResponse.records.toString());
//                        mView.ecardBillSuccess(ecardBillResponse.records);
//                    }
//                });
//    }
//
//    @Override
//    public void subscribe() {
//
//    }
//
//    @Override
//    public void unsubscribe() {
//        mSubscriptions.clear();
//    }
//}
