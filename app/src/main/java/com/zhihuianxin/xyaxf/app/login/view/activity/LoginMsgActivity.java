package com.zhihuianxin.xyaxf.app.login.view.activity;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.zhihuianxin.xyaxf.app.AppConstant;
import com.axinfu.modellib.thrift.message.Action;
import com.axinfu.modellib.thrift.message.AxfMesssage;
import com.zhihuianxin.axutil.SchedulerProvider;
import com.zhihuianxin.xyaxf.BaseRealmActionBarActivity;
import com.zhihuianxin.xyaxf.R;
import com.zhihuianxin.xyaxf.app.login.contract.ILoginMsgContract;
import com.zhihuianxin.xyaxf.app.login.presenter.LoginMsgPresenter;
import com.zhihuianxin.xyaxf.app.login.view.adapter.LoginMsgAdapter;
import com.zhihuianxin.xyaxf.app.view.SwipeRefreshAndLoadMoreLayout;

import io.realm.ActionRealmProxy;
import io.realm.AxfMesssageRealmProxy;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * Created by Vincent on 2016/10/25.
 */
public class LoginMsgActivity extends BaseRealmActionBarActivity implements ILoginMsgContract.ILoginMsgView{
    public static final String EXTRA_NEED_REFRESH = "extra_need_refresh";
    private ILoginMsgContract.ILoginMsgPresenter mPresenter;
    private SwipeRefreshAndLoadMoreLayout mSwipeRefreshLayout;
    private ListView mListView;
    private LoginMsgAdapter mAdapter;
    private View mEmptyView;
    private int mPage = 0;
    private boolean mCanLoad = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new LoginMsgPresenter(this,this, SchedulerProvider.getInstance());
        mSwipeRefreshLayout = (SwipeRefreshAndLoadMoreLayout) findViewById(R.id.swipeView);
        mListView = (ListView) findViewById(R.id.sListView);
        mAdapter = new LoginMsgAdapter(this,realm);
        mListView.setAdapter(mAdapter);
        mEmptyView = findViewById(R.id.empty_view);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mCanLoad = true;
                mPage = 0;
                mPresenter.getMsg(0, AppConstant.PAGE_SIZE);
            }
        });
        mSwipeRefreshLayout.setOnLoadListener(new SwipeRefreshAndLoadMoreLayout.OnLoadListener() {
            @Override
            public void onLoad() {
                if(mCanLoad){
                    mPresenter.getMsg((++mPage), AppConstant.PAGE_SIZE);
                    mSwipeRefreshLayout.setLoading(true);
                }
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
        boolean needRefresh = false;
        if(getIntent().getExtras() != null){
            needRefresh = getIntent().getExtras().getBoolean(EXTRA_NEED_REFRESH,false);
        }
        RealmResults<AxfMesssage> realmResults = realm.where(AxfMesssage.class).findAll();
        if(realmResults.size() == 0 || needRefresh){
            mSwipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(true);
                }
            });
            mPresenter.getMsg(0,AppConstant.PAGE_SIZE);
        } else{
            setDbDataToUI(realmResults);
            if(realmResults.size() < AppConstant.PAGE_SIZE){
                mSwipeRefreshLayout.setOnLoadListener(null);
            }
        }
    }

    private void setDbDataToUI(RealmResults<AxfMesssage> list){
        for(AxfMesssage obj : list){
            AxfMesssage msg = new AxfMesssage();
            Action action = new Action();
            action.type = ((ActionRealmProxy)((AxfMesssageRealmProxy)obj).realmGet$action()).realmGet$type();
            action.args = ((ActionRealmProxy)((AxfMesssageRealmProxy)obj).realmGet$action()).realmGet$args();
            msg.action = action;

            msg.title = ((AxfMesssageRealmProxy)obj).realmGet$title();
            msg.id = ((AxfMesssageRealmProxy)obj).realmGet$id();
            msg.content = ((AxfMesssageRealmProxy)obj).realmGet$content();
            msg.time = ((AxfMesssageRealmProxy)obj).realmGet$time();
            mAdapter.add(msg);
        }
    }

    @Override
    protected int getContentViewId() {
        return R.layout.login_msg_fragment;
    }

    @Override
    public void setMsg(RealmList<AxfMesssage> list) {
        mEmptyView.setVisibility(View.GONE);

        if(list.size() < AppConstant.PAGE_SIZE && mPage == 0){
            mSwipeRefreshLayout.setOnLoadListener(null);
        } else{
            mSwipeRefreshLayout.setOnLoadListener(new SwipeRefreshAndLoadMoreLayout.OnLoadListener() {
                @Override
                public void onLoad() {
                    if(mCanLoad){
                        mPresenter.getMsg((++mPage), AppConstant.PAGE_SIZE);
                        mSwipeRefreshLayout.setLoading(true);
                    }
                }
            });
        }

        if(mPage == 0){
            if(list.size() == 0){
                Toast.makeText(this,"没有更多数据了!",Toast.LENGTH_LONG).show();
                mEmptyView.setVisibility(View.VISIBLE);
            } else{
                mAdapter.clear();
                final RealmResults<AxfMesssage> results = realm.where(AxfMesssage.class).findAll();
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        results.deleteAllFromRealm();
                    }
                });
                showDatAndSave(list);
            }
        } else{
            if(list.size() == 0){
                mCanLoad = false;
                ((TextView)findViewById(R.id.pull_to_refresh_loadmore_text)).setText("加载完成");
                Toast.makeText(this,"没有更多数据了!",Toast.LENGTH_LONG).show();
            } else{
                showDatAndSave(list);
            }
        }
    }

    private void showDatAndSave(RealmList<AxfMesssage> list){
        for(AxfMesssage obj : list){
            mAdapter.add(obj);
        }
        mAdapter.notifyDataSetChanged();
        setDBToDb(list);
    }

    private void setDBToDb(final RealmList<AxfMesssage> list){
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                bgRealm.copyToRealmOrUpdate(list);
            }
        });
    }

    @Override
    public void setPresenter(ILoginMsgContract.ILoginMsgPresenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void loadStart() {

    }

    @Override
    public void loadError(String errorMsg) {
//        mSwipeRefreshLayout.setRefreshing(false);
//        mSwipeRefreshLayout.setLoading(false);
    }

    @Override
    public void loadComplete() {
        mSwipeRefreshLayout.setRefreshing(false);
        mSwipeRefreshLayout.setLoading(false);

        mAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean leftButtonEnabled() {
        return true;
    }

    @Override
    public void onLeftButtonClick(View view) {
        finish();
    }

    @Override
    public int getLeftButtonImageId() {
        return R.drawable.back;
    }
}
