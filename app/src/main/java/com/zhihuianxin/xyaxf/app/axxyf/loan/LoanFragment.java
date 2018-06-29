package com.zhihuianxin.xyaxf.app.axxyf.loan;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.zhihuianxin.xyaxf.app.AppConstant;
import modellib.service.LoanService;
import modellib.thrift.base.BaseResponse;
import com.google.gson.Gson;
import com.zhihuianxin.xyaxf.BaseRealmFragment;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.ApiFactory;
import com.zhihuianxin.xyaxf.app.BaseSubscriber;
import com.zhihuianxin.xyaxf.app.RetrofitFactory;
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

public class LoanFragment extends BaseRealmFragment {
    @InjectView(R.id.recyclerview)
    RecyclerView recyclerview;
    @InjectView(R.id.swipe)
    SwipeRefreshAndLoadMoreLayoutStick mSwipeRefreshLayout;
    @InjectView(R.id.rl_null)
    RelativeLayout rlNull;

    private LoanAdapter loanAdapter;

    private int mPage;
    private boolean mCanLoad = true;
    private List<LoanBills> loanBills;

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
    }

    @Override
    protected int getLayoutId() {
        return R.layout.loan_fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.inject(this, rootView);
        recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerview.setHasFixedSize(true);
        init();

        loanAdapter = new LoanAdapter(getActivity(), loanBills, R.layout.item_loan);
        recyclerview.setAdapter(loanAdapter);

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    //获取已还贷款明细
    private void get_repaymented_loan_bills(int page) {
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String, Object> map = new HashMap<>();
        map.put("loan_way_type", "GuiYangCreditLoanPay");
        map.put("page_index", page + "");
        map.put("page_size", "5");
        LoanService loanService = ApiFactory.getFactory().create(LoanService.class);
        loanService.get_repaymented_loan_bills(NetUtils.getRequestParams(getActivity(), map), NetUtils.getSign(NetUtils.getRequestParams(getActivity(), map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(getActivity(), false, null) {

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        if (mSwipeRefreshLayout!=null)
                        mSwipeRefreshLayout.setRefreshing(false);
                        if (rlNull!=null)
                        rlNull.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onNext(Object o) {

                        if (mSwipeRefreshLayout != null) {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }

                        LoanBillResp loanBillResp = new Gson().fromJson(o.toString(), LoanBillResp.class);
                        if (loanBillResp.loan_bills.size() == 0) {
                            if(mPage == 0){
                                rlNull.setVisibility(View.VISIBLE);
                            } else{
                                Toast.makeText(getActivity(),"暂无更多数据",Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            if (rlNull!=null)
                            rlNull.setVisibility(View.GONE);
                            loanBills.addAll(loanBillResp.loan_bills);
                        }

                        loanAdapter.update(loanBills);
                        loanAdapter.notifyDataSetChanged();

                        if (loanBillResp.loan_bills.size() == 0) {
                            mCanLoad = false;
                        }

                    }
                });
    }

    public class LoanBills implements Serializable {
        public String instalment_apply_no;                        // 分期付款申请编号
        public String instalment_trade_date;                    // 分期付款交易日期; 格式yyyy-MM-dd
        public String instalment_sequence_no;                    // 分期付款序号
        public String status;                                    // 状态 ？？？？
        public String credit_plan;                                // 所属信用计划
        public String product_number;                            // 商品编号
        public String product_name;                            // 商品名称
        public String currency;                                // 币种
        public String card_no;                                    // 卡号
        public String loan_amount;                                // 贷款金额
        public String loan_period;                                // 贷款期数
        public String purchase_amount;                            // 利息合计
        public String loan_days;                                // 贷款天数
        public String loan_date;                                // 贷款到期日
        public String day_loan_rate;                            // 贷款费率
        public String residual_capital;                        // 剩余本金
        public String current_interest;                        // 未出账单利息
        public String outerInterest_amount;                    // 已出账单利息（利息合计-未出账-即将产生息费）----未还款
        public String loan_bill_status;                // 提示信息（upcoming:即将到期，undue:未到期，overdue:已逾期）----未还款
        public String residual_should_repayment_amount;        // 剩余应还（剩余本金+未出账息费）----未还款
        public String total_repayment_amount;                    // 还款总额（贷款金额+利息合计）----已还款
        public String current_untilLoan_day_interest;            // 即将产生的账单利息
        public String delay_mark;                                // 延期标识
        public String delay_days;                                // 延期天数
        public String instalment_status_change_date;            // 分期状态变动日期
        public String apply_transfer_bank_card_no;                // 申请转入的银行卡卡号
        public String apply_transfer_bank_no;                    // 申请转入的银行卡银行行号
        public String apply_transfer_bank_name;                // 申请转入的银行卡银行名称
        public String protected_field;                            // 保留字段
        public String repaymentEndDate;                            // 保留字段
    }

    public class LoanBillResp {
        public List<LoanBills> loan_bills;
        public BaseResponse resp;
    }


    private void init() {


        loanBills = new ArrayList<>();

        get_repaymented_loan_bills(0);

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
                                get_repaymented_loan_bills(mPage++);
                            }else {
                                get_repaymented_loan_bills(mPage++);
                            }
                        }
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                isSlidingToLast = true;//dy > 0;
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mCanLoad = true;
                mPage = 0;
                loanBills.clear();
                get_repaymented_loan_bills(mPage);
                mSwipeRefreshLayout.setLoading(false);
            }
        });
    }
}
