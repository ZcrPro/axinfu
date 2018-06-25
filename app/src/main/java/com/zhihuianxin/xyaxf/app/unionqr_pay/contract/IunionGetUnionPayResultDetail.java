package com.zhihuianxin.xyaxf.app.unionqr_pay.contract;

import com.axinfu.modellib.thrift.unqr.UPQRPayRecord;
import com.zhihuianxin.xyaxf.app.BaseView;

/**
 * Created by Vincent on 2017/12/13.
 */

public class IunionGetUnionPayResultDetail {
    public interface IunionGetUnionPayResultView extends BaseView<IunionGetUnionPayResultPresenter>{
        void getPayResultDeatilResult(UPQRPayRecord pay_record_detail);
    }

    public interface IunionGetUnionPayResultPresenter {
        void getPayResultDeatil(String order_no);
    }
}
