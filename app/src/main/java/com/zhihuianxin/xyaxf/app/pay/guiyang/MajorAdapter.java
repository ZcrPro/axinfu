package com.zhihuianxin.xyaxf.app.pay.guiyang;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.payment.EnumFields;

import java.util.List;

/**
 * Created by zcrpro on 2017/11/7.
 */

public class MajorAdapter extends BaseAdapter {

    private List<EnumFields> mList;
    private Context mContext;

    public MajorAdapter(Context pContext, List<EnumFields> pList) {
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
        convertView = _LayoutInflater.inflate(R.layout.spinner_zhuanye, null);
        if (convertView != null) {
            TextView _TextView1 = (TextView) convertView.findViewById(R.id.tv);
            _TextView1.setText(((EnumFields)mList.get(position)).name);
        }
        return convertView;
    }
}
