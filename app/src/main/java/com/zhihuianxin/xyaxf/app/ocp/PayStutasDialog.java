package com.zhihuianxin.xyaxf.app.ocp;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhihuianxin.xyaxf.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class PayStutasDialog extends Dialog {


    @InjectView(R.id.contain)
    TextView contain;
    @InjectView(R.id.payed)
    Button payed;
    @InjectView(R.id.re_pay)
    Button rePay;
    @InjectView(R.id.load_payed_list)
    Button loadPayedList;
    @InjectView(R.id.bottom_view)
    LinearLayout bottomView;

    private selectItem listener;

    public PayStutasDialog(@NonNull Context context) {
        super(context, R.style.logoutDialog);
    }

    public PayStutasDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected PayStutasDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_status_dialog);
        ButterKnife.inject(this);
        initViews();
    }

    private void initViews() {
        payed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null)
                    listener.payed();
                dismiss();
            }
        });

        rePay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null)
                    listener.repay();
                dismiss();
            }
        });

        loadPayedList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null)
                    listener.go_pay_list();
                dismiss();
            }
        });
    }

    public void setListener(selectItem listener) {
        this.listener = listener;
    }

    public interface selectItem {
        void payed();

        void repay();

        void go_pay_list();
    }
}
