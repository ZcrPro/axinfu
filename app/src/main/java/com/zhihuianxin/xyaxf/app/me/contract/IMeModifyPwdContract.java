package com.zhihuianxin.xyaxf.app.me.contract;

import com.axinfu.modellib.thrift.customer.Customer;
import com.axinfu.modellib.thrift.customer.VerifyField;
import com.zhihuianxin.xyaxf.app.BasePresenter;
import com.zhihuianxin.xyaxf.app.BaseView;

import java.util.ArrayList;

/**
 * Created by Vincent on 2016/11/9.
 */

public interface IMeModifyPwdContract {
    interface IMeModifyPwdView extends BaseView<IMeModifyPwdPresenter>{
        void modifySuccess();
        void getmodifyPwdInfoResult(ArrayList<VerifyField> verify_fields);
        void resetPwdByVerInfoResult(Customer customer, String session);
    }

    interface IMeModifyPwdPresenter extends BasePresenter{
        void modifyPwd(String password, String new_password);
        void getmodifyPwdInfo(String mobile);
        void resetPwdByVerInfo(String mobile, String new_password, String attribute_code);
    }
}
