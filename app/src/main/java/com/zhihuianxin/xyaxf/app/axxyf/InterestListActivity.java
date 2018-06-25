package com.zhihuianxin.xyaxf.app.axxyf;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.astuetz.PagerSlidingTabStrip;
import com.zhihuianxin.xyaxf.BaseRealmActionBarActivity;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.axxyf.fragment.HisFragment;
import com.zhihuianxin.xyaxf.app.axxyf.fragment.NotOutBillFragment;
import com.zhihuianxin.xyaxf.app.axxyf.fragment.OutBillFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by zcrpro on 2018/1/2.
 */

public class InterestListActivity extends BaseRealmActionBarActivity {


    @InjectView(R.id.tabs)
    PagerSlidingTabStrip tabs;
    @InjectView(R.id.view_pager)
    ViewPager viewPager;

    private IntersListStatePagerAdapter pagerAdapter;
    private List<Fragment> fragments;
    private OutBillFragment outBillFragment;
    private NotOutBillFragment notOutBillFragment;
    private HisFragment hisFragment;
    private List<String> titles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
        initViews();
    }

    @Override
    protected int getContentViewId() {
        return R.layout.interest_list_activity;
    }

    private void initViews() {

        outBillFragment = new OutBillFragment();
        notOutBillFragment = new NotOutBillFragment();
        hisFragment = new HisFragment();

        titles = new ArrayList<>();
        titles.add("已出账单");
        titles.add("未出账单");
        titles.add("历史账单");
        fragments = new ArrayList<>();
        fragments.add(outBillFragment);
        fragments.add(notOutBillFragment);
        fragments.add(hisFragment);
        pagerAdapter = new IntersListStatePagerAdapter(getSupportFragmentManager(), titles, fragments);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(3);
        tabs.setViewPager(viewPager);
        @SuppressLint("ResourceType") ColorStateList csl = getResources().getColorStateList(R.drawable.fee_tabs_text_check_fragment);
        tabs.setTextColor(csl);
    }
}
