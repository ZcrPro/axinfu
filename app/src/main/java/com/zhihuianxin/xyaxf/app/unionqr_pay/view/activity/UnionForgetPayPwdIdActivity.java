package com.zhihuianxin.xyaxf.app.unionqr_pay.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.zhihuianxin.xyaxf.BaseRealmActionBarActivity;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.unionqr_pay.contract.IunionVerRealNameContract;
import com.zhihuianxin.xyaxf.app.unionqr_pay.presenter.UnionVerRealNamePresenter;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Vincent on 2017/11/10.
 * 忘记支付密码，输入身份证号
 */

public class UnionForgetPayPwdIdActivity extends BaseRealmActionBarActivity implements IunionVerRealNameContract.IunionVerRealName{
    private IunionVerRealNameContract.IunionVerRealNamePresenter presenter;
    @InjectView(R.id.next)
    Button nextBtn;
    @InjectView(R.id.inputEdit)
    EditText idEdit;

    @Override
    protected int getContentViewId() {
        return R.layout.union_forgetpaypwd_id_activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);

        initView();
    }

    private void initView(){
        new UnionVerRealNamePresenter(this,this);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String val = idEdit.getText().toString().trim();
                if(val.length() != 18){
                    Toast.makeText(UnionForgetPayPwdIdActivity.this,"请输入正确的身份证号",Toast.LENGTH_LONG).show();
                } else{
                    presenter.verRealName("",val);
                }
            }
        });
    }

    @Override
    public void verRealNameResult(boolean is_match) {
        if(is_match){
            Intent i = new Intent(this, UnionForgetPayPwdCodeActivity.class);
            Bundle b = new Bundle();
            b.putBoolean(UnionForgetPayPwdCodeActivity.EXTRA_SHOWIMG,true);
            i.putExtras(b);
            startActivity(i);

            finish();
        } else{
            Toast.makeText(this,"验证失败",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onLeftButtonClick(View view) {
        super.onLeftButtonClick(view);
        if(getCurrentFocus()!=null) {
            ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(getCurrentFocus()
                                    .getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void setPresenter(IunionVerRealNameContract.IunionVerRealNamePresenter presenter) {
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
