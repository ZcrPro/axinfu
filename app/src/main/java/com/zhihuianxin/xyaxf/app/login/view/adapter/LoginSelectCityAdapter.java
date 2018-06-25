package com.zhihuianxin.xyaxf.app.login.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.zhihuianxin.xyaxf.R;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * Created by Vincent on 2016/10/19.
 */

public class LoginSelectCityAdapter extends ArrayAdapter<LoginSelectCityAdapter.CityExt> implements StickyListHeadersAdapter,SectionIndexer {
    public static final String SELECTIONS = "定热ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private Context mContext;
    public LoginSelectCityAdapter(Context context) {
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
            CityExt cityExt = getItem(i);

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

    public static class CityExt{
        public String cityName;
        public String category_value;
        public String category;
        public String cityCode;
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        CityExt cityExt = getItem(position);

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
            view = ((Activity)mContext).getLayoutInflater().inflate(R.layout.city_item, parent, false);
        }

        CityExt cityExt = getItem(position);
        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText(cityExt.cityName);

        view.setTag(getItem(position));

        return view;
    }

    @Override
    public long getHeaderId(int position) {
        return getItem(position).category.charAt(0);
    }
}
