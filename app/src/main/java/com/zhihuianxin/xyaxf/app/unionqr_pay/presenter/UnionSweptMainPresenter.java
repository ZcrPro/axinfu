package com.zhihuianxin.xyaxf.app.unionqr_pay.presenter;

import android.content.Context;

import com.zhihuianxin.xyaxf.app.AppConstant;
import com.axinfu.modellib.service.CustomerService;
import com.axinfu.modellib.service.PaymentService;
import com.axinfu.modellib.service.UPQRService;
import com.axinfu.modellib.thrift.base.BaseResponse;
import com.google.gson.Gson;
import com.xyaxf.axpay.modle.PayRequest;
import com.zhihuianxin.secure.Secure;
import com.zhihuianxin.xyaxf.app.ApiFactory;
import com.zhihuianxin.xyaxf.app.BaseSubscriber;
import com.zhihuianxin.xyaxf.app.RetrofitFactory;
import com.zhihuianxin.xyaxf.app.unionqr_pay.contract.IunionSweptContract;
import com.zhihuianxin.xyaxf.app.utils.NetUtils;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.zhihuianxin.xyaxf.app.me.view.activity.AddBankCardActivity.bytesToHexString;

/**
 * Created by Vincent on 2017/12/4.
 */

public class UnionSweptMainPresenter implements IunionSweptContract.IunionSweptPresenter {
    private Context mContext;
    private IunionSweptContract.IunionSweptView mView;

    public UnionSweptMainPresenter(Context context,IunionSweptContract.IunionSweptView view){
        this.mContext = context;
        this.mView = view;
        view.setPresenter(this);
    }

    private class GetC2BCodeResponse{
        public BaseResponse resp;
        public String qr_code;
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
    public void JudgePayPwd() {
        RetrofitFactory.setBaseUrl(AppConstant.URL);

        Map<String,Object> map = new HashMap<>();
        PaymentService meService = ApiFactory.getFactory().create(PaymentService.class);
        meService.getPaymentConfig(NetUtils.getRequestParams(mContext,map),NetUtils.getSign(NetUtils.getRequestParams(mContext,map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(mContext,false,mView) {

                    @Override
                    public void onNext(Object o) {
                        UnionQrMainPresenter.GetPaymentConfigResponse response = new Gson().fromJson(o.toString(),UnionQrMainPresenter.GetPaymentConfigResponse.class);
                        mView.JudgePayPwdResult(response.config);
                    }
                });
    }

    @Override
    public void getC2BCode(String bank_card_id) {
        RetrofitFactory.setBaseUrl(AppConstant.URL);

        Map<String,Object> map = new HashMap<>();
        map.put("bank_card_id",bank_card_id);
        UPQRService meService = ApiFactory.getFactory().create(UPQRService.class);
        meService.getC2BCode(NetUtils.getRequestParams(mContext,map),NetUtils.getSign(NetUtils.getRequestParams(mContext,map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(mContext,true,mView) {

                    @Override
                    public void onNext(Object o) {
                        GetC2BCodeResponse response = new Gson().fromJson(o.toString(),GetC2BCodeResponse.class);
                        mView.getC2BCodeResult(response.qr_code);
                    }
                });
    }

    @Override
    public void verifyPayPwd(String pay_password) {
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String,Object> map = new HashMap<>();
        try {
            map.put("payment_password",bytesToHexString(Secure.encodeMessageField(pay_password.getBytes("UTF-8"))));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        PaymentService meService = ApiFactory.getFactory().create(PaymentService.class);
        meService.verifyPaymentPwd(NetUtils.getRequestParams(mContext,map),NetUtils.getSign(NetUtils.getRequestParams(mContext,map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(mContext,true,mView) {

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        if(e.getMessage().contains(AppConstant.UPQR_PAYPWD_THTIMES_ERROR)){
                            mView.verifyPayPwdResult(false,0);
                            // 要弹框 三次错误
                        } else {
                            // 底层Toast
                        }
                    }

                    @Override
                    public void onNext(Object o) {
                        UnionPayPwdPresenter.verPayPwdResponse response = new Gson().fromJson(o.toString(),UnionPayPwdPresenter.verPayPwdResponse.class);
                        mView.verifyPayPwdResult(response.is_match,response.left_retry_count);
                    }
                });
    }

    @Override
    public void getRealName() {
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String,Object> map = new HashMap<>();
        CustomerService meService = ApiFactory.getFactory().create(CustomerService.class);
        meService.getRealNameQR(NetUtils.getRequestParams(mContext,map),NetUtils.getSign(NetUtils.getRequestParams(mContext,map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(mContext,true,mView) {

                    @Override
                    public void onNext(Object o) {
                        UnionPayPwdPresenter.getRealNameResponse response = new Gson().fromJson(o.toString(),UnionPayPwdPresenter.getRealNameResponse.class);
                        mView.getRealNameResult(response.realname);
                    }
                });
    }

    @Override
    public void c2bQrVerifyPpaymentPassword(String qr_code, String amount, String payment_password) {
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String,Object> map = new HashMap<>();
        try {
            map.put("payment_password",bytesToHexString(Secure.encodeMessageField(payment_password.getBytes("UTF-8"))));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        map.put("qr_code",qr_code);
        map.put("amount",amount);
        UPQRService meService = ApiFactory.getFactory().create(UPQRService.class);
        meService.c2bVerPwd(NetUtils.getRequestParams(mContext,map),NetUtils.getSign(NetUtils.getRequestParams(mContext,map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(mContext,true,mView) {

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        if(e.getMessage().contains(AppConstant.UPQR_PAYPWD_THTIMES_ERROR)){
                            mView.c2bQrVerifyPpaymentPasswordResult(false,0);
                            // 要弹框 三次错误
                        } else {
                            // 底层Toast
                        }
                    }

                    @Override
                    public void onNext(Object o) {
                        UnionPayPwdPresenter.verPayPwdResponse response = new Gson().fromJson(o.toString(),UnionPayPwdPresenter.verPayPwdResponse.class);
                        mView.c2bQrVerifyPpaymentPasswordResult(response.is_match,response.left_retry_count);
                    }
                });
    }

    @Override
    public void payOrder(PayRequest payRequest) {}
    @Override
    public void loadPayList(String start_date, String end_date, String page_index, String page_size) {}
    @Override
    public void swepPayPwd(String qr_code, String amount, String payment_password) {}
}
