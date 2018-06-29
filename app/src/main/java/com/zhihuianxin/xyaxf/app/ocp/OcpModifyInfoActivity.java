package com.zhihuianxin.xyaxf.app.ocp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zhihuianxin.xyaxf.app.AppConstant;
import modellib.service.AxfQRPayService;
import modellib.thrift.customer.Customer;
import modellib.thrift.customer.VerifyField;
import com.zhihuianxin.xyaxf.App;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.ApiFactory;
import com.zhihuianxin.xyaxf.app.BaseSubscriber;
import com.zhihuianxin.xyaxf.app.RetrofitFactory;
import com.zhihuianxin.xyaxf.app.login.contract.ILoginVerPwdContract;
import com.zhihuianxin.xyaxf.app.login.presenter.LoginVerPwdPresenter;
import com.zhihuianxin.xyaxf.app.login.view.activity.LoginGetPwdActivity;
import com.zhihuianxin.xyaxf.app.login.view.activity.LoginGetPwdByCodeActivity;
import com.zhihuianxin.xyaxf.app.utils.NetUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.zhihuianxin.xyaxf.app.login.view.activity.LoginGetPwdActivity.EXTRA_VERIFY_DATA;

/**
 * Created by zcrpro on 2017/11/23.
 */

public class OcpModifyInfoActivity extends Activity implements ILoginVerPwdContract.ILoginVerPwdView{

    @InjectView(R.id.edit_info)
    EditText editInfo;
    @InjectView(R.id.edit_pass)
    EditText editPass;
    @InjectView(R.id.submit)
    Button submit;
    @InjectView(R.id.getPwdId)
    TextView getPwdId;
    @InjectView(R.id.back_icon)
    RelativeLayout backIcon;
    @InjectView(R.id.top_title)
    TextView topTitle;
    @InjectView(R.id.ll)
    RelativeLayout ll;

    private int modifyType;
    private String credential_type;
    private String credential_name;
    private ILoginVerPwdContract.ILoginVerPwdPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ocp_modify_info_actvity);
        ButterKnife.inject(this);
        getBundle();

        new LoginVerPwdPresenter(this,this);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (modifyType == 1) {
                    if (TextUtils.isEmpty(editInfo.getText().toString().trim())) {
                        Toast.makeText(OcpModifyInfoActivity.this, "请输入" + credential_name, Toast.LENGTH_SHORT).show();
                    } else {
                        if (TextUtils.isEmpty(editPass.getText().toString().trim())) {
                            Toast.makeText(OcpModifyInfoActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                        } else {
                            CredentialInfo credentialInfo = new CredentialInfo();
                            credentialInfo.type_code = credential_type;
                            credentialInfo.account = editInfo.getText().toString().trim();
                            modifyInfo(null, credentialInfo, editPass.getText().toString().trim());
                        }
                    }
                } else {
                    if (TextUtils.isEmpty(editInfo.getText().toString().trim())) {
                        Toast.makeText(OcpModifyInfoActivity.this, "请输入学工号", Toast.LENGTH_SHORT).show();
                    } else {
                        if (TextUtils.isEmpty(editPass.getText().toString().trim())) {
                            Toast.makeText(OcpModifyInfoActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                        } else {
                            modifyInfo(editInfo.getText().toString().trim(), null, editPass.getText().toString().trim());
                        }
                    }
                }
            }
        });

        getPwdId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.getmodifyPwdInfo(App.mAxLoginSp.getUserMobil());
            }
        });

        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void getBundle() {
        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        modifyType = bundle.getInt("type");
        if (bundle.getInt("type") == 1) {
            //修改info非学号
            credential_type = bundle.getString("credential_type");
            credential_name = bundle.getString("credential_name");
            if (credential_name != null)
                editInfo.setHint("请输入" + credential_name);

            topTitle.setText(credential_name + "修改");

        } else if (bundle.getInt("type") == 2) {
            //修改学工号
            editInfo.setHint("请输入学工号");

            topTitle.setText("学工号修改");

        } else {
            Log.d("OcpModifyInfoActivity", "出错");
        }
    }

    private void modifyInfo(final String student_no, final CredentialInfo credential_info, String login_password) {
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String, Object> map = new HashMap<>();
        if (student_no != null)
            map.put("student_no", student_no);
        if (credential_info != null)
            map.put("credential_info", credential_info);
        map.put("login_password", login_password);
        AxfQRPayService axfQRPayService = ApiFactory.getFactory().create(AxfQRPayService.class);
        axfQRPayService.update_account_info(NetUtils.getRequestParams(OcpModifyInfoActivity.this, map), NetUtils.getSign(NetUtils.getRequestParams(OcpModifyInfoActivity.this, map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(OcpModifyInfoActivity.this, true, null) {

                    @Override
                    public void onNext(Object o) {
                        if (student_no != null){
                            //修改的学号
                            App.ocpAccount.account.student_no = student_no;
                        }else if (credential_info!=null){
                            App.ocpAccount.account.credential_no = credential_info.account;
                            App.ocpAccount.account.credential_type = credential_info.type_code;
                        }
                        Toast.makeText(OcpModifyInfoActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
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
    public void loadStart() {

    }

    @Override
    public void loadError(String errorMsg) {

    }

    @Override
    public void loadComplete() {

    }

    public class CredentialInfo {
        public String type_code;                         // 证件类型
        public String account;                              // 账号
    }
}
