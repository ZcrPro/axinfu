package com.zhihuianxin.xyaxf.app.unionqr_pay.view.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zhihuianxin.xyaxf.app.AppConstant;
import com.axinfu.modellib.thrift.fee.PaymentRecord;
import com.axinfu.modellib.thrift.unqr.UPQRPayRecord;
import com.zhihuianxin.xyaxf.app.base.axutil.SchedulerProvider;
import com.zhihuianxin.xyaxf.app.base.axutil.Util;
import com.zhihuianxin.xyaxf.BaseRealmActionBarActivity;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.me.contract.IMePayListContract;
import com.zhihuianxin.xyaxf.app.me.presenter.MePayListPresenter;
import com.zhihuianxin.xyaxf.app.me.view.adapter.UnionRecordListAdapter;
import com.zhihuianxin.xyaxf.app.view.SwipeRefreshAndLoadMoreLayoutStick;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Calendar;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * Created by Vincent on 2017/12/7.
 */

public class UnionSweptRecordListActivity extends BaseRealmActionBarActivity implements IMePayListContract.IMePayListView {

    @InjectView(R.id.stickLView)
    StickyListHeadersListView stickLView;
    @InjectView(R.id.swipe)
    SwipeRefreshAndLoadMoreLayoutStick swipe;
    @InjectView(R.id.iv_null)
    ImageView ivNull;
    @InjectView(R.id.null_data)
    TextView nullData;
    @InjectView(R.id.ll_content)
    RelativeLayout llContent;
    @InjectView(R.id.back)
    View backView;
    @InjectView(R.id.selectData)
    View selectDataView;

    private IMePayListContract.IMePayListPresenter mPresenter;
    private SwipeRefreshAndLoadMoreLayoutStick mSwipeRefreshLayout;
    private StickyListHeadersListView mListView;
    private UnionRecordListAdapter mAdapter;
    private int mPage = 0;
    private boolean mCanLoad = true;
    private Calendar showDate;
    private boolean isCheckDate = false;
    private int years;
    private int mons;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
        new MePayListPresenter(this, this, SchedulerProvider.getInstance());
        showDate = Calendar.getInstance();
        mSwipeRefreshLayout = (SwipeRefreshAndLoadMoreLayoutStick) findViewById(R.id.swipe);
        mListView = (StickyListHeadersListView) findViewById(R.id.stickLView);
        findViewById(R.id.action_bar).setVisibility(View.GONE);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isCheckDate) {
                    mCanLoad = true;
                    mPage = 0;
                    mons = mons + 1;
                    if (mons > 12) {
                        years = years + 1;
                        mons = 1;
                    }
                    DecimalFormat df = new DecimalFormat("00");
                    String str1 = df.format(Integer.parseInt(mons + ""));
                    mPresenter.loadUnionSwepPayList(years + str1 + "01", years + str1 + Integer.parseInt(getDaysByYearMonth(years, mons) + "") + "", "0", 100 + "");
                    Log.d("TAGS", "onRefresh: " + (years + str1 + "01" + "---" + years + str1 + Integer.parseInt(getDaysByYearMonth(years, mons) + "")));
                } else {
                    mCanLoad = true;
                    mPage = 0;
                    mPresenter.loadUnionSwepPayList(null, null, "0", AppConstant.PAGE_SIZE + "");
                }
            }
        });
        mSwipeRefreshLayout.setOnLoadListener(new SwipeRefreshAndLoadMoreLayoutStick.OnLoadListener() {
            @Override
            public void onLoad() {
                if (mCanLoad) {
                    mPresenter.loadUnionSwepPayList(null, null, (++mPage) + "", AppConstant.PAGE_SIZE + "");
                    mSwipeRefreshLayout.setLoading(true);
                }
            }
        });
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem == 0)
                    mSwipeRefreshLayout.setEnabled(true);
                else
                    mSwipeRefreshLayout.setEnabled(false);
            }
        });

        mAdapter = new UnionRecordListAdapter(this);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position < mAdapter.getCount()) {
                    Intent i = new Intent(UnionSweptRecordListActivity.this, UnionSweptRecordDetailActivity.class);
                    Bundle bundle = new Bundle();

                    bundle.putSerializable(UnionSweptRecordDetailActivity.EXTRA_UNION_SWEP_DETAIL, (Serializable) view.getTag());
                    i.putExtras(bundle);
                    startActivity(i);
                }
            }
        });
        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        selectDataView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateDialog();
            }
        });

        RealmResults<UPQRPayRecord> records = realm.where(UPQRPayRecord.class).findAll();
        if (records.size() == 0) {
            mSwipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(true);
                }
            });
            mPresenter.loadUnionSwepPayList(null, null, "0", AppConstant.PAGE_SIZE + "");
        } else {
            setDBDataToUI(records);
            if (records.size() < AppConstant.PAGE_SIZE) {
                mSwipeRefreshLayout.setOnLoadListener(null);
            }
        }
    }

    private void setDBDataToUI(RealmResults<UPQRPayRecord> dataList) {
        for (UPQRPayRecord obj : dataList) {
            UPQRPayRecord record = new UPQRPayRecord();
//            record.order_no = ((PaymentRecordRealmProxy) obj).realmGet$order_no();
//            record.order_time = ((PaymentRecordRealmProxy) obj).realmGet$order_time();
//            record.payfor = ((PaymentRecordRealmProxy) obj).realmGet$payfor();
//            record.order_status = ((PaymentRecordRealmProxy) obj).realmGet$order_status();
//            record.order_status_desc = ((PaymentRecordRealmProxy) obj).realmGet$order_status_desc();
//            record.pay_amt = ((PaymentRecordRealmProxy) obj).realmGet$pay_amt();
//            record.pay_channel = ((PaymentRecordRealmProxy) obj).realmGet$pay_channel();
//            record.trade_summary = ((PaymentRecordRealmProxy) obj).realmGet$trade_summary();
//            record.pay_time = ((PaymentRecordRealmProxy) obj).realmGet$pay_time();

            UnionRecordListAdapter.PaymentRecordExt ext = new UnionRecordListAdapter.PaymentRecordExt();
            ext.paymentRecord = record;
            int[] timeItems = record.order_time != null ? Util.getTimeItems(record.order_time) : null;
            ext.category_value = String.format("%04d-%02d", timeItems[0], timeItems[1]);
            ext.category = ext.category_value;
            mAdapter.add(ext);
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected int getContentViewId() {
        return R.layout.union_pay_list_activity;
    }
    @Override
    public void setPayClosedList(RealmList<PaymentRecord> payList) {}
    @Override
    public void setPayList(RealmList<PaymentRecord> payList) {}

    @Override
    public void setUnionSwepPayList(RealmList<UPQRPayRecord> payList) {
        if(selectDataView !=null){
            if(isCheckDate){
                selectDataView.setVisibility(View.VISIBLE);
            } else{
                if(payList != null && payList.size() > 0){ // 筛选是否显示
                    selectDataView.setVisibility(View.VISIBLE);
                } else{
                    selectDataView.setVisibility(View.GONE);
                }
            }
        }

        if (payList.size() < AppConstant.PAGE_SIZE && mPage == 0) {
            if (!isCheckDate) {
                mSwipeRefreshLayout.setOnLoadListener(null);
            } else {
                mSwipeRefreshLayout.setOnLoadListener(new SwipeRefreshAndLoadMoreLayoutStick.OnLoadListener() {
                    @Override
                    public void onLoad() {
                        if (mons > 1) {
                            mons = mons - 1;
                        } else if (mons == 1) {
                            mons = 1;
                            years = years - 1;
                        }
                        DecimalFormat df = new DecimalFormat("00");
                        String str1 = df.format(Integer.parseInt(mons + ""));
                        mPresenter.loadUnionSwepPayList(years + str1 + "01", years + str1 + Integer.parseInt(getDaysByYearMonth(years, mons) + "") + "", "0", 100 + "");
                        Log.d("TAGS", "上拉: " + years + str1 + "01" + "---" + years + str1 + Integer.parseInt(getDaysByYearMonth(years, mons) + "") + "");
                    }
                });
            }
        } else {
            mSwipeRefreshLayout.setOnLoadListener(new SwipeRefreshAndLoadMoreLayoutStick.OnLoadListener() {
                @Override
                public void onLoad() {
                    if (mCanLoad) {
                        mPresenter.loadUnionSwepPayList(null, null, (++mPage) + "", AppConstant.PAGE_SIZE + "");
                        mSwipeRefreshLayout.setLoading(true);
                    }
                }
            });
        }

        if (mPage == 0) {
            if (payList.size() == 0) {
                if (!isCheckDate) {
//                    Toast.makeText(this, "没有更多数据了!", Toast.LENGTH_LONG).show();
                    if (mAdapter.isEmpty()) {
                        if (ivNull != null && nullData != null) {
                            ivNull.setVisibility(View.VISIBLE);
                            nullData.setVisibility(View.VISIBLE);
                        }
                    } else {
                        Toast.makeText(this, "没有更多数据了!", Toast.LENGTH_LONG).show();
                    }
                } else {
                    if (mAdapter.isEmpty()) {
                        if (ivNull != null && nullData != null) {
                            ivNull.setVisibility(View.VISIBLE);
                            nullData.setVisibility(View.VISIBLE);
                        }
                    } else {
                        Toast.makeText(this, "没有更多数据了!", Toast.LENGTH_LONG).show();
                    }
                }
            } else {
                if (ivNull != null && nullData != null) {
                    ivNull.setVisibility(View.GONE);
                    nullData.setVisibility(View.GONE);
                }
                mAdapter.clear();
                final RealmResults<PaymentRecord> results = realm.where(PaymentRecord.class).findAll();
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        results.deleteAllFromRealm();
                    }
                });
                showDataAndSave(payList);
            }
        } else {
            if (payList.size() == 0) {
                mCanLoad = false;
                ((TextView) findViewById(R.id.loadmore_text)).setText("加载完成");
                Toast.makeText(this, "没有更多数据了!", Toast.LENGTH_LONG).show();
            } else {
                if (ivNull != null && nullData != null) {
                    ivNull.setVisibility(View.GONE);
                    nullData.setVisibility(View.GONE);
                }
                showDataAndSave(payList);
            }
        }
    }

    private void showDataAndSave(RealmList<UPQRPayRecord> payList) {
        for (UPQRPayRecord obj : payList) {
            UnionRecordListAdapter.PaymentRecordExt ext = new UnionRecordListAdapter.PaymentRecordExt();
            ext.paymentRecord = obj;
            int[] timeItems = obj.order_time != null ? Util.getTimeItems(obj.order_time) : null;
            ext.category_value = String.format("%04d-%02d", timeItems[0], timeItems[1]);
            ext.category = ext.category_value;
            mAdapter.add(ext);
        }
    }

    @Override
    public void setPresenter(IMePayListContract.IMePayListPresenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void loadStart() {

    }

    @Override
    public void loadError(String errorMsg) {
    }

    @Override
    public void loadComplete() {
        mSwipeRefreshLayout.setRefreshing(false);
        mSwipeRefreshLayout.setLoading(false);

        mAdapter.notifyDataSetChanged();
    }

//    @Override
//    public boolean leftButtonEnabled() {
//        return true;
//    }
//
//    @Override
//    public void onLeftButtonClick(View view) {
//        finish();
//    }
//
//    @Override
//    public int getLeftButtonImageId() {
//        return R.drawable.back;
//    }
//
//    @Override
//    public boolean rightButtonEnabled() {
//        return rightBtnEnable;
//    }
//
//    @Override
//    public int getRightButtonImageId() {
//        return R.drawable.icon_calendar;
//    }
//
//    @Override
//    public void onRightButtonClick(View view) {
//        super.onRightButtonClick(view);
//        showDateDialog();
//    }

    private void showDateDialog() {
        new MonPickerDialog(getActivity(), AlertDialog.THEME_HOLO_LIGHT, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                showDate.set(Calendar.YEAR, year);
                showDate.set(Calendar.MONTH, monthOfYear);
                DecimalFormat df = new DecimalFormat("00");
                String str1 = df.format(Integer.parseInt(monthOfYear + 1 + ""));
                String str2 = df.format(Integer.parseInt(getDaysByYearMonth(year, monthOfYear + 1) + ""));
                isCheckDate = true;
                mPresenter.loadUnionSwepPayList(year + str1 + "01", year + str1 + str2, "0", 100 + "");
                years = year;
                mons = monthOfYear + 1;
                mAdapter.clear();
            }
        }, showDate.get(Calendar.YEAR), showDate.get(Calendar.MONTH), showDate.get(Calendar.DATE)).show();

    }

    public class MonPickerDialog extends DatePickerDialog {
        public MonPickerDialog(Context context, OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth) {
            super(context, callBack, year, monthOfYear, dayOfMonth);
            if(((ViewGroup) ((ViewGroup) this.getDatePicker().getChildAt(0)).getChildAt(0)).getChildAt(2)!= null){
                ((ViewGroup) ((ViewGroup) this.getDatePicker().getChildAt(0)).getChildAt(0)).getChildAt(2).setVisibility(View.GONE);
            }
        }

        public MonPickerDialog(Context context, int theme, OnDateSetListener listener, int year, int monthOfYear, int dayOfMonth) {
            super(context, theme, listener, year, monthOfYear, dayOfMonth);
            if(((ViewGroup) ((ViewGroup) this.getDatePicker().getChildAt(0)).getChildAt(0)).getChildAt(2) != null){
                ((ViewGroup) ((ViewGroup) this.getDatePicker().getChildAt(0)).getChildAt(0)).getChildAt(2).setVisibility(View.GONE);
            }
        }

        @Override
        public void onDateChanged(DatePicker view, int year, int month, int day) {
            super.onDateChanged(view, year, month, day);
            this.setTitle(year + "年");
        }

    }

    /**
     * 根据年 月 获取对应的月份 天数
     */
    public static int getDaysByYearMonth(int year, int month) {
        Calendar a = Calendar.getInstance();
        a.set(Calendar.YEAR, year);
        a.set(Calendar.MONTH, month - 1);
        a.set(Calendar.DATE, 1);
        a.roll(Calendar.DATE, -1);
        int maxDate = a.get(Calendar.DATE);
        return maxDate;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isCheckDate = false;
        ButterKnife.reset(this);
    }
}
