package com.zhihuianxin.xyaxf.app.login.contract;

import com.axinfu.modellib.thrift.customer.Customer;
import com.axinfu.modellib.thrift.customer.VerifyField;
import com.zhihuianxin.xyaxf.app.BasePresenter;
import com.zhihuianxin.xyaxf.app.BaseView;

import java.util.ArrayList;

/**
 * Created by Vincent on 2016/10/11.
 */

public interface ILoginVerPwdContract {
    interface ILoginVerPwdView extends BaseView<ILoginVerPwdPresenter>{
        void loginSuccess(Customer customer, String session);
        void getmodifyPwdInfoResult(ArrayList<VerifyField> verify_fields);
    }

    interface ILoginVerPwdPresenter extends BasePresenter{
        void login(String mobile, String pwd, String uuid);
        void getmodifyPwdInfo(String mobile);
    }
}
