package com.zhihuianxin.xyaxf.app.pay.guiyang;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.zhihuianxin.xyaxf.app.utils.NetUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.realm.RealmObject;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by zcrpro on 2017/10/31.
 */

public class AutonymActivity extends BaseRealmActionBarActivity {

    @InjectView(R.id.tv_name)
    TextView tvName;
    @InjectView(R.id.ed_name)
    EditText edName;
    @InjectView(R.id.tv_name2)
    TextView tvName2;
    @InjectView(R.id.ed_idcard)
    EditText edIdcard;
    @InjectView(R.id.btn_ok)
    Button btnOk;
    @InjectView(R.id.iv_w_logo)
    ImageView ivWLogo;
    @InjectView(R.id.w_text)
    TextView wText;

    private float mAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
        getBundle();
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(edName.getText().toString())) {
                    Toast.makeText(AutonymActivity.this, "请输入姓名", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(edIdcard.getText().toString())) {
                    Toast.makeText(AutonymActivity.this, "请输入身份证号码", Toast.LENGTH_SHORT).show();
                } else {
                    if (TextUtils.isEmpty(edIdcard.getText().toString())) {
                        Toast.makeText(AutonymActivity.this, "请输入身份证号码", Toast.LENGTH_SHORT).show();
                    }else {
                        commitInfo(edIdcard.getText().toString().trim(), edName.getText().toString().trim(), mAmount); //【目前多通道前端设计未给出具体方案，故保留该字段。】默认为：GuiYangCreditLoanPay
                    }
                }
            }
        });
    }

    private void getBundle() {
        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        if (bundle.getString("name") != null && !TextUtils.isEmpty(bundle.getString("name"))) {
            edName.setText(bundle.getString("name"));
            edName.setEnabled(false);
        }

        if (bundle.getString("idCard") != null && !TextUtils.isEmpty(bundle.getString("idCard"))) {
            edIdcard.setText(bundle.getString("idCard"));
            edIdcard.setEnabled(false);
        }

        if (bundle.getFloat("amount") != 0) {
            mAmount = bundle.getFloat("amount");
        }
    }

    private void commitInfo(final String id_card_no, final String name, final float amount) {
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String, Object> map = new HashMap<>();
        map.put("id_card_no", id_card_no);
        map.put("name", name);
        map.put("loan_way_type", "GuiYangCreditLoanPay");
        LoanService loanService = ApiFactory.getFactory().create(LoanService.class);
        loanService.commit_realname_info(NetUtils.getRequestParams(this, map), NetUtils.getSign(NetUtils.getRequestParams(this, map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(this, true, null) {
                    @Override
                    public void onNext(Object o) {
                        final RealNameResponse realNameResponse = new Gson().fromJson(o.toString(), RealNameResponse.class);
                        if (realNameResponse.resp.resp_code.equals(AppConstant.SUCCESS)) {
                            //提交成功开始获取预授信信息
                            if (realNameResponse.status.equals("OK")) {
                                Bundle bundle = new Bundle();
                                bundle.putString("name", name);
                                bundle.putString("idCard", id_card_no);
                                bundle.putFloat("amount", amount);
                                Intent intent = new Intent(AutonymActivity.this, AutonymSuccActivity.class);
                                intent.putExtras(bundle);
                                AutonymActivity.this.startActivity(intent);
                            } else {
                                AutonymActivity.this.startActivity(new Intent(AutonymActivity.this, ErrorActivity.class));
                                finish();
                            }
                        }
                    }
                });
    }


    public static class RealNameResponse extends RealmObject {
        public BaseResponse resp;
        public String status;
    }

    @Override
    protected int getContentViewId() {
        return R.layout.autonym_activity;
    }

}
