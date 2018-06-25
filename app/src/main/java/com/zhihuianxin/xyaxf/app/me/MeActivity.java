package com.zhihuianxin.xyaxf.app.me;

import android.os.Bundle;
import android.view.View;

import com.zhihuianxin.xyaxf.BaseRealmActionBarActivity;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.me.view.fragment.MeFragment;

/**
 * Created by zcrpro on 2017/11/17.
 */

public class MeActivity extends BaseRealmActionBarActivity {

    private MeFragment meFragment;

    @Override
    protected int getContentViewId() {
        return R.layout.mine_activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mine_activity);
        setDefaultFragment();
        findViewById(R.id.action_bar).setVisibility(View.GONE);
    }

    private void setDefaultFragment() {
        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        meFragment = new MeFragment();
        transaction.add(R.id.id_content, meFragment, MeFragment.class.getName());
        transaction.commit();
    }
}
