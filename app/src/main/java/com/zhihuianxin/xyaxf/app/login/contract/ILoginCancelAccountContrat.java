package com.zhihuianxin.xyaxf.app.login.contract;

import com.zhihuianxin.xyaxf.app.BaseView;

/**
 * Created by Vincent on 2017/12/21.
 */

public interface ILoginCancelAccountContrat {
    interface ILoginCancelAccountView extends BaseView<ILoginCancelAccountPresenter>{
        void cancelAccountResult();
    }

    interface ILoginCancelAccountPresenter{
        void cancelAccount(String pwd);
    }
}
