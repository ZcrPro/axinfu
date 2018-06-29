package com.zhihuianxin.xyaxf.app.axxyf.loan;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.zhihuianxin.xyaxf.app.AppConstant;
import modellib.service.LoanService;
import com.google.gson.Gson;
import com.zhihuianxin.xyaxf.BaseRealmFragment;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.ApiFactory;
import com.zhihuianxin.xyaxf.app.BaseSubscriber;
import com.zhihuianxin.xyaxf.app.RetrofitFactory;
import com.zhihuianxin.xyaxf.app.utils.NetUtils;
import com.zhihuianxin.xyaxf.app.utils.RxBus;
import com.zhihuianxin.xyaxf.app.view.SwipeRefreshAndLoadMoreLayoutStick;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by zcrpro on 2018/1/2.
 */

public class NotLoanFragment extends BaseRealmFragment {

    @InjectView(R.id.recyclerview)
    RecyclerView recyclerview;
    @InjectView(R.id.swipe)
    SwipeRefreshAndLoadMoreLayoutStick mSwipeRefreshLayout;
    @InjectView(R.id.rl_null)
    RelativeLayout rlNull;

    private NotLoanAdapter notLoanAdapter;
    private int mPage;
    private boolean mCanLoad = true;
    private List<LoanFragment.LoanBills> loanBills;
    private Subscription rxSbscription;

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
    }

    @Override
    protected int getLayoutId() {
        return R.layout.not_loan_fragment;
    }

    //获取未还贷款明细
    private void get_not_repaymented_loan_bills(int page) {
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String, Object> map = new HashMap<>();
        map.put("loan_way_type", "GuiYangCreditLoanPay");
        map.put("page_index", page+"");
        map.put("page_size", "5");
        LoanService loanService = ApiFactory.getFactory().create(LoanService.class);
        loanService.get_not_repaymented_loan_bills(NetUtils.getRequestParams(getActivity(), map), NetUtils.getSign(NetUtils.getRequestParams(getActivity(), map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Object>(getActivity(), false, null) {

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        if (mSwipeRefreshLayout != null) {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                        if(rlNull!=null)
                        rlNull.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onNext(Object o) {

                        if (mSwipeRefreshLayout != null) {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }

                        LoanFragment.LoanBillResp loanBillResp = new Gson().fromJson(o.toString(), LoanFragment.LoanBillResp.class);
                        loanBills.addAll(loanBillResp.loan_bills);

                        if(loanBills.size() == 0){
                            if(rlNull!=null)
                            rlNull.setVisibility(View.VISIBLE);
                        } else{
                            if(rlNull!=null){
                                rlNull.setVisibility(View.GONE);
                            }
                            notLoanAdapter.update(loanBills);
                            notLoanAdapter.notifyDataSetChanged();
                        }


                        if (loanBillResp.loan_bills.size() == 0) {
                            mCanLoad = false;
                        }
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.inject(this, rootView);
        recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerview.setHasFixedSize(true);
        init();

        notLoanAdapter = new NotLoanAdapter(getActivity(),loanBills, R.layout.item_not_loan);
        recyclerview.setAdapter(notLoanAdapter);

        rxSbscription = RxBus.getDefault().toObservable().subscribe(new Action1<Object>() {
            @Override
            public void call(Object event) {
                if(event.equals("refreshLoanList")){
                    if(mSwipeRefreshLayout != null){
                        mSwipeRefreshLayout.setRefreshing(true);
                        refreshFun();
                    }
                }
            }
        });

        return rootView;
    }

    private void init() {


        loanBills = new ArrayList<>();

        get_not_repaymented_loan_bills(0);

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
                            if (mPage==0){
                                mPage=1;
                                get_not_repaymented_loan_bills(mPage++);
                            }else {
                                get_not_repaymented_loan_bills(mPage++);
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
                refreshFun();
            }
        });
    }

    private void refreshFun(){
        mCanLoad = true;
        mPage = 0;
        loanBills.clear();
        get_not_repaymented_loan_bills(mPage);
        mSwipeRefreshLayout.setLoading(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
