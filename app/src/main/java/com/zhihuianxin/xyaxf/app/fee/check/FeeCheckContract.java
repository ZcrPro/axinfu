package com.zhihuianxin.xyaxf.app.fee.check;

import modellib.thrift.fee.FeeAccount;
import com.zhihuianxin.xyaxf.app.BasePresenter;
import com.zhihuianxin.xyaxf.app.BaseView;

/**
 * Created by zcrpro on 2016/11/10.
 */
public interface FeeCheckContract {

    interface PaymentCheckView extends BaseView<PaymentCheckPresenter> {
        void paymentCheckSuccess(FeeAccount eCard);
        void paymentCheckFailure();
    }

    interface  PaymentCheckPresenter extends BasePresenter {
        void openPaymentAccount(String name, String student_no, String id_card_no);
        void openPaymentAccount(String name, String id_card_no);
    }
}
