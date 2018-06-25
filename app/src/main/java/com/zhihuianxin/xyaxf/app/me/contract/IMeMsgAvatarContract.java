package com.zhihuianxin.xyaxf.app.me.contract;

import com.axinfu.modellib.thrift.customer.Customer;
import com.axinfu.modellib.thrift.customer.CustomerBaseInfo;
import com.axinfu.modellib.thrift.resource.UploadFileAccess;
import com.axinfu.modellib.thrift.unqr.RealName;
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
