package com.zhihuianxin.xyaxf.app.login.presenter;

import android.content.Context;

import com.zhihuianxin.xyaxf.app.AppConstant;
import com.axinfu.modellib.service.MessageService;
import com.axinfu.modellib.thrift.base.BaseResponse;
import com.axinfu.modellib.thrift.message.AxfMesssage;
import com.google.gson.Gson;
import com.zhihuianxin.xyaxf.app.base.axutil.BaseSchedulerProvider;
import com.zhihuianxin.xyaxf.app.ApiFactory;
import com.zhihuianxin.xyaxf.app.BaseSubscriber;
import com.zhihuianxin.xyaxf.app.RetrofitFactory;
import com.zhihuianxin.xyaxf.app.login.contract.ILoginMsgContract;
import com.zhihuianxin.xyaxf.app.utils.NetUtils;

import java.util.HashMap;
import java.util.Map;

import io.realm.RealmList;
import io.realm.RealmObject;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2016/10/25.
 */
public class LoginMsgPresenter implements ILoginMsgContract.ILoginMsgPresenter{
    private Context mContext;
    private ILoginMsgContract.ILoginMsgView mView;

    class GetMessageResponse extends RealmObject{
        public BaseResponse resp;
        public RealmList<AxfMesssage> messages;
    }

    public LoginMsgPresenter(Context context, ILoginMsgContract.ILoginMsgView view, BaseSchedulerProvider provider){
        mContext = context;
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void getMsg(int page_index,int page_size) {
        RetrofitFactory.setBaseUrl(AppConstant.URL);

        Map<String,Object> map = new HashMap<>();
        map.put("page_index",page_index);
        map.put("page_size",page_size);
        MessageService msgService = ApiFactory.getFactory().create(MessageService.class);
        msgService.getMsg(NetUtils.getRequestParams(mContext,map),NetUtils.getSign(NetUtils.getRequestParams(mContext,map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(mContext,false,mView) {
                    @Override
                    public void onNext(Object o) {
                        GetMessageResponse response = new Gson().fromJson(o.toString(),GetMessageResponse.class);
                        mView.setMsg(response.messages);
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
