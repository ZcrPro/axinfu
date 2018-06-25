package com.zhihuianxin.xyaxf.app.home.business;

import com.axinfu.modellib.thrift.business.Business;
import com.zhihuianxin.xyaxf.app.BasePresenter;
import com.zhihuianxin.xyaxf.app.BaseView;

import io.realm.RealmResults;

/**
 * Created by zcrpro on 2016/10/31.
 */
public interface BusinessGridContract {

    interface IBusinessGridView extends BaseView<IBusinessGridPresenter> {
        void success(RealmResults<Business> businesses);
        void failure();
    }

    interface  IBusinessGridPresenter extends BasePresenter {
        void loadBusinessData();
    }

}
