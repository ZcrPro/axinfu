package com.zhihuianxin.xyaxf.app.ocp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.view.UnionPayErrorDialog;

/**
 * Created by zcrpro on 2017/11/24.
 */

public class FeedDialog extends Dialog {

    private Button next;
    private TextView diss;
    private Context context;

    private UnionPayErrorDialog.OnPayPwdErrorListener onPayPwdErrorListener;

    public void setListener(UnionPayErrorDialog.OnPayPwdErrorListener listener) {
        onPayPwdErrorListener = listener;
    }

    public FeedDialog(Context context) {
        super(context, R.style.UnionPayErrorDialog);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.uninopay_callalert);
        initViews();
    }

    private void initViews() {
        next = (Button) findViewById(R.id.next);
        diss = (TextView) findViewById(R.id.diss);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call("400-028-1024");
            }
        });
        diss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    private void call(String phone) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

}
