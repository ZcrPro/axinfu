package com.zhihuianxin.xyaxf.app.main;

import android.content.Context;

import com.zhihuianxin.xyaxf.app.AppConstant;
import com.axinfu.modellib.service.CustomerService;
import com.axinfu.modellib.service.MessageService;
import com.google.gson.Gson;
import com.zhihuianxin.axutil.Util;
import com.zhihuianxin.xyaxf.app.ApiFactory;
import com.zhihuianxin.xyaxf.app.BaseSubscriber;
import com.zhihuianxin.xyaxf.app.RetrofitFactory;
import com.zhihuianxin.xyaxf.app.push.PushPresenter;
import com.zhihuianxin.xyaxf.app.utils.NetUtils;

import java.util.HashMap;
import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Vincent on 2016/11/15.
 */

public class MainPresenter implements IMainContract.IMainPresenter {
    private Context mContext;
    private IMainContract.IMainView mView;

    public MainPresenter(Context context, IMainContract.IMainView view){
        mContext = context;
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void updateGeTuiId(String client_id) {
        RetrofitFactory.setBaseUrl(AppConstant.URL);

        Map<String,Object> map = new HashMap<>();
        map.put("client_id",client_id);
        CustomerService meService = ApiFactory.getFactory().create(CustomerService.class);
        meService.updateGeTuiId(NetUtils.getRequestParams(mContext,map),NetUtils.getSign(NetUtils.getRequestParams(mContext,map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(mContext,false,mView) {
                    @Override
                    public void onNext(Object o) {
                    }
                });

    }

    @Override
    public void getImportantMessage(String timestamp) {
        mView.loadStart();
        RetrofitFactory.setBaseUrl(AppConstant.URL);

        Map<String,Object> map = new HashMap<>();
        if(!Util.isEmpty(timestamp)){
            map.put("timestamp",timestamp);
        }
        MessageService meService = ApiFactory.getFactory().create(MessageService.class);
        meService.getImportantMsg(NetUtils.getRequestParams(mContext,map),NetUtils.getSign(NetUtils.getRequestParams(mContext,map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(mContext,false,mView) {

                    @Override
                    public void onNext(Object o) {
                        PushPresenter.GetImprtantMsgResponse response = new Gson().fromJson(o.toString(),PushPresenter.GetImprtantMsgResponse.class);
                        mView.getImportantMessageSuccess(response.messages);
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
