package com.zhihuianxin.xyaxf.app.axxyf;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhihuianxin.xyaxf.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Vincent on 2018/3/12.
 */

public class ReplymentResultActivity extends Activity{
    public static final String EXTRA_RESULT = "replymentResult";
    public static final String EXTRA_REASON = "replymentReason";

    @InjectView(R.id.next)
    Button nextBtn;
    @InjectView(R.id.icon)
    ImageView iconImg;
    @InjectView(R.id.reason)
    TextView reasonTxt;
    @InjectView(R.id.result)
    TextView resultTxt;

    private boolean isSuccess;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.replyment_result_activity);
        ButterKnife.inject(this);

        isSuccess = getIntent().getExtras().getBoolean(EXTRA_RESULT);
        initView();
    }

    private void initView(){
        if(isSuccess){
            iconImg.setBackgroundResource(R.drawable.replayment_success);
            resultTxt.setText("提前还款成功!");
            reasonTxt.setVisibility(View.GONE);
        }else{
            iconImg.setBackgroundResource(R.drawable.replayment_error);
            resultTxt.setText("还款失败!");
            reasonTxt.setVisibility(View.VISIBLE);
            reasonTxt.setText(getIntent().getExtras().getString(EXTRA_REASON));
        }

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
