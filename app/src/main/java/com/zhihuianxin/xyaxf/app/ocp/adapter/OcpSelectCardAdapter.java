package com.zhihuianxin.xyaxf.app.ocp.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.axinfu.modellib.thrift.ocp.CredentialType;
import com.pacific.adapter.RecyclerAdapter;
import com.pacific.adapter.RecyclerAdapterHelper;
import com.zhihuianxin.xyaxf.R;

import java.util.List;

/**
 * Created by zcrpro on 2017/11/27.
 */

public class OcpSelectCardAdapter extends RecyclerAdapter<CredentialType> {

    private OnNextListener onNextListener;

    public OcpSelectCardAdapter(Context context, @NonNull int... layoutResIds) {
        super(context, layoutResIds);
    }

    public OcpSelectCardAdapter(Context context, @Nullable List<CredentialType> data, @NonNull int... layoutResIds) {
        super(context, data, layoutResIds);
    }

    @Override
    protected void convert(RecyclerAdapterHelper helper, final CredentialType item) {
        helper.setText(R.id.text, item.title);
        helper.getItemView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onNextListener!=null)
                    onNextListener.onNext(item);
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
