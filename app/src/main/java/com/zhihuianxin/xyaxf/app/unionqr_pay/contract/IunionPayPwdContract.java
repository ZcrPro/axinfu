package com.zhihuianxin.xyaxf.app.unionqr_pay.contract;

import modellib.thrift.base.BaseResponse;
import modellib.thrift.payment.PaymentOrder;
import modellib.thrift.unqr.PaymentConfig;
import modellib.thrift.unqr.RealName;
import modellib.thrift.unqr.UPCoupon;
import modellib.thrift.unqr.UPQROrder;
import modellib.thrift.unqr.UPQRPayeeInfo;
import com.xyaxf.axpay.modle.PayRequest;
import com.zhihuianxin.xyaxf.app.BasePresenter;
import com.zhihuianxin.xyaxf.app.BaseView;

/**
 * Created by Vincent on 2017/11/11.
 */

public interface IunionPayPwdContract {
    interface IJudgePayPwd extends BaseView<IJudgePayPwdPresenter> {
        void getRealNameResult(RealName realName);
        void verifyPayPwdResult(boolean is_match, int left_retry_count);
        void setPayPwdResult(BaseResponse baseResponse);
        void modifyPayPwdResult(int left_retry_count);
        void slearPayPwdResult();
        void payOrderResult(PaymentOrder order);
        void getUpQrOrderResult(UPQROrder upqrOrder);
        void getUpQrCouponResult(UPCoupon upCoupon);
        void setPinFreeResult(boolean is_match, int left_retry_count);
        void judgePayPwdResult(PaymentConfig config);
    }

    interface IJudgePayPwdPresenter extends BasePresenter {
        void getRealName();// 获取实名认证状态
        void verifyPayPwd(String pay_password);// 验证支付密码
        void setPayPwd(String pay_password);// 设置支付密码
        void modifyPayPwd(String ori, String newPwd);//修改支付密码
        void slearPayPwd();//清除支付密码
        void payOrder(PayRequest payRequest);// 下单
        void getOrderInfo(String qrCode);
        void getUpQrCoupon(String tn, String amt, String bank_card_no, UPQRPayeeInfo payee_info);
        void setPinFree(boolean pin_free, String pin_free_amount, String payment_password);
        void JudgePayPwd();
    }
}
