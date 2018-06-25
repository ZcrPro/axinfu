package com.zhihuianxin.xyaxf.app.view;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.axinfu.modellib.thrift.message.ImportantMessage;
import com.zhihuianxin.xyaxf.R;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Vincent on 2016/11/25.
 */

public class ImportantNoticeActivity extends Activity{
    public static final String EXTRA_DATA = "data";

    @InjectView(R.id.notice_btn)
    Button mBtn;
    @InjectView(R.id.page_index)
    TextView mIndexTxt;
    @InjectView(R.id.page_count)
    TextView mCountTxt;
    @InjectView(R.id.title)
    TextView mTitleTxt;
    @InjectView(R.id.content)
    TextView mContextTxt;
    @InjectView(R.id.title_icon)
    ImageView mTitleImg;
    @InjectView(R.id.main_view)
    View mMainView;

    private int mIndex = 0;

    private ArrayList<ImportantMessage> mList;
    private DisplayMetrics metrics;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.important_notice_activity);
        ButterKnife.inject(this);
        mBtn.setOnClickListener(listener);
        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        mList = (ArrayList<ImportantMessage>) getIntent().getExtras().getSerializable(EXTRA_DATA);
        initViews();
        showAnim();
    }

    private void showAnim(){
        mMainView.setVisibility(View.INVISIBLE);
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mTitleImg, "rotation", 0F, 10F,-10F,10F,-10F,10F,-10F,10F,-10F);
        objectAnimator.setDuration(700).start();
        objectAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                ObjectAnimator.ofFloat(mMainView, "translationY",-(int)metrics.density*327,0).setDuration(400).start();
                mMainView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    private void initViews(){
        if(mList.size() == 1 || mIndex == mList.size()-1){
            mBtn.setText("完成");
        } else{
            mBtn.setText("下一条");
        }

        mIndexTxt.setText((mIndex+1)+"");
        mCountTxt.setText(mList.size()+"");
        mTitleTxt.setText(mList.get(mIndex).title.length() > 8 ? mList.get(mIndex).title.substring(0,7)+"...":mList.get(mIndex).title);
        mContextTxt.setText(mList.get(mIndex).content);
    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(mList.size() == 1 || mIndex == mList.size()-1){
                finish();
            } else{
                mIndex++;
                initViews();
            }
        }
    };
}
