package com.zhihuianxin.xyaxf.app.fee.feelist;

import com.axinfu.modellib.thrift.fee.Fee;
import com.axinfu.modellib.thrift.fee.SchoolRoll;
import com.axinfu.modellib.thrift.fee.SubFeeDeduction;
import com.zhihuianxin.xyaxf.app.BasePresenter;
import com.zhihuianxin.xyaxf.app.BaseView;

import java.util.List;

/**
 * Created by zcrpro on 2016/11/10.
 */
public interface FeeNotFullContract {

    interface FeeNotFullView extends BaseView<FeeNotFullPresenter> {
        void feeNotFullSuccess(List<Fee> fees, SchoolRoll school_roll, List<SubFeeDeduction> deductible_fees, String pay_limit_hint);

        void feeNotFullFailure();
    }

    interface FeeNotFullPresenter extends BasePresenter {
        void loadFeeList();
        void loadOtherFeeList(String name, String other_no);
    }
}
