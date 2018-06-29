package com.zhihuianxin.xyaxf.app.login.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import modellib.thrift.resource.School;
import com.zhihuianxin.xyaxf.App;
import com.zhihuianxin.xyaxf.R;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * Created by Vincent on 2016/10/31.
 */

public class LoginSelectSchoolAdapter extends ArrayAdapter<LoginSelectSchoolAdapter.SchoolExt> implements StickyListHeadersAdapter,SectionIndexer {
    public static final String SELECTIONS = "定热ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private Context mContext;
    public LoginSelectSchoolAdapter(Context context) {
        super(context, 0);
        mContext = context;
    }

    @Override
    public Object[] getSections() {
        String[] sections = new String[SELECTIONS.length()];
        for(int i = 0; i < sections.length; i ++){
            sections[i] = SELECTIONS.substring(i, i + 1);
        }

        return sections;
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        String section = String.valueOf(SELECTIONS.charAt(sectionIndex));

        for(int i = 0; i < getCount(); i ++){
            SchoolExt cityExt = getItem(i);

            if(cityExt.category_value.substring(0, 1).toUpperCase().equals(section)){
                return i;
            }
        }

        return -1;
    }

    @Override
    public int getSectionForPosition(int position) {
        String index = getItem(position).category.substring(0, 1).toUpperCase();
        return SELECTIONS.indexOf(index);
    }

    public static class SchoolExt {
        public School school;
        public String category_value;
        public String category;
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        SchoolExt cityExt = getItem(position);

        View headerView;
        if(convertView != null){
            headerView = convertView;
        }
        else{
            headerView = ((Activity)mContext).getLayoutInflater().inflate(R.layout.header_item, parent, false);
        }

        TextView title = (TextView) headerView.findViewById(R.id.title);
        title.setText(cityExt.category);

        return headerView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if(convertView != null){
            view = convertView;
        }
        else{
            view = ((Activity)mContext).getLayoutInflater().inflate(R.layout.school_item, parent, false);
        }
        SchoolExt cityExt = getItem(position);
        CheckedTextView title = (CheckedTextView) view.findViewById(R.id.title);
        title.setText(cityExt.school.name);
        if(App.mAxLoginSp.getSelectSchoolCode().equals(cityExt.school.code)){
            title.setTextColor(mContext.getResources().getColor(R.color.axf_common_blue));
        } else{
            title.setTextColor(Color.BLACK);
        }

        view.setTag(getItem(position));
        return view;
    }

    @Override
    public long getHeaderId(int position) {
        return getItem(position).category.charAt(0);
    }
}
