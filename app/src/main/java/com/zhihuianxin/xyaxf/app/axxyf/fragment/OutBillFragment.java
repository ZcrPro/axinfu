package com.zhihuianxin.xyaxf.app.axxyf.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.zhihuianxin.xyaxf.app.AppConstant;
import com.axinfu.modellib.service.LoanService;
import com.axinfu.modellib.thrift.base.BaseResponse;
import com.google.gson.Gson;
import com.zhihuianxin.xyaxf.app.base.axutil.Util;
import com.zhihuianxin.xyaxf.App;
import com.zhihuianxin.xyaxf.BaseRealmFragment;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.ApiFactory;
import com.zhihuianxin.xyaxf.app.BaseSubscriber;
import com.zhihuianxin.xyaxf.app.RetrofitFactory;
import com.zhihuianxin.xyaxf.app.axxyf.HelpOldActivity;
import com.zhihuianxin.xyaxf.app.axxyf.adapter.OutBillListAdapter;
import com.zhihuianxin.xyaxf.app.utils.NetUtils;
import com.zhihuianxin.xyaxf.app.view.SwipeRefreshAndLoadMoreLayoutStick;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by zcrpro on 2018/1/2.
 */

public class OutBillFragment extends BaseRealmFragment {

    @InjectView(R.id.current_balance)
    TextView currentBalance;
    @InjectView(R.id.payment_due_date)
    TextView paymentDueDate;
    @InjectView(R.id.current_balance2)
    TextView currentBalance2;
    @InjectView(R.id.current_period_bill_amount)
    TextView currentPeriodBillAmount;
    @InjectView(R.id.last_period_bill_amount)
    TextView lastPeriodBillAmount;
    @InjectView(R.id.adjustment_amount)
    TextView adjustmentAmount;
    @InjectView(R.id.interest)
    TextView interest;
    @InjectView(R.id.fine_amount)
    TextView fineAmount;
    @InjectView(R.id.recyclerview)
    RecyclerView recyclerview;
    @InjectView(R.id.name)
    TextView name;
    @InjectView(R.id.repayment_amount)
    TextView repaymentAmount;
    @InjectView(R.id.swipe)
    SwipeRefreshAndLoadMoreLayoutStick mSwipeRefreshLayout;
    @InjectView(R.id.btn_ok)
    Button btnOk;
    @InjectView(R.id.adjustment_amount_view)
    View adjustment_amount_view;
    @InjectView(R.id.interest_view)
    View interest_view;
    @InjectView(R.id.fine_amount_view)
    View fine_amount_view;
    @InjectView(R.id.rl_null)
    View nullView;

    private OutBillListAdapter outBillListAdapter;

    private int mPage;
    private boolean mCanLoad = true;
    private LinkedList<BillDetails> billDetails;
    private String year_month;

    @Override
    protected void initView(View view, Bundle savedInstanceState) {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.out_bill_fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.inject(this, rootView);

        recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerview.setHasFixedSize(true);

        mSwipeRefreshLayout.setVisibility(View.GONE);
        get_bills_header();

        mSwipeRefreshLayout.setRefreshing(true);

        billDetails = new LinkedList<>();

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), HelpOldActivity.class));
            }
        });


        outBillListAdapter = new OutBillListAdapter(getActivity(), billDetails, R.layout.item_out_bill);
        recyclerview.setAdapter(outBillListAdapter);

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    public class BillsHeader {
        public String year_month;                                // 年月
        public String year;                                    // 年
        public String month;                                    // 月
        public String currency;                                // 币种
        public String current_balance;                            // 本期应还金额
        public String last_period_bill_amount;                    // 上期账单金额
        public String repayment_amount;                        // 偿还金额
        public String current_period_bill_amount;                // 本期账单金额
        public String interest;                                // 利息
        public String fine_amount;                                // 罚金
        public String other_amount;                        // 其它金额
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
        public BillsHeader bills_header;
        public BaseResponse resp;
    }

    //获取账单头,最后一期已出账单
    private void get_bills_header() {
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String, Object> map = new HashMap<>();
        map.put("loan_way_type", "GuiYangCreditLoanPay");
        LoanService loanService = ApiFactory.getFactory().create(LoanService.class);
        loanService.get_bills_header(NetUtils.getRequestParams(getActivity(), map), NetUtils.getSign(NetUtils.getRequestParams(getActivity(), map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(getActivity(), false, null) {

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        mSwipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onNext(Object o) {
                        final BillResp billResp = new Gson().fromJson(o.toString(), BillResp.class);
                        if(billResp.bills_header == null || Util.isEmpty(billResp.bills_header.month)){
                            mSwipeRefreshLayout.setRefreshing(false);
                            if(nullView != null){
                                nullView.setVisibility(View.VISIBLE);
                            }
                            return;
                        }

                        mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                        nullView.setVisibility(View.GONE);
                        name.setText(billResp.bills_header.year_month + "月账单");
                        if(App.loanAccountInfoRep.loan_account_info == null){
                            return;
                        }
                        for (int i = 0; i < App.loanAccountInfoRep.loan_account_info.valid_loan_way_account_info.size(); i++) {
                            if (App.loanAccountInfoRep.loan_account_info.valid_loan_way_account_info.get(i).loan_way.type.equals("GuiYangCreditLoanPay")) {
                                currentBalance.setText(App.loanAccountInfoRep.loan_account_info.valid_loan_way_account_info.get(i).last_period_not_repayment_amt);
                            }
                        }
                        paymentDueDate.setText(billResp.bills_header.payment_due_date);
                        currentBalance2.setText(billResp.bills_header.current_balance);
                        currentPeriodBillAmount.setText(billResp.bills_header.current_period_bill_amount);
                        lastPeriodBillAmount.setText(billResp.bills_header.last_period_bill_amount);
                        if(Util.isEmpty(billResp.bills_header.adjustment_amount) || Float.parseFloat(billResp.bills_header.adjustment_amount) == 0){
                            adjustment_amount_view.setVisibility(View.GONE);
                        } else{
                            adjustment_amount_view.setVisibility(View.VISIBLE);
                            adjustmentAmount.setText(billResp.bills_header.adjustment_amount);
                        }
                        if(Util.isEmpty(billResp.bills_header.interest) || Float.parseFloat(billResp.bills_header.interest) == 0){
                            interest_view.setVisibility(View.GONE);
                        } else{
                            interest_view.setVisibility(View.VISIBLE);
                            interest.setText(billResp.bills_header.interest);
                        }
                        if(Util.isEmpty(billResp.bills_header.fine_amount) || Float.parseFloat(billResp.bills_header.fine_amount) == 0){
                            fine_amount_view.setVisibility(View.GONE);
                        } else {
                            fine_amount_view.setVisibility(View.VISIBLE);
                            fineAmount.setText(billResp.bills_header.fine_amount);
                        }
                        repaymentAmount.setText(billResp.bills_header.repayment_amount);

                        get_outer_bills_details(mPage, billResp.bills_header.year_month);

                        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                            @Override
                            public void onRefresh() {
                                mCanLoad = true;
                                mPage = 0;
                                billDetails.clear();
                                get_outer_bills_details(mPage, billResp.bills_header.year_month);
                                mSwipeRefreshLayout.setLoading(false);
                            }
                        });

                        year_month = billResp.bills_header.year_month;

                        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                            @Override
                            public void onRefresh() {
                                mCanLoad = true;
                                mPage = 0;
                                billDetails.clear();
                                get_outer_bills_details(mPage, year_month);
                                mSwipeRefreshLayout.setLoading(false);
                            }
                        });

                    }
                });

        recyclerview.setOnScrollListener(new RecyclerView.OnScrollListener() {
            //用来标记是否正在向最后一个滑动
            boolean isSlidingToLast = false;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                // 当不滚动时
//                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
//                }
                int lastVisibleItem = manager.findLastCompletelyVisibleItemPosition();
                int totalItemCount = manager.getItemCount();
                if (lastVisibleItem == (totalItemCount - 1)) {
                    //加载更多功能的代码
                    if (mCanLoad) {
                        if (mPage==0){
                            mPage=1;
                            Log.d("OutBillFragment", "OPage1:" + mPage);
                            get_outer_bills_details(mPage++, year_month);
                        }else {
                            get_outer_bills_details(mPage++, year_month);
                            Log.d("OutBillFragment", "OPage2:" + mPage);
                        }

                        Log.d("OutBillFragment", "OPage3:" + mPage);
                    }
                }

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                isSlidingToLast = dy > 0;
            }
        });

    }

    //获取已出账单明细
    private void get_outer_bills_details(int mPage, String year_month) {
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String, Object> map = new HashMap<>();
        map.put("loan_way_type", "GuiYangCreditLoanPay");
        map.put("year_month", year_month);
        map.put("page_index", mPage + "");
        map.put("page_size", "10");
        LoanService loanService = ApiFactory.getFactory().create(LoanService.class);
        loanService.get_outer_bills_details(NetUtils.getRequestParams(getActivity(), map), NetUtils.getSign(NetUtils.getRequestParams(getActivity(), map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(getActivity(), false, null) {

                    @Override
                    public void onNext(Object o) {
                        if (mSwipeRefreshLayout != null) {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }

                        BillDtlResp billDtlResp = new Gson().fromJson(o.toString(), BillDtlResp.class);

                        billDetails.addAll(billDtlResp.bill_details);

                        outBillListAdapter.update(billDetails);
                        outBillListAdapter.notifyDataSetChanged();

                        if (billDtlResp.bill_details.size() < 10) {
                            mCanLoad = false;
                        }
                    }
                });
    }

    public class BillDetails {
        public String enter_account_date;                        // 入账日期，目前值为空
        public String serial_no;                                // 交易流水号，目前值为空
        public String trade_name;                                // 交易名称
        public String trade_date;                                // 交易日期, 格式yyyy-MM-dd
        public String trade_time;                                // 交易时间，格式HH:mm:ss
        public String trade_type;                                // 交易类型，目前值为空
        public String trade_amt;                                // 交易金额
        public String remark;                                    // 备注
    }

    public class BillDtlResp {
        public List<BillDetails> bill_details;
        public BaseResponse resp;
    }

}
