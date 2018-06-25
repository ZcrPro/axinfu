package com.zhihuianxin.xyaxf.app.ocp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.axinfu.modellib.thrift.ocp.CustomerTypes;
import com.pacific.adapter.RecyclerAdapter;
import com.pacific.adapter.RecyclerAdapterHelper;
import com.zhihuianxin.xyaxf.R;

import java.util.List;

/**
 * Created by zcrpro on 2017/11/27.
 */

public class OcpUserTypeAdapter extends RecyclerAdapter<CustomerTypes> {

    private OnNextListener onNextListener;

    public OcpUserTypeAdapter(Context context, @NonNull int... layoutResIds) {
        super(context, layoutResIds);
    }

    public OcpUserTypeAdapter(Context context, @Nullable List<CustomerTypes> data, @NonNull int... layoutResIds) {
        super(context, data, layoutResIds);
    }

    @Override
    protected void convert(RecyclerAdapterHelper helper, final CustomerTypes item) {
        helper.setText(R.id.text, item.type_name);
        helper.getItemView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onNextListener!=null)
                    onNextListener.onNext(item);
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
