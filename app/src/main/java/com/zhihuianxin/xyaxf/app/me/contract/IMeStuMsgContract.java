package com.zhihuianxin.xyaxf.app.me.contract;

import com.axinfu.modellib.thrift.customer.VerifyField;
import com.axinfu.modellib.thrift.ecard.ECard;
import com.axinfu.modellib.thrift.ecard.ECardAccount;
import com.axinfu.modellib.thrift.fee.FeeAccount;
import com.zhihuianxin.xyaxf.app.BasePresenter;
import com.zhihuianxin.xyaxf.app.BaseView;

import java.util.ArrayList;

/**
 * Created by Vincent on 2016/11/10.
 */

public interface IMeStuMsgContract {
    interface IMeStuMsgView extends BaseView<IMeStuMsgPresenter>{
        void modifyECardAccountSuccess(ECardAccount ecArdAccount, ECard eCard);

        void modifyStuNoSuccess(FeeAccount feeAccount);
        void modifyIDSuccess(FeeAccount feeAccount);
        void getmodifyPwdInfoResult(ArrayList<VerifyField> verify_fields);
    }

    interface IMeStuMsgPresenter extends BasePresenter{
        void modifyECardAccount(String account_no, String ecard_password, String login_password);

        void modifyStuNo(String student_no, String id_card_no, String login_password);
        void modifyID(String student_no, String id_card_no, String login_password);
        void getmodifyPwdInfo(String mobile);
    }
}
