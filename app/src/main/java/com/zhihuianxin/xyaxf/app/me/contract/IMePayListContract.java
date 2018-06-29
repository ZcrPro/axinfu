package com.zhihuianxin.xyaxf.app.me.contract;

import modellib.thrift.fee.PaymentRecord;
import modellib.thrift.unqr.UPQRPayRecord;
import com.xyaxf.axpay.modle.PayFor;
import com.zhihuianxin.xyaxf.app.BasePresenter;
import com.zhihuianxin.xyaxf.app.BaseView;

import io.realm.RealmList;

/**
 * Created by Vincent on 2016/10/24.
 */

public interface IMePayListContract {
    interface IMePayListView extends BaseView<IMePayListPresenter>{
        void setPayClosedList(RealmList<PaymentRecord> payList);
        void setPayList(RealmList<PaymentRecord> payList);
        void setUnionSwepPayList(RealmList<UPQRPayRecord> payList);
    }

    interface IMePayListPresenter extends BasePresenter{
        void loadPayClosedList(String start_date, String end_date, String page_index, String page_size);
        void loadPayList(String start_date, String end_date, String page_index, String page_size, PayFor pay_for);
        void loadUnionSwepPayList(String start_date, String end_date, String page_index, String page_size);
    }
}
