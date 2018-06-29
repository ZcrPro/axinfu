package com.zhihuianxin.xyaxf.app.me.contract;

import modellib.thrift.customer.Customer;
import modellib.thrift.customer.CustomerBaseInfo;
import modellib.thrift.resource.UploadFileAccess;
import modellib.thrift.unqr.RealName;
import com.zhihuianxin.xyaxf.app.BasePresenter;
import com.zhihuianxin.xyaxf.app.BaseView;

import io.realm.RealmList;

/**
 * Created by Vincent on 2016/11/15.
 */

public interface IMeMsgAvatarContract {
    interface IMeMsgAvatarView extends BaseView<IMeMsgAvatarPresenter> {
        void modifyBaseInfoSuccess(Customer customer);
        void getQiNiuAccessSuccess(RealmList<UploadFileAccess> accesses);
        void getRealNameResult(RealName realName);
    }

    interface IMeMsgAvatarPresenter extends BasePresenter {
        void getQiNiuAccess(String purpose, String ext, int file_count);
        void modifyCustomerBaseInfo(CustomerBaseInfo baseInfo);
        void getRealName();
    }
}
