package com.zhihuianxin.xyaxf.app.unionqr_pay.contract;

import com.zhihuianxin.xyaxf.app.BaseView;

/**
 * Created by Vincent on 2017/11/16.
 */

public interface IunionClearPwdContract {
    interface IunionClearPwd extends BaseView<IunionClearPwdPresenter>{
        void getVerCodeResult(String code);
        void setPayPwdResult();
        void slearPayPwdResult();
    }

    interface IunionClearPwdPresenter{
        void getVerCode(String verify_for, String mobile);
        void setPayPwd(String payment_password);
        void slearPayPwd(String secure_code);
    }
}
