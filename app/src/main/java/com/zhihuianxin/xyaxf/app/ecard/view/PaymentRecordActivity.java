package com.zhihuianxin.xyaxf.app.ecard.view;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhihuianxin.xyaxf.app.base.BaseActivity;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.ecard.adapter.PaymentRecordAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by zcrpro on 2016/11/1.
 */
public class PaymentRecordActivity extends BaseActivity {

    @InjectView(R.id.recyclerview)
    RecyclerView recyclerview;
    @InjectView(R.id.title)
    TextView title;
    @InjectView(R.id.ico_left)
    ImageView icoLeft;
    @InjectView(R.id.txt_left)
    TextView txtLeft;
    @InjectView(R.id.btn_left)
    FrameLayout btnLeft;
    @InjectView(R.id.ico_right)
    ImageView icoRight;
    @InjectView(R.id.txt_right)
    TextView txtRight;
    @InjectView(R.id.btn_right)
    FrameLayout btnRight;
    @InjectView(R.id.action_bar)
    FrameLayout actionBar;
    private PaymentRecordAdapter paymentRecordAdapter;
    private List<String> datas;

    @Override
    protected int getContentViewId() {
        return R.layout.ecard_record_window;
    }

    @Override
    protected int getFragmentContentId() {
        return 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
        title.setText("缴费记录");
        datas = new ArrayList<>();
        datas.clear();
        datas.add("100");
        datas.add("200");
        datas.add("4500");
        datas.add("200");
        datas.add("100");
        paymentRecordAdapter = new PaymentRecordAdapter(this, datas, R.layout.ecard_record_item);
        recyclerview.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,true));
        recyclerview.setHasFixedSize(true);
        recyclerview.setAdapter(paymentRecordAdapter);
        paymentRecordAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.reset(this);
    }
}
