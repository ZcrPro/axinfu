package com.zhihuianxin.xyaxf.app.fee.feelist.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhihuianxin.xyaxf.app.AppConstant;
import modellib.thrift.customer.Customer;
import modellib.thrift.fee.FeeAccount;
import modellib.thrift.fee.FeeRecord;
import com.zhihuianxin.xyaxf.app.base.axutil.SchedulerProvider;
import com.zhihuianxin.xyaxf.app.base.axutil.Util;
import com.zhihuianxin.xyaxf.App;
import com.zhihuianxin.xyaxf.BaseRealmFragment;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.fee.FeeActivity;
import com.zhihuianxin.xyaxf.app.fee.adapter.FeeFullAdapter;
import com.zhihuianxin.xyaxf.app.fee.feelist.FeeFullContract;
import com.zhihuianxin.xyaxf.app.fee.feelist.FeeFullPresenter;
import com.zhihuianxin.xyaxf.app.view.GifView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.realm.CustomerRealmProxy;
import io.realm.FeeAccountRealmProxy;
import io.realm.RealmResults;

/**
 * Created by zcrpro on 2016/10/19.
 * 已缴费
 */

public class FeeFulfilFragment extends BaseRealmFragment implements FeeFullContract.FeeFullView {

    @InjectView(R.id.rl_null)
    RelativeLayout rlNull;
    @InjectView(R.id.recyclerview)
    RecyclerView recyclerview;
    @InjectView(R.id.swiperefreshlayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @InjectView(R.id.iv_null)
    ImageView ivNull;
    @InjectView(R.id.gif_view)
    GifView gifView;
    @InjectView(R.id.text)
    TextView text;
    @InjectView(R.id.loading)
    LinearLayout loading;

    private FeeFullAdapter feeFullAdapter;
    private FeeFullContract.FeeFullPresenter presenter;
    private FeeFullPresenter fullPresenter;
    private String channal;
    protected boolean isVisible;
    private int page = 0;
    private boolean needLoadMore = true;
    public static boolean isFrist = true;

    /**
     * 在这里实现Fragment数据的缓加载.
     *
     * @param isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            isVisible = true;
            recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
            fullPresenter = new FeeFullPresenter(this, getActivity(), SchedulerProvider.getInstance());
            final RealmResults<Customer> customers = realm.where(Customer.class).equalTo("mobile", App.mAxLoginSp.getUserMobil()).findAll();
            final FeeAccount feeAccount = ((CustomerRealmProxy) customers.get(0)).realmGet$fee_account();

            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    page = 0;
                    needLoadMore = true;
                    if (channal.equals(FeeActivity.normal)) {
                        presenter.loadFeeRecordList(null, null, page, AppConstant.PAGE_SIZE, false);
                    } else {
                        if (((FeeAccountRealmProxy) feeAccount).realmGet$other_no() != null)
                            presenter.loadOtherFeeRecordList(((FeeAccountRealmProxy) feeAccount).realmGet$name(), ((FeeAccountRealmProxy) feeAccount).realmGet$other_no(), null, null, page, AppConstant.PAGE_SIZE, false);
                    }
                }
            });

            Bundle bundle = getArguments();//从activity传过来的Bundle
            if (bundle != null) {
                channal = bundle.getString(FeeActivity.ENTER_FLAG);
                assert channal != null;
                if (channal.equals(FeeActivity.normal)) {
                    if (!Util.isEmpty(App.mAxLoginSp.getOtherFeeNo())) {
                        if (isFrist) {
                            if (loading != null)
                                loading.setVisibility(View.VISIBLE);
                            if (gifView != null)
                                gifView.setMovieResource(R.raw.gif_loading);
                        }
                        presenter.loadOtherFeeRecordList(((FeeAccountRealmProxy) feeAccount).realmGet$name(), App.mAxLoginSp.getOtherFeeNo(), null, null, page, AppConstant.PAGE_SIZE, false);
                    } else {
                        //如果是绑定进入
                        if (isFrist) {
                            if (loading != null)
                                loading.setVisibility(View.VISIBLE);
                            if (gifView != null)
                                gifView.setMovieResource(R.raw.gif_loading);
                            FeeFulfilFragment.isFrist = false;
                        }
                        presenter.loadFeeRecordList(null, null, page, AppConstant.PAGE_SIZE, false);
                    }
                } else {
                    if (((FeeAccountRealmProxy) feeAccount).realmGet$other_no() != null && ((FeeAccountRealmProxy) feeAccount).realmGet$id_card_no() != null) {
                        if (isFrist) {
                            if (loading != null)
                                loading.setVisibility(View.VISIBLE);
                            if (gifView != null)
                                gifView.setMovieResource(R.raw.gif_loading);
                            FeeFulfilFragment.isFrist = false;
                        }
                        presenter.get_new_student_fee_records(((FeeAccountRealmProxy) feeAccount).realmGet$name(), ((FeeAccountRealmProxy) feeAccount).realmGet$id_card_no(), ((FeeAccountRealmProxy) feeAccount).realmGet$other_no(), null, null, page, AppConstant.PAGE_SIZE, false);
                    }
                }
            }

            recyclerview.setOnScrollListener(new RecyclerView.OnScrollListener() {
                //用来标记是否正在向最后一个滑动
                boolean isSlidingToLast = false;

                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    // 当不滚动时
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        //获取最后一个完全显示的ItemPosition
                        int lastVisibleItem = manager.findLastCompletelyVisibleItemPosition();
                        int totalItemCount = manager.getItemCount();

                        // 判断是否滚动到底部，并且是向右滚动
                        if (lastVisibleItem == (totalItemCount - 1) && isSlidingToLast) {
                            //加载更多功能的代码
                            if (needLoadMore) {
                                Log.d("FeeFulfilFragment", "可以加载更多了");
                                page = page + 1;
                                if (channal.equals(FeeActivity.normal)) {
                                    if (!Util.isEmpty(App.mAxLoginSp.getOtherFeeNo())) {
                                        presenter.loadOtherFeeRecordList(((FeeAccountRealmProxy) feeAccount).realmGet$name(), ((FeeAccountRealmProxy) feeAccount).realmGet$other_no(), null, null, page, AppConstant.PAGE_SIZE, true);
                                    }else {
                                        presenter.loadFeeRecordList(null, null, page, AppConstant.PAGE_SIZE, true);
                                    }
                                } else {
                                    if (((FeeAccountRealmProxy) feeAccount).realmGet$other_no() != null && ((FeeAccountRealmProxy) feeAccount).realmGet$id_card_no() != null)
                                        presenter.get_new_student_fee_records(((FeeAccountRealmProxy) feeAccount).realmGet$name(), ((FeeAccountRealmProxy) feeAccount).realmGet$id_card_no(), ((FeeAccountRealmProxy) feeAccount).realmGet$other_no(), null, null, page, AppConstant.PAGE_SIZE, false);
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

        } else {
            isVisible = false;
        }
    }


    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        Bundle bundle = getArguments();//从activity传过来的Bundle
        if (bundle != null) {
            String name = bundle.getString(FeeActivity.ENTER_FLAG);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fee_fulfil_fragment;
    }

    @Override
    public void feeFullSuccess(List<FeeRecord> fee_records, boolean isLoadMore) {
        if (isLoadMore) {
            if (fee_records.size() < AppConstant.PAGE_SIZE) {
                needLoadMore = false;
                if (fee_records.size() != 0) {
                    if (feeFullAdapter != null)
                        feeFullAdapter.addAll(fee_records);
                }
            } else {
                if (feeFullAdapter != null)
                    feeFullAdapter.addAll(fee_records);
            }
        } else {
            if (fee_records.isEmpty()) {
                if (rlNull != null)
                    rlNull.setVisibility(View.VISIBLE);
                recyclerview.setVisibility(View.GONE);
            } else {
                if (rlNull != null)
                    rlNull.setVisibility(View.GONE);
                recyclerview.setVisibility(View.VISIBLE);
                setDataToUI(fee_records);
                if (swipeRefreshLayout != null)
                    swipeRefreshLayout.setRefreshing(false);
            }

        }
    }

    private void setDataToUI(final List<FeeRecord> fee_records) {
        feeFullAdapter = new FeeFullAdapter(getActivity(), fee_records, R.layout.fee_fulfil_item);
        feeFullAdapter.setOnItemClickListener(new FeeFullAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(getActivity(),FeeFullItemDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(FeeFullItemDetailActivity.EXTRA_DATA,fee_records.get(position).fee_id);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        recyclerview.setAdapter(feeFullAdapter);
        feeFullAdapter.notifyDataSetChanged();
    }

    @Override
    public void feeFullFailure() {

    }

    @Override
    public void setPresenter(FeeFullContract.FeeFullPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void loadStart() {

    }

    @Override
    public void loadError(String errorMsg) {
        if (swipeRefreshLayout != null)
            swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void loadComplete() {
        if (swipeRefreshLayout != null)
            swipeRefreshLayout.setRefreshing(false);
        if (loading != null)
            loading.setVisibility(View.GONE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.inject(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
        rlNull = null;
    }
}
