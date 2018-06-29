package com.zhihuianxin.xyaxf.app.fee;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xyaxf.axpay.Util;
import com.zhihuianxin.xyaxf.app.AppConstant;
import modellib.service.FeeService;
import modellib.service.PaymentService;
import modellib.thrift.base.PayMethod;
import modellib.thrift.customer.Customer;
import modellib.thrift.fee.SubFeeItem;
import com.google.gson.Gson;
import com.xyaxf.axpay.modle.PayFeeExtRequest;
import com.xyaxf.axpay.modle.PayFeeRequestData;
import com.xyaxf.axpay.modle.PayFor;
import com.xyaxf.axpay.modle.PayRequest;
import com.zhihuianxin.xyaxf.App;
import com.zhihuianxin.xyaxf.BaseRealmActionBarActivity;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.ApiFactory;
import com.zhihuianxin.xyaxf.app.BaseSubscriber;
import com.zhihuianxin.xyaxf.app.RetrofitFactory;
import com.zhihuianxin.xyaxf.app.payment.CashierDeskActivity;
import com.zhihuianxin.xyaxf.app.payment.CashierDeskFeeNormalAdapter;
import com.zhihuianxin.xyaxf.app.payment.FeeCashierDeskActivity;
import com.zhihuianxin.xyaxf.app.payment.FeeListData;
import com.zhihuianxin.xyaxf.app.payment.PaymentStatusActivity;
import com.zhihuianxin.xyaxf.app.utils.FullyLinearLayoutManager;
import com.zhihuianxin.xyaxf.app.utils.NetUtils;
import com.zhihuianxin.xyaxf.app.view.LoadingDialog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.realm.AccountVerifyItemRealmProxy;
import io.realm.CustomerRealmProxy;
import io.realm.FeeAccountRealmProxy;
import io.realm.RealmResults;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class FeeInfoActivity extends BaseRealmActionBarActivity {


    //总金额用于显示
    public static final String PAY_AMOUNT = "amount";
    //费用的名字
    public static final String FEE_TITLE = "FEE_TITLE";
    //接收支付渠道列表
    public static final String FEE_WAY = "FEE_WAY";
    //勾选了的费用ID
    public static final String FEE_ID = "FEE_ID";
    public static final String buss_type = "buss_type";

    public static final String ECARD = "ecard";
    public static final String FEE = "fee";
    //判断是来自于ecard还是fee
    public static final String WHICH = "which";
    //接受normal下的选择项的信息
    public static final String FEE_NORMAL = "FEE_NORMAL";
    //支付需要信息
    public static final String PAY_FEE = "PAY_FEE";
    //整包
    public static final String FEE_PACK = "FEE_PACK";
    //单选
    public static final String FEE_SINGLE = "FEE_SINGLE";
    //父类
    public static final String FEE_FU = "FEE_FU";

    /**
     * 是否全部抵扣
     */
    public static final String HAS_DECU = "HAS_DECU";


    @InjectView(R.id.tv_name)
    TextView tvName;
    @InjectView(R.id.ll_name)
    LinearLayout llName;
    @InjectView(R.id.tv_id_card)
    TextView tvIdCard;
    @InjectView(R.id.ll_id_card)
    LinearLayout llIdCard;
    @InjectView(R.id.cashier_current_number)
    TextView mCurrentNumberText;
    @InjectView(R.id.tv_other_info)
    TextView tvOtherInfo;
    @InjectView(R.id.ll_other_info)
    LinearLayout llOtherInfo;
    @InjectView(R.id.recyclerview)
    RecyclerView recyclerview;
    @InjectView(R.id.ll_fee_detail)
    LinearLayout llFeeDetail;
    @InjectView(R.id.tv_deduction_ver)
    TextView tvDeduction;
    @InjectView(R.id.ll_dekou)
    LinearLayout ll_dekou;
    @InjectView(R.id.tv_total_amount)
    TextView paymentTotal;
    @InjectView(R.id.ll_fee_deduction)
    LinearLayout llFeeDeduction;
    @InjectView(R.id.next)
    Button next;
    @InjectView(R.id.tv_fee_title)
    TextView tvFeeTitle;

    private String from;
    private String amout;
    private String fee_title;
    private static String FeeId;
    private String busss_type;
    private float decu;
    private List<PayMethod> channel_codes;
    private List<HashMap<String, String>> fee_normals;
    private List<HashMap<String, String>> fee_packs;
    private List<HashMap<String, String>> fee_singles;
    private List<HashMap<String, String>> fee_fu;
    private List<HashMap<String, String>> payfees;


    private List<PayFeeExtRequest> payFeeExtRequests;
    private PayFeeExtRequest payFeeExtRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);

        FullyLinearLayoutManager mLayoutManager = new FullyLinearLayoutManager(this);
        recyclerview.setLayoutManager(mLayoutManager);

        payFeeExtRequests = new ArrayList<>();

        getBundle();
    }

    @SuppressLint("SetTextI18n")
    private void getBundle() {
        //获取是为哪个模块所支付
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            from = bundle.getString(WHICH);
            amout = bundle.getString(PAY_AMOUNT);
            fee_title = bundle.getString(FEE_TITLE);
            FeeId = bundle.getString(FEE_ID);
            decu = bundle.getFloat(HAS_DECU);
            busss_type = bundle.getString(buss_type);
            if (bundle.getSerializable(FEE_NORMAL) != null) {
                fee_normals = (List<HashMap<String, String>>) bundle.getSerializable(FEE_NORMAL);
            }
            if (bundle.getSerializable(FEE_PACK) != null) {
                fee_packs = (List<HashMap<String, String>>) bundle.getSerializable(FEE_PACK);
            }
            if (bundle.getSerializable(FEE_SINGLE) != null) {
                fee_singles = (List<HashMap<String, String>>) bundle.getSerializable(FEE_SINGLE);
            }
            if (bundle.getSerializable(FEE_FU) != null) {
                fee_fu = (List<HashMap<String, String>>) bundle.getSerializable(FEE_FU);
            }

            if (bundle.getSerializable(PAY_FEE) != null) {
                payfees = (List<HashMap<String, String>>) bundle.getSerializable(PAY_FEE);
            }

            if (from != null) {
                if (from.equals(FEE)) {
                    from = FEE;
                    App.formCasher = FEE;
                }
                if (from.equals(ECARD)) {
                    from = ECARD;
                    App.formCasher = ECARD;
                }
            }
            channel_codes = (List<PayMethod>) bundle.getSerializable(FEE_WAY);
        }


        //查询customer中的数据
        final RealmResults<Customer> customers = realm.where(Customer.class).equalTo("mobile", App.mAxLoginSp.getUserMobil()).findAll();
        new Handler().post(new Runnable() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                if (customers.size() <= 0) {
                    return;
                }
                if (from.equals(FEE)) {
                    tvName.setText(((FeeAccountRealmProxy) (((CustomerRealmProxy) customers.get(0)).realmGet$fee_account())).realmGet$name());
                    String idStr = ((FeeAccountRealmProxy) (((CustomerRealmProxy) customers.get(0)).realmGet$fee_account())).realmGet$id_card_no();
                    if (idStr != null) {
                        tvIdCard.setText(getIDValue(idStr));
                        llIdCard.setVisibility(View.VISIBLE);
                    } else {
                        tvIdCard.setText("无");
                        llIdCard.setVisibility(View.GONE);
                    }

                    if (!Util.isEmpty(App.mAxLoginSp.getOtherFeeNo())) {
                        mCurrentNumberText.setText("缴费账号：");
                        tvOtherInfo.setText(App.mAxLoginSp.getOtherFeeNo());
                    } else if (((FeeAccountRealmProxy) (((CustomerRealmProxy) customers.get(0)).realmGet$fee_account())).realmGet$other_no() != null) {
                        if (((AccountVerifyItemRealmProxy) ((FeeAccountRealmProxy) (((CustomerRealmProxy) customers.get(0)).realmGet$fee_account())).realmGet$new_stu_verify_config()).realmGet$title() != null) {
                            mCurrentNumberText.setText(((AccountVerifyItemRealmProxy) ((FeeAccountRealmProxy) (((CustomerRealmProxy) customers.get(0)).realmGet$fee_account())).realmGet$new_stu_verify_config()).realmGet$title() + ": ");
                        } else {
                            mCurrentNumberText.setText("新生缴费: ");
                        }
                        tvOtherInfo.setText(((FeeAccountRealmProxy) (((CustomerRealmProxy) customers.get(0)).realmGet$fee_account())).realmGet$other_no());
                    } else {
                        if (TextUtils.isEmpty(((FeeAccountRealmProxy) (((CustomerRealmProxy) customers.get(0)).realmGet$fee_account())).realmGet$student_no())) {
                            tvOtherInfo.setVisibility(View.GONE);
                            mCurrentNumberText.setVisibility(View.GONE);
                        } else {
                            mCurrentNumberText.setText("学号：");
                            tvOtherInfo.setText(((FeeAccountRealmProxy) (((CustomerRealmProxy) customers.get(0)).realmGet$fee_account())).realmGet$student_no());
                        }
                    }

                    if (amout != null) {
                        if (decu == Float.parseFloat(amout)) {
                            paymentTotal.setText("0元");
                        } else {
                            paymentTotal.setText(amout + "元");
                        }
                    }
                } else if (from.equals(ECARD)) {
                    //该界面不用于一卡通
                }
            }
        });

        if (fee_title != null)
            tvFeeTitle.setText(fee_title);

        if (fee_normals != null) {
            Object val = null;
            List<FeeListData> feeListDatas = new ArrayList<>();
            Iterator iter = fee_normals.get(0).entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                if (val != null) {
                    val = val.toString() + "@%" + entry.getValue();
                } else {
                    val = entry.getValue();
                }
            }

            String[] split = ((String) val).split("@%");
            for (int i = 0; i < split.length; i++) {
                FeeListData feeListData = new FeeListData();
                feeListData.name = split[i].split(":")[0];
                feeListData.amount = split[i].split(":")[1];
                feeListDatas.add(feeListData);
            }

            CashierDeskFeeNormalAdapter cashierDeskFeeAdapter = new CashierDeskFeeNormalAdapter(FeeInfoActivity.this, feeListDatas, R.layout.cashier_desk_fee_item);
            recyclerview.setAdapter(cashierDeskFeeAdapter);
            cashierDeskFeeAdapter.notifyDataSetChanged();
        }

        if (fee_packs != null) {
            Object val = null;
            List<FeeListData> feeListDatas = new ArrayList<>();
            Iterator iter = fee_packs.get(0).entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                if (val != null) {
                    val = val.toString() + "@%" + entry.getValue();
                } else {
                    val = entry.getValue();
                }
            }

            String[] split = ((String) val).split("@%");
            for (int i = 0; i < split.length; i++) {
                FeeListData feeListData = new FeeListData();
                feeListData.name = split[i].split(":")[0];
                feeListData.amount = split[i].split(":")[1];
                feeListDatas.add(feeListData);
            }
            CashierDeskFeeNormalAdapter cashierDeskFeeAdapter = new CashierDeskFeeNormalAdapter(FeeInfoActivity.this, feeListDatas, R.layout.cashier_desk_fee_item);
            recyclerview.setAdapter(cashierDeskFeeAdapter);
            cashierDeskFeeAdapter.notifyDataSetChanged();
        }

        if (fee_singles != null) {
            Object val = null;
            List<FeeListData> feeListDatas = new ArrayList<>();
            Iterator iter = fee_singles.get(0).entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                if (val != null) {
                    val = val.toString() + "@%" + entry.getValue();
                } else {
                    val = entry.getValue();
                }
            }

            String[] split = ((String) val).split("@%");
            for (int i = 0; i < split.length; i++) {
                FeeListData feeListData = new FeeListData();
                feeListData.name = split[i].split(":")[0];
                feeListData.amount = split[i].split(":")[1];
                feeListDatas.add(feeListData);
            }
            CashierDeskFeeNormalAdapter cashierDeskFeeAdapter = new CashierDeskFeeNormalAdapter(FeeInfoActivity.this, feeListDatas, R.layout.cashier_desk_fee_item);
            recyclerview.setAdapter(cashierDeskFeeAdapter);
            cashierDeskFeeAdapter.notifyDataSetChanged();
        }

        if (fee_fu != null) {
            Object val = null;
            List<FeeListData> feeListDatas = new ArrayList<>();
            Iterator iter = fee_fu.get(0).entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                if (val != null) {
                    val = val.toString() + "@%" + entry.getValue();
                } else {
                    val = entry.getValue();
                }
            }

            String[] split = ((String) val).split("@%");
            for (int i = 0; i < split.length; i++) {
                FeeListData feeListData = new FeeListData();
                feeListData.name = split[i].split(":")[0];
                feeListData.amount = split[i].split(":")[1];
                feeListDatas.add(feeListData);
            }
            CashierDeskFeeNormalAdapter cashierDeskFeeAdapter = new CashierDeskFeeNormalAdapter(FeeInfoActivity.this, feeListDatas, R.layout.cashier_desk_fee_item);
            recyclerview.setAdapter(cashierDeskFeeAdapter);
            cashierDeskFeeAdapter.notifyDataSetChanged();
        }

        //存在溢交款
        if (Math.abs(decu) > 0) {
            ll_dekou.setVisibility(View.VISIBLE);
            tvDeduction.setText(decu + "元");
        }


        if (Float.parseFloat(amout) == 0) {
            next.setText("确认");
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //刚刚抵扣完的支付
                    initPayRequestData();
                }
            });
        } else {
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle1 = new Bundle();
                    bundle1.putString(WHICH, from);
                    bundle1.putString(PAY_AMOUNT, amout);
                    bundle1.putString(FEE_TITLE, fee_title);
                    bundle1.putString(FEE_ID, FeeId);
                    bundle1.putFloat(HAS_DECU, decu);

                    if (busss_type != null)
                        bundle1.putString(buss_type, busss_type);

                    if (fee_normals != null)
                        bundle1.putSerializable(CashierDeskActivity.FEE_NORMAL, (Serializable) fee_normals);

                    if (fee_packs != null)
                        bundle1.putSerializable(CashierDeskActivity.FEE_PACK, (Serializable) fee_packs);

                    if (fee_singles != null)
                        bundle1.putSerializable(CashierDeskActivity.FEE_SINGLE, (Serializable) fee_singles);

                    if (channel_codes != null)
                        bundle1.putSerializable(CashierDeskActivity.FEE_WAY, (Serializable) channel_codes);

                    if (fee_fu != null)
                        bundle1.putSerializable(CashierDeskActivity.FEE_FU, (Serializable) fee_fu);

                    if (payfees != null)
                        bundle1.putSerializable(CashierDeskActivity.PAY_FEE, (Serializable) payfees);

                    Intent intent = new Intent(FeeInfoActivity.this, FeeCashierDeskActivity.class);
                    intent.putExtras(bundle1);
                    startActivity(intent);
                    finish();
                }
            });
        }
    }

    private void initPayRequestData() {
        payFeeExtRequests.clear();
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String, Object> map = new HashMap<>();
        map.put("fee_id", FeeId);
        FeeService feeserice = ApiFactory.getFactory().create(FeeService.class);
        feeserice.get_fee_by_id(NetUtils.getRequestParams(FeeInfoActivity.this, map), NetUtils.getSign(NetUtils.getRequestParams(FeeInfoActivity.this, map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(FeeInfoActivity.this, true, null) {
                    @Override
                    public void onNext(Object o) {
                        CashierDeskActivity.ISResponse isResponse = new Gson().fromJson(o.toString(), CashierDeskActivity.ISResponse.class);
                        for (int i = 0; i < payfees.size(); i++) {
                            for (Iterator iter = payfees.get(i).entrySet().iterator(); iter.hasNext(); ) {
                                Map.Entry element = (Map.Entry) iter.next();
                                Object strKey = element.getKey();
                                Object strValue = element.getValue();
                                System.out.println(strKey + "/" + strValue);
                                String[] sourceStrArray = strValue.toString().split(":");
                                payFeeExtRequest = new PayFeeExtRequest();
                                payFeeExtRequest.id = (String) element.getKey();
                                payFeeExtRequest.amount = sourceStrArray[0];
                                payFeeExtRequest.pay_amount = sourceStrArray[1];
                                payFeeExtRequest.title = sourceStrArray[2];
                                payFeeExtRequests.add(payFeeExtRequest);
                            }
                        }
                        //除此之外还添加app中的抵扣项目
                        if (App.subFeeDeductionHashMap.size() > 0) {
                            Iterator iter = App.subFeeDeductionHashMap.entrySet().iterator();
                            while (iter.hasNext()) {
                                Map.Entry entry = (Map.Entry) iter.next();
                                Object value = entry.getValue();
                                if (busss_type != null) {
                                    if (busss_type.equals(((SubFeeItem) value).business_channel) && ((SubFeeItem) value).isSelect) {
                                        payFeeExtRequest = new PayFeeExtRequest();
                                        payFeeExtRequest.id = ((SubFeeItem) value).id;
                                        payFeeExtRequest.amount = ((SubFeeItem) value).amount;
                                        payFeeExtRequest.pay_amount = ((SubFeeItem) value).amount;
                                        payFeeExtRequest.title = ((SubFeeItem) value).title;
                                        payFeeExtRequests.add(payFeeExtRequest);
                                    }
                                }
                            }
                        }
                        final PayFeeRequestData payFeeRequestData = new PayFeeRequestData();
                        payFeeRequestData.id = FeeId;
                        payFeeRequestData.exts = payFeeExtRequests;
                        if (fee_title != null)
                            payFeeRequestData.title = fee_title;

                        PayRequest payRequest = new PayRequest();
                        payRequest.pay_for = PayFor.SchoolFee;
                        payRequest.amount = amout;
                        payRequest.pay_method = new PayMethod();
                        payRequest.request_data = payFeeRequestData;
                        pay(payRequest);
                    }
                });
    }

    @Override
    protected int getContentViewId() {
        return R.layout.fee_info_activity;
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


    //刚好完成抵扣的支付下单
    private void pay(final PayRequest payRequest) {
        final LoadingDialog loadingDialog2 = new LoadingDialog(this);
        loadingDialog2.show();
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String, Object> map = new HashMap<>();
        payRequest.pay_method.channel = "NoNeed";
        map.put("pay_req", payRequest);
        PaymentService paymentService = ApiFactory.getFactory().create(PaymentService.class);
        paymentService.payOder(NetUtils.getRequestParams(FeeInfoActivity.this, map), NetUtils.getSign(NetUtils.getRequestParams(FeeInfoActivity.this, map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Object>() {
                    @Override
                    public void onCompleted() {
                        loadingDialog2.dismiss();
                    }

                    @Override
                    public void onError(Throwable e) {
                        loadingDialog2.dismiss();
                        Toast.makeText(FeeInfoActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(Object o) {
                        CashierDeskActivity.PayOderResponse payOderResponse = new Gson().fromJson(o.toString(), CashierDeskActivity.PayOderResponse.class);
                        //获取到paymentoder
                        if (payOderResponse.resp.resp_code.equals(AppConstant.SUCCESS)) {
                            //成功
                            Intent intent = new Intent(FeeInfoActivity.this, PaymentStatusActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString(PaymentStatusActivity.AMOUNT, payOderResponse.order.payment_amt);
                            bundle.putString(PaymentStatusActivity.NAME, payOderResponse.order.trade_summary);
                            bundle.putSerializable(PaymentStatusActivity.WAY, payOderResponse.order.pay_method);
                            bundle.putString(PaymentStatusActivity.TIME, payOderResponse.order.order_time);
                            bundle.putString(PaymentStatusActivity.ODER, payOderResponse.order.order_no);
                            intent.putExtras(bundle);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(FeeInfoActivity.this, payOderResponse.resp.resp_desc, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
