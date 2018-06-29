package com.zhihuianxin.xyaxf.app.unionqr_pay.presenter;

import android.content.Context;

import com.zhihuianxin.xyaxf.app.AppConstant;
import modellib.service.UPQRService;
import modellib.thrift.base.BaseResponse;
import com.google.gson.Gson;
import com.zhihuianxin.xyaxf.app.ApiFactory;
import com.zhihuianxin.xyaxf.app.BaseSubscriber;
import com.zhihuianxin.xyaxf.app.RetrofitFactory;
import com.zhihuianxin.xyaxf.app.unionqr_pay.contract.IunionBindCardContract;
import com.zhihuianxin.xyaxf.app.utils.NetUtils;

import java.util.HashMap;
import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Vincent on 2017/11/15.
 */

public class UnionUPQRBindCardPresenter implements IunionBindCardContract.IbindCardPresenter{
    private Context mContext;
    private IunionBindCardContract.IbindCard mView;

    private class ApplyUPQRBankCardRespopnse {
        public BaseResponse resp;
        public String html;
    }

    public UnionUPQRBindCardPresenter(Context context, IunionBindCardContract.IbindCard view){
        mContext = context;
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void bingUPQRBankCard() {
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String,Object> map = new HashMap<>();
        UPQRService upqrService = ApiFactory.getFactory().create(UPQRService.class);
        upqrService.applyUPQRPayBankCard(NetUtils.getRequestParams(mContext,map),NetUtils.getSign(NetUtils.getRequestParams(mContext,map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(mContext,true,mView) {

                    @Override
                    public void onNext(Object o) {
                        ApplyUPQRBankCardRespopnse response = new Gson().fromJson(o.toString(),ApplyUPQRBankCardRespopnse.class);
                        mView.bingUPQRBankCardResult(response.html);
                    }
                });

    }

    @Override
    public void getBankCard() {
        RetrofitFactory.setBaseUrl(AppConstant.URL);

        Map<String,Object> map = new HashMap<>();
        UPQRService upqrService = ApiFactory.getFactory().create(UPQRService.class);
        upqrService.getUPQRBankCards(NetUtils.getRequestParams(mContext,map),NetUtils.getSign(NetUtils.getRequestParams(mContext,map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(mContext,true,mView) {

                    @Override
                    public void onNext(Object o) {
                        UnionQrMainPresenter.GetUPQRBankCardsResponse response = new Gson().fromJson(o.toString(),UnionQrMainPresenter.GetUPQRBankCardsResponse.class);
                        mView.getBankCardResult(response.bank_cards);
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
