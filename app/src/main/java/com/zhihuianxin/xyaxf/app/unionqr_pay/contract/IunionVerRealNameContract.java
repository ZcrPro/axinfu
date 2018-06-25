package com.zhihuianxin.xyaxf.app.unionqr_pay.contract;

import com.zhihuianxin.xyaxf.app.BaseView;

/**
 * Created by Vincent on 2017/11/16.
 */

public interface IunionVerRealNameContract {
    interface IunionVerRealName extends BaseView<IunionVerRealNamePresenter>{
        void verRealNameResult(boolean is_match);
    }

    interface IunionVerRealNamePresenter{
        void verRealName(String name, String id_card_no);
    }
}
