package com.zhihuianxin.xyaxf.app.login.contract;

import modellib.thrift.customer.Customer;
import com.zhihuianxin.xyaxf.app.BasePresenter;
import com.zhihuianxin.xyaxf.app.BaseView;

/**
 * Created by Vincent on 2016/10/17.
 */

public interface ILoginSetPwdOrRegistContract {
    interface ILoginSetPwdOrRegistView extends BaseView<ILoginSetPwdOrRegistPresenter>{
        void setPwdOrRegistAndLoginSuccess(Customer customer, String session);
        void getVerCodeSuccess(String verCode);
    }

    interface ILoginSetPwdOrRegistPresenter extends BasePresenter{
        void getVerCode(String mobile);
        void setPwdOrRegistAndLogin(String mobile, String verCode, String pwd, String machineId);
    }
}
