package com.zhihuianxin.xyaxf.app.me.presenter;

import android.content.Context;

import com.zhihuianxin.xyaxf.app.AppConstant;
import com.axinfu.modellib.service.MeService;
import com.axinfu.modellib.thrift.app.QuestionAnswer;
import com.axinfu.modellib.thrift.base.BaseResponse;
import com.google.gson.Gson;
import com.zhihuianxin.xyaxf.app.ApiFactory;
import com.zhihuianxin.xyaxf.app.BaseSubscriber;
import com.zhihuianxin.xyaxf.app.RetrofitFactory;
import com.zhihuianxin.xyaxf.app.me.contract.IMeHelpCenterContract;
import com.zhihuianxin.xyaxf.app.utils.NetUtils;

import java.util.HashMap;
import java.util.Map;

import io.realm.RealmList;
import io.realm.RealmObject;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Vincent on 2016/11/11.
 */

public class MeHelpCenterPresenter implements IMeHelpCenterContract.IMeHelpCenterPresenter{
    private Context mContext;
    private IMeHelpCenterContract.IMeHelpCenterView mView;

    class GetHelpCenterDataResponse extends RealmObject{
        public BaseResponse resp;
        public RealmList<QuestionAnswer> questions;
    }

    public MeHelpCenterPresenter(Context context, IMeHelpCenterContract.IMeHelpCenterView view){
        mContext = context;
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void getQuestion() {
        RetrofitFactory.setBaseUrl(AppConstant.URL);

        Map<String,Object> map = new HashMap<>();
        MeService meService = ApiFactory.getFactory().create(MeService.class);
        meService.getHelpCenterData(NetUtils.getRequestParams(mContext,map),NetUtils.getSign(NetUtils.getRequestParams(mContext,map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(mContext,false,mView) {

                    @Override
                    public void onNext(Object o) {
                        GetHelpCenterDataResponse response = new Gson().fromJson(o.toString(),GetHelpCenterDataResponse.class);
                        mView.getQuestionSuccess(response.questions);
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
