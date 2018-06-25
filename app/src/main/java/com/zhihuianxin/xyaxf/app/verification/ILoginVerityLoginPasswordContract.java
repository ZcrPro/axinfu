package com.zhihuianxin.xyaxf.app.verification;

import com.zhihuianxin.xyaxf.app.BaseView;

/**
 * Created by Vincent on 2018/1/12.
 */

public interface ILoginVerityLoginPasswordContract {
    interface ILoginVerityLoginPasswordView extends BaseView<ILoginVerityLoginPasswordPresenter> {
        void verityLoginPwdResult();
    }

    interface ILoginVerityLoginPasswordPresenter{
        void verityLoginPwd(String login_password);
    }
}
