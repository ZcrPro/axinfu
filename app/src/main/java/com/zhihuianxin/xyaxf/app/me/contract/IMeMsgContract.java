package com.zhihuianxin.xyaxf.app.me.contract;

import modellib.thrift.customer.Customer;
import modellib.thrift.customer.CustomerBaseInfo;
import modellib.thrift.customer.VerifyField;
import com.zhihuianxin.xyaxf.app.BasePresenter;
import com.zhihuianxin.xyaxf.app.BaseView;

import java.util.ArrayList;

/**
 * Created by Vincent on 2016/11/7.
 */

public interface IMeMsgContract {
    interface IMeMsgView extends BaseView<IMeMsgPresenter>{
        void modifyBaseInfoSuccess(Customer customer);
        void modifyMobileSuccess(Customer customer);
        void getVerCodeSuccess(String code);
        void getmodifyPwdInfoResult(ArrayList<VerifyField> verify_fields);
    }

    interface IMeMsgPresenter extends BasePresenter{
        void modifyBaseInfo(CustomerBaseInfo baseInfo);
        void modifyMobile(String mobile, String verCode, String newPwd);
        void getVerCode(String mobile);
        void getmodifyPwdInfo(String mobile);
    }
}
