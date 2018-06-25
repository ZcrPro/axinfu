package com.zhihuianxin.xyaxf.app.axxyf.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.zhihuianxin.xyaxf.app.AppConstant;
import com.axinfu.modellib.service.LoanService;
import com.axinfu.modellib.thrift.base.BaseResponse;
import com.google.gson.Gson;
import com.zhihuianxin.xyaxf.BaseRealmFragment;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.ApiFactory;
import com.zhihuianxin.xyaxf.app.BaseSubscriber;
import com.zhihuianxin.xyaxf.app.RetrofitFactory;
import com.zhihuianxin.xyaxf.app.axxyf.HelpActivity;
import com.zhihuianxin.xyaxf.app.axxyf.adapter.HisAdapter;
import com.zhihuianxin.xyaxf.app.utils.NetUtils;
import com.zhihuianxin.xyaxf.app.view.SwipeRefreshAndLoadMoreLayoutStick;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by zcrpro on 2018/1/2.
 */

public class HisFragment extends BaseRealmFragment {

    @InjectView(R.id.recyclerview)
    RecyclerView recyclerview;
    @InjectView(R.id.swipe)
    SwipeRefreshAndLoadMoreLayoutStick mSwipeRefreshLayout;
    @InjectView(R.id.btn_ok)
    Button btnOk;
    @InjectView(R.id.rl_null)
    View nullView;

    private HisAdapter hisAdapter;
    private int mPage;
    private boolean mCanLoad = true;
    private List<BillsHeader> billsHeaders;

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
    }

    @Override
    protected int getLayoutId() {
        return R.layout.his_fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.inject(this, rootView);
        recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerview.setHasFixedSize(true);

        init();

        hisAdapter = new HisAdapter(getActivity(), billsHeaders, R.layout.item_his);
        recyclerview.setAdapter(hisAdapter);

        return rootView;
    }

    private void init() {

        billsHeaders = new ArrayList<>();

        get_history_bills_headers(0);

        mSwipeRefreshLayout.setRefreshing(true);

        recyclerview.setOnScrollListener(new RecyclerView.OnScrollListener() {
            //用来标记是否正在向最后一个滑动
            boolean isSlidingToLast = false;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                // 当不滚动时
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    int lastVisibleItem = manager.findLastCompletelyVisibleItemPosition();
                    int totalItemCount = manager.getItemCount();
                    if (lastVisibleItem == (totalItemCount - 1) && isSlidingToLast) {
                        //加载更多功能的代码
                        if (mCanLoad) {
                            Toast.makeText(getActivity(),"加载更多...",Toast.LENGTH_SHORT).show();
                            if (mPage==0){
                                mPage=1;
                                get_history_bills_headers(mPage++);
                            }else {
                                get_history_bills_headers(mPage++);
                            }
                        }
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                isSlidingToLast = dy > 0;
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mCanLoad = true;
                mPage = 0;
                billsHeaders.clear();
                get_history_bills_headers(mPage);
                mSwipeRefreshLayout.setLoading(false);
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), HelpActivity.class));
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    //获取历史账单头列表
    private void get_history_bills_headers(int page) {
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String, Object> map = new HashMap<>();
        map.put("loan_way_type", "GuiYangCreditLoanPay");
        map.put("page_index", page + "");
        map.put("page_size", "10");
        LoanService loanService = ApiFactory.getFactory().create(LoanService.class);
        loanService.get_history_bills_headers(NetUtils.getRequestParams(getActivity(), map), NetUtils.getSign(NetUtils.getRequestParams(getActivity(), map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(getActivity(), false, null) {

                    @Override
                    public void onNext(Object o) {

                        if (mSwipeRefreshLayout != null) {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }

                        BillResp billResp = new Gson().fromJson(o.toString(), BillResp.class);
                        if(billResp.bills_headers.size() == 0){
                            if(mPage == 0){
                                nullView.setVisibility(View.VISIBLE);
                            } else{
                                Toast.makeText(getActivity(),"暂无更多数据",Toast.LENGTH_SHORT).show();
                            }

                        } else{
                            nullView.setVisibility(View.GONE);
                            billsHeaders.addAll(billResp.bills_headers);
                        }

                        hisAdapter.update(billsHeaders);
                        hisAdapter.notifyDataSetChanged();

                        if (billResp.bills_headers.size() == 0) {
                            mCanLoad = false;
                        }

                    }
                });
    }

    public class BillsHeader implements Serializable {
        public String year_month;                                // 年月
        public String year;                                    // 年
        public String month;                                // 月
        public String currency;                                // 币种
        public String current_balance;                            // 本期应还金额
        public String last_period_bill_amount;                    // 上期账单金额
        public String repayment_amount;                        // 偿还金额
        public String current_period_bill_amount;                // 本期账单金额
        public String interest;                                // 利息
        public String fine_amount;                                // 罚金
        public String other_amount;                            // 其它金额
        public String consume_amount;                            // 消费金额
        public String card_fee;                                // 卡片金额
        public String adjustment_amount;                        // 调整金额
        public String payment_due_date;                        // 还款截止日期
        public String bills_date;                                // 账单日期
        public String repayment_account;                        // 自扣还款账号
        public String repayment_model;                            // 还款方式
        public String create_datetime;                            // 数据创建时间
    }


    public class BillResp {
        public List<BillsHeader> bills_headers;
        public BaseResponse resp;
    }
}
