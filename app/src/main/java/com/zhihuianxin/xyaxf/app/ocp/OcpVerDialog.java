package com.zhihuianxin.xyaxf.app.ocp;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import modellib.thrift.ocp.CredentialType;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.ocp.adapter.OcpSelectCardAdapter;

import java.util.List;

/**
 * Created by zcrpro on 2017/11/27.
 */

public class OcpVerDialog extends Dialog {

    private Context context;
    private RecyclerView recyclerView;
    private TextView btn_cancle;
    private OnNextListener onNextListener;
    private OcpSelectCardAdapter ocpSelectCardAdapter;
    private List<CredentialType> credentialTypes;

    public OcpVerDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    public OcpVerDialog(@NonNull Context context, List<CredentialType> credentialTypes) {
        super(context);
        this.context = context;
        this.credentialTypes = credentialTypes;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout root = (LinearLayout) LayoutInflater.from(context).inflate(
                R.layout.ocp_select_card_dialog, null);
        recyclerView = (RecyclerView) root.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, true));
        recyclerView.setHasFixedSize(true);

        btn_cancle = (TextView) root.findViewById(R.id.btn_cancel);
        this.setContentView(root);
        Window dialogWindow = this.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        dialogWindow.setWindowAnimations(R.style.dialogstyle); // 添加动画
        WindowManager.LayoutParams lp = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        lp.x = 0; // 新位置X坐标
        lp.y = -20; // 新位置Y坐标
        lp.width = (int) context.getResources().getDisplayMetrics().widthPixels; // 宽度
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT; // 高度
//      lp.alpha = 9f; // 透明度
        root.measure(0, 0);
        lp.alpha = 9f; // 透明度
        dialogWindow.setAttributes(lp);

        ocpSelectCardAdapter = new OcpSelectCardAdapter(context, credentialTypes, R.layout.ocp_select_card_item);
        recyclerView.setAdapter(ocpSelectCardAdapter);

        ocpSelectCardAdapter.setOnNextListener(new OcpSelectCardAdapter.OnNextListener() {
            @Override
            public void onNext(CredentialType credentialType) {
                if (onNextListener!=null)
                    onNextListener.onNext(credentialType);
                dismiss();
            }
        });

        btn_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

    }

    public interface OnNextListener {
        void onNext(CredentialType credentialType);
    }

    public void setOnNextListener(OnNextListener listener) {
        onNextListener = listener;
    }
}
