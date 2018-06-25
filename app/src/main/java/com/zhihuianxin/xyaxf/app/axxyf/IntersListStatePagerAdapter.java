package com.zhihuianxin.xyaxf.app.axxyf;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

/**
 * Created by zcrpro on 2016/10/19.
 */

public class IntersListStatePagerAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> fragments;
    private List<String> titles;

    public IntersListStatePagerAdapter(FragmentManager fm, List<String> titles, List<Fragment> fragments) {
        super(fm);
        this.titles=titles;
        this.fragments=fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
            return titles.get(position);
    }
}
