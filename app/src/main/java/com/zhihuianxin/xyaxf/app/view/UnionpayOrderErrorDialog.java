package com.zhihuianxin.xyaxf.app.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.zhihuianxin.xyaxf.R;

/**
 * Created by Vincent on 2017/11/10.
 */

public class UnionpayOrderErrorDialog extends Dialog{
    public interface OnPayOrderErrorListener {
        void canel();
        void changeOtherCard();
    }

    public void setOnPayOrderErrorListener (OnPayOrderErrorListener listener){
        this.listener = listener;
    }

    public UnionpayOrderErrorDialog(Context context) {
        super(context, R.style.UnionPayErrorDialog);
    }

    private Button cancelBtn,changeBnt;
    private OnPayOrderErrorListener listener;
    private TextView mContainTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.union_errorlessmoney_alert);

        mContainTxt = (TextView) findViewById(R.id.contain);
        cancelBtn = (Button) findViewById(R.id.cancel);
        changeBnt = (Button) findViewById(R.id.change);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.canel();
            }
        });
        changeBnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.changeOtherCard();
            }
        });
    }

    public void setText(String str){
        mContainTxt.setText(str);
    }
}
