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

import com.axinfu.modellib.thrift.ocp.CustomerTypes;
import com.zhihuianxin.xyaxf.R;

import java.util.List;

/**
 * Created by zcrpro on 2017/11/27.
 */

public class OcpUserTypeDialog extends Dialog {

    private Context context;
    private RecyclerView recyclerView;
    private TextView btn_cancle;
    private OnNextListener onNextListener;
    private OcpUserTypeAdapter ocpUserTypeAdapter;
    private List<CustomerTypes> credentialTypes;

    public OcpUserTypeDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    public OcpUserTypeDialog(@NonNull Context context, List<CustomerTypes> credentialTypes) {
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

        ocpUserTypeAdapter = new OcpUserTypeAdapter(context, credentialTypes, R.layout.ocp_select_card_item);
        recyclerView.setAdapter(ocpUserTypeAdapter);

        ocpUserTypeAdapter.setOnNextListener(new OcpUserTypeAdapter.OnNextListener() {
            @Override
            public void onNext(CustomerTypes customerType) {
                if (onNextListener!=null)
                    onNextListener.onNext(customerType);
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
        void onNext(CustomerTypes customerTypes);
    }

    public void setOnNextListener(OnNextListener listener) {
        onNextListener = listener;
    }
}
