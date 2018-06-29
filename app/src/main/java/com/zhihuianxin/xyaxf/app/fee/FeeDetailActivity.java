package com.zhihuianxin.xyaxf.app.fee;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.sdk.android.man.MANHitBuilders;
import com.alibaba.sdk.android.man.MANService;
import com.alibaba.sdk.android.man.MANServiceProvider;
import com.axinfu.modellib.thrift.base.PayMethod;
import com.axinfu.modellib.thrift.customer.Customer;
import com.axinfu.modellib.thrift.fee.Fee;
import com.axinfu.modellib.thrift.fee.SchoolRoll;
import com.axinfu.modellib.thrift.fee.SubFeeItem;
import com.zhihuianxin.xyaxf.app.base.axutil.Util;
import com.zhihuianxin.xyaxf.App;
import com.zhihuianxin.xyaxf.BaseRealmActionBarActivity;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.fee.deduction.DeductionActivity;
import com.zhihuianxin.xyaxf.app.fee.detail.FeeDetailNormalAdapter;
import com.zhihuianxin.xyaxf.app.fee.detail.FeeDetailPackAdapter;
import com.zhihuianxin.xyaxf.app.fee.detail.FeeDetailSingleAdapter;
import com.zhihuianxin.xyaxf.app.fee.detail.FeeNormalAdapter;
import com.zhihuianxin.xyaxf.app.payment.CashierDeskActivity;
import com.zhihuianxin.xyaxf.app.utils.FullyLinearLayoutManager;
import com.zhihuianxin.xyaxf.app.utils.RxBus;
import com.zhihuianxin.xyaxf.test.TestDataConfig;

import java.io.Serializable;
import java.text.DecimalFormat;
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
import io.realm.FeeRealmProxy;
import io.realm.RealmResults;
import io.realm.SchoolRollRealmProxy;
import rx.functions.Action1;

import static com.zhihuianxin.xyaxf.App.subFeeDeductionHashMap;

/**
 * Created by zcrpro on 2016/10/20.
 */
public class FeeDetailActivity extends BaseRealmActionBarActivity {

    public static final String FEE_ID = "id";
    public static final String FEE_WAY = "FEE_WAY";
    public static final String deduction_NUM = "deduction";
    @InjectView(R.id.tv_name)
    TextView tvName;
    @InjectView(R.id.tv_id_card)
    TextView tvIdCard;
    @InjectView(R.id.tv_fee_title)
    TextView tvFeeTitle;
    @InjectView(R.id.tv_fee_endate)
    TextView tvFeeEndate;
    @InjectView(R.id.recyclerview)
    RecyclerView recyclerview;
    @InjectView(R.id.tv_fee_num)
    TextView tvFeeTotalNum;
    @InjectView(R.id.btn_ok)
    Button btnOk;
    @InjectView(R.id.checkbox)
    CheckBox checkbox;
    @InjectView(R.id.tv_quanxuan)
    TextView tvQuanxuan;
    @InjectView(R.id.ll_select_all)
    RelativeLayout llSelectAll;
    @InjectView(R.id.view_ck)
    RelativeLayout view_ck;
    @InjectView(R.id.rl_check)
    RelativeLayout rlCheck;
    @InjectView(R.id.tv_fee_info)
    TextView tvFeeInfo;
    @InjectView(R.id.tv_bufen_wa)
    TextView tvBufenWa;
    @InjectView(R.id.ll_bufen_wa)
    LinearLayout llBufenWa;
    @InjectView(R.id.ll_hide_soft)
    LinearLayout ll_hide_soft;
    @InjectView(R.id.scrollview)
    ScrollView scrollview;
    @InjectView(R.id.user_name)
    LinearLayout userName;
    @InjectView(R.id.user_id)
    LinearLayout userId;
    //    @InjectView(R.id.iv_show_more_info)
//    ImageView ivShowMoreInfo;
    @InjectView(R.id.ll_xueji)
    LinearLayout llXueji;
    @InjectView(R.id.tv_id_card_lable)
    TextView tvIdCardLable;
    @InjectView(R.id.tv_fee_year)
    TextView tvFeeYear;
    @InjectView(R.id.tv_deduction)
    TextView tvDeduction;
    @InjectView(R.id.tv_deduction_wenzi)
    TextView tv_deduction_wenzi;
    @InjectView(R.id.ll_into_deduction)
    LinearLayout llIntoDeduction;
    @InjectView(R.id.iv_into_decu)
    ImageView ivIntoDecu;
    @InjectView(R.id.ll)
    LinearLayout ll;
    @InjectView(R.id.ll_yijiao)
    LinearLayout llYijiao;
    @InjectView(R.id.ll_decu_w)
    LinearLayout ll_decu_w;
    //    @InjectView(R.id.xueji_wenzi)
//    TextView xuejiWenzi;
//    @InjectView(R.id.xueji_line)
//    View xuejiLine;
    @InjectView(R.id.tv_decu_w)
    TextView tvDecuW;
    @InjectView(R.id.tv_jine_w)
    TextView tvJineW;
    @InjectView(R.id.ll_jine_w)
    LinearLayout llJineW;
    @InjectView(R.id.tv_id_card_lable_1)
    TextView tvIdCardLable1;
    @InjectView(R.id.tv_id_card_1)
    TextView tvIdCard1;
    @InjectView(R.id.user_id_1)
    LinearLayout userId1;
    @InjectView(R.id.tv_xueji)
    TextView tvXueji;

    private String fee_id;
    private boolean isOpenInfo = false;
    private FeeDetailSingleAdapter feeDetailSingleAdapter;
    private Bundle bundle;
    private FeeDetailNormalAdapter feeDetailNormalAdapter;
    private FeeNormalAdapter feeNormalAdapter;
    private FeeDetailPackAdapter feeDetailPackAdapter;
    private boolean isPass = true;

    private HashMap<String, String> hashMap;
    private List<HashMap<String, String>> fee_hash;
    private boolean open = false;
    private InputMethodManager manager;
    private List<PayMethod> channel_codes;

    private String buss_type;

    //只作不抵扣的金额记录
    private float pureNum = 0f;

    @Override
    protected int getContentViewId() {
        return R.layout.fee_details_activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
        bundle = new Bundle();
        getBundle();
        fee_hash = new ArrayList<>();
        hashMap = new HashMap<>();
        manager = (InputMethodManager) getSystemService("input_method");// Context.INPUT_METHOD_SERVICE
    }

    /**
     * 获取缴费的详细信息
     */
    private void getBundle() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            fee_id = bundle.getString(FEE_ID);
            if (fee_id != null) {
                initViews();
            }
            channel_codes = (List<PayMethod>) bundle.getSerializable(FEE_WAY);
        }
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

    /**
     * 装载数据
     */
    @SuppressLint("SetTextI18n")
    private void initViews() {
        FullyLinearLayoutManager linearLayoutManager = new FullyLinearLayoutManager(this);
        recyclerview.setNestedScrollingEnabled(false);
        recyclerview.setLayoutManager(linearLayoutManager);
//        recyclerview.setHasFixedSize(true);
        //查询customer中的数据
        final RealmResults<Customer> customers = realm.where(Customer.class).equalTo("mobile", App.mAxLoginSp.getUserMobil())
                .findAll();

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                if (customers.size() <= 0) {
                    return;
                }
                tvName.setText(((FeeAccountRealmProxy) (((CustomerRealmProxy) customers.get(0)).realmGet$fee_account())).realmGet$name());
                String idValue = ((FeeAccountRealmProxy) (((CustomerRealmProxy) customers.get(0)).realmGet$fee_account())).realmGet$id_card_no();
                tvIdCard.setText(getIDValue(idValue));
                tvIdCardLable.setText("身份证号：");

                final RealmResults<SchoolRoll> schoolRolls = realm.where(SchoolRoll.class).equalTo("mobile", App.mAxLoginSp.getUserMobil())
                        .findAll();

                if (TestDataConfig.SCHOOLROLL) {
                    tvXueji.setText("学籍：测试数据某某测试数据某某测试数据某某");
                    llXueji.setVisibility(View.VISIBLE);
                    tvXueji.setVisibility(View.VISIBLE);
                } else {
                    if (schoolRolls.size() > 0) {
                        tvXueji.setVisibility(View.VISIBLE);
                        llXueji.setVisibility(View.VISIBLE);
                        if (((SchoolRollRealmProxy) schoolRolls.get(0)).realmGet$district() != null) {
                            tvXueji.setText("学籍：" + ((SchoolRollRealmProxy) schoolRolls.get(0)).realmGet$district() + ((SchoolRollRealmProxy) schoolRolls.get(0)).realmGet$academy() + ((SchoolRollRealmProxy) schoolRolls.get(0)).realmGet$major() +
                                    ((SchoolRollRealmProxy) schoolRolls.get(0)).realmGet$grade() + ((SchoolRollRealmProxy) schoolRolls.get(0)).realmGet$clazz());
                        } else {
                            if (((SchoolRollRealmProxy) schoolRolls.get(0)).realmGet$academy() == null || ((SchoolRollRealmProxy) schoolRolls.get(0)).realmGet$major() == null ||
                                    ((SchoolRollRealmProxy) schoolRolls.get(0)).realmGet$grade() == null || ((SchoolRollRealmProxy) schoolRolls.get(0)).realmGet$clazz() == null) {
                                tvXueji.setVisibility(View.GONE);
                                llXueji.setVisibility(View.GONE);
                            }
                            tvXueji.setText("学籍：" + ((SchoolRollRealmProxy) schoolRolls.get(0)).realmGet$academy() + ((SchoolRollRealmProxy) schoolRolls.get(0)).realmGet$major() +
                                    ((SchoolRollRealmProxy) schoolRolls.get(0)).realmGet$grade() + ((SchoolRollRealmProxy) schoolRolls.get(0)).realmGet$clazz());
                        }
                    } else {
                        tvXueji.setVisibility(View.GONE);
                        llXueji.setVisibility(View.GONE);
                    }
                }

                if (!Util.isEmpty(App.mAxLoginSp.getOtherFeeNo())) {
                    tvIdCardLable.setText("缴费账号：");
                    tvIdCard.setText(App.mAxLoginSp.getOtherFeeNo());

                    if (!TextUtils.isEmpty(idValue)) {
                        userId1.setVisibility(View.VISIBLE);
                        tvIdCard1.setText(getIDValue(idValue));
                        tvIdCardLable1.setText("身份证号：");
                    }

                } else if (((FeeAccountRealmProxy) (((CustomerRealmProxy) customers.get(0)).realmGet$fee_account())).realmGet$other_no() != null) {
                    //新生缴费
                    if (((AccountVerifyItemRealmProxy) ((FeeAccountRealmProxy) (((CustomerRealmProxy) customers.get(0)).realmGet$fee_account())).realmGet$new_stu_verify_config()).realmGet$title() != null) {
                        tvIdCardLable.setText(((AccountVerifyItemRealmProxy) ((FeeAccountRealmProxy) (((CustomerRealmProxy) customers.get(0)).realmGet$fee_account())).realmGet$new_stu_verify_config()).realmGet$title() + ": ");
                    } else {
                        tvIdCardLable.setText("新生缴费: ");
                    }
                    tvIdCard.setText(((FeeAccountRealmProxy) (((CustomerRealmProxy) customers.get(0)).realmGet$fee_account())).realmGet$other_no());

                    if (!TextUtils.isEmpty(idValue)) {
                        userId1.setVisibility(View.VISIBLE);
                        tvIdCard1.setText(getIDValue(idValue));
                        tvIdCardLable1.setText("身份证号：");
                    }

                } else if (((FeeAccountRealmProxy) (((CustomerRealmProxy) customers.get(0)).realmGet$fee_account())).realmGet$student_no() != null) {
                    tvIdCardLable.setText("学号：");
                    tvIdCard.setText(((FeeAccountRealmProxy) (((CustomerRealmProxy) customers.get(0)).realmGet$fee_account())).realmGet$student_no());
                    if (!TextUtils.isEmpty(idValue)) {
                        userId1.setVisibility(View.VISIBLE);
                        tvIdCard1.setText(getIDValue(idValue));
                        tvIdCardLable1.setText("身份证号：");
                    }

                } else {

                }
            }
        });

        //根据id查询缴费项的详细信息
        final RealmResults<Fee> fees = realm.where(Fee.class).equalTo("id", fee_id)
                .findAll();
//        fees.addChangeListener(new RealmChangeListener<RealmResults<Fee>>() {
//            @Override
//            public void onChange(RealmResults<Fee> element) {
//                if (element.size()>0){
        //显示共有的标题等数据

        if (((FeeRealmProxy) fees.get(0)).realmGet$business_channel() != null) {
            buss_type = ((FeeRealmProxy) fees.get(0)).realmGet$business_channel();
        }

        tvFeeTitle.setText(((FeeRealmProxy) fees.get(0)).realmGet$title());

        if (((FeeRealmProxy) fees.get(0)).realmGet$year() != null) {
            tvFeeYear.setText(((FeeRealmProxy) fees.get(0)).realmGet$year());
        } else {
            tvFeeYear.setVisibility(View.GONE);
        }


        if (subFeeDeductionHashMap.size() > 0) {
            Iterator iter = App.subFeeDeductionHashMap.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                Object value = entry.getValue();
                if (((SubFeeItem) value).business_channel.equals(buss_type)) {
                    llYijiao.setVisibility(View.VISIBLE);
                    tvDeduction.setText(new DecimalFormat("0.00").format(getNum()));
                }
            }
        } else {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    llYijiao.setVisibility(View.GONE);
                }
            });
        }

        if (TextUtils.isEmpty(((FeeRealmProxy) fees.get(0)).realmGet$end_date())) {
            tvFeeEndate.setText("");
        } else {
            tvFeeEndate.setText(getValidDateText(((FeeRealmProxy) fees.get(0)).realmGet$end_date()));
        }
        if (((FeeRealmProxy) fees.get(0)).realmGet$info() != null) {
            if (!TextUtils.isEmpty(((FeeRealmProxy) fees.get(0)).realmGet$info())) {
                tvFeeInfo.setText(((FeeRealmProxy) fees.get(0)).realmGet$info());
            } else {
                tvFeeInfo.setText("无");
            }
        } else {
            tvFeeInfo.setText("无");
        }
        if (((FeeRealmProxy) fees.get(0)).realmGet$group_type() != null) {
            if (((FeeRealmProxy) fees.get(0)).realmGet$subfees().size() > 0) {
                if (((FeeRealmProxy) fees.get(0)).realmGet$group_type().equals("single")) {
                    //单选
                    feeDetailSingleAdapter = new FeeDetailSingleAdapter(FeeDetailActivity.this, ((FeeRealmProxy) fees.get(0)).realmGet$subfees(), R.layout.fee_radio_item);
                    recyclerview.setAdapter(feeDetailSingleAdapter);
                    feeDetailSingleAdapter.notifyDataSetChanged();
                    feeDetailSingleAdapter.feeChanged(new FeeDetailSingleAdapter.FeenumChangeListener() {
                        @Override
                        public void feeChanged(String total) {
                            pureNum = Float.parseFloat(total);
                            //选择了一个子项
                            if (total.equals("0.00")) {
                                tvFeeTotalNum.setText(total);
                            } else {
                                if (llYijiao.getVisibility() == View.VISIBLE) {
                                    if (Math.abs(Float.parseFloat(tvDeduction.getText().toString().trim())) != 0 && tv_deduction_wenzi.getVisibility() == View.GONE) {
                                        tvFeeTotalNum.setText(new DecimalFormat("0.00").format(Float.parseFloat(total) - Math.abs(Float.parseFloat(tvDeduction.getText().toString().trim()))));
                                    } else {
                                        tvFeeTotalNum.setText(new DecimalFormat("0.00").format(Float.parseFloat(total)) + "");
                                    }
                                } else {
                                    tvFeeTotalNum.setText(new DecimalFormat("0.00").format(Float.parseFloat(total)) + "");
                                }
                            }

                            if (Float.parseFloat(tvFeeTotalNum.getText().toString()) <= 0f) {
                                if (llYijiao.getVisibility() == View.VISIBLE) {
                                    if (tv_deduction_wenzi.getVisibility() == View.GONE) {
                                        if (Float.parseFloat(tvFeeTotalNum.getText().toString()) == 0f) {
                                            btnOk.setEnabled(true);
                                            btnOk.setBackgroundResource(R.color.axf_common_blue);
                                            DissMissDecuToastView();
                                        } else {
                                            btnOk.setEnabled(false);
                                            btnOk.setBackgroundResource(R.color.axf_light_gray);
                                            showDecuToastView();
                                        }
                                    }
                                } else {
                                    btnOk.setEnabled(false);
                                    btnOk.setBackgroundResource(R.color.axf_light_gray);
                                    DissMissDecuToastView();
                                }

                            } else {
                                btnOk.setEnabled(true);
                                btnOk.setBackgroundResource(R.color.axf_common_blue);
                                DissMissDecuToastView();
                            }

                            if (Float.parseFloat(tvFeeTotalNum.getText().toString().trim()) > 0) {
                                if (!TextUtils.isEmpty(App.hint)) {
                                    tvJineW.setText(App.hint);
                                    llJineW.setVisibility(View.VISIBLE);
                                } else {
                                    llJineW.setVisibility(View.GONE);
                                }
                            } else {
                                llJineW.setVisibility(View.GONE);
                            }

                        }
                    });

                    feeDetailSingleAdapter.noPass(new FeeDetailSingleAdapter.NoPassListener() {
                        @Override
                        public void noPass(boolean ispass) {
                            isPass = ispass;
                        }
                    });

                    feeDetailSingleAdapter.feeDetails(new FeeDetailSingleAdapter.FeeDetailsListener() {
                        @Override
                        public void feeDetailsInfo(List<HashMap<String, String>> details) {
                            bundle.putSerializable(CashierDeskActivity.FEE_SINGLE, (Serializable) details);
                        }
                    });

                    feeDetailSingleAdapter.getEdHint(new FeeDetailSingleAdapter.EDListener() {
                        @Override
                        public void edHint(boolean isEd, String minAmount) {
                            if (isEd) {
                                llBufenWa.setVisibility(View.VISIBLE);
                                tvBufenWa.setText("“部分缴费项”单次最小金额为:" + minAmount + "元");
                            } else {
                                llBufenWa.setVisibility(View.GONE);
                            }
                        }
                    });

                    feeDetailSingleAdapter.getPayinfo(new FeeDetailSingleAdapter.PayListener() {
                        @Override
                        public void payInfo(List<HashMap<String, String>> payfees) {
                            bundle.putSerializable(CashierDeskActivity.PAY_FEE, (Serializable) payfees);
                        }
                    });


                } else if (((FeeRealmProxy) fees.get(0)).realmGet$group_type().equals("normal")) {

                    feeNormalAdapter = new FeeNormalAdapter(FeeDetailActivity.this, ((FeeRealmProxy) fees.get(0)).realmGet$subfees(), R.layout.fee_check_item);
                    recyclerview.setAdapter(feeNormalAdapter);
                    feeNormalAdapter.feeCheckd(new FeeNormalAdapter.FeeCheckListener() {
                        @Override
                        public void feeTotalAmount(final float amounts) {
                            pureNum = amounts;
                            if (amounts < 0) {
                                tvFeeTotalNum.setText("0.00");
                            } else {
                                if (llYijiao.getVisibility() == View.VISIBLE) {
                                    if (Math.abs(Float.parseFloat(tvDeduction.getText().toString().trim())) != 0 && tv_deduction_wenzi.getVisibility() == View.GONE) {
                                        tvFeeTotalNum.setText(new DecimalFormat("0.00").format(amounts - Math.abs(Float.parseFloat(tvDeduction.getText().toString().trim()))));
                                    } else {
                                        tvFeeTotalNum.setText(new DecimalFormat("0.00").format(amounts) + "");
                                    }
                                } else {
                                    new Handler().post(new Runnable() {
                                        @Override
                                        public void run() {
                                            tvFeeTotalNum.setText(new DecimalFormat("0.00").format(amounts) + "");
                                        }
                                    });
                                }
                            }

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (Float.parseFloat(tvFeeTotalNum.getText().toString()) <= 0f) {
                                        if (llYijiao.getVisibility() == View.VISIBLE) {
                                            if (tv_deduction_wenzi.getVisibility() == View.GONE) {
                                                if (Float.parseFloat(tvFeeTotalNum.getText().toString()) == 0f) {
                                                    btnOk.setEnabled(true);
                                                    btnOk.setBackgroundResource(R.color.axf_common_blue);
                                                    DissMissDecuToastView();
                                                } else {
                                                    btnOk.setEnabled(false);
                                                    btnOk.setBackgroundResource(R.color.axf_light_gray);
                                                    showDecuToastView();
                                                }
                                            }
                                        } else {
                                            btnOk.setEnabled(false);
                                            btnOk.setBackgroundResource(R.color.axf_light_gray);
                                            DissMissDecuToastView();
                                        }

                                    } else {
                                        btnOk.setEnabled(true);
                                        btnOk.setBackgroundResource(R.color.axf_common_blue);
                                        DissMissDecuToastView();
                                    }
                                }
                            }, 50);

                            if (Float.parseFloat(tvFeeTotalNum.getText().toString().trim()) > 0) {
                                if (!TextUtils.isEmpty(App.hint)) {
                                    tvJineW.setText(App.hint);
                                    llJineW.setVisibility(View.VISIBLE);
                                } else {
                                    llJineW.setVisibility(View.GONE);
                                }
                            } else {
                                llJineW.setVisibility(View.GONE);
                            }

                        }
                    });
                    //全选按钮的控制
                    feeNormalAdapter.isCheckAll(new FeeNormalAdapter.CheckAllListener() {
                        @Override
                        public void isCheckAll(boolean checkall, boolean enable) {
                            if (enable) {
                                checkbox.setEnabled(true);
                                checkbox.setBackgroundResource(R.drawable.choose_icon2);
                            } else {
                                checkbox.setEnabled(false);
                                checkbox.setBackgroundResource(R.drawable.choose_icon1);
                            }
                            if (checkall) {
                                checkbox.setChecked(true);
                                open = true;
                                if (!enable) {
                                    checkbox.setBackgroundResource(R.drawable.choose_icon1);
                                } else {
                                    checkbox.setBackgroundResource(R.drawable.choose_icon5);
                                }
                            } else {
                                checkbox.setChecked(false);
                                checkbox.setBackgroundResource(R.drawable.choose_icon2);
                                open = false;
                            }
                        }
                    });
                    llSelectAll.setVisibility(View.VISIBLE);
                    if (checkbox.getVisibility() == View.VISIBLE) {
                        view_ck.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!open) {
                                    feeNormalAdapter.selectedAll();
                                    open = true;
                                    checkbox.setBackgroundResource(R.drawable.choose_icon5);
                                } else {
                                    feeNormalAdapter.notSelectedAll();
                                    open = false;
                                    checkbox.setBackgroundResource(R.drawable.choose_icon2);
                                }
                                if (checkbox.isChecked()) {
                                    checkbox.setChecked(false);
                                } else {
                                    checkbox.setChecked(true);
                                }
                            }
                        });
                        rlCheck.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (!open) {
                                    feeNormalAdapter.selectedAll();
                                    open = true;
                                    checkbox.setBackgroundResource(R.drawable.choose_icon5);
                                } else {
                                    feeNormalAdapter.notSelectedAll();
                                    open = false;
                                    checkbox.setBackgroundResource(R.drawable.choose_icon2);
                                }
                                if (checkbox.isChecked()) {
                                    checkbox.setChecked(false);
                                } else {
                                    checkbox.setChecked(true);
                                }
                            }
                        });
                    }

                    feeNormalAdapter.noPass(new FeeNormalAdapter.NoPassListener() {
                        @Override
                        public void noPass(boolean ispass) {
                            isPass = ispass;
                        }
                    });

                    feeNormalAdapter.getEdHint(new FeeNormalAdapter.EDListener() {
                        @Override
                        public void edHint(boolean isEd, String minAmount) {
                            if (isEd) {
                                llBufenWa.setVisibility(View.VISIBLE);
                                tvBufenWa.setText("“部分缴费项”单次最小金额为:" + minAmount + "元");
                            } else {
                                llBufenWa.setVisibility(View.GONE);
                            }
                        }
                    });

                    feeNormalAdapter.getPayinfo(new FeeNormalAdapter.PayListener() {
                        @Override
                        public void payInfo(List<HashMap<String, String>> payfees) {
                            bundle.putSerializable(CashierDeskActivity.PAY_FEE, (Serializable) payfees);
                        }
                    });

                    feeNormalAdapter.feeDetails(new FeeNormalAdapter.FeeDetailsListener() {
                        @Override
                        public void feeDetailsInfo(List<HashMap<String, String>> details) {
                            bundle.putSerializable(CashierDeskActivity.FEE_NORMAL, (Serializable) details);
                        }
                    });

                } else if (((FeeRealmProxy) fees.get(0)).realmGet$group_type().equals("pack")) {
                    feeDetailPackAdapter = new FeeDetailPackAdapter(FeeDetailActivity.this, ((FeeRealmProxy) fees.get(0)).realmGet$subfees(), R.layout.fee_pack_item);
                    recyclerview.setAdapter(feeDetailPackAdapter);
                    feeDetailPackAdapter.feeCheckd(new FeeDetailPackAdapter.FeeCheckListener() {
                        @Override
                        public void feeTotalAmount(float amounts) {
                            pureNum = amounts;
                            if (llYijiao.getVisibility() == View.VISIBLE) {
                                if (Math.abs(Float.parseFloat(tvDeduction.getText().toString().trim())) != 0 && tv_deduction_wenzi.getVisibility() == View.GONE) {
                                    tvFeeTotalNum.setText(new DecimalFormat("0.00").format(amounts - Math.abs(Float.parseFloat(tvDeduction.getText().toString().trim()))));
                                } else {
                                    tvFeeTotalNum.setText(new DecimalFormat("0.00").format(amounts) + "");
                                }
                            } else {
                                tvFeeTotalNum.setText(new DecimalFormat("0.00").format(amounts) + "");
                            }
                            if (Float.parseFloat(tvFeeTotalNum.getText().toString()) <= 0f) {
                                if (llYijiao.getVisibility() == View.VISIBLE) {
                                    if (tv_deduction_wenzi.getVisibility() == View.GONE) {
                                        if (Float.parseFloat(tvFeeTotalNum.getText().toString()) == 0f) {
                                            btnOk.setEnabled(true);
                                            btnOk.setBackgroundResource(R.color.axf_common_blue);
                                            DissMissDecuToastView();
                                        } else {
                                            btnOk.setEnabled(false);
                                            btnOk.setBackgroundResource(R.color.axf_light_gray);
                                            showDecuToastView();
                                        }
                                    }
                                } else {
                                    btnOk.setEnabled(false);
                                    btnOk.setBackgroundResource(R.color.axf_light_gray);
                                    DissMissDecuToastView();
                                }

                            } else {
                                btnOk.setEnabled(true);
                                btnOk.setBackgroundResource(R.color.axf_common_blue);
                                DissMissDecuToastView();
                            }

                            if (Float.parseFloat(tvFeeTotalNum.getText().toString().trim()) > 0) {
                                if (!TextUtils.isEmpty(App.hint)) {
                                    tvJineW.setText(App.hint);
                                    llJineW.setVisibility(View.VISIBLE);
                                } else {
                                    llJineW.setVisibility(View.GONE);
                                }
                            } else {
                                llJineW.setVisibility(View.GONE);
                            }

                        }
                    });

                    feeDetailPackAdapter.feeDetails(new FeeDetailPackAdapter.FeeDetailsListener() {
                        @Override
                        public void feeDetailsInfo(List<HashMap<String, String>> details) {
                            bundle.putSerializable(CashierDeskActivity.FEE_PACK, (Serializable) details);
                        }
                    });

                    feeDetailPackAdapter.noPass(new FeeDetailPackAdapter.NoPassListener() {
                        @Override
                        public void noPass(boolean ispass) {
                            isPass = ispass;
                        }
                    });

                    feeDetailPackAdapter.getEdHint(new FeeDetailPackAdapter.EDListener() {
                        @Override
                        public void edHint(boolean isEd, String minAmount) {
                            if (isEd) {
                                llBufenWa.setVisibility(View.VISIBLE);
                                tvBufenWa.setText("“部分缴费项”单次最小金额为:" + minAmount + "元");
                            } else {
                                llBufenWa.setVisibility(View.GONE);
                            }
                        }
                    });

                    feeDetailPackAdapter.getPayinfo(new FeeDetailPackAdapter.PayListener() {
                        @Override
                        public void payInfo(List<HashMap<String, String>> payfees) {
                            bundle.putSerializable(CashierDeskActivity.PAY_FEE, (Serializable) payfees);
                        }
                    });
                }
            } else {
                //无缴费子项目--提交阿里云检测平台
                MANHitBuilders.MANCustomHitBuilder hitBuilder = new MANHitBuilders.MANCustomHitBuilder("rule");
                hitBuilder.setDurationOnEvent(3 * 60 * 1000);
                hitBuilder.setEventPage("FeeDetailActivity.class");
                hitBuilder.setProperty("rule_fee", "lable" + "无缴费子项" + "||" +
                        "id" + fee_id + "||" + "time" + Util.getNowTime() + "||" + "mobile" + App.mAxLoginSp.getUserMobil() + "||");
                MANService manService = MANServiceProvider.getService();
                manService.getMANAnalytics().getDefaultTracker().send(hitBuilder.build());
            }
        }

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tvFeeTotalNum.getText().equals("")) {
                    Toast.makeText(FeeDetailActivity.this, "请选择或输入正确的缴费金额", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    if (Float.parseFloat(tvFeeTotalNum.getText().toString()) < 0) {
                        Toast.makeText(FeeDetailActivity.this, "请选择或输入正确的缴费金额", Toast.LENGTH_SHORT).show();
                    } else {
                        if (isPass) {
                            if (((FeeRealmProxy) fees.get(0)).realmGet$group_type() == null) {
                                hashMap.clear();
                                hashMap.put(((FeeRealmProxy) fees.get(0)).realmGet$id(), ((FeeRealmProxy) fees.get(0)).realmGet$title() + ":" + tvFeeTotalNum.getText().toString().trim());
                                fee_hash.clear();
                                fee_hash.add(hashMap);
                                bundle.putSerializable(CashierDeskActivity.FEE_NORMAL, (Serializable) fee_hash);
                            }
                            bundle.putString(CashierDeskActivity.WHICH, CashierDeskActivity.FEE);
                            bundle.putString(CashierDeskActivity.PAY_AMOUNT, tvFeeTotalNum.getText().toString().trim());
                            bundle.putString(CashierDeskActivity.FEE_TITLE, tvFeeTitle.getText().toString().trim());
                            bundle.putString(CashierDeskActivity.FEE_ID, fee_id);
                            if (llYijiao.getVisibility() == View.VISIBLE) {
                                if (Math.abs(Float.parseFloat(tvDeduction.getText().toString().trim())) != 0 && tv_deduction_wenzi.getVisibility() == View.GONE) {
                                    bundle.putFloat(CashierDeskActivity.HAS_DECU, Float.parseFloat(tvDeduction.getText().toString().trim()));
                                } else {
                                    bundle.putFloat(CashierDeskActivity.HAS_DECU, 0f);
                                }
                            } else {
                                bundle.putFloat(CashierDeskActivity.HAS_DECU, 0f);
                            }
                            if (channel_codes != null)
                                bundle.putSerializable(CashierDeskActivity.FEE_WAY, (ArrayList<PayMethod>) channel_codes);
                            if (buss_type != null) {
                                bundle.putString(CashierDeskActivity.buss_type, buss_type);
                            }
                            Intent intent = new Intent(FeeDetailActivity.this, FeeInfoActivity.class);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        } else if (!isPass && llYijiao.getVisibility() == View.VISIBLE && tv_deduction_wenzi.getVisibility() == View.GONE) {
                            if (isPass) {
                                if (((FeeRealmProxy) fees.get(0)).realmGet$group_type() == null) {
                                    hashMap.clear();
                                    hashMap.put(((FeeRealmProxy) fees.get(0)).realmGet$id(), ((FeeRealmProxy) fees.get(0)).realmGet$title() + ":" + tvFeeTotalNum.getText().toString().trim());
                                    fee_hash.clear();
                                    fee_hash.add(hashMap);
                                    bundle.putSerializable(CashierDeskActivity.FEE_NORMAL, (Serializable) fee_hash);
                                }
                                bundle.putString(CashierDeskActivity.WHICH, CashierDeskActivity.FEE);
                                bundle.putString(CashierDeskActivity.PAY_AMOUNT, tvFeeTotalNum.getText().toString().trim());
                                bundle.putString(CashierDeskActivity.FEE_TITLE, tvFeeTitle.getText().toString().trim());
                                bundle.putString(CashierDeskActivity.FEE_ID, fee_id);
                                if (llYijiao.getVisibility() == View.VISIBLE) {
                                    if (Math.abs(Float.parseFloat(tvDeduction.getText().toString().trim())) != 0 && tv_deduction_wenzi.getVisibility() == View.GONE) {
                                        bundle.putFloat(CashierDeskActivity.HAS_DECU, Float.parseFloat(tvDeduction.getText().toString().trim()));
                                    } else {
                                        bundle.putFloat(CashierDeskActivity.HAS_DECU, 0f);
                                    }
                                } else {
                                    bundle.putFloat(CashierDeskActivity.HAS_DECU, 0f);
                                }
                                if (channel_codes != null)
                                    bundle.putSerializable(CashierDeskActivity.FEE_WAY, (ArrayList<PayMethod>) channel_codes);
                                if (buss_type != null) {
                                    bundle.putString(CashierDeskActivity.buss_type, buss_type);
                                }
                                Intent intent = new Intent(FeeDetailActivity.this, FeeInfoActivity.class);
                                intent.putExtras(bundle);
                                startActivity(intent);
                            } else {
                                Toast.makeText(FeeDetailActivity.this, "支付金额不能小于最小缴费金额", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(FeeDetailActivity.this, "支付金额不能小于最小缴费金额", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

        scrollview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null) {
                        manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                }
                return false;
            }
        });

        llYijiao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final RealmResults<Fee> fees = realm.where(Fee.class).equalTo("id", fee_id)
                        .findAll();
                Bundle bundle = new Bundle();
                bundle.putString(DeductionActivity.buss_type, ((FeeRealmProxy) fees.get(0)).realmGet$business_channel());
                Intent i = new Intent(FeeDetailActivity.this, DeductionActivity.class);
                i.putExtras(bundle);
                startActivity(i);

            }
        });
    }

    /**
     * 显示抵扣的超过提示
     */
    private void showDecuToastView() {
        ll_decu_w.setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏抵扣的超过提示
     */
    private void DissMissDecuToastView() {
        ll_decu_w.setVisibility(View.GONE);
    }

    private String getValidDateText(String endDate) {
        return String.format("截止时间: %s-%s-%s %s:%s",
                endDate.substring(0, 4), endDate.substring(4, 6), endDate.substring(6, 8), endDate.substring(8, 10), endDate.substring(10, 12));

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null) {
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                llBufenWa.setFocusable(true);
                llBufenWa.setFocusableInTouchMode(true);
            }
        }
        return super.onTouchEvent(event);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onResume() {
        super.onResume();
        RxBus.getDefault().toObservable().subscribe(new Action1<Object>() {
            @Override
            public void call(Object event) {
                if (event.equals("fee_succeed")) {
                    finish();
                }
                if (event.equals("succeed2")) {
                    finish();
                }
                if (event.equals("finish")) {
                    finish();
                }
            }
        });

        if (getNum() != 0) {
            tvDeduction.setText(new DecimalFormat("0.00").format(getNum()));
            tv_deduction_wenzi.setVisibility(View.GONE);
            tvFeeTotalNum.setText(new DecimalFormat("0.00").format(pureNum + getNum()));
            tvDeduction.setTextColor(Color.parseColor("#ff4867"));
        } else {
            float s = 0f;
            final RealmResults<Fee> fees = realm.where(Fee.class).equalTo("id", fee_id)
                    .findAll();
            Iterator iter = App.subFeeDeductionHashMap.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                Object value = entry.getValue();
                if (((SubFeeItem) value).business_channel.equals(((FeeRealmProxy) fees.get(0)).realmGet$business_channel())) {
                    s = s + Float.parseFloat(((SubFeeItem) value).amount);
                }
            }
            tvDeduction.setText(new DecimalFormat("0.00").format(Math.abs(s)));
            tv_deduction_wenzi.setVisibility(View.VISIBLE);
            tv_deduction_wenzi.setTextColor(R.color.axf_light_gray);
            tvDeduction.setTextColor(R.color.axf_light_gray);

            tvFeeTotalNum.setText(new DecimalFormat("0.00").format(pureNum));
        }

        if (Float.parseFloat(tvFeeTotalNum.getText().toString()) <= 0) {
            if (llYijiao.getVisibility() == View.VISIBLE) {
                if (tv_deduction_wenzi.getVisibility() == View.GONE) {
                    if (Float.parseFloat(tvFeeTotalNum.getText().toString()) == 0f) {
                        btnOk.setEnabled(true);
                        btnOk.setBackgroundResource(R.color.axf_common_blue);
                        DissMissDecuToastView();
                    } else {
                        btnOk.setEnabled(false);
                        btnOk.setBackgroundResource(R.color.axf_light_gray);
                        showDecuToastView();
                    }
                }
            } else {
                btnOk.setEnabled(false);
                btnOk.setBackgroundResource(R.color.axf_light_gray);
                DissMissDecuToastView();
            }
        } else {
            btnOk.setEnabled(true);
            btnOk.setBackgroundResource(R.color.axf_common_blue);
            DissMissDecuToastView();
        }

    }

    private Float getNum() {
        final RealmResults<Fee> fees = realm.where(Fee.class).equalTo("id", fee_id)
                .findAll();
        //计算抵扣总额
        float s = 0f;
        Iterator iter = App.subFeeDeductionHashMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            Object value = entry.getValue();
            if (((SubFeeItem) value).business_channel.equals(((FeeRealmProxy) fees.get(0)).realmGet$business_channel()) && ((SubFeeItem) value).isSelect) {
                s = s + Float.parseFloat(((SubFeeItem) value).amount);
            }
        }
        if (s != 0) {
            return s;
        } else {
            //未使用
            return 0f;
        }
    }
}
