package com.zhihuianxin.xyaxf.app.fee;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.astuetz.PagerSlidingTabStrip;
import com.axinfu.modellib.thrift.customer.Customer;
import com.axinfu.modellib.thrift.fee.FeeAccount;
import com.zhihuianxin.xyaxf.App;
import com.zhihuianxin.xyaxf.BaseRealmActionBarActivity;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.fee.feelist.view.FeeFulfilFragment;
import com.zhihuianxin.xyaxf.app.fee.feelist.view.FeeNotFulfilFragment;
import com.zhihuianxin.xyaxf.app.utils.AxFrageStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.realm.CustomerRealmProxy;
import io.realm.FeeAccountRealmProxy;
import io.realm.RealmResults;

/**
 * Created by zcrpro on 2016/10/18.
 */

public class FeeActivity extends BaseRealmActionBarActivity {


    @InjectView(R.id.tabs)
    PagerSlidingTabStrip tabs;
    @InjectView(R.id.view_pager)
    ViewPager viewPager;
    private AxFrageStatePagerAdapter pagerAdapter;
    private List<Fragment> fragments;
    private FeeFulfilFragment paymentFulfilFragment;
    private FeeNotFulfilFragment paymentNotFulfilFragment;
    private List<String> titles;

    public static final String ENTER_FLAG = "ENTER_FLAG";
    public static final String normal = "normal";
    public static final String other = "other";

    /**
     * 缴费开通失败的标志--进入不去加载缴费项目
     */
    public static final String ENTER_FEE_FLAG = "ENTER_FEE_FLAG";
    public static final String defeat = "defeat";

    private Bundle fragmentBundle;
    public boolean support_required_fee;

    @Override
    protected int getContentViewId() {
        return R.layout.fee_activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
        getBundle();
        initViews();
        FeeFulfilFragment.isFrist = true;

        Bundle bundle = getIntent().getExtras();
        if (bundle.getBoolean("new_student") && bundle.getBoolean("sup")) {
            support_required_fee = true;
        } else {
            final RealmResults<Customer> customers = realm.where(Customer.class).equalTo("mobile", App.mAxLoginSp.getUserMobil()).findAll();
            final FeeAccount feeAccount = ((CustomerRealmProxy) customers.get(0)).realmGet$fee_account();
            if (((FeeAccountRealmProxy) feeAccount).realmGet$support_required_fee()) {
                support_required_fee = true;
            }
        }
    }

    private void getBundle() {
        paymentFulfilFragment = new FeeFulfilFragment();
        paymentNotFulfilFragment = new FeeNotFulfilFragment();
        fragmentBundle = new Bundle();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String flag = bundle.getString(FeeActivity.ENTER_FLAG);
            if (flag != null) {
                if (flag.equals(normal)) {
                    if (bundle.getString(ENTER_FEE_FLAG) != null) {
                        fragmentBundle.putString(ENTER_FEE_FLAG, defeat);
                    }
                    fragmentBundle.putString(ENTER_FLAG, normal);
                } else {
                    fragmentBundle.putString(ENTER_FLAG, other);
                }
                paymentNotFulfilFragment.setArguments(fragmentBundle);
                paymentFulfilFragment.setArguments(fragmentBundle);
            }
        }
    }

    private void initViews() {
        titles = new ArrayList<>();
        titles.add("待缴费");
        titles.add("已缴费");
        fragments = new ArrayList<>();
        fragments.add(paymentNotFulfilFragment);
        fragments.add(paymentFulfilFragment);
        pagerAdapter = new AxFrageStatePagerAdapter(getSupportFragmentManager(), titles, fragments);
        viewPager.setAdapter(pagerAdapter);
        tabs.setViewPager(viewPager);
        ColorStateList csl = getResources().getColorStateList(R.drawable.fee_tabs_text);
        tabs.setTextColor(csl);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        App.mAxLoginSp.setOtherFeeNo("");
        FeeFulfilFragment.isFrist = false;
    }

    @Override
    public boolean rightButtonEnabled() {
        if (support_required_fee) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String getRightButtonText() {
        if (support_required_fee) {
            return "学费详情";
        } else {
            return "";
        }
    }

    @Override
    public void onRightButtonClick(View view) {
        if (support_required_fee) {
            startActivity(new Intent(this, ShouldFeeActivity.class));
            super.onRightButtonClick(view);
        }
    }
}
