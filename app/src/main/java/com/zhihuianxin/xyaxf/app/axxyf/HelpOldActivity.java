package com.zhihuianxin.xyaxf.app.axxyf;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhihuianxin.xyaxf.App;
import com.zhihuianxin.xyaxf.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by zcrpro on 2018/1/4.
 */

public class HelpOldActivity extends Activity {

    @InjectView(R.id.account)
    TextView account;
    @InjectView(R.id.notice_btn)
    Button noticeBtn;
    @InjectView(R.id.main_view)
    RelativeLayout mainView;
    @InjectView(R.id.title_icon)
    ImageView titleIcon;
    @InjectView(R.id.telTxt)
    TextView telTxt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.axxxy_old_help);
        ButterKnife.inject(this);
        if (App.loanAccountInfoRep!=null){
            for (int i = 0; i <App.loanAccountInfoRep.loan_account_info.valid_loan_way_account_info.size() ; i++) {
                if (App.loanAccountInfoRep.loan_account_info.valid_loan_way_account_info.get(i).loan_way.type.equals("GuiYangCreditLoanPay")){
                    account.setText(optimizeAccount(App.loanAccountInfoRep.loan_account_info.valid_loan_way_account_info.get(i).card_no));
                }
            }

        }
        findViewById(R.id.mainview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
//        String a = "<font color='#c4c4c4'>默认还账单，若要提前还贷款本金，需拨打</font><font color='#208af0'>400-288-4028</font><font color='#c4c4c4'>申请</font>";
//        telTxt.setText(Html.fromHtml(a));

        noticeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
}
