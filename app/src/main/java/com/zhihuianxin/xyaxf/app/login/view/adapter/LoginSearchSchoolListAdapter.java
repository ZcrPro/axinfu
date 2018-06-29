package com.zhihuianxin.xyaxf.app.login.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import modellib.thrift.resource.School;
import com.zhihuianxin.xyaxf.R;

/**
 * Created by Vincent on 2016/11/4.
 */

public class LoginSearchSchoolListAdapter extends ArrayAdapter<School> {
    private Context mContext;
    private String mKey;
    public LoginSearchSchoolListAdapter(Context context,String key) {
        super(context, 0);
        mContext = context;
        mKey = key;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if(convertView != null){
            view = convertView;
        }
        else{
            view = ((Activity)mContext).getLayoutInflater().inflate(R.layout.city_item, parent, false);
        }
        ((TextView)view.findViewById(R.id.title)).setText(getColorText(getItem(position).name,mKey)==null?getItem(position).name:getColorText(getItem(position).name,mKey));
        view.setTag(getItem(position));
        return view;
    }

    private SpannableStringBuilder getColorText(String str, String key){
        SpannableStringBuilder builder = new SpannableStringBuilder(str);
        ForegroundColorSpan redSpan = new ForegroundColorSpan(mContext.getResources().getColor(R.color.axf_common_blue));
        int index = str.indexOf(key);
        if(index == -1){
            return null;
        } else{
            builder.setSpan(redSpan, index, index+key.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return builder;
        }
    }
}
