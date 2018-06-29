package com.zhihuianxin.xyaxf.app.ecard.open;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.axinfu.modellib.thrift.business.AccountVerifyItem;
import com.axinfu.modellib.thrift.customer.Customer;
import com.axinfu.modellib.thrift.ecard.ECard;
import com.axinfu.modellib.thrift.ecard.ECardAccount;
import com.axinfu.modellib.thrift.fee.FeeAccount;
import com.zhihuianxin.xyaxf.app.base.axutil.SchedulerProvider;
import com.zhihuianxin.xyaxf.App;
import com.zhihuianxin.xyaxf.BaseRealmActionBarActivity;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.ecard.view.EcardActivity;
import com.zhihuianxin.xyaxf.app.utils.RxBus;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.realm.AccountVerifyItemRealmProxy;
import io.realm.CustomerRealmProxy;
import io.realm.ECardAccountRealmProxy;
import io.realm.FeeAccountRealmProxy;
import io.realm.RealmResults;

/**
 * Created by zcrpro on 2016/11/3.
 */
public class EcardOpenActivity extends BaseRealmActionBarActivity implements EcardOpenContract.EcardOpenView {

    @InjectView(R.id.btn_ok)
    Button btnOk;
    @InjectView(R.id.tv_account_no)
    TextView tvAccountNo;
    @InjectView(R.id.ed_account_no)
    EditText edAccountNo;
    @InjectView(R.id.tv_name)
    TextView tvName;
    @InjectView(R.id.ed_name)
    EditText edName;
    @InjectView(R.id.tv_password)
    TextView tvPassword;
    @InjectView(R.id.ed_password)
    EditText edPassword;
    @InjectView(R.id.ll_password)
    RelativeLayout llPassword;

    private EcardOpenContract.EcardOpenPresenter presenter;
    private EcardOpenPresenter ecardOpenPresenter;
    private boolean isAccount = false;
    private boolean isName = false;
    private boolean isPass = false;

    @Override
    protected int getContentViewId() {
        return R.layout.ecard_open_activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);

        initVerInfo();

        ecardOpenPresenter = new EcardOpenPresenter(this, this, SchedulerProvider.getInstance());

    }

    /**
     * 查询从服务器上返回的需要验证的信息
     */
    private void initVerInfo() {
        final RealmResults<Customer> customers = realm.where(Customer.class).equalTo("mobile", App.mAxLoginSp.getUserMobil())
                .findAll();
        if (customers.size() != 0) {
            //取出需要显示的验证项目
            final AccountVerifyItem accountVerifyItem = ((AccountVerifyItemRealmProxy) ((ECardAccountRealmProxy) ((CustomerRealmProxy) customers.get(0)).realmGet$ecard_account()).realmGet$account_no_verify_config());
            if (accountVerifyItem != null) {
                tvAccountNo.setText(((AccountVerifyItemRealmProxy) accountVerifyItem).realmGet$title());
                edAccountNo.setHint(((AccountVerifyItemRealmProxy) accountVerifyItem).realmGet$hint());
            }
            if (((ECardAccountRealmProxy) ((CustomerRealmProxy) customers.get(0)).realmGet$ecard_account()).realmGet$need_password()) {
                //如果需要密码显示密码的界面
                llPassword.setVisibility(View.VISIBLE);
            } else {
                llPassword.setVisibility(View.GONE);
            }

            if (((ECardAccountRealmProxy) ((CustomerRealmProxy) customers.get(0)).realmGet$ecard_account()).realmGet$name()!=null){
                edName.setText(((ECardAccountRealmProxy) ((CustomerRealmProxy) customers.get(0)).realmGet$ecard_account()).realmGet$name());

                isName =true;

                final FeeAccount feeAccount = ((CustomerRealmProxy) customers.get(0)).realmGet$fee_account();
                if (((FeeAccountRealmProxy) feeAccount).realmGet$status().equals("OK")) {
                    edName.setEnabled(false);
                    edName.setBackgroundResource(R.drawable.payment_not_check_edittext);
                }
            }

        }
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(edName.getText().toString().trim()) || !TextUtils.isEmpty(edAccountNo.getText().toString().trim())) {
                    presenter.openEcard(edName.getText().toString().trim(), edAccountNo.getText().toString().trim(), edPassword.getText().toString().trim());
                } else {
                    Toast.makeText(EcardOpenActivity.this, "请将信息填写完整", Toast.LENGTH_SHORT).show();
                }
            }
        });
        edPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty() || s.length() < 6) {
                    isPass = false;
                } else {
                    isPass = true;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (llPassword.getVisibility() == View.VISIBLE) {
                    if (isName && isPass && isAccount) {
                        btnOk.setEnabled(true);
                    } else {
                        btnOk.setEnabled(false);
                    }
                } else {
                    if (isName && isAccount) {
                        btnOk.setEnabled(true);
                    } else {
                        btnOk.setEnabled(false);
                    }
                }
            }
        });
        edAccountNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()) {
                    isAccount = false;
                } else {
                    isAccount = true;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (llPassword.getVisibility() == View.VISIBLE) {
                    if (isName && isPass && isAccount) {
                        btnOk.setEnabled(true);
                    } else {
                        btnOk.setEnabled(false);
                    }
                } else {
                    if (isName && isAccount) {
                        btnOk.setEnabled(true);
                    } else {
                        btnOk.setEnabled(false);
                    }
                }
            }
        });
        edName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()) {
                    isName = false;
                } else {
                    isName = true;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (llPassword.getVisibility() == View.VISIBLE) {
                    if (isName && isPass && isAccount) {
                        btnOk.setEnabled(true);
                    } else {
                        btnOk.setEnabled(false);
                    }
                } else {
                    if (isName && isAccount) {
                        btnOk.setEnabled(true);
                    } else {
                        btnOk.setEnabled(false);
                    }
                }
            }
        });


    }

    @Override
    public void ecardOpenSuccess(final ECardAccount account, final ECard ecard) {
        //发送一个bus给首页重新刷新customer
        RxBus.getDefault().send("ecard_refresh");
        startActivity(new Intent(EcardOpenActivity.this, EcardActivity.class));
        finish();
    }

    @Override
    public void ecardOpenFailure() {

    }

    @Override
    public void setPresenter(EcardOpenContract.EcardOpenPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void loadStart() {

    }

    @Override
    public void loadError(String errorMsg) {

    }

    @Override
    public void loadComplete() {

    }
}
