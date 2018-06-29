package com.zhihuianxin.xyaxf.app.ocp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import modellib.thrift.ocp.CredentialType;
import com.zhihuianxin.xyaxf.R;

import java.util.List;

/**
 * Created by zcrpro on 2017/11/19.
 */

public class OpenOcpSpinnerAdapter extends BaseAdapter {

    private List<CredentialType> mList;
    private Context mContext;

    public OpenOcpSpinnerAdapter(Context pContext, List<CredentialType> pList) {
        this.mContext = pContext;
        this.mList = pList;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * 下面是重要代码
     */
    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater _LayoutInflater = LayoutInflater.from(mContext);
        convertView = _LayoutInflater.inflate(R.layout.open_ocp_spinner_item, null);
        if (convertView != null) {
            TextView tv = (TextView) convertView.findViewById(R.id.tv);
            tv.setText(mList.get(position).title);
        }
        return convertView;
    }
}
