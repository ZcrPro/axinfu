package com.zhihuianxin.xyaxf.app.axxyf.loan;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhihuianxin.xyaxf.BaseRealmActionBarActivity;
import com.zhihuianxin.xyaxf.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by zcrpro on 2018/1/3.
 */

public class LoanDelActivity extends BaseRealmActionBarActivity {

    @InjectView(R.id.total_repayment_amount)
    TextView totalRepaymentAmount;
    @InjectView(R.id.ll1)
    LinearLayout ll1;
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
    @InjectView(R.id.repaymentEndDate)
    TextView repaymentEndDate;

    private LoanFragment.LoanBills loanBills;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
        getBundle();
    }

    @Override
    protected int getContentViewId() {
        return R.layout.loan_del_activity;
    }

    private void getBundle() {
        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        if (bundle.getSerializable("LoanBills") != null) {
            loanBills = (LoanFragment.LoanBills) bundle.getSerializable("LoanBills");
        }

        totalRepaymentAmount.setText(loanBills.loan_amount);
        //loanAmount.setText(loanBills.loan_amount);
        instalmentTradeDate.setText(loanBills.instalment_trade_date);
        loanDate.setText(loanBills.loan_date);
        loanDays.setText(loanBills.loan_days + "天");
        loanPeriod.setText(loanBills.loan_period + "期");
        dayLoanRate.setText(loanBills.day_loan_rate);
        repaymentEndDate.setText(loanBills.instalment_status_change_date);

    }
}
