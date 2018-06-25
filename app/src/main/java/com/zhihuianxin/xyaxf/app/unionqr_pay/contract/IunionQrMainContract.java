package com.zhihuianxin.xyaxf.app.unionqr_pay.contract;

import com.axinfu.modellib.thrift.unqr.PaymentConfig;
import com.axinfu.modellib.thrift.unqr.UPBankCard;
import com.axinfu.modellib.thrift.unqr.UPCoupon;
import com.axinfu.modellib.thrift.unqr.UPQROrder;
import com.axinfu.modellib.thrift.unqr.UPQRPayeeInfo;
import com.zhihuianxin.xyaxf.app.BasePresenter;
import com.zhihuianxin.xyaxf.app.BaseView;

import io.realm.RealmList;

/**
 * Created by Vincent on 2017/11/9.
 */

public interface IunionQrMainContract {
    interface IGetBankCardInfo extends BaseView<IGetBankCardInfoPresenter>{
        void judgePayPwdResult(PaymentConfig config);
        void getBankCardResult(RealmList<UPBankCard> bankCards);
        void applyBankCardResult(String addCardUrl);
        void removeBankCardResult();
        void getUpQrOrderResult(UPQROrder upqrOrder);
        void getUpQrCouponResult(UPCoupon upCoupon);
    }

    interface IGetBankCardInfoPresenter extends BasePresenter{
        void JudgePayPwd();
        void getBankCard();
        void applyBankCard();
        void removeBankCard(String bankCardId);
        void getOrderInfo(String qrCode);
        void getUpQrCoupon(String tn, String amt, String bank_card_no, UPQRPayeeInfo payee_info);
    }
}
