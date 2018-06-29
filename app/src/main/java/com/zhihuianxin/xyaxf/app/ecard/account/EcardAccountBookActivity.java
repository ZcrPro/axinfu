package com.zhihuianxin.xyaxf.app.ecard.account;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

import com.zhihuianxin.xyaxf.app.AppConstant;
import modellib.thrift.ecard.ECardRecord;
import com.zhihuianxin.xyaxf.app.base.axutil.SchedulerProvider;
import com.zhihuianxin.xyaxf.App;
import com.zhihuianxin.xyaxf.BaseRealmActionBarActivity;
import com.zhihuianxin.xyaxf.R;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by zcrpro on 2016/11/12.
 */
public class EcardAccountBookActivity extends BaseRealmActionBarActivity implements EcardAccountBookContract.EcardAccountBookView {

    @InjectView(R.id.recyclerview)
    RecyclerView recyclerview;
    @InjectView(R.id.swiperefreshlayout)
    SwipeRefreshLayout swiperefreshlayout;

    private EcardAccountBookPresenter ecardAccountBookPresenter;
    private EcardAccountBookContract.EcardAccountBookPresenter presenter;
    private EcardAccountBookAdapter bookAdapter;
    private int page = 0;
    private boolean needLoadMore = true;

    @Override
    protected int getContentViewId() {
        return R.layout.ecard_account_book_activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);

        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        recyclerview.setHasFixedSize(true);

        ecardAccountBookPresenter = new EcardAccountBookPresenter(this, this, SchedulerProvider.getInstance());

        if (TextUtils.isEmpty(App.mAxLoginSp.getEcardPassword())) {
            presenter.loadEcardAccountBook(null, null, page, AppConstant.PAGE_SIZE, false, false);
        }else {
            presenter.loadEcardAccountBook(null, null, page, AppConstant.PAGE_SIZE, false, false,App.mAxLoginSp.getEcardPassword());
        }

        swiperefreshlayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page=0;
                if (TextUtils.isEmpty(App.mAxLoginSp.getEcardPassword())){
                    presenter.loadEcardAccountBook(null, null,page,AppConstant.PAGE_SIZE,true,false);
                }else {
                    presenter.loadEcardAccountBook(null, null,page,AppConstant.PAGE_SIZE,true,false,App.mAxLoginSp.getEcardPassword());
                }
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
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    //获取最后一个完全显示的ItemPosition
                    int lastVisibleItem = manager.findLastCompletelyVisibleItemPosition();
                    int totalItemCount = manager.getItemCount();

                    // 判断是否滚动到底部，并且是向右滚动
                    if (lastVisibleItem == (totalItemCount - 1) && isSlidingToLast) {
                        //加载更多功能的代码
                        if (needLoadMore) {
                            page = page + 1;
                            if (TextUtils.isEmpty(App.mAxLoginSp.getEcardPassword())) {
                                presenter.loadEcardAccountBook(null, null,page,AppConstant.PAGE_SIZE,false,true);
                            }else {
                                presenter.loadEcardAccountBook(null, null,page,AppConstant.PAGE_SIZE,false,true,App.mAxLoginSp.getEcardPassword());
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

    }

    @Override
    public void ecardAccountBookSuccess(List<ECardRecord> eCardRecords,boolean isLoadMore) {
        //获取到数据
        if (isLoadMore) {
            if (eCardRecords.size() < AppConstant.PAGE_SIZE) {
                needLoadMore = false;
                if (eCardRecords.size()!=0){
                    if (bookAdapter != null)
                        bookAdapter.addAll(eCardRecords);
                }
            }else {
                if (bookAdapter != null)
                    bookAdapter.addAll(eCardRecords);
            }
        } else {
            bookAdapter = new EcardAccountBookAdapter(this, eCardRecords, R.layout.ecrad_account_book_item);
            recyclerview.setAdapter(bookAdapter);
            bookAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void ecardAccountBookFailure() {

    }

    @Override
    public void setPresenter(EcardAccountBookContract.EcardAccountBookPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void loadStart() {

    }

    @Override
    public void loadError(String errorMsg) {

    }

    @Override
    public void loadComplete() {
        swiperefreshlayout.setRefreshing(false);
    }
}
