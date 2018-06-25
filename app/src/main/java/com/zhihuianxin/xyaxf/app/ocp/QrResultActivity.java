package com.zhihuianxin.xyaxf.app.ocp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhihuianxin.xyaxf.app.AppConstant;
import com.axinfu.modellib.service.AxfQRPayService;
import com.axinfu.modellib.thrift.base.BaseResponse;
import com.axinfu.modellib.thrift.ocp.CustomerTypes;
import com.axinfu.modellib.thrift.ocp.OcpAccount;
import com.axinfu.modellib.thrift.unqr.PaymentConfig;
import com.axinfu.modellib.thrift.unqr.UPBankCard;
import com.axinfu.modellib.thrift.unqr.UPCoupon;
import com.axinfu.modellib.thrift.unqr.UPQROrder;
import com.google.gson.Gson;
import com.zhihuianxin.axutil.Util;
import com.zhihuianxin.xyaxf.App;
import com.zhihuianxin.xyaxf.BaseRealmActionBarActivity;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.ApiFactory;
import com.zhihuianxin.xyaxf.app.BaseSubscriber;
import com.zhihuianxin.xyaxf.app.RetrofitFactory;
import com.zhihuianxin.xyaxf.app.home.qrcode.QRPayActivity;
import com.zhihuianxin.xyaxf.app.unionqr_pay.contract.IunionQrMainContract;
import com.zhihuianxin.xyaxf.app.unionqr_pay.entity.UnionPayEntity;
import com.zhihuianxin.xyaxf.app.unionqr_pay.presenter.UnionQrMainPresenter;
import com.zhihuianxin.xyaxf.app.unionqr_pay.view.activity.UnionPayActivity;
import com.zhihuianxin.xyaxf.app.unionqr_pay.view.fragment.UnionCashierFragment;
import com.zhihuianxin.xyaxf.app.utils.NetUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.realm.RealmList;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by zcrpro on 2017/12/1.
 */

public class QrResultActivity extends BaseRealmActionBarActivity implements IunionQrMainContract.IGetBankCardInfo {

    @InjectView(R.id.iv_null)
    ImageView ivNull;
    @InjectView(R.id.null_data)
    TextView nullData;
    @InjectView(R.id.rl_null)
    RelativeLayout rlNull;
    @InjectView(R.id.tv_result)
    TextView tvResult;

    private String result;
    private UnionPayEntity entity;
    private IunionQrMainContract.IGetBankCardInfoPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ButterKnife.inject(this);

        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        result = bundle.getString("code");

        new UnionQrMainPresenter(this, this);

        if (result != null) {
            if (result.contains("95516")) { // 银联二维码
                entity = new UnionPayEntity();
                App.mAxLoginSp.setReGetUPQR("0");
                App.mAxLoginSp.setUnionQrCode(result);
                App.mAxLoginSp.setUnionReMark("");
                presenter.getOrderInfo(result);
            } else if (result.contains("wg.axinfu.com/gqr/")) {
                //如果是安心付二维码 查询账户的一卡通状态
                acquiring(result);
                QrResultActivity.this.result = result;
            } else if (result.contains("wxpc.zhihuianxin.net")) {
                //如果是安心付二维码 查询账户的一卡通状态
                acquiring(result);
                QrResultActivity.this.result = result;
            } else if (result.contains("qrId=")) {
                String newStr = result.substring(result.indexOf("qrId="), result.length()).substring(5, result.substring(result.indexOf("qrId="), result.length()).length());
                acquiring(newStr);
                QrResultActivity.this.result = newStr;
            } else if (result.contains("http://wangguan-preview.ymt.axinfu.com")) {
                //如果是安心付二维码 查询账户的一卡通状态
                acquiring(result);
                QrResultActivity.this.result = result;
            } else if (result.startsWith("weixin://") || result.startsWith("alipays://") || result.startsWith("mailto://") || result.startsWith("tel://")) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(result));
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (result.startsWith("http://") || result.startsWith("https://")) {
                Intent i = new Intent(QrResultActivity.this, QRPayActivity.class);
                Bundle bundle2 = new Bundle();
                bundle2.putString(QRPayActivity.EXTRA_URL, result);
                i.putExtras(bundle2);
                startActivity(i);
                finish();
            } else {
                tvResult.setText(result);
            }
        }
    }

    @Override
    protected int getContentViewId() {
        return R.layout.qr_result_activity;
    }

    @Override
    public void getBankCardResult(RealmList<UPBankCard> bankCards) {
        ArrayList<UPBankCard> bankCardsNew = new ArrayList<>();
        for (int i = 0; i < bankCards.size(); i++) {
            UPBankCard bankCard = new UPBankCard();
            bankCard.setId(Util.isEmpty(bankCards.get(i).getId()) ? "" : bankCards.get(i).getId());
            bankCard.setIss_ins_name(Util.isEmpty(bankCards.get(i).getIss_ins_name()) ? "" : bankCards.get(i).getIss_ins_name());
            bankCard.setCard_no(Util.isEmpty(bankCards.get(i).getCard_no()) ? "" : bankCards.get(i).getCard_no());
            bankCard.setCard_type_name(Util.isEmpty(bankCards.get(i).getCard_type_name()) ? "" : bankCards.get(i).getCard_type_name());
            bankCard.setIss_ins_icon(Util.isEmpty(bankCards.get(i).getIss_ins_icon()) ? "" : bankCards.get(i).getIss_ins_icon());
            bankCardsNew.add(bankCard);
        }
        entity.setBankCards(bankCardsNew);

        if (bankCards.size() == 0) {
            gotoUnionPayActivity();
        } else {
            if (!(Util.isEmpty(entity.getUpqrOrder().amt) || entity.getUpqrOrder().amt.equals("0"))) {
                presenter.getUpQrCoupon(
                        entity.getUpqrOrder().tn,
                        entity.getUpqrOrder().amt,
                        getSelectedBank(bankCards),
                        entity.getUpqrOrder().payee_info);
            } else {
                gotoUnionPayActivity();
            }
        }
    }

    private String getSelectedBank(RealmList<UPBankCard> bankCards) {
        String result = "";
        for (int i = 0; i < entity.getBankCards().size(); i++) {
            if (bankCards.get(i).getId().equals(App.mAxLoginSp.getUnionSelBankId())) {
                result = bankCards.get(i).getId();
            }
        }
        if (Util.isEmpty(result)) {
            result = bankCards.get(0).getId();
        }
        return result;
    }

    @Override
    public void getUpQrOrderResult(UPQROrder upqrOrder) {
        Log.d("TAG", "getUpQrOrderResult: " + new Gson().toJson(upqrOrder));
        if (upqrOrder == null) {
            finish();
        } else {
            entity.setUpqrOrder(upqrOrder);
            presenter.getBankCard();
        }
    }

    private void gotoUnionPayActivity() {
        Intent i = new Intent(this, UnionPayActivity.class);
        Bundle b = new Bundle();
        b.putSerializable(UnionCashierFragment.EXTRA_ENTITY, entity);
        i.putExtras(b);
        startActivity(i);
        finish();
    }

    @Override
    public void getUpQrCouponResult(UPCoupon upCoupon) {
        entity.setUpCoupon(upCoupon);
        gotoUnionPayActivity();
    }

    @Override
    public void judgePayPwdResult(PaymentConfig config) {
    }

    @Override
    public void applyBankCardResult(String addCardUrl) {
    }

    @Override
    public void removeBankCardResult() {
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


    /**
     * 收单
     */
    private void acquiring(final String qrId) {
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String, Object> map = new HashMap<>();
//        map.put("qr", qrId);
        if (qrId.contains("wg.axinfu.com/gqr/") || qrId.contains("wxpc.zhihuianxin.net") || qrId.contains("http://wangguan-preview.ymt.axinfu.com")) {
            map.put("qr", qrId);
        } else {
            map.put("qr_id", qrId);
        }
        AxfQRPayService axfQRPayService = ApiFactory.getFactory().create(AxfQRPayService.class);
        axfQRPayService.acquiring(NetUtils.getRequestParams(QrResultActivity.this, map), NetUtils.getSign(NetUtils.getRequestParams(QrResultActivity.this, map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(QrResultActivity.this, true, null) {

                    @Override
                    public void onNext(Object o) {

                        PayInfoResp payInfoResp = new Gson().fromJson(o.toString(), PayInfoResp.class);
                        if (payInfoResp.pay_info.trade_status.code.name().equals(TradeCode.TRADE_ROLE_MISMATCH.name()) || payInfoResp.pay_info.trade_status.code.name().equals(TradeCode.TRADE_FORBIDDEN.name())) {
                            Bundle bundle =new Bundle();
                            bundle.putString("msg", payInfoResp.pay_info.trade_status.msg);
                            if(payInfoResp.pay_info.trade_status.code.name().equals(TradeCode.TRADE_ROLE_MISMATCH.name())){
                                //角色不匹配
                                bundle.putString("title","不支持商户");
                            }
                            if(payInfoResp.pay_info.trade_status.code.name().equals(TradeCode.TRADE_FORBIDDEN.name())){
                                //禁止交易
                                bundle.putString("title","暂无法支付");
                            }
                            Intent intent =new Intent(QrResultActivity.this, NotSupPay.class);
                            intent.putExtras(bundle);
                            startActivity(intent);
                            finish();
                        } else if (payInfoResp.pay_info.trade_status.code.name().equals(TradeCode.TRADE_NEED_AUTH.name())) {
                            Bundle bundle = new Bundle();
                            bundle.putString("qr", result);
                            bundle.putSerializable("payinfo", (Serializable) payInfoResp.pay_info);
                            bundle.putBoolean("isNeedVer", true);
                            Intent intent = new Intent(QrResultActivity.this, OcpVerActivity.class);
                            intent.putExtras(bundle);
                            startActivity(intent);
                            finish();
                        } else {
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("payinfo", (Serializable) payInfoResp.pay_info);
                            bundle.putString("qr", qrId);
                            if (payInfoResp.pay_info.payee_info.amounts.size() > 0) {
                                bundle.putBoolean(OcpPayFixedDeskActivity.FIXED, true);
                            } else {
                                bundle.putBoolean(OcpPayFixedDeskActivity.FIXED, false);
                            }
                            Intent intent = new Intent(QrResultActivity.this, OcpPayFixedDeskActivity.class);
                            intent.putExtras(bundle);
                            startActivity(intent);
                            finish();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        rlNull.setVisibility(View.VISIBLE);
                        nullData.setText(e.getMessage());
                        super.onError(e);
                    }
                });
    }

    public class PayInfoResp implements Serializable {
        public PayInfo pay_info;                  // 商店信息
        public BaseResponse resp;                    // 手续费, percent * amount + fixed_value
    }

    public class PayInfo implements Serializable {
        public PayeeInfo payee_info;                  // 商店信息
        public String auth_hint;                  // 商店信息
        public TradeStatus trade_status;                // 交易状态
        public boolean has_strategy;                // 交易状态
        public List<CustomerTypes> supported_cust_types;
    }

    public class TradeStatus implements Serializable {
        public TradeCode code;                // 交易状态码
        public String msg;                    // 提示消息
    }

    enum TradeCode implements Serializable {
        TRADE_ALLOWED,                // 允许交易
        TRADE_ROLE_MISMATCH,        // 缴费不匹配
        TRADE_NEED_AUTH,            // 用户需要验证
        TRADE_FORBIDDEN,            // 禁止交易
    }

    public class PayeeInfo implements Serializable {
        public String name;                          //
        public String merchant_name;                          //
        public String shop_name;                          //
        public List<PayMethod> pay_methods;            // 支付方法
        public List<Amount> amounts;                    //金额列表，如果为空则自定义，如果为1个或者多个不能自定义
        public boolean is_allowed_outside_pay;            // 是否允许校外人员支付
        public boolean is_the_same_school;            // 学生和商户是否同校
    }

    public class Amount implements Serializable {
        public String amount;                            // 金额
        public String desc;                        // 金额描述
    }

    public class ServiceFee implements Serializable {
        public Float percent;                // 费率
        public String fixed_value;                        // 固定值
    }

    public static class PayMethod implements Serializable {
        public String channel;
        public String merchant_id;
        public String merchant_code;
        public String promotion_hint;
        public String assistance_hint;
        public List<String> supported_banks;
        public boolean is_recommended = false;            // 是否推广
        public boolean is_default = false;                    // 是否默认
        public boolean enabled = false;                    // 是否可用
        //自定义是否选中
        public boolean isSelected = false;
        public UPBankCard card;
        public String purpose;
        public PaymentConfig payment_config;

        public class UPBankCard implements Serializable {
            public String id;
            public String card_type_name;
            public String card_no;
            public String iss_ins_code;
            public String iss_ins_name;
            public String iss_ins_icon;
        }

        @Override
        public String toString() {
            return "PayMethod{" +
                    "channel='" + channel + '\'' +
                    ", merchant_id='" + merchant_id + '\'' +
                    ", merchant_code='" + merchant_code + '\'' +
                    ", promotion_hint='" + promotion_hint + '\'' +
                    ", assistance_hint='" + assistance_hint + '\'' +
                    ", supported_banks=" + supported_banks +
                    ", is_recommended=" + is_recommended +
                    ", is_default=" + is_default +
                    ", isSelected=" + isSelected +
                    '}';
        }
    }


    /**
     * 获取一码通账户状态
     */
    private void loadOcpAccountStatus() {
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String, Object> map = new HashMap<>();
        AxfQRPayService axfQRPayService = ApiFactory.getFactory().create(AxfQRPayService.class);
        axfQRPayService.get_account_info(NetUtils.getRequestParams(QrResultActivity.this, map), NetUtils.getSign(NetUtils.getRequestParams(QrResultActivity.this, map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(QrResultActivity.this, true, this) {

                    @Override
                    public void onNext(Object o) {
                        final OcpAccount ocpAccount = new Gson().fromJson(o.toString(), OcpAccount.class);
                        //获取到之后存入数据
                        App.ocpAccount = ocpAccount;
                        acquiring(result);
                    }
                });
    }
}
