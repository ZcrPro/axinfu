package com.zhihuianxin.xyaxf.app.ecard.bill;//package com.zhihuianxin.xyaxf.app.ecard.bill;
//
//import com.axinfu.modellib.thrift.ecard.ECardChargeRecord;
//import com.zhihuianxin.xyaxf.app.BasePresenter;
//import com.zhihuianxin.xyaxf.app.BaseView;
//
//import java.util.List;
//
///**
// * Created by zcrpro on 2016/11/9.
// */
//public interface EcardBillContract {
//
//    interface EcardBillView extends BaseView<EcardBillPresenter> {
//        void ecardBillSuccess(List<ECardChargeRecord> eCardChargeRecord);
//
//        void ecardBillFailure();
//    }
//
//    interface EcardBillPresenter extends BasePresenter {
//        void loadEccrdBillData(String start_date, String end_date, int page_index);
//    }
//
//}
