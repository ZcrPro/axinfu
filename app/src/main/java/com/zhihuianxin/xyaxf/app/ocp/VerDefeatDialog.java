package com.zhihuianxin.xyaxf.app.ocp;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.view.UnionPayErrorDialog;

/**
 * Created by zcrpro on 2017/11/24.
 */

public class VerDefeatDialog extends Dialog {

    private Button next;
    private TextView textView;
    private String err;

    private UnionPayErrorDialog.OnPayPwdErrorListener onPayPwdErrorListener;

    public void setListener(UnionPayErrorDialog.OnPayPwdErrorListener listener) {
        onPayPwdErrorListener = listener;
    }

    public VerDefeatDialog(Context context) {
        super(context, R.style.UnionPayErrorDialog);
    }

    public VerDefeatDialog(Context context, String err) {
        super(context, R.style.UnionPayErrorDialog);
        this.err = err;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ocp_ver_defeat);
        initViews();
    }

    private void initViews() {
        textView = (TextView) findViewById(R.id.text);
        textView.setText(err);
        next = (Button) findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}
