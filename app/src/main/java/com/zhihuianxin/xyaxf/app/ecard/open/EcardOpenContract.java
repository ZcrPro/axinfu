package com.zhihuianxin.xyaxf.app.ecard.open;

import com.axinfu.modellib.thrift.ecard.ECard;
import com.axinfu.modellib.thrift.ecard.ECardAccount;
import com.zhihuianxin.xyaxf.app.BasePresenter;
import com.zhihuianxin.xyaxf.app.BaseView;

/**
 * Created by zcrpro on 2016/11/14.
 */
public interface EcardOpenContract {

    interface EcardOpenView extends BaseView<EcardOpenPresenter> {
        void ecardOpenSuccess(ECardAccount account, ECard ecard);
        void ecardOpenFailure();
    }

    interface EcardOpenPresenter extends BasePresenter {
        void openEcard(String name, String account_no, String password);
    }

}
