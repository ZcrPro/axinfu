package com.zhihuianxin.xyaxf.app.unionqr_pay.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.zhihuianxin.xyaxf.app.AppConstant;
import com.axinfu.modellib.service.PaymentService;
import com.axinfu.modellib.thrift.customer.Customer;
import com.google.gson.Gson;
import com.zhihuianxin.axutil.Util;
import com.zhihuianxin.xyaxf.App;
import com.zhihuianxin.xyaxf.BaseRealmActionBarActivity;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.ApiFactory;
import com.zhihuianxin.xyaxf.app.BaseSubscriber;
import com.zhihuianxin.xyaxf.app.RetrofitFactory;
import com.zhihuianxin.xyaxf.app.unionqr_pay.contract.IunionCommitRealNameContrat;
import com.zhihuianxin.xyaxf.app.unionqr_pay.entity.UnionPayEntity;
import com.zhihuianxin.xyaxf.app.unionqr_pay.presenter.UnionCommitRealNamePresenter;
import com.zhihuianxin.xyaxf.app.unionqr_pay.presenter.UnionQrMainPresenter;
import com.zhihuianxin.xyaxf.app.unionqr_pay.view.fragment.UnionCashierFragment;
import com.zhihuianxin.xyaxf.app.utils.NetUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.realm.CustomerBaseInfoRealmProxy;
import io.realm.CustomerRealmProxy;
import io.realm.RealmResults;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Vincent on 2017/11/10.
 */

public class UnionCertificationActivity extends BaseRealmActionBarActivity implements IunionCommitRealNameContrat.ICommitRealName{

    @InjectView(R.id.inputEdit)
    EditText nameEdit;
    @InjectView(R.id.inputEditId)
    EditText idEdit;
    @InjectView(R.id.cernext)
    Button mNextBtn;

    private DisplayMetrics metrics;
    private IunionCommitRealNameContrat.ICommitRealNamePresenter presenter;
    private UnionPayEntity entity;

    private boolean isOcp;

    @Override
    protected int getContentViewId() {
        return R.layout.union_certificate_cativity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        initView();
    }

    private void initView(){
        if(getIntent().getExtras() != null && getIntent().getExtras().getSerializable(UnionCashierFragment.EXTRA_ENTITY) != null){
            entity = (UnionPayEntity) getIntent().getExtras().getSerializable(UnionCashierFragment.EXTRA_ENTITY);
        }
        if(getIntent().getExtras() != null ){
            isOcp = getIntent().getExtras().getBoolean("isOcp");
        }

        RealmResults<Customer> realmResults = realm.
                where(Customer.class).equalTo("mobile", App.mAxLoginSp.getUserMobil()).findAll();
        String stuName = ((CustomerBaseInfoRealmProxy) (((CustomerRealmProxy)
                realmResults.get(0))).realmGet$base_info()).realmGet$name();
        if(Util.isEmpty(stuName)){
            nameEdit.setText("");
        } else{
            nameEdit.setText(stuName);
        }

        new UnionCommitRealNamePresenter(this,this);
        mNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(nameEdit.getText().toString().trim().length() == 0){
                    Toast.makeText(UnionCertificationActivity.this,"请输入姓名",Toast.LENGTH_LONG).show();
                    return;
                }
                if(idEdit.getText().toString().trim().length() != 18){
                    Toast.makeText(UnionCertificationActivity.this,"请输入正确的身份证号",Toast.LENGTH_LONG).show();
                    return;
                }

                presenter.commitRealName(idEdit.getText().toString().trim(),nameEdit.getText().toString().trim());
            }
        });

    }

    @Override
    public void commitRealNameResult() {
        Toast.makeText(this,"验证通过",Toast.LENGTH_LONG).show();
        //判断是否设置过支付密码
        JudgePayPwd();
    }

    @Override
    public void setPresenter(IunionCommitRealNameContrat.ICommitRealNamePresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void loadStart() {

    }

    @Override
    public void loadError(String errorMsg) {
        Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void loadComplete() {

    }

    public void JudgePayPwd() {
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String,Object> map = new HashMap<>();
        PaymentService meService = ApiFactory.getFactory().create(PaymentService.class);
        meService.getPaymentConfig(NetUtils.getRequestParams(this,map),NetUtils.getSign(NetUtils.getRequestParams(this,map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(this,true,null) {

                    @Override
                    public void onNext(Object o) {
                        UnionQrMainPresenter.GetPaymentConfigResponse response = new Gson().fromJson(o.toString(),UnionQrMainPresenter.GetPaymentConfigResponse.class);
                        if (response.config.has_pay_password) {
                            Intent i = new Intent(UnionCertificationActivity.this,UnionHtmlActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable(UnionCashierFragment.EXTRA_ENTITY,entity);
                            bundle.putBoolean(UnionHtmlActivity.EXTRA_FROM_UPQRCASHIER,false);
                            bundle.putBoolean("isOcp",isOcp);
                            if(getIntent().getExtras()!=null &&
                                    getIntent().getExtras().getString(UnionSweptEmptyCardActivity.EXTRA_FROM_EMPTY)!=null){
                                bundle.putString(UnionSweptEmptyCardActivity.EXTRA_FROM_EMPTY,getIntent().getExtras().getString(UnionSweptEmptyCardActivity.EXTRA_FROM_EMPTY));
                            }
                            i.putExtras(bundle);
                            startActivity(i);
                            finish();
                        }else {
                            Intent i = new Intent(getActivity(), UnionSetPayPwdActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString(UnionSweptEmptyCardActivity.EXTRA_FROM_EMPTY, "1");
                            bundle.putBoolean("priceTag", true);
                            if (getIntent().getExtras() != null && getIntent().getExtras().getSerializable(UnionCashierFragment.EXTRA_ENTITY) != null)
                                bundle.putSerializable(UnionCashierFragment.EXTRA_ENTITY, getIntent().getExtras().getSerializable(UnionCashierFragment.EXTRA_ENTITY));
                            i.putExtras(bundle);
                            startActivity(i);
                            finish();
                        }
                    }
                });
    }
}
