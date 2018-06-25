package com.zhihuianxin.xyaxf.app.push;

import android.content.Context;

import com.zhihuianxin.xyaxf.app.AppConstant;
import com.axinfu.modellib.service.MessageService;
import com.axinfu.modellib.thrift.base.BaseResponse;
import com.axinfu.modellib.thrift.message.ImportantMessage;
import com.google.gson.Gson;
import com.zhihuianxin.xyaxf.app.ApiFactory;
import com.zhihuianxin.xyaxf.app.BaseSubscriber;
import com.zhihuianxin.xyaxf.app.RetrofitFactory;
import com.zhihuianxin.xyaxf.app.utils.NetUtils;

import java.util.HashMap;
import java.util.Map;

import io.realm.RealmList;
import io.realm.RealmObject;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Vincent on 2016/11/15.
 */

public class PushPresenter implements IPushContract.IPushImportantMsgPresenter{
    private Context mContext;
    private IPushContract.IPushImportantMsgView mView;

    public class GetImprtantMsgResponse extends RealmObject{
        public BaseResponse resp;
        public RealmList<ImportantMessage> messages;
    }

    public PushPresenter(Context context,IPushContract.IPushImportantMsgView view){
        mContext = context;
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void getImportantMessage(String timestamp) {
        mView.loadStart();
        RetrofitFactory.setBaseUrl(AppConstant.URL);

        Map<String,Object> map = new HashMap<>();
        map.put("timestamp",timestamp);
        MessageService meService = ApiFactory.getFactory().create(MessageService.class);
        meService.getImportantMsg(NetUtils.getRequestParams(mContext,map),NetUtils.getSign(NetUtils.getRequestParams(mContext,map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(mContext,false,mView) {
                    @Override
                    public void onNext(Object o) {
                        GetImprtantMsgResponse response = new Gson().fromJson(o.toString(),GetImprtantMsgResponse.class);
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
