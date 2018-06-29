package com.zhihuianxin.xyaxf.app.ecard;

import android.content.Context;
import android.content.res.Resources;

import com.zhihuianxin.xyaxf.app.AppConstant;
import com.axinfu.modellib.service.EcardService;
import com.axinfu.modellib.thrift.base.BaseResponse;
import com.axinfu.modellib.thrift.ecard.ECard;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.zhihuianxin.xyaxf.app.base.axutil.BaseSchedulerProvider;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.ApiFactory;
import com.zhihuianxin.xyaxf.app.BaseSubscriber;
import com.zhihuianxin.xyaxf.app.RetrofitFactory;
import com.zhihuianxin.xyaxf.app.utils.NetUtils;
import com.zhihuianxin.xyaxf.app.view.LoadingDialog;
import com.zhihuianxin.xyaxf.test.TestDataConfig;

import java.util.HashMap;
import java.util.Map;

import io.realm.RealmObject;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by zcrpro on 2016/11/8.
 */
public class Ecardpresenter implements EcardContract.EcardPresenter {

    private EcardContract.EcardView mView;
    private Context mContext;
    private CompositeSubscription mSubscriptions;
    private BaseSchedulerProvider mSchedulerProvider;
    private LoadingDialog loadingDialog;

    public static class EcardResponse extends RealmObject {
        public BaseResponse resp;
        public ECard ecard;
    }

    public Ecardpresenter(EcardContract.EcardView mView, Context mContext, BaseSchedulerProvider mSchedulerProvider) {
        this.mView = mView;
        this.mContext = mContext;
        this.mSchedulerProvider = mSchedulerProvider;
        mView.setPresenter(this);
        mSubscriptions = new CompositeSubscription();
        loadingDialog = new LoadingDialog(mContext);
    }

    @Override
    public void loadEcardData(boolean refresh) {
        if (TestDataConfig.ECARD) {
            Gson gson = new Gson();
            try {
                EcardResponse ecardResponse = gson.fromJson(mContext.getResources().getString(R.string.ecard), EcardResponse.class);
                mView.ecardSuccess(ecardResponse.ecard);
                if (ecardResponse.resp.resp_code.equals(AppConstant.NEED_ECARD_PASSWORD)){
                    mView.needPassword();
                }
            } catch (JsonSyntaxException | Resources.NotFoundException e) {
                e.printStackTrace();
            }
        } else {
            if (!refresh) {
                loadingDialog.show();
            }
            RetrofitFactory.setBaseUrl(AppConstant.URL);
            Map<String, Object> map = new HashMap<>();
            EcardService ecardService = ApiFactory.getFactory().create(EcardService.class);
            ecardService.getEcard(NetUtils.getRequestParams(mContext, map), NetUtils.getSign(NetUtils.getRequestParams(mContext, map)))
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new BaseSubscriber<Object>(mContext,false,mView) {
                        @Override
                        public void onNext(Object o) {
                            EcardResponse ecardResponse = new Gson().fromJson(o.toString(), EcardResponse.class);
                            if (ecardResponse.resp.resp_code.equals(AppConstant.NEED_ECARD_PASSWORD)) {
                                mView.needPassword();
                            }
                            mView.ecardSuccess(ecardResponse.ecard);
                            loadingDialog.dismiss();
                        }
                    });
        }
    }

    @Override
    public void subscribe() {

    }

    @Override
    public void unsubscribe() {
        mSubscriptions.clear();
    }
}
