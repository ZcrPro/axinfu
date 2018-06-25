package com.zhihuianxin.xyaxf.app.ocp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.axinfu.modellib.thrift.ocp.OcpAccount;
import com.zhihuianxin.axutil.Util;
import com.zhihuianxin.xyaxf.App;
import com.zhihuianxin.xyaxf.BaseRealmActionBarActivity;
import com.zhihuianxin.xyaxf.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by zcrpro on 2017/11/23.
 */

public class MeOcpActivity extends BaseRealmActionBarActivity {

    @InjectView(R.id.ver_id_title)
    TextView verIdTitle;
    @InjectView(R.id.ver_id_ve)
    TextView verIdVe;
    @InjectView(R.id.accountNextIcon1)
    ImageView accountNextIcon1;
    @InjectView(R.id.rl_card_id)
    RelativeLayout rlCardId;
    @InjectView(R.id.ver_xgh_title)
    TextView verXghTitle;
    @InjectView(R.id.ver_xgh_ve)
    TextView verXghVe;
    @InjectView(R.id.accountNextIcon)
    ImageView accountNextIcon;
    @InjectView(R.id.rl_xgh)
    RelativeLayout rlXgh;

    private OcpAccount ocpAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null != App.ocpAccount) {
            ocpAccount = App.ocpAccount;
        }
        initData();
    }

    private void initData() {


        if (ocpAccount.account.credential_type==null||ocpAccount.account.supported_credential_type.size()==0){
            rlCardId.setVisibility(View.GONE);
        }

        for (int i = 0; i < ocpAccount.account.supported_credential_type.size(); i++) {
            if (ocpAccount.account.supported_credential_type.get(i).verify_type_code.equals(ocpAccount.account.credential_type)) {
                verIdTitle.setText(ocpAccount.account.supported_credential_type.get(i).title);
            }
        }

        String s = "*";

        // 用于显示的加*身份证
        if (ocpAccount.account.credential_no!=null&&ocpAccount.account.credential_no.length() > 4) {
            for (int i = 0; i < ocpAccount.account.credential_no.length(); i++) {
                s = s + "*";
            }
            String show_id = ocpAccount.account.credential_no.substring(0, 2) + s + ocpAccount.account.credential_no.substring(ocpAccount.account.credential_no.length() - 2);
            verIdVe.setText(show_id);
        } else {
            verIdVe.setText(ocpAccount.account.credential_no);
        }

        String xgh = ocpAccount.account.student_no;

        verXghVe.setText(xgh.length() >= 2 ? Util.getXingHao(xgh.length() - 2) + xgh.substring(xgh.length() - 2, xgh.length()) : xgh);

        rlCardId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putInt("type", 1);
                bundle.putString("credential_type", ocpAccount.account.credential_type);
                bundle.putString("credential_name", verIdTitle.getText().toString().trim());
                Intent intent = new Intent(MeOcpActivity.this, OcpModifyInfoActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        rlXgh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putInt("type", 2);
                Intent intent = new Intent(MeOcpActivity.this, OcpModifyInfoActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

    }

    @Override
    protected int getContentViewId() {
        return R.layout.me_ocp_activity;
    }
}
