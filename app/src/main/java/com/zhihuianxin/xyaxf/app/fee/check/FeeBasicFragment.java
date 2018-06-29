package com.zhihuianxin.xyaxf.app.fee.check;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import modellib.thrift.customer.Customer;
import modellib.thrift.fee.FeeAccount;
import com.zhihuianxin.xyaxf.app.base.axutil.SchedulerProvider;
import com.zhihuianxin.xyaxf.app.base.axutil.Util;
import com.zhihuianxin.xyaxf.App;
import com.zhihuianxin.xyaxf.BaseRealmFragment;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.fee.FeeActivity;
import com.zhihuianxin.xyaxf.app.utils.RxBus;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.realm.CustomerRealmProxy;
import io.realm.ECardAccountRealmProxy;
import io.realm.FeeAccountRealmProxy;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by zcrpro on 2016/11/28.
 */
public class FeeBasicFragment extends BaseRealmFragment implements FeeCheckContract.PaymentCheckView {
    @InjectView(R.id.tv_normal_name)
    TextView tvNormalName;
    @InjectView(R.id.ed_normal_name)
    EditText edNormalName;
    @InjectView(R.id.tv_normal_idcard)
    TextView tvNormalIdcard;
    @InjectView(R.id.ed_normal_idcard)
    EditText edNormalIdcard;
    @InjectView(R.id.tv_normal_student_no)
    TextView tvNormalStudentNo;
    @InjectView(R.id.ed_normal_student_no)
    EditText edNormalStudentNo;
    @InjectView(R.id.btn_ok)
    Button btnOk;
    @InjectView(R.id.tv_bufen_wa)
    TextView tvBufenWa;
    @InjectView(R.id.ll_wa)
    LinearLayout llWa;

    private FeeCheckPresenter feeCheckPresenter;
    private FeeCheckContract.PaymentCheckPresenter presenter;

    private RealmResults<Customer> customers;

    @Override
    protected void initView(View view, Bundle savedInstanceState) {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fee_basic_fragment;
    }


    private String getIDValue(String oriID) {
        if (oriID != null) {
            if (oriID.length() != 18) {
                return oriID;
            } else {
                return oriID.substring(0, 2) + "**************" + oriID.substring(16);
            }
        } else {
            return null;
        }
    }

    String idCard;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.inject(this, rootView);
        feeCheckPresenter = new FeeCheckPresenter(this, getActivity(), SchedulerProvider.getInstance());

        customers = realm.where(Customer.class).equalTo("mobile", App.mAxLoginSp.getUserMobil()).findAll();
        if (customers.size() != 0) {
            if (((ECardAccountRealmProxy) (((CustomerRealmProxy) customers.get(0)).realmGet$ecard_account())).realmGet$name() != null) {
                edNormalName.setText(((ECardAccountRealmProxy) (((CustomerRealmProxy) customers.get(0)).realmGet$ecard_account())).realmGet$name());
            } else {
                edNormalName.setText(((FeeAccountRealmProxy) (((CustomerRealmProxy) customers.get(0)).realmGet$fee_account())).realmGet$name());
            }

            if (((FeeAccountRealmProxy) (((CustomerRealmProxy) customers.get(0)).realmGet$fee_account())).realmGet$id_card_verified()) {
                edNormalIdcard.setText(getIDValue(((FeeAccountRealmProxy) (((CustomerRealmProxy) customers.get(0)).realmGet$fee_account())).realmGet$id_card_no()));
                edNormalIdcard.setEnabled(false);
            } else {
                edNormalIdcard.setText(((FeeAccountRealmProxy) (((CustomerRealmProxy) customers.get(0)).realmGet$fee_account())).realmGet$id_card_no());
                edNormalIdcard.setEnabled(true);
            }

            if (((FeeAccountRealmProxy) (((CustomerRealmProxy) customers.get(0)).realmGet$fee_account())).realmGet$student_no() != null)
                edNormalStudentNo.setText(((FeeAccountRealmProxy) (((CustomerRealmProxy) customers.get(0)).realmGet$fee_account())).realmGet$student_no());


            btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String name = edNormalName.getText().toString().trim();
                    if (((FeeAccountRealmProxy) (((CustomerRealmProxy) customers.get(0)).realmGet$fee_account())).realmGet$id_card_verified()) {
                         idCard = ((FeeAccountRealmProxy) (((CustomerRealmProxy) customers.get(0)).realmGet$fee_account())).realmGet$id_card_no();
                    }else {
                         idCard = edNormalIdcard.getText().toString().trim();
                    }
                    String studentNo = edNormalStudentNo.getText().toString().trim();
                    if (((FeeAccountRealmProxy) (((CustomerRealmProxy) customers.get(0)).realmGet$fee_account())).realmGet$status().equals("OK")) {
                        if (Util.isEmpty(((FeeAccountRealmProxy) (((CustomerRealmProxy) customers.get(0)).realmGet$fee_account())).realmGet$student_no()) && !TextUtils.isEmpty(studentNo)) {
                            presenter.openPaymentAccount(name, studentNo, idCard);
                        } else if (Util.isEmpty(((FeeAccountRealmProxy) (((CustomerRealmProxy) customers.get(0)).realmGet$fee_account())).realmGet$student_no()) && TextUtils.isEmpty(studentNo)) {
                            Bundle bundle = new Bundle();
                            bundle.putString(FeeActivity.ENTER_FLAG, FeeActivity.normal);
                            Intent intent = new Intent(getActivity(), FeeActivity.class);
                            intent.putExtras(bundle);
                            startActivity(intent);
                            getActivity().finish();
                        }
                    } else {
                        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(idCard)) {
                            if (!TextUtils.isEmpty(studentNo)) {
                                presenter.openPaymentAccount(name, studentNo, idCard);
                            } else {
                                presenter.openPaymentAccount(name, idCard);
                            }
                        } else {
                            Toast.makeText(getActivity(), "请将信息填写完整", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

            if (!((FeeAccountRealmProxy) (((CustomerRealmProxy) customers.get(0)).realmGet$fee_account())).realmGet$status().equals("OK") &&
                    !TextUtils.isEmpty(((FeeAccountRealmProxy) (((CustomerRealmProxy) customers.get(0)).realmGet$fee_account())).realmGet$open_fee_account_hint())) {
                llWa.setVisibility(View.VISIBLE);
                tvBufenWa.setText(((FeeAccountRealmProxy) (((CustomerRealmProxy) customers.get(0)).realmGet$fee_account())).realmGet$open_fee_account_hint());
            } else {
                llWa.setVisibility(View.GONE);
            }
        }

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void paymentCheckSuccess(final FeeAccount feeAccount) {
        //存储一下
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                bgRealm.copyToRealmOrUpdate(feeAccount);
            }
        });

        Bundle bundle = new Bundle();
        if (!feeAccount.status.equals("OK")) {
            bundle.putString(FeeActivity.ENTER_FEE_FLAG, FeeActivity.defeat);
        }
        bundle.putString(FeeActivity.ENTER_FLAG, FeeActivity.normal);
        Intent intent = new Intent(getActivity(), FeeActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
        getActivity().finish();
        RxBus.getDefault().send("fee_refresh");
    }

    @Override
    public void paymentCheckFailure() {

    }

    @Override
    public void setPresenter(FeeCheckContract.PaymentCheckPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void loadStart() {

    }

    @Override
    public void loadError(String errorMsg) {

    }

    @Override
    public void loadComplete() {

    }
}
