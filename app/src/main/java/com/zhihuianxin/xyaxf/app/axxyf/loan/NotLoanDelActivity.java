package com.zhihuianxin.xyaxf.app.axxyf.loan;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhihuianxin.xyaxf.app.AppConstant;
import com.axinfu.modellib.service.LoanService;
import com.google.gson.Gson;
import com.zhihuianxin.xyaxf.BaseRealmActionBarActivity;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.ApiFactory;
import com.zhihuianxin.xyaxf.app.BaseSubscriber;
import com.zhihuianxin.xyaxf.app.RetrofitFactory;
import com.zhihuianxin.xyaxf.app.axxyf.HelpActivity;
import com.zhihuianxin.xyaxf.app.utils.NetUtils;
import com.zhihuianxin.xyaxf.app.utils.RxBus;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by zcrpro on 2018/1/3.
 */

public class NotLoanDelActivity extends BaseRealmActionBarActivity {

    @InjectView(R.id.residual_should_repayment_amount)
    TextView residual_should_repayment_amount;
    @InjectView(R.id.ll1)
    LinearLayout ll1;
    @InjectView(R.id.residual_capital)
    TextView residualCapital;
    @InjectView(R.id.loan_amount)
    TextView loanAmount;
    @InjectView(R.id.instalment_trade_date)
    TextView instalmentTradeDate;
    @InjectView(R.id.loan_date)
    TextView loanDate;
    @InjectView(R.id.loan_days)
    TextView loanDays;
    @InjectView(R.id.loan_period)
    TextView loanPeriod;
    @InjectView(R.id.day_loan_rate)
    TextView dayLoanRate;
    @InjectView(R.id.outerInterest_amount)
    TextView outerInterestAmount;
    @InjectView(R.id.current_interest)
    TextView currentInterest;
    @InjectView(R.id.current_untilLoan_day_interest)
    TextView currentUntilLoanDayInterest;
    @InjectView(R.id.status)
    TextView status;
    @InjectView(R.id.btn_ok)
    Button btnOk;

    private LoanFragment.LoanBills loanBills;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
        getBundle();

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                repaymentClick();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        RxBus.getDefault().send("refreshLoanList");
    }

    private void repaymentClick(){
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String, Object> map = new HashMap<>();
        map.put("repayment_type", "1");
        map.put("instalment_apply_no",loanBills.instalment_apply_no);
        map.put("instalment_sequence_no",loanBills.instalment_sequence_no);
        map.put("loan_way_type","GuiYangCreditLoanPay");
        LoanService loanService = ApiFactory.getFactory().create(LoanService.class);
        loanService.test_calculation_loan_bills(NetUtils.getRequestParams(this, map), NetUtils.getSign(NetUtils.getRequestParams(this, map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(this, true, null) {

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                    }

                    @Override
                    public void onNext(Object o) {
                        HelpActivity.ReplyResponse response = new Gson().fromJson(o.toString(),HelpActivity.ReplyResponse.class);
                        Intent intent = new Intent(NotLoanDelActivity.this, HelpActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(HelpActivity.EXTRA_TEST_CAL_RESULT, response.repayment_info);
                        bundle.putSerializable(HelpActivity.EXTRA_LOANBILLS, getIntent().getExtras().getSerializable("LoanBills"));
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });
    }

    private void getBundle() {
        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        if (bundle.getSerializable("LoanBills") != null) {
            loanBills = (LoanFragment.LoanBills) bundle.getSerializable("LoanBills");
        }

        residual_should_repayment_amount.setText(loanBills.residual_should_repayment_amount);
        residualCapital.setText(loanBills.residual_capital);
        loanAmount.setText(loanBills.loan_amount);
        instalmentTradeDate.setText(loanBills.instalment_trade_date);
        outerInterestAmount.setText(loanBills.outerInterest_amount);
        loanDate.setText(loanBills.loan_date);
        loanDays.setText(loanBills.loan_days+"天");
        loanPeriod.setText(loanBills.loan_period+"期");
        dayLoanRate.setText(loanBills.day_loan_rate);
        currentInterest.setText(loanBills.current_interest);
        currentUntilLoanDayInterest.setText(loanBills.current_untilLoan_day_interest);
        if (loanBills.status.equals("upcoming")){
            status.setTextColor(Color.rgb(255,212,77));
            status.setText("即将到期");
        }else if (loanBills.status.equals("overdue")){
            status.setTextColor(Color.rgb(171,43,56));
            status.setText("已逾期");
        }else {
            status.setTextColor(Color.rgb(22,116,22));
            status.setText("未到期");
        }

    }

    @Override
    protected int getContentViewId() {
        return R.layout.not_loan_del_activity;
    }
}
