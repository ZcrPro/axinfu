package com.zhihuianxin.xyaxf.app.axxyf;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.zhihuianxin.xyaxf.app.AppConstant;
import com.axinfu.modellib.service.LoanService;
import com.axinfu.modellib.thrift.base.BaseResponse;
import com.axinfu.modellib.thrift.loan.RepaymentInfo;
import com.xyaxf.axpay.Util;
import com.zhihuianxin.xyaxf.App;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.ApiFactory;
import com.zhihuianxin.xyaxf.app.BaseSubscriber;
import com.zhihuianxin.xyaxf.app.RetrofitFactory;
import com.zhihuianxin.xyaxf.app.axxyf.loan.LoanFragment;
import com.zhihuianxin.xyaxf.app.utils.NetUtils;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by zcrpro on 2018/1/4.
 */

public class HelpActivity extends Activity {
    public static final String EXTRA_LOANBILLS = "helpLoanBills";
    public static final String EXTRA_TEST_CAL_RESULT = "cal_result";

    @InjectView(R.id.account)
    TextView account;
    @InjectView(R.id.notice_btn)
    Button noticeBtn;
    @InjectView(R.id.telTxt)
    TextView telTxt;
    @InjectView(R.id.benqibenjin)
    TextView benjibenjinTxt;
    @InjectView(R.id.weichulixi)
    TextView weichulixiTxt;
    @InjectView(R.id.zhanghuyue)
    TextView zhanghuyueTxt;
    @InjectView(R.id.shengyuTotalTxt)
    TextView shengyuTotalTxt;
    @InjectView(R.id.shengyuyinghuan)
    TextView shengyuyinghuanTxt;
    @InjectView(R.id.buzulinid)
    View buzuView;

    private LoanFragment.LoanBills loanBills;

    public class ReplyResponse{
        public RepaymentInfo repayment_info;
        public BaseResponse resp;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.axxyf_help);
        ButterKnife.inject(this);

        loanBills = (LoanFragment.LoanBills) getIntent().getExtras().getSerializable(EXTRA_LOANBILLS);

        initView();

        calculate((RepaymentInfo) getIntent().getExtras().getSerializable(EXTRA_TEST_CAL_RESULT));
    }

    @SuppressLint("SetTextI18n")
    private void initView(){
        if (App.loanAccountInfoRep!=null){
            for (int i = 0; i <App.loanAccountInfoRep.loan_account_info.valid_loan_way_account_info.size() ; i++) {
                if (App.loanAccountInfoRep.loan_account_info.valid_loan_way_account_info.get(i).loan_way.type.equals("GuiYangCreditLoanPay")){
                    account.setText(optimizeAccount(App.loanAccountInfoRep.loan_account_info.valid_loan_way_account_info.get(i).card_no));
                }
            }
        }
        String a = "<font color='#c4c4c4'>使用微信钱包还信用卡，或使用网上银行，手机银行等电子渠道向信用卡还款.</font><font color='#208af0'>客服电话：400-1196-033</font>";
        telTxt.setText(Html.fromHtml(a));
        benjibenjinTxt.setText("剩余本金: "+loanBills.residual_capital);
        weichulixiTxt.setText("未出帐单利息: "+loanBills.current_interest);
        DecimalFormat decimalFormat = new DecimalFormat("############0.00");
        String total = decimalFormat.format(Float.parseFloat(loanBills.residual_capital)+Float.parseFloat(loanBills.current_interest));
        shengyuyinghuanTxt.setText(total);
        noticeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(((Button)view).getText().equals("知道了")){
                    finish();
                } else{
                    adviceLoan();
                }
            }
        });
        findViewById(R.id.mainview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private String optimizeAccount(String account){
        String optimAccount = "";
        for(int i = 0;i < account.length();i++){
            if(i % 4 == 0 && i != 0){
                optimAccount += " ";
                optimAccount += account.charAt(i)+"";
            } else{
                optimAccount += account.charAt(i)+"";
            }
        }
        return optimAccount;
    }

    private void calculate(RepaymentInfo repaymentInfo){
        DecimalFormat decimalFormat = new DecimalFormat("############0.00");
        String renxuhuan = decimalFormat.format(
                Float.parseFloat(loanBills.residual_capital)+
                Float.parseFloat(loanBills.current_interest)+
                        Float.parseFloat(repaymentInfo.capital_balance));
        shengyuTotalTxt.setText("您仍需还"+renxuhuan+"元");

        if(!Util.isEmpty(repaymentInfo.capital_balance) && repaymentInfo.capital_balance.length() >= 1){
            zhanghuyueTxt.setText(Float.parseFloat(repaymentInfo.capital_balance)==0?repaymentInfo.capital_balance.substring(1):repaymentInfo.capital_balance);
        }
        if(Float.parseFloat(loanBills.residual_capital)+Float.parseFloat(loanBills.current_interest)+Float.parseFloat(repaymentInfo.capital_balance) > 0){
            buzuView.setVisibility(View.VISIBLE);
            noticeBtn.setText("知道了");
        } else{
            buzuView.setVisibility(View.GONE);
            noticeBtn.setText("申请提前还款");
        }
    }

    private void repaySuccess(){
        Intent intent = new Intent(this,ReplymentResultActivity.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean(ReplymentResultActivity.EXTRA_RESULT,true);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    private void repayError(String msg){
        Intent intent = new Intent(this,ReplymentResultActivity.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean(ReplymentResultActivity.EXTRA_RESULT,false);
        bundle.putString(ReplymentResultActivity.EXTRA_REASON,msg);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    private void adviceLoan(){
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String, Object> map = new HashMap<>();
        map.put("repayment_type", "1");
        map.put("instalment_apply_no",loanBills.instalment_apply_no);
        map.put("instalment_sequence_no",loanBills.instalment_sequence_no);
        map.put("loan_way_type","GuiYangCreditLoanPay");
        LoanService loanService = ApiFactory.getFactory().create(LoanService.class);
        loanService.advice_loan_bills(NetUtils.getRequestParams(this, map), NetUtils.getSign(NetUtils.getRequestParams(this, map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(this, true, null) {

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        repayError(e.getMessage());
                    }

                    @Override
                    public void onNext(Object o) {
                        repaySuccess();
                    }
                });

    }
}
