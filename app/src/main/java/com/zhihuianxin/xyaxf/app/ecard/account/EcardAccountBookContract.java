package com.zhihuianxin.xyaxf.app.ecard.account;

import modellib.thrift.ecard.ECardRecord;
import com.zhihuianxin.xyaxf.app.BasePresenter;
import com.zhihuianxin.xyaxf.app.BaseView;

import java.util.List;

/**
 * Created by zcrpro on 2016/11/12.
 */
public interface EcardAccountBookContract {

    interface EcardAccountBookView extends BaseView<EcardAccountBookPresenter> {
        void ecardAccountBookSuccess(List<ECardRecord> eCardRecords, boolean isLoadMore);

        void ecardAccountBookFailure();
    }

    interface EcardAccountBookPresenter extends BasePresenter {
        void loadEcardAccountBook(String start_date, String end_date, int page_index, int page_size, boolean refresh, boolean isLoadMore);
        void loadEcardAccountBook(String start_date, String end_date, int page_index, int page_size, boolean refresh, boolean isLoadMore, String password);
    }
}
