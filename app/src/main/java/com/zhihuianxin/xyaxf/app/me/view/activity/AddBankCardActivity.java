package com.zhihuianxin.xyaxf.app.me.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zhihuianxin.xyaxf.app.AppConstant;
import com.axinfu.modellib.service.BankCardService;
import com.axinfu.modellib.thrift.bankcard.BankCard;
import com.axinfu.modellib.thrift.base.BaseResponse;
import com.axinfu.modellib.thrift.customer.Customer;
import com.axinfu.modellib.thrift.customer.VerifyField;
import com.google.gson.Gson;
import com.zhihuianxin.secure.Secure;
import com.zhihuianxin.xyaxf.App;
import com.zhihuianxin.xyaxf.BaseRealmActionBarActivity;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.ApiFactory;
import com.zhihuianxin.xyaxf.app.BaseSubscriber;
import com.zhihuianxin.xyaxf.app.RetrofitFactory;
import com.zhihuianxin.xyaxf.app.login.contract.ILoginVerPwdContract;
import com.zhihuianxin.xyaxf.app.login.presenter.LoginVerPwdPresenter;
import com.zhihuianxin.xyaxf.app.login.view.activity.LoginGetPwdActivity;
import com.zhihuianxin.xyaxf.app.login.view.activity.LoginGetPwdByCodeActivity;
import com.zhihuianxin.xyaxf.app.me.utils.SpaceText;
import com.zhihuianxin.xyaxf.app.utils.NetUtils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.zhihuianxin.xyaxf.app.login.view.activity.LoginGetPwdActivity.EXTRA_VERIFY_DATA;

/**
 * Created by zcrprozcrpro on 2017/5/18.
 */

public class AddBankCardActivity extends BaseRealmActionBarActivity implements ILoginVerPwdContract.ILoginVerPwdView{

    @InjectView(R.id.id_value)
    EditText idValue;
    @InjectView(R.id.pwd)
    EditText pwd;
    @InjectView(R.id.next)
    Button next;
    @InjectView(R.id.getPwdId)
    TextView getPwdId;
    @InjectView(R.id.iv_w_logo)
    ImageView ivWLogo;
    @InjectView(R.id.w_text)
    TextView wText;
    @InjectView(R.id.ll_tishi)
    RelativeLayout llTishi;

    private ILoginVerPwdContract.ILoginVerPwdPresenter presenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
        getBundle();
        initView();
    }

    private void getBundle() {
        String s = null;
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            List<String> supported_bank_name = bundle.getStringArrayList("supported_bank_name");
            if (supported_bank_name != null) {
                llTishi.setVisibility(View.VISIBLE);
                for (int i = 0; i < supported_bank_name.size(); i++) {
                    if (s == null) {
                        s = supported_bank_name.get(i);
                    } else {
                        s = s + "、" + supported_bank_name.get(i);
                    }
                }
                wText.setText("请添加本人的" + s + "银行卡");
            }
        }
    }

    private void initView() {
        new LoginVerPwdPresenter(this,this);
        idValue.addTextChangedListener(new SpaceText(idValue));

        next.setEnabled(true);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(idValue.getText().toString().trim()) || TextUtils.isEmpty(pwd.getText().toString().trim())) {
                    Toast.makeText(AddBankCardActivity.this, "请先填写完整相关信息", Toast.LENGTH_SHORT).show();
                    return;
                }
                RetrofitFactory.setBaseUrl(AppConstant.URL);
                Map<String, Object> map = new HashMap<>();
                try {
                    map.put("card_no", bytesToHexString(Secure.encodeMessageField((idValue.getText().toString().trim().replaceAll(" ", "")).getBytes("UTF-8"))));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    map.put("login_password",bytesToHexString(Secure.encodeMessageField(pwd.getText().toString().trim().getBytes("UTF-8"))));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                BankCardService bankCardService = ApiFactory.getFactory().create(BankCardService.class);
                bankCardService.add_bank_card(NetUtils.getRequestParams(AddBankCardActivity.this, map), NetUtils.getSign(NetUtils.getRequestParams(AddBankCardActivity.this, map)))
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new BaseSubscriber<Object>(AddBankCardActivity.this, true, null) {
                            @Override
                            public void onNext(Object o) {
                                BankCardResponse bankCardResponse = new Gson().fromJson(o.toString(), BankCardResponse.class);
                                if (bankCardResponse.resp.resp_code.equals(AppConstant.SUCCESS)) {
                                    Toast.makeText(AddBankCardActivity.this, "绑定银行卡成功", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Toast.makeText(AddBankCardActivity.this, bankCardResponse.resp.resp_desc, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        getPwdId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.getmodifyPwdInfo(App.mAxLoginSp.getUserMobil());
            }
        });
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

    @Override
    protected int getContentViewId() {
        return R.layout.add_bank_card_activity;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.reset(this);
    }

    @Override
    public void loginSuccess(Customer customer, String session) {

    }

    @Override
    public void getmodifyPwdInfoResult(ArrayList<VerifyField> verify_fields) {
        if(verify_fields!= null && verify_fields.size() > 0){
            Intent intent = new Intent(this, LoginGetPwdActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(EXTRA_VERIFY_DATA, verify_fields);
            intent.putExtras(bundle);
            startActivity(intent);
        } else{
            startActivity(new Intent(this,LoginGetPwdByCodeActivity.class));
        }
    }

    @Override
    public void setPresenter(ILoginVerPwdContract.ILoginVerPwdPresenter presenter) {
        this.presenter = presenter;
    }
    @Override
    public void loadStart() {}
    @Override
    public void loadError(String errorMsg) {}
    @Override
    public void loadComplete() {}

    public static class BankCardResponse {
        public BaseResponse resp;
        public BankCard bank_card;
    }

    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }
}
