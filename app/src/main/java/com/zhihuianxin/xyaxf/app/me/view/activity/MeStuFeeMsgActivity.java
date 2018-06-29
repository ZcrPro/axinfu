package com.zhihuianxin.xyaxf.app.me.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import modellib.thrift.customer.Customer;
import com.zhihuianxin.xyaxf.app.base.axutil.Util;
import com.zhihuianxin.xyaxf.App;
import com.zhihuianxin.xyaxf.BaseRealmActionBarActivity;
import com.zhihuianxin.xyaxf.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import io.realm.CustomerRealmProxy;
import io.realm.FeeAccountRealmProxy;

/**
 * Created by Vincent on 2016/10/20.
 */

public class MeStuFeeMsgActivity extends BaseRealmActionBarActivity{
    @InjectView(R.id.card_name)
    TextView mXhText;
    @InjectView(R.id.xh_value)
    TextView mXhTextValue;
    @InjectView(R.id.id_value)
    TextView mIdValue;
    @InjectView(R.id.me_stu_fee_card_id)
    View mFeeStuIdModifyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFeeMsg();
    }

    private void loadFeeMsg() {
        String idNum = ((FeeAccountRealmProxy)((CustomerRealmProxy)realm.where(Customer.class).equalTo("mobile",App.mAxLoginSp.getUserMobil()).findAll().get(0)).realmGet$fee_account()).realmGet$id_card_no();
        mIdValue.setText(getIDValue(idNum));
        final String status =((FeeAccountRealmProxy)((CustomerRealmProxy)realm.where(Customer.class).equalTo("mobile", App.mAxLoginSp.getUserMobil())
                .findAll().get(0)).realmGet$fee_account()).realmGet$status();
        if(!Util.isEmpty(status) && status.equals("OK")){
            mFeeStuIdModifyView.setVisibility(View.VISIBLE);
            mXhText.setText("学号");// 缴费的学号名称是固定的，一卡通的是配置的
            String cardValue = ((FeeAccountRealmProxy)((CustomerRealmProxy)realm.where(Customer.class).equalTo("mobile", App.mAxLoginSp.getUserMobil())
                    .findAll().get(0)).realmGet$fee_account()).realmGet$student_no();
            if(!Util.isEmpty(cardValue)){
                mXhTextValue.setText(cardValue.length() >= 2 ?
                        Util.getXingHao(cardValue.length() - 2) + cardValue.substring(cardValue.length()-2,cardValue.length()):cardValue);
            }
        } else{
            mFeeStuIdModifyView.setVisibility(View.GONE);
        }
    }

    private String getIDValue(String oriID){
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

    @Override
    protected int getContentViewId() {
        return R.layout.me_stu_fee_msg_activity;
    }

    @OnClick(R.id.me_stu_fee_card_id)
    public void onBtnMeStuFeeCard(){
        startActivity(new Intent(this,MeStuXHModifyActivity.class));
    }

    @OnClick(R.id.me_stu_fee_id)
    public void onBtnMeStuId(){
        startActivity(new Intent(this,MeStuIdModifyActivity.class));
    }

    @Override
    public boolean leftButtonEnabled() {
        return true;
    }

    @Override
    public void onLeftButtonClick(View view) {
        finish();
    }

    @Override
    public int getLeftButtonImageId() {
        return R.drawable.back;
    }
}
