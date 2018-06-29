package com.zhihuianxin.xyaxf.app.fee.feelist.view;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhihuianxin.xyaxf.app.AppConstant;
import modellib.service.FeeService;
import modellib.thrift.base.BaseResponse;
import modellib.thrift.fee.FeeAccount;
import modellib.thrift.fee.FeeRecord;
import modellib.thrift.fee.SchoolRoll;
import com.google.gson.Gson;
import com.zhihuianxin.xyaxf.BaseRealmActionBarActivity;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.ApiFactory;
import com.zhihuianxin.xyaxf.app.BaseSubscriber;
import com.zhihuianxin.xyaxf.app.RetrofitFactory;
import com.zhihuianxin.xyaxf.app.utils.NetUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Vincent on 2017/7/3.
 */
public class FeeFullItemDetailActivity extends BaseRealmActionBarActivity {
    public static final String EXTRA_DATA = "data";

    @InjectView(R.id.amountId)
    TextView mAmountTxt;
    @InjectView(R.id.cardNum)
    TextView mcardNumTxt;
    @InjectView(R.id.name)
    TextView mNameTxt;
    @InjectView(R.id.idNum)
    TextView mIdNumTxt;
    @InjectView(R.id.otherNum)
    TextView motherNumTxt;
    @InjectView(R.id.xsNum)
    TextView mXsNumText;
    @InjectView(R.id.xq)
    TextView mXqTxt;
    @InjectView(R.id.yx)
    TextView mYxTxt;
    @InjectView(R.id.major)
    TextView mMajorTxt;
    @InjectView(R.id.bj)
    TextView mBjTxt;
    @InjectView(R.id.qitahaoma)
    LinearLayout qitahaoma;
    @InjectView(R.id.xinshenghao)
    LinearLayout xinshengHao;
    @InjectView(R.id.ll_xuehao)
    LinearLayout llXuehao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
        getDataFromServer(getIntent().getExtras().getString(EXTRA_DATA));
    }

    private void getDataFromServer(String feeId) {
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String, Object> map = new HashMap<>();
        map.put("fee_id", feeId);
        FeeService feeService = ApiFactory.getFactory().create(FeeService.class);
        feeService.get_fee_record(NetUtils.getRequestParams(getActivity(), map),
                NetUtils.getSign(NetUtils.getRequestParams(getActivity(), map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(getActivity(), true, null) {

                    @Override
                    public void onNext(Object o) {
                        FeeDetailResponse feeDetailResponse = new Gson().fromJson(o.toString(), FeeDetailResponse.class);
                        initViews(feeDetailResponse);
                    }
                });
    }

    private void initViews(FeeDetailResponse data) {
        mAmountTxt.setText((Float.parseFloat(data.record.amount)) + "元");

        if (data.account.student_no != null) {
            llXuehao.setVisibility(View.VISIBLE);
            mcardNumTxt.setText(data.account.student_no);
        } else {
            llXuehao.setVisibility(View.VISIBLE);
            mcardNumTxt.setText("-");
        }

        mNameTxt.setText(data.account.name);
        mIdNumTxt.setText(data.account.id_card_no);

        if (TextUtils.isEmpty(data.account.other_no)) {
            qitahaoma.setVisibility(View.GONE);
        } else {
            qitahaoma.setVisibility(View.VISIBLE);
            motherNumTxt.setText(data.account.other_no);
        }

        if (TextUtils.isEmpty(data.account.new_student_no)) {
            xinshengHao.setVisibility(View.GONE);
        } else {
            xinshengHao.setVisibility(View.VISIBLE);
            mXsNumText.setText(data.account.new_student_no);
        }

        if (TextUtils.isEmpty(data.roll.district)){
            mXqTxt.setText("-");
        }else {
            mXqTxt.setText(data.roll.district);
        }

        if (TextUtils.isEmpty(data.roll.academy)){
            mYxTxt.setText("-");
        }else {
            mYxTxt.setText(data.roll.academy);
        }

        if (TextUtils.isEmpty(data.roll.major)){
            mMajorTxt.setText("-");
        }else {
            mMajorTxt.setText(data.roll.major);
        }

        if (TextUtils.isEmpty(data.roll.clazz)){
            mBjTxt.setText("-");
        }else {
            mBjTxt.setText(data.roll.clazz);
        }

    }

    public static class FeeDetailResponse implements Serializable {
        public BaseResponse resp;
        public FeeAccount account;
        public FeeRecord record;
        public SchoolRoll roll;
    }

    @Override
    protected int getContentViewId() {
        return R.layout.fee_notfull_item_detail_activity;
    }
}
