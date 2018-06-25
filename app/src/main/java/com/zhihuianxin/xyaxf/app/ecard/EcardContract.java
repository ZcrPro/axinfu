package com.zhihuianxin.xyaxf.app.ecard;

import com.axinfu.modellib.thrift.ecard.ECard;
import com.zhihuianxin.xyaxf.app.BasePresenter;
import com.zhihuianxin.xyaxf.app.BaseView;

/**
 * Created by zcrpro on 2016/11/8.
 */
public interface EcardContract {

    interface EcardView extends BaseView<EcardPresenter> {
        void ecardSuccess(ECard eCard);
        void ecardFailure();
        void needPassword();
    }

    interface  EcardPresenter extends BasePresenter {
        void loadEcardData(boolean refesh);
    }
}
