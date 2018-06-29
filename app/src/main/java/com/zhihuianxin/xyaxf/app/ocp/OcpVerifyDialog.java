package com.zhihuianxin.xyaxf.app.ocp;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import modellib.thrift.ocp.CustomerTypes;
import com.zhihuianxin.xyaxf.R;

import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 * Created by zcrpro on 2018/1/29.
 */

public class OcpVerifyDialog extends Dialog {


    @InjectView(R.id.ok)
    Button ok;
    @InjectView(R.id.cancel)
    Button cancel;
    @InjectView(R.id.bottom_view)
    LinearLayout bottomView;

    private CustomerTypes customerTypes;
    private Context context;

    private selectItem listener;

    public OcpVerifyDialog(@NonNull Context context) {
        super(context, R.style.logoutDialog);
    }

    public OcpVerifyDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    public OcpVerifyDialog(@NonNull Context context, CustomerTypes customerTypes) {
        super(context);
        this.context=context;
        this.customerTypes = customerTypes;
    }

    protected OcpVerifyDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ocp_ver_window);
        ButterKnife.inject(this);
        initViews();
    }

    private void initViews() {
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //去传这个usertypes；
                if (listener!=null)
                    listener.commit();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    public void setListener(selectItem listener) {
        this.listener = listener;
    }

    public interface selectItem{
        void commit();
    }
}
