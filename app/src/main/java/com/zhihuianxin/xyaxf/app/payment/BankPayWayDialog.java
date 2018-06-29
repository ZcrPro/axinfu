package com.zhihuianxin.xyaxf.app.payment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import modellib.thrift.base.PayMethod;
import com.zhihuianxin.xyaxf.R;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Vincent on 2017/11/15.
 */

public class BankPayWayDialog extends Dialog {

    public interface OnNextListener {
        void onNext(PayMethod data);
    }

    public void setOnNextListener(OnNextListener listener) {
        onNextListener = listener;
    }

    private OnNextListener onNextListener;
    private ImageView mCancelText;
    private List<PayMethod> payMethods;
    private Context mContext;
    private RecyclerView recyclerView;

    private BankPayWayAdapter bankPayWayAdapter;

    private PayMethod payMethod;

    public BankPayWayDialog(Context context) {
        super(context, R.style.UnionPayErrorDialog);
        mContext = context;
    }

    public BankPayWayDialog(Context context, List<PayMethod> payMethods) {
        super(context, R.style.UnionPayErrorDialog);
        this.payMethods = payMethods;
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ocp_pay_way_dialog);
        mCancelText = (ImageView) findViewById(R.id.cancelremark);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);

        recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, true));
        recyclerView.setHasFixedSize(true);

        mCancelText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        Collections.sort(payMethods, new Comparator<PayMethod>() {
            @Override
            public int compare(PayMethod payMethod, PayMethod t1) {
                if (payMethod.purpose==null&&t1.purpose!=null){
                    return -1;
                }
                return 0;
            }
        });

        Collections.sort(payMethods, new Comparator<PayMethod>() {
            @Override
            public int compare(PayMethod payMethod, PayMethod t1) {
                if ((!payMethod.enabled&&payMethod.purpose==null)&&(t1.enabled&&t1.purpose==null)){
                    return -1;
                }
                return 0;
            }
        });

        bankPayWayAdapter = new BankPayWayAdapter(mContext, payMethods, R.layout.ocp_pay_method_all_item);
        recyclerView.setAdapter(bankPayWayAdapter);
        bankPayWayAdapter.getFristPayWay(new BankPayWayAdapter.fristSelectPayWay() {
            @Override
            public void way(PayMethod item) {
                if (item != null) {
                    payMethod = item;
                }
            }
        });

        bankPayWayAdapter.onItemclickListener2(new BankPayWayAdapter.clickListener2() {
            @Override
            public void way() {
                dismiss();
            }
        });

        bankPayWayAdapter.onItemclickListener(new BankPayWayAdapter.clickListener() {
            @Override
            public void way(PayMethod item) {
                onBtnClick(item);
            }
        });


    }

    private void onBtnClick(PayMethod payMethod) {
        if (onNextListener != null) {
            onNextListener.onNext(payMethod);
        }
        dismiss();
    }
}
