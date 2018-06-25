package com.zhihuianxin.xyaxf.app.login.contract;

import com.axinfu.modellib.thrift.customer.Customer;
import com.axinfu.modellib.thrift.resource.School;
import com.zhihuianxin.xyaxf.app.BasePresenter;
import com.zhihuianxin.xyaxf.app.BaseView;

import io.realm.RealmList;

/**
 * Created by Vincent on 2016/10/31.
 */

public interface ILoginSelectSchoolContract {
    interface ISelectSchoolView extends BaseView<ISelectSchoolPresenter> {
        void setSchoolData(RealmList<School> schoolData);
        void updateSchoolSuccess(Customer customer);
    }

    interface ISelectSchoolPresenter extends BasePresenter {
        void loadSchool(String cityCode);
        void updateSchool(String school_code);
    }
}
