package com.zhihuianxin.xyaxf.app.axxyf;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhihuianxin.xyaxf.app.AppConstant;
import com.axinfu.modellib.service.LoanService;
import com.axinfu.modellib.thrift.base.BaseResponse;
import com.google.gson.Gson;
import com.zhihuianxin.xyaxf.app.base.axutil.Util;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.ApiFactory;
import com.zhihuianxin.xyaxf.app.BaseSubscriber;
import com.zhihuianxin.xyaxf.app.RetrofitFactory;
import com.zhihuianxin.xyaxf.app.utils.NetUtils;
import com.zhihuianxin.xyaxf.app.view.LoadingDialog;
import com.zhihuianxin.xyaxf.app.view.SwipeRefreshAndLoadMoreLayoutStick;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by zcrpro on 2017/12/27.
 */

public class AxxyfActivity extends Activity {

    @InjectView(R.id.ic_back)
    ImageView icBack;
    @InjectView(R.id.title)
    TextView title;
    @InjectView(R.id.ll)
    LinearLayout ll;
    @InjectView(R.id.img)
    ImageView img;
    @InjectView(R.id.right)
    ImageView right;
    @InjectView(R.id.img2)
    ImageView img2;
    @InjectView(R.id.right2)
    ImageView right2;
    @InjectView(R.id.max_residual_capital)
    TextView maxResidualCapital;
    @InjectView(R.id.credit_max_amt)
    TextView creditMaxAmt;
    @InjectView(R.id.card_no)
    TextView cardNo;
    @InjectView(R.id.left_bottom_txt)
    TextView leftBottomTxt;
    @InjectView(R.id.right_top_txt)
    TextView rightTopTxt;
    @InjectView(R.id.ll_lixi)
    RelativeLayout llLixi;
    @InjectView(R.id.ll_loan)
    RelativeLayout llLoan;
    @InjectView(R.id.instalment_residual_capital)
    TextView instalmentResidualCapital;
    @InjectView(R.id.right_bottom_txt)
    TextView rightbottomTxt;
    @InjectView(R.id.tv_wenzi2)
    TextView tvWenzi2;
    @InjectView(R.id.account_out_bill_date)
    TextView accountOutBillDate;
    @InjectView(R.id.swipe)
    SwipeRefreshAndLoadMoreLayoutStick swipe;
    private LoadingDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.axxyf_activity);
        ButterKnife.inject(this);

        initViews();
    }

    @SuppressLint("SetTextI18n")
    private void initViews() {
        dialog = new LoadingDialog(this);
        dialog.show();
        getAccountPayMethodInfo();
//        for (int i = 0; i < App.loanAccountInfoRep.loan_account_info.valid_loan_way_account_info.size(); i++) {
//            if (App.loanAccountInfoRep.loan_account_info.valid_loan_way_account_info.get(i).loan_way.type.equals("GuiYangCreditLoanPay")) {
//                maxResidualCapital.setText("¥" + App.loanAccountInfoRep.loan_account_info.valid_loan_way_account_info.get(i).banlance);
//                creditMaxAmt.setText(App.loanAccountInfoRep.loan_account_info.valid_loan_way_account_info.get(i).credit_max_amt);
//                cardNo.setText(App.loanAccountInfoRep.loan_account_info.valid_loan_way_account_info.get(i).card_no);
//                if(Util.isEmpty(App.loanAccountInfoRep.loan_account_info.valid_loan_way_account_info.get(i).repayment_end_date)){
//                    leftBottomTxt.setVisibility(View.GONE);
//                } else{
//                    leftBottomTxt.setVisibility(View.VISIBLE);
//                    leftBottomTxt.setText("最晚还款日："+App.loanAccountInfoRep.loan_account_info.valid_loan_way_account_info.get(i).repayment_end_date);
//                }
//                if (Float.parseFloat(App.loanAccountInfoRep.loan_account_info.valid_loan_way_account_info.get(i).last_period_not_repayment_amt) == 0) {
//                    rightTopTxt.setVisibility(View.GONE);
//                    rightbottomTxt.setText("本期已还清");// 本期剩余应还
//                } else {
//                    rightTopTxt.setVisibility(View.VISIBLE);
//                    rightTopTxt.setText(App.loanAccountInfoRep.loan_account_info.valid_loan_way_account_info.get(i).last_period_not_repayment_amt);
//                    rightbottomTxt.setText("本期剩余应还");
//                }

//                if (Float.parseFloat(App.loanAccountInfoRep.loan_account_info.valid_loan_way_account_info.get(i).instalment_residual_capital) == 0) {
//                    instalmentResidualCapital.setVisibility(View.GONE);
//                    tvWenzi2.setText("暂无贷款或本期已还清");
//                } else {
//                    instalmentResidualCapital.setText(App.loanAccountInfoRep.loan_account_info.valid_loan_way_account_info.get(i).instalment_residual_capital);
//                }
//
//                accountOutBillDate.setText("每月" + (App.loanAccountInfoRep.loan_account_info.valid_loan_way_account_info.get(i).account_out_bill_date + "日"));
//            }
//        }

        icBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        llLixi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AxxyfActivity.this, InterestListActivity.class));
            }
        });

        llLoan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AxxyfActivity.this, LoansActivity.class));
            }
        });

        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAccountPayMethodInfo();
            }
        });
    }

    /**
     * 获取账户状态
     */
    private void getAccountPayMethodInfo() {
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String, Object> map = new HashMap<>();
        map.put("loan_way_type", "GuiYangCreditLoanPay");
        LoanService loanService = ApiFactory.getFactory().create(LoanService.class);
        loanService.get_account_info(NetUtils.getRequestParams(this, map), NetUtils.getSign(NetUtils.getRequestParams(this, map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(this, false, null) {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onNext(Object o) {
                        if (swipe.isRefreshing()) {
                            swipe.setRefreshing(false);
                        }
                        final LoanAccountInfoRep loanAccountInfoRep = new Gson().fromJson(o.toString(), LoanAccountInfoRep.class);

                        if (loanAccountInfoRep != null && loanAccountInfoRep.resp.resp_code.equals(AppConstant.SUCCESS)) {
                            //获取到了账户状态
                            if(loanAccountInfoRep.loan_account_info != null){
                                for (int i = 0; i < loanAccountInfoRep.loan_account_info.valid_loan_way_account_info.size(); i++) {
                                    if (loanAccountInfoRep.loan_account_info.valid_loan_way_account_info.get(i).loan_way.type.equals("GuiYangCreditLoanPay")) {
                                        maxResidualCapital.setText("¥" + loanAccountInfoRep.loan_account_info.valid_loan_way_account_info.get(i).banlance);
                                        creditMaxAmt.setText(loanAccountInfoRep.loan_account_info.valid_loan_way_account_info.get(i).credit_max_amt);
                                        cardNo.setText(loanAccountInfoRep.loan_account_info.valid_loan_way_account_info.get(i).card_no);

                                        if (Float.parseFloat(loanAccountInfoRep.loan_account_info.valid_loan_way_account_info.get(i).
                                                last_period_not_repayment_amt) <= 0) {// 没有贷款或者贷款了已经还清
                                            if(Util.isEmpty(loanAccountInfoRep.loan_account_info.valid_loan_way_account_info.get(i).
                                                    repayment_end_date)){//没有贷款
                                                rightTopTxt.setVisibility(View.GONE);
                                                leftBottomTxt.setVisibility(View.GONE);
                                                rightbottomTxt.setVisibility(View.VISIBLE);
                                                rightbottomTxt.setText("暂无账单信息");
                                            } else{//贷款了但已还清
                                                leftBottomTxt.setVisibility(View.VISIBLE);
                                                leftBottomTxt.setText("最晚还款日："+loanAccountInfoRep.loan_account_info.valid_loan_way_account_info.get(i).repayment_end_date);
                                                rightTopTxt.setVisibility(View.GONE);
                                                rightbottomTxt.setVisibility(View.VISIBLE);
                                                rightbottomTxt.setText("本期已还清");
                                            }
                                        }else {// 有贷款
                                            rightTopTxt.setVisibility(View.VISIBLE);
                                            rightTopTxt.setText(loanAccountInfoRep.loan_account_info.valid_loan_way_account_info.get(i).last_period_not_repayment_amt);
                                            rightbottomTxt.setVisibility(View.VISIBLE);
                                            rightbottomTxt.setText("本期剩余应还");
                                            leftBottomTxt.setVisibility(View.VISIBLE);
                                            leftBottomTxt.setText("最晚还款日："+loanAccountInfoRep.loan_account_info.valid_loan_way_account_info.get(i).repayment_end_date);
                                        }

                                        if (Float.parseFloat(loanAccountInfoRep.loan_account_info.valid_loan_way_account_info.get(i).instalment_residual_capital) == 0) {
                                            instalmentResidualCapital.setVisibility(View.GONE);
                                            tvWenzi2.setText("暂无贷款或贷款均已到期");
                                        } else {
                                            instalmentResidualCapital.setText(loanAccountInfoRep.loan_account_info.valid_loan_way_account_info.get(i).instalment_residual_capital);
                                            tvWenzi2.setText("未到期贷款本金合计");
                                        }
                                        accountOutBillDate.setText("每月" + (loanAccountInfoRep.loan_account_info.valid_loan_way_account_info.get(i).account_out_bill_date + "日"));
                                    }
                                }
                            }
                        }
                        dialog.dismiss();
                    }
                });
    }

    public class LoanAccountInfoRep {
        public BaseResponse resp;
        public LoanAccountInfo loan_account_info;
        public String realname_auth_status;
    }

    public class LoanAccountInfo {
        public BaseResponse resp;
        public String id_card_no;
        public String name;
        public List<LoanWayAccountInfo> valid_loan_way_account_info;
    }

    public class LoanWayAccountInfo {
        public String card_no;                                            // 卡号
        public String credit_max_amt;                                        // 授信最大金额额度
        public String loan_amt;                                            // 借款总金额
        public LoanWay loan_way;                                        // 通道信息
        public String status;
        public String instalment_residual_capital;
        public String banlance;
        public String repayment_end_date;
        public String last_period_not_repayment_amt;
        public String account_out_bill_date;
    }

    public class LoanWay {
        public String name;                        // 通道名称
        public String type;                    // 通道类型
        public String remark;            // 通道备注
        public String min_loan_amt;                // 最低贷款金额
    }
}
