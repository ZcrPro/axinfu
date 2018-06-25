package com.zhihuianxin.xyaxf.app.unionqr_pay.presenter;

import android.content.Context;

import com.zhihuianxin.xyaxf.app.AppConstant;
import com.axinfu.modellib.service.UPQRService;
import com.axinfu.modellib.thrift.base.BaseResponse;
import com.axinfu.modellib.thrift.unqr.UPQRPayRecord;
import com.google.gson.Gson;
import com.zhihuianxin.xyaxf.app.ApiFactory;
import com.zhihuianxin.xyaxf.app.BaseSubscriber;
import com.zhihuianxin.xyaxf.app.RetrofitFactory;
import com.zhihuianxin.xyaxf.app.unionqr_pay.contract.IunionGetUnionPayResultDetail;
import com.zhihuianxin.xyaxf.app.utils.NetUtils;

import java.util.HashMap;
import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Vincent on 2017/12/13.
 */

public class UnionGetUnionPayResultDetailPresenter implements IunionGetUnionPayResultDetail.IunionGetUnionPayResultPresenter{
    private Context mContext;
    private IunionGetUnionPayResultDetail.IunionGetUnionPayResultView mView;

    public UnionGetUnionPayResultDetailPresenter(Context context,IunionGetUnionPayResultDetail.IunionGetUnionPayResultView view){
        this.mContext = context;
        this.mView = view;
        mView.setPresenter(this);
    }

    class GetUnionPatResultDetail{
        public BaseResponse resp;
        public UPQRPayRecord pay_record_detail;
    }
    @Override
    public void getPayResultDeatil(String order_no) {
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String,Object> map = new HashMap<>();
        map.put("order_no",order_no);
        UPQRService upqrService = ApiFactory.getFactory().create(UPQRService.class);
        upqrService.getUnionPayResultDetail(NetUtils.getRequestParams(mContext,map),NetUtils.getSign(NetUtils.getRequestParams(mContext,map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(mContext,true,mView) {

                    @Override
                    public void onNext(Object o) {
                        GetUnionPatResultDetail response = new Gson().fromJson(o.toString(),GetUnionPatResultDetail.class);
                        mView.getPayResultDeatilResult(response.pay_record_detail);
                    }
                });

    }
}
