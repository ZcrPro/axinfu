package com.zhihuianxin.xyaxf.app.login.contract;

import modellib.thrift.customer.Customer;
import com.zhihuianxin.xyaxf.app.BasePresenter;
import com.zhihuianxin.xyaxf.app.BaseView;

/**
 * Created by Vincent on 2016/11/12.
 */

public interface ILoginGetPwdContract {
    interface ILoginGetPwdView extends BaseView<ILoingGetPwdPresenter>{
        void getVerCodeSuccess(String code);
        void getPwdSuccess(Customer customer, String session);
    }

    interface ILoingGetPwdPresenter extends BasePresenter{
        void getVerCode(String verify_for, String mobile);
        void getPwd(String mobile, String secureity_code, String new_password, String attribute_code);
    }
}
