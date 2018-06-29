package com.zhihuianxin.xyaxf.app.unionqr_pay.presenter;

import android.content.Context;

import com.zhihuianxin.xyaxf.app.AppConstant;
import modellib.service.CustomerService;
import modellib.service.PaymentService;
import modellib.service.UPQRService;
import modellib.thrift.base.BaseResponse;
import modellib.thrift.payment.PaymentOrder;
import modellib.thrift.unqr.RealName;
import modellib.thrift.unqr.UPQRPayeeInfo;
import com.google.gson.Gson;
import com.xyaxf.axpay.modle.PayRequest;
import com.zhihuianxin.secure.Secure;
import com.zhihuianxin.xyaxf.app.ApiFactory;
import com.zhihuianxin.xyaxf.app.BaseSubscriber;
import com.zhihuianxin.xyaxf.app.RetrofitFactory;
import com.zhihuianxin.xyaxf.app.payment.CashierDeskActivity;
import com.zhihuianxin.xyaxf.app.unionqr_pay.contract.IunionPayPwdContract;
import com.zhihuianxin.xyaxf.app.utils.NetUtils;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import io.realm.RealmObject;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.zhihuianxin.xyaxf.app.me.view.activity.AddBankCardActivity.bytesToHexString;

/**
 * Created by Vincent on 2017/11/11.
 */

public class UnionPayPwdPresenter implements IunionPayPwdContract.IJudgePayPwdPresenter{

    private Context mContext;
    private IunionPayPwdContract.IJudgePayPwd mView;

    public UnionPayPwdPresenter(Context context, IunionPayPwdContract.IJudgePayPwd view){
        mContext = context;
        mView = view;
        mView.setPresenter(this);
    }

    public class verPayPwdResponse{
        public BaseResponse resp;
        public boolean is_match;
        public int left_retry_count;
    }

    private class setPayPwdResponse extends RealmObject {
        public BaseResponse resp;
    }

    public class getRealNameResponse extends RealmObject {
        public RealName realname;
        public BaseResponse resp;
    }

    private class modifyPayPwdResponse{
        public BaseResponse resp;
        public int left_retry_count;
    }
    @Override
    public void subscribe() {

    }

    @Override
    public void unsubscribe() {

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
                        getRealNameResponse response = new Gson().fromJson(o.toString(),getRealNameResponse.class);
                        mView.getRealNameResult(response.realname);
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
                        verPayPwdResponse response = new Gson().fromJson(o.toString(),verPayPwdResponse.class);
                        mView.verifyPayPwdResult(response.is_match,response.left_retry_count);
                    }
                });
    }

    @Override
    public void setPayPwd(String pay_password) {
        RetrofitFactory.setBaseUrl(AppConstant.URL);

        Map<String,Object> map = new HashMap<>();
        try {
            map.put("payment_password",bytesToHexString(Secure.encodeMessageField(pay_password.getBytes("UTF-8"))));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        PaymentService meService = ApiFactory.getFactory().create(PaymentService.class);
        meService.setPayPwd(NetUtils.getRequestParams(mContext,map),NetUtils.getSign(NetUtils.getRequestParams(mContext,map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(mContext,true,mView) {

                    @Override
                    public void onNext(Object o) {
                        setPayPwdResponse response = new Gson().fromJson(o.toString(),setPayPwdResponse.class);
                        mView.setPayPwdResult(response.resp);
                    }
                });
    }


    @Override
    public void modifyPayPwd(String ori,String newPwd) {
        RetrofitFactory.setBaseUrl(AppConstant.URL);

        Map<String,Object> map = new HashMap<>();
        try {
            map.put("payment_password",bytesToHexString(Secure.encodeMessageField(ori.getBytes("UTF-8"))));
            map.put("new_payment_password",bytesToHexString(Secure.encodeMessageField(newPwd.getBytes("UTF-8"))));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        PaymentService meService = ApiFactory.getFactory().create(PaymentService.class);
        meService.modifyPaymentPwd(NetUtils.getRequestParams(mContext,map),NetUtils.getSign(NetUtils.getRequestParams(mContext,map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(mContext,true,mView) {

                    @Override
                    public void onNext(Object o) {
                        modifyPayPwdResponse response = new Gson().fromJson(o.toString(),modifyPayPwdResponse.class);
                        mView.modifyPayPwdResult(response.left_retry_count);
                    }
                });
    }

    @Override
    public void slearPayPwd() {}

    @Override
    public void payOrder(PayRequest payRequest) {
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String,Object> map = new HashMap<>();
        map.put("pay_req", payRequest);

        PaymentService meService = ApiFactory.getFactory().create(PaymentService.class);
        meService.payOder(NetUtils.getRequestParams(mContext,map),NetUtils.getSign(NetUtils.getRequestParams(mContext,map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(mContext,true,mView) {

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        CashierDeskActivity.PayOderResponse payOderResponse = new CashierDeskActivity.PayOderResponse();
                        payOderResponse.order = new PaymentOrder();
                        payOderResponse.order.error_msg = e.getMessage();
                        mView.payOrderResult(payOderResponse.order);
                    }

                    @Override
                    public void onNext(Object o) {
                        CashierDeskActivity.PayOderResponse payOderResponse = new Gson().fromJson(o.toString(), CashierDeskActivity.PayOderResponse.class);
                        mView.payOrderResult(payOderResponse.order);
                    }
                });
    }

    @Override
    public void getOrderInfo(String qrCode) {
        RetrofitFactory.setBaseUrl(AppConstant.URL);

        Map<String,Object> map = new HashMap<>();
        map.put("qr_code",qrCode);
        UPQRService upqrService = ApiFactory.getFactory().create(UPQRService.class);
        upqrService.getUPQROrder(NetUtils.getRequestParams(mContext,map),NetUtils.getSign(NetUtils.getRequestParams(mContext,map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(mContext,true,mView) {

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        mView.getUpQrOrderResult(null);
                    }

                    @Override
                    public void onNext(Object o) {
                        UnionQrMainPresenter.GetUPQROrderResponse response = new Gson().fromJson(o.toString(),UnionQrMainPresenter.GetUPQROrderResponse.class);
                        mView.getUpQrOrderResult(response.order);
                    }
                });
    }

    @Override
    public void getUpQrCoupon(String tn, String amt, String bank_card_id, UPQRPayeeInfo payee_info) {
        RetrofitFactory.setBaseUrl(AppConstant.URL);

        Map<String,Object> map = new HashMap<>();
        map.put("tn",tn);
        map.put("amt",amt);
        map.put("bank_card_id",bank_card_id);
        map.put("payee_id",payee_info.id);
        UPQRService upqrService = ApiFactory.getFactory().create(UPQRService.class);
        upqrService.getUPCoupon(NetUtils.getRequestParams(mContext,map),NetUtils.getSign(NetUtils.getRequestParams(mContext,map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(mContext,true,mView) {

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        mView.getUpQrCouponResult(null);
                    }

                    @Override
                    public void onNext(Object o) {
                        UnionQrMainPresenter.GetUPQRCouponResponse response = new Gson().fromJson(o.toString(),UnionQrMainPresenter.GetUPQRCouponResponse.class);
                        mView.getUpQrCouponResult(response.coupons.size() == 0?null:response.coupons.get(0));
                    }
                });
    }

    @Override
    public void setPinFree(boolean pin_free, String pin_free_amount, String payment_password) {
        RetrofitFactory.setBaseUrl(AppConstant.URL);

        Map<String,Object> map = new HashMap<>();
        map.put("pin_free",pin_free);
        map.put("pin_free_amount",pin_free_amount);
        try {
            map.put("payment_password",bytesToHexString(Secure.encodeMessageField(payment_password.getBytes("UTF-8"))));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        PaymentService meService = ApiFactory.getFactory().create(PaymentService.class);
        meService.setPinFree(NetUtils.getRequestParams(mContext,map),NetUtils.getSign(NetUtils.getRequestParams(mContext,map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(mContext,true,mView) {

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        if(e.getMessage().contains(AppConstant.UPQR_PAYPWD_THTIMES_ERROR)){
                            mView.setPinFreeResult(false,0);
                            // 要弹框 三次错误
                        } else {
                            // 底层Toast
                        }
                    }

                    @Override
                    public void onNext(Object o) {
                        verPayPwdResponse response = new Gson().fromJson(o.toString(),verPayPwdResponse.class);
                        mView.setPinFreeResult(response.is_match,response.left_retry_count);
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
                .subscribe(new BaseSubscriber<Object>(mContext,true,mView) {

                    @Override
                    public void onNext(Object o) {
                        UnionQrMainPresenter.GetPaymentConfigResponse response = new Gson().fromJson(o.toString(),UnionQrMainPresenter.GetPaymentConfigResponse.class);
                        mView.judgePayPwdResult(response.config);
                    }
                });

    }
}
