package com.zhihuianxin.xyaxf.app.login.contract;

import com.axinfu.modellib.thrift.resource.City;
import com.zhihuianxin.xyaxf.app.BasePresenter;
import com.zhihuianxin.xyaxf.app.BaseView;

import io.realm.RealmList;

/**
 * Created by Vincent on 2016/10/12.
 */

public interface ILoginSelectCityContract {
    interface ISelectCityView extends BaseView<ISelectCityPresenter> {
        void setCityData(RealmList<City> cityData);
    }

    interface ISelectCityPresenter extends BasePresenter {
        void loadCity();
    }
}
