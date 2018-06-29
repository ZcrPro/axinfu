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

import com.zhihuianxin.xyaxf.app.AppConstant;
import modellib.service.FeeService;
import modellib.thrift.base.BaseResponse;
import modellib.thrift.business.AccountVerifyItem;
import modellib.thrift.customer.Customer;
import modellib.thrift.fee.Fee;
import modellib.thrift.fee.FeeAccount;
import com.google.gson.Gson;
import com.zhihuianxin.xyaxf.App;
import com.zhihuianxin.xyaxf.BaseRealmFragment;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.ApiFactory;
import com.zhihuianxin.xyaxf.app.BaseSubscriber;
import com.zhihuianxin.xyaxf.app.RetrofitFactory;
import com.zhihuianxin.xyaxf.app.fee.FeeActivity;
import com.zhihuianxin.xyaxf.app.utils.NetUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.realm.AccountVerifyItemRealmProxy;
import io.realm.CustomerRealmProxy;
import io.realm.ECardAccountRealmProxy;
import io.realm.FeeAccountRealmProxy;
import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by zcrpro on 2016/11/28.
 */
public class FeeNewlyFragment extends BaseRealmFragment {

    @InjectView(R.id.tv_other_name)
    TextView tvOtherName;
    @InjectView(R.id.ed_other_name)
    EditText edOtherName;
    @InjectView(R.id.tv_other_id_card)
    TextView tvOtherIdCard;
    @InjectView(R.id.ed_other_other_id_card)
    EditText edOtherOtherIdCard;
    @InjectView(R.id.tv_other_info)
    TextView tvOtherInfo;
    @InjectView(R.id.ed_other_info)
    EditText edOtherInfo;
    @InjectView(R.id.btn_ok)
    Button btnOk;
    @InjectView(R.id.tv_bufen_wa)
    TextView tvBufenWa;
    @InjectView(R.id.ll_wa)
    LinearLayout llWa;
    @InjectView(R.id.new_info)
    LinearLayout newInfo;

    @Override
    protected void initView(View view, Bundle savedInstanceState) {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fee_newly_fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.inject(this, rootView);
        initData();
        return rootView;
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

    String otherIdCard;
    private void initData() {
        //获取信息
        final RealmResults<Customer> customers = realm.where(Customer.class).equalTo("mobile", App.mAxLoginSp.getUserMobil()).findAll();
        if (customers.size() != 0) {


            if (((FeeAccountRealmProxy) (((CustomerRealmProxy) customers.get(0)).realmGet$fee_account())).realmGet$id_card_verified()) {
                edOtherOtherIdCard.setText(getIDValue(((FeeAccountRealmProxy) (((CustomerRealmProxy) customers.get(0)).realmGet$fee_account())).realmGet$id_card_no()));
                edOtherOtherIdCard.setEnabled(false);
            } else {
                edOtherOtherIdCard.setText(((FeeAccountRealmProxy) (((CustomerRealmProxy) customers.get(0)).realmGet$fee_account())).realmGet$id_card_no());
                edOtherOtherIdCard.setEnabled(true);
            }


//            edOtherOtherIdCard.setText(((FeeAccountRealmProxy) (((CustomerRealmProxy) customers.get(0)).realmGet$fee_account())).realmGet$id_card_no());
//            if (TextUtils.isEmpty(edOtherOtherIdCard.getText().toString())){
//                edOtherOtherIdCard.setEnabled(true);
//                edOtherOtherIdCard.setBackgroundResource(R.drawable.payment_check_edittext);
//            }else {
//                edOtherOtherIdCard.setEnabled(false);
//                edOtherOtherIdCard.setBackgroundResource(R.drawable.payment_not_check_edittext);
//            }

            //获取新生缴费的记录
            if (((ECardAccountRealmProxy) (((CustomerRealmProxy) customers.get(0)).realmGet$ecard_account())).realmGet$name() != null) {
                edOtherName.setText(((ECardAccountRealmProxy) (((CustomerRealmProxy) customers.get(0)).realmGet$ecard_account())).realmGet$name());
//                edOtherName.setEnabled(false);
//                edOtherName.setBackgroundResource(R.drawable.payment_not_check_edittext);
            } else if (((FeeAccountRealmProxy) (((CustomerRealmProxy) customers.get(0)).realmGet$fee_account())).realmGet$name() != null) {
                edOtherName.setText(((FeeAccountRealmProxy) (((CustomerRealmProxy) customers.get(0)).realmGet$fee_account())).realmGet$name());
//                edOtherName.setEnabled(false);
//                edOtherName.setBackgroundResource(R.drawable.payment_not_check_edittext);
            } else {

            }

            //加载需要验证的信息
            //取出需要显示的验证项目
            final AccountVerifyItem accountVerifyItem = ((AccountVerifyItemRealmProxy) ((FeeAccountRealmProxy) ((CustomerRealmProxy) customers.get(0)).realmGet$fee_account()).realmGet$new_stu_verify_config());
            if (accountVerifyItem != null) {
                tvOtherInfo.setText(((AccountVerifyItemRealmProxy) accountVerifyItem).realmGet$title());
                edOtherInfo.setHint(((AccountVerifyItemRealmProxy) accountVerifyItem).realmGet$hint());
            }

            if (!((FeeAccountRealmProxy) (((CustomerRealmProxy) customers.get(0)).realmGet$fee_account())).realmGet$status().equals("OK") &&
                    !TextUtils.isEmpty(((FeeAccountRealmProxy) (((CustomerRealmProxy) customers.get(0)).realmGet$fee_account())).realmGet$new_student_fee_hint())) {
                llWa.setVisibility(View.VISIBLE);
                tvBufenWa.setText(((FeeAccountRealmProxy) (((CustomerRealmProxy) customers.get(0)).realmGet$fee_account())).realmGet$new_student_fee_hint());
            } else {
                llWa.setVisibility(View.GONE);
            }

            //是否显示新生入学需要的必填项目
            if (((FeeAccountRealmProxy) (((CustomerRealmProxy) customers.get(0)).realmGet$fee_account())).realmGet$new_stu_verify_config() != null) {
                newInfo.setVisibility(View.VISIBLE);
                tvOtherInfo.setText(((AccountVerifyItemRealmProxy) ((FeeAccountRealmProxy) (((CustomerRealmProxy) customers.get(0)).realmGet$fee_account())).realmGet$new_stu_verify_config()).realmGet$title());
                edOtherInfo.setHint(((AccountVerifyItemRealmProxy) ((FeeAccountRealmProxy) (((CustomerRealmProxy) customers.get(0)).realmGet$fee_account())).realmGet$new_stu_verify_config()).realmGet$hint());
            } else {
                newInfo.setVisibility(View.GONE);
            }

        }

        btnOk.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //如果存在新生缴费的其他信息
                        if (newInfo.getVisibility() == View.VISIBLE) {
                            final String otherName = edOtherName.getText().toString().trim();

                            if (((FeeAccountRealmProxy) (((CustomerRealmProxy) customers.get(0)).realmGet$fee_account())).realmGet$id_card_verified()) {
                                otherIdCard = ((FeeAccountRealmProxy) (((CustomerRealmProxy) customers.get(0)).realmGet$fee_account())).realmGet$id_card_no();
                            }else {
                                otherIdCard = edOtherOtherIdCard.getText().toString().trim();
                            }

//                            final String otherIdCard = edOtherOtherIdCard.getText().toString().trim();

                            final String otherInfo = edOtherInfo.getText().toString().trim();
                            if (TextUtils.isEmpty(otherInfo)) {
                                Toast.makeText(getActivity(), " 请输入" + ((AccountVerifyItemRealmProxy) ((FeeAccountRealmProxy) (((CustomerRealmProxy) customers.get(0)).realmGet$fee_account())).realmGet$new_stu_verify_config()).realmGet$title(), Toast.LENGTH_SHORT).show();
                            } else {
                                if (TextUtils.isEmpty(otherName) || TextUtils.isEmpty(otherIdCard)) {
                                    Toast.makeText(getActivity(), "请将信息填写完整", Toast.LENGTH_SHORT).show();
                                } else if (!TextUtils.isEmpty(otherName) && !TextUtils.isEmpty(otherIdCard) && !TextUtils.isEmpty(otherInfo)) {
                                        openNewStudent(otherName, otherIdCard, otherInfo);
                                } else {
                                        openNewStudent(otherName, otherIdCard);
                                }
                            }

                        } else {
                            //不存在就不提交
                            final String otherName = edOtherName.getText().toString().trim();
                            final String otherIdCard = edOtherOtherIdCard.getText().toString().trim();
                            if (TextUtils.isEmpty(otherName) || TextUtils.isEmpty(otherIdCard)) {
                                Toast.makeText(getActivity(), "请将信息填写完整", Toast.LENGTH_SHORT).show();
                            } else {
                                    openNewStudent(otherName, otherIdCard);
                            }
                        }
                    }
                }
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    /**
     * 开通新生缴费
     */
    private void openNewStudent(final String name, final String id_card_no) {
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("id_card_no", id_card_no);
        map.put("new_student_no", "");
        FeeService feeService = ApiFactory.getFactory().create(FeeService.class);
        feeService.getNewStudentFees(NetUtils.getRequestParams(getActivity(), map), NetUtils.getSign(NetUtils.getRequestParams(getActivity(), map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(getActivity(), true, null) {
                    @Override
                    public void onNext(Object o) {
                        try {
                            final PaymentCheckResponse paymentCheckResponse = new Gson().fromJson(o.toString(), PaymentCheckResponse.class);
                            if (paymentCheckResponse.pay_limit_hint != null) {
                                App.hint = paymentCheckResponse.pay_limit_hint;
                            } else {
                                App.hint = null;
                            }
                            realm.executeTransactionAsync(new Realm.Transaction() {
                                @Override
                                public void execute(Realm bgRealm) {
                                    FeeAccount feeAccount = new FeeAccount();
                                    feeAccount.name = name;
                                    feeAccount.id_card_no = id_card_no;
                                    bgRealm.copyToRealmOrUpdate(feeAccount);
                                }
                            }, new Realm.Transaction.OnSuccess() {
                                @Override
                                public void onSuccess() {
                                    Bundle bundle = new Bundle();
                                    bundle.putString(FeeActivity.ENTER_FLAG, FeeActivity.other);
                                    bundle.putBoolean("new_student", true);
                                    bundle.putBoolean("sup", paymentCheckResponse.account.support_required_fee);
                                    Intent intent = new Intent(getActivity(), FeeActivity.class);
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                    getActivity().finish();
                                }
                            }, new Realm.Transaction.OnError() {
                                @Override
                                public void onError(Throwable error) {
                                    Toast.makeText(getActivity(), "验证缴费信息失败,请稍后再试!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(mActivity, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * 开通新生缴费
     */
    private void openNewStudent(final String name, final String id_card_no, final String student_no) {
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("id_card_no", id_card_no);
        map.put("new_student_no", student_no);
        FeeService feeService = ApiFactory.getFactory().create(FeeService.class);
        feeService.getNewStudentFees(NetUtils.getRequestParams(getActivity(), map), NetUtils.getSign(NetUtils.getRequestParams(getActivity(), map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(getActivity(), true, null) {
                    @Override
                    public void onNext(Object o) {
                        try {
                            final PaymentCheckResponse paymentCheckResponse = new Gson().fromJson(o.toString(), PaymentCheckResponse.class);
                            if (paymentCheckResponse.pay_limit_hint != null) {
                                App.hint = paymentCheckResponse.pay_limit_hint;
                            } else {
                                App.hint = null;
                            }
                            realm.executeTransactionAsync(new Realm.Transaction() {
                                @Override
                                public void execute(Realm bgRealm) {
                                    FeeAccount feeAccount = new FeeAccount();
                                    feeAccount.name = name;
                                    feeAccount.id_card_no = id_card_no;
                                    feeAccount.other_no = student_no;
                                    feeAccount.support_required_fee = paymentCheckResponse.account.support_required_fee;
                                    bgRealm.copyToRealmOrUpdate(feeAccount);
                                }
                            }, new Realm.Transaction.OnSuccess() {
                                @Override
                                public void onSuccess() {
                                    Bundle bundle = new Bundle();
                                    bundle.putString(FeeActivity.ENTER_FLAG, FeeActivity.other);
                                    Intent intent = new Intent(getActivity(), FeeActivity.class);
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                    getActivity().finish();
                                }
                            }, new Realm.Transaction.OnError() {
                                @Override
                                public void onError(Throwable error) {
                                    Toast.makeText(getActivity(), "验证缴费信息失败,请稍后再试!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(mActivity, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public static class PaymentCheckResponse extends RealmObject {
        public BaseResponse resp;
        public FeeAccount account;
        public List<Fee> fees;
        public String pay_limit_hint;
    }
}
