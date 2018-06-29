package com.zhihuianxin.xyaxf.app.login.contract;

import modellib.thrift.customer.Customer;
import com.zhihuianxin.xyaxf.app.BasePresenter;
import com.zhihuianxin.xyaxf.app.BaseView;

/**
 * Created by Vincent on 2017/12/21.
 */

public interface ILoginVerPwdFieldContract {
    interface ILoginVerPwdFieldView extends BaseView<ILoginVerPwdFieldPresenter> {
        void verpwdFieldResult(String errorMsg);
        void setPwdByFieldResult(Customer customer, String session);
    }

    interface ILoginVerPwdFieldPresenter extends BasePresenter {
        void verpwdField(String mobile, String name, String student_no, String id_card_no, String bank_card_no);
        void setPwdByField(String mobile, String new_password, String attribute_code);
    }
}
