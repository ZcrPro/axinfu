package com.zhihuianxin.xyaxf.app.payment.quk;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zhihuianxin.xyaxf.app.AppConstant;
import modellib.service.BankCardService;
import modellib.thrift.base.BaseResponse;
import modellib.thrift.payment.BankCardInfo;
import modellib.thrift.payment.QuickPayMethod;
import com.google.gson.Gson;
import com.xyaxf.axpay.modle.PayRequest;
import com.zhihuianxin.secure.Secure;
import com.zhihuianxin.xyaxf.BaseRealmActionBarActivity;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.ApiFactory;
import com.zhihuianxin.xyaxf.app.BaseSubscriber;
import com.zhihuianxin.xyaxf.app.RetrofitFactory;
import com.zhihuianxin.xyaxf.app.utils.NetUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.zhihuianxin.xyaxf.app.me.view.activity.AddBankCardActivity.bytesToHexString;


/**
 * Created by zcrprozcrpro on 2017/7/4.
 */

public class QukMobileActivity extends BaseRealmActionBarActivity {

    @InjectView(R.id.inputEdit)
    EditText inputEdit;
    @InjectView(R.id.iv_w_logo)
    ImageView ivWLogo;
    @InjectView(R.id.w_text)
    TextView wText;
    @InjectView(R.id.next)
    Button next;

    public static final String METHOD = "METHOD";
    private QuickPayMethod quickPayMethod;
    private Float amount;
    private PayRequest payRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
        inputEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.equals("")) {
                    next.setEnabled(false);
                } else {
                    next.setEnabled(true);
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verCardNo(inputEdit.getText().toString().trim());
            }
        });

        //获取bundle

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            amount = bundle.getFloat("amount");
            payRequest = (PayRequest) bundle.getSerializable("payRequest");
            quickPayMethod = (QuickPayMethod) bundle.getSerializable(METHOD);
            if (quickPayMethod != null)
                initUI();
        }

    }

    private void initUI() {
        if (quickPayMethod.supported_banks != null) {
            String name = null;
            for (int i = 0; i < quickPayMethod.supported_banks.size(); i++) {
                if (name != null) {
                    name = name + "、" + quickPayMethod.supported_banks.get(i);
                } else {
                    name = quickPayMethod.supported_banks.get(i);
                }
            }
            if (TextUtils.isEmpty(name)){
                wText.setVisibility(View.GONE);
            }else {
                wText.setVisibility(View.VISIBLE);
                wText.setText("目前支持以下银行卡的储蓄卡:" + name);
            }
        }
    }

    private void verCardNo(final String bank_card_no) {
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String, Object> map = new HashMap<>();
        try {
            map.put("bank_card_no", bytesToHexString(Secure.encodeMessageField(bank_card_no.getBytes("UTF-8"))));
        } catch (Exception e) {
            e.printStackTrace();
        }
        BankCardService bankCardService = ApiFactory.getFactory().create(BankCardService.class);
        bankCardService.get_bankcard_info(NetUtils.getRequestParams(this, map), NetUtils.getSign(NetUtils.getRequestParams(this, map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(this, true, null) {
                    @Override
                    public void onNext(Object o) {
                        final CardResponse cardResponse = new Gson().fromJson(o.toString(), CardResponse.class);
                        if (cardResponse.resp.resp_code.equals(AppConstant.SUCCESS)) {

                            //判断是否是支持的银行卡

                            if (quickPayMethod.supported_banks.contains(cardResponse.bank_card_info.bank_name)) {
                                //传入银行卡信息到下个界面
                                if (cardResponse.bank_card_info != null) {
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable(QukActivity.BANKINFO, cardResponse.bank_card_info);
                                    bundle.putSerializable(METHOD, quickPayMethod);
                                    bundle.putString("card_no", bank_card_no);
                                    bundle.putFloat("amount", amount);
                                    bundle.putSerializable("payRequest", payRequest);
                                    Intent intent = new Intent(QukMobileActivity.this, QukActivity.class);
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(QukMobileActivity.this, "服务器返回数据异常，请联系客服", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                //如果不存在该银行
                                Toast.makeText(QukMobileActivity.this, "暂不支持该银行卡", Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                });
    }

    @Override
    protected int getContentViewId() {
        return R.layout.quk_mobile_activity;
    }

    public static class CardResponse {
        public BaseResponse resp;
        public BankCardInfo bank_card_info;
    }

}
