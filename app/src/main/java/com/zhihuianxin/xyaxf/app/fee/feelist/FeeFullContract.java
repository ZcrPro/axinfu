package com.zhihuianxin.xyaxf.app.fee.feelist;

import com.axinfu.modellib.thrift.fee.FeeRecord;
import com.zhihuianxin.xyaxf.app.BasePresenter;
import com.zhihuianxin.xyaxf.app.BaseView;

import java.util.List;

/**
 * Created by zcrpro on 2016/11/13.
 */
public interface FeeFullContract {

    interface FeeFullView extends BaseView<FeeFullPresenter> {
        void feeFullSuccess(List<FeeRecord> fee_records, boolean isLoadMore);
        void feeFullFailure();
    }

    interface FeeFullPresenter extends BasePresenter {
        void loadFeeRecordList(String start_date, String end_date, int page_index, int page_size, boolean isLoadMore);
        void loadOtherFeeRecordList(String name, String other_no, String start_date, String end_date, int page_index, int page_size, boolean isLoadMore);
        void get_new_student_fee_records(String name, String id_card_no, String new_student_no, String start_date, String end_date, int page_index, int page_size, boolean isLoadMore);
    }
}
