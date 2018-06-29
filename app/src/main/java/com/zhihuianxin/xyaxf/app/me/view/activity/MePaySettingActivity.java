package com.zhihuianxin.xyaxf.app.me.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import modellib.thrift.base.BaseResponse;
import modellib.thrift.payment.PaymentOrder;
import modellib.thrift.unqr.PaymentConfig;
import modellib.thrift.unqr.RealName;
import modellib.thrift.unqr.RealNameAuthStatus;
import modellib.thrift.unqr.UPCoupon;
import modellib.thrift.unqr.UPQROrder;
import com.zhihuianxin.xyaxf.App;
import com.zhihuianxin.xyaxf.BaseRealmActionBarActivity;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.unionqr_pay.contract.IunionPayPwdContract;
import com.zhihuianxin.xyaxf.app.unionqr_pay.presenter.UnionPayPwdPresenter;
import com.zhihuianxin.xyaxf.app.unionqr_pay.view.activity.UnionForgetPayPwdCodeActivity;
import com.zhihuianxin.xyaxf.app.unionqr_pay.view.activity.UnionForgetPwdOriActivity;
import com.zhihuianxin.xyaxf.app.unionqr_pay.view.activity.UnionSwepLittlePayInputPwdActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Vincent on 2017/11/11.
 */

public class MePaySettingActivity extends BaseRealmActionBarActivity implements IunionPayPwdContract.IJudgePayPwd{
    @InjectView(R.id.modify_pwd)
    View modifyView;
    @InjectView(R.id.forgetPwdId)
    View forgetView;
    @InjectView(R.id.littertxt)
    TextView litterTxt;
//    @InjectView(R.id.littlepayImg)
//    ImageView littlepayImg;
    @InjectView(R.id.littleswitch)
    Switch littleswitch;

    private IunionPayPwdContract.IJudgePayPwdPresenter presenter;
    private PaymentConfig config;

    @Override
    protected int getContentViewId() {
        return R.layout.me_pay_set_activity;
    }

    @Override
    protected void onResume() {
        super.onResume();
        new UnionPayPwdPresenter(this,this);
        presenter.JudgePayPwd();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
    }

    private void initViews(){
        modifyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                App.no_add_card = true;
                App.no_add_card_and_pay=false;
                startActivity(new Intent(MePaySettingActivity.this, UnionForgetPwdOriActivity.class));
            }
        });
        forgetView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                App.no_add_card = true;
                App.no_add_card_and_pay=false;
                presenter.getRealName();
            }
        });
        litterTxt.setText("扫码向商户付款时，"+config.pin_free_amount+"元以内金额免输入支付密码");
        if(config.pin_free){
            littleswitch.setChecked(true);
        } else{
            littleswitch.setChecked(false);
        }
        littleswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!buttonView.isPressed())return;

                if(isChecked){// 关闭状态，需要打开 isChecked 是将要变为的状态
                    Intent intent = new Intent(MePaySettingActivity.this,
                            UnionSwepLittlePayInputPwdActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(UnionSwepLittlePayInputPwdActivity.EXTRA_FREE_AMOUNT,config.pin_free_amount);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    App.no_add_card = true;
                    App.no_add_card_and_pay=false;
                } else{// 开启状态，需要关闭
                    Intent intent = new Intent(MePaySettingActivity.this,
                            UnionSwepLittlePayInputPwdActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(UnionSwepLittlePayInputPwdActivity.EXTRA_FREE_AMOUNT,config.pin_free_amount);
                    bundle.putBoolean("close",true);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    App.no_add_card = true;
                    App.no_add_card_and_pay=false;
                }
            }
        });
    }

    @Override
    public void getRealNameResult(RealName realName) {
            if(realName.status.equals(RealNameAuthStatus.OK.name())){
                startActivity(new Intent(this, UnionForgetPayPwdCodeActivity.class));
            } else{
                Intent i = new Intent(this, UnionForgetPayPwdCodeActivity.class);
                Bundle b = new Bundle();
                b.putBoolean(UnionForgetPayPwdCodeActivity.EXTRA_SHOWIMG,false);
                i.putExtras(b);
                startActivity(i);
            }
    }
    @Override
    public void judgePayPwdResult(PaymentConfig config) {
        this.config = config;
        initViews();
    }
    @Override
    public void setPresenter(IunionPayPwdContract.IJudgePayPwdPresenter presenter) {
        this.presenter = presenter;
    }
    @Override
    public void verifyPayPwdResult(boolean is_match,int left_retry_count) {}
    @Override
    public void setPayPwdResult(BaseResponse baseResponse) {}
    @Override
    public void modifyPayPwdResult(int left_retry_count) {}
    @Override
    public void slearPayPwdResult() {}
    @Override
    public void payOrderResult(PaymentOrder order) {}
    @Override
    public void getUpQrOrderResult(UPQROrder upqrOrder) {}
    @Override
    public void getUpQrCouponResult(UPCoupon upCoupon) {}
    @Override
    public void setPinFreeResult(boolean is_match, int left_retry_count) {}
    @Override
    public void loadStart() {}
    @Override
    public void loadError(String errorMsg) {}
    @Override
    public void loadComplete() {}
}
