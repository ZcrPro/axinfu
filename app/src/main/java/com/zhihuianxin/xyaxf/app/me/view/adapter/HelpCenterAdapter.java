package com.zhihuianxin.xyaxf.app.me.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.axinfu.modellib.thrift.app.QuestionAnswer;
import com.zhihuianxin.xyaxf.R;

/**
 * Created by Vincent on 2016/11/11.
 */

public class HelpCenterAdapter  extends ArrayAdapter<QuestionAnswer>{
    private Context mContext;
    public HelpCenterAdapter(Context context) {
        super(context, 0);
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if(convertView != null){
            view = convertView;
        }
        else{
            view = ((Activity)mContext).getLayoutInflater().inflate(R.layout.me_help_center_item, parent, false);
        }
        ((TextView)view.findViewById(R.id.question_title)).setText(getItem(position).question);
        view.setTag(getItem(position));
        return view;
    }
}
