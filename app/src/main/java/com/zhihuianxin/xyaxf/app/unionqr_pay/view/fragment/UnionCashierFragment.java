package com.zhihuianxin.xyaxf.app.unionqr_pay.view.fragment;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhihuianxin.xyaxf.app.AppConstant;
import com.axinfu.modellib.service.PaymentService;
import com.axinfu.modellib.thrift.base.PayChannel;
import com.axinfu.modellib.thrift.base.PayMethod;
import com.axinfu.modellib.thrift.payment.PaymentOrder;
import com.axinfu.modellib.thrift.unqr.PaymentConfig;
import com.axinfu.modellib.thrift.unqr.UPBankCard;
import com.axinfu.modellib.thrift.unqr.UPCoupon;
import com.axinfu.modellib.thrift.unqr.UPQROrder;
import com.google.gson.Gson;
import com.xyaxf.axpay.modle.PayFor;
import com.xyaxf.axpay.modle.PayRequest;
import com.xyaxf.axpay.modle.UPQRPayRequestData;
import com.zhihuianxin.axutil.Util;
import com.zhihuianxin.xyaxf.App;
import com.zhihuianxin.xyaxf.BaseRealmFragment;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.ApiFactory;
import com.zhihuianxin.xyaxf.app.BaseSubscriber;
import com.zhihuianxin.xyaxf.app.RetrofitFactory;
import com.zhihuianxin.xyaxf.app.payment.CashierDeskActivity;
import com.zhihuianxin.xyaxf.app.unionqr_pay.contract.IunionQrMainContract;
import com.zhihuianxin.xyaxf.app.unionqr_pay.entity.UnionPayEntity;
import com.zhihuianxin.xyaxf.app.unionqr_pay.presenter.UnionQrMainPresenter;
import com.zhihuianxin.xyaxf.app.unionqr_pay.view.activity.UnionPAyResultActivity;
import com.zhihuianxin.xyaxf.app.utils.NetUtils;
import com.zhihuianxin.xyaxf.app.view.UnionpayOrderErrorDialog;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.realm.RealmList;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Vincent on 2017/11/7.
 */

public class UnionCashierFragment extends BaseRealmFragment implements View.OnTouchListener, IunionQrMainContract.IGetBankCardInfo {
    public static final String EXTRA_ENTITY = "entityData";
    public static final String EXTRA_SHOW_UNIONCASHIER = "showCashier";

    private View couponView;
    private View mMainView;
    private View mSelectBankView;
    private ImageView mCloseImg;
    private Button mNextPay;
    private TextView mendPrice, mOriPrice, mMName, mBankName, mYHMsg;
    private IunionQrMainContract.IGetBankCardInfoPresenter presenter;

    UnionpayOrderErrorDialog payOrderErrorDialog;

    public interface IToSelectBank {
        void gotoSelectBank();

        void close(UnionPayEntity entity);

        void gotoPay();
    }

    public IToSelectBank iToSelectBank;

    public void setItoSelectBank(IToSelectBank itoSelectBank) {
        this.iToSelectBank = itoSelectBank;
    }

    private UnionPayEntity entity;

    public static Fragment newInstance(UnionPayEntity entity) {
        UnionCashierFragment fragment = new UnionCashierFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(EXTRA_ENTITY, entity);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        new UnionQrMainPresenter(getActivity(), this);
        entity = (UnionPayEntity) getArguments().getSerializable(UnionCashierFragment.EXTRA_ENTITY);
        mMainView = view;

        initViewsss();

        if (entity.getBankCards().size() == 0) {
            initViewsss();
        } else {
            presenter.getUpQrCoupon(entity.getUpqrOrder().tn, entity.getUpqrOrder().amt, entity.getBankCards().get(0).getId(), entity.getUpqrOrder().payee_info);
        }


        payOrderErrorDialog = new UnionpayOrderErrorDialog(getActivity());
    }

    private void initViewsss() {
        couponView = mMainView.findViewById(R.id.coupon);
        if (entity.getUpCoupon() == null) {
            couponView.setVisibility(View.GONE);
        } else {
            couponView.setVisibility(View.VISIBLE);
            mYHMsg = (TextView) mMainView.findViewById(R.id.youhuiMsg);
            mYHMsg.setText(entity.getUpCoupon().desc);
        }
        mNextPay = (Button) mMainView.findViewById(R.id.next);
        mendPrice = (TextView) mMainView.findViewById(R.id.endPrice);
        mOriPrice = (TextView) mMainView.findViewById(R.id.oriPrice);
        mOriPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        mMName = (TextView) mMainView.findViewById(R.id.mName);
        mBankName = (TextView) mMainView.findViewById(R.id.bankName);

        if (entity.getUpCoupon() != null && !Util.isEmpty(entity.getUpCoupon().offst_amt)) {
            DecimalFormat decimalFormat = new DecimalFormat("############0.00");
            mendPrice.setText(decimalFormat.format(Double.parseDouble(entity.getUpqrOrder().amt) - Double.parseDouble(entity.getUpCoupon().offst_amt)));
            mOriPrice.setText("￥" + decimalFormat.format(Float.parseFloat(entity.getUpqrOrder().amt)));
        } else {
            mendPrice.setText("￥" + entity.getUpqrOrder().amt);
            mOriPrice.setText("");
        }
        mMName.setText(entity.getUpqrOrder().payee_info.name);
        if (entity.getBankCards().size() == 0) {
            mBankName.setText("添加银行卡");
        } else {
            mBankName.setText(getSelectedBank());
        }

        if (entity.getBankCards().size() != 0) {
            mNextPay.setEnabled(true);
        } else {
            mNextPay.setEnabled(false);
        }

        mSelectBankView = mMainView.findViewById(R.id.select_bank);
        mSelectBankView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iToSelectBank.gotoSelectBank();
            }
        });
        mCloseImg = (ImageView) mMainView.findViewById(R.id.close_icon);
        mCloseImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iToSelectBank.close(entity);
            }
        });
        mNextPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < entity.getUpqrOrder().pay_methods.size(); i++) {
                    if (entity.getUpqrOrder().pay_methods.get(i).payment_config != null) {
                if(entity.getBankCards().size() != 0){
                    if (entity.getUpqrOrder().pay_methods.get(i).payment_config.pin_free && Float.parseFloat(entity.getUpqrOrder().amt) <= Float.parseFloat(entity.getUpqrOrder().pay_methods.get(i).payment_config.pin_free_amount) && Float.parseFloat(entity.getUpqrOrder().amt) <= Float.parseFloat(entity.getUpqrOrder().pay_methods.get(i).payment_config.trade_limit_per_day)) {
                        payOrder();
                    }else {
                        iToSelectBank.gotoPay();
                    }
                }
                    }else {
                        iToSelectBank.gotoPay();
                    }
                }
            }
        });
        mMainView.setOnTouchListener(this);
    }

    @Override
    public void getUpQrCouponResult(UPCoupon upCoupon) {
        entity.setUpCoupon(upCoupon);
        initViewsss();
    }

    private String getSelectedBank() {
        String result = "";
        for (int i = 0; i < entity.getBankCards().size(); i++) {
            if (entity.getBankCards().get(i).getId().equals(App.mAxLoginSp.getUnionSelBankId())) {
                result = entity.getBankCards().get(i).getIss_ins_name() + " " + entity.getBankCards().get(i).getCard_type_name()
                        + " (" + entity.getBankCards().get(i).getCard_no() + ")";
            }
        }
        if (Util.isEmpty(result)) {
            result = entity.getBankCards().get(0).getIss_ins_name() + " "
                    + entity.getBankCards().get(0).getCard_type_name() + " (" + entity.getBankCards().get(0).getCard_no() + ")";
        }
        return result;
    }

    private String getSelectedBankId() {
        String result = "";
        for (int i = 0; i < entity.getBankCards().size(); i++) {
            if (entity.getBankCards().get(i).getId().equals(App.mAxLoginSp.getUnionSelBankId())) {
                result = entity.getBankCards().get(i).getId();
            }
        }
        if (Util.isEmpty(result)) {
            result = entity.getBankCards().get(0).getId();
        }
        return result;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.unionqrpay_bank;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return true;
    }

    @Override
    public void judgePayPwdResult(PaymentConfig config) {
    }

    @Override
    public void getBankCardResult(RealmList<UPBankCard> bankCards) {
    }

    @Override
    public void applyBankCardResult(String addCardUrl) {
    }

    @Override
    public void removeBankCardResult() {
    }

    @Override
    public void getUpQrOrderResult(UPQROrder upqrOrder) {
    }

    @Override
    public void setPresenter(IunionQrMainContract.IGetBankCardInfoPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void loadStart() {
    }

    @Override
    public void loadError(String errorMsg) {
    }

    @Override
    public void loadComplete() {
    }


    private void payOrder() {
        DecimalFormat decimalFormat;
        PayRequest payRequest = new PayRequest();
        payRequest.pay_for = PayFor.UPQRPay;
        payRequest.amount = entity.getUpqrOrder().amt;
        payRequest.pay_method = new PayMethod();
        payRequest.pay_method.channel = PayChannel.UPQRPay.name();
        UPQRPayRequestData requestData = new UPQRPayRequestData();
        requestData.tn = entity.getUpqrOrder().tn;
        requestData.orig_amt = entity.getUpqrOrder().amt;
        if (entity.getUpCoupon() != null && !Util.isEmpty(entity.getUpCoupon().offst_amt)) {
            decimalFormat = new DecimalFormat(".00");
            requestData.amt = decimalFormat.format(Double.parseDouble(entity.getUpqrOrder().amt) - Double.parseDouble(entity.getUpCoupon().offst_amt));
        } else {
            requestData.amt = entity.getUpqrOrder().amt;
        }
        requestData.bank_card_id = getSelectedBankId();
        ArrayList<UPCoupon> coupons = new ArrayList<>();
        if (entity.getUpCoupon() != null) {
            coupons.add(entity.getUpCoupon());
        }
        requestData.coupons = coupons;
        requestData.payer_comments = App.mAxLoginSp.getUnionReMark();
        payRequest.request_data = requestData;
        payOrder(payRequest);
    }

    public void payOrder(PayRequest payRequest) {
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String, Object> map = new HashMap<>();
        map.put("pay_req", payRequest);
        PaymentService meService = ApiFactory.getFactory().create(PaymentService.class);
        meService.payOder(NetUtils.getRequestParams(getActivity(), map), NetUtils.getSign(NetUtils.getRequestParams(getActivity(), map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(getActivity(), true, null) {

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        CashierDeskActivity.PayOderResponse payOderResponse = new CashierDeskActivity.PayOderResponse();
                        payOderResponse.order = new PaymentOrder();
                        payOderResponse.order.error_msg = e.getMessage();
                        payOrderResult(payOderResponse.order);
                    }

                    @Override
                    public void onNext(Object o) {
                        CashierDeskActivity.PayOderResponse payOderResponse = new Gson().fromJson(o.toString(), CashierDeskActivity.PayOderResponse.class);
                        payOrderResult(payOderResponse.order);
                    }
                });
    }


    private void payOrderResult(PaymentOrder order) {
        if (Util.isEmpty(order.error_msg)) {
            Intent i = new Intent(getActivity(), UnionPAyResultActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(UnionCashierFragment.EXTRA_ENTITY, entity);
            bundle.putSerializable(UnionPAyResultActivity.EXTRA_UPQR_PAYORDER, order);
            i.putExtras(bundle);
            startActivity(i);

            getActivity().finish();
        } else {
            App.mAxLoginSp.setReGetUPQR("1");
            if (order.error_msg.contains(AppConstant.UPQR_PAY_PWD_ERROR)) {
                payOrderErrorDialog.show();
                payOrderErrorDialog.setText(order.error_msg);
            } else {
                // 其他情况在底层类（basesubscriber）进行toast提示
            }
        }
    }

}
