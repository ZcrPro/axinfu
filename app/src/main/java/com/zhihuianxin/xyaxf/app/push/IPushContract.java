package com.zhihuianxin.xyaxf.app.push;

import modellib.thrift.message.ImportantMessage;
import com.zhihuianxin.xyaxf.app.BasePresenter;
import com.zhihuianxin.xyaxf.app.BaseView;

import io.realm.RealmList;

/**
 * Created by Vincent on 2016/11/15.
 */

public interface IPushContract {
    interface IPushImportantMsgView extends BaseView<IPushImportantMsgPresenter>{
        void getImportantMessageSuccess(RealmList<ImportantMessage> list);
    }

    interface IPushImportantMsgPresenter extends BasePresenter{
        void getImportantMessage(String timestamp);
    }
}
