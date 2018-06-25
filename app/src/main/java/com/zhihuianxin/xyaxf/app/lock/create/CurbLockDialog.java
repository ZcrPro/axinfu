package com.zhihuianxin.xyaxf.app.lock.create;

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

import com.zhihuianxin.xyaxf.R;

import java.util.List;

/**
 * Created by zcrpro on 2017/11/27.
 */

public class CurbLockDialog extends Dialog {

    private Context context;
    private RecyclerView recyclerView;
    private TextView btn_cancle;
    private CurbLockAdapter curbLockAdapter;
    private List<String> credentialTypes;

    private CurbLockAdapter.OnNextListener onNextListener;

    public CurbLockDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    public CurbLockDialog(@NonNull Context context, List<String> credentialTypes) {
        super(context);
        this.context = context;
        this.credentialTypes = credentialTypes;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout root = (LinearLayout) LayoutInflater.from(context).inflate(
                R.layout.curb_lock_dialog, null);
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

        curbLockAdapter = new CurbLockAdapter(context, credentialTypes, R.layout.curb_lock_item);
        recyclerView.setAdapter(curbLockAdapter);

        curbLockAdapter.setOnNextListener(new CurbLockAdapter.OnNextListener() {
            @Override
            public void onNext(String credentialType) {
                if(onNextListener!=null)
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
        void onNext(String type);
    }

    public void setOnNextListener(CurbLockAdapter.OnNextListener listener) {
        onNextListener = listener;
    }
}
