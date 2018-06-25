package com.zhihuianxin.xyaxf.app.unionqr_pay.presenter;

import android.content.Context;

import com.zhihuianxin.xyaxf.app.AppConstant;
import com.axinfu.modellib.service.UPQRService;
import com.axinfu.modellib.thrift.base.BaseResponse;
import com.zhihuianxin.xyaxf.app.ApiFactory;
import com.zhihuianxin.xyaxf.app.BaseSubscriber;
import com.zhihuianxin.xyaxf.app.RetrofitFactory;
import com.zhihuianxin.xyaxf.app.unionqr_pay.contract.IunionCommitRealNameContrat;
import com.zhihuianxin.xyaxf.app.utils.NetUtils;

import java.util.HashMap;
import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Vincent on 2017/11/15.
 */

public class UnionCommitRealNamePresenter implements IunionCommitRealNameContrat.ICommitRealNamePresenter{

    private Context mContext;
    private IunionCommitRealNameContrat.ICommitRealName mView;

    public UnionCommitRealNamePresenter(Context context,IunionCommitRealNameContrat.ICommitRealName view){
        mContext = context;
        mView = view;
        mView.setPresenter(this);
    }

    private class CommitRealNameResponse {
        public BaseResponse resp;
    }

    @Override
    public void commitRealName(String id, String name) {
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String,Object> map = new HashMap<>();
        map.put("name",name);
        map.put("id_card_no",id);
        UPQRService upqrService = ApiFactory.getFactory().create(UPQRService.class);
        upqrService.commitRealName(NetUtils.getRequestParams(mContext,map),NetUtils.getSign(NetUtils.getRequestParams(mContext,map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(mContext,true,mView) {

                    @Override
                    public void onNext(Object o) {
                        mView.commitRealNameResult();
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

