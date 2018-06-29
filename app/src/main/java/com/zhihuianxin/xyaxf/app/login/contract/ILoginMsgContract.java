package com.zhihuianxin.xyaxf.app.login.contract;

import modellib.thrift.message.AxfMesssage;
import com.zhihuianxin.xyaxf.app.BasePresenter;
import com.zhihuianxin.xyaxf.app.BaseView;

import io.realm.RealmList;

/**
 * Created by Administrator on 2016/10/25.
 */
public interface ILoginMsgContract {
    interface ILoginMsgView extends BaseView<ILoginMsgPresenter>{
        void setMsg(RealmList<AxfMesssage> list);
    }

    interface ILoginMsgPresenter extends BasePresenter{
        void getMsg(int page_index, int page_size);
    }
}
