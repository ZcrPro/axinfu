package com.zhihuianxin.xyaxf.app.me.contract;

import com.axinfu.modellib.thrift.app.Appendix;
import com.axinfu.modellib.thrift.base.Feedback;
import com.axinfu.modellib.thrift.resource.UploadFileAccess;
import com.zhihuianxin.xyaxf.app.BasePresenter;
import com.zhihuianxin.xyaxf.app.BaseView;

import java.util.ArrayList;

import io.realm.RealmList;

/**
 * Created by Vincent on 2016/11/10.
 */

public interface IMeFeedBackContract {
    interface IMeFeedBackView extends BaseView<IMeFeedBackPresenter>{
        void feedBackSuccess();
        void getFeedBackList(RealmList<Feedback> feedbacks);
        void getQiNiuAccessSuccess(RealmList<UploadFileAccess> accesses);
    }

    interface IMeFeedBackPresenter extends BasePresenter{
        void feedBack(String question, ArrayList<Appendix> list);
        void getFeedBack();
        void getQiNiuAccess(String purpose, String ext, int file_count);
    }
}
