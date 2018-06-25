package com.zhihuianxin.xyaxf.app.ocp;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import com.raizlabs.android.dbflow.sql.language.Delete;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.ocp.adapter.OcpAllPayWayAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Vincent on 2017/11/15.
 */

public class OcpPayWayDialog extends Dialog {

    public interface OnNextListener {
        void onNext(QrResultActivity.PayMethod data);
    }

    public void setOnNextListener(OnNextListener listener) {
        onNextListener = listener;
    }

    private OnNextListener onNextListener;
    private ImageView mCancelText;
    private List<QrResultActivity.PayMethod> payMethods;
    private OcpAllPayWayAdapter ocpAllPayWayAdapter;
    private Context mContext;
    private RecyclerView recyclerView;
    private QrResultActivity.PayMethod payMethod;

    public OcpPayWayDialog(Context context) {
        super(context, R.style.UnionPayErrorDialog);
        mContext = context;
    }

    public OcpPayWayDialog(Context context, List<QrResultActivity.PayMethod> payMethods) {
        super(context, R.style.UnionPayErrorDialog);
        this.payMethods = payMethods;
        mContext = context;
    }

    @Override
    public void dismiss() {
        super.dismiss();
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
                Delete.table(PayWayTagData.class);
                dismiss();
            }
        });

        ArrayList<QrResultActivity.PayMethod> newPayMethods = new ArrayList<>();

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

        Collections.sort(newPayMethods, new Comparator<QrResultActivity.PayMethod>() {
            @Override
            public int compare(QrResultActivity.PayMethod payMethod, QrResultActivity.PayMethod t1) {
                if (payMethod.purpose==null&&t1.purpose!=null){
                    return -1;
                }
                return 0;
            }
        });

        Collections.sort(newPayMethods, new Comparator<QrResultActivity.PayMethod>() {
            @Override
            public int compare(QrResultActivity.PayMethod payMethod, QrResultActivity.PayMethod t1) {
                if ((!payMethod.enabled&&payMethod.purpose==null)&&(t1.enabled&&t1.purpose==null)){
                    return -1;
                }
                return 0;
            }
        });

        ocpAllPayWayAdapter = new OcpAllPayWayAdapter(mContext, newPayMethods, R.layout.ocp_pay_method_all_item);
        recyclerView.setAdapter(ocpAllPayWayAdapter);
        ocpAllPayWayAdapter.getFristPayWay(new OcpAllPayWayAdapter.fristSelectPayWay() {
            @Override
            public void way(QrResultActivity.PayMethod item) {
                if (item != null) {
                    payMethod = item;
                }
            }
        });

        ocpAllPayWayAdapter.onItemclickListener2(new OcpAllPayWayAdapter.clickListener2() {
            @Override
            public void way() {
                dismiss();
            }
        });

        ocpAllPayWayAdapter.onItemclickListener(new OcpAllPayWayAdapter.clickListener() {
            @Override
            public void way(QrResultActivity.PayMethod item) {
                onBtnClick(item);
            }
        });

    }

    private void onBtnClick(QrResultActivity.PayMethod payMethod) {
        if (onNextListener != null) {
            Delete.table(PayWayTagData.class);
            onNextListener.onNext(payMethod);
        }
        dismiss();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Delete.table(PayWayTagData.class);
            return false;
        }else {
            return super.onKeyDown(keyCode, event);
        }

    }
}
