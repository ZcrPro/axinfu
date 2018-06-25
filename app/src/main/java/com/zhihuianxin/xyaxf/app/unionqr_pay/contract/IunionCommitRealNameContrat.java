package com.zhihuianxin.xyaxf.app.unionqr_pay.contract;

import com.zhihuianxin.xyaxf.app.BasePresenter;
import com.zhihuianxin.xyaxf.app.BaseView;

/**
 * Created by Vincent on 2017/11/15.
 */

public interface IunionCommitRealNameContrat {
    interface ICommitRealName extends BaseView<ICommitRealNamePresenter> {
        void commitRealNameResult();
    }

    interface ICommitRealNamePresenter extends BasePresenter {
        void commitRealName(String id, String name);
    }
}
