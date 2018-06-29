package com.zhihuianxin.xyaxf.app.ecard.view;

import android.content.Context;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.zhihuianxin.xyaxf.app.AppConstant;
import modellib.service.EcardService;
import modellib.thrift.base.BaseResponse;
import modellib.thrift.ecard.ECardChargeRecord;
import com.google.gson.Gson;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.ApiFactory;
import com.zhihuianxin.xyaxf.app.RetrofitFactory;
import com.zhihuianxin.xyaxf.app.ecard.adapter.PaymentRecordAdapter;
import com.zhihuianxin.xyaxf.app.utils.NetUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.RealmObject;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.zhihuianxin.xyaxf.R.id.recyclerview;

/**
 * Created by zcrpro on 2016/11/9.
 */
public class EcardRecordWindow extends PopupWindow {

    private View view;
    private PaymentRecordAdapter paymentRecordAdapter;
    private Context context;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private int page;
    private boolean needLoadMore = true;

    public EcardRecordWindow(Context context) {
        super(context);
        if (context == null) {
            return;
        }
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService("layout_inflater");// Context.LAYOUT_INFLATER_SERVICE
        view = inflater.inflate(R.layout.ecard_record_window, null);
        ImageView back = (ImageView) view.findViewById(R.id.back);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefreshlayout);
        recyclerView = (RecyclerView) view.findViewById(recyclerview);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EcardRecordWindow.this.dismiss();
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                loadEccrdBillData(null, null, page, AppConstant.PAGE_SIZE, false);
            }
        }, 2000);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 0;
                loadEccrdBillData(null, null, page, AppConstant.PAGE_SIZE, false);
            }
        });

        this.setContentView(view);
        //设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        //设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        //设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        //设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.ecard_record_popwindow_anim_style);
        this.setBackgroundDrawable(null);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, true));
        recyclerView.setHasFixedSize(true);

//        loadEccrdBillData(null, null, page, AppConstant.PAGE_SIZE, false);

        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
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
                        if (needLoadMore) {
                            page = page + 1;
                            loadEccrdBillData(null, null, page, AppConstant.PAGE_SIZE, true);
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


        recyclerView.setLayoutManager(new SnappingLinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        recyclerView.smoothScrollToPosition(0);

    }

    private void loadEccrdBillData(String start_date, String end_date, int page_index, int size, final boolean loadMore) {
        RetrofitFactory.setBaseUrl(AppConstant.URL);
        Map<String, Object> map = new HashMap<>();
        map.put("start_date", start_date);
        map.put("end_date", end_date);
        map.put("page_index", page_index);
        map.put("page_size", size);
        EcardService ecardService = ApiFactory.getFactory().create(EcardService.class);
        ecardService.getChargeRecords(NetUtils.getRequestParams(context, map), NetUtils.getSign(NetUtils.getRequestParams(context, map)))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Object>() {

                    @Override
                    public void onCompleted() {
                        if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(Object o) {
                        final EcardBillResponse ecardBillResponse = new Gson().fromJson(o.toString(), EcardBillResponse.class);
                        if (loadMore) {
                            if (ecardBillResponse.records.size() < AppConstant.PAGE_SIZE) {
                                needLoadMore = false;
                                if (ecardBillResponse.records.size() != 0) {
                                    paymentRecordAdapter.addAll(ecardBillResponse.records);
                                    paymentRecordAdapter.notifyDataSetChanged();
                                }
                            } else {
                                paymentRecordAdapter.addAll(ecardBillResponse.records);
                                paymentRecordAdapter.notifyDataSetChanged();
                            }
                        } else {
                            paymentRecordAdapter = new PaymentRecordAdapter(context, ecardBillResponse.records, R.layout.ecard_record_item);
                            recyclerView.setAdapter(paymentRecordAdapter);
                            paymentRecordAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    public static class EcardBillResponse extends RealmObject {
        public BaseResponse resp;
        public List<ECardChargeRecord> records;
    }
}