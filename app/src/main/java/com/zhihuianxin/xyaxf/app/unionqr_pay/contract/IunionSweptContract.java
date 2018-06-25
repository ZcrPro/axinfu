package com.zhihuianxin.xyaxf.app.unionqr_pay.contract;

import com.axinfu.modellib.thrift.fee.PaymentRecord;
import com.axinfu.modellib.thrift.payment.PaymentOrder;
import com.axinfu.modellib.thrift.unqr.PaymentConfig;
import com.axinfu.modellib.thrift.unqr.RealName;
import com.axinfu.modellib.thrift.unqr.UPBankCard;
import com.xyaxf.axpay.modle.PayRequest;
import com.zhihuianxin.xyaxf.app.BaseView;

import io.realm.RealmList;

/**
 * Created by Vincent on 2017/12/4.
 */

public interface IunionSweptContract {
    interface IunionSweptView extends BaseView<IunionSweptPresenter>{
        void getBankCardResult(RealmList<UPBankCard> bankCards);
        void JudgePayPwdResult(PaymentConfig config);
        void getC2BCodeResult(String qr_code);
        void swepPayPwdResult();
        void verifyPayPwdResult(boolean is_match, int left_retry_count);
        void payOrderResult(PaymentOrder order);
        void setPayList(RealmList<PaymentRecord> payList);
        void getRealNameResult(RealName realName);
        void c2bQrVerifyPpaymentPasswordResult(boolean is_match, int left_retry_count);
    }

    interface IunionSweptPresenter{
        void getBankCard();
        void JudgePayPwd();
        void getC2BCode(String bank_card_id);
        void swepPayPwd(String qr_code, String amount, String payment_password);
        void verifyPayPwd(String pay_password);// 验证支付密码
        void payOrder(PayRequest payRequest);// 下单
        void loadPayList(String start_date, String end_date, String page_index, String page_size);
        void getRealName();// 获取实名认证状态
        void c2bQrVerifyPpaymentPassword(String qr_code, String amount, String payment_password);
    }
}
