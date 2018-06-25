package com.zhihuianxin.xyaxf.app.fee.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zhihuianxin.xyaxf.R;

import java.util.List;

public class FeeOtherInfoAdapter extends BaseAdapter {

    private Context mContext;
    private List<String> data;

    public FeeOtherInfoAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public FeeOtherInfoAdapter(Context mContext,List<String> data) {
        this.mContext = mContext;
        this.data=data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view;
        if (convertView != null) {
            view = convertView;
        } else {
            view = ((Activity) mContext).getLayoutInflater().inflate(R.layout.fee_other_info_item, parent, false);
        }

        TextView title = (TextView) view.findViewById(R.id.text);
        title.setText(data.get(position));
        return view;
    }

}
