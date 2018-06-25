package com.zhihuianxin.xyaxf.app.unionqr_pay.view.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.zhihuianxin.xyaxf.BaseRealmActionBarActivity;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.view.KeyboardAmountView;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Vincent on 2017/11/9.
 */

public class UnionUnLimitAmountActivity extends BaseRealmActionBarActivity implements KeyboardAmountView.OnNumberClickListener{
    public static final String EXTRA_IS_USER = "isUser";
    @InjectView(R.id.am_nkv_keyboard)
    KeyboardAmountView mNkvKeyboard;
    private String pwd = "";
    @InjectView(R.id.amountedit)
    EditText edit;
    @InjectView(R.id.remarkViewid)
    View mRemarkView;

    private String amount="";

    @Override
    protected int getContentViewId() {
        return R.layout.union_pay_new_activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);

        initView();
    }

    private void initView(){
        if(getIntent().getExtras() != null && getIntent().getExtras().getBoolean(EXTRA_IS_USER,false)){
            mRemarkView.setVisibility(View.GONE);
        } else{
            mRemarkView.setVisibility(View.VISIBLE);
        }

        mNkvKeyboard.setOnNumberClickListener(this);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeybord();
            }
        });
    }

    @Override
    public void onNumberReturn(String number) {
        if((number.equals(".") || number.equals("0")) && amount.length() == 0){
            return;
        }
        if(amount.contains(".") && number.equals(".")){
            return;
        }

        if(amount.contains(".")){
            String arr[] = amount.split("\\.");
            if(arr.length > 1 && arr[1].length() == 2){
                return;
            }
            if(arr.length > 1 && arr[1].length() > 2){
                return;
            }
            amount += number;
        } else {
            if(amount.length() > 8){
                return;
            } else{
                amount += number;
            }
        }
        setEditContain();
    }

    @Override
    public void onNumberDelete() {
        if (amount.length() <= 1) {
            amount = "";
        } else {
            amount = amount.substring(0, amount.length() - 1);
        }
        setEditContain();
    }

    @Override
    public void onCommit() {
        Toast.makeText(getApplicationContext(),amount,Toast.LENGTH_LONG).show();
    }

    /**
     * 关闭软键盘
     */
    public void closeKeybord() {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

        imm.hideSoftInputFromWindow(edit.getWindowToken(), 0);
    }

    private void setEditContain(){
        edit.setText(amount);
        edit.setSelection(edit.getText().length());
    }
}
