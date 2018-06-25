package com.zhihuianxin.xyaxf.app.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.zhihuianxin.xyaxf.MainActivity;
import com.zhihuianxin.xyaxf.R;

/**
 * Created by Vincent on 2017/11/10.
 */

public class UnionPayErrorDialog extends Dialog{
    public interface OnPayPwdErrorListener{
        void reinput();
        void forgetPwd();
        void cancel();
    }

    private Button cancelBtn,forgetBtn;
    private TextView twView,thView;
    private OnPayPwdErrorListener onPayPwdErrorListener;

    public void setListener(OnPayPwdErrorListener listener){
        onPayPwdErrorListener = listener;
    }

    private Context context;

    public UnionPayErrorDialog(Context context) {
        super(context, R.style.UnionPayErrorDialog);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = View.inflate(context,R.layout.union_error3times_alert,null);
        setContentView(view);
        initViews();
    }

    private void initViews(){
        cancelBtn = (Button) findViewById(R.id.cancel);
        forgetBtn = (Button) findViewById(R.id.forgetpwd);
        twView = (TextView) findViewById(R.id.tw_text);
        thView = (TextView) findViewById(R.id.th_text);

        forgetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPayPwdErrorListener.forgetPwd();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cancelBtn.getText().equals("放弃支付")){
                        onPayPwdErrorListener.cancel();
                        context.startActivity(new Intent(context, MainActivity.class));
                        ((Activity)context).finish();
                } else{
                    onPayPwdErrorListener.reinput();
                }
            }
        });
    }

    public void showErrorText(String canInputCount){
        String text = "输入密码不正确，你还可以输入"+canInputCount+"次";
        twView.setVisibility(View.VISIBLE);
        twView.setText(text);
        thView.setVisibility(View.GONE);
    }

    public void showEndText(){
        twView.setVisibility(View.GONE);
        thView.setVisibility(View.VISIBLE);
    }

    public void setBtnText(String text){
        cancelBtn.setText(text);
    }
}
