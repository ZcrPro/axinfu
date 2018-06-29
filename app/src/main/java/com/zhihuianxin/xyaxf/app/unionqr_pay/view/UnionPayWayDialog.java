package com.zhihuianxin.xyaxf.app.unionqr_pay.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import modellib.thrift.base.PayMethod;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.ocp.QrResultActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Vincent on 2017/11/15.
 */

public class UnionPayWayDialog extends Dialog {

    public interface OnNextListener {
        void onNext(PayMethod data);
    }

    public void setOnNextListener(OnNextListener listener) {
        onNextListener = listener;
    }

    private OnNextListener onNextListener;
    private ImageView mCancelText;
    private List<PayMethod> payMethods;
    private AllPayWayAdapter allPayWayAdapter;
    private Context mContext;
    private RecyclerView recyclerView;
    private QrResultActivity.PayMethod payMethod;

    public UnionPayWayDialog(Context context) {
        super(context, R.style.UnionPayErrorDialog);
        mContext = context;
    }

    public UnionPayWayDialog(Context context, List<PayMethod> payMethods) {
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

        ArrayList<PayMethod> newPayMethods = new ArrayList<>();

        for (int i = 0; i < payMethods.size(); i++) {
            if (payMethods.get(i).purpose != null) {
                newPayMethods.add(payMethods.get(i));
            }
        }

        for (int i = 0; i < payMethods.size(); i++) {
            if (payMethods.get(i).channel.equals("UPQRQuickPay") && payMethods.get(i).purpose == null) {
                newPayMethods.add(payMethods.get(i));
            }
        }

        for (int i = 0; i < payMethods.size(); i++) {
            if (!payMethods.get(i).channel.equals("UPQRQuickPay") && payMethods.get(i).purpose == null) {
                newPayMethods.add(payMethods.get(i));
            }
        }

        Collections.sort(newPayMethods, new Comparator<PayMethod>() {
            @Override
            public int compare(PayMethod payMethod, PayMethod t1) {
                if (payMethod.purpose==null&&t1.purpose!=null){
                    return -1;
                }
                return 0;
            }
        });

        Collections.sort(newPayMethods, new Comparator<PayMethod>() {
            @Override
            public int compare(PayMethod payMethod, PayMethod t1) {
                if ((!payMethod.enabled&&payMethod.purpose==null)&&(t1.enabled&&t1.purpose==null)){
                    return -1;
                }
                return 0;
            }
        });

        allPayWayAdapter = new AllPayWayAdapter(mContext, newPayMethods, R.layout.ocp_pay_method_all_item);
        recyclerView.setAdapter(allPayWayAdapter);

        allPayWayAdapter.onItemclickListener(new AllPayWayAdapter.clickListener() {
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
