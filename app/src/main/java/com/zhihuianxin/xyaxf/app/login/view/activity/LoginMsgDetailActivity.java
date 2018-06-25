package com.zhihuianxin.xyaxf.app.login.view.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.axinfu.modellib.thrift.message.AxfMesssage;
import com.zhihuianxin.axutil.Util;
import com.zhihuianxin.xyaxf.BaseRealmActionBarActivity;
import com.zhihuianxin.xyaxf.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Vincent on 2016/11/28.
 */

public class LoginMsgDetailActivity extends BaseRealmActionBarActivity{
    public static final String EXTRA_DATA = "data";

    @InjectView(R.id.detail_title)
    TextView mTitleText;
    @InjectView(R.id.time)
    TextView mTimeText;
    @InjectView(R.id.content)
    TextView mContentText;

    private AxfMesssage mMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ButterKnife.inject(this);
        mMsg = (AxfMesssage) getIntent().getExtras().getSerializable(EXTRA_DATA);
        initView();
    }

    private void initView(){
        mTitleText.setText(mMsg.title);
        int[] timeItems = mMsg.time != null? Util.getTimeItems(mMsg.time):null;
        mTimeText.setText(String.format("%04d-%02d-%02d", timeItems[0], timeItems[1],timeItems[2]));
        mContentText.setText(mMsg.content);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.loginmsg_detail_activity;
    }

    @Override
    public boolean leftButtonEnabled() {
        return true;
    }

    @Override
    public void onLeftButtonClick(View view) {
        finish();
    }

    @Override
    public int getLeftButtonImageId() {
        return R.drawable.back;
    }
}
