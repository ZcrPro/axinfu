package com.zhihuianxin.xyaxf.app.me.view.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.axinfu.modellib.thrift.app.QuestionAnswer;
import com.zhihuianxin.axutil.Util;
import com.zhihuianxin.xyaxf.BaseRealmActionBarActivity;
import com.zhihuianxin.xyaxf.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Vincent on 2016/11/11.
 */

public class MeHelpCenterDetailActivity extends BaseRealmActionBarActivity{
    public static final String EXTRA_QUESTION = "question_data";

    @InjectView(R.id.content)
    TextView mContentText;
    @InjectView(R.id.title_help_detail)
    TextView mTitleTxt;
    @InjectView(R.id.time)
    TextView mTimeTxt;

    private QuestionAnswer mQuestionAnswer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);

        mQuestionAnswer = (QuestionAnswer) getIntent().getExtras().getSerializable(EXTRA_QUESTION);
        mTitleTxt.setText(mQuestionAnswer.question);
        mContentText.setText(mQuestionAnswer.content);

        int[] timeItems = mQuestionAnswer.create_time != null? Util.getTimeItems(mQuestionAnswer.create_time): null;
        mTimeTxt.setText(timeItems!=null ?
                String.format("%04d-%02d-%02d", timeItems[0], timeItems[1],timeItems[2]) : "");
    }

    @Override
    protected int getContentViewId() {
        return R.layout.help_center_detail_activity;
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
