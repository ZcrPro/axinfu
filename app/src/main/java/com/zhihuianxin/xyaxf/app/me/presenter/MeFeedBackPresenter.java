package com.zhihuianxin.xyaxf.app.me.presenter;

import android.content.Context;

import com.zhihuianxin.xyaxf.app.AppConstant;
import com.axinfu.modellib.service.MeService;
import com.axinfu.modellib.thrift.app.Appendix;
import com.axinfu.modellib.thrift.base.BaseResponse;
import com.axinfu.modellib.thrift.base.Feedback;
import com.axinfu.modellib.thrift.resource.UploadFileAccess;
import com.google.gson.Gson;
import com.zhihuianxin.xyaxf.app.ApiFactory;
import com.zhihuianxin.xyaxf.app.BaseSubscriber;
import com.zhihuianxin.xyaxf.app.RetrofitFactory;
import com.zhihuianxin.xyaxf.app.me.contract.IMeFeedBackContract;
import com.zhihuianxin.xyaxf.app.utils.NetUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.realm.RealmList;
import io.realm.RealmObject;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Vincent on 2016/11/10.
 */

public class MeFeedBackPresenter implements IMeFeedBackContract.IMeFeedBackPresenter {
    private Context mContext;
    private IMeFeedBackContract.IMeFeedBackView mView;

    class GetFeedBackResponse extends RealmObject{
        public BaseResponse resp;
        public RealmList<Feedback> feedbacks;
    }

    class GetQiNiuAccess extends RealmObject{
        public BaseResponse resp;
        public RealmList<UploadFileAccess> accesses;
    }

    public MeFeedBackPresenter(Context context, IMeFeedBackContract.IMeFeedBackView view){
//        mDialog.setCancelable(false);
//        mDialog.setOnKeyListener(listener);
        mContext = context;
        mView = view;
        mView.setPresenter(this);
    }

//    DialogInterface.OnKeyListener listener = new DialogInterface.OnKeyListener(){
//
//        @Override
//        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
//            return false;
//        }
//    };

    @Override
    public void feedBack(String question, ArrayList<Appendix> list) {
        RetrofitFactory.setBaseUrl(AppConstant.URL);

        Map<String,Object> map = new HashMap<>();
        map.put("question",question);
        if(list != null){
            map.put("appendices",list);
        }
        MeService meService = ApiFactory.getFactory().create(MeService.class);
        meService.feedBack(NetUtils.getRequestParams(mContext,map),NetUtils.getSign(NetUtils.getRequestParams(mContext,map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(mContext,true,mView) {
                    @Override
                    public void onNext(Object o) {
                        mView.feedBackSuccess();
                    }
                });
    }

    @Override
    public void getFeedBack() {
        RetrofitFactory.setBaseUrl(AppConstant.URL);

        Map<String,Object> map = new HashMap<>();
        MeService meService = ApiFactory.getFactory().create(MeService.class);
        meService.getFeedBack(NetUtils.getRequestParams(mContext,map),NetUtils.getSign(NetUtils.getRequestParams(mContext,map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(mContext,false,mView) {

                    @Override
                    public void onNext(Object o) {
                        GetFeedBackResponse response = new Gson().fromJson(o.toString(),GetFeedBackResponse.class);
                        mView.getFeedBackList(response.feedbacks);
                    }
                });
    }

    @Override
    public void getQiNiuAccess(String purpose, String ext,int file_count) {
        RetrofitFactory.setBaseUrl(AppConstant.URL);

        Map<String,Object> map = new HashMap<>();
        map.put("purpose",purpose);
        map.put("ext",ext);
        map.put("file_number",file_count);
        MeService meService = ApiFactory.getFactory().create(MeService.class);
        meService.getQiNiuAccess(NetUtils.getRequestParams(mContext,map),NetUtils.getSign(NetUtils.getRequestParams(mContext,map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(mContext,true,mView) {

                    @Override
                    public void onNext(Object o) {
                        GetQiNiuAccess response = new Gson().fromJson(o.toString(),GetQiNiuAccess.class);
                        mView.getQiNiuAccessSuccess(response.accesses);
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
