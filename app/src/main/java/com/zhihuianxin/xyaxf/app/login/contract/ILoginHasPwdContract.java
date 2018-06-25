package com.zhihuianxin.xyaxf.app.login.contract;

import com.axinfu.modellib.thrift.customer.Customer;
import com.axinfu.modellib.thrift.customer.MobileStatus;
import com.axinfu.modellib.thrift.customer.VerifyField;
import com.zhihuianxin.xyaxf.app.BasePresenter;
import com.zhihuianxin.xyaxf.app.BaseView;

import java.util.ArrayList;

/**
 * Created by Vincent on 2016/10/18.
 */

public interface ILoginHasPwdContract {
    interface ILoginHasPwdView extends BaseView<ILoginHasPwdPresenter>{
        void serverPwd(MobileStatus status);
        void loginSuccess(Customer customer, String session);
        void setPwdOrRegistAndLoginSuccess(Customer customer, String session);
        void getVerCodeSuccess(String verCode);
        void getmodifyPwdInfoResult(ArrayList<VerifyField> verify_fields);
    }

    interface ILoginHasPwdPresenter extends BasePresenter{
        void hasSetPwd(String mobile);
        void login(String mobile, String pwd, String uuid);
        void getVerCode(String mobile, String UserStatue);
        void setPwdOrRegistAndLogin(String mobile, String verCode, String pwd, String machineId);
        void getmodifyPwdInfo(String mobile);
    }
}
