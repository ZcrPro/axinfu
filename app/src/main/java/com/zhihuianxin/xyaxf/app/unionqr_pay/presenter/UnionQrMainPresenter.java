package com.zhihuianxin.xyaxf.app.unionqr_pay.presenter;

import android.content.Context;
import android.util.Log;

import com.zhihuianxin.xyaxf.app.AppConstant;
import modellib.service.PaymentService;
import modellib.service.UPQRService;
import modellib.thrift.base.BaseResponse;
import modellib.thrift.unqr.PaymentConfig;
import modellib.thrift.unqr.UPBankCard;
import modellib.thrift.unqr.UPCoupon;
import modellib.thrift.unqr.UPQROrder;
import modellib.thrift.unqr.UPQRPayeeInfo;
import com.google.gson.Gson;
import com.zhihuianxin.xyaxf.app.ApiFactory;
import com.zhihuianxin.xyaxf.app.BaseSubscriber;
import com.zhihuianxin.xyaxf.app.RetrofitFactory;
import com.zhihuianxin.xyaxf.app.unionqr_pay.contract.IunionQrMainContract;
import com.zhihuianxin.xyaxf.app.utils.NetUtils;

import java.util.HashMap;
import java.util.Map;

import io.realm.RealmList;
import io.realm.RealmObject;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Vincent on 2017/11/9.
 */

public class UnionQrMainPresenter implements IunionQrMainContract.IGetBankCardInfoPresenter {
    private Context mContext;
    private IunionQrMainContract.IGetBankCardInfo mView;

    public UnionQrMainPresenter(Context context, IunionQrMainContract.IGetBankCardInfo view) {
        mContext = context;
        mView = view;
        mView.setPresenter(this);
    }

    public class GetUPQRCouponResponse extends RealmObject {
        public BaseResponse resp;
        public RealmList<UPCoupon> coupons;
    }

    public class GetUPQROrderResponse extends RealmObject {
        public BaseResponse resp;
        public UPQROrder order;
    }

    public class GetPaymentConfigResponse extends RealmObject {
        public BaseResponse resp;
        public PaymentConfig config;
    }

    public class GetUPQRBankCardsResponse extends RealmObject {
        public BaseResponse resp;
        public RealmList<UPBankCard> bank_cards;
    }

    @Override
    public void JudgePayPwd() {
        RetrofitFactory.setBaseUrl(AppConstant.URL);

        Map<String, Object> map = new HashMap<>();
        PaymentService meService = ApiFactory.getFactory().create(PaymentService.class);
        meService.getPaymentConfig(NetUtils.getRequestParams(mContext, map), NetUtils.getSign(NetUtils.getRequestParams(mContext, map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(mContext, true, mView) {

                    @Override
                    public void onNext(Object o) {
                        GetPaymentConfigResponse response = new Gson().fromJson(o.toString(), GetPaymentConfigResponse.class);
                        mView.judgePayPwdResult(response.config);
                    }
                });
    }

    @Override
    public void getBankCard() {
        RetrofitFactory.setBaseUrl(AppConstant.URL);

        Map<String, Object> map = new HashMap<>();
        UPQRService upqrService = ApiFactory.getFactory().create(UPQRService.class);
        upqrService.getUPQRBankCards(NetUtils.getRequestParams(mContext, map), NetUtils.getSign(NetUtils.getRequestParams(mContext, map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(mContext, true, mView) {

                    @Override
                    public void onNext(Object o) {
                        GetUPQRBankCardsResponse response = new Gson().fromJson(o.toString(), GetUPQRBankCardsResponse.class);
                        mView.getBankCardResult(response.bank_cards);
                    }
                });
    }

    @Override
    public void applyBankCard() {

    }

    @Override
    public void removeBankCard(String bankCardId) {
        RetrofitFactory.setBaseUrl(AppConstant.URL);

        Map<String, Object> map = new HashMap<>();
        map.put("id", bankCardId);
        UPQRService upqrService = ApiFactory.getFactory().create(UPQRService.class);
        upqrService.delUPBankCard(NetUtils.getRequestParams(mContext, map), NetUtils.getSign(NetUtils.getRequestParams(mContext, map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(mContext, true, mView) {

                    @Override
                    public void onNext(Object o) {
                        mView.removeBankCardResult();
                    }
                });

    }

    @Override
    public void getOrderInfo(String qrCode) {
        RetrofitFactory.setBaseUrl(AppConstant.URL);

        Map<String, Object> map = new HashMap<>();
        map.put("qr_code", qrCode);
        UPQRService upqrService = ApiFactory.getFactory().create(UPQRService.class);
        upqrService.getUPQROrder(NetUtils.getRequestParams(mContext, map), NetUtils.getSign(NetUtils.getRequestParams(mContext, map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(mContext, true, mView) {

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        mView.getUpQrOrderResult(null);
                    }

                    @Override
                    public void onNext(Object o) {
                        Log.d("UnionQrMainPresenter", "response:" + o.toString());
                        GetUPQROrderResponse response = new Gson().fromJson(o.toString(), GetUPQROrderResponse.class);
                        Log.d("UnionQrMainPresenter", "response:" + new Gson().toJson(response));
                        mView.getUpQrOrderResult(response.order);
                    }
                });
    }

    @Override
    public void getUpQrCoupon(String tn, String amt, String bank_card_id, UPQRPayeeInfo payee_info) {
        RetrofitFactory.setBaseUrl(AppConstant.URL);

        Map<String, Object> map = new HashMap<>();
        map.put("tn", tn);
        map.put("amt", amt);
        map.put("bank_card_id", bank_card_id);
        map.put("payee_id", payee_info.id);
        UPQRService upqrService = ApiFactory.getFactory().create(UPQRService.class);
        upqrService.getUPCoupon(NetUtils.getRequestParams(mContext, map), NetUtils.getSign(NetUtils.getRequestParams(mContext, map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(mContext, false, mView) {

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        mView.getUpQrCouponResult(null);
                    }

                    @Override
                    public void onNext(Object o) {
                        GetUPQRCouponResponse response = new Gson().fromJson(o.toString(), GetUPQRCouponResponse.class);
                        mView.getUpQrCouponResult(response.coupons.size() == 0 ? null : response.coupons.get(0));
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
