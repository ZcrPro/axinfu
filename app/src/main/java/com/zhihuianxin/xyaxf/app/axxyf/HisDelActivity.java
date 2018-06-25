package com.zhihuianxin.xyaxf.app.axxyf;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhihuianxin.xyaxf.app.AppConstant;
import com.axinfu.modellib.service.LoanService;
import com.google.gson.Gson;
import com.zhihuianxin.axutil.Util;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.ApiFactory;
import com.zhihuianxin.xyaxf.app.BaseSubscriber;
import com.zhihuianxin.xyaxf.app.RetrofitFactory;
import com.zhihuianxin.xyaxf.app.axxyf.adapter.OutBillListAdapter;
import com.zhihuianxin.xyaxf.app.axxyf.fragment.HisFragment;
import com.zhihuianxin.xyaxf.app.axxyf.fragment.OutBillFragment;
import com.zhihuianxin.xyaxf.app.utils.NetUtils;
import com.zhihuianxin.xyaxf.app.view.SwipeRefreshAndLoadMoreLayoutStick;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by zcrpro on 2018/1/3.
 */

public class HisDelActivity extends Activity {


    @InjectView(R.id.name)
    TextView name;
    @InjectView(R.id.payment_due_date)
    TextView paymentDueDate;
    @InjectView(R.id.current_balance2)
    TextView currentBalance2;
    @InjectView(R.id.current_period_bill_amount)
    TextView currentPeriodBillAmount;
    @InjectView(R.id.last_period_bill_amount)
    TextView lastPeriodBillAmount;
    @InjectView(R.id.repayment_amount)
    TextView repaymentAmount;
    @InjectView(R.id.adjustment_amount)
    TextView adjustmentAmount;
    @InjectView(R.id.interest)
    TextView interest;
    @InjectView(R.id.fine_amount)
    TextView fineAmount;
    @InjectView(R.id.recyclerview)
    RecyclerView recyclerview;
    @InjectView(R.id.swipe)
    SwipeRefreshAndLoadMoreLayoutStick mSwipeRefreshLayout;
    @InjectView(R.id.ic_back)
    ImageView icBack;
    @InjectView(R.id.title)
    TextView title;
    @InjectView(R.id.adjustment_amount_view)
    View adjustment_amount_view;
    @InjectView(R.id.interest_view)
    View interest_view;
    @InjectView(R.id.fine_amount_view)
    View fine_amount_view;

    private HisFragment.BillsHeader billsHeader;
    private OutBillListAdapter outBillListAdapter;

    private int mPage;
    private boolean mCanLoad = true;
    private List<OutBillFragment.BillDetails> billDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.his_del_activity);
        ButterKnife.inject(this);

        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        recyclerview.setHasFixedSize(true);

        icBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        getBundle();
    }

    private void getBundle() {
        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        if (bundle.getSerializable("BillsHeader") != null) {
            billsHeader = (HisFragment.BillsHeader) bundle.getSerializable("BillsHeader");
        }

        title.setText(billsHeader.year_month + "月账单");
        name.setText(billsHeader.year_month + "月账单");
        paymentDueDate.setText(billsHeader.payment_due_date);
        currentBalance2.setText(billsHeader.current_balance);
        currentPeriodBillAmount.setText(billsHeader.current_period_bill_amount);
        lastPeriodBillAmount.setText(billsHeader.last_period_bill_amount);
        if(Util.isEmpty(billsHeader.adjustment_amount) || Float.parseFloat(billsHeader.adjustment_amount) == 0){
            adjustment_amount_view.setVisibility(View.GONE);
        } else{
            adjustment_amount_view.setVisibility(View.VISIBLE);
            adjustmentAmount.setText(billsHeader.adjustment_amount);
        }
        if(Util.isEmpty(billsHeader.interest) || Float.parseFloat(billsHeader.interest) == 0){
            interest_view.setVisibility(View.GONE);
        } else{
            interest_view.setVisibility(View.VISIBLE);
            interest.setText(billsHeader.interest);
        }
        if(Util.isEmpty(billsHeader.fine_amount) || Float.parseFloat(billsHeader.fine_amount) == 0){
            fine_amount_view.setVisibility(View.GONE);
        } else {
            fine_amount_view.setVisibility(View.VISIBLE);
            fineAmount.setText(billsHeader.fine_amount);
        }

        repaymentAmount.setText(billsHeader.repayment_amount);

       init();

        outBillListAdapter = new OutBillListAdapter(HisDelActivity.this, billDetails, R.layout.item_out_bill);
        recyclerview.setAdapter(outBillListAdapter);
    }


    private void init() {
        billDetails = new ArrayList<>();

        get_outer_bills_details(0);

        mSwipeRefreshLayout.setRefreshing(true);

        recyclerview.setOnScrollListener(new RecyclerView.OnScrollListener() {
            //用来标记是否正在向最后一个滑动
            boolean isSlidingToLast = false;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                // 当不滚动时
//                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
//
//                }
                int lastVisibleItem = manager.findLastCompletelyVisibleItemPosition();
                int totalItemCount = manager.getItemCount();
                if (lastVisibleItem == (totalItemCount - 1)) {
                    //加载更多功能的代码
                    if (mCanLoad) {
                        if (mPage==0){
                            mPage=1;
                            get_outer_bills_details(mPage++);
                        }else {
                            get_outer_bills_details(mPage++);
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
                billDetails.clear();
                get_outer_bills_details(mPage);
                mSwipeRefreshLayout.setLoading(false);
            }
        });
    }

    //获取已出账单明细
    private void get_outer_bills_details(int page) {
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String, Object> map = new HashMap<>();
        map.put("loan_way_type", "GuiYangCreditLoanPay");
        map.put("year_month", billsHeader.year_month);
        map.put("page_index", page+"");
        map.put("page_size", "10");
        LoanService loanService = ApiFactory.getFactory().create(LoanService.class);
        loanService.get_outer_bills_details(NetUtils.getRequestParams(this, map), NetUtils.getSign(NetUtils.getRequestParams(this, map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(this, false, null) {

                    @Override
                    public void onNext(Object o) {

                        if (mSwipeRefreshLayout != null) {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }

                        OutBillFragment.BillDtlResp billDtlResp = new Gson().fromJson(o.toString(), OutBillFragment.BillDtlResp.class);
                        billDetails.addAll(billDtlResp.bill_details);

                        outBillListAdapter.update(billDetails);
                        outBillListAdapter.notifyDataSetChanged();

                        if (billDtlResp.bill_details.size() == 0) {
                            mCanLoad = false;
                        }

                    }
                });
    }

}
