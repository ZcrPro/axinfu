package com.zhihuianxin.xyaxf.app.fee.feelist.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zhihuianxin.xyaxf.app.AppConstant;
import modellib.service.FeeService;
import modellib.thrift.base.BaseResponse;
import modellib.thrift.base.PayMethod;
import modellib.thrift.customer.Customer;
import modellib.thrift.fee.Fee;
import modellib.thrift.fee.FeeAccount;
import modellib.thrift.fee.SchoolRoll;
import modellib.thrift.fee.SubFee;
import modellib.thrift.fee.SubFeeDeduction;
import modellib.thrift.fee.SubFeeItem;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.zhihuianxin.xyaxf.app.base.axutil.SchedulerProvider;
import com.zhihuianxin.xyaxf.App;
import com.zhihuianxin.xyaxf.BaseRealmFragment;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.ApiFactory;
import com.zhihuianxin.xyaxf.app.BaseSubscriber;
import com.zhihuianxin.xyaxf.app.RetrofitFactory;
import com.zhihuianxin.xyaxf.app.fee.FeeActivity;
import com.zhihuianxin.xyaxf.app.fee.FeeDetailActivity;
import com.zhihuianxin.xyaxf.app.fee.adapter.FeeNotFullAdapter;
import com.zhihuianxin.xyaxf.app.fee.feelist.FeeNotFullContract;
import com.zhihuianxin.xyaxf.app.fee.feelist.FeeNotFullPresenter;
import com.zhihuianxin.xyaxf.app.utils.NetUtils;
import com.zhihuianxin.xyaxf.app.utils.RxBus;
import com.zhihuianxin.xyaxf.app.view.GifView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import io.realm.CustomerRealmProxy;
import io.realm.FeeAccountRealmProxy;
import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by zcrpro on 2016/10/19.
 * 暂时未完成
 */

public class FeeNotFulfilFragment extends BaseRealmFragment implements FeeNotFullContract.FeeNotFullView {
    public static final int CHECK_FEE_ITEM = 1000;
    public static final String EXTRA_CHECK_DATA = "check_data";

    @InjectView(R.id.null_data)
    TextView mNullText;
    @InjectView(R.id.recyclerview)
    RecyclerView recyclerview;
    @InjectView(R.id.swiperefreshlayout)
    SwipeRefreshLayout swiperefreshlayout;
    @InjectView(R.id.rl_null)
    RelativeLayout rlNull;
    @InjectView(R.id.iv_null)
    ImageView ivNull;
    @InjectView(R.id.gif_view)
    GifView gifView;
    @InjectView(R.id.text)
    TextView text;
    @InjectView(R.id.loading)
    LinearLayout loading;
    @InjectView(R.id.ll_not_found)
    LinearLayout llNotFound;
    private FeeNotFullAdapter feeNotFullAdapter;

    private FeeNotFullContract.FeeNotFullPresenter presenter;
    private FeeNotFullPresenter feeNotFullPresenter;
    private String channal;

    private Subscription rxSbscription;

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fee_not_fulfil_fragment;
    }

    @Override
    public void feeNotFullSuccess(final List<Fee> fees, final SchoolRoll school_roll, final List<SubFeeDeduction> deductible_fees, String pay_limit_hint) {
        if (fees.isEmpty()) {
            if (rlNull != null)
                rlNull.setVisibility(View.VISIBLE);
            if (recyclerview != null)
                recyclerview.setVisibility(View.GONE);
            rlNull.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivityForResult(new Intent(getActivity(), FeeNotFulfilSearchFeeActivity.class), CHECK_FEE_ITEM);
                }
            });
        } else {
            setDbToUI(fees);
            if (rlNull != null)
                rlNull.setVisibility(View.GONE);
            if (recyclerview != null)
                recyclerview.setVisibility(View.VISIBLE);
        }

        //存入数据库
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                bgRealm.copyToRealmOrUpdate(fees);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                // Transaction was a success.
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                // Transaction failed and was automatically canceled.
            }
        });


        if (school_roll == null) {
        } else {
            //存入数据库
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm bgRealm) {
                    school_roll.mobile = App.mAxLoginSp.getUserMobil();
                    bgRealm.copyToRealmOrUpdate(school_roll);
                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {
                    // Transaction was a success.
                }
            }, new Realm.Transaction.OnError() {
                @Override
                public void onError(Throwable error) {
                    // Transaction failed and was automatically canceled.
                }
            });
        }

        //先移除所有的费用列表
        App.subFeeDeductionHashMap.clear();

        if (deductible_fees != null || deductible_fees.size() > 0) {
            for (int i = 0; i < deductible_fees.size(); i++) {
                SubFeeItem s = new SubFeeItem();
                s.amount = deductible_fees.get(i).amount;
                s.id = deductible_fees.get(i).id;
                s.title = deductible_fees.get(i).title;
                s.loan_amt = deductible_fees.get(i).loan_amt;
                s.min_pay_amount = deductible_fees.get(i).min_pay_amount;
                s.is_priority = deductible_fees.get(i).is_priority;
                s.deduct_amount = deductible_fees.get(i).deduct_amount;
                s.paid_amount = deductible_fees.get(i).paid_amount;
                s.refund_amount = deductible_fees.get(i).refund_amount;
                s.business_channel = deductible_fees.get(i).business_channel;
                s.year = deductible_fees.get(i).year;
                s.total_amount = deductible_fees.get(i).total_amount;
                s.isSelect = true;
                App.subFeeDeductionHashMap.put(s.id, s);
            }
        }

        if (pay_limit_hint != null) {
            App.hint = pay_limit_hint;
        } else {
            App.hint = null;
        }
    }

    @OnClick(R.id.rl_null)
    public void onBtnNullRLClick() {
        startActivityForResult(new Intent(getActivity(), FeeNotFulfilSearchFeeActivity.class), CHECK_FEE_ITEM);
    }

    private void setDbToUI(final List<Fee> fees) {
        feeNotFullAdapter = new FeeNotFullAdapter(fees, R.layout.fee_no_fulfil_item);
        if (getActivity() != null)
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (fees.size() > 4) {
                        View footView = LayoutInflater.from(getActivity()).inflate(R.layout.fee_not_fulfil_fragment_bottom, null);
                        footView.setOnClickListener(footerViewClickListener);
                        feeNotFullAdapter.addFooterView(footView);
                        feeNotFullAdapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
                            @Override
                            public void onItemClick(View view, int i) {
                                //进入缴费详情界面
                                Bundle bundle = new Bundle();
                                bundle.putString(FeeDetailActivity.FEE_ID, ((Fee) view.getTag()).id);
                                bundle.putSerializable(FeeDetailActivity.FEE_WAY, (ArrayList<PayMethod>) ((Fee) view.getTag()).pay_methods);
                                Intent intent = new Intent(getActivity(), FeeDetailActivity.class);
                                intent.putExtras(bundle);
                                getActivity().startActivity(intent);
                            }
                        });
                        recyclerview.setAdapter(feeNotFullAdapter);
                        feeNotFullAdapter.notifyDataSetChanged();
                    } else {
//                        View footView = LayoutInflater.from(getActivity()).inflate(R.layout.fee_not_fulfil_fragment_bottom, null);
//                        footView.setOnClickListener(footerViewClickListener);
//                        feeNotFullAdapter.addFooterView(footView);
                        feeNotFullAdapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
                            @Override
                            public void onItemClick(View view, int i) {
                                //进入缴费详情界面
                                Bundle bundle = new Bundle();
                                bundle.putString(FeeDetailActivity.FEE_ID, ((Fee) view.getTag()).id);
                                bundle.putSerializable(FeeDetailActivity.FEE_WAY, (ArrayList<PayMethod>) ((Fee) view.getTag()).pay_methods);
                                Intent intent = new Intent(getActivity(), FeeDetailActivity.class);
                                intent.putExtras(bundle);
                                getActivity().startActivity(intent);
                            }
                        });
                        recyclerview.setAdapter(feeNotFullAdapter);
                        feeNotFullAdapter.notifyDataSetChanged();
                        if (llNotFound != null)
                            llNotFound.setVisibility(View.VISIBLE);
                        llNotFound.setOnClickListener(footerViewClickListener);
                    }
                }
            });
    }

    View.OnClickListener footerViewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivityForResult(new Intent(getActivity(), FeeNotFulfilSearchFeeActivity.class), CHECK_FEE_ITEM);
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHECK_FEE_ITEM && resultCode == getActivity().RESULT_OK) {
            if (loading != null)
                loading.setVisibility(View.VISIBLE);
            if (gifView != null)
                gifView.setMovieResource(R.raw.gif_loading);
            presenter.loadOtherFeeList(((FeeAccountRealmProxy) ((CustomerRealmProxy) realm.where(Customer.class)
                    .equalTo("mobile", App.mAxLoginSp.getUserMobil()).findAll().get(0)).realmGet$fee_account())
                    .realmGet$name(), data.getExtras().getString(EXTRA_CHECK_DATA));
        }
    }

    @Override
    public void feeNotFullFailure() {

    }

    @Override
    public void setPresenter(FeeNotFullContract.FeeNotFullPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void loadStart() {

    }

    @Override
    public void loadError(String errorMsg) {
        if (rlNull == null) {
            return;
        }
        rlNull.setVisibility(View.VISIBLE);
        recyclerview.setVisibility(View.GONE);
        rlNull.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getActivity(), FeeNotFulfilSearchFeeActivity.class), CHECK_FEE_ITEM);
            }
        });
        if (loading != null)
            loading.setVisibility(View.GONE);
    }

    @Override
    public void loadComplete() {
        if (swiperefreshlayout != null)
            swiperefreshlayout.setRefreshing(false);

        if (loading != null)
            loading.setVisibility(View.GONE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!rxSbscription.isUnsubscribed()) {
            rxSbscription.unsubscribe();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.inject(this, rootView);
        recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        loadingData();
        rxSbscription = RxBus.getDefault().toObservable().subscribe(new Action1<Object>() {
            @Override
            public void call(Object event) {
                if (event.equals("succeed2")) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            loadingData();
                        }
                    }, 100);
                }
            }
        });
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        gifView.clearAnimation();
//        gifView = null;
    }

    /**
     * 开通新生缴费
     */
    private void openNewStudent(String name, String id_card_no, String student_no) {
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("id_card_no", id_card_no);
        map.put("new_student_no", student_no);
        FeeService feeService = ApiFactory.getFactory().create(FeeService.class);
        feeService.getNewStudentFees(NetUtils.getRequestParams(getActivity(), map), NetUtils.getSign(NetUtils.getRequestParams(getActivity(), map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(getActivity(), true, this) {

                    @Override
                    public void onNext(Object o) {
                        final PaymentCheckResponse paymentCheckResponse = new Gson().fromJson(o.toString(), PaymentCheckResponse.class);
                        if (paymentCheckResponse.pay_limit_hint!=null){
                            App.hint=paymentCheckResponse.pay_limit_hint;
                        }else {
                            App.hint=null;
                        }
                        realm.executeTransactionAsync(new Realm.Transaction() {
                            @Override
                            public void execute(Realm bgRealm) {
                                bgRealm.copyToRealmOrUpdate(paymentCheckResponse.account);
                            }
                        }, new Realm.Transaction.OnSuccess() {
                            @Override
                            public void onSuccess() {
                            }
                        }, new Realm.Transaction.OnError() {
                            @Override
                            public void onError(Throwable error) {
                                Toast.makeText(getActivity(), "验证缴费信息失败,请稍后再试!", Toast.LENGTH_SHORT).show();
                            }
                        });

                        if (paymentCheckResponse.fees.isEmpty()) {
                            rlNull.setVisibility(View.VISIBLE);
                            recyclerview.setVisibility(View.GONE);
                            rlNull.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    startActivityForResult(new Intent(getActivity(), FeeNotFulfilSearchFeeActivity.class), CHECK_FEE_ITEM);
                                }
                            });
                        } else {
                            setDbToUI(paymentCheckResponse.fees);
                            rlNull.setVisibility(View.GONE);
                            recyclerview.setVisibility(View.VISIBLE);
                        }

                        //存入数据库
                        realm.executeTransactionAsync(new Realm.Transaction() {
                            @Override
                            public void execute(Realm bgRealm) {
                                bgRealm.copyToRealmOrUpdate(paymentCheckResponse.fees);
                            }
                        }, new Realm.Transaction.OnSuccess() {
                            @Override
                            public void onSuccess() {
                                // Transaction was a success.
                            }
                        }, new Realm.Transaction.OnError() {
                            @Override
                            public void onError(Throwable error) {
                                // Transaction failed and was automatically canceled.
                            }
                        });

                        if (paymentCheckResponse.school_roll == null) {
                        } else {
                            //存入数据库
                            realm.executeTransactionAsync(new Realm.Transaction() {
                                @Override
                                public void execute(Realm bgRealm) {
                                    paymentCheckResponse.school_roll.mobile = App.mAxLoginSp.getUserMobil();
                                    bgRealm.copyToRealmOrUpdate(paymentCheckResponse.school_roll);
                                }
                            }, new Realm.Transaction.OnSuccess() {
                                @Override
                                public void onSuccess() {
                                    // Transaction was a success.
                                }
                            }, new Realm.Transaction.OnError() {
                                @Override
                                public void onError(Throwable error) {
                                    // Transaction failed and was automatically canceled.
                                }
                            });
                        }

                        //先移除所有的费用列表
                        App.subFeeDeductionHashMap.clear();

                        if (paymentCheckResponse.deductible_fees != null || paymentCheckResponse.deductible_fees.size() > 0) {
                            for (int i = 0; i < paymentCheckResponse.deductible_fees.size(); i++) {
                                SubFeeItem s = new SubFeeItem();
                                s.amount = paymentCheckResponse.deductible_fees.get(i).amount;
                                s.id = paymentCheckResponse.deductible_fees.get(i).id;
                                s.id = paymentCheckResponse.deductible_fees.get(i).loan_amt;
                                s.title = paymentCheckResponse.deductible_fees.get(i).title;
                                s.min_pay_amount = paymentCheckResponse.deductible_fees.get(i).min_pay_amount;
                                s.is_priority = paymentCheckResponse.deductible_fees.get(i).is_priority;
                                s.deduct_amount = paymentCheckResponse.deductible_fees.get(i).deduct_amount;
                                s.paid_amount = paymentCheckResponse.deductible_fees.get(i).paid_amount;
                                s.refund_amount = paymentCheckResponse.deductible_fees.get(i).refund_amount;
                                s.business_channel = paymentCheckResponse.deductible_fees.get(i).business_channel;
                                s.year = paymentCheckResponse.deductible_fees.get(i).year;
                                s.total_amount = paymentCheckResponse.deductible_fees.get(i).total_amount;
                                s.isSelect = true;
                                App.subFeeDeductionHashMap.put(s.id, s);
                            }
                        }

                    }
                });
    }


    /**
     * 开通新生缴费
     */
    private void openNewStudent(String name, String id_card_no) {
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("id_card_no", id_card_no);
        map.put("new_student_no", "");
        FeeService feeService = ApiFactory.getFactory().create(FeeService.class);
        feeService.getNewStudentFees(NetUtils.getRequestParams(getActivity(), map), NetUtils.getSign(NetUtils.getRequestParams(getActivity(), map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(getActivity(), true, this) {

                    @Override
                    public void onNext(Object o) {
                        final PaymentCheckResponse paymentCheckResponse = new Gson().fromJson(o.toString(), PaymentCheckResponse.class);
                        if (paymentCheckResponse.pay_limit_hint!=null){
                            App.hint=paymentCheckResponse.pay_limit_hint;
                        }else {
                            App.hint=null;
                        }
                        if (paymentCheckResponse.fees.isEmpty()) {
                            rlNull.setVisibility(View.VISIBLE);
                            recyclerview.setVisibility(View.GONE);
                            rlNull.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    startActivityForResult(new Intent(getActivity(), FeeNotFulfilSearchFeeActivity.class), CHECK_FEE_ITEM);
                                }
                            });
                        } else {
                            setDbToUI(paymentCheckResponse.fees);
                            rlNull.setVisibility(View.GONE);
                            recyclerview.setVisibility(View.VISIBLE);
                        }


                        //存入数据库
                        realm.executeTransactionAsync(new Realm.Transaction() {
                            @Override
                            public void execute(Realm bgRealm) {
                                bgRealm.copyToRealmOrUpdate(paymentCheckResponse.fees);
                            }
                        }, new Realm.Transaction.OnSuccess() {
                            @Override
                            public void onSuccess() {
                                // Transaction was a success.
                            }
                        }, new Realm.Transaction.OnError() {
                            @Override
                            public void onError(Throwable error) {
                                // Transaction failed and was automatically canceled.
                            }
                        });

                        realm.executeTransactionAsync(new Realm.Transaction() {
                            @Override
                            public void execute(Realm bgRealm) {
                                bgRealm.copyToRealmOrUpdate(paymentCheckResponse.account);
                            }
                        }, new Realm.Transaction.OnSuccess() {
                            @Override
                            public void onSuccess() {
                            }
                        }, new Realm.Transaction.OnError() {
                            @Override
                            public void onError(Throwable error) {
                                Toast.makeText(getActivity(), "验证缴费信息失败,请稍后再试!", Toast.LENGTH_SHORT).show();
                            }
                        });

                        if (paymentCheckResponse.school_roll == null) {
                        } else {
                            //存入数据库
                            realm.executeTransactionAsync(new Realm.Transaction() {
                                @Override
                                public void execute(Realm bgRealm) {
                                    paymentCheckResponse.school_roll.mobile = App.mAxLoginSp.getUserMobil();
                                    bgRealm.copyToRealmOrUpdate(paymentCheckResponse.school_roll);
                                }
                            }, new Realm.Transaction.OnSuccess() {
                                @Override
                                public void onSuccess() {
                                    // Transaction was a success.
                                }
                            }, new Realm.Transaction.OnError() {
                                @Override
                                public void onError(Throwable error) {
                                    // Transaction failed and was automatically canceled.
                                }
                            });
                        }

                        //先移除所有的费用列表
                        App.subFeeDeductionHashMap.clear();

                        if (paymentCheckResponse.deductible_fees != null || paymentCheckResponse.deductible_fees.size() > 0) {
                            for (int i = 0; i < paymentCheckResponse.deductible_fees.size(); i++) {
                                SubFeeItem s = new SubFeeItem();
                                s.amount = paymentCheckResponse.deductible_fees.get(i).amount;
                                s.id = paymentCheckResponse.deductible_fees.get(i).id;
                                s.loan_amt = paymentCheckResponse.deductible_fees.get(i).loan_amt;
                                s.title = paymentCheckResponse.deductible_fees.get(i).title;
                                s.min_pay_amount = paymentCheckResponse.deductible_fees.get(i).min_pay_amount;
                                s.is_priority = paymentCheckResponse.deductible_fees.get(i).is_priority;
                                s.deduct_amount = paymentCheckResponse.deductible_fees.get(i).deduct_amount;
                                s.paid_amount = paymentCheckResponse.deductible_fees.get(i).paid_amount;
                                s.refund_amount = paymentCheckResponse.deductible_fees.get(i).refund_amount;
                                s.business_channel = paymentCheckResponse.deductible_fees.get(i).business_channel;
                                s.year = paymentCheckResponse.deductible_fees.get(i).year;
                                s.total_amount = paymentCheckResponse.deductible_fees.get(i).total_amount;
                                s.isSelect = true;
                                App.subFeeDeductionHashMap.put(s.id, s);
                            }
                        }

                    }
                });
    }

    public static class PaymentCheckResponse extends RealmObject {
        public BaseResponse resp;
        public FeeAccount account;
        public List<Fee> fees;
        public String pay_limit_hint;
        public SchoolRoll school_roll;
        public List<SubFee> deductible_fees;
    }

    private void loadingData() {
        feeNotFullPresenter = new FeeNotFullPresenter(this, getActivity(), SchedulerProvider.getInstance());
        final RealmResults<Customer> customers = realm.where(Customer.class).equalTo("mobile", App.mAxLoginSp.getUserMobil()).findAll();
        if (customers.size() != 0) {
            swiperefreshlayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    final FeeAccount feeAccount = ((CustomerRealmProxy) customers.get(0)).realmGet$fee_account();
                    Bundle bundle = getArguments();//从activity传过来的Bundle
                    if (bundle != null) {
                        if (bundle.getString(FeeActivity.ENTER_FEE_FLAG) != null) {
                            if (swiperefreshlayout != null)
                                swiperefreshlayout.setRefreshing(false);
                            return;
                        }
                    }

                    if (channal.equals(FeeActivity.normal)) {//如果未开通不调用这个接口
                        if (feeAccount != null) {
                            if (!((FeeAccountRealmProxy) feeAccount).realmGet$status().equals("OK")) {
                                if (rlNull != null)
                                    rlNull.setVisibility(View.VISIBLE);
                                if (recyclerview != null)
                                    recyclerview.setVisibility(View.GONE);
                                rlNull.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        startActivityForResult(new Intent(getActivity(), FeeNotFulfilSearchFeeActivity.class), CHECK_FEE_ITEM);
                                    }
                                });
                                if (swiperefreshlayout != null) {
                                    swiperefreshlayout.setRefreshing(false);
                                }

                                /**
                                 * 开通失败不调用次接口
                                 */
                                if (bundle.getString(FeeActivity.ENTER_FEE_FLAG) == null) {
//                                    if (loading!=null)
//                                        loading.setVisibility(View.VISIBLE);
//                                    if (gifView!=null)
//                                        gifView.setMovieResource(R.raw.gif_loading);
                                    presenter.loadFeeList();
                                }

                            } else {
//                                if (loading!=null)
//                                    loading.setVisibility(View.VISIBLE);
//                                if (gifView!=null)
//                                    gifView.setMovieResource(R.raw.gif_loading);
                                presenter.loadFeeList();
                            }
                        }
                    } else {
                        if (((FeeAccountRealmProxy) feeAccount).realmGet$other_no() != null) {
                            openNewStudent(((FeeAccountRealmProxy) feeAccount).realmGet$name(), ((FeeAccountRealmProxy) feeAccount).realmGet$id_card_no(), ((FeeAccountRealmProxy) feeAccount).realmGet$other_no());
                        } else {
                            openNewStudent(((FeeAccountRealmProxy) feeAccount).realmGet$name(), ((FeeAccountRealmProxy) feeAccount).realmGet$id_card_no());
                        }
                    }
                }
            });

            final FeeAccount feeAccount = ((CustomerRealmProxy) customers.get(0)).realmGet$fee_account();
            Bundle bundle = getArguments();//从activity传过来的Bundle
            if (bundle != null) {
                channal = bundle.getString(FeeActivity.ENTER_FLAG);
                assert channal != null;
                if (channal.equals(FeeActivity.normal)) {
                    //如果是绑定进入
                    //如果未开通不调用这个接口
                    if (feeAccount != null) {
                        if (!((FeeAccountRealmProxy) feeAccount).realmGet$status().equals("OK")) {
                            if (rlNull != null)
                                rlNull.setVisibility(View.VISIBLE);
                            if (recyclerview != null)
                                recyclerview.setVisibility(View.GONE);
                            rlNull.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    startActivityForResult(new Intent(getActivity(), FeeNotFulfilSearchFeeActivity.class), CHECK_FEE_ITEM);
                                }
                            });
                            /**
                             * 开通失败不调用次接口
                             */
                            if (bundle.getString(FeeActivity.ENTER_FEE_FLAG) == null) {

                                if (rlNull != null)
                                    rlNull.setVisibility(View.GONE);

                                if (loading != null)
                                    loading.setVisibility(View.VISIBLE);
                                if (gifView != null)
                                    gifView.setMovieResource(R.raw.gif_loading);
                                presenter.loadFeeList();


                            }

                        } else {
                            if (loading != null)
                                loading.setVisibility(View.VISIBLE);
                            if (gifView != null)
                                gifView.setMovieResource(R.raw.gif_loading);
                            presenter.loadFeeList();
                        }
                    }
                } else {
                    if (((FeeAccountRealmProxy) feeAccount).realmGet$other_no() != null) {
                        openNewStudent(((FeeAccountRealmProxy) feeAccount).realmGet$name(), ((FeeAccountRealmProxy) feeAccount).realmGet$id_card_no(), ((FeeAccountRealmProxy) feeAccount).realmGet$other_no());
                    } else {
                        openNewStudent(((FeeAccountRealmProxy) feeAccount).realmGet$name(), ((FeeAccountRealmProxy) feeAccount).realmGet$id_card_no());
                    }
                }
                // else {
                //presenter.loadOtherFeeList(((FeeAccountRealmProxy) feeAccounts.get(0)).realmGet$name(), ((FeeAccountRealmProxy) feeAccounts.get(0)).realmGet$other_no());
                //}
            }
        }
    }
}
