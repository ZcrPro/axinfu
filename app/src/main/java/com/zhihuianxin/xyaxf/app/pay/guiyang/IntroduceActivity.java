package com.zhihuianxin.xyaxf.app.pay.guiyang;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.zhihuianxin.xyaxf.app.AppConstant;
import com.axinfu.modellib.service.LoanService;
import com.axinfu.modellib.thrift.base.BaseResponse;
import com.google.gson.Gson;
import com.zhihuianxin.xyaxf.BaseRealmActionBarActivity;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.ApiFactory;
import com.zhihuianxin.xyaxf.app.BaseSubscriber;
import com.zhihuianxin.xyaxf.app.RetrofitFactory;
import com.zhihuianxin.xyaxf.app.pay.guiyang.status.ErrorActivity;
import com.zhihuianxin.xyaxf.app.pay.guiyang.status.ProcessingActivity;
import com.zhihuianxin.xyaxf.app.utils.NetUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.realm.RealmObject;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by zcrpro on 2017/11/13.
 */

public class IntroduceActivity extends BaseRealmActionBarActivity {

    @InjectView(R.id.btn_ok)
    ImageView btnOk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getBundle();
            }
        });
    }

    @Override
    protected int getContentViewId() {
        return R.layout.axxyf_introduce_activity;
    }


    private void getBundle() {
        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        if (bundle.getBoolean("isVrName")) {
            //实名已经认证
            if (bundle.getString("idCard") != null && !TextUtils.isEmpty(bundle.getString("idCard"))) {
                checkApprovalInfo(bundle.getString("idCard"),bundle.getString("name"),bundle.getFloat("amount"));
            }
        } else {
            //未实名认证
            Bundle bundle2 = new Bundle();
            if (bundle.getString("name") != null && !TextUtils.isEmpty(bundle.getString("name"))) {
                bundle2.putString("name", bundle.getString("name"));
            }
            if (bundle.getString("idCard") != null && !TextUtils.isEmpty(bundle.getString("idCard"))) {
                bundle2.putString("idCard", bundle.getString("idCard"));
            }
            if (bundle.getFloat("amount") != 0) {
                bundle2.putFloat("amount", bundle.getFloat("amount"));
            }
            Intent intent = new Intent(this, AutonymActivity.class);
            intent.putExtras(bundle2);
            startActivity(intent);
            finish();
        }
    }

    private void checkApprovalInfo(final String idCard,final String name,final float amount) {
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String, Object> map = new HashMap<>();
        map.put("loan_way_type", "GuiYangCreditLoanPay");
        LoanService loanService = ApiFactory.getFactory().create(LoanService.class);
        loanService.check_pre_approval(NetUtils.getRequestParams(IntroduceActivity.this, map), NetUtils.getSign(NetUtils.getRequestParams(IntroduceActivity.this, map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(IntroduceActivity.this, true, null) {
                    @Override
                    public void onNext(Object o) {
                        final ApprovalResponse approvalResponse = new Gson().fromJson(o.toString(), ApprovalResponse.class);
                        if (approvalResponse.resp.resp_code.equals(AppConstant.SUCCESS)) {
                            //判断预授信信息
                            if (approvalResponse.status.equals("Success")) {
                                Bundle bundle = new Bundle();
                                bundle.putString("name", name);
                                bundle.putString("idCard", idCard);
                                bundle.putFloat("amount", amount);
                                Intent intent = new Intent(IntroduceActivity.this, AutonymSuccActivity.class);
                                intent.putExtras(bundle);
                                IntroduceActivity.this.startActivity(intent);
                                finish();
                            } else if (approvalResponse.status.equals("Processing")) {
                                (IntroduceActivity.this).startActivity(new Intent((IntroduceActivity.this), ProcessingActivity.class));
                                finish();
                            } else if (approvalResponse.status.equals("Error")) {
                                (IntroduceActivity.this).startActivity(new Intent((IntroduceActivity.this), ErrorActivity.class));
                                finish();
                            } else if (approvalResponse.status.equals("AccountNotExist")) {
                                //进入预售信息补全界面
                                Bundle bundle = new Bundle();
                                bundle.putString("name", name);
                                bundle.putString("idCard", idCard);
                                bundle.putFloat("amount", amount);
                                Intent intent = new Intent(IntroduceActivity.this, AutonymSuccActivity.class);
                                intent.putExtras(bundle);
                                IntroduceActivity.this.startActivity(intent);
                                finish();
                            } else if (approvalResponse.status.equals("RealNameAuthError")) {
                                //实名认证失败
                                (IntroduceActivity.this).startActivity(new Intent((IntroduceActivity.this), ErrorActivity.class));
                                finish();
                            } else {

                            }
                        }
                    }
                });
    }

    public static class ApprovalResponse extends RealmObject {
        public BaseResponse resp;
        public String status;
    }

    /**
     * 通知金融服务端--开户
     */
    private void GuiyangPayOpen(final String idCard) {
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String, Object> map = new HashMap<>();
        map.put("loan_way_type", "GuiYangCreditLoanPay");
        LoanService loanService = ApiFactory.getFactory().create(LoanService.class);
        loanService.apply_open_account(NetUtils.getRequestParams(IntroduceActivity.this, map), NetUtils.getSign(NetUtils.getRequestParams(IntroduceActivity.this, map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(IntroduceActivity.this, true, null) {
                    @Override
                    public void onNext(Object o) {

                    }
                });
    }

    /**
     * 开户成功通知
     *
     * @param serial_no
     */
    private void guiyangOpenResult(String serial_no) {
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String, Object> map = new HashMap<>();
        map.put("serial_no", serial_no);
        LoanService loanService = ApiFactory.getFactory().create(LoanService.class);
        loanService.open_account_success_notify(NetUtils.getRequestParams(IntroduceActivity.this, map), NetUtils.getSign(NetUtils.getRequestParams(IntroduceActivity.this, map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(IntroduceActivity.this, true, null) {

                    @Override
                    public void onNext(Object o) {

                    }
                });
    }
}
