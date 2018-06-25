package com.zhihuianxin.xyaxf.app.me.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.axinfu.modellib.thrift.customer.Customer;
import com.zhihuianxin.axutil.Util;
import com.zhihuianxin.xyaxf.App;
import com.zhihuianxin.xyaxf.BaseRealmActionBarActivity;
import com.zhihuianxin.xyaxf.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import io.realm.AccountVerifyItemRealmProxy;
import io.realm.CustomerRealmProxy;
import io.realm.ECardAccountRealmProxy;

/**
 * Created by zcrpro on 2016/11/21.
 */
public class MeStuEcardMsgActivity extends BaseRealmActionBarActivity {


    @InjectView(R.id.ver_title)
    TextView verTitle;
    @InjectView(R.id.ver_value)
    TextView verValue;

    @Override
    protected int getContentViewId() {
        return R.layout.me_stu_ecard_msg_activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadEcardMsg();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.reset(this);
    }

    /**
     * 查询ecard的信息
     */
    private void loadEcardMsg() {
        final Customer customer = realm.where(Customer.class).equalTo("mobile", App.mAxLoginSp.getUserMobil())
                .findAll().get(0);
        if (customer != null)
            verTitle.setText(((AccountVerifyItemRealmProxy) ((ECardAccountRealmProxy) ((CustomerRealmProxy) customer).realmGet$ecard_account()).realmGet$account_no_verify_config()).realmGet$title());
            String cardValue = ((ECardAccountRealmProxy)((CustomerRealmProxy)realm.where(Customer.class).equalTo("mobile",App.mAxLoginSp.getUserMobil()).findAll().get(0)).realmGet$ecard_account()).realmGet$account_no();
            if(cardValue.length() == 18){
                verValue.setText(cardValue.substring(0,2)+"**************" + cardValue.substring(cardValue.length() - 2,cardValue.length()));
            } else{
                verValue.setText(cardValue.length() >= 2? Util.getXingHao(cardValue.length() - 2)+cardValue.substring(cardValue.length()-2,cardValue.length()):cardValue);
            }

    }

    @OnClick(R.id.me_stu_card_id)
    public void onBtnStuChangeEcardMsg(){
        startActivity(new Intent(this,MeStuAccountModifyActivity.class));
    }
}
