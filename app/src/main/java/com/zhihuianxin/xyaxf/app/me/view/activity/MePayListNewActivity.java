package com.zhihuianxin.xyaxf.app.me.view.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhihuianxin.xyaxf.app.AppConstant;
import com.axinfu.modellib.thrift.fee.PaymentRecord;
import com.axinfu.modellib.thrift.unqr.UPQRPayRecord;
import com.xyaxf.axpay.modle.PayFor;
import com.zhihuianxin.xyaxf.app.base.axutil.SchedulerProvider;
import com.zhihuianxin.xyaxf.app.base.axutil.Util;
import com.zhihuianxin.xyaxf.BaseRealmActionBarActivity;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.me.contract.IMePayListContract;
import com.zhihuianxin.xyaxf.app.me.presenter.MePayListPresenter;
import com.zhihuianxin.xyaxf.app.me.view.adapter.MePayListAdapter;
import com.zhihuianxin.xyaxf.app.view.SwipeRefreshAndLoadMoreLayoutStick;
import com.zhihuianxin.xyaxf.app.view.SwipeRefreshAndLoadMorePayNewListLayout;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.realm.RealmList;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

import static java.util.Calendar.DATE;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;
import static java.util.Calendar.getInstance;

/**
 * Created by Vincent on 2018/4/21.
 */

public class MePayListNewActivity extends BaseRealmActionBarActivity implements IMePayListContract.IMePayListView {
    @InjectView(R.id.stickLView)
    StickyListHeadersListView stickLView;
    @InjectView(R.id.swipe)
    SwipeRefreshAndLoadMorePayNewListLayout mSwipeRefreshLayout;
    @InjectView(R.id.iv_null)
    ImageView ivNull;
    @InjectView(R.id.null_data)
    TextView nullData;
    @InjectView(R.id.searchtimeview)
    View searchTimeView;
    @InjectView(R.id.searchtimeTxt)
    TextView searchTimeTxt;
    @InjectView(R.id.searchtimeicon)
    View searchTimeImg;
    @InjectView(R.id.searchpayforview)
    View searchPayForView;
    @InjectView(R.id.searchpayforTxt)
    TextView searchPayForTxt;
    @InjectView(R.id.searchpayforicon)
    View searchPayForImg;
    @InjectView(R.id.searchBgView)
    View searchBgView;
    @InjectView(R.id.payforview)
    View selectPayForView;
    @InjectView(R.id.closedrecordsId)
    View closedViewId;
    @InjectView(R.id.payforall)
    View selectPayForAll;
    @InjectView(R.id.payforecard)
    View selectPayForEcard;
    @InjectView(R.id.payforfee)
    View selectPayForFee;
    @InjectView(R.id.payforscan)
    View selectPayForScan;
    @InjectView(R.id.payforupqr)
    View selectPayForUpqr;
    @InjectView(R.id.payforcancel)
    View selectPayForCancel;
    @InjectView(R.id.backicon)
    View backImg;
    @InjectView(R.id.backview)
    View backView;

    private IMePayListContract.IMePayListPresenter mPresenter;
    private MePayListAdapter mAdapter;
    private int mPage = 0;
    private boolean mCanLoad = false;
    private Calendar showDate;
    private boolean isCheckDate = false;

    @Override
    protected int getContentViewId() {
        return R.layout.me_paylist_new_activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);

        initViews();
    }

    private void initViews(){
        findViewById(R.id.action_bar).setVisibility(View.GONE);
        new MePayListPresenter(this, this, SchedulerProvider.getInstance());
        showDate = getInstance();
        StickyListHeadersListView mListView = (StickyListHeadersListView) findViewById(R.id.stickLView);
        mSwipeRefreshLayout.setStickyListheadersListView(mListView);
        closedViewId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MePayListNewActivity.this, MePayListActivity.class));
            }
        });
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isCheckDate = false;
                showDate = getInstance();
                setSearchTimeTxt(true);
                getDataFromServer(false);
            }
        });
        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {finish();}
        });
        mSwipeRefreshLayout.setOnLoadListener(new SwipeRefreshAndLoadMoreLayoutStick.OnLoadListener() {
            @Override
            public void onLoad() {
                if (mCanLoad) {
                    getDataFromServer(true);
                }
            }
        });
        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {}
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem == 0)
                    mSwipeRefreshLayout.setEnabled(true);
                else
                    mSwipeRefreshLayout.setEnabled(false);
            }
        });
        mAdapter = new MePayListAdapter(this);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position < mAdapter.getCount()) {
                    Intent i = new Intent(MePayListNewActivity.this, MePayListDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(MePayListDetailActivity.EXTRA_DATA, (Serializable) view.getTag());
                    i.putExtras(bundle);
                    startActivity(i);
                }
            }
        });

        searchTimeView.setOnClickListener(listener);
        searchTimeTxt.setOnClickListener(listener);
        searchTimeImg.setOnClickListener(listener);
        searchPayForView.setOnClickListener(listener);
        searchPayForTxt.setOnClickListener(listener);
        searchPayForImg.setOnClickListener(listener);
        searchBgView.setOnClickListener(listener);
        selectPayForView.setOnClickListener(listener);
        selectPayForAll.setOnClickListener(listener);
        selectPayForEcard.setOnClickListener(listener);
        selectPayForFee.setOnClickListener(listener);
        selectPayForScan.setOnClickListener(listener);
        selectPayForUpqr.setOnClickListener(listener);
        selectPayForCancel.setOnClickListener(listener);


        resetSearchUI();
        getDataFromServer(false);
    }

    private void resetSearchUI() {
        showDate = getInstance();
        searchPayForTxt.setText("全部类型");
        setSearchTimeTxt(true);
    }

    @SuppressLint("SetTextI18n")
    private void setSearchTimeTxt(boolean allTime){
        if(allTime){
            isCheckDate = false;
            searchTimeTxt.setText("全部时间");
        } else{
            String month = String.valueOf(showDate.get(MONTH)+1);
            searchTimeTxt.setText(showDate.get(YEAR)+"-"+((month.length()==1)?"0"+month:month));
        }
    }

    private void getDataFromServer(boolean isLoadMore){
        if(isLoadMore){
            mPage = mPage + 1;
        } else{
            mPage = 0;
            mAdapter.clear();
            mSwipeRefreshLayout.setRefreshing(true);
        }

        mPresenter.loadPayList(isCheckDate?startdate:null,
                isCheckDate?endDate:null,
                String.valueOf(mPage),
                String .valueOf(AppConstant.PAGE_SIZE),
                getPayFor(searchPayForTxt.getText().toString()));
    }

    private PayFor getPayFor(String payFortxt){
        PayFor payFor = null;
        switch (payFortxt) {
            case "一卡通充值":
                payFor = PayFor.RechargeECard;
                break;
            case "缴费":
                payFor = PayFor.SchoolFee;
                break;
            case "扫码支付":
                payFor = PayFor.ScanPay;
                break;
            case "付款码支付":
                payFor = PayFor.UPQRPay;
                break;
            case "全部类型":
                payFor = PayFor.ALL;
                break;
            case "取消":
                payFor = PayFor.CANCEL;
                break;
        }
        return payFor;
    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.searchtimeview:
                    showDateDialog();
                    break;
                case R.id.searchtimeTxt:
                    showDateDialog();
                    break;
                case R.id.searchtimeicon:
                    showDateDialog();
                    break;
                case R.id.searchpayforview:
                    selectPayForView.setVisibility(View.VISIBLE);
                    searchBgView.setVisibility(View.VISIBLE);
                    searchPayForImg.setBackgroundResource(R.drawable.icon_up);
                    break;
                case R.id.searchpayforTxt:
                    selectPayForView.setVisibility(View.VISIBLE);
                    searchBgView.setVisibility(View.VISIBLE);
                    searchPayForImg.setBackgroundResource(R.drawable.icon_up);
                    break;
                case R.id.searchpayforicon:
                    selectPayForView.setVisibility(View.VISIBLE);
                    searchBgView.setVisibility(View.VISIBLE);
                    searchPayForImg.setBackgroundResource(R.drawable.icon_up);
                    break;
                case R.id.searchBgView:
                    break;
                case R.id.payforview:
                    break;
                case R.id.payforall:
                    searchPayForTxt.setText("全部类型");
                    searchFromPayFor();
                    break;
                case R.id.payforecard:
                    searchPayForTxt.setText("一卡通充值");
                    searchFromPayFor();
                    break;
                case R.id.payforfee:
                    searchPayForTxt.setText("缴费");
                    searchFromPayFor();
                    break;
                case R.id.payforscan:
                    searchPayForTxt.setText("扫码支付");
                    searchFromPayFor();
                    break;
                case R.id.payforupqr:
                    searchPayForTxt.setText("付款码支付");
                    searchFromPayFor();
                    break;
                case R.id.payforcancel:
                    selectPayForView.setVisibility(View.GONE);
                    searchBgView.setVisibility(View.GONE);
                    searchPayForImg.setBackgroundResource(R.drawable.icon_down);
                    break;
            }
        }
    };


    private void searchFromPayFor() {
        getDataFromServer(false);

        searchPayForImg.setBackgroundResource(R.drawable.icon_down);
        searchBgView.setVisibility(View.GONE);
        selectPayForView.setVisibility(View.GONE);
    }

    @Override
    public void setPayList(RealmList<PaymentRecord> payList) {
        mCanLoad = payList.size() == AppConstant.PAGE_SIZE;
        mSwipeRefreshLayout.setCanload(mCanLoad);
        if (payList.size() == 0) {
            mAdapter.clear();
            ivNull.setVisibility(View.VISIBLE);
            nullData.setVisibility(View.VISIBLE);
        } else {
            ivNull.setVisibility(View.GONE);
            nullData.setVisibility(View.GONE);
            showDataToList(payList);
        }
    }

    @SuppressLint("DefaultLocale")
    private void showDataToList(RealmList<PaymentRecord> payList) {
        for (PaymentRecord obj : payList) {
            MePayListAdapter.PaymentRecordExt ext = new MePayListAdapter.PaymentRecordExt();
            ext.paymentRecord = obj;
            int[] timeItems = obj.order_time != null ? Util.getTimeItems(obj.order_time) : null;
            assert timeItems != null;
            ext.category_value = String.format("%04d-%02d", timeItems[0], timeItems[1]);
            ext.category = ext.category_value;
            mAdapter.add(ext);
        }
    }

    private String startdate,endDate;
    private void showDateDialog() {
        searchTimeImg.setBackgroundResource(R.drawable.icon_up);
        MonPickerDialog dialog  = new MonPickerDialog(getActivity(), AlertDialog.THEME_HOLO_LIGHT, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                showDate.set(YEAR, year);
                showDate.set(MONTH, monthOfYear);
                DecimalFormat df = new DecimalFormat("00");
                String str1 = df.format(Integer.parseInt(monthOfYear + 1 + ""));
                String str2 = df.format(Integer.parseInt(getDaysByYearMonth(year, monthOfYear + 1) + ""));
                isCheckDate = true;
                startdate = year + str1 + "01";
                endDate = year + str1 + str2;

                getDataFromServer(false);

                searchTimeImg.setBackgroundResource(R.drawable.icon_down);
                setSearchTimeTxt(false);
            }
        }, showDate.get(YEAR), showDate.get(MONTH), showDate.get(DATE));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                searchTimeImg.setBackgroundResource(R.drawable.icon_down);
            }
        });
        dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        dialog.show();

    }

    public class MonPickerDialog extends DatePickerDialog {
        MonPickerDialog(Context context, int theme, OnDateSetListener listener, int year, int monthOfYear, int dayOfMonth) {
            super(context, theme, listener, year, monthOfYear, dayOfMonth);
            if(Locale.getDefault().getLanguage().equals("zh")){
                if(((ViewGroup) ((ViewGroup) this.getDatePicker().getChildAt(0)).getChildAt(0)).getChildAt(2) != null){
                    ((ViewGroup) ((ViewGroup) this.getDatePicker().getChildAt(0)).getChildAt(0)).getChildAt(2).setVisibility(View.GONE);
                }
            } else{
                if(((ViewGroup) ((ViewGroup) this.getDatePicker().getChildAt(0)).getChildAt(0)).getChildAt(1) != null){
                    ((ViewGroup) ((ViewGroup) this.getDatePicker().getChildAt(0)).getChildAt(0)).getChildAt(1).setVisibility(View.GONE);
                }
            }

        }

        @Override
        public void onDateChanged(@NonNull DatePicker view, int year, int month, int day) {
            super.onDateChanged(view, year, month, day);
            this.setTitle(year + "年");
        }
    }

    /**
     * 根据年 月 获取对应的月份 天数
     */
    public static int getDaysByYearMonth(int year, int month) {
        Calendar a = getInstance();
        a.set(YEAR, year);
        a.set(MONTH, month - 1);
        a.set(DATE, 1);
        a.roll(DATE, -1);
        return a.get(DATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isCheckDate = false;
        ButterKnife.reset(this);
    }

    @Override
    public void loadComplete() {
        mSwipeRefreshLayout.setRefreshing(false);
        mAdapter.notifyDataSetChanged();
        mSwipeRefreshLayout.setLoading(false);
    }
    @Override
    public void setUnionSwepPayList(RealmList<UPQRPayRecord> payList) {}
    @Override
    public void setPresenter(IMePayListContract.IMePayListPresenter presenter) {mPresenter = presenter;}
    @Override
    public void setPayClosedList(RealmList<PaymentRecord> payList) {}
    @Override
    public void loadStart() {}
    @Override
    public void loadError(String errorMsg) {}
    @Override
    public void onRightButtonClick(View view) {
        super.onRightButtonClick(view);
        showDateDialog();
    }
}
