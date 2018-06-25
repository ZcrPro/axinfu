package com.zhihuianxin.xyaxf.app.lock.create;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.pacific.adapter.RecyclerAdapter;
import com.pacific.adapter.RecyclerAdapterHelper;
import com.zhihuianxin.xyaxf.R;

import java.util.List;

/**
 * Created by zcrpro on 2017/11/27.
 */

public class CurbLockAdapter extends RecyclerAdapter<String> {

    private OnNextListener onNextListener;

    public CurbLockAdapter(Context context, @NonNull int... layoutResIds) {
        super(context, layoutResIds);
    }

    public CurbLockAdapter(Context context, @Nullable List<String> data, @NonNull int... layoutResIds) {
        super(context, data, layoutResIds);
    }

    @Override
    protected void convert(RecyclerAdapterHelper helper, final String item) {
        helper.setText(R.id.text, item);
        helper.getItemView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onNextListener!=null)
                    onNextListener.onNext(item);
            }
        });
    }

    public interface OnNextListener {
        void onNext(String type);
    }

    public void setOnNextListener(OnNextListener listener) {
        onNextListener = listener;
    }
}
