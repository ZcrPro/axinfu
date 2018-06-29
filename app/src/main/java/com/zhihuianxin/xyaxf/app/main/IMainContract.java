package com.zhihuianxin.xyaxf.app.main;

import modellib.thrift.message.ImportantMessage;
import com.zhihuianxin.xyaxf.app.BasePresenter;
import com.zhihuianxin.xyaxf.app.BaseView;

import io.realm.RealmList;

/**
 * Created by Vincent on 2016/11/15.
 */

public interface IMainContract {
    interface IMainView extends BaseView<IMainPresenter>{
        void getImportantMessageSuccess(RealmList<ImportantMessage> list);
    }

    interface IMainPresenter extends BasePresenter{
        void updateGeTuiId(String client_id);
        void getImportantMessage(String timestamp);
    }
}
