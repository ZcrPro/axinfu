package com.zhihuianxin.xyaxf.app.fee.check;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.astuetz.PagerSlidingTabStrip;
import com.zhihuianxin.xyaxf.BaseRealmActionBarActivity;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.fee.FeeActivity;
import com.zhihuianxin.xyaxf.app.utils.AxFrageStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by zcrpro on 2016/10/18.
 * 缴费前进行身份验证
 */

public class FeeCheckActivity extends BaseRealmActionBarActivity {

    @Override
    protected int getContentViewId() {
        return R.layout.fee_check_activity;
    }

    @InjectView(R.id.tabs)
    PagerSlidingTabStrip tabs;
    @InjectView(R.id.view_pager)
    ViewPager viewPager;
    private AxFrageStatePagerAdapter pagerAdapter;
    private List<Fragment> fragments;
    private FeeBasicFragment feeBasicFragment;
    private FeeNewlyFragment feeNewlyFragment;
    private List<String> titles;

    public static final String ENTER_FLAG = "ENTER_FLAG";
    public static final String normal = "normal";
    public static final String other = "other";

    private Bundle fragmentBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
        getBundle();
        initViews();
    }

    private void getBundle() {
        feeBasicFragment = new FeeBasicFragment();
        feeNewlyFragment = new FeeNewlyFragment();
        fragmentBundle= new Bundle();
        Bundle bundle = getIntent().getExtras();
        if (bundle!=null){
            String flag = bundle.getString(FeeActivity.ENTER_FLAG);
            if (flag!=null){
                if (flag.equals(normal)){
                    fragmentBundle.putString(ENTER_FLAG,normal);
                }else {
                    fragmentBundle.putString(ENTER_FLAG,other);
                }
                feeNewlyFragment.setArguments(fragmentBundle);
                feeBasicFragment.setArguments(fragmentBundle);
            }
        }
    }

    private void initViews() {
        titles = new ArrayList<>();
        titles.add("基本信息");
        titles.add("新生入学缴费");
        fragments = new ArrayList<>();
        fragments.add(feeBasicFragment);
        fragments.add(feeNewlyFragment);
        pagerAdapter = new AxFrageStatePagerAdapter(getSupportFragmentManager(), titles, fragments);
        viewPager.setAdapter(pagerAdapter);
        tabs.setViewPager(viewPager);
        ColorStateList csl = getResources().getColorStateList(R.drawable.fee_tabs_text_check_fragment);
        tabs.setTextColor(csl);
    }
}
