package com.zhihuianxin.xyaxf.app.unionqr_pay.contract;

import modellib.thrift.unqr.UPBankCard;
import com.zhihuianxin.xyaxf.app.BasePresenter;
import com.zhihuianxin.xyaxf.app.BaseView;

import io.realm.RealmList;

/**
 * Created by Vincent on 2017/11/15.
 */

public interface IunionBindCardContract {
    interface IbindCard extends BaseView<IbindCardPresenter> {
        void bingUPQRBankCardResult(String add_card_url);
        void getBankCardResult(RealmList<UPBankCard> bankCards);
    }

    interface IbindCardPresenter extends BasePresenter {
        void bingUPQRBankCard();
        void getBankCard();
    }
}
