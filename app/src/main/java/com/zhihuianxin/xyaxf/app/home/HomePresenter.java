package com.zhihuianxin.xyaxf.app.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.axinfu.modellib.service.AxfQRPayService;
import com.axinfu.modellib.service.CustomerService;
import com.axinfu.modellib.service.EcardService;
import com.axinfu.modellib.service.LoanService;
import com.axinfu.modellib.service.MessageService;
import com.axinfu.modellib.service.PaymentService;
import com.axinfu.modellib.service.UPQRService;
import com.axinfu.modellib.thrift.base.BaseResponse;
import com.axinfu.modellib.thrift.customer.Customer;
import com.axinfu.modellib.thrift.ecard.ECard;
import com.axinfu.modellib.thrift.ocp.OcpAccount;
import com.axinfu.modellib.thrift.payment.PayLimit;
import com.google.gson.Gson;
import com.zhihuianxin.xyaxf.App;
import com.zhihuianxin.xyaxf.app.ApiFactory;
import com.zhihuianxin.xyaxf.app.AppConstant;
import com.zhihuianxin.xyaxf.app.BaseSubscriber;
import com.zhihuianxin.xyaxf.app.RetrofitFactory;
import com.zhihuianxin.xyaxf.app.axxyf.AxxyfActivity;
import com.zhihuianxin.xyaxf.app.banner.BannerPresenter;
import com.zhihuianxin.xyaxf.app.unionqr_pay.presenter.UnionQrMainPresenter;
import com.zhihuianxin.xyaxf.app.utils.NetUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.RealmObject;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by zcrpro on 2016/11/4.
 */
public class HomePresenter implements HomeContract.HomePresenter {

    private HomeContract.HomeView mView;
    private Context mContext;
    private CompositeSubscription mSubscriptions;

    public static class EcardResponse extends RealmObject {
        public BaseResponse resp;
        public ECard ecard;
    }

    public static class BankResponse extends RealmObject {
        public BaseResponse resp;
        public PayLimit limit;
    }

    public static class protocolResponse extends RealmObject {
        public BaseResponse resp;
        public List<App.Protocol> protocols;
    }

    public static class CustomerResponse extends RealmObject {
        public BaseResponse resp;
        public Customer customer;
    }

    public HomePresenter(HomeContract.HomeView mView, Context mContext) {
        this.mView = mView;
        this.mContext = mContext;
        mSubscriptions = new CompositeSubscription();
        mView.setPresenter(this);
    }

    @Override
    public void loadBannerData() {
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String, Object> map = new HashMap<>();
        MessageService messageService = ApiFactory.getFactory().create(MessageService.class);
        messageService.getAdvertises(NetUtils.getRequestParams(mContext, map), NetUtils.getSign(NetUtils.getRequestParams(mContext, map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(mContext,false,mView) {
                    @Override
                    public void onNext(Object o) {
                        BannerPresenter.BannerResponse bannnerResponse = new Gson().fromJson(o.toString(), BannerPresenter.BannerResponse.class);
                        mView.bannerSuccess(bannnerResponse.advertises);
                    }
                });

    }

    @Override
    public void loadCustomerData() {
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String, Object> map = new HashMap<>();
        CustomerService customerService = ApiFactory.getFactory().create(CustomerService.class);
        customerService.getCustomer(NetUtils.getRequestParams(mContext, map), NetUtils.getSign(NetUtils.getRequestParams(mContext, map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(mContext,false,mView) {
                    @Override
                    public void onNext(Object o) {
                        CustomerResponse customerResponse = new Gson().fromJson(o.toString(), CustomerResponse.class);
                        mView.customerSuccess(customerResponse.customer);
                        Log.d("fee_account", "onNext: "+new Gson().toJson(customerResponse.customer.fee_account));
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
    public void getProtocolByNo() {
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String, Object> map = new HashMap<>();
        final List<String> list = new ArrayList<>();
        list.add("UP-QRFW-2018.01-01");
        list.add("UP-QRKJZF-2018.01-01");
        map.put("protocol_nos", list);
        CustomerService customerService = ApiFactory.getFactory().create(CustomerService.class);
        customerService.get_protocol_by_no(NetUtils.getRequestParams(mContext, map), NetUtils.getSign(NetUtils.getRequestParams(mContext, map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(mContext, false, mView) {

                    @Override
                    public void onNext(Object o) {
                        final protocolResponse protocolResponse = new Gson().fromJson(o.toString(), protocolResponse.class);
                        mView.getProtocolByNoSuccess(protocolResponse.protocols);
                    }
                });

    }

    @Override
    public void getAccountPayMethodInfo() {
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String, Object> map = new HashMap<>();
        map.put("loan_way_type", "GuiYangCreditLoanPay");
        LoanService loanService = ApiFactory.getFactory().create(LoanService.class);
        loanService.get_account_info(NetUtils.getRequestParams(mContext, map), NetUtils.getSign(NetUtils.getRequestParams(mContext, map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(mContext, false, mView) {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }

                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onNext(Object o) {
                        final AxxyfActivity.LoanAccountInfoRep loanAccountInfoRep = new Gson().fromJson(o.toString(), AxxyfActivity.LoanAccountInfoRep.class);
                        mView.getAccountPayMethodInfoSuccess(loanAccountInfoRep);
                    }
                });
    }

    @Override
    public void getBanklimit() {
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String, Object> map = new HashMap<>();
        PaymentService paymentService = ApiFactory.getFactory().create(PaymentService.class);
        paymentService.bankLimit(NetUtils.getRequestParams(mContext, map), NetUtils.getSign(NetUtils.getRequestParams(mContext, map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(mContext, false, mView) {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Object o) {
                        BankResponse bankResponse = new Gson().fromJson(o.toString(), BankResponse.class);
                        mView.getBanklimitSuccess(bankResponse);
                    }
                });
    }

    @Override
    public void getOcpAccountStatus(final Customer customer) {
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String, Object> map = new HashMap<>();
        AxfQRPayService axfQRPayService = ApiFactory.getFactory().create(AxfQRPayService.class);
        axfQRPayService.get_account_info(NetUtils.getRequestParams(mContext, map), NetUtils.getSign(NetUtils.getRequestParams(mContext, map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(mContext, false, mView) {

                    @Override
                    public void onNext(Object o) {
                        OcpAccount ocpAccount = new Gson().fromJson(o.toString(), OcpAccount.class);
                        mView.getOcpAccountStatusSuccess(customer,ocpAccount);
                    }
                });
    }

    @Override
    public void getEcardData() {
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String, Object> map = new HashMap<>();
        EcardService ecardService = ApiFactory.getFactory().create(EcardService.class);
        ecardService.getEcard(NetUtils.getRequestParams(mContext, map), NetUtils.getSign(NetUtils.getRequestParams(mContext, map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(mContext, false, mView) {
                    @Override
                    public void onCompleted() {}
                    @Override
                    public void onError(Throwable e) {}
                    @Override
                    public void onNext(Object o) {
                        EcardResponse ecardResponse = new Gson().fromJson(o.toString(), EcardResponse.class);
                        mView.getEcardDataSuccess(ecardResponse);
                    }
                });
    }


    @Override
    public void subscribe() {

    }

    @Override
    public void unsubscribe() {
        mSubscriptions.clear();
    }
}
