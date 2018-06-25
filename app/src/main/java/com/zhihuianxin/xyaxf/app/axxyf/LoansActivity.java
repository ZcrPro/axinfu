package com.zhihuianxin.xyaxf.app.axxyf;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.astuetz.PagerSlidingTabStrip;
import com.zhihuianxin.xyaxf.BaseRealmActionBarActivity;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.axxyf.loan.LoanFragment;
import com.zhihuianxin.xyaxf.app.axxyf.loan.NotLoanFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by zcrpro on 2018/1/2.
 */

public class LoansActivity extends BaseRealmActionBarActivity {

    @InjectView(R.id.tabs)
    PagerSlidingTabStrip tabs;
    @InjectView(R.id.view_pager)
    ViewPager viewPager;

    private List<Fragment> fragments;
    private LoanFragment loanFragment;
    private NotLoanFragment notLoanFragment;
    private List<String> titles;

    private IntersListStatePagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
        initViews();
    }

    @Override
    protected int getContentViewId() {
        return R.layout.loan_activity;
    }


    private void initViews() {

        loanFragment = new LoanFragment();
        notLoanFragment = new NotLoanFragment();

        titles = new ArrayList<>();
        titles.add("未到期贷款");
        titles.add("已到期贷款");
        fragments = new ArrayList<>();
        fragments.add(notLoanFragment);
        fragments.add(loanFragment);
        pagerAdapter = new IntersListStatePagerAdapter(getSupportFragmentManager(), titles, fragments);
        viewPager.setAdapter(pagerAdapter);
        tabs.setViewPager(viewPager);
        @SuppressLint("ResourceType") ColorStateList csl = getResources().getColorStateList(R.drawable.fee_tabs_text_check_fragment);
        tabs.setTextColor(csl);
    }
}
