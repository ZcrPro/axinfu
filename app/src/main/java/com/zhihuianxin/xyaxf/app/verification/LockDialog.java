package com.zhihuianxin.xyaxf.app.verification;

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


/**
 * Created by zcrpro on 2018/1/29.
 */

public class LockDialog extends Dialog {


    @InjectView(R.id.contain)
    TextView contain;
    @InjectView(R.id.liji)
    Button liji;
    @InjectView(R.id.shaohou)
    Button shaohou;
    @InjectView(R.id.buzai)
    Button buzai;
    @InjectView(R.id.bottom_view)
    LinearLayout bottomView;

    private selectItem listener;

    public LockDialog(@NonNull Context context) {
        super(context, R.style.logoutDialog);
    }

    public LockDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected LockDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lock_window);
        ButterKnife.inject(this);
        contain.setText("建议设置解锁密码提高应用使用安全，如现在跳过，可在\"我的-设置\"下设置");
        initViews();
    }

    private void initViews() {
        liji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        shaohou.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener!=null)
                    listener.shaohou();
            }
        });

        buzai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener!=null)
                    listener.buzai();
            }
        });
    }

    public void setListener(selectItem listener) {
        this.listener = listener;
    }

    public interface selectItem{
        void shaohou();
        void buzai();
    }
}
