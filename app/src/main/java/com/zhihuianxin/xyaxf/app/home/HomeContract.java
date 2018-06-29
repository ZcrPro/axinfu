package com.zhihuianxin.xyaxf.app.home;

import modellib.thrift.customer.Customer;
import modellib.thrift.message.Advertise;
import modellib.thrift.ocp.OcpAccount;
import modellib.thrift.unqr.UPBankCard;
import com.zhihuianxin.xyaxf.App;
import com.zhihuianxin.xyaxf.app.BasePresenter;
import com.zhihuianxin.xyaxf.app.BaseView;
import com.zhihuianxin.xyaxf.app.axxyf.AxxyfActivity;

import java.util.List;

import io.realm.RealmList;

/**
 * Created by zcrpro on 2016/11/4.
 */
public interface HomeContract {

    interface HomeView extends BaseView<HomePresenter> {
        void customerSuccess(Customer customer);
        void customerFailure();
        void bannerSuccess(List<Advertise> list);
        void getBankCardResult(RealmList<UPBankCard> bankCards);
        void getProtocolByNoSuccess(List<App.Protocol> protocols);
        void getAccountPayMethodInfoSuccess(AxxyfActivity.LoanAccountInfoRep rep);
        void getBanklimitSuccess(com.zhihuianxin.xyaxf.app.home.HomePresenter.BankResponse bankResponse);
        void getOcpAccountStatusSuccess(Customer customer, OcpAccount ocpAccount);
        void getEcardDataSuccess(com.zhihuianxin.xyaxf.app.home.HomePresenter.EcardResponse ecardResponse);
    }

    interface  HomePresenter extends BasePresenter {
        void loadBannerData();
        void loadCustomerData();
        void getBankCard();
        void getProtocolByNo();
        void getAccountPayMethodInfo();
        void getBanklimit();
        void getOcpAccountStatus(Customer customer);
        void getEcardData();
    }
}
