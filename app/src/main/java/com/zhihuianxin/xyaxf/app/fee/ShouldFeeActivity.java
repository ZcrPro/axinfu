package com.zhihuianxin.xyaxf.app.fee;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AbsListView;
import android.widget.RelativeLayout;

import com.zhihuianxin.xyaxf.app.AppConstant;
import modellib.service.FeeService;
import modellib.thrift.base.BaseResponse;
import modellib.thrift.fee.SubFee;
import com.google.gson.Gson;
import com.zhihuianxin.xyaxf.BaseRealmActionBarActivity;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.ApiFactory;
import com.zhihuianxin.xyaxf.app.BaseSubscriber;
import com.zhihuianxin.xyaxf.app.RetrofitFactory;
import com.zhihuianxin.xyaxf.app.fee.adapter.ShouldFeeAdapter;
import com.zhihuianxin.xyaxf.app.utils.NetUtils;
import com.zhihuianxin.xyaxf.app.view.SwipeRefreshAndLoadMoreLayoutStick;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * Created by zcrprozcrpro on 2017/5/19.
 */

public class ShouldFeeActivity extends BaseRealmActionBarActivity {

    @InjectView(R.id.stickLView)
    StickyListHeadersListView stickLView;
    @InjectView(R.id.swipe)
    SwipeRefreshAndLoadMoreLayoutStick swipe;
    @InjectView(R.id.rl_null)
    RelativeLayout rlNull;

    private ShouldFeeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
        loadingData();

        swipe.post(new Runnable() {
            @Override
            public void run() {
                swipe.setRefreshing(true);
            }
        });

        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadingData();
            }
        });

        stickLView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem == 0)
                    swipe.setEnabled(true);
                else
                    swipe.setEnabled(false);
            }
        });
    }

    private void loadingData() {
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String, Object> map = new HashMap<>();
        FeeService feeService = ApiFactory.getFactory().create(FeeService.class);
        feeService.get_required_fees(NetUtils.getRequestParams(this, map), NetUtils.getSign(NetUtils.getRequestParams(this, map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(this, false, null) {
                    @Override
                    public void onNext(Object o) {
                        ShouldFeeResponse shouldFeeResponse = new Gson().fromJson(o.toString(), ShouldFeeResponse.class);
                        if (shouldFeeResponse.resp.resp_code.equals(AppConstant.SUCCESS)) {
                            if (shouldFeeResponse.fees.size() > 0) {
                                adapter = new ShouldFeeAdapter(ShouldFeeActivity.this);
                                adapter.clear();
                                adapter.addAll(shouldFeeResponse.fees);
                                stickLView.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                                if (swipe.isRefreshing()) {
                                    swipe.setRefreshing(false);
                                }
                                rlNull.setVisibility(View.GONE);
                            } else {
                                rlNull.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });
    }

    @Override
    protected int getContentViewId() {
        return R.layout.should_fee_activity;
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

    public static class ShouldFeeResponse {
        public BaseResponse resp;
        public List<SubFee> fees;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.reset(this);
    }
}
